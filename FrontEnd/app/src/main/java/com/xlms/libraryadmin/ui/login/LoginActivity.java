package com.xlms.libraryadmin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xlms.libraryadmin.R;
import com.xlms.libraryadmin.ui.admin.AdminDashboardActivity;
import com.xlms.libraryadmin.ui.client.ClientDashboardActivity;
import com.xlms.libraryadmin.ui.signup.SignUpActivity;

/**
 * Login Activity for XLMS Library Management System
 * Handles user authentication and role-based navigation
 * Features modern UI with hero aesthetic
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister, textViewForgotPassword;
    private ImageButton buttonTogglePassword;
    private View toggleRememberMe, switchThumb, switchBackground;
    private View emailAccentLine, passwordAccentLine;

    // State
    private boolean isPasswordVisible = false;
    private boolean isRememberMeChecked = false;

    // Dummy credentials for testing
    private static final String ADMIN_EMAIL = "admin@xlms.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String CLIENT_EMAIL = "user@xlms.com";
    private static final String CLIENT_PASSWORD = "user123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupFocusListeners();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        buttonTogglePassword = findViewById(R.id.buttonTogglePassword);
        toggleRememberMe = findViewById(R.id.toggleRememberMe);
        switchThumb = findViewById(R.id.switchThumb);
        switchBackground = findViewById(R.id.switchBackground);
        emailAccentLine = findViewById(R.id.emailAccentLine);
        passwordAccentLine = findViewById(R.id.passwordAccentLine);
    }

    private void setupFocusListeners() {
        // Email field focus listener
        editTextEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                emailAccentLine.setAlpha(1.0f);
            } else {
                emailAccentLine.setAlpha(0.0f);
            }
        });

        // Password field focus listener
        editTextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                passwordAccentLine.setAlpha(1.0f);
            } else {
                passwordAccentLine.setAlpha(0.0f);
            }
        });
    }

    private void setupClickListeners() {
        // Login button
        buttonLogin.setOnClickListener(v -> handleLogin());

        // Toggle password visibility
        buttonTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Remember me toggle
        toggleRememberMe.setOnClickListener(v -> toggleRememberMe());

        // Forgot password
        textViewForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Password reset coming soon...", Toast.LENGTH_SHORT).show();
        });

        // Register link
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            editTextPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            buttonTogglePassword.setColorFilter(ContextCompat.getColor(this, R.color.primary));
        } else {
            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            buttonTogglePassword.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
        }
        // Move cursor to end
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    private void toggleRememberMe() {
        isRememberMeChecked = !isRememberMeChecked;
        updateSwitchAppearance();
    }

    private void updateSwitchAppearance() {
        int dp2 = (int) (2 * getResources().getDisplayMetrics().density);
        int dp22 = (int) (22 * getResources().getDisplayMetrics().density);
        
        if (isRememberMeChecked) {
            // Move thumb to right and set ON state (blue track)
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) switchThumb.getLayoutParams();
            params.leftMargin = dp22;
            params.gravity = android.view.Gravity.CENTER_VERTICAL;
            switchThumb.setLayoutParams(params);
            switchBackground.setBackgroundResource(R.drawable.switch_track_checked);
        } else {
            // Move thumb to left and set OFF state (gray track)
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) switchThumb.getLayoutParams();
            params.leftMargin = dp2;
            params.gravity = android.view.Gravity.CENTER_VERTICAL;
            switchThumb.setLayoutParams(params);
            switchBackground.setBackgroundResource(R.drawable.switch_track_background);
        }
    }

    private void handleLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check credentials and navigate based on role
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            // Admin login
            navigateToAdminDashboard();
        } else if (email.equals(CLIENT_EMAIL) && password.equals(CLIENT_PASSWORD)) {
            // Client login
            navigateToClientDashboard();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToAdminDashboard() {
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        intent.putExtra("USER_ROLE", "ADMIN");
        intent.putExtra("USER_EMAIL", ADMIN_EMAIL);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void navigateToClientDashboard() {
        Intent intent = new Intent(LoginActivity.this, ClientDashboardActivity.class);
        intent.putExtra("USER_ROLE", "CLIENT");
        intent.putExtra("USER_EMAIL", CLIENT_EMAIL);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
