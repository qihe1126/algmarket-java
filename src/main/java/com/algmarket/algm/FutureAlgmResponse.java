package com.algmarket.algm;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureAlgmResponse implements Future<AlgmResponse> {
    protected Future<AlgmResponse> promise;

    protected FutureAlgmResponse() {
    }

    public FutureAlgmResponse(Future<AlgmResponse> promise) {
        this.promise = promise;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return promise.isCancelled();
    }

    public boolean isDone() {
        return promise.isDone();
    }

    public AlgmResponse get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    public AlgmResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout, unit);
    }
}
