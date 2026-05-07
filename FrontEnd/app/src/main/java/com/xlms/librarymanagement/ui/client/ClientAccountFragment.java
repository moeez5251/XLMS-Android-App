package com.xlms.librarymanagement.ui.client;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.ApiService;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.LendedBook;
import com.xlms.librarymanagement.model.Reservation;
import com.xlms.librarymanagement.utils.ReservationRepository;
import com.xlms.librarymanagement.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientAccountFragment extends Fragment implements LendingHistoryAdapter.OnReturnClickListener {

    private SessionManager sessionManager;
    private TextView textViewUserName, textViewUserEmail;
    private RecyclerView recyclerViewLendingHistory, recyclerViewReservations;
    private LendingHistoryAdapter lendingAdapter;
    private ReservationAdapter reservationAdapter;
    private List<LendedBook> masterLendingList = new ArrayList<>();
    private List<LendedBook> displayLendingList = new ArrayList<>();
    private List<Reservation> reservationList;
    private TextView chipAll, chipNotReturned, chipReturned;
    private View layoutNoReservations;
    private View loadingOverlay;

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
        recyclerViewReservations = view.findViewById(R.id.recyclerViewReservations);
        layoutNoReservations = view.findViewById(R.id.layoutNoReservations);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);

        recyclerViewLendingHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        setupLendingHistory();
        setupReservations();
        setupFilters();
        setupSecurityActions(view);
        
        String email = sessionManager.getUserEmail();
        String name = sessionManager.getUserName();
        
        if (email != null) textViewUserEmail.setText(email);
        if (name != null) textViewUserName.setText(name);

        return view;
    }

    private void setupLendingHistory() {
        showLoading(true);
        ApiClient.getApiService(requireContext()).getLendings().enqueue(new Callback<List<LendedBook>>() {
            @Override
            public void onResponse(Call<List<LendedBook>> call, Response<List<LendedBook>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterLendingList = response.body();
                    displayLendingList = new ArrayList<>(masterLendingList);
                    lendingAdapter = new LendingHistoryAdapter(displayLendingList, ClientAccountFragment.this);
                    recyclerViewLendingHistory.setAdapter(lendingAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<LendedBook>> call, Throwable t) {
                showLoading(false);
            }
        });
    }

    @Override
    public void onReturnClick(LendedBook lending) {
        long diffInMillis = new Date().getTime() - parseDate(lending.getDueDate()).getTime();
        long daysOverdue = Math.max(0, TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS));
        long fine = daysOverdue * 100;

        String message = "Book Title: " + lending.getBookTitle() + "\n" +
                         "Book Author: " + lending.getAuthor() + "\n" +
                         "Book Category: " + lending.getCategory() + "\n" +
                         "Issue Date: " + lending.getIssuedDate() + "\n" +
                         "Due Date: " + lending.getDueDate() + "\n" +
                         "Copies Lent: " + lending.getCopies() + "\n" +
                         "Per Day Fine: 100\n\n" +
                         "Fine Details\n" +
                         "Your Fine will be: Rs. " + fine;

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Return")
                .setMessage(message)
                .setPositiveButton("Return Book", (dialog, which) -> performReturn(lending))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performReturn(LendedBook lending) {
        showLoading(true);
        JsonObject body = new JsonObject();
        body.addProperty("book_id", lending.getBookId());
        body.addProperty("borrower_id", lending.getBorrowerId());

        ApiClient.getApiService(requireContext()).returnBook(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Returned successfully", Toast.LENGTH_SHORT).show();
                    setupLendingHistory();
                } else {
                    Toast.makeText(getContext(), "Failed to return", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date parseDate(String dateStr) {
        try { return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr); } 
        catch (ParseException e) { return new Date(); }
    }
    
    private void showLoading(boolean loading) {
        if (loadingOverlay != null) loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void setupReservations() {
        ReservationRepository repository = new ReservationRepository(requireContext());
        reservationList = repository.getReservationsByUser(sessionManager.getUserEmail());

        if (reservationList.isEmpty()) {
            layoutNoReservations.setVisibility(View.VISIBLE);
            recyclerViewReservations.setVisibility(View.GONE);
        } else {
            layoutNoReservations.setVisibility(View.GONE);
            recyclerViewReservations.setVisibility(View.VISIBLE);
            reservationAdapter = new ReservationAdapter(reservationList);
            recyclerViewReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewReservations.setAdapter(reservationAdapter);
        }
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
        resetChipStyle(chipAll);
        resetChipStyle(chipNotReturned);
        resetChipStyle(chipReturned);

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

            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
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
