package com.mile.lambda.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mile.lambdas.common.PhoneNumber;
import com.mile.lambdas.common.UserEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class S3JsonFileHandler implements RequestHandler<S3Event, Void> {

    final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    final AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.defaultClient();
    final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoClient);

    @Override
    public Void handleRequest(S3Event s3Event, Context context) {
        try {
            s3Event.getRecords().forEach(e -> System.out.println("******: " + e));
            S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
            String srcBucket = record.getS3().getBucket().getName();
            String fileName = record.getS3().getObject().getKey();

            S3Object xFile = s3Client.getObject(srcBucket, fileName);
            String metadata = xFile.getObjectMetadata().getUserMetadata().get("mile");
            InputStream contents = xFile.getObjectContent();
            ObjectMapper objectMapper = new ObjectMapper();
            List<UserEntity> userEntities = objectMapper.readValue(contents, new TypeReference<List<UserEntity>>() {
            });

            if (somePhoneNumberAlreadyExists(userEntities)) {
                context.getLogger().log("Some of the phone numbers already exists!");
                return null;
            }
            userEntities.forEach(userEntity -> {
                userEntity.setId(UUID.randomUUID().toString());
                userEntity.setMetadata(metadata);
            });
            dynamoDBMapper.batchSave(userEntities);

            List<PhoneNumber> phoneNumbers = userEntities
                    .stream()
                    .map(userEntity -> new PhoneNumber(userEntity.getPhoneNumber(), userEntity.getId()))
                    .collect(Collectors.toList());
            dynamoDBMapper.batchSave(phoneNumbers);

        } catch (IOException e) {
            context.getLogger().log(e.getMessage());
        }
        return null;
    }

    private boolean somePhoneNumberAlreadyExists(List<UserEntity> userEntities) {
        List<KeyPair> keyPairs = userEntities.stream()
                .map(userEntity -> new KeyPair().withHashKey(userEntity.getPhoneNumber()))
                .collect(Collectors.toList());
        Map<Class<?>, List<KeyPair>> keyPairForTable = new HashMap<>();
        keyPairForTable.put(PhoneNumber.class, keyPairs);
        Map<String, List<Object>> listMap = dynamoDBMapper.batchLoad(keyPairForTable);
        List<PhoneNumber> phoneNumbers = listMap.get("phone_numbers").stream()
                .map(e -> (PhoneNumber) e)
                .collect(Collectors.toList());
        return !phoneNumbers.isEmpty();
    }


}
