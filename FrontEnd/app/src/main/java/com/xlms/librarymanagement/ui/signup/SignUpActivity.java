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

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

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
        // Save user data for later steps
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        
        // Move to verification
        loadVerificationFragment();
    }

    @Override
    public void onNavigateToLogin() {
        // Navigate to login screen
        navigateToLogin();
    }

    // ========== EmailVerificationFragment Callbacks ==========
    
    @Override
    public void onVerificationComplete(String otp) {
        // Sign up was already completed in the fragment
        loadSuccessFragment();
    }

    @Override
    public void onNavigateBack() {
        onBackPressed();
    }

    @Override
    public void onResendCode() {
        com.xlms.librarymanagement.api.OtpRequest request = new com.xlms.librarymanagement.api.OtpRequest(fullName, email);
        
        com.xlms.librarymanagement.api.ApiClient.getApiService(this).sendOtp(request).enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.MessageResponse>() {
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
        // Session was already saved in EmailVerificationFragment.performSignUp().
        // No need to save again here, just navigate.

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
