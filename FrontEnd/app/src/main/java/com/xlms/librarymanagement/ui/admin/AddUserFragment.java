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
    private Spinner spinnerMembershipType, spinnerMembershipTier;
    private RadioGroup radioGroupRole;
    private RadioButton radioUser, radioAdmin;
    private Button buttonCancel, buttonCreate;

    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onUserAdded(Member member);
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
        spinnerMembershipTier = view.findViewById(R.id.spinnerMembershipTier);
        radioGroupRole = view.findViewById(R.id.radioGroupRole);
        radioUser = view.findViewById(R.id.radioUser);
        radioAdmin = view.findViewById(R.id.radioAdmin);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCreate = view.findViewById(R.id.buttonCreate);
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

    private void setupClickListeners() {
        buttonCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel();
        });

        buttonCreate.setOnClickListener(v -> {
            if (validateAndCreate()) {
                if (listener != null) listener.onCancel();
            }
        });
    }

    private boolean validateAndCreate() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return false;
        }

        String role = radioAdmin.isChecked() ? "Admin" : "User";
        String membershipType = spinnerMembershipType.getSelectedItem().toString();
        String membershipTier = spinnerMembershipTier.getSelectedItem().toString();
        String userId = "Us" + UUID.randomUUID().toString().substring(0, 7).toLowerCase();

        Member newMember = new Member(userId, name, email, role, membershipType, 0, "Active");

        if (listener != null) {
            listener.onUserAdded(newMember);
        }

        Toast.makeText(requireContext(), "User created successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
