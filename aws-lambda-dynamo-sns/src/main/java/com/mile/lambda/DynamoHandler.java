package com.mile.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import java.util.Map;

public class DynamoHandler implements RequestHandler<DynamodbEvent, Void> {

    final AmazonSNS amazonSNS = AmazonSNSClientBuilder.defaultClient();

    @Override
    public Void handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        try {
            for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
                if (record == null) {
                    continue;
                }

                Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
                AttributeValue phoneNumberAttributeValue = newImage.get("phoneNumber");
                String phoneNumber = phoneNumberAttributeValue.getS();
                PublishRequest request = new PublishRequest().withMessage("Zdravo Iz lambde")
                        .withPhoneNumber(phoneNumber);
                PublishResult result = amazonSNS.publish(request);

                context.getLogger().log(" Message sent. Status was " + result.getSdkHttpMetadata().getHttpStatusCode());
            }
        } catch (Exception e) {
            context.getLogger().log("Something went wrong: " + e.getMessage());
        }
        return null;
    }
}
