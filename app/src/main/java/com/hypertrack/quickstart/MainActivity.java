package com.hypertrack.quickstart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;

import static com.hypertrack.quickstart.LoginActivity.HT_QUICK_START_SHARED_PREFS_KEY;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        initToolbar(getString(R.string.app_name));

        // Initialize UI Views
        initUIViews();
    }

    private void initUIViews() {
        // Initialize AssignAction Button
        Button logoutButton = (Button) findViewById(R.id.logout_btn);
        if (logoutButton != null) logoutButton.setOnClickListener(logoutButtonClickListener);
        final Button trackingToggle = (Button) findViewById(R.id.tracking_toggle);
        if (HyperTrack.isTracking())
            trackingToggle.setText(R.string.stop_tracking);
        trackingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HyperTrack.isTracking()) {
                    HyperTrack.stopTracking(new HyperTrackCallback() {
                        @Override
                        public void onSuccess(@NonNull SuccessResponse successResponse) {
                            trackingToggle.setText(R.string.start_tracking);

                        }

                        @Override
                        public void onError(@NonNull ErrorResponse errorResponse) {
                            Toast.makeText(MainActivity.this, errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    HyperTrack.startTracking(new HyperTrackCallback() {
                        @Override
                        public void onSuccess(@NonNull SuccessResponse successResponse) {
                            trackingToggle.setText(R.string.stop_tracking);

                        }

                        @Override
                        public void onError(@NonNull ErrorResponse errorResponse) {
                            Toast.makeText(MainActivity.this, errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // Click Listener for AssignAction Button
    private View.OnClickListener logoutButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Toast.makeText(MainActivity.this, R.string.main_logout_success_msg, Toast.LENGTH_SHORT).show();

            // Stop HyperTrack SDK
            // Refer here for more detail https://docs.hypertrack.com/sdks/android/reference/hypertrack.html#void-stoptracking
            HyperTrack.stopTracking();
            clearUser();

            // Proceed to LoginActivity for a fresh User Login
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    };

    public  void clearUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(HT_QUICK_START_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
