package com.mile.lambda.dto;

public class Body<T> {
    private T body;

    public Body() {
    }

    public Body(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}