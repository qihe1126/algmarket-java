package com.algmarket.algm;

public class AlgmRef {
    public final String algmUri;

    public AlgmRef(String algmUri) {
        this.algmUri = algmUri.replaceAll("^algm://|^/", "");
    }

    public String getUrl() {
        return "/v1/algm/" + algmUri;
    }

    @Override
    public String toString() {
        return "algm://" + algmUri;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
