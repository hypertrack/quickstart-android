package com.hypertrack.quickstart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;

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
        if (logoutButton != null)
            logoutButton.setOnClickListener(logoutButtonClickListener);
    }

    // Click Listener for AssignAction Button
    private View.OnClickListener logoutButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, R.string.main_logout_success_msg,
                    Toast.LENGTH_SHORT).show();

            // Stop HyperTrack SDK
            HyperTrack.stopTracking();

            // Proceed to LoginActivity for a fresh User Login
            Intent loginIntent = new Intent(MainActivity.this,
                    LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    };
}
