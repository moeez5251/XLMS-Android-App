package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xlms.librarymanagement.R;

/**
 * Client Dashboard - Main screen for library users/clients
 * Features: Browse Books, Search, Borrow/Return, View History
 */
public class ClientDashboardActivity extends AppCompatActivity {

    private TextView textViewWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);

        // Get user info from intent
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        
        initViews();
        setupData(userEmail);
    }

    private void initViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
    }

    private void setupData(String userEmail) {
        textViewWelcome.setText("Welcome!\n" + userEmail);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
