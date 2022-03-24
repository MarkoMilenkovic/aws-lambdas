Lambda with API gateway trigger that inserts data in Dynamo DB:

Prerequisites: 
1. Create Lambda function full access permission for Dynamo DB
2. Create API trigger for lambda (make query parameter id as required and add request validator, also make API public)
4. Create Dynamo db table: users with partitioning key: id (add some other field, for example name)

Build jar and deploy:
1. Open terminal and run: mvn clean install
2. Upload (uber) jar from target folder to aws lambda 

Testing:
1. Open Dynamo DB table users and copy id of any users (if table is empty create new record)
2. Follow URL from configured API gateway (if there is no query param it will return error 
so paste id from dynamo record in query parameter with key named id)
3. In response there should be json representing record from dynamo db 

View logs from lambda:
Open cloudwatch -> Log group -> find your lambda