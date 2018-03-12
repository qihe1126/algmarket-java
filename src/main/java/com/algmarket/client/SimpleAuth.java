package com.algmarket.client;

import org.apache.http.HttpRequest;

public final class SimpleAuth extends Auth {
    String apiKey;

    public SimpleAuth(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void authenticateRequest(HttpRequest request) {
        request.addHeader("KeyValue", this.apiKey);
    }
}
