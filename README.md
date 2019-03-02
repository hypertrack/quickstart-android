# Sample Android App using HyperTrack SDK

A sample Android application demonstrating the use of HyperTrack SDK.
 - [Quickstart app](#quickstart-app)
 - [FAQ](#Frequently-Asked-Questions)



## Quickstart
#### Step 1. Open this project in [Android Studio](https://developer.android.com/studio/index.html)
![Android-Studio-quickstart-android](tbd)

#### Step 2. Signup and get Publishable key.
1. Signup [here](https://v3.dashboard.hypertrack.com/signup).
2. Get `test` publishable key from [dashboard](https://v3.dashboard.hypertrack.com/account/keys) settings page.
3. Add the test publishable key to [MyApplication](https://github.com/hypertrack/quickstart-android/blob/master/app/src/main/java/com/hypertrack/quickstart/MyApplication.java) file.

```java
HyperTrack.initialize(this.getApplicationContext(), BuildConfig.HYPERTRACK_PK);
```

#### Step 3. FCM Integration
The SDK has a bi-directional communication model with the server. This enables the SDK to run on a variable frequency model, which balances the fine trade-off between low latency tracking and battery efficiency, and improve robustness. For this purpose, the Android SDK uses FCM or GCM silent notifications.

By default, project is configured with test `google-service.json` credentials.

For testing purpose, you need to add FCM Server Key `AAAAckZ1H20:APA91bEyilv0qgVyfSECb-jZxsgetGyKyJGVIavCOLhWn5GdI0aQBz76dPKAf5P73fVBE7OXoS5QicAV5ASrmcyhizGnNbD0DhwJPVSZaLKQrRGYH3Bam-7WGe3OEX_Chhf7CEPToVw0` on HyperTrack [dashboard settings page](https://dashboard.hypertrack.com/settings).

**Note:**
But if you want to use your FCM configuration or moving to production then replace `FCM Server Key` on HyperTrack [dashboard settings page](https://dashboard.hypertrack.com/settings) with yours FCM server key and replace
[google-service.json](https://github.com/hypertrack/quickstart-android/blob/master/app/google-services.json) .

## Usage

#### Step 1. Location Permission and Location Setting.
Ask for `location permission` and `location services`. Refer [here](https://docs.hypertrack.com/sdks/android/reference/hypertrack.html#boolean-checklocationpermission) for more detail.
```java
// Check for Location permission
if (!HyperTrack.checkLocationPermission(this)) {
    HyperTrack.requestPermissions(this);
    return;
}

// Check for Location settings
if (!HyperTrack.checkLocationServices(this)) {
    HyperTrack.requestLocationServices(this);
}
```

#### Step 2. Create HyperTrack User
The next thing that you need to do is to create a HyperTrack user. More details about the function [here](https://docs.hypertrack.com/sdks/android/reference/user.html#getorcreate-user).

When the user is created, we need to start tracking his location and activity.

```java
// Get User details, if specified
final String name = nameText.getText().toString();
final String phoneNumber = phoneNumberText.getText().toString();
final String uniqueId = !HTTextUtils.isEmpty(uniqueId.getText().toString()) ?
        uniqueId.getText().toString() : phoneNumber;

UserParams userParams = new UserParams().setName(name).setPhone(phoneNumber).setUniqueId(uniqueId);
/**
 * Get or Create a User for given uniqueId on HyperTrack Server here to
 * login your user & configure HyperTrack SDK with this generated
 * HyperTrack UserId.
 * OR
 * Implement your API call for User Login and get back a HyperTrack
 * UserId from your API Server to be configured in the HyperTrack SDK.
 */
HyperTrack.getOrCreateUser(userParams, new HyperTrackCallback() {
    @Override
    public void onSuccess(@NonNull SuccessResponse successResponse) {

        User user = (User) successResponse.getResponseObject();
        String userId = user.getId();
        // Handle createUser success here, if required
        // HyperTrack SDK auto-configures UserId on createUser API call,
        // so no need to call HyperTrack.setUserId() API

        // On UserLogin success
        onUserLoginSuccess();
    }

    @Override
    public void onError(@NonNull ErrorResponse errorResponse) {

        Toast.makeText(LoginActivity.this, R.string.login_error_msg + " " + errorResponse
                .getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

#### STEP 3. Create Action to start tracking
```java
 private void onUserLoginSuccess() {

        //Refer here for more detail
        // https://docs.hypertrack.com/sdks/android/reference/action.html#create-and-assign-action
        ActionParamsBuilder actionParamsBuilder = new ActionParamsBuilder();
        actionParamsBuilder.setType(Action.TYPE_VISIT);
        actionParamsBuilder.setExpectedPlace(new Place().setAddress("HyperTrack").setCountry("India"));
        HyperTrack.createAction(actionParamsBuilder.build(), new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                Action action = (Action) response.getResponseObject();
                saveAction(action);
                Intent mainActivityIntent = new Intent(LoginActivity.this,
                        MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                finish();
                Log.d(TAG, "onSuccess:  Action Created");
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                Log.e(TAG, "onError:  Action Creation Failed: " + errorResponse.getErrorMessage());
            }
        });
    }
```

#### Step 4. Complete Action to stop tracking
When user logs out call `HyperTracking.completeAction(actionId)` to complete the action. Refer [here](https://docs.hypertrack.com/sdks/android/reference/action.html#complete-action) for more detail.

```java
HyperTrack.completeAction(actionId);
```

## Testing (Mocking user Location)
A user’s tracking session starts with HyperTrack.createMockAction() and ends with HyperTrack.completeMockAction(). In order to mock user movement developers would call `HyperTrack.createMockAction()` and `HyperTrack.completeMockAction()` respectively.

[`HyperTrack.createMockAction()`]() API starts a simulation from the device's current location to a nearby place of interest within a 5-10km radius so the session is long enough to test your app & its features.

Developer can simulate user's location to a particular destination location from a given source location.
```
public static void createMockAction(ActionParams actionParams, @NonNull LatLng sourceLatLng, @NonNull LatLng destinationLatLng, @Nullable HyperTrackCallback callback);
```

## Documentation
For detailed documentation of the APIs, customizations and what all you can build using HyperTrack, please visit the official [docs](https://docs.hypertrack.com/).

## Contribute
Feel free to clone, use, and contribute back via [pull requests](https://help.github.com/articles/about-pull-requests/). We'd love to see your pull requests - send them in! Please use the [issues tracker](https://github.com/hypertrack/quickstart-android/issues) to raise bug reports and feature requests.

We are excited to see what live location feature you build in your app using this project. Do ping us at help@hypertrack.io once you build one, and we would love to feature your app on our blog!

## Support
Join our [Slack community](http://slack.hypertrack.com) for instant responses, or interact with our growing [community](https://community.hypertrack.com). You can also email us at help@hypertrack.com.
