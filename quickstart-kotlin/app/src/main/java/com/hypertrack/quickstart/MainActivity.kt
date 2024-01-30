package com.hypertrack.quickstart

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hypertrack.quickstart.android.kotlin.R
import com.hypertrack.sdk.android.HyperTrack
import com.hypertrack.sdk.android.Json
import com.hypertrack.sdk.android.Result
import java.util.Random

/** @noinspection FieldCanBeLocal
 */
class MainActivity : AppCompatActivity() {

    private var errorsSubscription: HyperTrack.Cancellable? = null
    private var isAvailableSubscription: HyperTrack.Cancellable? = null
    private var isTrackingSubscription: HyperTrack.Cancellable? = null
    private var locationSubscription: HyperTrack.Cancellable? = null
    private var locateSubscription: HyperTrack.Cancellable? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tvDeviceId = findViewById<TextView>(R.id.tvDeviceId)
        val tvLocation = findViewById<TextView>(R.id.tvLocation)
        val tvErrors = findViewById<TextView>(R.id.tvErrors)
        val tvIsTracking = findViewById<TextView>(R.id.tvIsTracking)
        val tvIsAvailable = findViewById<TextView>(R.id.tvIsAvailable)

        val btnStartTracking = findViewById<Button>(R.id.btnStartTracking)
        val btnStopTracking = findViewById<Button>(R.id.btnStopTracking)
        val btnSetAvailable = findViewById<Button>(R.id.btnSetAvailable)
        val btnSetUnavailable = findViewById<Button>(R.id.btnSetUnavailable)
        val btnAddGeotag = findViewById<Button>(R.id.btnAddGeotag)
        val btnAddGeotagWithExpectedLocation =
            findViewById<Button>(R.id.btnAddGeotagWithExpectedLocation)
        val btnLocate = findViewById<Button>(R.id.btnLocate)
        val btnGetErrors = findViewById<Button>(R.id.btnGetErrors)
        val btnGetIsAvailable = findViewById<Button>(R.id.btnGetIsAvailable)
        val btnGetIsTracking = findViewById<Button>(R.id.btnGetIsTracking)
        val btnGetLocation = findViewById<Button>(R.id.btnGetLocation)
        val btnGetMetadata = findViewById<Button>(R.id.btnGetMetadata)
        val btnGetName = findViewById<Button>(R.id.btnGetName)

        // Get Device ID
        val deviceId = HyperTrack.deviceID
        Log.d(TAG, "Device ID: $deviceId")
        tvDeviceId.text = deviceId

        // Set Name
        val name = "Quickstart Android Kotlin"
        HyperTrack.name = name
        Log.d(TAG, "Name set to: $name")

        // Set Metadata
        val metadata: MutableMap<String, Any?> = HashMap()
        // `driver_handle` is used to link the device and the driver.
        // You can use any unique user identifier here.
        // The recommended way is to set it on app login in set it to null on logout
        // (to remove the link between the device and the driver)
        metadata["driver_handle"] = "test_driver_quickstart_android_kotlin"
        // You can also add any custom data to the metadata.
        metadata["source"] = name
        metadata["employee_id"] = Random().nextInt(10000)

