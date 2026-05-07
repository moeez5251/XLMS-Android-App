package com.xlms.librarymanagement.ui.client;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Book;
import com.xlms.librarymanagement.model.LendedBook;
import com.xlms.librarymanagement.utils.LendingRepository;
import com.xlms.librarymanagement.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.ApiService;
import com.xlms.librarymanagement.api.MessageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutFragment extends Fragment {

    private Book selectedBook;
    private Calendar lendDate = Calendar.getInstance(); // Today
    private Calendar dueDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

    private TextView textViewLendDate, textViewDueDate, textViewTotalPrice, textViewSubtotal, textViewCopyCount;
    private int copyCount = 1;
    private android.widget.ProgressBar progressBar;
    private View loadingOverlay;

    public static CheckoutFragment newInstance(Book book) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedBook = (Book) getArguments().getSerializable("book");
        }
        // Lend date is fixed to today
        lendDate = Calendar.getInstance();
        
        // Default due date: tomorrow
        dueDate = Calendar.getInstance();
        dueDate.add(Calendar.DAY_OF_YEAR, 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        initViews(view);
        setupBookDetails(view);
        setupDatePickers(view);
        setupCopyControls(view);

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.buttonCheckOut).setOnClickListener(v -> handleCheckout());

        return view;
    }

    private void handleCheckout() {
        if (selectedBook == null) return;

        showLoading(true);

        JsonObject body = new JsonObject();
        body.addProperty("book_id", selectedBook.getBookId());
        body.addProperty("IssuedDate", dateFormat.format(lendDate.getTime()));
        body.addProperty("DueDate", dateFormat.format(dueDate.getTime()));
        body.addProperty("CopiesLent", copyCount);
        body.addProperty("FinePerDay", 100);

        ApiService apiService = ApiClient.getApiService(requireContext());
        apiService.lendBook(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (isAdded()) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        String errorMsg = "Checkout failed";
                        try {
                            if (response.errorBody() != null) {
                                JsonObject errorJson = com.google.gson.JsonParser.parseString(response.errorBody().string()).getAsJsonObject();
                                if (errorJson.has("error") && errorJson.get("error").isJsonPrimitive()) {
                                    errorMsg = errorJson.get("error").getAsString();
                                } else if (errorJson.has("message")) {
                                    errorMsg = errorJson.get("message").getAsString();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (isAdded()) {
                    showLoading(false);
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initViews(View view) {
        textViewLendDate = view.findViewById(R.id.textViewLendDate);
        textViewDueDate = view.findViewById(R.id.textViewDueDate);
        textViewSubtotal = view.findViewById(R.id.textViewSubtotal);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        textViewCopyCount = view.findViewById(R.id.textViewCopyCount);
        
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void showLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        View btn = getView().findViewById(R.id.buttonCheckOut);
        if (btn != null) btn.setEnabled(!loading);
    }

    private void setupBookDetails(View view) {
        if (selectedBook == null) return;

        ((TextView) view.findViewById(R.id.textViewTitle)).setText(selectedBook.getTitle());
        ((TextView) view.findViewById(R.id.textViewAuthor)).setText(selectedBook.getAuthor());
        ((TextView) view.findViewById(R.id.textViewPricePerCopy)).setText(String.format("Rs %.0f", selectedBook.getPrice()));
        ((TextView) view.findViewById(R.id.textViewLanguage)).setText(selectedBook.getLanguage());

        updatePrice();
    }

    private void setupDatePickers(View view) {
        // Lend Date is fixed to Today according to requirements
        view.findViewById(R.id.layoutLendDatePicker).setOnClickListener(v -> 
                Toast.makeText(requireContext(), "Lend date is set to today", Toast.LENGTH_SHORT).show());
        
        view.findViewById(R.id.layoutDueDatePicker).setOnClickListener(v -> showDatePicker());
        
        updateDateDisplays();
    }

    private void setupCopyControls(View view) {
        view.findViewById(R.id.buttonMinus).setOnClickListener(v -> {
            if (copyCount > 1) {
                copyCount--;
                updatePrice();
            }
        });

        view.findViewById(R.id.buttonPlus).setOnClickListener(v -> {
            if (copyCount < selectedBook.getAvailable()) {
                copyCount++;
                updatePrice();
            } else {
                Toast.makeText(requireContext(), "Only " + selectedBook.getAvailable() + " copies available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            
            if (selected.after(lendDate)) {
                dueDate.set(year, month, dayOfMonth);
                updateDateDisplays();
                updatePrice();
            } else {
                Toast.makeText(requireContext(), "Due date must be after today", Toast.LENGTH_SHORT).show();
            }
        }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.getDatePicker().setMinDate(lendDate.getTimeInMillis() + 24 * 60 * 60 * 1000);
        datePickerDialog.show();
    }

    private void updateDateDisplays() {
        textViewLendDate.setText(displayFormat.format(lendDate.getTime()));
        textViewDueDate.setText(displayFormat.format(dueDate.getTime()));
    }

    private void updatePrice() {
        long diff = dueDate.getTimeInMillis() - lendDate.getTimeInMillis();
        long days = diff / (24 * 60 * 60 * 1000);
        if (days <= 0) days = 1;

        double subtotal = selectedBook.getPrice() * copyCount;
        double total = subtotal * days;

        textViewCopyCount.setText(String.valueOf(copyCount));
        textViewSubtotal.setText(String.format("Rs %.0f", subtotal));
        textViewTotalPrice.setText(String.format("Rs %.0f", total));
    }
}