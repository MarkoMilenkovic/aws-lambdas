package com.mile.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

import java.util.Collections;
import java.util.Map;

public class DisconnectHandler implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

    private static final String CONNECTIONS_TABLE_NAME = "connections";
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .httpClient(UrlConnectionHttpClient.create())
            .region(Region.EU_CENTRAL_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .overrideConfiguration(ClientOverrideConfiguration.builder().build())
            .build();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> requestEvent, Context context) {
        String requestId = extractConnectionId(requestEvent);
        Map<String, AttributeValue> keyToGet = Collections.singletonMap("connectionId", AttributeValue.builder().s(requestId).build());
        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(CONNECTIONS_TABLE_NAME)
                .key(keyToGet)
                .build();
        dynamoDbClient.deleteItem(deleteReq);
        APIGatewayProxyResponseEvent proxyResponseEvent = new APIGatewayProxyResponseEvent();
        proxyResponseEvent.setStatusCode(200);
        return proxyResponseEvent;
    }

    private String extractConnectionId(Map<String, Object> input) {
        Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
        return (String) requestContext.get("connectionId");
    }
}
