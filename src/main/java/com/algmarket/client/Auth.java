package com.algmarket.client;

import org.apache.http.HttpRequest;

public abstract class Auth {
    protected abstract void authenticateRequest(HttpRequest request);
}
