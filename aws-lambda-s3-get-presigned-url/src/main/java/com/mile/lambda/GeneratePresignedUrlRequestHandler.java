package com.mile.lambda;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.net.URL;
import java.util.Map;

public class GeneratePresignedUrlRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        Map<String, String> queryStringParameters = apiGatewayProxyRequestEvent.getQueryStringParameters();
        String fileName = queryStringParameters.get("fileName");
        // Set the pre-signed URL to expire after 5 minutes
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5;
        expiration.setTime(expTimeMillis);
        System.out.println("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest("lemilica", fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        generatePresignedUrlRequest.putCustomRequestHeader("x-amz-meta-mile", "mile");
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        responseEvent.setStatusCode(200);
        System.out.println(url.toString());
        responseEvent.setBody(url.toString());
        return responseEvent;
    }

}
