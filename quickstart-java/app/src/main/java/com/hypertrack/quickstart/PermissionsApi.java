package com.hypertrack.quickstart;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class PermissionsApi {

    private static final int REQUEST_CODE = 4214146;

    static boolean isBackgroundLocationPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return activity.checkSelfPermission(
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    static boolean isLocationPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.checkSelfPermission(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    static boolean isLocationServicesEnabled(Activity activity) {
        return ((LocationManager)
                activity.getSystemService(android.content.Context.LOCATION_SERVICE)
        )
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    static boolean isNotificationsPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return activity.checkSelfPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    static void openAppSettings(Activity activity) {
        activity.startActivity(
                new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + activity.getPackageName())
                )
        );
    }

    static void openLocationServicesSettings(Activity activity) {
        activity.startActivity(
                new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
        );
    }

    static void requestLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                    new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_CODE
            );
        }
    }

    static void requestBackgroundLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.requestPermissions(
                    new String[] {
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    REQUEST_CODE
            );
        }
    }

    static void requestNotificationsPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.requestPermissions(
                    new String[] {
                            android.Manifest.permission.POST_NOTIFICATIONS
                    },
                    REQUEST_CODE
            );
        }
    }
}
