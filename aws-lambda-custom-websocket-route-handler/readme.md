Lambda with API gateway WebSocket trigger that send exact data back to WebSocket client.

Testing:
1. Open WebSocket connection
2. Send message in given format :
   {"action":"lemi", "status": "WIN"}
Since we are using custom route expression configured in API Gateway, payload must contain
key: "action" with value "lemi" in order to invoke this Lambda
3. Same message should be received on client websocket