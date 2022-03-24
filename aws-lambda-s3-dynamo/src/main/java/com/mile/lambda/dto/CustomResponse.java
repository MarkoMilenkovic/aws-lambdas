package com.mile.lambda.dto;

public class CustomResponse<T> {

    private int statusCode;
    private Body<T> body;

    public CustomResponse() {
    }

    public CustomResponse(Body<T> body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public Body<T> getBody() {
        return body;
    }

    public void setBody(Body<T> body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
