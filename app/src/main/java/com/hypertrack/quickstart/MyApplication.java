package com.hypertrack.quickstart;

import android.app.Application;
import android.util.Log;

import com.hypertrack.core_android_sdk.HyperTrackCore;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
        HyperTrackCore.initialize(getApplicationContext(),"uvIAA8xJANxUxDgINOX62-LINLuLeymS6JbGieJ9PegAPITcr9fgUpROpfSMdL9kv-qFjl17NeAuBHse8Qu9sw");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "Application onTerminate");
        HyperTrackCore.onStop();
    }
}
