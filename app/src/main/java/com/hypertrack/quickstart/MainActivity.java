package com.hypertrack.quickstart;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hypertrack.core_android_sdk.HyperTrackCore;
import com.hypertrack.core_android_sdk.permissions.LocationPermissionCallback;

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



    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        deviceId.setText(HyperTrackCore.getDeviceId());
        updatedUI();

    }

    public void onClick(View view) {
        Log.d(TAG, "onClick for view " + view.getId());

        switch (view.getId()) {
            case R.id.locationPermissionButton:
                if (!HyperTrackCore.checkLocationPermission(this)) {
                    HyperTrackCore.requestLocationPermissions(this, this);
                }
                break;
            case R.id.trackingButton:
                if (HyperTrackCore.isTracking()) {
                    HyperTrackCore.pauseTracking();
                } else {
                    HyperTrackCore.resumeTracking();
                }
        }
    }

    private void updatedUI() {
        Log.d(TAG, "Updating UI");
        // checking for location data access permissions:
        if (HyperTrackCore.checkLocationPermission(this)) {
            requestLocationPermission.setEnabled(false);
            if (HyperTrackCore.isLocationServiceRunning()) {
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

        if (HyperTrackCore.isTracking()) {
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
