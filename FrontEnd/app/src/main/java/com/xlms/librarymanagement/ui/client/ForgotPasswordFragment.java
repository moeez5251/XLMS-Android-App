package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private EditText editTextEmail;
    private Button buttonSendReset;
    private ImageButton buttonBack;
    private View loadingOverlay;
    private String prefilledEmail = "";

    public static ForgotPasswordFragment newInstance(String email) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            prefilledEmail = getArguments().getString("email", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        buttonSendReset = view.findViewById(R.id.buttonSendReset);
        buttonBack = view.findViewById(R.id.buttonBack);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);

        if (!prefilledEmail.isEmpty()) {
            editTextEmail.setText(prefilledEmail);
        }

        buttonBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        buttonSendReset.setOnClickListener(v -> handleForgotPassword());

        return view;
    }

    private void handleForgotPassword() {
        String email = editTextEmail.getText().toString().trim();
        editTextEmail.setEnabled(false);

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            return;
        }

        if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);

        JsonObject body = new JsonObject();
        body.addProperty("Email", email);

        ApiClient.getApiService(requireContext()).forgotPassword(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Reset instructions sent to your email", Toast.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    String errorMsg = "Failed to send reset email";
                    try {
                        if (response.errorBody() != null) {
                            JsonObject errorJson = new com.google.gson.JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                            if (errorJson.has("error")) errorMsg = errorJson.get("error").getAsString();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
