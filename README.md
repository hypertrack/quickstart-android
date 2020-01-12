
# HyperTrack Quickstart for Android SDK
![License](https://img.shields.io/github/license/hypertrack/quickstart-android.svg)

[HyperTrack](https://www.hypertrack.com) lets you add live location tracking to your mobile app. This repo contains an example client app that has everything you need to get started in minutes. This repo written in Java, checkout [Kotlin](https://github.com/hypertrack/quickstart-android-kotlin) quickstart if your feel more comfortable with it.

* [Publishable Key](#publishable-key)–Get your Publishable Key
* [Quickstart](#quickstart-app)–Start with a ready-to-go app
* [Integrate the SDK](#integrate-the-sdk)–Integrate the SDK into your app
* [Dashboard](#dashboard)–See all your devices' locations on HyperTrack Dashboard
* [FAQs](#frequently-asked-questions)–Frequently asked questions

## Publishable Key

We use Publishable Key to identify your devices. To get one:
1. Go to the [Signup page](https://dashboard.hypertrack.com/signup). Enter your email address and password.
2. Open the verification link sent to your inbox.
3. Open the [Keys page](https://dashboard.hypertrack.com/account/keys), where you can copy your Publishable Key.

![Keys page in dashboard](https://user-images.githubusercontent.com/10487613/56365755-45aa0780-61fa-11e9-858d-9a2e24ca31c9.png)

Next, you can [start with the Quickstart app](#quickstart-app), or can [integrate the SDK](#integrate-the-sdk) in your app.

## Quickstart app
#### Step 1. Open this project in [Android Studio](https://developer.android.com/studio/index.html)
![Android-Studio-quickstart-android](https://user-images.githubusercontent.com/10487613/56304771-81d06000-6147-11e9-8f9d-3eafd1eabd47.png)

#### Step 2. Set your Publishable key

1. Add the publishable key to [MyActivity](https://github.com/hypertrack/quickstart-android/tree/master/app/src/main/java/com/hypertrack/quickstart/MainActivity.java#L18) file.

2. Run project on your device use simulator instance.

3. Go through one-time permission flow (applicable for Android M and later).

![run](https://user-images.githubusercontent.com/10487613/56304774-81d06000-6147-11e9-94c4-386c7a56d869.png)


#### Step 3. Check your location on the HyperTrack [dashboard](https://dashboard.hypertrack.com/devices)

## Integrate the SDK
 - [Add Hypertrack SDK](#step-1-add-hypertrack-sdk)
 - [Start tracking](#step-2-initialize-sdk)
 - [Utility methods (optional)](#step-3-optional-utility-methods)

#### Step 1. Add Hypertrack SDK
Add following lines to your applications `build.gradle`:
```
// Import the SDK within your repositories block
repositories {
    maven {
        name 'hypertrack'
        url 'http://m2.hypertrack.com'
    }
    ...
}

//Add HyperTrack as a dependency
dependencies {
    implementation 'com.hypertrack:hypertrack:3.8.1'
    ...
}
```

#### Step 2. Start tracking.
Add SDK init call when you wan't to start tracking:
```java
    HyperTrack.getInstance(MyActivity.this, "your-publishable-key-here")
              .start();
```
SDK will prompt for permission, if necessary, pushing new activity activity on top of tasks stack and popping it, after permission request finishes.
That's it. You have implemented tracking.

#### Step 3. _(optional)_ Utility Methods
###### Turn tracking on and off
Depending on your needs, you can always _stop_ and _start_ tracking, invoking [`.stop()`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/HyperTrack.html#stop--) and [`start()`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/HyperTrack.html#start--) SDK methods.
It is recommended to store reference to SDK instance in order to use it for further actions. You can determine current sdk state using [`isRunning()`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/HyperTrack.html#isRunning--) call.

###### Add SDK state listener to catch events.
You can subscribe to SDK status changes [`addTrackingListener`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/HyperTrack.html#addTrackingListener-com.hypertrack.sdk.TrackingStateObserver.OnTrackingStateChangeListener-) and handle them in the appropriate methods [`onError(TrackingError)`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/TrackingStateObserver.OnTrackingStateChangeListener.html#onError-com.hypertrack.sdk.TrackingError-) [`onTrackingStart()`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/TrackingStateObserver.OnTrackingStateChangeListener.html#onTrackingStart--) [`onTrackingStop()`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/TrackingStateObserver.OnTrackingStateChangeListener.html#onTrackingStop--)

###### Customize foreground service notification
HyperTrack tracking runs as a separate foreground service, so when it is running, your users will see a persistent notification. By default, it displays your app icon with text `{app name} is running` but you can customize it anytime after initialization by calling:
```java
HyperTrack sdkInstance = HyperTrack.getInstance(context, publishableKey);
sdkInstance.setTrackingNotificationConfig(
                new ServiceNotificationConfig.Builder()
                        .setContentTitle("Tap to stop tracking")
                        .build()
        );
```
Check out other configurable properties in [ServiceNotificationConfig reference](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/ServiceNotificationConfig.html)

###### Identify devices
All devices tracked on HyperTrack are uniquely identified using [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier). You can get this identifier programmatically in your app by calling [`getDeviceID()`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/HyperTrack.html#getDeviceID--) after initialization.
Another approach is to tag device with a name that will make it easy to distinguish them on HyperTrack Dashboard.
```java
HyperTrack sdkInstance = HyperTrack.getInstance(context, publishableKey);

sdkInstance.setDeviceName(name);
```

###### Use trips

Create trips to track the device journey going from one place to another. Most of the time, you are not only interested in the latest location but a set of location-related data. For example how long you should expect someone to arrive, what distance someone traveled during his working day etc.
For such cases, you can scope location data at the required level of granularity by creating a trip, through submitting a request like below to HyperTrack backend.

You can create trip invoking appropriate SDK method:
```java
CreateTripRequest tripRequest = new CreateTripRequest.Builder()
        .setDestinationLongitude(-115.122993)
        .setDestinationLatitude(36.089361)
        .build();

AsyncResultHandler<ShareableTrip> resultHandler = new AsyncResultHandler<ShareableTrip>() {
    @Override
    public void onResultReceived(@NonNull ShareableTrip shareableTrip) {
        Log.d(TAG, "Created trip with id " + shareableTrip.getTripId()
                + ",\nthat can be viewed at: " + shareableTrip.getEmbedUrl()
                + ",\nthat can be shared with: " + shareableTrip.getShareableUrl());
    }

    @Override
    public void onFailure(@NonNull Error error) {
        Log.w(TAG, "Trip creation failed with error ", error);
    }
};

HyperTrack sdkInstance = HyperTrack.getInstance(context, publishableKey);
sdkInstance.createTrip(tripRequest, resultHandler);
```
Handler, that you've passed to `createTrip` as a second argument, receives [`ShareableTrip`](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/trip/ShareableTrip.html) object. Using URL obtained from `getEmbedUrl()` getter we can see device location with respect to its destination and other trip details.

![elvis-on-trip-to-paradise](https://user-images.githubusercontent.com/10487613/69039721-26c05d80-09f5-11ea-8047-2be04607dbb4.png)

When you need to finish a trip, you can do so from client via
```java
AsyncResultHandler<String> completionResultHandler = new AsyncResultHandler<String>() {
    @Override
    public void onResultReceived(@NonNull String s) {
        Log.d(TAG, "Trip completion succeded");
    }

    @Override
    public void onFailure(@NonNull Error error) {
        Log.w(TAG, "Trip completion failed", error);
    }
};

HyperTrack sdkInstance = HyperTrack.getInstance(context, publishableKey);
sdkInstance.completeTrip(shareableTrip.getTripId(), completionResultHandler);
```

After the trip has ended, you can access its aggregate data (entire route, time, etc.). You can review the trip using the replay feature in the web view mentioned above.

![trip-replay](https://user-images.githubusercontent.com/10487613/69040653-d1854b80-09f6-11ea-9fc3-4930d68667b1.gif)

Although you can use client-side trips management, someone might prefer to control them from backend. Check out [API Reference](https://docs.hypertrack.com/#references-apis-trips-post-trips) for request syntax and examples.

###### Create trip marker
Use this optional method if you want to associate data with specific place in your trip. E.g. user marking a task as done, user tapping a button to share location, user accepting an assigned job, device entering a geofence, etc.
```java
HyperTrack sdkInstance = HyperTrack.getInstance(context, publishableKey);

Map<String, Object> order = new HashMap<>();
order.put("item", "Martin D-18");
order.put("previousOwners", Collections.emptyList());
order.put("price", 7.75);

sdkInstance.addTripMarker(order);
```
Look into [documentation](https://hypertrack.github.io/sdk-android-hidden/javadoc/3.8.1/com/hypertrack/sdk/HyperTrack.html) for more details.

###### Enable server to device communication
Server to device communication uses firebase push notifications as transport for commands so for remote tracking state management Firebase integration is required. So you need to [setup Firebase Cloud Messaging](https://firebase.google.com/docs/android/setup), if you have no push notifications enabled so far. Next step is to specify `HyperTrackMessagingService` as push messages receiver by adding following snippet to your apps Android manifest:
```xml
...
  <service android:name="com.hypertrack.sdk.HyperTrackMessagingService" android:exported="false">
      <intent-filter>
          <action android:name="com.google.firebase.MESSAGING_EVENT" />
      </intent-filter>
  </service>
</application>
```
If you already use firebase push notifications you can extend `HyperTrackMessagingService` instead of Firebase, or declare two receivers side by side, if you wish.
Check out [Quickstart app with notifications integrated](https://github.com/hypertrack/quickstart-android/tree/push-integration-example) if you prefer to get a look at example.
Last step is to add your Firebase API key to [HyperTrack dashboard](https://dashboard.hypertrack.com/setup) under *Server to Device communication* section.
![FCM-key](https://user-images.githubusercontent.com/10487613/62698515-fc1e3c00-b9e5-11e9-913a-2ad3da9d573f.png)

#### You are all set

You can now run the app and start using HyperTrack. You can see your devices on the [dashboard](#dashboard).
Also you can find more about SDK integration [here](https://github.com/hypertrack/live-app-android).

## Dashboard

Once your app is running, go to the [Dashboard page](https://dashboard.hypertrack.com/devices) where you can see a list of all your devices and their location on the map.

![Dashboard](https://user-images.githubusercontent.com/10487613/56365147-d5e74d00-61f8-11e9-9214-629c26368d8c.png)

## Frequently Asked Questions
- [What API levels (Android versions) are supported](#supported-versions)
- [NoClassDefFoundError](#javalangnoclassdeffounderror)
- [Dependencies Conflicts](#dependencies)
- [Persistent notification](#persistent-notification)
- [Handling custom ROMs](#handling-custom-roms)
- [HyperTrack notification shows even after app is terminated](#hypertrack-notification-shows-even-after-my-app-is-terminated)
- [How tracking works in Doze mode](#how-tracking-service-works-in-android-doze-mode)
- [AAPT: error: attribute android:foregroundServiceType not found](#apps-that-target-api-below-29)


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
    |    \--- com.android.support:support-annotations:23.0.1 -> 28.0.0
    +--- com.google.android.gms:play-services-location:16.0.0
    |    +--- com.google.android.gms:play-services-base:16.0.1
    |    |    +--- com.google.android.gms:play-services-basement:16.0.1
    |    |    |    \--- com.android.support:support-v4:26.1.0
    |    |    |         +--- com.android.support:support-compat:26.1.0
    |    |    |         |    +--- com.android.support:support-annotations:26.1.0 -> 28.0.0
    |    |    |         |    \--- android.arch.lifecycle:runtime:1.0.0
    |    |    |         |         +--- android.arch.lifecycle:common:1.0.0
    |    |    |         |         \--- android.arch.core:common:1.0.0
    |    |    |         +--- com.android.support:support-media-compat:26.1.0
    |    |    |         |    +--- com.android.support:support-annotations:26.1.0 -> 28.0.0
    |    |    |         |    \--- com.android.support:support-compat:26.1.0 (*)
    |    |    |         +--- com.android.support:support-core-utils:26.1.0
    |    |    |         |    +--- com.android.support:support-annotations:26.1.0 -> 28.0.0
    |    |    |         |    \--- com.android.support:support-compat:26.1.0 (*)
    |    |    |         +--- com.android.support:support-core-ui:26.1.0
    |    |    |         |    +--- com.android.support:support-annotations:26.1.0 -> 28.0.0
    |    |    |         |    \--- com.android.support:support-compat:26.1.0 (*)
    |    |    |         \--- com.android.support:support-fragment:26.1.0
    |    |    |              +--- com.android.support:support-compat:26.1.0 (*)
    |    |    |              +--- com.android.support:support-core-ui:26.1.0 (*)
    |    |    |              \--- com.android.support:support-core-utils:26.1.0 (*)
    |    |    \--- com.google.android.gms:play-services-tasks:16.0.1
    |    |         \--- com.google.android.gms:play-services-basement:16.0.1 (*)
    |    +--- com.google.android.gms:play-services-basement:16.0.1 (*)
    |    +--- com.google.android.gms:play-services-places-placereport:16.0.0
    |    |    \--- com.google.android.gms:play-services-basement:16.0.1 (*)
    |    \--- com.google.android.gms:play-services-tasks:16.0.1 (*)
    \--- com.android.support:support-annotations:28.0.0

Common problem here is depending on different versions of `com.android.support` library components. You can explicitly specify required version by adding it as a dependency in your app's `build.gradle`, e.g.:
```
  implementation `com.android.support:support-v4:28.0.0`
```
That will take precedence over SDK version and you'll have one version of support library on your classpath.

#### Persistent notification
HyperTrack SDK, by default, runs as a foreground service. This is to ensure that the location tracking works reliably even when your app is minimized. A foreground service is a service that the user is actively aware of and isn't a candidate for the system to kill when low on memory.
Android mandates that a foreground service provides a persistent notification in the status bar. This means that the notification cannot be dismissed by the user.

![persistent-notification](https://user-images.githubusercontent.com/10487613/54007190-6ec47c00-4115-11e9-9743-332befbcf8f5.png)

#### Handling custom ROMs
Smartphones are getting more and more powerful, but the battery capacity is lagging behind. Device manufactures are always trying to squeeze some battery saving features into the firmware with each new Android release. Manufactures like Xiaomi, Huawei and OnePlus have their own battery savers that kills the services running in the background.
To avoid OS killing the service, users of your app need to override the automatic battery management and set it manual. To inform your users and direct them to the right setting page, you may add the following code in your app. This would intent out your user to the right settings page on the device.
```
try {
    Intent intent = new Intent();
    String manufacturer = android.os.Build.MANUFACTURER;
    if ("xiaomi".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
    }
    else if ("oppo".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
    }
    else if ("vivo".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
    }

    List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    if  (list.size() > 0) {
        context.startActivity(intent);
    }
    }
    catch (Exception e) {
        Crashlytics.logException(e);
}
```
You may also try out open source libraries like https://github.com/judemanutd/AutoStarter.

Some manufacturers don't allow to whitelist apps programmatically. In that case the only way to achieve service reliability is manual setup. E.g. for Oxygen OS (OnePlus) you need to select *Lock* menu item from app options button in _Recent Apps_ view:

![one-plus-example](https://user-images.githubusercontent.com/10487613/58070846-388a8a80-7ba3-11e9-8b4f-11e39d26382b.png)


#### HyperTrack notification shows even after my app is terminated
The HyperTrack service runs as a separate component and it is still running when the app that started it is terminated. That is why you can observe that notification. When you stop tracking (`stopTracking()`), the notification goes away.

#### How tracking service works in android doze mode
Doze mode requires device [to be stationary](https://developer.android.com/training/monitoring-device-state/doze-standby.html#understand_doze), so before OS starts imposing power management restrictions, exact device location is obtained. When device starts moving, Android leaves Doze mode and works regularly, so no special handling of Doze mode required with respect to location tracking.

#### Apps that target API below 29
If build fails with error like `AAPT: error: attribute android:foregroundServiceType not found` that means that you're targeting your app for Android P or earlier. Starting from Android 10 Google imposes additional restrictions on services, that access location data while phone screen is turned off. Possible workaround here is to use library version 3.7.0, that compiles against API 28 and below, but tracking service won't work properly on Android Q devices with screen been turned off or locked.

## Support
Join our [Slack community](https://join.slack.com/t/hypertracksupport/shared_invite/enQtNDA0MDYxMzY1MDMxLTdmNDQ1ZDA1MTQxOTU2NTgwZTNiMzUyZDk0OThlMmJkNmE0ZGI2NGY2ZGRhYjY0Yzc0NTJlZWY2ZmE5ZTA2NjI) for instant responses. You can also email us at help@hypertrack.com.
