package com.algmarket.model;

import com.algmarket.APIException;
import com.algmarket.client.HttpClient;
import com.algmarket.client.HttpClientHelpers;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import java.io.*;
import java.nio.charset.Charset;

public class ModelFile extends ModelObject {
    public ModelFile(HttpClient client, String dataUrl) {
        super(client, dataUrl, DataObjectType.FILE);
    }

    public boolean exists() throws APIException {
        HttpResponse response = client.head(getUrl());
        int status = response.getStatusLine().getStatusCode();
        if(status != 200 && status != 404) {
            throw APIException.fromHttpResponse(response);
        }
        return (200 == status);
    }

    public File getFile() throws APIException, IOException {
        String filename = getName();
        int extensionIndex = filename.lastIndexOf('.');
        String body;
        String ext;
        if(extensionIndex <= 0) {
            // No extension
            body = filename;
            ext = "";
        } else {
            body = filename.substring(0, extensionIndex);
            ext = filename.substring(extensionIndex);
        }
        //暂时不做处理
       /* if(body.length() < 3 || body.length() > 127) {
            body = "algodata";
        }*/

        File tempFile = File.createTempFile(body, ext);
        client.getFile(getUrl(), tempFile);

        return tempFile;
    }

    public InputStream getInputStream() throws APIException, IOException {
        final HttpResponse response = client.get(getUrl());
        HttpClientHelpers.throwIfNotOk(response);
        return response.getEntity().getContent();
    }

    public String getString() throws IOException {
        return IOUtils.toString(getInputStream(), Charset.forName("UTF-8"));
    }

    public String getString(Charset encoding) throws IOException {
        return IOUtils.toString(getInputStream(), encoding);
    }

    public byte[] getBytes() throws IOException {
        return IOUtils.toByteArray(getInputStream());
    }

    public void put(String data) throws APIException {
        HttpResponse response = client.put(getUrl(), new StringEntity(data, "UTF-8"));
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void put(String data,Charset charset) throws APIException {
        HttpResponse response = client.put(getUrl(), new StringEntity(data, charset));
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void put(byte[] data) throws APIException {
        HttpResponse response = client.put(getUrl(), new ByteArrayEntity(data, ContentType.APPLICATION_OCTET_STREAM));
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void put(InputStream is) throws APIException {
        HttpResponse response = client.put(getUrl(), new InputStreamEntity(is, -1, ContentType.APPLICATION_OCTET_STREAM));
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void put(File file) throws APIException, FileNotFoundException {
        put(new FileInputStream(file));
    }

    public void delete() throws APIException {
        HttpResponse response = client.delete(getUrl());
        HttpClientHelpers.throwIfNotOk(response);
    }
}
