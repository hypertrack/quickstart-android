package com.hypertrack.quickstart;

import android.app.Application;
import android.util.Log;

import com.hypertrack.sdk.HyperTrack;


public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
        HyperTrack.initialize(getApplicationContext(),getString(R.string.your_publishable_key));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "Application onTerminate");
        HyperTrack.onStop();
    }
}
