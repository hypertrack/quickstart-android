package com.hypertrack.quickstart;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.permissions.LocationPermissionCallback;

import java.util.Collections;

public class MainActivity extends AppCompatActivity implements LocationPermissionCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button requestLocationPermission;
    private Button trackingSwitcher;
    private TextView deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestLocationPermission = findViewById(R.id.locationPermissionButton);
        trackingSwitcher = findViewById(R.id.trackingButton);
        deviceId = findViewById(R.id.deviceIdTextView);

        HyperTrack.setNameAndMetadataForDevice(getString(R.string.name), Collections.<String, Object>emptyMap());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        deviceId.setText(HyperTrack.getDeviceId());
        updateUiWithDelay();

    }

    public void onClick(View view) {
        Log.d(TAG, "onClick for view " + view.getId());

        switch (view.getId()) {
            case R.id.locationPermissionButton:
                if (!HyperTrack.checkLocationPermission(this)) {
                    HyperTrack.requestLocationPermission(this, this);
                }
                break;
            case R.id.trackingButton:
                if (HyperTrack.isTracking()) {
                    HyperTrack.pauseTracking();
                } else {
                    HyperTrack.resumeTracking();
                }
        }
    }

    private void updatedUI() {
        Log.d(TAG, "Updating UI");
        // checking for location data access permissions:
        if (HyperTrack.checkLocationPermission(this)) {
            requestLocationPermission.setEnabled(false);
            if (HyperTrack.isServiceRunning()) {
                requestLocationPermission.setText(getString(R.string.service_running));
            } else {
                requestLocationPermission.setText(getString(R.string.service_not_running));
            }
            trackingSwitcher.setEnabled(true);
        } else {
            requestLocationPermission.setEnabled(true);
            requestLocationPermission.setText(getString(R.string.request_permission));
            trackingSwitcher.setEnabled(false);
        }

        if (HyperTrack.isTracking()) {
            trackingSwitcher.setText(getString(R.string.pause_tracking));
        } else {
            trackingSwitcher.setText(getString(R.string.resume_tracking));
        }
    }

    @Override
    public void onLocationPermissionGranted() {
        Log.d(TAG, "Location data access permission was granted");
        updateUiWithDelay();
    }

    @Override
    public void onLocationPermissionDenied() {
        Log.w(TAG, "Location data access permission was denied");
        updatedUI();
    }

    private void updateUiWithDelay() {
                final Handler handler = new Handler();
                handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() { updatedUI(); }
                        },
                5000);
    }
}
