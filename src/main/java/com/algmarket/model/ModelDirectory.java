package com.algmarket.model;

import com.algmarket.APIException;
import com.algmarket.client.HttpClient;
import com.algmarket.client.HttpClientHelpers;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ModelDirectory extends ModelObject {
    public ModelDirectory(HttpClient client, String dataUrl) {
        super(client, dataUrl, DataObjectType.DIRECTORY);
    }

    public boolean exists() throws APIException {
        HttpResponse response = this.client.get(getUrl());
        int status = response.getStatusLine().getStatusCode();
        if(status != 200 && status != 404) {
            throw APIException.fromHttpResponse(response);
        }
        return (200 == status);
    }

    @Deprecated
    public ModelFileIterator getFileIter() throws APIException {
        return new ModelFileIterator(this);
    }
    public Iterable<ModelFile> files() throws APIException {
        return new Iterable<ModelFile>() {
            public ModelFileIterator iterator() {
                return new ModelFileIterator(ModelDirectory.this);
            }
        };
    }

    @Deprecated
    public ModelDirectoryIterator getDirIter() throws APIException {
        return new ModelDirectoryIterator(this);
    }
    public Iterable<ModelDirectory> dirs() throws APIException {
        return new Iterable<ModelDirectory>() {
            public ModelDirectoryIterator iterator() {
                return new ModelDirectoryIterator(ModelDirectory.this);
            }
        };
    }

    public ModelFile file(String filename) {
        return new ModelFile(client, trimmedPath + "/" + filename);
    }

    public ModelFile putFile(File file) throws APIException, FileNotFoundException {
        ModelFile dataFile = new ModelFile(client, trimmedPath + "/" + file.getName());
        dataFile.put(file);
        return dataFile;
    }

    private class CreateDirectoryRequest {
        @SuppressWarnings("unused")
        private String name;
        private ModelAcl acl;
        CreateDirectoryRequest(String name, ModelAcl acl) {
            this.name = name;
            this.acl = acl;
        }
    }

    private class UpdateDirectoryRequest {
        @SuppressWarnings("unused")//Used indirectly by GSON
        private ModelAcl acl;
        UpdateDirectoryRequest(ModelAcl acl) {
            this.acl = acl;
        }
    }

    public void create() throws APIException {
        CreateDirectoryRequest reqObj = new CreateDirectoryRequest(this.getName(), null);
        Gson gson = new Gson();
        JsonElement inputJson = gson.toJsonTree(reqObj);

        String url = this.getParent().getUrl();
        HttpResponse response = this.client.post(url, new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON));
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void create(ModelAcl modelAcl) throws APIException {
        CreateDirectoryRequest reqObj = new CreateDirectoryRequest(this.getName(), modelAcl);
        Gson gson = new Gson();
        JsonElement inputJson = gson.toJsonTree(reqObj);

        String url = this.getParent().getUrl();

        HttpResponse response = this.client.post(url, new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON));
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void delete(boolean forceDelete) throws APIException {
        HttpResponse response = client.delete(getUrl() + "?force=" + forceDelete);
        HttpClientHelpers.throwIfNotOk(response);
    }

    public void delete() throws APIException {
        HttpResponse response = client.delete(getUrl() + "?force=false");
        HttpClientHelpers.throwIfNotOk(response);
    }

    public ModelAcl getPermissions() throws APIException {
        DirectoryListResponse response = getPage(null, true);
        return ModelAcl.fromAclResponse(response.acl);
    }

    public boolean updatePermissions(ModelAcl modelAcl) throws APIException {
        UpdateDirectoryRequest request = new UpdateDirectoryRequest(modelAcl);
        Gson gson = new Gson();
        JsonElement inputJson = gson.toJsonTree(request);

        StringEntity entity = new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON);
        HttpResponse response = client.patch(getUrl(), entity);

        HttpClientHelpers.throwIfNotOk(response);
        return true;
    }

    protected class FileMetadata {
        protected String filename;

        protected FileMetadata(String filename) {
            this.filename = filename;
        }
    }

    protected class DirectoryMetadata {
        protected String name;
        protected DirectoryMetadata(String name) {
            this.name = name;
        }
    }

    protected class DirectoryListResponse {
        protected List<FileMetadata> files;
        protected List<DirectoryMetadata> folders;
        protected String marker;
        protected Map<String, List<String>> acl;
        protected DirectoryListResponse(List<FileMetadata> files,
                                        List<DirectoryMetadata> folders,
                                        String marker,
                                        Map<String, List<String>> acl) {
            this.files = files;
            this.folders = folders;
            this.marker = marker;
            this.acl = acl;
        }
    }

    protected DirectoryListResponse getPage(String marker, Boolean getAcl) throws APIException {
        String url = getUrl();

        Map<String, String> params = new HashMap<String, String>();
        if (marker != null) {
            params.put("marker", marker);
        }
        if (getAcl) {
            params.put("acl", getAcl.toString());
        }

        return client.get(url, new TypeToken<DirectoryListResponse>(){}, params);
    }
}
