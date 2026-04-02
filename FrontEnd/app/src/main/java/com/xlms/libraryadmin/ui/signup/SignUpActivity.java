package com.xlms.libraryadmin.ui.signup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xlms.libraryadmin.R;
import com.xlms.libraryadmin.ui.login.LoginActivity;

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
        
        // Navigate to verification
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
        // Here you would verify the OTP with your backend
        // For now, proceed to success screen
        loadSuccessFragment();
    }

    @Override
    public void onNavigateBack() {
        onBackPressed();
    }

    @Override
    public void onResendCode() {
        // Handle resend code logic
        // Send OTP again to user email
    }

    // ========== SignUpSuccessFragment Callbacks ==========
    
    @Override
    public void onContinueToDashboard() {
        // Navigate to dashboard (for new users)
        // For now, navigate to login
        navigateToLogin();
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
