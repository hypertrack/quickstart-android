package com.hypertrack.quickstart;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hypertrack.sdk.HyperTrack;

import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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
        Log.d(TAG, "onResume");
        HyperTrack.enableDebugLogging();
        HyperTrack.setNameAndMetadataForDevice(getString(R.string.name), Collections.<String, Object>emptyMap());
        deviceId.setText(HyperTrack.getDeviceId());
        updateUiWithDelay();

    }

    public void onClick(View view) {
        Log.d(TAG, "onClick for view " + view.getId());

        switch (view.getId()) {
            case R.id.trackingButton:
                Log.d(TAG, "Tracking button pressed");
                if (HyperTrack.isTracking()) {
                    HyperTrack.stopTracking();
                    updateUiWithDelay();
                } else {
                    HyperTrack.initialize(this, PUBLISHABLE_KEY);
                    updateUiWithDelay();
                }

        }
    }

    private void updatedUI() {
        Log.d(TAG, "Updating UI");
        // checking for location data access permissions:
        trackingSwitcher.setEnabled(true);

        if (HyperTrack.isTracking()) {
            trackingSwitcher.setText(getString(R.string.pause_tracking));
        } else {
            trackingSwitcher.setText(getString(R.string.resume_tracking));
        }
        deviceId.setText(HyperTrack.getDeviceId());
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
