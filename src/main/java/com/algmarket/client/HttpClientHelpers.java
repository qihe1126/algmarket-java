package com.algmarket.client;

import com.algmarket.APIException;
import com.algmarket.algm.AlgmResponse;
import com.algmarket.algm.AlgmSuccess;
import com.algmarket.algm.Metadata;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

public class HttpClientHelpers {

    private HttpClientHelpers() {}

    static abstract public class AbstractBasicResponseConsumer<T> extends AbstractAsyncResponseConsumer<T> {
        protected volatile HttpResponse response;
        private volatile SimpleInputBuffer buf;

        @Override
        protected void onResponseReceived(final HttpResponse response) throws IOException {
            this.response = response;
        }

        @Override
        protected void onEntityEnclosed(final HttpEntity entity, final ContentType contentType) throws IOException {
            long len = entity.getContentLength();
            if (len > Integer.MAX_VALUE) {
                throw new ContentTooLongException("Entity content is too long: " + len);
            }
            if (len < 0) {
                len = 4096;
            }
            this.buf = new SimpleInputBuffer((int) len, new HeapByteBufferAllocator());
            this.response.setEntity(new ContentBufferEntity(entity, this.buf));
        }

        @Override
        protected void onContentReceived(final ContentDecoder decoder, final IOControl ioctrl) throws IOException {
            Asserts.notNull(this.buf, "Content buffer");
            this.buf.consumeContent(decoder);
        }

        @Override
        protected void releaseResources() {
            this.response = null;
            this.buf = null;
        }
    }

    static public class JsonDeserializeResponseHandler<T> extends AbstractBasicResponseConsumer<T> {
        @SuppressWarnings("rawtypes")
        final private TypeToken typeToken;

        public JsonDeserializeResponseHandler(@SuppressWarnings("rawtypes") TypeToken typeToken) {
            this.typeToken = typeToken;
        }

        @Override
        protected T buildResult(HttpContext context) throws APIException {
            JsonElement json = parseResponseJson(response);
            throwIfJsonHasError(json);
            Gson gson = new Gson();
            return gson.fromJson(json, typeToken.getType());
        }
    }

    static public class AlgmResponseHandler extends AbstractBasicResponseConsumer<AlgmResponse> {
        @Override
        protected AlgmResponse buildResult(HttpContext context) throws APIException {
            JsonElement json = parseResponseJson(response);
            return jsonToAlgmResponse(json);
        }
    }

    public static AlgmResponse jsonToAlgmResponse(JsonElement json) throws APIException {
        if (json != null && json.isJsonObject()) {
            final JsonObject obj = json.getAsJsonObject();
            Double duration = obj.get("duration").getAsDouble();
            Metadata meta = new Metadata(null, duration, null);
            return new AlgmSuccess(obj.get("data"), meta, null, null);
        } else {
            throw new APIException("Unexpected API response: " + json);
        }
    }

    public static void throwIfNotOk(HttpResponse response) throws APIException {
        final int status = response.getStatusLine().getStatusCode();
       // entity.getContent()
        if (200 > status || status > 300) {
            if (status == 400) {
                throw new APIException("400 malformed request");
            } else if (status == 401) {
                throw new APIException("401 not authorized");
            } else if (status == 404) {
                throw new APIException("404 not found");
            } else if (status == 415) {
                throw new APIException("415 unsupported content type");
            } else if (status == 504) {
                throw new APIException("504 server timeout");
            }
            else if (status == 403) {
                HttpEntity entity= response.getEntity();
                String  entityStr = null;
                try {
                    entityStr = EntityUtils.toString(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JsonObject obj = new JsonParser().parse(entityStr).getAsJsonObject();
                String message=obj.get("error").getAsString();
                throw new APIException(message);
            }
            else {
                throw new APIException(status + " unexpected API response");
            }
        }
    }

    public static void throwIfJsonHasError(JsonElement json) throws APIException {
        if (json != null && json.isJsonObject()) {
            final JsonObject obj = json.getAsJsonObject();
            if (obj.has("error")) {
                final JsonObject error = obj.getAsJsonObject("error");
                final String msg = error.get("message").getAsString();
                throw new APIException(msg);
            }
        }
    }

    final static JsonParser parser = new JsonParser();

    public static JsonElement parseResponseJson(HttpResponse response) throws APIException {
        throwIfNotOk(response);

        try {
            final HttpEntity entity = response.getEntity();
            final InputStream is = entity.getContent();
            String jsonString = IOUtils.toString(is, "UTF-8");
            JsonElement json = parser.parse(jsonString);
            return json;
        } catch (IOException ex) {
            throw new APIException("IOException: " + ex.getMessage());
        }
    }

}
