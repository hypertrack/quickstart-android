package com.hypertrack.quickstart;

import android.content.Context;

import androidx.annotation.NonNull;

public class PermissionManager {

    @NonNull private final Context mContext;

    private PermissionManager(@NonNull Context context) { mContext = context; }

    public static PermissionManager getInstance(@NonNull Context context) {
        return new PermissionManager(context);
    }

    public void requestPermissionsIfNecessary() {
        // TODO Denys:
    }
}
