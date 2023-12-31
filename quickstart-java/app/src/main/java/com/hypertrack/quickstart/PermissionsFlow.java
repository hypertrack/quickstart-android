package com.hypertrack.quickstart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PermissionsFlow {

    static void startPermissionsFlow(Activity activity) {
        if (!PermissionsApi.isLocationPermissionGranted(activity)) {
            requestForegroundLocation(activity);
        } else {
            onForegroundLocationGranted(activity);
        }
    }

    private static void showDialog(
            Activity activity,
            String title,
            String text,
            Function1<DialogInterface, Unit> onProceedClick,
            Function1<DialogInterface, Unit> onGoToSettingsClick
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton("Proceed", null);
        builder.setNegativeButton("Go To Settings", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            onProceedClick.invoke(dialog);
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            onGoToSettingsClick.invoke(dialog);
        });
    }

    private static void requestForegroundLocation(Activity activity) {
        showDialog(
                activity,
                "Please grant Location permission",
                "This app collects activity and location data \n to  manage your work on the move \n even when the app is closed or not in use\n\n" +
                        "We use this data to:\n" +
                        "- Show your movement history\n" +
                        "- Optimize routes\n" +
                        "- Mark places you visit\n" +
                        "- Make expense claims easier\n" +
                        "- Compute daily stats \n\t\t(like total driven distance)",
                dialog -> {
                    if (!PermissionsApi.isLocationPermissionGranted(activity)) {
                        PermissionsApi.requestLocationPermission(activity);
                    } else {
                        dialog.dismiss();
                        onForegroundLocationGranted(activity);
                    }
                    return null;
                },
                dialog -> {
                    PermissionsApi.openAppSettings(activity);
                    return null;
                }
        );
    }

    private static void onForegroundLocationGranted(Activity activity) {
        if (!PermissionsApi.isBackgroundLocationPermissionGranted(activity)) {
            requestBackgroundLocation(activity);
        } else {
            onBackgroundLocationGranted(activity);
        }
    }

    private static void requestBackgroundLocation(Activity activity) {
        showDialog(
                activity,
                "Allow All the Time Location Access",
                "The permission is required to automatically start tracking location based on your work schedule (without you having to open app). The app will always show a notification when tracking is active.",
                dialog -> {
                    if (!PermissionsApi.isBackgroundLocationPermissionGranted(activity)) {
                        PermissionsApi.requestBackgroundLocationPermission(activity);
                    } else {
                        dialog.dismiss();
                        onBackgroundLocationGranted(activity);
                    }
                    return null;
                },
                dialog -> {
                    PermissionsApi.openAppSettings(activity);
                    return null;
                }
        );
    }

    private static void onBackgroundLocationGranted(Activity activity) {
        if (!PermissionsApi.isNotificationsPermissionGranted(activity)) {
            requestNotifications(activity);
        } else {
            onNotificationsGranted(activity);
        }
    }

    private static void requestNotifications(Activity activity) {
        showDialog(
                activity,
                "Allow Notifications permission",
                "The permission is required by Android OS to launch the service to track your location while the app is in the background \n\n" +
                        "Also the app will notify you about tracking status and any tracking issues.",
                dialog -> {
                    if (!PermissionsApi.isNotificationsPermissionGranted(activity)) {
                        PermissionsApi.requestNotificationsPermission(activity);
                    } else {
                        dialog.dismiss();
                        onNotificationsGranted(activity);
                    }
                    return null;
                },
                dialog -> {
                    PermissionsApi.openAppSettings(activity);
                    return null;
                }
        );
    }

    private static void onNotificationsGranted(Activity activity) {
        if (!PermissionsApi.isLocationServicesEnabled(activity)) {
            requestLocationServices(activity);
        } else {
            onLocationServicesEnabled(activity);
        }
    }

    private static void requestLocationServices(Activity activity) {
        showDialog(
                activity,
                "Please enable Location Services",
                "This app collects activity and location data \n to  manage your work on the move \n even when the app is closed or not in use\n\n" +
                        "We use this data to:\n" +
                        "- Show your movement history\n" +
                        "- Optimize routes\n" +
                        "- Mark places you visit\n" +
                        "- Make expense claims easier\n" +
                        "- Compute daily stats \n\t\t(like total driven distance)",
                dialog -> {
                    if (!PermissionsApi.isLocationServicesEnabled(activity)) {
                        PermissionsApi.openLocationServicesSettings(activity);
                    } else {
                        dialog.dismiss();
                        onLocationServicesEnabled(activity);
                    }
                    return null;
                },
                dialog -> {
                    PermissionsApi.openLocationServicesSettings(activity);
                    return null;
                }
        );
    }

    private static void onLocationServicesEnabled(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("All permissions granted");
        builder.setMessage("You have granted all the permissions required for the app to work properly");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
