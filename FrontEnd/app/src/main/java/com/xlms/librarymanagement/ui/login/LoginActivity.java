package com.xlms.librarymanagement.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.AuthUsersRequest;
import com.xlms.librarymanagement.api.AuthUsersResponse;
import com.xlms.librarymanagement.api.LoginRequest;
import com.xlms.librarymanagement.api.LoginResponse;
import com.xlms.librarymanagement.ui.admin.AdminDashboardActivity;
import com.xlms.librarymanagement.ui.auth.ForgotPasswordActivity;
import com.xlms.librarymanagement.ui.client.ClientDashboardActivity;
import com.xlms.librarymanagement.ui.signup.SignUpActivity;
import com.xlms.librarymanagement.utils.SessionManager;

import java.io.IOException;

/**
 * Login Activity for XLMS Library Management System
 * Handles user authentication and role-based navigation
 * Features modern UI with hero aesthetic
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoogleLogin;
    private TextView textViewRegister, textViewForgotPassword;
    private ImageButton buttonTogglePassword;
    private View emailAccentLine, passwordAccentLine;

    // State
    private boolean isPasswordVisible = false;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if already logged in
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            navigateBasedOnRole(sessionManager.getUserRole(), sessionManager.getUserEmail());
            return;
        }

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        setContentView(R.layout.activity_login);

        initViews();
        setupFocusListeners();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        buttonTogglePassword = findViewById(R.id.buttonTogglePassword);
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

        // Google login
        buttonGoogleLogin.setOnClickListener(v -> signInWithGoogle());

        // Toggle password visibility
        buttonTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Forgot password
        textViewForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        });

        // Register link
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        });
    }

    private void signInWithGoogle() {
        // Force sign out first to ensure account selection dialog is shown
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String name = account.getDisplayName();
            String email = account.getEmail();

            if (email != null && name != null) {
                performGoogleLogin(name, email);
            } else {
                Toast.makeText(this, "Could not retrieve user info from Google", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void performGoogleLogin(String name, String email) {
        buttonGoogleLogin.setEnabled(false);
        Toast.makeText(this, "Authenticating with Google...", Toast.LENGTH_SHORT).show();

        AuthUsersRequest request = new AuthUsersRequest(name, email);
        ApiClient.getApiService(this).authUsers(request).enqueue(new retrofit2.Callback<AuthUsersResponse>() {
            @Override
            public void onResponse(retrofit2.Call<AuthUsersResponse> call, retrofit2.Response<AuthUsersResponse> response) {
                buttonGoogleLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    AuthUsersResponse authResponse = response.body();
                    saveGoogleSessionAndNavigate(email, authResponse.getUserId(), authResponse.getRole(), name, authResponse.getToken());
                } else {
                    String errorMessage = "Authentication failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            com.google.gson.JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("message")) {
                                errorMessage = errorObj.get("message").getAsString();
                            } else if (errorObj.has("error")) {
                                errorMessage = errorObj.get("error").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<AuthUsersResponse> call, Throwable t) {
                buttonGoogleLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveGoogleSessionAndNavigate(String email, String userId, String role, String name, String token) {
        SessionManager sessionManager = new SessionManager(this);

        // Map backend roles: "Admin" -> "ADMIN", "Standard-User" -> "CLIENT"
        String appRole = "CLIENT";
        if (role != null) {
            if (role.equalsIgnoreCase("Admin")) {
                appRole = "ADMIN";
            } else if (role.equalsIgnoreCase("Standard-User")) {
                appRole = "CLIENT";
            }
        }

        sessionManager.saveSession(email, appRole, name, userId, token);

        ApiClient.resetClient();
        navigateBasedOnRole(appRole, email);
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            // Password is now visible - show eye with slash to hide
            editTextPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            buttonTogglePassword.setImageResource(R.drawable.visibility_off_24);
            buttonTogglePassword.setColorFilter(ContextCompat.getColor(this, R.color.primary));
        } else {
            // Password is now hidden - show eye open to show
            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            buttonTogglePassword.setImageResource(R.drawable.visibility_24);
            buttonTogglePassword.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
        }
        // Move cursor to end
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    private void handleLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        buttonLogin.setEnabled(false);
        buttonLogin.setText("Logging in...");

        LoginRequest loginRequest = new LoginRequest(email, password);
        ApiClient.getApiService(this).login(loginRequest).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                buttonLogin.setEnabled(true);
                buttonLogin.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    saveSessionAndNavigate(email, loginResponse);
                } else {
                    String errorMessage = "Login failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            // Try to parse message from backend error response
                            com.google.gson.JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("message")) {
                                errorMessage = errorObj.get("message").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                buttonLogin.setEnabled(true);
                buttonLogin.setText("Login");
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSessionAndNavigate(String email, LoginResponse response) {
        SessionManager sessionManager = new SessionManager(this);

        // Map backend roles: "Admin" -> "ADMIN", "Standard-User" -> "CLIENT"
        String appRole = "CLIENT";
        String backendRole = response.getRole();
        if (backendRole != null) {
            if (backendRole.equalsIgnoreCase("Admin")) {
                appRole = "ADMIN";
            } else if (backendRole.equalsIgnoreCase("Standard-User")) {
                appRole = "CLIENT";
            }
        }

        sessionManager.saveSession(email, appRole, null, response.getUserId(), response.getToken());

        // Clear the static Retrofit instance so it picks up the NEW token
        ApiClient.resetClient();

        navigateBasedOnRole(appRole, email);
    }

    private void navigateBasedOnRole(String role, String email) {
        Intent intent;
        if ("ADMIN".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, ClientDashboardActivity.class);
        }
        intent.putExtra("USER_ROLE", role);
        intent.putExtra("USER_EMAIL", email);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}