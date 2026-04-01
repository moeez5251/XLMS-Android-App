package com.xlms.libraryadmin.ui.signup;

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

import com.xlms.libraryadmin.R;

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

        // Proceed to success screen
        if (mListener != null) {
            mListener.onVerificationComplete(otp.toString());
        }
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
