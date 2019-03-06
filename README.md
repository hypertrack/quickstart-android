# HyperTrack Quickstart for Android SDK

A sample Android application demonstrating the use of HyperTrack SDK.
 - [Quickstart](#quickstart-app)
 - [Add to your project](#add-hypertrack-sdk-to-your-project)
 - [FAQ](#frequently-asked-questions)

## Quickstart app
#### Step 1. Open this project in [Android Studio](https://developer.android.com/studio/index.html)
![Android-Studio-quickstart-android](https://user-images.githubusercontent.com/10487613/53846914-9ecc1d80-3f63-11e9-80fe-07456c4ac0f8.png)

#### Step 2. Signup and get Publishable key.
1. Signup [here](https://v3.dashboard.hypertrack.com/signup).

2. Get `test` publishable key from [dashboard](https://v3.dashboard.hypertrack.com/account/keys) settings page.

![Dashoard Settings](https://user-images.githubusercontent.com/10487613/53847261-ccfe2d00-3f64-11e9-8883-6b9a626c4ce3.png)

3. Add the test publishable key to [MyApplication](https://github.com/hypertrack/quickstart-android/blob/42ccbfc62cc06c049e695d7c8c6fcf4c46f214eb/app/src/main/java/com/hypertrack/quickstart/MyApplication.java#L16) file.

4. Run project on your device use simulator instance.

4. Go through one-time permission flow (applicable for Android M and later).

![run](https://user-images.githubusercontent.com/10487613/53847992-9ece1c80-3f67-11e9-8969-339484ed232c.png)


#### Step 3. Check your location at [dashboard](https://v3.dashboard.hypertrack.com/devices)
![device-view](https://user-images.githubusercontent.com/10487613/53848754-6bd95800-3f6a-11e9-8464-580f791f3eea.png)


## Add Hypertrack SDK to your project
 - [Initialize SDK](#step-1-initialize-sdk)
 - [Ask for permission](#step-2-location-data-access-permission)
 - [Cleanup resources](#step-3-cleanup-resources)
 - [Manage tracking](#step-4-manage-tracking-state)

#### Step 1. Initialize SDK
Add SDK init call to your _Application's_ `onCreate()` callback:
```java
@Override
public void onCreate() {
    super.onCreate();
    HyperTrackCore.initialize(getApplicationContext(),getString(R.string.your_publishable_key));
}
```

#### Step 2. Location data access permission.
Ask for `location permission` when appropriate, passing _listener_ to receive callback.
```java
if (!HyperTrack.checkLocationPermission(this)) {
    HyperTrack.requestPermissions(mPermissionCallback);
    return;
}
```

#### Step 3. Cleanup resources
Add `HyperTrackCore.onStop()` call to your Application's `onTerminate()` callback
```java
@Override
public void onTerminate() {
    super.onTerminate();
    HyperTrackCore.onStop();
}
```

#### Step 4. Manage tracking state.
Depending on your needs, you can always _pause_ and _resume_ tracking, invoking `HyperTrackCore.pauseTracking()` and `HyperTrackCore.resumeTracking()` SDK methods.

#### Step 5. Customize foreground service notification
TBD

#### Step 6. Set device metadata
TBD

## Frequently Asked Questions
- [What API levels (Android versions) are supported](#supported-versions)
- [NoClassDefFoundError](#javalangnoclassdeffounderror)


#### Supported versions
Currently we do support all of the Android versions starting from API 17 (Android 4.2 Jelly Bean)

#### java.lang.NoClassDefFoundError
I've added SDK and my app started failing with message like `Fatal Exception: java.lang.NoClassDefFoundError`.
The reason of it, is that on Android API level 19 and below you cannot have more than 65536 methods in your app (including libraries methods). Please, check [this Starckoverflow](https://stackoverflow.com/questions/34997835/fatal-exception-java-lang-noclassdeffounderror-when-calling-static-method-in-an) answer for solutions.



## Support
Join our [Slack community](http://slack.hypertrack.com) for instant responses, or interact with our growing [community](https://community.hypertrack.com). You can also email us at help@hypertrack.com.
