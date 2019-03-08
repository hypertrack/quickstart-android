
# HyperTrack Quickstart for Android SDK
![License](https://img.shields.io/github/license/hypertrack/quickstart-android.svg)

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
![Android-Studio-quickstart-android](https://user-images.githubusercontent.com/10487613/53929273-0eb0d580-4042-11e9-9736-51fbb7945bfd.png)

#### Step 2. Set your Publishable key

1. Add the publishable key to [MyApplication](https://github.com/hypertrack/quickstart-android/blob/42ccbfc62cc06c049e695d7c8c6fcf4c46f214eb/app/src/main/java/com/hypertrack/quickstart/MyApplication.java#L16) file.

2. Run project on your device use simulator instance.

3. Go through one-time permission flow (applicable for Android M and later).

![run](https://user-images.githubusercontent.com/10487613/53847992-9ece1c80-3f67-11e9-8969-339484ed232c.png)


#### Step 3. Check your location on the HyperTrack [dashboard](https://v3.dashboard.hypertrack.com/devices)

## Integrate the SDK
 - [Add Hypertrack SDK](#step-1-add-hypertrack-sdk)
 - [Initialize SDK](#step-2-initialize-sdk)
 - [Ask for permission](#step-3-location-data-access-permission)
 - [Cleanup resources](#step-4-cleanup-resources)
 - [Manage tracking](#step-5-manage-tracking-state)

#### Step 1. Add Hypertrack SDK
Add following lines to your applications `build.gradle`:
```
// Import the SDK within your repositories block
repositories {
    maven { url 'http://hypertrack-core-android.s3-website-us-east-1.amazonaws.com/' }
    ...
}

//Add HyperTrack as a dependency
dependencies {
    implementation("com.hypertrack:hypertrack:3.0.0-SNAPSHOT@aar"){
        transitive = true;
    }
    ...
}
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
Depending on your needs, you can always _pause_ and _resume_ tracking, invoking `HyperTrack.pauseTracking()` and `HyperTrack.resumeTracking()` SDK methods.

#### Step 6. (optional) Customize foreground service notification
HyperTrack tracking runs as a separate foreground service, so when tracking is on you'll see a notification icon at the top of the screen.
By default it will display your app icon with text `{app name} is running` but you can customize it anytime after initialization by calling
```java
HyperTrack.addNotificationIconsAndTitle(
    R.drawable.ic_small,
    R.drawable.ic_large,
    notificationTitleText,
    notificationBodyText
);
```

#### Step 7. (optional) Set device metadata
All of the tracking devices are uniquely identified by per-device set [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier). Sometimes it is more convenient to tag device with specific name, that will make it easier to choose it at dashboard.
```java
HyperTrack.setNameAndMetadataForDevice(name, metaData);
```

#### You are all set

You can run the app and start using HyperTrack. You can see your devices on the [dashboard](#dashboard).

## Dashboard

Once your app is running, go to the [Dashboard page](https://v3.dashboard.hypertrack.com/devices) where you can see a list of all your devices and their location on the map.

![Dashboard](https://user-images.githubusercontent.com/10487613/53848754-6bd95800-3f6a-11e9-8464-580f791f3eea.png)

## Frequently Asked Questions
- [What API levels (Android versions) are supported](#supported-versions)
- [NoClassDefFoundError](#javalangnoclassdeffounderror)
- [Handling dependency conflicts](#dependencies)


#### Supported versions
Currently we do support all of the Android versions starting from API 19 (Android 4.4 Kit Kat)

#### java.lang.NoClassDefFoundError
I've added SDK and my app started failing with message like `Fatal Exception: java.lang.NoClassDefFoundError`.
The reason of it, is that on Android API level 19 and below you cannot have more than 65536 methods in your app (including libraries methods). Please, check [this Stackoverflow](https://stackoverflow.com/questions/34997835/fatal-exception-java-lang-noclassdeffounderror-when-calling-static-method-in-an) answer for solutions.

#### Dependencies
SDK dependencies graph looks like below:

    +--- com.android.volley:volley:1.1.0
    +--- com.google.code.gson:gson:2.8.5
    +--- org.greenrobot:eventbus:3.1.1
    +--- com.parse.bolts:bolts-tasks:1.4.0
    +--- net.grandcentrix.tray:tray:0.12.0
    |    \--- com.android.support:support-annotations:26.0.1
    \--- com.google.android.gms:play-services-location:16.0.0
         +--- com.google.android.gms:play-services-base:16.0.1
         |    +--- com.google.android.gms:play-services-basement:16.0.1
         |    |    \--- com.android.support:support-v4:26.1.0
         |    |         +--- com.android.support:support-compat:26.1.0
         |    |         +--- com.android.support:support-media-compat:26.1.0
         |    |         |    +--- com.android.support:support-annotations:26.1.0
         |    |         |    \--- com.android.support:support-compat:26.1.0
         |    |         +--- com.android.support:support-core-utils:26.1.0
         |    |         +--- com.android.support:support-core-ui:26.1.0
         |    |         \--- com.android.support:support-fragment:26.1.0
         |    \--- com.google.android.gms:play-services-tasks:16.0.1
         |         \--- com.google.android.gms:play-services-basement:16.0.1
         +--- com.google.android.gms:play-services-basement:16.0.1
         +--- com.google.android.gms:play-services-places-placereport:16.0.0
         |    \--- com.google.android.gms:play-services-basement:16.0.1
         \--- com.google.android.gms:play-services-tasks:16.0.1

Common problem here is depending on different versions of `com.android.support` library components. You can explicitly specify required version by adding it as a dependency in your app's `build.gradle`, e.g.:
```
  implementation `com.android.support:support-media-compat:26.1.0`
```
That will take precedence over SDK version and you'll have one version of support library on your classpath.

## Support
Join our [Slack community](http://slack.hypertrack.com) for instant responses. You can also email us at help@hypertrack.com.
