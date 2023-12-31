package com.hypertrack.quickstart;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hypertrack.quickstart.sdk_androidjava.R;
import com.hypertrack.sdk.android.HyperTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView trackingStatusLabel;

    /**
     * HyperTrack subscription for isTracking
     */
    private HyperTrack.Cancellable isTrackingSubscription;
    /**
     * HyperTrack subscription for errors
     */
    private HyperTrack.Cancellable errorSubscription;

    /**
     * This list can contain any permissions you need EXCEPT for the background location permission
     * to Add more permissions, go to {@link #initPermissions()} method.
     */
    private List<String> regularPermissionsToBeRequested = new ArrayList<>();

    /**
     * A special Background location permission configuration
     */
    private String backgroundLocationPermission;

    /**
     * A special background location permission launcher
     * It can be run only if regular location permissions are granted
     */
    private ActivityResultLauncher<String> requestBackgroundPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (areRegularPermissionsGranted() && isBackgroundLocationPermissionGranted()) {
                    HyperTrack.setTracking(true);
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    new AlertDialog.Builder(this)
                            .setTitle("Tracking feature disabled")
                            .setMessage("Without Background Location permission the tracking is not accurate")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            });

    /**
     * Regular permissions launcher
     */
    private ActivityResultLauncher<String[]> requestRegularPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissionsGrantedMap -> {
                if (areRegularPermissionsGranted()) {
                    if (isBackgroundLocationPermissionGranted()) {
                        HyperTrack.setTracking(true);
                    } else {
                        showBackgroundLocationPermissionRationaleDialogOrAskPermissionDirectly();
                    }
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    new AlertDialog.Builder(this)
                            .setTitle("Tracking feature disabled")
                            .setMessage("Without Location and Notifications permissions the tracking is not available")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPermissions();

        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        TextView deviceId = findViewById(R.id.deviceIdTextView);
        trackingStatusLabel = findViewById(R.id.statusLabel);

        deviceId.setText(HyperTrack.getDeviceID());
        Log.d(TAG, "device id is " + HyperTrack.getDeviceID());

        trackingStatusLabel.setOnClickListener(view -> {
            if (HyperTrack.isTracking()) {
                HyperTrack.setTracking(false);
            } else {
                requestPermissionsAndStartTracking();
            }
        });

        subscribeToIsTrackingChanges();

        subscribeToErrors();
    }

    @Override
    protected void onDestroy() {
        isTrackingSubscription.cancel();
        errorSubscription.cancel();
        super.onDestroy();
    }

    /**
     * Subscribes to HyperTrack SDK Tracking Changes
     */
    private void subscribeToIsTrackingChanges() {
        isTrackingSubscription = HyperTrack.subscribeToIsTracking(isTracking -> {
            if (isTracking) {
                onTrackingStart();
            } else {
                onTrackingStop();
            }
            return null;
        });
    }

    /**
     * Subscribes to HyperTrack SDK Errors
     */
    private void subscribeToErrors() {
        errorSubscription = HyperTrack.subscribeToErrors(
                errors -> {
                    onError(errors);
                    return null;
                }
        );
    }

    /**
     * Initializes all regular permissions according to SDK version
     * Initializes also the special Background location permission
     */
    private void initPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            regularPermissionsToBeRequested.add(android.Manifest.permission.POST_NOTIFICATIONS);
        }
        regularPermissionsToBeRequested.add(android.Manifest.permission.ACCESS_FINE_LOCATION);

        // This permission has to be asked separately after ACCESS_FINE_LOCATION is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermission = android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
        }
    }

    /**
     * Handles HyperTrack Errors
     * @param errors
     */
    public void onError(Set<? extends HyperTrack.Error> errors) {
        Log.d(TAG, "onError: " + errors.toString());
        trackingStatusLabel.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLabelError));
        for (HyperTrack.Error error : errors) {
            if (error instanceof HyperTrack.Error.InvalidPublishableKey) {
                trackingStatusLabel.setText(R.string.check_publishable_key);
            } else if (error instanceof HyperTrack.Error.Permissions) {
                trackingStatusLabel.setText(R.string.permissions_not_granted);
            } else if (error instanceof HyperTrack.Error.Location) {
                trackingStatusLabel.setText(R.string.enable_gps);
            } else if (error instanceof HyperTrack.Error.NoExemptionFromBackgroundStartRestrictions) {
                trackingStatusLabel.setText(R.string.cant_start_tracking);
            } else if (error instanceof HyperTrack.Error.BlockedFromRunning) {
                trackingStatusLabel.setText(R.string.cant_start_tracking);
            }
        }
    }

    /**
     * Changes the UI when Tracking Starts
     */
    public void onTrackingStart() {
        trackingStatusLabel.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLabelActive));
        trackingStatusLabel.setText(R.string.tracking_active);
    }

    /**
     * Changes the UI when Tracking stops
     */
    public void onTrackingStop() {
        trackingStatusLabel.setBackgroundColor(ContextCompat.getColor(this, R.color.colorLabelStopped));
        trackingStatusLabel.setText(R.string.tracking_stopped);
    }

    /**
     * Checks if regular permissions are granted
     *
     * @return true if all regular permissions are granted
     */
    private boolean areRegularPermissionsGranted() {
        boolean areRegularPermissionsGranted = true;
        for (String permission : regularPermissionsToBeRequested) {
            areRegularPermissionsGranted = areRegularPermissionsGranted &&
                    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return areRegularPermissionsGranted;
    }

    /**
     * Checks if background location permission is granted
     *
     * @return true if permission is needed and granted or when permission is not needed
     */
    private boolean isBackgroundLocationPermissionGranted() {
        if (backgroundLocationPermission != null) {
            return ContextCompat.checkSelfPermission(this, backgroundLocationPermission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /**
     * Shows the regular permissions rationale dialog if needed.
     */
    private void showRegularPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission required")
                .setMessage("Location and Notifications permissions are needed for proper tracking behavior")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    askForRegularPermissions();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    /**
     * Shows the special background location permission rationale dialog if needed.
     */
    private void showBackgroundLocationPermissionRationaleDialogOrAskPermissionDirectly() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, backgroundLocationPermission)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Background location permission is needed for accurate tracking behavior")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        askForBackgroundLocationPermission();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } else {
            askForBackgroundLocationPermission();
        }
    }

    /**
     * Directly asks for regular permissions
     */
    private void askForRegularPermissions() {
        requestRegularPermissionsLauncher.launch(regularPermissionsToBeRequested.toArray(new String[0]));
    }

    /**
     * Asks for special background permission
     */
    private void askForBackgroundLocationPermission() {
        requestBackgroundPermissionLauncher.launch(backgroundLocationPermission);
    }

    /**
     * Special method called when the user wants to start tracking.
     * It checks all the permissions and does all the necessary actions in proper order.
     */
    private void requestPermissionsAndStartTracking() {
        if (areRegularPermissionsGranted() && isBackgroundLocationPermissionGranted()) {
            HyperTrack.setTracking(true);
        } else if (!areRegularPermissionsGranted()) {
            boolean rationaleShown = false;
            for (String permission : regularPermissionsToBeRequested) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    rationaleShown = true;
                    showRegularPermissionRationaleDialog();
                }
            }
            if (!rationaleShown) {
                askForRegularPermissions();
            }
        } else {
            showBackgroundLocationPermissionRationaleDialogOrAskPermissionDirectly();
        }
    }
}
