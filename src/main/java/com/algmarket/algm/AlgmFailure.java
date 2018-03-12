package com.algmarket.algm;

import java.lang.reflect.Type;

public final class AlgmFailure extends AlgmResponse {

    public Exception error;

    public AlgmFailure(Exception error) {
        this.error = error;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public Metadata getMetadata() throws Exception {
        throw error;
    }

    @Override
    public AlgmAsyncResponse getAsyncResponse() throws Exception {
        throw error;
    }

    @Override
    protected <T> T as(Class<T> returnClass) throws Exception {
        throw error;
    }

    @Override
    protected <T> T as(Type returnType) throws Exception {
        throw error;
    }

    @Override
    public String asJsonString() throws Exception {
        throw error;
    }

    @Override
    public String asString() throws Exception {
        throw error;
    }

    @Override
    public String getRawOutput() throws Exception {
        throw error;
    }

}
