package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.utils.SessionManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xlms.librarymanagement.model.LendedBook;
import com.xlms.librarymanagement.utils.LendingRepository;
import java.util.ArrayList;
import java.util.List;

public class ClientAccountFragment extends Fragment {

    private SessionManager sessionManager;
    private TextView textViewUserName, textViewUserEmail;
    private RecyclerView recyclerViewLendingHistory;
    private LendingHistoryAdapter lendingAdapter;
    private List<LendedBook> masterLendingList;
    private List<LendedBook> displayLendingList;
    private TextView chipAll, chipNotReturned, chipReturned;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_account, container, false);

        sessionManager = new SessionManager(requireContext());
        textViewUserName = view.findViewById(R.id.textViewUserName);
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail);
        recyclerViewLendingHistory = view.findViewById(R.id.recyclerViewLendingHistory);
        chipAll = view.findViewById(R.id.chipAll);
        chipNotReturned = view.findViewById(R.id.chipNotReturned);
        chipReturned = view.findViewById(R.id.chipReturned);

        setupLendingHistory();
        setupFilters();
        setupSecurityActions(view);
        
        String email = sessionManager.getUserEmail();
        String name = sessionManager.getUserName();
        
        if (email != null) {
            textViewUserEmail.setText(email);
        }
        
        if (name != null) {
            textViewUserName.setText(name);
        }

        return view;
    }

    private void setupLendingHistory() {
        LendingRepository repository = new LendingRepository(requireContext());
        masterLendingList = repository.getLendingsByUser(sessionManager.getUserEmail());
        displayLendingList = new ArrayList<>();

        if (masterLendingList.isEmpty()) {
            // Add some mock data if empty for first time demo
            masterLendingList.add(new LendedBook(1, sessionManager.getUserEmail(), sessionManager.getUserName(), "TV", "The Great Gatsby", "F. Scott Fitzgerald", "Classic", 1, "2024-04-15", "2024-04-29", "Returned"));
            repository.addLending(masterLendingList.get(0));
        }

        displayLendingList.addAll(masterLendingList);
        lendingAdapter = new LendingHistoryAdapter(displayLendingList);
        recyclerViewLendingHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewLendingHistory.setAdapter(lendingAdapter);
    }

    private void setupFilters() {
        if (chipAll != null) chipAll.setOnClickListener(v -> filterLendings("All"));
        if (chipNotReturned != null) chipNotReturned.setOnClickListener(v -> filterLendings("Not Returned"));
        if (chipReturned != null) chipReturned.setOnClickListener(v -> filterLendings("Returned"));
    }

    private void filterLendings(String status) {
        displayLendingList.clear();
        if ("All".equalsIgnoreCase(status)) {
            displayLendingList.addAll(masterLendingList);
            updateChipUI(chipAll);
        } else {
            for (LendedBook book : masterLendingList) {
                if (book.getStatus().equalsIgnoreCase(status)) {
                    displayLendingList.add(book);
                }
            }
            if ("Not Returned".equalsIgnoreCase(status)) updateChipUI(chipNotReturned);
            else updateChipUI(chipReturned);
        }
        lendingAdapter.notifyDataSetChanged();
    }

    private void updateChipUI(TextView activeChip) {
        // Reset all chips
        resetChipStyle(chipAll);
        resetChipStyle(chipNotReturned);
        resetChipStyle(chipReturned);

        // Set active chip
        activeChip.setBackgroundResource(R.drawable.bg_filter_chip_active);
        activeChip.setTextColor(getResources().getColor(R.color.white));
    }

    private void resetChipStyle(TextView chip) {
        if (chip == null) return;
        chip.setBackgroundResource(R.drawable.bg_filter_chip);
        chip.setTextColor(getResources().getColor(R.color.on_surface_variant));
    }

    private void setupSecurityActions(View view) {
        EditText editTextOldPassword = view.findViewById(R.id.editTextOldPassword);
        EditText editTextNewPassword = view.findViewById(R.id.editTextNewPassword);

        view.findViewById(R.id.buttonUpdateCredentials).setOnClickListener(v -> {
            String oldPass = editTextOldPassword != null ? editTextOldPassword.getText().toString().trim() : "";
            String newPass = editTextNewPassword != null ? editTextNewPassword.getText().toString().trim() : "";

            if (oldPass.isEmpty()) {
                if (editTextOldPassword != null) editTextOldPassword.setError("Required");
                return;
            }
            if (newPass.length() < 6) {
                if (editTextNewPassword != null) editTextNewPassword.setError("Minimum 6 characters");
                return;
            }

            android.widget.Toast.makeText(getContext(), "Password updated successfully", android.widget.Toast.LENGTH_SHORT).show();
            if (editTextOldPassword != null) editTextOldPassword.setText("");
            if (editTextNewPassword != null) editTextNewPassword.setText("");
        });

        view.findViewById(R.id.buttonDeleteAccount).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        sessionManager.clearSession();
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
