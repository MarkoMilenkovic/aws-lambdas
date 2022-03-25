package com.mile.lambda.dto;

import java.util.Objects;

public class MessageDto {

    private String action;
    private String status;

    public MessageDto() {
    }

    public MessageDto(String action, String status) {
        this.action = action;
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageDto that = (MessageDto) o;
        return action.equals(that.action) && status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, status);
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "action='" + action + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
