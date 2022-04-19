package com.mile.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.CognitoUserPoolPostConfirmationEvent;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class PostConfirmationHandler implements RequestHandler<CognitoUserPoolPostConfirmationEvent, CognitoUserPoolPostConfirmationEvent> {

    private static final DynamoDbClient client;

    static {
        try {
            client = DynamoDbClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .endpointOverride(new URI("https://dynamodb.eu-central-1.amazonaws.com"))
                    .region(Region.EU_CENTRAL_1)
                    .httpClient(UrlConnectionHttpClient.builder().build())
                    .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to parse URL: " + e.getInput() + " ,reason: " + e.getReason());
        }
    }

    @Override
    public CognitoUserPoolPostConfirmationEvent handleRequest(CognitoUserPoolPostConfirmationEvent cognitoEvent, Context context) {
        Map<String, String> userAttributes = cognitoEvent.getRequest().getUserAttributes();

        Map<String, AttributeValue> item_values = new HashMap<>();
        item_values.put("name", AttributeValue.builder().s(cognitoEvent.getUserName()).build());
        item_values.put("email", AttributeValue.builder().s(userAttributes.get("email")).build());
        item_values.put("id", AttributeValue.builder().s(userAttributes.get("sub")).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("users")
                .item(item_values)
                .build();

        client.putItem(putItemRequest);
        return cognitoEvent;
    }
}
