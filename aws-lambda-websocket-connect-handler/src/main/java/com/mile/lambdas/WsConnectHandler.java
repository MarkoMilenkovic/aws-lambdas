package com.mile.lambdas;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

public class WsConnectHandler implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

    private static final String CONNECTIONS_TABLE_NAME = "connections";
    private static final DynamoDbEnhancedClient dynamoDbEnhancedClient = initDispatcher();
    private static final DynamoDbTable<ConnectEntity> custTable =
            dynamoDbEnhancedClient.table(CONNECTIONS_TABLE_NAME, TableSchema.fromBean(ConnectEntity.class));

    private static DynamoDbEnhancedClient initDispatcher() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .region(Region.EU_CENTRAL_1)
                        .httpClientBuilder(UrlConnectionHttpClient.builder())
                        .build())
                .build();
    }

    static {
        warmUp();
    }

    private static void warmUp() {
        custTable.getItem(new ConnectEntity("test", "test"));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> requestEvent, Context context) {
        String requestId = extractConnectionId(requestEvent);
        String name = extractName(requestEvent);
        ConnectEntity connectEntity = new ConnectEntity(requestId, name);
        custTable.putItem(connectEntity);
        APIGatewayProxyResponseEvent proxyResponseEvent = new APIGatewayProxyResponseEvent();
        proxyResponseEvent.setStatusCode(200);
        return proxyResponseEvent;
    }

    private String extractConnectionId(Map<String, Object> input) {
        Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
        return (String) requestContext.get("connectionId");
    }

    private String extractName(Map<String, Object> input) {
        Map<String, Object> requestContext = (Map<String, Object>) input.get("queryStringParameters");
        return (String) requestContext.get("name");
    }
}
