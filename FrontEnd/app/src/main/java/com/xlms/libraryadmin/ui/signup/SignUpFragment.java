package com.xlms.libraryadmin.ui.signup;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xlms.libraryadmin.R;

/**
 * Sign Up Fragment - Step 1 of 3 in the registration flow
 * Collects user information: Full Name, Email, Password
 */
public class SignUpFragment extends Fragment {

    // UI Components
    private EditText editTextFullName, editTextEmail, editTextPassword;
    private ImageButton buttonTogglePassword;
    private CheckBox checkboxTerms;
    private Button buttonCreateAccount;
    private TextView textViewLogin;
    private View nameAccentLine, emailAccentLine, passwordAccentLine;

    // State
    private boolean isPasswordVisible = false;
    private OnSignUpCompleteListener mListener;

    // Interface for communication with activity
    public interface OnSignUpCompleteListener {
        void onSignUpComplete(String fullName, String email, String password);
        void onNavigateToLogin();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpCompleteListener) {
            mListener = (OnSignUpCompleteListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupFocusListeners();
        setupClickListeners();
    }

    private void initViews(View view) {
        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonTogglePassword = view.findViewById(R.id.buttonTogglePassword);
        checkboxTerms = view.findViewById(R.id.checkboxTerms);
        buttonCreateAccount = view.findViewById(R.id.buttonCreateAccount);
        textViewLogin = view.findViewById(R.id.textViewLogin);
        nameAccentLine = view.findViewById(R.id.nameAccentLine);
        emailAccentLine = view.findViewById(R.id.emailAccentLine);
        passwordAccentLine = view.findViewById(R.id.passwordAccentLine);
    }

    private void setupFocusListeners() {
        // Full Name field focus listener
        editTextFullName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateAccentLine(nameAccentLine, editTextFullName.hasFocus());
            }
        });
        editTextFullName.setOnFocusChangeListener((v, hasFocus) -> 
            updateAccentLine(nameAccentLine, hasFocus));

        // Email field focus listener
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateAccentLine(emailAccentLine, editTextEmail.hasFocus());
            }
        });
        editTextEmail.setOnFocusChangeListener((v, hasFocus) -> 
            updateAccentLine(emailAccentLine, hasFocus));

        // Password field focus listener
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateAccentLine(passwordAccentLine, editTextPassword.hasFocus());
            }
        });
        editTextPassword.setOnFocusChangeListener((v, hasFocus) -> 
            updateAccentLine(passwordAccentLine, hasFocus));
    }

    private void updateAccentLine(View accentLine, boolean hasFocus) {
        if (accentLine != null) {
            if (hasFocus) {
                accentLine.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                accentLine.getLayoutParams().width = 0;
            }
            accentLine.requestLayout();
        }
    }

    private void setupClickListeners() {
        // Toggle password visibility
        buttonTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Create Account button
        buttonCreateAccount.setOnClickListener(v -> handleCreateAccount());

        // Login link
        textViewLogin.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onNavigateToLogin();
            }
        });
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            editTextPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            buttonTogglePassword.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary));
        } else {
            editTextPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            buttonTogglePassword.setColorFilter(ContextCompat.getColor(requireContext(), R.color.outline_variant));
        }
        editTextPassword.setSelection(editTextPassword.getText().length());
    }

    private void handleCreateAccount() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validation
        if (fullName.isEmpty()) {
            editTextFullName.setError("Please enter your full name");
            editTextFullName.requestFocus();
            return;
        }

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

        if (password.isEmpty()) {
            editTextPassword.setError("Please create a password");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (!checkboxTerms.isChecked()) {
            Toast.makeText(requireContext(), "Please agree to the Terms of Service", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed to email verification
        if (mListener != null) {
            mListener.onSignUpComplete(fullName, email, password);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
