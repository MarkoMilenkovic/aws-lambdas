Lambda with Dynamo DB trigger that publishes SNS request to send sms:

Prerequisites: 
1. Create Lambda function full access permission for Dynamo DB and SNS
4. Create Dynamo db table: users with partitioning key: id (and field phoneNumber)

Build jar and deploy:
1. Open terminal and run: mvn clean install
2. Upload (uber) jar from target folder to aws lambda 

Testing:
1. Open Dynamo DB table and insert some users with phone number that is verifies in SNS
2. Check if SMS message is received

View logs from lambda:
Open cloudwatch -> Log group -> find your lambda