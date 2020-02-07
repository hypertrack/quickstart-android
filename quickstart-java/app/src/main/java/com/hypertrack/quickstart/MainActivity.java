package com.hypertrack.quickstart;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hypertrack.quickstart.android.github.R;
import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.TrackingError;
import com.hypertrack.sdk.TrackingStateObserver;

public class MainActivity extends AppCompatActivity implements TrackingStateObserver.OnTrackingStateChangeListener {
    private static final String TAG = "MainActivity";
    private static final String PUBLISHABLE_KEY = "paste_your_key_here";

    private TextView trackingStatusLabel;
    private HyperTrack sdkInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        TextView deviceId = findViewById(R.id.deviceIdTextView);
        trackingStatusLabel = findViewById(R.id.statusLabel);
        HyperTrack.enableDebugLogging();

        sdkInstance = HyperTrack
                .getInstance(this, PUBLISHABLE_KEY)
                .addTrackingListener(this);

        deviceId.setText(sdkInstance.getDeviceID());
        Log.d(TAG, "device id is " + sdkInstance.getDeviceID());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (sdkInstance.isRunning()) {
            onTrackingStart();
        } else {
            onTrackingStop();
        }

        sdkInstance.requestPermissionsIfNecessary();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdkInstance.removeTrackingListener(this);
    }

    // TrackingStateObserver.OnTrackingStateChangeListener interface methods

    @Override
    public void onError(TrackingError trackingError) {
        Log.d(TAG, "onError: " + trackingError.message);
        trackingStatusLabel.setBackgroundColor(getResources().getColor(R.color.colorLabelError));
        switch (trackingError.code) {
            case TrackingError.INVALID_PUBLISHABLE_KEY_ERROR:
            case TrackingError.AUTHORIZATION_ERROR:
                trackingStatusLabel.setText(R.string.check_publishable_key);
                break;
            case TrackingError.GPS_PROVIDER_DISABLED_ERROR:
                trackingStatusLabel.setText(R.string.enable_gps);
                break;
            case TrackingError.PERMISSION_DENIED_ERROR:
                trackingStatusLabel.setText(R.string.permissions_not_granted);
                break;
            default:
                trackingStatusLabel.setText(R.string.cant_start_tracking);

        }
    }

    @Override
    public void onTrackingStart() {
        trackingStatusLabel.setBackgroundColor(getResources().getColor(R.color.colorLabelActive));
        trackingStatusLabel.setText(R.string.tracking_active);

    }

    @Override
    public void onTrackingStop() {
        trackingStatusLabel.setBackgroundColor(getResources().getColor(R.color.colorLabelStopped));
        trackingStatusLabel.setText(R.string.tracking_stopped);

    }

}
