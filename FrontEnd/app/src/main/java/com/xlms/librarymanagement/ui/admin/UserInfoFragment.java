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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Member;

public class UserInfoFragment extends Fragment {

    private static final String ARG_MEMBER = "member_data";

    private TextView textViewTitle, textViewUserId, textViewStatus;
    private LinearLayout layoutStatus;
    private EditText editTextName, editTextEmail, editTextPassword;
    private Spinner spinnerMembershipType, spinnerMembershipTier;
    private RadioGroup radioGroupRole;
    private RadioButton radioUser, radioAdmin;
    private Button buttonEdit, buttonSave, buttonCancelEdit, buttonDelete, buttonDeactivate;
    private LinearLayout layoutViewActions, layoutEditActions;

    private Member currentMember;
    private boolean isEditMode = false;
    private OnUserInfoActionListener listener;

    public interface OnUserInfoActionListener {
        void onUserUpdated(Member member);
        void onUserDeleted(Member member);
        void onUserStatusChanged(Member member);
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
            Toast.makeText(requireContext(), "Error: No user data", Toast.LENGTH_SHORT).show();
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
        spinnerMembershipTier = view.findViewById(R.id.spinnerMembershipTier);
        radioGroupRole = view.findViewById(R.id.radioGroupRole);
        radioUser = view.findViewById(R.id.radioUser);
        radioAdmin = view.findViewById(R.id.radioAdmin);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancelEdit = view.findViewById(R.id.buttonCancelEdit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonDeactivate = view.findViewById(R.id.buttonDeactivate);
        layoutViewActions = view.findViewById(R.id.layoutViewActions);
        layoutEditActions = view.findViewById(R.id.layoutEditActions);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.membership_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembershipType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> tierAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.membership_tiers, android.R.layout.simple_spinner_item);
        tierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembershipTier.setAdapter(tierAdapter);
    }

    private void populateFields() {
        textViewUserId.setText("ID: " + currentMember.getUserId());
        editTextName.setText(currentMember.getName());
        editTextEmail.setText(currentMember.getEmail());
        editTextPassword.setText("••••••••••");

        setSpinnerSelection(spinnerMembershipType, currentMember.getMembershipType());
        setSpinnerSelection(spinnerMembershipTier, currentMember.getRole()); // Using role as tier for now

        if ("Admin".equals(currentMember.getRole())) {
            radioAdmin.setChecked(true);
        } else {
            radioUser.setChecked(true);
        }

        updateStatusBadge();
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
            layoutStatus.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.status_active_bg));
            textViewStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_active_text));
            textViewStatus.setText("Active");
            buttonDeactivate.setText("Deactivate");
        } else {
            layoutStatus.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.status_deactivated_bg));
            textViewStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_deactivated_text));
            textViewStatus.setText("Deactivated");
            buttonDeactivate.setText("Activate");
        }
    }

    private void setupClickListeners() {
        buttonEdit.setOnClickListener(v -> {
            isEditMode = true;
            updateUIForMode();
        });

        buttonCancelEdit.setOnClickListener(v -> {
            isEditMode = false;
            populateFields(); // Reset fields
            updateUIForMode();
        });

        buttonSave.setOnClickListener(v -> {
            if (validateAndSave()) {
                isEditMode = false;
                updateUIForMode();
            }
        });

        buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        buttonDeactivate.setOnClickListener(v -> toggleUserStatus());
    }

    private void updateUIForMode() {
        boolean editable = isEditMode;
        
        editTextName.setEnabled(editable);
        editTextEmail.setEnabled(editable);
        // Password field is NEVER editable
        editTextPassword.setEnabled(false);
        spinnerMembershipType.setEnabled(editable);
        spinnerMembershipTier.setEnabled(editable);
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
            updateStatusBadge();
        }
    }

    private boolean validateAndSave() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        String role = radioAdmin.isChecked() ? "Admin" : "User";
        String membershipType = spinnerMembershipType.getSelectedItem().toString();
        String membershipTier = spinnerMembershipTier.getSelectedItem().toString();

        // Update member
        currentMember.setName(name);
        currentMember.setEmail(email);
        currentMember.setRole(role);
        currentMember.setMembershipType(membershipType);

        if (listener != null) {
            listener.onUserUpdated(currentMember);
        }

        Toast.makeText(requireContext(), "User updated successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete \"" + currentMember.getName() + "\"? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                if (listener != null) {
                    listener.onUserDeleted(currentMember);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void toggleUserStatus() {
        String currentStatus = currentMember.getStatus();
        String newStatus = "Active".equals(currentStatus) ? "Deactivated" : "Active";
        
        String action = "Active".equals(newStatus) ? "activate" : "deactivate";
        
        new AlertDialog.Builder(requireContext())
            .setTitle(action.substring(0, 1).toUpperCase() + action.substring(1) + " User")
            .setMessage("Are you sure you want to " + action + " \"" + currentMember.getName() + "\"?")
            .setPositiveButton(action.substring(0, 1).toUpperCase() + action.substring(1), (dialog, which) -> {
                currentMember.setStatus(newStatus);
                updateStatusBadge();
                if (listener != null) {
                    listener.onUserStatusChanged(currentMember);
                }
                Toast.makeText(requireContext(), "User " + action + "d successfully", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
