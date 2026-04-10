package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class ForgotPasswordFragment extends Fragment {

    private ImageButton buttonBack;
    private EditText editTextEmail;
    private Button buttonSendReset;
    private TextView textViewBackToProfile;
    private View emailAccentLine;

    private OnForgotPasswordActionListener listener;

    public interface OnForgotPasswordActionListener {
        void onBack();
    }

    public void setOnForgotPasswordActionListener(OnForgotPasswordActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupFocusListeners();
        setupClickListeners();
    }

    private void initViews(View view) {
        buttonBack = view.findViewById(R.id.buttonBack);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonSendReset = view.findViewById(R.id.buttonSendReset);
        textViewBackToProfile = view.findViewById(R.id.textViewBackToProfile);
        emailAccentLine = view.findViewById(R.id.emailAccentLine);
    }

    private void setupFocusListeners() {
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
                editTextEmail.setError(null);
            }
        });
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });

        buttonSendReset.setOnClickListener(v -> handleSendReset());

        textViewBackToProfile.setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });
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

        Toast.makeText(requireContext(), "Reset instructions sent to " + email, Toast.LENGTH_LONG).show();
        editTextEmail.setText("");
    }
}
