package com.algmarket;

import com.algmarket.client.SimpleAuth;

public final class AlgMarket {
    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private static AlgMarketClient defaultClient = null;

    private AlgMarket() {
    }

    public static AlgMarketClient client() {
        return getDefaultClient();
    }

    public static AlgMarketClient client(int maxConnections) {
        return new AlgMarketClient(null, null, maxConnections);
    }

    public static AlgMarketClient client(String simpleKey) {
        return new AlgMarketClient(new SimpleAuth(simpleKey), null, DEFAULT_MAX_CONNECTIONS);
    }

    public static AlgMarketClient client(String simpleKey, String apiAddress) {
        return new AlgMarketClient(new SimpleAuth(simpleKey), apiAddress, DEFAULT_MAX_CONNECTIONS);
    }

    public static AlgMarketClient client(String simpleKey, int maxConnections) {
        return new AlgMarketClient(new SimpleAuth(simpleKey), null, maxConnections);
    }

    public static AlgMarketClient client(String simpleKey, String apiAddress, int maxConnections) {
        return new AlgMarketClient(new SimpleAuth(simpleKey), apiAddress, maxConnections);
    }

    private static AlgMarketClient getDefaultClient() {
        if (defaultClient == null) {
            defaultClient = new AlgMarketClient(null, null, DEFAULT_MAX_CONNECTIONS);
        }
        return defaultClient;
    }
}
