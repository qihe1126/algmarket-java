package com.algmarket.algm;

import com.algmarket.client.HttpClient;
import com.algmarket.client.HttpClientHelpers;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public final class Algm {
    private final AlgmRef algmRef;
    private final HttpClient client;
    private final Map<String, String> options;
    final static Gson gson = new Gson();

    public Algm(HttpClient client, AlgmRef algmRef) {
        this(client, algmRef, new HashMap<String, String>());
    }

    public Algm(HttpClient client, AlgmRef algmRef, Map<String, String> options) {
        this.client = client;
        this.algmRef = algmRef;
        this.options = options;
    }

    public AlgmResponse call(Object input) throws Exception {
        if (input instanceof String) {
            return callRequest((String) input, ContentType.Text);
        } else if (input instanceof byte[]) {
            return callBinaryRequest((byte[]) input);
        } else {
            return callRequest(gson.toJsonTree(input).toString(), ContentType.Json);
        }
    }

    public FutureAlgmResponse callAsync(Object input) {
        final Gson gson = new Gson();
        final JsonElement inputJson = gson.toJsonTree(input);
        return callJsonAsync(inputJson.toString());
    }


    public AlgmResponse callJson(String inputJson) throws Exception {
        return callRequest(inputJson, ContentType.Json);
    }

    public FutureAlgmResponse callJsonAsync(String inputJson) {
        return callRequestAsync(inputJson, ContentType.Json);
    }

    private AlgmResponse callRequest(String input, ContentType content_type) throws Exception {
        try {
            return callRequestAsync(input, content_type).get();
        } catch (java.util.concurrent.ExecutionException e) {
            e.printStackTrace();
            throw new Exception(e.getCause().getMessage());
        } catch (java.util.concurrent.CancellationException e) {
            throw new Exception("API connection cancelled: " + algmRef.getUrl() + " (" + e.getMessage() + ")", e);
        } catch (InterruptedException e) {
            throw new Exception("API connection interrupted: " + algmRef.getUrl() + " (" + e.getMessage() + ")", e);
        }
    }

    private FutureAlgmResponse callRequestAsync(String input, ContentType content_type) {
        StringEntity requestEntity = null;
        if (content_type == ContentType.Text) {
            requestEntity = new StringEntity(input, "UTF-8");
        } else if (content_type == ContentType.Json) {
            requestEntity = new StringEntity(input, org.apache.http.entity.ContentType.APPLICATION_JSON);
        }
        HttpClientHelpers.AlgmResponseHandler responseHandler = new HttpClientHelpers.AlgmResponseHandler();
        Future<AlgmResponse> promise = client.post(
                algmRef.getUrl(),
                requestEntity,
                responseHandler,
                options
        );
        return new FutureAlgmResponse(promise);
    }

    private void AlgmResponseHandler() {
    }

    private AlgmResponse callBinaryRequest(byte[] input) throws Exception {
        try {
            return callBinaryRequestAsync(input).get();
        } catch (java.util.concurrent.ExecutionException e) {
            throw new Exception(e.getCause().getMessage());
        } catch (java.util.concurrent.CancellationException e) {
            throw new Exception("API connection cancelled: " + algmRef.getUrl() + " (" + e.getMessage() + ")", e);
        } catch (InterruptedException e) {
            throw new Exception("API connection interrupted: " + algmRef.getUrl() + " (" + e.getMessage() + ")", e);
        }
    }

    private FutureAlgmResponse callBinaryRequestAsync(byte[] input) {
        HttpClientHelpers.AlgmResponseHandler responseHandler = new HttpClientHelpers.AlgmResponseHandler();
        Future<AlgmResponse> promise = client.post(
                algmRef.getUrl(),
                new ByteArrayEntity(input, org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM),
                responseHandler,
                options
        );
        return new FutureAlgmResponse(promise);
    }

}
