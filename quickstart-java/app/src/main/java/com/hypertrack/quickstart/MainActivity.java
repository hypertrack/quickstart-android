package com.hypertrack.quickstart;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hypertrack.quickstart.sdk_androidjava.R;
import com.hypertrack.sdk.android.HyperTrack;
import com.hypertrack.sdk.android.Result;

import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private HyperTrack.Cancellable locationSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView deviceId = findViewById(R.id.tvDeviceId);
        TextView tvLocation = findViewById(R.id.tvLocation);

        deviceId.setText(HyperTrack.getDeviceID());

        findViewById(R.id.bStartTracking).setOnClickListener(view -> {
            HyperTrack.setTracking(true);
        });

        findViewById(R.id.bStopTracking).setOnClickListener(view -> {
            HyperTrack.setTracking(false);
        });

        findViewById(R.id.bStartPermissionsFlow).setOnClickListener(view -> {
            PermissionsFlow.startPermissionsFlow(this);
        });

        locationSubscription = HyperTrack.subscribeToLocation(locationResult -> {
            tvLocation.setText(getLocationResultText(locationResult));
            return null;
        });
    }

    private String getLocationResultText(Result<HyperTrack.Location, HyperTrack.LocationError> locationResult) {
        if (locationResult instanceof Result.Success) {
            HyperTrack.Location location = (
                    (Result.Success<HyperTrack.Location, HyperTrack.LocationError>) locationResult
            ).getSuccess();
            return location.toString();
        } else {
            HyperTrack.LocationError error = (
                    (Result.Failure<HyperTrack.Location, HyperTrack.LocationError>) locationResult
            ).getFailure();
            return error.toString();
        }
    }

    @Override
    protected void onDestroy() {
        locationSubscription.cancel();
        super.onDestroy();
    }

}
