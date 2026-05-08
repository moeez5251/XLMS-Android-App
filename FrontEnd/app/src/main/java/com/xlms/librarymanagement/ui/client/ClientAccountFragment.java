package com.xlms.librarymanagement.ui.client;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.xlms.librarymanagement.ui.auth.ForgotPasswordActivity;
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

import com.facebook.shimmer.ShimmerFrameLayout;

public class ClientAccountFragment extends Fragment implements LendingHistoryAdapter.OnReturnClickListener {

    private SessionManager sessionManager;
    private TextView textViewUserName, textViewUserEmail;
    private RecyclerView recyclerViewLendingHistory, recyclerViewReservations;
    private LendingHistoryAdapter lendingAdapter;
    private ReservationAdapter reservationAdapter;
    private List<LendedBook> masterLendingList = new ArrayList<>();
    private List<LendedBook> displayLendingList = new ArrayList<>();
    private List<Reservation> reservationList = new ArrayList<>();
    private TextView chipAll, chipNotReturned, chipReturned;
    private View layoutNoReservations;
    private View loadingOverlay;
    private ShimmerFrameLayout shimmerLendings, shimmerReservations, shimmerAccountInfo;
    private View layoutAccountInfo;
    private TextView textViewMembership;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_account, container, false);

        sessionManager = new SessionManager(requireContext());
        textViewUserName = view.findViewById(R.id.textViewUserName);
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail);
        textViewMembership = view.findViewById(R.id.textViewMembership);
        recyclerViewLendingHistory = view.findViewById(R.id.recyclerViewLendingHistory);
        chipAll = view.findViewById(R.id.chipAll);
        chipNotReturned = view.findViewById(R.id.chipNotReturned);
        chipReturned = view.findViewById(R.id.chipReturned);
        recyclerViewReservations = view.findViewById(R.id.recyclerViewReservations);
        layoutNoReservations = view.findViewById(R.id.layoutNoReservations);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        shimmerLendings = view.findViewById(R.id.shimmerLendings);
        shimmerReservations = view.findViewById(R.id.shimmerReservations);
        shimmerAccountInfo = view.findViewById(R.id.shimmerAccountInfo);
        layoutAccountInfo = view.findViewById(R.id.layoutAccountInfo);

        recyclerViewLendingHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        setupLendingHistory();
        setupReservations();
        setupUserProfile();
        setupFilters();
        setupSecurityActions(view);
        
        return view;
    }

    private void setupLendingHistory() {
        showLendingLoading(true);
        ApiClient.getApiService(requireContext()).getLendings().enqueue(new Callback<List<LendedBook>>() {
            @Override
            public void onResponse(Call<List<LendedBook>> call, Response<List<LendedBook>> response) {
                showLendingLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterLendingList = response.body();
                    displayLendingList = new ArrayList<>(masterLendingList);
                    lendingAdapter = new LendingHistoryAdapter(displayLendingList, ClientAccountFragment.this);
                    recyclerViewLendingHistory.setAdapter(lendingAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<LendedBook>> call, Throwable t) {
                showLendingLoading(false);
                Toast.makeText(getContext(), "Failed to load lendings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupReservations() {
        showReservationLoading(true);
        ApiService apiService = ApiClient.getApiService(requireContext());
        apiService.getReservations().enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Reservation> rawReservations = response.body();
                    if (rawReservations.isEmpty()) {
                        showReservationLoading(false);
                        layoutNoReservations.setVisibility(View.VISIBLE);
                        recyclerViewReservations.setVisibility(View.GONE);
                    } else {
                        // Enrich reservations with book details
                        enrichReservations(rawReservations);
                    }
                } else {
                    showReservationLoading(false);
                    Toast.makeText(getContext(), "Failed to load reservations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                showReservationLoading(false);
                Toast.makeText(getContext(), "Failed to load reservations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enrichReservations(List<Reservation> list) {
        reservationList = list;
        final int[] enrichedCount = {0};
        ApiService apiService = ApiClient.getApiService(requireContext());

        for (Reservation res : reservationList) {
            com.xlms.librarymanagement.api.GetByIdRequest req = new com.xlms.librarymanagement.api.GetByIdRequest(res.getBookId());
            apiService.getBookById(req).enqueue(new Callback<List<com.xlms.librarymanagement.model.Book>>() {
                @Override
                public void onResponse(Call<List<com.xlms.librarymanagement.model.Book>> call, Response<List<com.xlms.librarymanagement.model.Book>> response) {
                    enrichedCount[0]++;
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        com.xlms.librarymanagement.model.Book book = response.body().get(0);
                        res.setBookTitle(book.getTitle());
                        res.setAuthor(book.getAuthor());
                    }
                    checkAllEnriched(enrichedCount[0], reservationList.size());
                }

                @Override
                public void onFailure(Call<List<com.xlms.librarymanagement.model.Book>> call, Throwable t) {
                    enrichedCount[0]++;
                    checkAllEnriched(enrichedCount[0], reservationList.size());
                }
            });
        }
    }

    private void checkAllEnriched(int current, int total) {
        if (current >= total) {
            showReservationLoading(false);
            layoutNoReservations.setVisibility(View.GONE);
            recyclerViewReservations.setVisibility(View.VISIBLE);
            reservationAdapter = new ReservationAdapter(reservationList);
            recyclerViewReservations.setAdapter(reservationAdapter);
        }
    }

    private void setupUserProfile() {
        showAccountLoading(true);
        ApiClient.getApiService(requireContext()).getUserProfile().enqueue(new Callback<com.xlms.librarymanagement.model.Member>() {
            @Override
            public void onResponse(Call<com.xlms.librarymanagement.model.Member> call, Response<com.xlms.librarymanagement.model.Member> response) {
                showAccountLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    com.xlms.librarymanagement.model.Member user = response.body();
                    textViewUserName.setText(user.getName());
                    textViewUserEmail.setText(user.getEmail());
                    textViewMembership.setText(user.getMembershipType());
                    
                    // Update session if needed
                    sessionManager.saveUserEmail(user.getEmail());
                    sessionManager.saveUserName(user.getName());
                }
            }

            @Override
            public void onFailure(Call<com.xlms.librarymanagement.model.Member> call, Throwable t) {
                showAccountLoading(false);
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReturnClick(LendedBook lending) {
        long diffInMillis = new Date().getTime() - parseDate(lending.getDueDate()).getTime();
        long daysOverdue = Math.max(0, diffInMillis / (1000 * 60 * 60 * 24));
        long fine = daysOverdue * lending.getFinePerDay();

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_return_book, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Bind data
        ((TextView) dialogView.findViewById(R.id.dialogBookTitle)).setText(lending.getBookTitle());
        ((TextView) dialogView.findViewById(R.id.dialogBookAuthor)).setText(lending.getAuthor());
        ((TextView) dialogView.findViewById(R.id.dialogBookCategory)).setText(lending.getCategory());
        ((TextView) dialogView.findViewById(R.id.dialogIssuedDate)).setText(formatDate(lending.getIssuedDate()));
        ((TextView) dialogView.findViewById(R.id.dialogDueDate)).setText(formatDate(lending.getDueDate()));
        ((TextView) dialogView.findViewById(R.id.dialogCopiesLent)).setText(String.valueOf(lending.getCopies()));
        ((TextView) dialogView.findViewById(R.id.dialogPerDayFine)).setText("Rs. " + lending.getFinePerDay());
        ((TextView) dialogView.findViewById(R.id.dialogTotalFine)).setText("Rs. " + fine);

        dialogView.findViewById(R.id.btnDialogCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnDialogReturn).setOnClickListener(v -> {
            dialog.dismiss();
            performReturn(lending);
        });

        dialog.show();
    }

    private void performReturn(LendedBook lending) {
        showLoadingOverlay(true);
        JsonObject body = new JsonObject();
        body.addProperty("book_id", lending.getBookId());
        body.addProperty("borrower_id", lending.getBorrowerId());

        ApiClient.getApiService(requireContext()).returnBook(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                showLoadingOverlay(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Returned successfully", Toast.LENGTH_SHORT).show();
                    setupLendingHistory();
                } else {
                    Toast.makeText(getContext(), "Failed to return", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                showLoadingOverlay(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "N/A";
        try {
            // Handle ISO format from backend
            String cleanDate = dateStr.replace("Z", "+0000");
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            Date date = inputFormat.parse(cleanDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                // Fallback for simple yyyy-MM-dd
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(dateStr.split("T")[0]);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                return outputFormat.format(date);
            } catch (Exception e2) {
                return dateStr;
            }
        }
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null) return new Date();
        try {
            String cleanDate = dateStr.replace("Z", "+0000");
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            return inputFormat.parse(cleanDate);
        } catch (ParseException e) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                return inputFormat.parse(dateStr.split("T")[0]);
            } catch (ParseException e2) {
                return new Date();
            }
        }
    }
    
    private void showLendingLoading(boolean loading) {
        if (shimmerLendings != null) {
            if (loading) {
                shimmerLendings.setVisibility(View.VISIBLE);
                shimmerLendings.startShimmer();
                recyclerViewLendingHistory.setVisibility(View.GONE);
            } else {
                shimmerLendings.stopShimmer();
                shimmerLendings.setVisibility(View.GONE);
                recyclerViewLendingHistory.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showReservationLoading(boolean loading) {
        if (shimmerReservations != null) {
            if (loading) {
                shimmerReservations.setVisibility(View.VISIBLE);
                shimmerReservations.startShimmer();
                recyclerViewReservations.setVisibility(View.GONE);
            } else {
                shimmerReservations.stopShimmer();
                shimmerReservations.setVisibility(View.GONE);
                recyclerViewReservations.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showAccountLoading(boolean loading) {
        if (shimmerAccountInfo != null) {
            if (loading) {
                shimmerAccountInfo.setVisibility(View.VISIBLE);
                shimmerAccountInfo.startShimmer();
                layoutAccountInfo.setVisibility(View.GONE);
            } else {
                shimmerAccountInfo.stopShimmer();
                shimmerAccountInfo.setVisibility(View.GONE);
                layoutAccountInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showLoadingOverlay(boolean loading) {
        if (loadingOverlay != null) loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
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
        TextView textViewForgetPassword = view.findViewById(R.id.textViewForgetPassword);
        if (textViewForgetPassword != null) {
            textViewForgetPassword.setOnClickListener(v -> {
                ForgotPasswordFragment fragment = ForgotPasswordFragment.newInstance(sessionManager.getUserEmail());
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .replace(R.id.mainContentFrame, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        EditText editTextOldPassword = view.findViewById(R.id.editTextOldPassword);
        EditText editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        EditText editTextConfirmNewPassword = view.findViewById(R.id.editTextConfirmNewPassword);

        view.findViewById(R.id.buttonUpdateCredentials).setOnClickListener(v -> {
            String oldPass = editTextOldPassword != null ? editTextOldPassword.getText().toString().trim() : "";
            String newPass = editTextNewPassword != null ? editTextNewPassword.getText().toString().trim() : "";
            String confirmPass = editTextConfirmNewPassword != null ? editTextConfirmNewPassword.getText().toString().trim() : "";

            if (oldPass.isEmpty()) {
                if (editTextOldPassword != null) editTextOldPassword.setError("Old password required");
                return;
            }
            if (newPass.length() < 6) {
                if (editTextNewPassword != null) editTextNewPassword.setError("Minimum 6 characters");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                if (editTextConfirmNewPassword != null) editTextConfirmNewPassword.setError("Passwords do not match");
                return;
            }
            if(oldPass.equals(newPass)){
                if (editTextNewPassword != null) editTextNewPassword.setError("New password must be different than old one");
                return;
            }
            performChangePassword(oldPass, newPass, editTextOldPassword, editTextNewPassword, editTextConfirmNewPassword);
        });
    }

    private void performChangePassword(String oldPass, String newPass, EditText oldEt, EditText newEt, EditText confirmEt) {
        showLoadingOverlay(true);
        JsonObject body = new JsonObject();
        body.addProperty("OldPassword", oldPass);
        body.addProperty("NewPassword", newPass);

        ApiClient.getApiService(requireContext()).changePassword(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                showLoadingOverlay(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                    if (oldEt != null) oldEt.setText("");
                    if (newEt != null) newEt.setText("");
                    if (confirmEt != null) confirmEt.setText("");
                } else {
                    String errorMsg = "Failed to update password";
                    try {
                        if (response.errorBody() != null) {
                            JsonObject errorJson = new com.google.gson.JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                            if (errorJson.has("error")) {
                                errorMsg = errorJson.get("error").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                showLoadingOverlay(false);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
