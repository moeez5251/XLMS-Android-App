package com.xlms.libraryadmin.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xlms.libraryadmin.R;
import com.xlms.libraryadmin.ui.login.LoginActivity;

/**
 * Forgot Password Activity - Account Recovery
 * Allows users to request password reset instructions via email
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // UI Components
    private ImageButton buttonBack;
    private EditText editTextEmail;
    private Button buttonSendReset;
    private TextView textViewBackToLogin;
    private View emailAccentLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupFocusListeners();
        setupClickListeners();
    }

    private void initViews() {
        buttonBack = findViewById(R.id.buttonBack);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSendReset = findViewById(R.id.buttonSendReset);
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);
        emailAccentLine = findViewById(R.id.emailAccentLine);
    }

    private void setupFocusListeners() {
        // Email field focus listener for animated underline
        editTextEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                emailAccentLine.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                emailAccentLine.requestLayout();
            } else {
                emailAccentLine.getLayoutParams().width = 0;
                emailAccentLine.requestLayout();
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                // Clear error when user types
                editTextEmail.setError(null);
            }
        });
    }

    private void setupClickListeners() {
        // Back button
        buttonBack.setOnClickListener(v -> onBackPressed());

        // Send Reset Instructions button
        buttonSendReset.setOnClickListener(v -> handleSendReset());

        // Back to Login link
        textViewBackToLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void handleSendReset() {
        String email = editTextEmail.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            editTextEmail.setError("Please enter your email address");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        // Show success message
        Toast.makeText(
            this, 
            "Reset instructions sent to " + email, 
            Toast.LENGTH_LONG
        ).show();

        // Clear the field
        editTextEmail.setText("");

        // In production: Send reset email via backend API
        // For now, just show confirmation
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
