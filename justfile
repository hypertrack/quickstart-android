alias ogp := open-github-prs
alias us := update-sdk

SDK_NAME := "HyperTrack SDK Android"
REPOSITORY_NAME := "quickstart-android"

open-github-prs:
    open "https://github.com/hypertrack/{{REPOSITORY_NAME}}/pulls"

update-sdk android_version:
    git checkout -b update-sdk-{{android_version}}
    just _update-sdk-android-version-file {{android_version}}
    git add .
    git commit -m "Update {{SDK_NAME}} to {{android_version}}"
    just open-github-prs

_update-sdk-android-version-file android_version:
    ./scripts/update_file.sh quickstart-java/app/build.gradle "def hyperTrackVersion = \'.*\'" "def hyperTrackVersion = \'{{android_version}}\'"
    ./scripts/update_file.sh quickstart-kotlin/app/build.gradle "def hyperTrackVersion = \'.*\'" "def hyperTrackVersion = \'{{android_version}}\'"
