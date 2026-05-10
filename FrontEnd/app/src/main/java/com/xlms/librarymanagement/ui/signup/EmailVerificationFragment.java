package com.xlms.librarymanagement.ui.signup;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;

import java.util.Locale;

/**
 * Email Verification Fragment - Step 2 of 3 in the registration flow
 * Handles 6-digit OTP code entry with auto-focus and timer
 */
public class EmailVerificationFragment extends Fragment {

    // UI Components
    private EditText[] otpInputs = new EditText[6];
    private Button buttonVerify;
    private TextView textViewTimer, textViewResend;
    private ImageButton buttonBack;

    // Timer
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 10 * 60 * 1000; // 10 minutes

    // Listener
    private OnVerificationCompleteListener mListener;

    public interface OnVerificationCompleteListener {
        void onVerificationComplete(String otp);
        void onNavigateBack();
        void onResendCode();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnVerificationCompleteListener) {
            mListener = (OnVerificationCompleteListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupOTPInputs();
        setupClickListeners();
        startTimer();
    }

    private void initViews(View view) {
        otpInputs[0] = view.findViewById(R.id.editTextOtp1);
        otpInputs[1] = view.findViewById(R.id.editTextOtp2);
        otpInputs[2] = view.findViewById(R.id.editTextOtp3);
        otpInputs[3] = view.findViewById(R.id.editTextOtp4);
        otpInputs[4] = view.findViewById(R.id.editTextOtp5);
        otpInputs[5] = view.findViewById(R.id.editTextOtp6);
        
        buttonVerify = view.findViewById(R.id.buttonVerify);
        textViewTimer = view.findViewById(R.id.textViewTimer);
        textViewResend = view.findViewById(R.id.textViewResend);
        buttonBack = view.findViewById(R.id.buttonBack);
    }

    private void setupOTPInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }
                
                @Override public void afterTextChanged(Editable s) {}
            });

            otpInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpInputs[index].getText().toString().isEmpty() && index > 0) {
                        otpInputs[index - 1].requestFocus();
                        otpInputs[index - 1].setText("");
                    }
                }
                return false;
            });

            otpInputs[i].setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    otpInputs[index].setBackgroundResource(R.drawable.otp_input_focused);
                } else {
                    otpInputs[index].setBackgroundResource(R.drawable.otp_input_background);
                }
            });
        }
    }

    private void setupClickListeners() {
        // Back button
        buttonBack.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onNavigateBack();
            }
        });

        // Verify button
        buttonVerify.setOnClickListener(v -> handleVerification());

        // Resend code
        textViewResend.setOnClickListener(v -> handleResendCode());
    }

    private void handleVerification() {
        StringBuilder otp = new StringBuilder();
        for (EditText input : otpInputs) {
            otp.append(input.getText().toString().trim());
        }

        if (otp.length() != 6) {
            Toast.makeText(requireContext(), "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonVerify.setEnabled(false);
        buttonVerify.setText("Verifying...");

        // Step 1: Verify OTP
        String email = "";
        if (getActivity() instanceof SignUpActivity) {
            email = ((SignUpActivity) getActivity()).getEmail();
        }
        
        com.xlms.librarymanagement.api.VerifyOtpRequest verifyRequest = new com.xlms.librarymanagement.api.VerifyOtpRequest(email, otp.toString());
        com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext()).verifyOtp(verifyRequest).enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.MessageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.MessageResponse> response) {
                if (response.isSuccessful()) {
                    // Step 2: Perform Final Sign Up
                    performSignUp();
                } else {
                    buttonVerify.setEnabled(true);
                    buttonVerify.setText("Verify Access");
                    String error = "Verification failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            com.google.gson.JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("message")) error = errorObj.get("message").getAsString();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, Throwable t) {
                buttonVerify.setEnabled(true);
                buttonVerify.setText("Verify Access");
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSignUp() {
        buttonVerify.setText("Creating Account...");
        
        String fullName = "", email = "", password = "";
        if (getActivity() instanceof SignUpActivity) {
            SignUpActivity activity = (SignUpActivity) getActivity();
            fullName = activity.getFullName();
            email = activity.getEmail();
            password = activity.getPassword();
        }

        final String finalFullName = fullName;
        final String finalEmail = email;

        com.xlms.librarymanagement.api.SignUpRequest signUpRequest = new com.xlms.librarymanagement.api.SignUpRequest(fullName, email, password);
        com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext()).signup(signUpRequest).enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.RegisterResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.RegisterResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.RegisterResponse> response) {
                buttonVerify.setEnabled(true);
                buttonVerify.setText("Verify Access");
                if (response.isSuccessful() && response.body() != null) {
                    // Save session data
                    com.xlms.librarymanagement.api.RegisterResponse regResp = response.body();
                    
                    // Map backend roles: "Admin" -> "ADMIN", "Standard-User" -> "CLIENT"
                    String appRole = "CLIENT";
                    String backendRole = regResp.getRole();
                    if (backendRole != null) {
                        if (backendRole.equalsIgnoreCase("Admin")) {
                            appRole = "ADMIN";
                        } else if (backendRole.equalsIgnoreCase("Standard-User")) {
                            appRole = "CLIENT";
                        }
                    }

                    com.xlms.librarymanagement.utils.SessionManager sessionManager = new com.xlms.librarymanagement.utils.SessionManager(requireContext());
                    sessionManager.saveSession(finalEmail, appRole, finalFullName, regResp.getUserId(), regResp.getToken());
                    
                    // Reset ApiClient to pick up the new token
                    com.xlms.librarymanagement.api.ApiClient.resetClient();

                    if (mListener != null) {
                        mListener.onVerificationComplete(null);
                    }
                } else {
                    String error = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            com.google.gson.JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("message")) error = errorObj.get("message").getAsString();
                            else if (errorObj.has("error")) error = errorObj.get("error").getAsString();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.RegisterResponse> call, Throwable t) {
                buttonVerify.setEnabled(true);
                buttonVerify.setText("Verify Access");
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleResendCode() {
        if (mListener != null) {
            mListener.onResendCode();
        }
        Toast.makeText(requireContext(), "Code resent to your email", Toast.LENGTH_SHORT).show();
        restartTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                textViewTimer.setText("EXPIRED");
                textViewResend.setEnabled(true);
                textViewResend.setAlpha(1.0f);
            }
        }.start();

        // Disable resend initially
        textViewResend.setEnabled(false);
        textViewResend.setAlpha(0.5f);
    }

    private void restartTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = 10 * 60 * 1000;
        startTimer();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(), "Expires in %02d:%02d", minutes, seconds);
        textViewTimer.setText(timeLeft);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
