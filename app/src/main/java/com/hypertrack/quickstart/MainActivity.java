package com.hypertrack.quickstart;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hypertrack.sdk.Config;
import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.TrackingError;
import com.hypertrack.sdk.TrackingStateObserver;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PUBLISHABLE_KEY = "uvIAA8xJANxUxDgINOX62-LINLuLeymS6JbGieJ9PegAPITcr9fgUpROpfSMdL9kv-qFjl17NeAuBHse8Qu9sw";
//    private static final String PUBLISHABLE_KEY = "paste_your_key_here";

    private Button trackingSwitcher;
    private TextView deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackingSwitcher = findViewById(R.id.trackingButton);
        deviceId = findViewById(R.id.deviceIdTextView);

        // Add SDK state listener if needed. We do this before initialization to catch all events.
        HyperTrack.addTrackingStateListener(new TrackingStateObserver.OnTrackingStateChangeListener() {
            @Override
            public void onError(TrackingError trackingError) {
                Log.e(TAG, "SDK failed with error");
                switch (trackingError.getCode()) {
                    case TrackingError.AUTHORIZATION_ERROR:
                        // Handle this error if needed.
                        break;
                    case TrackingError.INVALID_PUBLISHABLE_KEY_ERROR:
                        // Check your publishable key and initialize SDK once again.
                        break;
                    case TrackingError.PERMISSION_DENIED_ERROR:
                        // User refused permission or they were not requested.
                        // Request permission from the user yourself or leave it to SDK.
                        break;
                    case TrackingError.GPS_PROVIDER_DISABLED_ERROR:
                        // User disabled GPS in device settings.
                        break;
                    case TrackingError.UNKNOWN_ERROR:
                        // Some error we can't recognize. It may be connected with network or some device features.
                        break;
                }
            }

            @Override
            public void onStart() {
                // It's called when the SDK starts tracking.
                deviceId.setText(HyperTrack.getDeviceId());
                trackingSwitcher.setText(getString(R.string.pause_tracking));
            }

            @Override
            public void onStop() {
                // It's called when the SDK stops tracking.
                trackingSwitcher.setText(getString(R.string.resume_tracking));
            }
        });

        // Initialize SDK with activity instance and start tracking immediately. It's preferred to use main activity.
        HyperTrack.initialize(this, PUBLISHABLE_KEY);

        // This is necessary to add unique attributes to each specific device.
        HyperTrack.setNameAndMetadataForDevice(getString(R.string.name), Collections.<String, Object>emptyMap());

        // Only for debug version
        if (BuildConfig.DEBUG) {
            HyperTrack.enableDebugLogging();
        }
    }

    public void onClick(View view) {
        Log.d(TAG, "onClick for view " + view.getId());

        switch (view.getId()) {
            case R.id.trackingButton:
                Log.d(TAG, "Tracking button pressed");
                if (HyperTrack.isTracking()) {
                    HyperTrack.stopTracking();
                } else {
                    HyperTrack.startTracking();
                }
        }
    }
}