        // We convert metadata to custom Json object to make sure that the data is Json compatible
        // If it can't be converted to Json, the result will be null
        val metadataJson = Json.fromMap(metadata)
            ?: throw RuntimeException("Metadata can't be converted to Json")
        // don't forget to check it and add proper error handling
        HyperTrack.metadata = metadataJson
        Log.d(TAG, "Metadata set to: $metadata")
        tvDeviceId.setOnClickListener { _ ->
            // copy device id to clipboard
            val clipboard =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Device ID", deviceId)
            clipboard.setPrimaryClip(clip)
        }
        btnStartTracking.setOnClickListener {
            HyperTrack.isTracking = true
        }
        btnStopTracking.setOnClickListener {
            HyperTrack.isTracking = false
        }
        btnSetAvailable.setOnClickListener {
            HyperTrack.isAvailable = true
        }
        btnSetUnavailable.setOnClickListener {
            HyperTrack.isAvailable = false
        }
        btnAddGeotag.setOnClickListener {
            // Create geotag payload
            val geotagPayload: MutableMap<String, Any?> =
                HashMap()
            /*
              geotagPayload is an arbitrary object.
              You can put there any JSON-serializable data.
              It will be displayed in the HyperTrack dashboard and
              available in the webhook events.
             */
            geotagPayload["payload"] = "Quickstart AndroidJava"
            geotagPayload["value"] = Random().nextDouble()

            // We convert geotagPayload to custom Json object to make sure that the data is Json compatible
            // If it can't be converted to Json, the result will be null
            // don't forget to check it and add proper error handling
            val geotagPayloadJson = Json.fromMap(geotagPayload)
                ?: throw RuntimeException("Geotag payload can't be converted to Json")

            // Add geotag
            val geotagResult: Result<HyperTrack.Location, HyperTrack.LocationError> =
                HyperTrack.addGeotag(geotagPayloadJson)
            showDialog("Geotag result", getLocationResultText(geotagResult))
        }
        btnAddGeotagWithExpectedLocation.setOnClickListener {
            // Create geotag payload
            val geotagPayload: MutableMap<String, Any?> =
                HashMap()
            /*
              geotagPayload is an arbitrary object.
              You can put there any JSON-serializable data.
              It will be displayed in the HyperTrack dashboard and
              available in the webhook events.
             */
            geotagPayload["payload"] = "Quickstart AndroidJava"
            geotagPayload["value"] = Random().nextDouble()

            // We convert geotagPayload to custom Json object to make sure that the data is Json compatible
            // If it can't be converted to Json, the result will be null
            val geotagPayloadJson = Json.fromMap(geotagPayload)
                ?: throw RuntimeException("Geotag payload can't be converted to Json")
            // don't forget to check it and add proper error handling

            // Create expected location
            val expectedLocation = HyperTrack.Location(
                37.775,
                -122.4183
            )

            // Add geotag
            val geotagResult: Result<HyperTrack.LocationWithDeviation, HyperTrack.LocationError> =
                HyperTrack.addGeotag(geotagPayloadJson, expectedLocation)
            showDialog(
                "Geotag with expected location result",
                getLocationWithDeviationResultText(geotagResult)
            )
        }
        btnLocate.setOnClickListener(View.OnClickListener { v: View? ->
            // you can use subscription object to unsubscribe from the locate result
            // with locateSubscription.cancel()
            locateSubscription =
                HyperTrack.locate { locateResult: Result<HyperTrack.Location, Set<HyperTrack.Error>> ->
                    showDialog("Locate result", getLocateResultText(locateResult))
                    null
                }
        })
        btnGetErrors.setOnClickListener {
            showDialog(
                "Errors",
                getErrorsText(HyperTrack.errors)
            )
        }
        btnGetIsAvailable.setOnClickListener {
            showDialog(
                "Get Is available",
                "isAvailable: ${HyperTrack.isAvailable}"
            )
        }
        btnGetIsTracking.setOnClickListener {
            showDialog(
                "Get Is tracking",
                "isTracking: ${HyperTrack.isTracking}"
            )
        }
        btnGetLocation.setOnClickListener {
            showDialog(
                "Get location",
                getLocationResultText(HyperTrack.location)
            )
        }
        btnGetMetadata.setOnClickListener {
            showDialog(
                "Get metadata",
                "metadata: $metadata"
            )
        }
        btnGetName.setOnClickListener {
            showDialog(
                "Get name",
                "name: $name"
            )
        }
        errorsSubscription = HyperTrack.subscribeToErrors { errors: Set<HyperTrack.Error> ->
            tvErrors.text = getErrorsText(errors)
        }
        isAvailableSubscription = HyperTrack.subscribeToIsAvailable { isAvailable: Boolean ->
            tvIsAvailable.text = "Is available: $isAvailable"
        }
        isTrackingSubscription = HyperTrack.subscribeToIsTracking { isTracking: Boolean ->
            tvIsTracking.text = "Is tracking: $isTracking"
        }
        locationSubscription =
            HyperTrack.subscribeToLocation { locationResult: Result<HyperTrack.Location, HyperTrack.LocationError> ->
                tvLocation.text = getLocationResultText(locationResult)
            }
    }

    private fun getErrorsText(errors: Set<HyperTrack.Error>): String {
        return errors.joinToString(",\n") {
            when (it) {
                HyperTrack.Error.BlockedFromRunning -> "BlockedFromRunning"
                HyperTrack.Error.InvalidPublishableKey -> "InvalidPublishableKey"
                HyperTrack.Error.Location.Mocked -> "Location.Mocked"
                HyperTrack.Error.Location.ServicesDisabled -> "Location.ServicesDisabled"
                HyperTrack.Error.Location.ServicesUnavailable -> "Location.ServicesUnavailable"
                HyperTrack.Error.Location.SignalLost -> "Location.SignalLost"
                HyperTrack.Error.NoExemptionFromBackgroundStartRestrictions -> "NoExemptionFromBackgroundStartRestrictions"
                HyperTrack.Error.Permissions.Location.Denied -> "Permissions.Location.Denied"
                HyperTrack.Error.Permissions.Location.InsufficientForBackground -> "Permissions.Location.InsufficientForBackground"
                HyperTrack.Error.Permissions.Location.ReducedAccuracy -> "Permissions.Location.ReducedAccuracy"
                HyperTrack.Error.Permissions.Notifications.Denied -> "Permissions.Notifications.Denied"
            }
        }.let { "[\n$it\n]" }
    }

    private fun getLocationResultText(
        locationResult: Result<HyperTrack.Location, HyperTrack.LocationError>
    ): String {
        return when (locationResult) {
            is Result.Success -> {
                locationResult.success.toString()
            }

            is Result.Failure -> {
                when (val error = locationResult.failure) {
                    is HyperTrack.LocationError.Errors -> {
                        getErrorsText(error.errors)
                    }

                    is HyperTrack.LocationError.NotRunning -> {
                        "Not running"
                    }

                    is HyperTrack.LocationError.Starting -> {
                        "Starting"
                    }
                }
            }
        }
    }

    private fun getLocationWithDeviationResultText(
        locationResult: Result<HyperTrack.LocationWithDeviation, HyperTrack.LocationError>
    ): String {
        return when (locationResult) {
            is Result.Success -> {
                locationResult.success.toString()
            }

            is Result.Failure -> {
                when (val error = locationResult.failure) {
                    is HyperTrack.LocationError.Errors -> {
                        getErrorsText(error.errors)
                    }

                    is HyperTrack.LocationError.NotRunning -> {
                        "Not running"
                    }

                    is HyperTrack.LocationError.Starting -> {
                        "Starting"
                    }
                }
            }
        }
    }

    private fun getLocateResultText(locationResult: Result<HyperTrack.Location, Set<HyperTrack.Error>>): String {
        return when (locationResult) {
            is Result.Success -> {
                locationResult.success.toString()
            }

            is Result.Failure -> {
                getErrorsText(locationResult.failure)
            }
        }
    }

    override fun onDestroy() {
        errorsSubscription?.cancel()
        isAvailableSubscription?.cancel()
        isTrackingSubscription?.cancel()
        locateSubscription?.cancel()
        locationSubscription?.cancel()
        super.onDestroy()
    }

    private fun showDialog(
        title: String,
        text: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(text)
        builder.setPositiveButton("Close", null)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener { v: View? -> dialog.dismiss() }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
