package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
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

public class ProfileFragment extends Fragment {

    private ImageButton buttonMenu;
    private TextView textViewArchiveId;
    private EditText editTextFullName, editTextEmail;
    private EditText editTextCurrentPassword, editTextNewPassword, editTextVerifyPassword;
    private Button buttonForgotPassword, buttonCancel, buttonUpdate;

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
        setupClickListeners();
    }

    private void initViews(View view) {
        buttonMenu = view.findViewById(R.id.buttonMenu);
        textViewArchiveId = view.findViewById(R.id.textViewArchiveId);
        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextVerifyPassword = view.findViewById(R.id.editTextVerifyPassword);
        buttonForgotPassword = view.findViewById(R.id.buttonForgotPassword);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);

        // Set dummy data
        textViewArchiveId.setText("XLMS-2944-XBD-09");
    }

    private void setupClickListeners() {
        // Menu button opens bottom sheet
        if (buttonMenu != null) {
            buttonMenu.setOnClickListener(v -> {
                if (getActivity() instanceof AdminDashboardActivity) {
                    ((AdminDashboardActivity) getActivity()).openBottomSheet();
                }
            });
        }

        // Forgot Password button opens ForgotPasswordFragment
        if (buttonForgotPassword != null) {
            buttonForgotPassword.setOnClickListener(v -> openForgotPasswordFragment());
        }

        // Cancel button resets form
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> {
                editTextFullName.setText("Dr. Julian Vane");
                editTextEmail.setText("j.vane@university-archival.edu");
                editTextCurrentPassword.setText("");
                editTextNewPassword.setText("");
                editTextVerifyPassword.setText("");
                Toast.makeText(requireContext(), "Changes cancelled", Toast.LENGTH_SHORT).show();
            });
        }

        // Update button validates and saves
        if (buttonUpdate != null) {
            buttonUpdate.setOnClickListener(v -> handleUpdate());
        }
    }

    private void handleUpdate() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String verifyPassword = editTextVerifyPassword.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        // If changing password, validate all password fields
        if (!TextUtils.isEmpty(currentPassword) || !TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(verifyPassword)) {
            if (TextUtils.isEmpty(currentPassword)) {
                editTextCurrentPassword.setError("Current password is required");
                editTextCurrentPassword.requestFocus();
                return;
            }

            if (newPassword.length() < 12) {
                editTextNewPassword.setError("Password must be at least 12 characters");
                editTextNewPassword.requestFocus();
                return;
            }

            if (!newPassword.equals(verifyPassword)) {
                editTextVerifyPassword.setError("Passwords do not match");
                editTextVerifyPassword.requestFocus();
                return;
            }
        }

        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }

    private void openForgotPasswordFragment() {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
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
}
