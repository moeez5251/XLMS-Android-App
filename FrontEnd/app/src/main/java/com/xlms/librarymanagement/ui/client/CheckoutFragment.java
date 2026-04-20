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

public class CheckoutFragment extends Fragment {

    private Book selectedBook;
    private Calendar lendDate = Calendar.getInstance();
    private Calendar dueDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());

    private TextView textViewLendDate, textViewDueDate, textViewTotalPrice, textViewSubtotal;

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
        // Default due date: tomorrow
        dueDate.add(Calendar.DAY_OF_YEAR, 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        initViews(view);
        setupBookDetails(view);
        setupDatePickers(view);

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.buttonCheckOut).setOnClickListener(v -> handleCheckout());

        return view;
    }

    private void handleCheckout() {
        if (selectedBook == null) return;

        SessionManager sessionManager = new SessionManager(requireContext());
        String email = sessionManager.getUserEmail();
        String name = sessionManager.getUserName();
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split(" ");
            if (parts.length > 0) initials += parts[0].charAt(0);
            if (parts.length > 1) initials += parts[1].charAt(0);
        }

        LendedBook lendedBook = new LendedBook(
                (int) System.currentTimeMillis(), // Fake numeric ID
                email,
                name,
                initials,
                selectedBook.getTitle(),
                selectedBook.getAuthor(),
                selectedBook.getCategory(),
                1,
                dateFormat.format(lendDate.getTime()),
                dateFormat.format(dueDate.getTime()),
                "Not Returned"
        );

        LendingRepository repository = new LendingRepository(requireContext());
        repository.addLending(lendedBook);

        Toast.makeText(getContext(), "Book Checked Out Successfully!", Toast.LENGTH_LONG).show();
        
        // Go back to dashboard or show success
        getParentFragmentManager().popBackStack();
    }

    private void initViews(View view) {
        textViewLendDate = view.findViewById(R.id.textViewLendDate);
        textViewDueDate = view.findViewById(R.id.textViewDueDate);
        textViewSubtotal = view.findViewById(R.id.textViewSubtotal);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);

        updateDateDisplays();
    }

    private void setupBookDetails(View view) {
        if (selectedBook == null) return;

        ((TextView) view.findViewById(R.id.textViewTitle)).setText(selectedBook.getTitle());
        ((TextView) view.findViewById(R.id.textViewAuthor)).setText(selectedBook.getAuthor());
        ((TextView) view.findViewById(R.id.textViewPricePerCopy)).setText(String.format("Rs %.0f", selectedBook.getPrice()));
        ((TextView) view.findViewById(R.id.textViewLanguage)).setText(selectedBook.getLanguage());

        textViewSubtotal.setText(String.format("Rs %.0f", selectedBook.getPrice()));
        textViewTotalPrice.setText(String.format("Rs %.0f", selectedBook.getPrice()));
    }

    private void setupDatePickers(View view) {
        view.findViewById(R.id.layoutLendDatePicker).setOnClickListener(v -> showDatePicker(true));
        view.findViewById(R.id.layoutDueDatePicker).setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isLendDate) {
        Calendar current = isLendDate ? lendDate : dueDate;
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            if (isLendDate) {
                lendDate.set(year, month, dayOfMonth);
            } else {
                dueDate.set(year, month, dayOfMonth);
            }
            updateDateDisplays();
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.show();
    }

    private void updateDateDisplays() {
        textViewLendDate.setText(dateFormat.format(lendDate.getTime()));
        textViewDueDate.setText(dateFormat.format(dueDate.getTime()));
    }
}