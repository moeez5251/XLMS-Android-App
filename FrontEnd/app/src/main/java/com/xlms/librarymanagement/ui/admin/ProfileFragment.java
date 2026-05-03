package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;

import com.google.gson.JsonObject;
import com.xlms.librarymanagement.model.Member;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements AdminDashboardActivity.Refreshable {

    private TextView textViewArchiveId;
    private EditText editTextFullName, editTextEmail;
    private EditText editTextCurrentPassword, editTextNewPassword, editTextVerifyPassword;
    private Button buttonForgotPassword, buttonCancel, buttonUpdate;
    private android.widget.ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadProfileData();
        setupClickListeners();
    }

    @Override
    public void refreshData() {
        loadProfileData();
    }

    private void initViews(View view) {
        textViewArchiveId = view.findViewById(R.id.textViewArchiveId);
        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextFullName.setEnabled(false);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextEmail.setEnabled(false); // Disable email edit
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextVerifyPassword = view.findViewById(R.id.editTextVerifyPassword);
        buttonForgotPassword = view.findViewById(R.id.buttonForgotPassword);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void loadProfileData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext()).getUserProfile().enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Member member = response.body();
                    textViewArchiveId.setText("ID: " + member.getUserId());
                    editTextFullName.setText(member.getName());
                    editTextEmail.setText(member.getEmail());
                } else {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        if (buttonForgotPassword != null) {
            buttonForgotPassword.setOnClickListener(v -> openForgotPasswordFragment());
        }
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> loadProfileData());
        }
        if (buttonUpdate != null) {
            buttonUpdate.setOnClickListener(v -> handleUpdate());
        }
    }

    private void handleUpdate() {
        String newPassword = editTextNewPassword.getText().toString().trim();
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String verifyPassword = editTextVerifyPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(requireContext(), "Fill password fields to update", Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentPassword.equals(newPassword) || currentPassword.equals(verifyPassword)){
            Toast.makeText(requireContext(), "New password cannot be same as old password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(verifyPassword)) {
            editTextVerifyPassword.setError("Passwords do not match");
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        JsonObject body = new JsonObject();
        body.addProperty("OldPassword", currentPassword);
        body.addProperty("NewPassword", newPassword);

        ApiClient.getApiService(requireContext()).changePassword(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
                    editTextCurrentPassword.setText("");
                    editTextNewPassword.setText("");
                    editTextVerifyPassword.setText("");
                } else {
                    String error = "Failed to update";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            JsonObject errorObj = com.google.gson.JsonParser.parseString(errorJson).getAsJsonObject();
                            if (errorObj.has("error")) error = errorObj.get("error").getAsString();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openForgotPasswordFragment() {
        String fullId = textViewArchiveId.getText().toString();
        String userId = "";
        if (fullId.startsWith("ID: ")) {
            userId = fullId.substring(4);
        }
        
        ForgotPasswordFragment fragment = ForgotPasswordFragment.newInstance(
                editTextEmail.getText().toString(),
                userId
        );
        fragment.setOnForgotPasswordActionListener(new ForgotPasswordFragment.OnForgotPasswordActionListener() {
            @Override
            public void onBack() {
                closeDetailFragment();
            }
        });
        openDetailFragment(fragment);
    }

    private void openDetailFragment(Fragment fragment) {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).openDetailScreen(fragment);
        }
    }

    private void closeDetailFragment() {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).closeDetailScreen();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references if needed
    }
}
