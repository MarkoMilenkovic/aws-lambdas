package com.mile.lambdas;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

@DynamoDbBean
public class ConnectEntity {

    private String connectionId;
    private String name;

    public ConnectEntity() {
    }

    public ConnectEntity(String connectionId) {
        this.connectionId = connectionId;
    }

    public ConnectEntity(String connectionId, String name) {
        this.connectionId = connectionId;
        this.name = name;
    }

    @DynamoDbPartitionKey
    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectEntity that = (ConnectEntity) o;
        return connectionId.equals(that.connectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionId);
    }

    @Override
    public String toString() {
        return "ConnectEntity{" +
                "connectionId='" + connectionId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
