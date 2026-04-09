package com.xlms.librarymanagement.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.BookInfo;
import com.xlms.librarymanagement.model.BookLending;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LendBookFragment extends Fragment {

    private static final String ARG_BOOK_INFO = "book_info";

    private EditText editTextLenderName, editTextEmail, editTextDueDate, editTextCopies, editTextFine;
    private Spinner spinnerBookTitle, spinnerCategory, spinnerAuthor;
    private TextView textViewCurrentDate;
    private ImageButton buttonBack;
    private Button buttonCancel, buttonLendBook;

    private BookInfo bookInfo;
    private OnLendBookActionListener listener;

    public interface OnLendBookActionListener {
        void onBookLended(BookLending lending);
        void onBack();
    }

    public static LendBookFragment newInstance(BookInfo book) {
        LendBookFragment fragment = new LendBookFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_INFO, book);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnLendBookActionListener(OnLendBookActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lend_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            bookInfo = (BookInfo) getArguments().getSerializable(ARG_BOOK_INFO);
        }

        initViews(view);
        setupSpinners();
        populateFields();
        setupClickListeners();
    }

    private void initViews(View view) {
        editTextLenderName = view.findViewById(R.id.editTextLenderName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextDueDate = view.findViewById(R.id.editTextDueDate);
        editTextCopies = view.findViewById(R.id.editTextCopies);
        editTextFine = view.findViewById(R.id.editTextFine);
        spinnerBookTitle = view.findViewById(R.id.spinnerBookTitle);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerAuthor = view.findViewById(R.id.spinnerAuthor);
        textViewCurrentDate = view.findViewById(R.id.textViewCurrentDate);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonLendBook = view.findViewById(R.id.buttonLendBook);
    }

    private void setupSpinners() {
        // Book Title Spinner
        List<String> bookTitles = Arrays.asList("Choose from the list", bookInfo != null ? bookInfo.getTitle() : "", "Modern Architecture", "The Republic");
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, bookTitles);
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBookTitle.setAdapter(titleAdapter);
        if (bookInfo != null) {
            spinnerBookTitle.setSelection(1); // Select the passed book
        }

        // Category Spinner
        List<String> categories = Arrays.asList("Choose from the list", "History", "Philosophy", "Social Sciences", "Business", "Productivity", "Self-Help");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        if (bookInfo != null) {
            int index = categories.indexOf(bookInfo.getCategory());
            if (index > 0) spinnerCategory.setSelection(index);
        }

        // Author Spinner
        List<String> authors = Arrays.asList("Choose from the list", "Prof. Elena Moretti", "David H. Stern", "A. G. Wright", bookInfo != null ? bookInfo.getAuthor() : "");
        ArrayAdapter<String> authorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, authors);
        authorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAuthor.setAdapter(authorAdapter);
        if (bookInfo != null) {
            int index = authors.indexOf(bookInfo.getAuthor());
            if (index > 0) spinnerAuthor.setSelection(index);
        }
    }

    private void populateFields() {
        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        textViewCurrentDate.setText(currentDate);
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });

        buttonCancel.setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });

        // Due Date Picker
        editTextDueDate.setOnClickListener(v -> showDatePicker());

        buttonLendBook.setOnClickListener(v -> {
            if (validateAndSubmit()) {
                Toast.makeText(requireContext(), "Book lended successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                editTextDueDate.setText(sdf.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private boolean validateAndSubmit() {
        String lenderName = editTextLenderName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String dueDate = editTextDueDate.getText().toString().trim();
        String copiesStr = editTextCopies.getText().toString().trim();
        String fineStr = editTextFine.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(lenderName)) {
            editTextLenderName.setError("Lender name is required");
            editTextLenderName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dueDate)) {
            editTextDueDate.setError("Due date is required");
            editTextDueDate.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(copiesStr) || Integer.parseInt(copiesStr) < 1) {
            editTextCopies.setError("Valid copies count is required");
            editTextCopies.requestFocus();
            return false;
        }

        // Create BookLending object
        BookLending lending = new BookLending();
        lending.setLenderName(lenderName);
        lending.setLenderEmail(email);
        lending.setBookTitle(bookInfo != null ? bookInfo.getTitle() : spinnerBookTitle.getSelectedItem().toString());
        lending.setBookCategory(spinnerCategory.getSelectedItem().toString());
        lending.setBookAuthor(bookInfo != null ? bookInfo.getAuthor() : spinnerAuthor.getSelectedItem().toString());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        lending.setIssuedDate(sdf.format(Calendar.getInstance().getTime()));
        lending.setDueDate(dueDate);
        lending.setCopiesLent(Integer.parseInt(copiesStr));
        lending.setPerDayFine(TextUtils.isEmpty(fineStr) ? 0.0 : Double.parseDouble(fineStr));

        // Pass to listener
        if (listener != null) {
            listener.onBookLended(lending);
        }

        return true;
    }
}
