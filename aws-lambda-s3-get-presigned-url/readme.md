Lambda for generating presigned url for direct download into s3:

Prerequisites: 
1. Create bucket in S3
2. Create Lambda function in same region as S3 bucket with permission to get Object from S3 bucket
3. Create API gateway trigger for lambda

Build jar and deploy:
1. Open terminal and run: mvn clean install
2. Upload (uber) jar from target folder to aws lambda 

Testing:
1. Hit the configured api gateway endpoint - copy url from response
2. Invoke HTTP PUT request with url from step 1.: set body as binary and select file to upload
3. Add HTTP Headers (that represent custom user metadata - key :x-amz-meta-mile, value: mile)
4. Hit send and Check if file is uploaded to S3 with correct user metadata

View logs from lambda:
Open cloudwatch -> Log group -> find your lambda