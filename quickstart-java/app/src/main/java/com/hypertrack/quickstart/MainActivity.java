package com.hypertrack.quickstart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hypertrack.quickstart.android.java.R;
import com.hypertrack.sdk.android.HyperTrack;
import com.hypertrack.sdk.android.Json;
import com.hypertrack.sdk.android.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** @noinspection FieldCanBeLocal*/
public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";

    private HyperTrack.Cancellable errorsSubscription;
    private HyperTrack.Cancellable isAvailableSubscription;
    private HyperTrack.Cancellable isTrackingSubscription;
    private HyperTrack.Cancellable locationSubscription;
    private HyperTrack.Cancellable locateSubscription;
    private HyperTrack.Cancellable ordersSubscription;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvDeviceId = findViewById(R.id.tvDeviceId);
        TextView tvErrors = findViewById(R.id.tvErrors);
        TextView tvIsAvailable = findViewById(R.id.tvIsAvailable);
        TextView tvIsTracking = findViewById(R.id.tvIsTracking);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvOrders = findViewById(R.id.tvOrders);

        View btnAddGeotag = findViewById(R.id.btnAddGeotag);
        View btnAddGeotagWithExpectedLocation = findViewById(R.id.btnAddGeotagWithExpectedLocation);
        View btnGetErrors = findViewById(R.id.btnGetErrors);
        View btnGetIsAvailable = findViewById(R.id.btnGetIsAvailable);
        View btnGetIsTracking = findViewById(R.id.btnGetIsTracking);
        View btnGetLocation = findViewById(R.id.btnGetLocation);
        View btnGetMetadata = findViewById(R.id.btnGetMetadata);
        View btnGetName = findViewById(R.id.btnGetName);
        View btnGetOrders = findViewById(R.id.btnGetOrders);
        View btnLocate = findViewById(R.id.btnLocate);
        View btnSetAvailable = findViewById(R.id.btnSetAvailable);
        View btnSetUnavailable = findViewById(R.id.btnSetUnavailable);
        View btnStartPermissionsFlow = findViewById(R.id.btnStartPermissionsFlow);
        View btnStartTracking = findViewById(R.id.btnStartTracking);
        View btnStopTracking = findViewById(R.id.btnStopTracking);

        // Get Device ID
        String deviceId = HyperTrack.getDeviceID();
        Log.d(TAG, "Device ID: " + deviceId);
        tvDeviceId.setText(deviceId);

        // `worker_handle` is used to link the device and the driver.
        // You can use any unique user identifier here.
        // The recommended way is to set it on app login in set it to null on logout
        // (to remove the link between the device and the worker)
        HyperTrack.setWorkerHandle("test_worker_quickstart_android_java");

        // Set Name
        String name = "Quickstart Android Java";
        HyperTrack.setName(name);
        Log.d(TAG, "Name set to: " + name);

        // Set Metadata
        Map<String, Object> metadata = new HashMap<>();
        // `driver_handle` is used to link the device and the driver.
        // You can use any unique user identifier here.
        // The recommended way is to set it on app login in set it to null on logout
        // (to remove the link between the device and the driver)
        metadata.put("driver_handle", "test_driver_quickstart_android_java");
        // You can also add any custom data to the metadata.
        metadata.put("source", name);
        metadata.put("employee_id", new Random().nextInt(10000));

        // We convert metadata to custom Json object to make sure that the data is Json compatible
        // If it can't be converted to Json, the result will be null
        @Nullable Json.Object metadataJson = Json.fromMap(metadata);
        // don't forget to check it and add proper error handling
        if (metadataJson == null) {
            throw new RuntimeException("Metadata can't be converted to Json");
        }

        HyperTrack.setMetadata(metadataJson);
        Log.d(TAG, "Metadata set to: " + metadata.toString());

        tvDeviceId.setOnClickListener(v -> {
            // copy device id to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Device ID", deviceId);
            clipboard.setPrimaryClip(clip);
        });
        
        btnStartTracking.setOnClickListener(v -> {
            HyperTrack.setTracking(true);
        });

        btnStopTracking.setOnClickListener(v -> {
            HyperTrack.setTracking(false);
        });

        btnSetAvailable.setOnClickListener(v -> {
            HyperTrack.setAvailable(true);
        });

        btnSetUnavailable.setOnClickListener(v -> {
            HyperTrack.setAvailable(false);
        });

        btnAddGeotag.setOnClickListener(v -> {
            // Create geotag payload
            Map<String, Object> geotagPayload = new HashMap<>();
            /*
              geotagPayload is an arbitrary object.
              You can put there any JSON-serializable data.
              It will be displayed in the HyperTrack dashboard and
              available in the webhook events.
             */
            geotagPayload.put("payload", "Quickstart AndroidJava");
            geotagPayload.put("value", new Random().nextDouble());

            // We convert geotagPayload to custom Json object to make sure that the data is Json compatible
            // If it can't be converted to Json, the result will be null
            @Nullable Json.Object geotagPayloadJson = Json.fromMap(geotagPayload);
            // don't forget to check it and add proper error handling
            if (geotagPayloadJson == null) {
                throw new RuntimeException("Geotag payload can't be converted to Json");
            }

            // Add geotag
            Result<HyperTrack.Location, HyperTrack.LocationError> geotagResult =
                    HyperTrack.addGeotag(geotagPayloadJson);
            showDialog("Geotag result", getLocationResultText(geotagResult));
        });

        btnAddGeotagWithExpectedLocation.setOnClickListener(v -> {
            // Create geotag payload
            Map<String, Object> geotagPayload = new HashMap<>();
            /*
              geotagPayload is an arbitrary object.
              You can put there any JSON-serializable data.
              It will be displayed in the HyperTrack dashboard and
              available in the webhook events.
             */
            geotagPayload.put("payload", "Quickstart AndroidJava");
            geotagPayload.put("value", new Random().nextDouble());

            // We convert geotagPayload to custom Json object to make sure that the data is Json compatible
            // If it can't be converted to Json, the result will be null
            @Nullable Json.Object geotagPayloadJson = Json.fromMap(geotagPayload);
            // don't forget to check it and add proper error handling
            if (geotagPayloadJson == null) {
                throw new RuntimeException("Geotag payload can't be converted to Json");
            }

            // Create expected location
            HyperTrack.Location expectedLocation = new HyperTrack.Location(
                    37.775,
                    -122.4183
            );

            // Add geotag
            Result<HyperTrack.LocationWithDeviation, HyperTrack.LocationError> geotagResult =
                    HyperTrack.addGeotag(geotagPayloadJson, expectedLocation);
            showDialog("Geotag with expected location result", getLocationWithDeviationResultText(geotagResult));
        });

        btnLocate.setOnClickListener(v -> {
            // you can use subscription object to unsubscribe from the locate result
            // with locateSubscription.cancel()
            locateSubscription = HyperTrack.locate(locationResult -> {
                showDialog("Locate result", getLocateResultText(locationResult));
                return null;
            });
        });

        btnGetErrors.setOnClickListener(v -> {
            showDialog("Errors", getErrorsText(HyperTrack.getErrors()));
        });

        btnGetIsAvailable.setOnClickListener(v -> {
            showDialog("Get Is available", "isAvailable: " + HyperTrack.isAvailable());
        });

        btnGetIsTracking.setOnClickListener(v -> {
            showDialog("Get Is tracking", "isTracking: " + HyperTrack.isTracking());
        });

        btnGetLocation.setOnClickListener(v -> {
            showDialog("Get location", getLocationResultText(HyperTrack.getLocation()));
        });

        btnGetMetadata.setOnClickListener(v -> {
            showDialog("Get metadata", "metadata: " + HyperTrack.getMetadata());
        });

        btnGetName.setOnClickListener(v -> {
            showDialog("Get name", "name: " + HyperTrack.getName());
        });

        btnGetOrders.setOnClickListener(v -> {
            showDialog("Get orders", getOrdersText(HyperTrack.getOrders()));
        });

        btnStartPermissionsFlow.setOnClickListener(view -> {
            PermissionsFlow.startPermissionsFlow(this);
        });

        errorsSubscription = HyperTrack.subscribeToErrors(errors -> {
            tvErrors.setText(getErrorsText(errors));
            return null;
        });

        isAvailableSubscription = HyperTrack.subscribeToIsAvailable(isAvailable -> {
            tvIsAvailable.setText("Is available: " + isAvailable.toString());
            return null;
        });

        isTrackingSubscription = HyperTrack.subscribeToIsTracking(isTracking -> {
            tvIsTracking.setText("Is tracking: " + isTracking.toString());
            return null;
        });

        locationSubscription = HyperTrack.subscribeToLocation(locationResult -> {
            tvLocation.setText(getLocationResultText(locationResult));
            return null;
        });

        ordersSubscription = HyperTrack.subscribeToOrders(orders -> {
            tvOrders.setText(getOrdersText(orders));
            return null;
        });
    }

    private String getErrorsText(Set<? extends HyperTrack.Error> errors) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");

        for (HyperTrack.Error error : errors) {
            String errorName;

            if (error instanceof HyperTrack.Error.BlockedFromRunning) {
                errorName = "BlockedFromRunning";
            } else if (error instanceof HyperTrack.Error.InvalidPublishableKey) {
                errorName = "InvalidPublishableKey";
            } else if (error instanceof HyperTrack.Error.Location.Mocked) {
                errorName = "Location.Mocked";
            } else if (error instanceof HyperTrack.Error.Location.ServicesDisabled) {
                errorName = "Location.ServicesDisabled";
            } else if (error instanceof HyperTrack.Error.Location.ServicesUnavailable) {
                errorName = "Location.ServicesUnavailable";
            } else if (error instanceof HyperTrack.Error.Location.SignalLost) {
                errorName = "Location.SignalLost";
            } else if (error instanceof HyperTrack.Error.NoExemptionFromBackgroundStartRestrictions) {
                errorName = "NoExemptionFromBackgroundStartRestrictions";
            } else if (error instanceof HyperTrack.Error.Permissions.Location.Denied) {
                errorName = "Permissions.Location.Denied";
            } else if (error instanceof HyperTrack.Error.Permissions.Location.InsufficientForBackground) {
                errorName = "Permissions.Location.InsufficientForBackground";
            } else if (error instanceof HyperTrack.Error.Permissions.Location.ReducedAccuracy) {
                errorName = "Permissions.Location.ReducedAccuracy";
            } else if (error instanceof HyperTrack.Error.Permissions.Notifications.Denied) {
                errorName = "Permissions.Notifications.Denied";
            } else {
                throw new RuntimeException("Unknown error type " + error.getClass().getName());
            }

            builder.append("\t").append(errorName);
            builder.append(",\n");
        }

        builder.append("]");
        return builder.toString();
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
            return getLocationErrorText(error);
        }
    }

    private String getLocationErrorText(HyperTrack.LocationError locationError) {
        if(locationError instanceof HyperTrack.LocationError.Errors) {
            HyperTrack.LocationError.Errors errors = (HyperTrack.LocationError.Errors) locationError;
            return getErrorsText(errors.getErrors());
        } else if(locationError instanceof HyperTrack.LocationError.NotRunning) {
            return "Not running";
        } else if(locationError instanceof HyperTrack.LocationError.Starting) {
            return "Starting";
        } else {
            throw new RuntimeException("Unknown error type " + locationError.getClass().getName());
        }
    }

    private String getLocationWithDeviationResultText(Result<HyperTrack.LocationWithDeviation, HyperTrack.LocationError> locationResult) {
        if (locationResult instanceof Result.Success) {
            HyperTrack.LocationWithDeviation location = (
                    (Result.Success<HyperTrack.LocationWithDeviation, HyperTrack.LocationError>) locationResult
            ).getSuccess();
            return location.toString();
        } else {
            HyperTrack.LocationError error = (
                    (Result.Failure<HyperTrack.LocationWithDeviation, HyperTrack.LocationError>) locationResult
            ).getFailure();
            if(error instanceof HyperTrack.LocationError.Errors) {
                HyperTrack.LocationError.Errors errors = (HyperTrack.LocationError.Errors) error;
                return getErrorsText(errors.getErrors());
            } else if(error instanceof HyperTrack.LocationError.NotRunning) {
                return "Not running";
            } else if(error instanceof HyperTrack.LocationError.Starting) {
                return "Starting";
            } else {
                throw new RuntimeException("Unknown error type " + error.getClass().getName());
            }
        }
    }

    private String getLocateResultText(Result<HyperTrack.Location, Set<HyperTrack.Error>> locationResult) {
        if (locationResult instanceof Result.Success) {
            HyperTrack.Location location = (
                    (Result.Success<HyperTrack.Location, Set<HyperTrack.Error>>) locationResult
            ).getSuccess();
            return location.toString();
        } else {
            Set<HyperTrack.Error> errors = (
                    (Result.Failure<HyperTrack.Location, Set<HyperTrack.Error>>) locationResult
            ).getFailure();
            return getErrorsText(errors);
        }
    }

    private String getOrdersText(Map<String, HyperTrack.Order> orders) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");

        for (HyperTrack.Order order : orders.values()) {
            Result<Boolean, HyperTrack.LocationError> isInsideGeofence = order.isInsideGeofence();
            String isInsideGeofenceText;

            if (isInsideGeofence instanceof Result.Success) {
                isInsideGeofenceText = ((Result.Success<Boolean, HyperTrack.LocationError>) isInsideGeofence).getSuccess().toString();
            } else {
                isInsideGeofenceText = getLocationErrorText(((Result.Failure<Boolean, HyperTrack.LocationError>) isInsideGeofence).getFailure());
            }

            builder.append("\t").append(order.getOrderHandle()).append(" > isInsideGeofence: ").append(isInsideGeofenceText);
            builder.append(",\n");
        }

        builder.append("]");
        return builder.toString();
    }

    @Override
    protected void onDestroy() {
        if(errorsSubscription != null) {
            errorsSubscription.cancel();
        }
        if(isAvailableSubscription != null) {
            isAvailableSubscription.cancel();
        }
        if(isTrackingSubscription != null) {
            isTrackingSubscription.cancel();
        }
        if(locationSubscription != null) {
            locationSubscription.cancel();
        }
        if(locateSubscription != null) {
            locateSubscription.cancel();
        }
        if(ordersSubscription != null) {
            ordersSubscription.cancel();
        }
        super.onDestroy();
    }

    private void showDialog(
            String title,
            String text
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton("Close", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

}
