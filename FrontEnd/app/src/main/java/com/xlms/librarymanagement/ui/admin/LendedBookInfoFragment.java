package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.GetByIdRequest;
import com.xlms.librarymanagement.model.BookLending;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LendedBookInfoFragment extends Fragment {

    private static final String ARG_BORROWER_ID = "borrower_id";
    private TextView textViewTitle, textViewAuthor, textViewCategory, textViewLenderEmail;
    private TextView textViewIssuedDate, textViewStatus, textViewLenderName, textViewDueDate, textViewBookId, textViewBorrowerId;
    private String borrowerId;
    private ImageButton buttonBack;
    private LinearLayout contentLayout, shimmerLayout;

    public static LendedBookInfoFragment newInstance(int borrowerId) {
        LendedBookInfoFragment fragment = new LendedBookInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BORROWER_ID, String.valueOf(borrowerId));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lended_book_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (getArguments() != null) {
            borrowerId = getArguments().getString(ARG_BORROWER_ID);
        }

        initViews(view);
        fetchLenderDetails();
        setupClickListeners();
    }

    private void initViews(View view) {
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewAuthor = view.findViewById(R.id.textViewAuthor);
        textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewLenderEmail = view.findViewById(R.id.textViewLenderEmail);
        textViewIssuedDate = view.findViewById(R.id.textViewIssuedDate);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewLenderName = view.findViewById(R.id.textViewLenderName);
        textViewDueDate = view.findViewById(R.id.textViewDueDate);
        textViewBookId = view.findViewById(R.id.textViewBookId);
        textViewBorrowerId = view.findViewById(R.id.textViewBorrowerId);
        
        contentLayout = view.findViewById(R.id.contentLayout);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        buttonBack = view.findViewById(R.id.buttonBack);
    }

    private void showSkeleton(boolean show) {
        if (shimmerLayout == null) return;
        if (show) {
            shimmerLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        } else {
            shimmerLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    private void fetchLenderDetails() {
        showSkeleton(true);
        ApiClient.getApiService(requireContext())
            .getLenderById(new GetByIdRequest(borrowerId))
            .enqueue(new Callback<BookLending>() {
                @Override
                public void onResponse(Call<BookLending> call, Response<BookLending> response) {
                    showSkeleton(false);
                    if (response.isSuccessful() && response.body() != null) {
                        populateFields(response.body());
                    } else {
                        Toast.makeText(requireContext(), "Failed to load details", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<BookLending> call, Throwable t) {
                    showSkeleton(false);
                    Toast.makeText(requireContext(), "Error fetching details", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void populateFields(BookLending lending) {
        textViewTitle.setText(lending.getBookTitle());
        textViewAuthor.setText(lending.getBookAuthor());
        textViewCategory.setText(lending.getBookCategory());
        textViewLenderEmail.setText(lending.getLenderEmail());
        textViewIssuedDate.setText(formatIsoDate(lending.getIssuedDate()));
        textViewDueDate.setText(formatIsoDate(lending.getDueDate()));
        textViewStatus.setText(lending.getStatus());
        textViewLenderName.setText(lending.getLenderName());
        textViewBookId.setText(lending.getBookId());
        textViewBorrowerId.setText(String.valueOf(lending.getBorrowerId()));
    }

    private String formatIsoDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "N/A";
        try {
            String cleanDate = isoDate.replace("Z", "+0000");
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            java.util.Date date = inputFormat.parse(cleanDate);
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
                java.util.Date date = inputFormat.parse(isoDate.split("T")[0]);
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.US);
                return outputFormat.format(date);
            } catch (Exception e2) {
                return isoDate;
            }
        }
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }
}
