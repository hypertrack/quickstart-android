package com.hypertrack.quickstart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.TrackingError;
import com.hypertrack.sdk.TrackingStateObserver;

import java.util.Collections;

public class MainActivity extends AppCompatActivity implements TrackingStateObserver.OnTrackingStateChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PUBLISHABLE_KEY = "paste_your_key_here";

    private Button trackingSwitcher;
    private TextView deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackingSwitcher = findViewById(R.id.trackingButton);
        deviceId = findViewById(R.id.deviceIdTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HyperTrack.isTracking()) {
            onTrackingStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HyperTrack.removeTrackingStateListener(this);
    }

    public void onClick(View view) {
        Log.d(TAG, "onClick for view " + view.getId());

        switch (view.getId()) {
            case R.id.trackingButton:
                Log.d(TAG, "Tracking button pressed");
                if (HyperTrack.isTracking()) {
                    HyperTrack.stopTracking();
                } else {
                    // Initialize SDK with activity instance and start tracking immediately. It's preferred to use main activity.
                    HyperTrack.initialize(this, PUBLISHABLE_KEY);
                    HyperTrack.addTrackingStateListener(this);

                    if (BuildConfig.DEBUG) {
                        HyperTrack.enableDebugLogging();
                    }
                    // It gives possibility to add unique attributes to each specific device.
                    HyperTrack.setNameAndMetadataForDevice(getString(R.string.name), Collections.<String, Object>emptyMap());
                }
        }
    }

    // TrackingStateObserver.OnTrackingStateChangeListener interface methods

    @Override
    public void onError(TrackingError trackingError) {
        if (trackingError.code == TrackingError.INVALID_PUBLISHABLE_KEY_ERROR
                || trackingError.code == TrackingError.AUTHORIZATION_ERROR) {
            Log.e(TAG, "Initialization failed");
        } else {
            Log.e(TAG, "Tracking failed");
        }
        trackingSwitcher.setText(getString(R.string.resume_tracking));
    }

    @Override
    public void onTrackingStart() {
        deviceId.setText(HyperTrack.getDeviceId());
        trackingSwitcher.setText(getString(R.string.pause_tracking));

    }

    @Override
    public void onTrackingStop() {
        trackingSwitcher.setText(getString(R.string.resume_tracking));
    }

}
