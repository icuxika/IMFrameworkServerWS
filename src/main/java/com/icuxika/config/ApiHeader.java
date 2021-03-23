package com.icuxika.config;

public class ApiHeader {

    private String token;

    private Integer clientType;

    private Long userId;

    public boolean isValid() {
        return token != null && clientType != null && userId != null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ApiHeader{" +
                "token='" + token + '\'' +
                ", clientType=" + clientType +
                ", userId=" + userId +
                '}';
    }
}
