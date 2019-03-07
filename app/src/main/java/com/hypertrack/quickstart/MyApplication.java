package com.hypertrack.quickstart;

import android.app.Application;

import com.hypertrack.sdk.HyperTrack;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HyperTrack.initialize(getApplicationContext(),getString(R.string.your_publishable_key));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        HyperTrack.onStop();
    }
}
