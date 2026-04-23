package com.xlms.librarymanagement.ui.signup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.ui.login.LoginActivity;
import com.xlms.librarymanagement.utils.SessionManager;

/**
 * Sign Up Activity - Hosts the 3-step registration flow
 * Step 1: Sign Up Form
 * Step 2: Email Verification
 * Step 3: Success Message
 */
public class SignUpActivity extends AppCompatActivity 
    implements SignUpFragment.OnSignUpCompleteListener,
               EmailVerificationFragment.OnVerificationCompleteListener,
               SignUpSuccessFragment.OnSuccessCompleteListener {

    private static final String TAG_SIGN_UP = "SIGN_UP";
    private static final String TAG_VERIFICATION = "VERIFICATION";
    private static final String TAG_SUCCESS = "SUCCESS";

    // User data
    private String fullName;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_container);

        // Load Sign Up fragment initially
        if (savedInstanceState == null) {
            loadSignUpFragment();
        }
    }

    private void loadSignUpFragment() {
        SignUpFragment fragment = new SignUpFragment();
        loadFragment(fragment, TAG_SIGN_UP);
    }

    private void loadVerificationFragment() {
        EmailVerificationFragment fragment = new EmailVerificationFragment();
        loadFragment(fragment, TAG_VERIFICATION);
    }

    private void loadSuccessFragment() {
        SignUpSuccessFragment fragment = new SignUpSuccessFragment();
        loadFragment(fragment, TAG_SUCCESS);
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_right_in,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_right_out
            )
            .replace(R.id.fragmentContainer, fragment, tag);
        
        if (!tag.equals(TAG_SIGN_UP)) {
            transaction.addToBackStack(tag);
        }
        
        transaction.commit();
    }

    // ========== SignUpFragment Callbacks ==========
    
    @Override
    public void onSignUpComplete(String fullName, String email, String password) {
        // Save user data
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        
        // Call register API
        registerUser();
    }

    private void registerUser() {
        // Show a progress dialog or some loading indicator
        // For simplicity, we can use the activity's state or a dedicated fragment
        
        com.xlms.librarymanagement.api.RegisterRequest request = new com.xlms.librarymanagement.api.RegisterRequest(
                fullName, email, "Client", "Basic", password
        );

        com.xlms.librarymanagement.api.ApiClient.getApiService(this).register(request).enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.MessageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.MessageResponse> response) {
                if (response.isSuccessful()) {
                    // Navigate to verification (Backend sends OTP automatically if implemented)
                    loadVerificationFragment();
                } else {
                    String error = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            com.google.gson.JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("error")) {
                                error = errorObj.get("error").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    android.widget.Toast.makeText(SignUpActivity.this, error, android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, Throwable t) {
                android.widget.Toast.makeText(SignUpActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNavigateToLogin() {
        // Navigate to login screen
        navigateToLogin();
    }

    // ========== EmailVerificationFragment Callbacks ==========
    
    @Override
    public void onVerificationComplete(String otp) {
        com.xlms.librarymanagement.api.VerifyOtpRequest request = new com.xlms.librarymanagement.api.VerifyOtpRequest(email, otp);
        
        com.xlms.librarymanagement.api.ApiClient.getApiService(this).verifyOtp(request).enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.MessageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.MessageResponse> response) {
                if (response.isSuccessful()) {
                    loadSuccessFragment();
                } else {
                    String error = "Verification failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            com.google.gson.JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("message")) {
                                error = errorObj.get("message").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    android.widget.Toast.makeText(SignUpActivity.this, error, android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, Throwable t) {
                android.widget.Toast.makeText(SignUpActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNavigateBack() {
        onBackPressed();
    }

    @Override
    public void onResendCode() {
        com.xlms.librarymanagement.api.OtpRequest request = new com.xlms.librarymanagement.api.OtpRequest(fullName, email);
        
        com.xlms.librarymanagement.api.ApiClient.getApiService(this).resendOtp(request).enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.MessageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.MessageResponse> response) {
                if (response.isSuccessful()) {
                    android.widget.Toast.makeText(SignUpActivity.this, "Verification code resent", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(SignUpActivity.this, "Failed to resend code", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, Throwable t) {
                android.widget.Toast.makeText(SignUpActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========== SignUpSuccessFragment Callbacks ==========
    
    @Override
    public void onContinueToDashboard() {
        // Save session for new user
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.saveSession(email, "CLIENT", fullName);

        // Navigate to dashboard
        android.content.Intent intent = new android.content.Intent(SignUpActivity.this, com.xlms.librarymanagement.ui.client.ClientDashboardActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        finish();
    }

    private void navigateToLogin() {
        finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        } else {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        }
    }
}
