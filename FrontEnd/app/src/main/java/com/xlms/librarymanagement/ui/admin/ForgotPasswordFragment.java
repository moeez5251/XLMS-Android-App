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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment {

    private static final String ARG_EMAIL = "arg_email";
    private static final String ARG_USER_ID = "arg_user_id";
    private ImageButton buttonBack;
    private EditText editTextEmail;
    private Button buttonSendReset;
    private TextView textViewBackToProfile;
    private View emailAccentLine;
    private ProgressBar progressBar;
    private String prefilledEmail;
    private String userId;

    private OnForgotPasswordActionListener listener;

    public interface OnForgotPasswordActionListener {
        void onBack();
    }

    public void setOnForgotPasswordActionListener(OnForgotPasswordActionListener listener) {
        this.listener = listener;
    }

    public static ForgotPasswordFragment newInstance(String email, String userId) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
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

        if (getArguments() != null) {
            prefilledEmail = getArguments().getString(ARG_EMAIL);
            userId = getArguments().getString(ARG_USER_ID);
        }

        initViews(view);
        setupFocusListeners();
        setupClickListeners();

        if (prefilledEmail != null) {
            editTextEmail.setText(prefilledEmail);
            editTextEmail.setEnabled(false);
        }
    }

    private void initViews(View view) {
        buttonBack = view.findViewById(R.id.buttonBack);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonSendReset = view.findViewById(R.id.buttonSendReset);
        textViewBackToProfile = view.findViewById(R.id.textViewBackToProfile);
        emailAccentLine = view.findViewById(R.id.emailAccentLine);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupFocusListeners() {
        editTextEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (emailAccentLine == null) return;
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

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        buttonSendReset.setEnabled(false);

        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        if (userId != null && !userId.isEmpty()) {
            body.addProperty("ID", userId);
        }

        ApiClient.getApiService(requireContext()).forgotPassword(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                buttonSendReset.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Reset instructions sent!", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onBack();
                } else {
                    Toast.makeText(getContext(), "Failed to send reset instructions", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                buttonSendReset.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
