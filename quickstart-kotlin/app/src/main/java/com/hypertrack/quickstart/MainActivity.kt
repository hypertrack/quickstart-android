package com.hypertrack.quickstart

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hypertrack.quickstart.sdk_androidkotlin.R
import com.hypertrack.sdk.android.HyperTrack


class MainActivity : AppCompatActivity() {

    private lateinit var trackingStatus: TextView
    private var isTrackingSubscription: HyperTrack.Cancellable? = null
    private var errorsSubscription: HyperTrack.Cancellable? = null

    /**
     * This list can contain any permissions you need EXCEPT for the background location permission
     */
    private val regularPermissionsToBeRequested: List<String> = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        },
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    /**
     * A special Background location permission configuration
     */
    private val backgroundLocationPermission: String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            null
        }

    /**
     * A special background location permission launcher
     * It can be run only if regular location permissions are granted
     */
    private val requestBackgroundPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean? ->
        if (areRegularPermissionsGranted() && isBackgroundLocationPermissionGranted()) {
            HyperTrack.isTracking = true
        } else {
            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            AlertDialog.Builder(this)
                .setTitle("Tracking feature disabled")
                .setMessage("Without Background Location permission the tracking is not accurate")
                .setPositiveButton(
                    "OK"
                ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .show()
        }
    }

    /**
     * Regular permissions launcher
     */
    private val requestRegularPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { _: Map<String, Boolean> ->
            if (areRegularPermissionsGranted()) {
                if (isBackgroundLocationPermissionGranted()) {
                    HyperTrack.isTracking = true
                } else {
                    showBackgroundLocationPermissionRationaleDialogOrAskPermissionDirectly()
                }
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                AlertDialog.Builder(this)
                    .setTitle("Tracking feature disabled")
                    .setMessage("Without Location and Notifications permissions the tracking is not available")
                    .setPositiveButton(
                        "OK"
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        trackingStatus = findViewById(R.id.statusLabel)
        val deviceId = findViewById<TextView>(R.id.deviceIdTextView)
        deviceId.text = HyperTrack.deviceID
        Log.d(TAG, "device id is ${HyperTrack.deviceID}")

        trackingStatus.setOnClickListener {
            if (HyperTrack.isTracking) {
                HyperTrack.isTracking = false
            } else {
                requestPermissionsAndStartTracking()
            }
        }

        subscribeToIsTrackingChanges()
        subscribeToErrors()
    }

    override fun onDestroy() {
        isTrackingSubscription?.cancel()
        errorsSubscription?.cancel()
        super.onDestroy()
    }

    /**
     * Subscribes to HyperTrack SDK Errors
     */
    private fun subscribeToErrors() {
        errorsSubscription = HyperTrack.subscribeToErrors(::onErrors)
    }

    /**
     * Subscribes to HyperTrack SDK Tracking Changes
     */
    private fun subscribeToIsTrackingChanges() {
        isTrackingSubscription = HyperTrack.subscribeToIsTracking {
            when (it) {
                true -> onTrackingStart()
                false -> onTrackingStop()
            }
        }
    }

    /**
     * Handles HyperTrack Errors
     * @param errors
     */
    private fun onErrors(errors: Set<HyperTrack.Error>) {
        Log.d(TAG, "onErrors ${errors.joinToString(", ")}")

        trackingStatus.text = errors.joinToString("\n") {
            when (it) {
                HyperTrack.Error.BlockedFromRunning -> resources.getText(R.string.cant_start_tracking)
                HyperTrack.Error.InvalidPublishableKey -> resources.getText(R.string.check_publishable_key)
                HyperTrack.Error.Location.Mocked -> resources.getText(R.string.enable_gps)
                HyperTrack.Error.Location.ServicesDisabled -> resources.getText(R.string.enable_gps)
                HyperTrack.Error.Location.ServicesUnavailable -> resources.getText(R.string.enable_gps)
                HyperTrack.Error.Location.SignalLost -> resources.getText(R.string.enable_gps)
                HyperTrack.Error.NoExemptionFromBackgroundStartRestrictions -> resources.getText(R.string.cant_start_tracking)
                HyperTrack.Error.Permissions.Location.Denied -> resources.getText(R.string.permissions_not_granted)
                HyperTrack.Error.Permissions.Location.InsufficientForBackground -> resources.getText(
                    R.string.permissions_not_granted
                )

                HyperTrack.Error.Permissions.Location.ReducedAccuracy -> resources.getText(R.string.permissions_not_granted)
                HyperTrack.Error.Permissions.Notifications.Denied -> resources.getText(R.string.permissions_not_granted)
            }
        }
        trackingStatus.setBackgroundColor(resources.getColor(R.color.colorLabelError))
    }

    private fun onTrackingStart() {
        Log.d(TAG, "onTrackingStart")
        trackingStatus.setBackgroundColor(resources.getColor(R.color.colorLabelActive))
        trackingStatus.text = resources.getText(R.string.tracking_active)
    }

    private fun onTrackingStop() {
        Log.d(TAG, "onTrackingStop")
        trackingStatus.setBackgroundColor(resources.getColor(R.color.colorLabelStopped))
        trackingStatus.text = resources.getText(R.string.tracking_stopped)
    }

    /**
     * Checks if regular permissions are granted
     *
     * @return true if all regular permissions are granted
     */
    private fun areRegularPermissionsGranted(): Boolean {
        var areRegularPermissionsGranted = true
        for (permission in regularPermissionsToBeRequested) {
            areRegularPermissionsGranted = areRegularPermissionsGranted &&
                    ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
        }
        return areRegularPermissionsGranted
    }

    /**
     * Checks if background location permission is granted
     *
     * @return true if permission is needed and granted or when permission is not needed
     */
    private fun isBackgroundLocationPermissionGranted(): Boolean {
        return if (backgroundLocationPermission != null) {
            ContextCompat.checkSelfPermission(
                this,
                backgroundLocationPermission
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Shows the regular permissions rationale dialog if needed.
     */
    private fun showRegularPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission required")
            .setMessage("Location and Notifications permissions are needed for proper tracking behavior")
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                askForRegularPermissions()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Shows the special background location permission rationale dialog if needed.
     */
    private fun showBackgroundLocationPermissionRationaleDialogOrAskPermissionDirectly() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                backgroundLocationPermission!!
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission required")
                .setMessage("Background location permission is needed for accurate tracking behavior")
                .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    askForBackgroundLocationPermission()
                }
                .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .show()
        } else {
            askForBackgroundLocationPermission()
        }
    }

    /**
     * Directly asks for regular permissions
     */
    private fun askForRegularPermissions() {
        requestRegularPermissionsLauncher.launch(regularPermissionsToBeRequested.toTypedArray())
    }

    /**
     * Asks for special background permission
     */
    private fun askForBackgroundLocationPermission() {
        requestBackgroundPermissionLauncher.launch(backgroundLocationPermission)
    }

    /**
     * Special method called when the user wants to start tracking.
     * It checks all the permissions and does all the necessary actions in proper order.
     */
    private fun requestPermissionsAndStartTracking() {
        if (areRegularPermissionsGranted() && isBackgroundLocationPermissionGranted()) {
            HyperTrack.isTracking = true
        } else if (!areRegularPermissionsGranted()) {
            var rationaleShown = false
            for (permission in regularPermissionsToBeRequested) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    rationaleShown = true
                    showRegularPermissionRationaleDialog()
                }
            }
            if (!rationaleShown) {
                askForRegularPermissions()
            }
        } else {
            showBackgroundLocationPermissionRationaleDialogOrAskPermissionDirectly()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
