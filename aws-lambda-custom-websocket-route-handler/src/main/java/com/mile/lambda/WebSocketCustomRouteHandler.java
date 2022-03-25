package com.mile.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mile.lambda.dto.MessageDto;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class WebSocketCustomRouteHandler implements RequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {

    private final static String ENDPOINT = "https://j181tjxtog.execute-api.eu-central-1.amazonaws.com/develop/";
    private final static ApiGatewayManagementApiClient client;
    private final static ObjectMapper objectMapper = new ObjectMapper();

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
        APIGatewayProxyResponseEvent proxyResponseEvent = new APIGatewayProxyResponseEvent();
        try {
            String connectionId = extractConnectionId(requestEvent);
            MessageDto messageDto = extractMessageDto(requestEvent);
            PostToConnectionRequest postToConnectionRequest = PostToConnectionRequest.builder()
                    .connectionId(connectionId)
                    .data(SdkBytes.fromByteArray(objectMapper.writeValueAsBytes(messageDto)))
                    .build();
            proxyResponseEvent.setStatusCode(200);
            client.postToConnection(postToConnectionRequest);
        } catch (JsonProcessingException e) {
            proxyResponseEvent.setStatusCode(500);
        }
        return proxyResponseEvent;
    }

    private String extractConnectionId(Map<String, Object> input) {
        Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
        return (String) requestContext.get("connectionId");
    }

    private MessageDto extractMessageDto(Map<String, Object> input) {
        Object body = input.get("body");
        try {
            return objectMapper.readValue(body.toString(), MessageDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to json to MessageDto");
        }
    }
}
