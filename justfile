alias us := update-sdk

update-sdk android_version:
    git checkout -b update-sdk-{{android_version}}
    just _update-sdk-android-version-file {{android_version}}
    git add .
    git commit -m "Update HyperTrack SDK Android to {{android_version}}"


_update-sdk-android-version-file android_version:
    ./scripts/update_file.sh quickstart-java/app/build.gradle "def hyperTrackVersion = \'.*\'" "def hyperTrackVersion = \'{{android_version}}\'"
    ./scripts/update_file.sh quickstart-kotlin/app/build.gradle "def hyperTrackVersion = \'.*\'" "def hyperTrackVersion = \'{{android_version}}\'"
