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
        HyperTrackCore.initialize(getApplicationContext(),getString(R.string.your_publishable_key));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "Application onTerminate");
        HyperTrackCore.onStop();
    }
}
