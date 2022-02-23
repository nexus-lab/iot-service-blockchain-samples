package org.nexus_lab.iot_service_blockchain.sample.crystalball;

public class LiveDataWrapper<T> {
    private final T mData;
    private final Throwable mThrowable;

    public LiveDataWrapper(T data, Throwable throwable) {
        mData = data;
        mThrowable = throwable;
    }

    public T getData() {
        return mData;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
