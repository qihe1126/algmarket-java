package com.algmarket.client;

import com.algmarket.APIException;
import com.algmarket.AlgMarketConfig;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HttpClient {

    final private Auth auth;
    final private String apiAddress;

    private static String userAgent = "algmarket-java/" + "1.0.1";

    private static List<CloseableHttpAsyncClient> clients = new LinkedList<CloseableHttpAsyncClient>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                synchronized (clients) {
                    for (CloseableHttpAsyncClient client : clients) {
                        try {
                            client.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
    }

    private final CloseableHttpAsyncClient client;

    public HttpClient(Auth auth, String apiAddress, int maxConnections) {
        this.auth = auth;
        if (apiAddress != null) {
            this.apiAddress = apiAddress;
        } else {
            this.apiAddress = AlgMarketConfig.apiAddress();
        }

        client = HttpAsyncClientBuilder.create().setMaxConnTotal(maxConnections).setMaxConnPerRoute(maxConnections).useSystemProperties().build();

        synchronized (clients) {
            clients.add(client);
        }
        client.start();
    }

    private String getUrl(String path) {
        return apiAddress + path;
    }

    private void addQueryParameters(HttpRequestBase request, Map<String, String> params) {
        if (params != null) {
            URIBuilder builder = new URIBuilder(request.getURI());
            for (Map.Entry<String, String> param : params.entrySet()) {
                builder.addParameter(param.getKey(), param.getValue());
            }
            try {
                request.setURI(builder.build());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to construct API URI", e);
            }
        }
    }

    public HttpResponse get(String path) throws APIException {
        final HttpGet request = new HttpGet(getUrl(path));
        return this.execute(request);
    }

    public <T> Future<T> get(String path, HttpAsyncResponseConsumer<T> consumer) {
        final HttpGet request = new HttpGet(getUrl(path));
        return this.executeAsync(request, consumer);
    }

    public <T> T get(String path, TypeToken<T> typeToken, Map<String, String> params) throws APIException {
        final HttpGet request = new HttpGet(getUrl(path));
        addQueryParameters(request, params);
        return this.execute(request, new HttpClientHelpers.JsonDeserializeResponseHandler<T>(typeToken));
    }

    public void getFile(String path, File destination) throws APIException {
        System.out.println(path);
        final HttpGet request = new HttpGet(getUrl(path));
        request.addHeader("ImgPath", destination.getAbsolutePath());
        this.executeGetFile(request, destination);
    }

    public HttpResponse post(String path, HttpEntity data) throws APIException {
        final HttpPost request = new HttpPost(getUrl(path));
        request.setEntity(data);
        return this.execute(request);
    }

    public <T> Future<T> post(String path, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        return post(getUrl(path), data, consumer, null);
    }

    public <T> Future<T> post(String path, HttpEntity data, HttpAsyncResponseConsumer<T> consumer, Map<String, String> parameters) {
        final HttpPost request = new HttpPost(getUrl(path));
        request.setEntity(data);
        addQueryParameters(request, parameters);
        return this.executeAsync(request, consumer);
    }

    /**
     * PUT requests
     */
    public HttpResponse put(String path, HttpEntity data) throws APIException {
        final HttpPut request = new HttpPut(getUrl(path));
        request.setEntity(data);
        return this.execute(request);
    }

    public <T> Future<T> put(String path, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        final HttpPut request = new HttpPut(getUrl(path));
        request.setEntity(data);
        return this.executeAsync(request, consumer);
    }

    public HttpResponse delete(String path) throws APIException {
        final HttpDelete request = new HttpDelete(getUrl(path));
        return execute(request);
    }

    public <T> Future<T> delete(String path, HttpAsyncResponseConsumer<T> consumer) {
        final HttpDelete request = new HttpDelete(getUrl(path));
        return executeAsync(request, consumer);
    }

    public HttpResponse head(String path) throws APIException {
        final HttpHead request = new HttpHead(getUrl(path));
        return executeHead(request);
    }

    public <T> Future<T> head(String path, HttpAsyncResponseConsumer<T> consumer) {
        final HttpHead request = new HttpHead(getUrl(path));
        return executeAsync(request, consumer);
    }

    public HttpResponse patch(String path, StringEntity entity) throws APIException {
        final HttpPatch request = new HttpPatch(getUrl(path));
        request.setEntity(entity);
        return this.execute(request);
    }

    private HttpResponse execute(HttpUriRequest request) throws APIException {
        return execute(request, new BasicAsyncResponseConsumer());
    }

    private HttpResponse executeHead(HttpUriRequest request) throws APIException {
        return execute(request, new HttpResponseConsumer());
    }

    private void executeGetFile(HttpUriRequest request, File destination) throws APIException {
        try {
            ZeroCopyConsumer<File> consumer = new ZeroCopyConsumer<File>(destination) {
                @Override
                protected File process(final HttpResponse response, final File file, final ContentType contentType) throws APIException {
                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        throw new APIException("Download failed: " + response.getStatusLine());
                    }
                    return file;
                }
            };

            execute(request, consumer);
        } catch (FileNotFoundException e) {
            throw new APIException("Could not find destination file: " + destination.getAbsolutePath());
        }
    }

    private <T> T execute(HttpUriRequest request, HttpAsyncResponseConsumer<T> consumer) throws APIException {
        try {
            return executeAsync(request, consumer).get();
        } catch (ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch (CancellationException e) {
            throw new APIException("API connection cancelled: " + request.getURI().toString() + " (" + e.getMessage() + ")", e);
        } catch (InterruptedException e) {
            throw new APIException("API connection interrupted: " + request.getURI().toString() + " (" + e.getMessage() + ")", e);
        }
    }

    private <T> Future<T> executeAsync(HttpUriRequest request, HttpAsyncResponseConsumer<T> consumer) {
        if (this.auth != null) {
            this.auth.authenticateRequest(request);
        }
        request.addHeader("User-Agent", HttpClient.userAgent);
        HttpHost target = new HttpHost(request.getURI().getHost(), request.getURI().getPort());
        return client.execute(new BasicAsyncRequestProducer(target, request), consumer, null);
    }

    static class HttpResponseConsumer extends AsyncByteConsumer<HttpResponse> {
        private HttpResponse response;

        @Override
        protected void onResponseReceived(final HttpResponse response) {
            this.response = response;
        }

        @Override
        protected HttpResponse buildResult(final HttpContext context) throws APIException {
            return this.response;
        }

        @Override
        protected void onByteReceived(final ByteBuffer buf, final IOControl ioctrl) {
        }
    }

    @Override
    protected void finalize() {
        synchronized (clients) {
            clients.remove(client);
        }
    }
}
