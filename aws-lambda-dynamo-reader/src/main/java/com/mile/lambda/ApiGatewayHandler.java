package com.mile.lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mile.lambdas.common.UserEntity;

import java.util.Map;

public class ApiGatewayHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    final AmazonDynamoDB dynamoClient = AmazonDynamoDBClientBuilder.defaultClient();
    final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(dynamoClient);
    final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            Map<String, String> queryStringParameters = apiGatewayProxyRequestEvent.getQueryStringParameters();
            String id = queryStringParameters.get("id");
            UserEntity userEntity = dynamoDBMapper.load(UserEntity.class, id);
            response.setStatusCode(200);

            String json = objectWriter.writeValueAsString(userEntity);
            response.setBody(json);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("{\"message\" : \"Something went wrong\"}");
            context.getLogger().log("Error occurred:" + e.getMessage());
        }
        return response;

    }

}
