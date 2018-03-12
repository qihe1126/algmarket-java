package com.algmarket.algm;

import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class AlgmResponse implements Serializable {

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    public abstract Metadata getMetadata() throws Exception;

    public abstract AlgmAsyncResponse getAsyncResponse() throws Exception;

    protected abstract <T> T as(Class<T> returnClass) throws Exception;

    protected abstract <T> T as(Type returnType) throws Exception;


    public abstract String asJsonString() throws Exception;

    public abstract String asString() throws Exception;

    public abstract String getRawOutput() throws Exception;
}
