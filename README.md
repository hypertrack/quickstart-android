# HyperTrack Quickstart for Android SDK

![License](https://img.shields.io/github/license/hypertrack/quickstart-android.svg)

[HyperTrack](https://www.hypertrack.com) lets you add live location tracking to your mobile app. Live location is made available along with ongoing activity, tracking controls and tracking outage with reasons. This repo contains an example iOS app that has everything you need to get started in minutes.

* [Publishable Key](#publishable-key)–Sign up and get your keys
* [Quickstart](#quickstart-app)–Start with a ready-to-go app with reliable tracking service
* [Create a trip](#create-a-trip)-Create a trip using our REST API to start tracking location
* [Dashboard](#dashboard)–See live location of all your devices on your HyperTrack dashboard
* [Documentation](#documentation)-Refer to documentation for integration guides and API reference
* [Support](#support)–Support

## Publishable Key

We use Publishable Key to identify your devices. To get one:
1. Go to the [Signup page](https://dashboard.hypertrack.com/signup). Enter your email address and password.
2. Open the verification link sent to your email.
3. Open the [Setup page](https://dashboard.hypertrack.com/setup), where you can copy your Publishable Key.

## Quickstart app

1. [Clone this repo](#step-1-clone-this-repo)
3. [Set your Publishable Key](#step-3-set-your-publishable-key)
4. [Setup silent push notifications](#step-4-setup-silent-push-notifications)
5. [Run the Quickstart app](#step-5-run-the-quickstart-app)

### Step 1: Clone this repo
```bash
git clone https://github.com/hypertrack/quickstart-android.git
cd quickstart-android
```

### Step 2: Set your Publishable Key

Open the Quickstart project inside the workspace and set your [Publishable Key](#publishable-key) inside the placeholder in the [`MainActivity.java`](https://github.com/hypertrack/quickstart-android/blob/9491b1fc8d8a0d4af8339552257cfda5917bda27/quickstart-java/app/src/main/java/com/hypertrack/quickstart/MainActivity.java#L16)/[`MainActivity.kt`](https://github.com/hypertrack/quickstart-android/blob/9491b1fc8d8a0d4af8339552257cfda5917bda27/quickstart-kotlin/app/src/main/java/com/hypertrack/quickstart/MainActivity.kt#L89) file.

### Step 3: Setup silent push notifications

<details>
  <summary>Register quickstart app in firebase</summary>
  <br/>
  <p>1. Goto <a href="https://console.firebase.google.com/">Firebase Console</a> and create test project (or you can reuse existing one)</p>
  <img src="Images/add-app-to-test-project.png"/>
  <p>2. Register new Android application using <code>com.hypertrack.quickstart.android.github</code> package name.</p>
  <img src="Images/register-quickstart-app.png"/>
  <p>3. Download <code>google-services.json</code> file and copy it to <code>/quickstart-java/app</code> or <code>/quickstart-kotlin/app/</code> folder.</p>
  <img src="Images/download-google-services-json.png"/>

</details>
</p>
Log into the HyperTrack dashboard, and open the <a href="https://dashboard.hypertrack.com/setup#server_device_communication">setup page</a>. Fill FCM Key section in Android paragraph obtained from <i>Firebase Developer console > Project Settings (gear icon at top left) > Cloud Messaging tab.</i></p>
<img src="Images/copy-server-key.png"/>


### Step 4: Run the Quickstart app

Run the app on your phone and you should see the following interface:

![Quickstart app](Images/On_Device.png)

Grant location and activity permissions if prompted.

Next you can [create a trip](#create-a-trip) to start tracking using our [REST API](https://docs.hypertrack.com/#references-apis).

After the trip is created check out the [dashboard](#dashboard) to see the live location of your devices on the map.

## Create a trip

You can use our [Postman collection](https://www.getpostman.com/run-collection/a2318d122f1b88fae3c1) to create a trip using [HyperTrack REST API](https://docs.hypertrack.com/#references-apis-trips-post-trips) or use the following cURL request:

```curl
curl -u ACCOUNTID:SECRETKEY --location --request POST 'https://v3.api.hypertrack.com/trips/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "device_id": "DEVICEID",
    "destination": {
        "geometry": {
            "type": "Point",
            "coordinates": [LONGITUDE, LATITUDE]
        }
    }
}'
```

Substitute:
* `DEVICEID` for Device ID of your device (can be seen on the app itself or in logs)
* `ACCOUNTID` and `SECRETKEY` for values obtained in the [Setup page](https://dashboard.hypertrack.com/setup)'s API section.
* `LATITUDE` and `LONGITUDE` for real values of your destination

## Dashboard

Once your app is running, go to the [dashboard](https://dashboard.hypertrack.com/devices) where you can see a list of all your devices and their live location with ongoing activity on the map.

## Documentation

You can find our [integration guide](https://docs.hypertrack.com/#guides-sdks-android) and API reference on our [documentation website](https://docs.hypertrack.com/#references-sdks-android). There is also a full in-code reference for all SDK methods.

## Support
Join our [Slack community](https://join.slack.com/t/hypertracksupport/shared_invite/enQtNDA0MDYxMzY1MDMxLTdmNDQ1ZDA1MTQxOTU2NTgwZTNiMzUyZDk0OThlMmJkNmE0ZGI2NGY2ZGRhYjY0Yzc0NTJlZWY2ZmE5ZTA2NjI) for instant responses. You can also email us at help@hypertrack.com.
