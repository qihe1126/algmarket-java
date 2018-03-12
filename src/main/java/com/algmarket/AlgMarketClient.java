package com.algmarket;

import com.algmarket.algm.Algm;
import com.algmarket.algm.AlgmRef;
import com.algmarket.client.Auth;
import com.algmarket.client.HttpClient;
import com.algmarket.data.DataDirectory;
import com.algmarket.data.DataFile;
import com.algmarket.model.ModelDirectory;
import com.algmarket.model.ModelFile;

public final class AlgMarketClient {
    private HttpClient client;

    protected AlgMarketClient(Auth auth, String apiAddress, int maxConnections) {
        this.client = new HttpClient(auth, apiAddress, maxConnections);
    }

    public Algm algm(String algmUri) {
        return new Algm(client, new AlgmRef(algmUri));
    }

    public DataFile file(String path) {
        return new DataFile(client, path);
    }

    public ModelFile modelfile(String path) {
        return new ModelFile(client, path);
    }

    public DataDirectory dir(String path) {
        return new DataDirectory(client, path);
    }

    public ModelDirectory dirModel(String path) {
        return new ModelDirectory(client, path);
    }
}
