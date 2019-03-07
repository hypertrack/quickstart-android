# HyperTrack Quickstart for Android SDK

[HyperTrack](https://www.hypertrack.com) lets you add live location tracking to your mobile app. This repo contains an example client app that has everything you need to get started in minutes.

* [Publishable Key](#publishable-key)–Get your Publishable Key
* [Quickstart](#quickstart-app)–Start with a ready-to-go app
* [Integrate the SDK](#integrate-the-sdk)–Integrate the SDK into your app
* [Dashboard](#dashboard)–See all your devices' locations on HyperTrack Dashboard
* [FAQs](#frequently-asked-questions)–Frequently asked questions

## Publishable Key

We use Publishable Key to identify your devices. To get one:
1. Go to the [Signup page](https://v3.dashboard.hypertrack.com/signup). Enter your email address and password.
2. Open the verification link sent to your inbox.
3. Open the [Keys page](https://v3.dashboard.hypertrack.com/account/keys), where you can copy your Publishable Key.

![Keys page in dashboard](https://user-images.githubusercontent.com/10487613/53847261-ccfe2d00-3f64-11e9-8883-6b9a626c4ce3.png)

Next, you can [start with the Quickstart app](#quickstart-app), or can [integrate the SDK](#integrate-the-sdk) in your app.

## Quickstart app
#### Step 1. Open this project in [Android Studio](https://developer.android.com/studio/index.html)
![Android-Studio-quickstart-android](https://user-images.githubusercontent.com/10487613/53846914-9ecc1d80-3f63-11e9-80fe-07456c4ac0f8.png)

#### Step 2. Set your Publishable key

1. Add the publishable key to [MyApplication](https://github.com/hypertrack/quickstart-android/blob/42ccbfc62cc06c049e695d7c8c6fcf4c46f214eb/app/src/main/java/com/hypertrack/quickstart/MyApplication.java#L16) file.

2. Run project on your device use simulator instance.

3. Go through one-time permission flow (applicable for Android M and later).

![run](https://user-images.githubusercontent.com/10487613/53847992-9ece1c80-3f67-11e9-8969-339484ed232c.png)


#### Step 3. Check your location on the HyperTrack [dashboard](https://v3.dashboard.hypertrack.com/devices)

## Integrate the SDK
 - [Add Hypertrack SDK dependency](#step-1-add-hypertrack-sdk-dependency)
 - [Initialize SDK](#step-2-initialize-sdk)
 - [Ask for permission](#step-3-location-data-access-permission)
 - [Cleanup resources](#step-4-cleanup-resources)
 - [Manage tracking](#step-5-manage-tracking-state)

#### Step 1. Add Hypertrack SDK dependency
Add following lines to your applications `build.gradle`:
```
repositories {
    maven {
        url 'http://hypertrack-core-android.s3-website-us-east-1.amazonaws.com/'
...
dependencies {
        implementation("com.hypertrack:sdk:3.0.0-SNAPSHOT@aar") {transitive = true}
```

#### Step 2. Initialize SDK
Add SDK init call to your _Application's_ `onCreate()` callback:
```java
@Override
public void onCreate() {
    super.onCreate();
    HyperTrack.initialize(getApplicationContext(),getString(R.string.your_publishable_key));
}
```

#### Step 3. Location data access permission.
Ask for `location permission` when appropriate, passing _listener_ to receive callback.
```java
if (!HyperTrack.checkLocationPermission(this)) {
    HyperTrack.requestPermissions(mPermissionCallback);
    return;
}
```

#### Step 4. Cleanup resources
Add `HyperTrack.onStop()` call to your Application's `onTerminate()` callback
```java
@Override
public void onTerminate() {
    super.onTerminate();
    HyperTrack.onStop();
}
```

#### Step 5. Manage tracking state.
Depending on your needs, you can always _pause_ and _resume_ tracking, invoking `HyperTrackCore.pauseTracking()` and `HyperTrackCore.resumeTracking()` SDK methods.

#### Step 6. Customize foreground service notification
TBD

#### Step 7. Set device metadata
TBD

#### You are all set

You can run the app and start using HyperTrack. You can see your devices on the [dashboard](#dashboard).

## Dashboard

Once your app is running, go to the [Dashboard page](https://v3.dashboard.hypertrack.com/devices) where you can see a list of all your devices and their location on the map.

![Dashboard](https://user-images.githubusercontent.com/10487613/53848754-6bd95800-3f6a-11e9-8464-580f791f3eea.png)

## Frequently Asked Questions
- [What API levels (Android versions) are supported](#supported-versions)
- [NoClassDefFoundError](#javalangnoclassdeffounderror)


#### Supported versions
Currently we do support all of the Android versions starting from API 19 (Android 4.4 Kit Kat)

#### java.lang.NoClassDefFoundError
I've added SDK and my app started failing with message like `Fatal Exception: java.lang.NoClassDefFoundError`.
The reason of it, is that on Android API level 19 and below you cannot have more than 65536 methods in your app (including libraries methods). Please, check [this Starckoverflow](https://stackoverflow.com/questions/34997835/fatal-exception-java-lang-noclassdeffounderror-when-calling-static-method-in-an) answer for solutions.



## Support
Join our [Slack community](http://slack.hypertrack.com) for instant responses. You can also email us at help@hypertrack.com.
