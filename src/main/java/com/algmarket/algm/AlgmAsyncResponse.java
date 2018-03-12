package com.algmarket.algm;

public class AlgmAsyncResponse {
    private String async;
    private String request_id;

    public AlgmAsyncResponse(String async, String requestId) {
        this.async = async;
        this.request_id = requestId;
    }

    public String getAsyncProtocol() {
        return async;
    }

    public String getRequestId() {
        return request_id;
    }

}
