package com.xlms.librarymanagement.ui.auth;

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

import com.google.gson.JsonObject;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.ui.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Forgot Password Activity - Account Recovery
 * Allows users to request password reset instructions via email
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // UI Components
    private ImageButton buttonBack;
    private EditText editTextEmail;
    private Button buttonSendReset;
    private android.widget.ProgressBar progressBar;
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
        progressBar = findViewById(R.id.progressBar);
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

        buttonSendReset.setEnabled(false);
        buttonSendReset.setText("");
        progressBar.setVisibility(View.VISIBLE);

        JsonObject body = new JsonObject();
        body.addProperty("Email", email);

        ApiClient.getApiService(this).resetPassword(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                buttonSendReset.setEnabled(true);
                buttonSendReset.setText("Send Reset Instructions");
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset instructions sent to your email", Toast.LENGTH_LONG).show();
                    editTextEmail.setText("");
                } else {
                    String errorMessage = "Failed to send reset instructions";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("error")) {
                                errorMessage = errorObj.get("error").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                buttonSendReset.setEnabled(true);
                buttonSendReset.setText("Send Reset Instructions");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
