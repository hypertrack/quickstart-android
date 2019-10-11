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

public class MainActivity extends AppCompatActivity implements TrackingStateObserver.OnTrackingStateChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PUBLISHABLE_KEY = "paste_your_key_here";

    private Button trackingSwitcher;
    private TextView deviceId;

    private HyperTrack sdkInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackingSwitcher = findViewById(R.id.trackingButton);
        deviceId = findViewById(R.id.deviceIdTextView);

        sdkInstance = HyperTrack.getInstance(this, PUBLISHABLE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sdkInstance.isRunning()) {
            onTrackingStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdkInstance.removeTrackingListener(this);
    }

    public void onClick(View view) {
        Log.d(TAG, "onClick for view " + view.getId());

        if (view.getId() == R.id.trackingButton) {
            Log.d(TAG, "Tracking button pressed");
            if (sdkInstance.isRunning()) {
                sdkInstance.stop();
            } else {
                // Optional: add label to easily distinguish device in dashboard
                sdkInstance.setDeviceName(getString(R.string.name));
                // Optional: add listener to handle tracking state changes properly
                sdkInstance.addTrackingListener(this);
                sdkInstance.start();

                if (BuildConfig.DEBUG) {
                    HyperTrack.enableDebugLogging();
                }
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
        deviceId.setText(sdkInstance.getDeviceID());
        trackingSwitcher.setText(getString(R.string.pause_tracking));

    }

    @Override
    public void onTrackingStop() {
        trackingSwitcher.setText(getString(R.string.resume_tracking));
    }

}
