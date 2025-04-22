package com.example.yogaadmin_uioptimized.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataUtil {
    public static <T> T getValue(final LiveData<T> liveData) {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        try {
            latch.await(2, TimeUnit.SECONDS); // Wait for maximum 2 seconds
        } catch (InterruptedException e) {
            throw new RuntimeException("LiveData value was not set in 2 seconds", e);
        }
        //noinspection unchecked
        return (T) data[0];
    }
}