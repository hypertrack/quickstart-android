package com.hypertrack.quickstart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;

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
    }

    // Click Listener for AssignAction Button
    private View.OnClickListener logoutButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Toast.makeText(MainActivity.this, R.string.main_logout_success_msg,
                    Toast.LENGTH_SHORT).show();

            // Stop HyperTrack SDK
            // Refer here for more detail
            // https://docs.hypertrack.com/sdks/android/reference/hypertrack.html#void-stoptracking
            HyperTrack.completeAction(getActionId());
            clearUser();

            // Proceed to LoginActivity for a fresh User Login
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    };

    public  void clearUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(HT_QUICK_START_SHARED_PREFS_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private String getActionId() {
        SharedPreferences sharedPreferences = getSharedPreferences(HT_QUICK_START_SHARED_PREFS_KEY,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString("action_id", null);
    }
}
