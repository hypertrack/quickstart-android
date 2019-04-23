
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
1. Go to the [Signup page](https://dashboard.hypertrack.com/signup). Enter your email address and password.
2. Open the verification link sent to your inbox.
3. Open the [Keys page](https://dashboard.hypertrack.com/account/keys), where you can copy your Publishable Key.

![Keys page in dashboard](https://user-images.githubusercontent.com/10487613/56365755-45aa0780-61fa-11e9-858d-9a2e24ca31c9.png)

Next, you can [start with the Quickstart app](#quickstart-app), or can [integrate the SDK](#integrate-the-sdk) in your app.

## Quickstart app
#### Step 1. Open this project in [Android Studio](https://developer.android.com/studio/index.html)
![Android-Studio-quickstart-android](https://user-images.githubusercontent.com/10487613/56304771-81d06000-6147-11e9-8f9d-3eafd1eabd47.png)

#### Step 2. Set your Publishable key

1. Add the publishable key to [MyActivity](https://github.com/hypertrack/quickstart-android-hidden/blob/88f24d0e37c6ba75f6b6f5ad9ca711b4e0af1bfc/app/src/main/java/com/hypertrack/quickstart/MainActivity.java#L22) file.

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
    implementation("com.hypertrack:hypertrack:3.1.0@aar"){
        transitive = true;
    }
    ...
}
```

#### Step 2. Start tracking.
Add SDK init call when you wan't to start tracking:
```java
    HyperTrack.initialize(MyActivity.this, "your-publishable-key-here");
```
SDK will prompt for permission, if necessary, adding fragment on top of activity, that was passed in as a first argument.
That's it. You have implemented tracking.

#### Step 3. _(optional)_ Utility Methods
###### Turn tracking on and off
Depending on your needs, you can always _stop_ and _start_ tracking, invoking `HyperTrack.stopTracking()` and `HyperTrack.startTracking()` SDK methods.
Also, checkout [overloaded variants](http://hypertrack-javadoc.s3-website-us-west-2.amazonaws.com/com/hypertrack/sdk/HyperTrack.html#initialize-android.app.Activity-java.lang.String-com.hypertrack.sdk.TrackingInitDelegate-) of `initialize` methods for fine-grained control
on initialization, permission request and tracking start. You can determine current tracking state using `HyperTrack.isTracking()` call.

###### Customize foreground service notification
HyperTrack tracking runs as a separate foreground service, so when tracking is ON, your users will see a persistent notification. By default, it displays your app icon with text `{app name} is running` but you can customize it anytime after initialization by calling:
```java
HyperTrack.addNotificationIconsAndTitle(
    R.drawable.ic_small,
    R.drawable.ic_large,
    notificationTitleText,
    notificationBodyText
);
```

###### Identify devices
All devices tracked on HyperTrack are uniquely identified using [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier). You can get this identifier programmatically in your app by calling `HyperTrack.getDeviceId()` after initialization.
Another approach is to tag device with a name that will make it easy to distinguish them on HyperTrack Dashboard.
```java
HyperTrack.setNameAndMetadataForDevice(name, metaData);
```

#### You are all set

You can now run the app and start using HyperTrack. You can see your devices on the [dashboard](#dashboard).

## Dashboard

Once your app is running, go to the [Dashboard page](https://dashboard.hypertrack.com/devices) where you can see a list of all your devices and their location on the map.

![Dashboard](https://user-images.githubusercontent.com/10487613/56365147-d5e74d00-61f8-11e9-9214-629c26368d8c.png)

## Frequently Asked Questions
- [What API levels (Android versions) are supported](#supported-versions)
- [NoClassDefFoundError](#javalangnoclassdeffounderror)
- [Android X](#android-x)
- [Persistent notification](#persistent-notification)
- [Handling custom ROMs](#handling-custom-roms)


#### Supported versions
Currently we do support all of the Android versions starting from API 19 (Android 4.4 Kit Kat)

#### java.lang.NoClassDefFoundError
I've added SDK and my app started failing with message like `Fatal Exception: java.lang.NoClassDefFoundError`.
The reason of it, is that on Android API level 19 and below you cannot have more than 65536 methods in your app (including libraries methods). Please, check [this Stackoverflow](https://stackoverflow.com/questions/34997835/fatal-exception-java-lang-noclassdeffounderror-when-calling-static-method-in-an) answer for solutions.

#### Android X

Common problem here is depending on different versions of `com.android.support` library components. Our choice is to follow Google advice to migrate to [Android X](https://developer.android.com/jetpack/androidx). So in case, if you see message like `Failed resolution of: Landroidx/my/app/NotificationCompat$Builder` in logs, migrate your project to android x. It will have positive impact on overall app stability, even outside of HyperTrack.


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
You may also try out open source libraries like https://github.com/judemanutd/AutoStarter


## Support
Join our [Slack community](http://slack.hypertrack.com) for instant responses. You can also email us at help@hypertrack.com.
