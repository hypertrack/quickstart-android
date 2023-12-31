package com.hypertrack.quickstart.sdk_androidkotlin

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hypertrack.quickstart.sdk_androidkotlin.ui.theme.SDKAndroidKotlinTheme
import com.hypertrack.sdk.android.HyperTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val isTrackingState = mutableStateOf(HyperTrack.isTracking)

    private val permissionsToBeRequestedInOrder = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        },
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        } else {
            null
        },
    )

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HyperTrack.subscribeToIsTracking { isTracking ->
            isTrackingState.value = isTracking
        }

        setContent {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val permissionActions = remember { mutableStateOf(emptyList<() -> Unit>()) }
            val openPermissionDialog = remember { mutableStateOf("") }

            if (openPermissionDialog.value.isNotEmpty()) {
                PermissionDialog(
                    permissionToBeRequested = openPermissionDialog.value,
                    requestPermissionAction = {
                        requestPermissionLauncher.launch(
                            openPermissionDialog.value
                        )
                    }
                )
            }

            if (permissionActions.value.isNotEmpty()) {
                permissionActions.value.forEach { action ->
                    action()
                }
            }

            SDKAndroidKotlinTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize(),
                ) { contentPadding ->
                    Column(
                        modifier = Modifier.padding(contentPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Welcome to HyperTrack SDK Android sample",
                            modifier = Modifier.padding(16.dp),
                        )
                        Text("DeviceID:")
                        Text(" ${HyperTrack.deviceID}")
                        Text(
                            text = "Is SDK tracking: ${HyperTrack.isTracking}",
                            modifier = Modifier.padding(16.dp),
                        )
                        Button(
                            onClick = {
                                requestAllPermissions(
                                    scope,
                                    snackbarHostState,
                                    permissionActions,
                                    openPermissionDialog
                                )
                            }) {
                            Text("Request permissions")
                        }
                        Button(
                            onClick = { HyperTrack.isTracking = isTrackingState.value.not() }) {
                            Text("Toggle tracking")
                        }
                    }
                }
            }
        }
    }

    /**
     * A method to ask for permissions, according to
     * https://developer.android.com/develop/ui/views/notifications/notification-permission
     *
     * This method iterates over `permissionsToBeRequestedInOrder` and if a permission check is needed,
     */
    private fun requestAllPermissions(
        scope: CoroutineScope,
        snackbarHostState: SnackbarHostState,
        permissionActions: MutableState<List<() -> Unit>>,
        openPermissionDialog: MutableState<String>
    ) {
        permissionsToBeRequestedInOrder.forEach { permissionToBeRequested ->
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    permissionToBeRequested
                ) == PackageManager.PERMISSION_GRANTED ->
                    scope.launch {
                        snackbarHostState.showSnackbar("Permission $permissionToBeRequested already granted")
                    }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permissionToBeRequested
                ) -> showPermissionRationale(
                    openPermissionDialog = openPermissionDialog,
                    permissionToBeRequested = permissionToBeRequested,
                )

                else -> requestPermissionLauncher.launch(
                    permissionToBeRequested
                )
            }

        }
    }

    private fun showPermissionRationale(
        openPermissionDialog: MutableState<String>,
        permissionToBeRequested: String
    ) {
        openPermissionDialog.value = permissionToBeRequested
    }
}

@Composable
fun PermissionDialog(permissionToBeRequested: String, requestPermissionAction: () -> Unit) {
    val openAlertDialog = remember { mutableStateOf(true) }

    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    requestPermissionAction()
                },
                dialogTitle = "Permission required",
                dialogText = "A $permissionToBeRequested permission is required for the app to track location.",
                icon = Icons.Default.Info
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
