package com.algmarket.algm;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.Type;

public final class AlgmSuccess extends AlgmResponse {

    private transient JsonElement result;
    private Metadata metadata;
    private String resultJson;
    private String rawOutput;
    private AlgmAsyncResponse asyncResponse;

    private static transient final Gson gson = new Gson();
    private static transient final Type byteType = new TypeToken<byte[]>() {
    }.getType();

    public AlgmSuccess(JsonElement result, Metadata metadata, String rawOutput, AlgmAsyncResponse asyncResponse) {
        this.result = result;
        this.metadata = metadata;
        if (result != null) {
            this.resultJson = result.toString();
        } else {
            this.resultJson = null;
        }
        this.rawOutput = rawOutput;
        this.asyncResponse = asyncResponse;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public AlgmAsyncResponse getAsyncResponse() throws Exception {
        return asyncResponse;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T as(Class<T> returnClass) {
        if (result == null && resultJson != null) {
            result = gson.toJsonTree(resultJson);
        }
        if (metadata.getContentType() == ContentType.Void) {
            return null;
        } else if (metadata.getContentType() == ContentType.Text) {
            return gson.fromJson(new JsonPrimitive(result.getAsString()), returnClass);
        } else if (metadata.getContentType() == ContentType.Json) {
            return gson.fromJson(result, returnClass);
        } else if (metadata.getContentType() == ContentType.Binary) {
            if (byte[].class == returnClass) {
                return (T) Base64.decodeBase64(result.getAsString());
            } else {
                throw new UnsupportedOperationException("Only support returning as byte[] for Binary data");
            }

        } else {
            throw new UnsupportedOperationException("Unknown ContentType in response: " + metadata.getContentType().toString());
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T as(Type returnType) {
        if (result == null && resultJson != null) {
            result = gson.toJsonTree(resultJson);
        }

        if (metadata.getContentType() == ContentType.Void) {
            return null;
        } else if (metadata.getContentType() == ContentType.Text) {
            return gson.fromJson(new JsonPrimitive(result.getAsString()), returnType);
        } else if (metadata.getContentType() == ContentType.Json) {
            return gson.fromJson(result, returnType);
        } else if (metadata.getContentType() == ContentType.Binary) {
            if (byteType.equals(returnType)) {
                return (T) Base64.decodeBase64(result.getAsString());
            } else {
                throw new UnsupportedOperationException("Only support returning as byte[] for Binary data");
            }
        } else {
            throw new UnsupportedOperationException("Unknown ContentType in response: " + metadata.getContentType().toString());
        }
    }

    @Override
    public String asJsonString() {
        return resultJson;
    }

    @Override
    public String asString() {
        return as(String.class);
    }

    @Override
    public String getRawOutput() throws Exception {
        return rawOutput;
    }

}
