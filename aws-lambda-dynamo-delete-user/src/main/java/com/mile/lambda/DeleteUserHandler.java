package com.mile.lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.mile.lambdas.common.PhoneNumber;
import com.mile.lambdas.common.UserEntity;

import java.util.Map;

public class DeleteUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    final AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.defaultClient();
    final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoClient);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            Map<String, String> queryStringParameters = apiGatewayProxyRequestEvent.getQueryStringParameters();
            String id = queryStringParameters.get("id");
            UserEntity userEntity = dynamoDBMapper.load(UserEntity.class, id);
            dynamoDBMapper.delete(userEntity);

            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhoneNumber(userEntity.getPhoneNumber());
            dynamoDBMapper.delete(phoneNumber);

            response.setStatusCode(200);
            response.setBody("");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("{\"message\" : \"Something went wrong\"}");
            context.getLogger().log("Error occurred:" + e.getMessage());
        }
        return response;    }
}
