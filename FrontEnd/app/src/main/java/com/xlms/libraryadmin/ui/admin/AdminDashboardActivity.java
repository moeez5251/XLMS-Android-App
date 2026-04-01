package com.xlms.libraryadmin.ui.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xlms.libraryadmin.R;

/**
 * Admin Dashboard - Main screen for admin users
 * Features: Add/Edit/Delete Books, Manage Users, View Transactions
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private TextView textViewWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Get user info from intent
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        
        initViews();
        setupData(userEmail);
    }

    private void initViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
    }

    private void setupData(String userEmail) {
        textViewWelcome.setText("Welcome, Admin!\n" + userEmail);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
