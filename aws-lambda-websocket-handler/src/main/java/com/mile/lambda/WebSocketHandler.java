package com.mile.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class WebSocketHandler implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

    private final static String ENDPOINT = "https://j181tjxtog.execute-api.eu-central-1.amazonaws.com/develop/";
    private final static ApiGatewayManagementApiClient client;

    static {
        client = ApiGatewayManagementApiClient.builder()
                .httpClient(UrlConnectionHttpClient.create())
                .endpointOverride(getUri())
                .build();
    }

    private static URI getUri() {
        try {
            return new URI(ENDPOINT);
        } catch (URISyntaxException e) {
            return null;
        }
    }


    @Override
    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> requestEvent, Context context) {
        String res = "kod mene radi";
        String connectionId = extractConnectionId(requestEvent);
        PostToConnectionRequest postToConnectionRequest = PostToConnectionRequest.builder()
                .connectionId(connectionId)
                .data(SdkBytes.fromByteArray(res.getBytes(StandardCharsets.UTF_8)))
                .build();
        PostToConnectionResponse result = client.postToConnection(postToConnectionRequest);
        System.out.println("result: " + result);
        APIGatewayProxyResponseEvent proxyResponseEvent = new APIGatewayProxyResponseEvent();
        proxyResponseEvent.setStatusCode(200);
        return proxyResponseEvent;
    }

    private String extractConnectionId(Map<String, Object> input) {
        Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
        return (String) requestContext.get("connectionId");
    }
}
