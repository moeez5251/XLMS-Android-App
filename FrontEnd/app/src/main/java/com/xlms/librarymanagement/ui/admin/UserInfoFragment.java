package com.xlms.librarymanagement.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoFragment extends Fragment {

    private static final String ARG_MEMBER = "member_data";

    private TextView textViewTitle, textViewUserId, textViewStatus;
    private LinearLayout layoutStatus;
    private EditText editTextName, editTextEmail, editTextPassword;
    private Spinner spinnerMembershipType;
    private RadioGroup radioGroupRole;
    private RadioButton radioUser, radioAdmin;
    private Button buttonEdit, buttonSave, buttonCancelEdit, buttonDelete, buttonToggleStatus;
    private LinearLayout layoutViewActions, layoutEditActions;
    private ProgressBar progressBar;

    private Member currentMember;
    private boolean isEditMode = false;
    private OnUserInfoActionListener listener;

    private final String[] membershipOptions = {"English", "Urdu", "French", "Hindi"};

    public interface OnUserInfoActionListener {
        void onUserUpdated();
        void onUserDeleted();
        void onBack();
    }

    public static UserInfoFragment newInstance(Member member) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEMBER, member);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnUserInfoActionListener(OnUserInfoActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            currentMember = (Member) getArguments().getSerializable(ARG_MEMBER);
        }

        if (currentMember == null) {
            if (listener != null) listener.onBack();
            return;
        }

        initViews(view);
        setupSpinners();
        populateFields();
        setupClickListeners();
        updateUIForMode();
    }

    private void initViews(View view) {
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewUserId = view.findViewById(R.id.textViewUserId);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        layoutStatus = view.findViewById(R.id.layoutStatus);
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        spinnerMembershipType = view.findViewById(R.id.spinnerMembershipType);
        radioGroupRole = view.findViewById(R.id.radioGroupRole);
        radioUser = view.findViewById(R.id.radioUser);
        radioAdmin = view.findViewById(R.id.radioAdmin);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancelEdit = view.findViewById(R.id.buttonCancelEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonToggleStatus = view.findViewById(R.id.buttonDeactivate);
        layoutViewActions = view.findViewById(R.id.layoutViewActions);
        layoutEditActions = view.findViewById(R.id.layoutEditActions);
        progressBar = view.findViewById(R.id.progressBar);
        View backButton = view.findViewById(R.id.buttonBack);
        if (backButton != null) {
            backButton.setOnClickListener(v -> { if(listener != null) listener.onBack(); });
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, membershipOptions);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembershipType.setAdapter(typeAdapter);
    }

    private void populateFields() {
        textViewUserId.setText("ID: " + currentMember.getUserId());
        editTextName.setText(currentMember.getName());
        editTextEmail.setText(currentMember.getEmail());

        setSpinnerSelection(spinnerMembershipType, currentMember.getMembershipType());

        if ("Admin".equalsIgnoreCase(currentMember.getRole())) {
            radioAdmin.setChecked(true);
        } else {
            radioUser.setChecked(true);
        }

        updateStatusBadge();
        updateToggleStatusButton();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void updateStatusBadge() {
        if ("Active".equals(currentMember.getStatus())) {
            textViewStatus.setText("Active");
        } else {
            textViewStatus.setText("Deactivated");
        }
    }

    private void updateToggleStatusButton() {
        if ("Active".equals(currentMember.getStatus())) {
            buttonToggleStatus.setText("Deactivate");
        } else {
            buttonToggleStatus.setText("Activate");
        }
    }

    private void setupClickListeners() {
        buttonEdit.setOnClickListener(v -> {
            isEditMode = true;
            updateUIForMode();
        });

        buttonCancelEdit.setOnClickListener(v -> {
            isEditMode = false;
            populateFields();
            updateUIForMode();
        });

        buttonSave.setOnClickListener(v -> saveChanges());
        buttonDelete.setOnClickListener(v -> deleteUser());
        buttonToggleStatus.setOnClickListener(v -> toggleUserStatus());
    }

    private void updateUIForMode() {
        boolean editable = isEditMode;
        editTextName.setEnabled(editable);
        editTextEmail.setEnabled(false);
        spinnerMembershipType.setEnabled(editable);
        radioUser.setEnabled(editable);
        radioAdmin.setEnabled(editable);

        if (editable) {
            layoutViewActions.setVisibility(View.GONE);
            layoutEditActions.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.GONE);
            textViewTitle.setText("Edit User");
        } else {
            layoutViewActions.setVisibility(View.VISIBLE);
            layoutEditActions.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.VISIBLE);
            textViewTitle.setText("User Information");
        }
    }

    private void saveChanges() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        currentMember.setName(editTextName.getText().toString());
        currentMember.setEmail(editTextEmail.getText().toString());
        currentMember.setMembershipType(spinnerMembershipType.getSelectedItem().toString());
        currentMember.setRole(radioAdmin.isChecked() ? "Admin" : "Standard-User");

        // Ensure ID is set for the backend update call
        if (currentMember.getId() == null || currentMember.getId().isEmpty()) {
            currentMember.setId(currentMember.getUserId());
        }

        ApiClient.getApiService(requireContext())
            .updateUser(currentMember)
            .enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "User updated", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onUserUpdated();
                        isEditMode = false;
                        updateUIForMode();
                    } else {
                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void deleteUser() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete this user? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                List<String> ids = new ArrayList<>();
                ids.add(currentMember.getUserId());
                ApiClient.getApiService(requireContext()).deleteUser(ids).enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onUserDeleted();
                        } else {
                            Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override 
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Error deleting user", Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void toggleUserStatus() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        boolean isDeactivating = "Active".equals(currentMember.getStatus());
        
        if (isDeactivating) {
            List<String> ids = new java.util.ArrayList<>();
            ids.add(currentMember.getUserId());
            ApiClient.getApiService(requireContext()).deactivateUser(ids).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        currentMember.setStatus("Deactivated");
                        updateUIAfterStatusChange();
                    }
                }
                @Override public void onFailure(Call<MessageResponse> call, Throwable t) { 
                    if (progressBar != null) progressBar.setVisibility(View.GONE); 
                }
            });
        } else {
            com.google.gson.JsonObject idObj = new com.google.gson.JsonObject();
            idObj.addProperty("ID", currentMember.getUserId());
            ApiClient.getApiService(requireContext()).activateUser(idObj).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        currentMember.setStatus("Active");
                        updateUIAfterStatusChange();
                    }
                }
                @Override public void onFailure(Call<MessageResponse> call, Throwable t) { 
                    if (progressBar != null) progressBar.setVisibility(View.GONE); 
                }
            });
        }
    }

    private void updateUIAfterStatusChange() {
        updateStatusBadge();
        updateToggleStatusButton();
        if (listener != null) listener.onUserUpdated();
    }
}
