package com.xlms.librarymanagement.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Member;

import java.util.UUID;

public class AddUserFragment extends Fragment {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Spinner spinnerMembershipType;
    private RadioGroup radioGroupRole;
    private RadioButton radioUser, radioAdmin;
    private Button buttonCancel, buttonCreate;
    private android.widget.ProgressBar progressBar;

    private OnUserActionListener listener;
    private final String[] membershipOptions = {"English", "Urdu", "French", "Hindi"};

    public interface OnUserActionListener {
        void onUserAdded();
        void onCancel();
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupSpinners();
        setupClickListeners();
    }

    private void initViews(View view) {
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        spinnerMembershipType = view.findViewById(R.id.spinnerMembershipType);
        radioGroupRole = view.findViewById(R.id.radioGroupRole);
        radioUser = view.findViewById(R.id.radioUser);
        radioAdmin = view.findViewById(R.id.radioAdmin);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCreate = view.findViewById(R.id.buttonCreate);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupSpinners() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, membershipOptions);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembershipType.setAdapter(typeAdapter);
    }

    private void setupClickListeners() {
        buttonCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel();
        });

        buttonCreate.setOnClickListener(v -> validateAndCreate());
    }

    private void validateAndCreate() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password too short");
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        buttonCreate.setEnabled(false);

        String role = radioAdmin.isChecked() ? "Admin" : "User";
        String type = spinnerMembershipType.getSelectedItem().toString();

        com.xlms.librarymanagement.api.RegisterRequest request = new com.xlms.librarymanagement.api.RegisterRequest(
            name, email, role, type, password
        );

        com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext())
            .register(request)
            .enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.MessageResponse>() {
                @Override
                public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.MessageResponse> response) {
                    buttonCreate.setEnabled(true);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "User created successfully!", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onUserAdded();
                    } else {
                        String errorMessage = "Registration failed";
                        if (response.body() != null && response.body().getError() != null) {
                            errorMessage = response.body().getError();
                        } else if (response.errorBody() != null) {
                            try {
                                String errorJson = response.errorBody().string();
                                com.google.gson.JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                                if (errorObj.has("error")) errorMessage = errorObj.get("error").getAsString();
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, Throwable t) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    buttonCreate.setEnabled(true);
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
