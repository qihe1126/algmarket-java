package com.algmarket;

public final class AlgMarketConfig {

    private static String apiAddress;

    private AlgMarketConfig() {

    }

    static {
        apiAddress = "http://api.algmarket.com";
    }

    public static String apiAddress() {
        return apiAddress;
    }

}
