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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Book;

public class BookInfoFragment extends Fragment {

    private static final String ARG_BOOK = "book_data";

    private TextView textViewTitle, textViewBookId;
    private EditText editTextTitle, editTextAuthor, editTextTotalCopies, editTextPrice, editTextPages;
    private Spinner spinnerCategory, spinnerLanguage, spinnerStatus;
    private Button buttonDelete, buttonEdit, buttonCancelEdit, buttonSave;
    private LinearLayout layoutViewActions, layoutEditActions;

    private Book currentBook;
    private boolean isEditMode = false;
    private OnBookInfoActionListener listener;

    public interface OnBookInfoActionListener {
        void onBookUpdated(Book book);
        void onBookDeleted(Book book);
        void onBack();
    }

    public void setOnBookInfoActionListener(OnBookInfoActionListener listener) {
        this.listener = listener;
    }

    public static BookInfoFragment newInstance(Book book) {
        BookInfoFragment fragment = new BookInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get book data
        if (getArguments() != null) {
            currentBook = (Book) getArguments().getSerializable(ARG_BOOK);
        }

        if (currentBook == null) {
            Toast.makeText(requireContext(), "Error: No book data", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onBack();
            return;
        }

        initViews(view);
        setupSpinners();
        populateFields();
        setupClickListeners();
    }

    private void initViews(View view) {
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewBookId = view.findViewById(R.id.textViewBookId);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextAuthor = view.findViewById(R.id.editTextAuthor);
        editTextTotalCopies = view.findViewById(R.id.editTextTotalCopies);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextPages = view.findViewById(R.id.editTextPages);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonCancelEdit = view.findViewById(R.id.buttonCancelEdit);
        buttonSave = view.findViewById(R.id.buttonSave);
        layoutViewActions = view.findViewById(R.id.layoutViewActions);
        layoutEditActions = view.findViewById(R.id.layoutEditActions);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.book_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.book_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.book_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void populateFields() {
        textViewBookId.setText("ID: " + currentBook.getBookId());
        editTextTitle.setText(currentBook.getTitle());
        editTextAuthor.setText(currentBook.getAuthor());
        editTextTotalCopies.setText(String.valueOf(currentBook.getTotal()));
        editTextPrice.setText(String.valueOf(currentBook.getPrice()));
        editTextPages.setText(String.valueOf(0)); // Pages not in model yet

        // Set spinner selections
        setSpinnerSelection(spinnerCategory, currentBook.getCategory());
        setSpinnerSelection(spinnerLanguage, currentBook.getLanguage());
        setSpinnerSelection(spinnerStatus, currentBook.getStatus());
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

    private void setupClickListeners() {
        buttonEdit.setOnClickListener(v -> {
            isEditMode = true;
            enableEditing(true);
        });

        buttonCancelEdit.setOnClickListener(v -> {
            isEditMode = false;
            enableEditing(false);
            populateFields(); // Reset fields
        });

        buttonSave.setOnClickListener(v -> {
            if (validateAndSave()) {
                isEditMode = false;
                enableEditing(false);
            }
        });

        buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void enableEditing(boolean enabled) {
        editTextTitle.setEnabled(enabled);
        editTextAuthor.setEnabled(enabled);
        editTextTotalCopies.setEnabled(enabled);
        editTextPrice.setEnabled(enabled);
        editTextPages.setEnabled(enabled);
        spinnerCategory.setEnabled(enabled);
        spinnerLanguage.setEnabled(enabled);
        spinnerStatus.setEnabled(enabled);

        if (enabled) {
            layoutViewActions.setVisibility(View.GONE);
            layoutEditActions.setVisibility(View.VISIBLE);
            textViewTitle.setText("Edit Book");
        } else {
            layoutViewActions.setVisibility(View.VISIBLE);
            layoutEditActions.setVisibility(View.GONE);
            textViewTitle.setText("Book Information");
        }
    }

    private boolean validateAndSave() {
        String title = editTextTitle.getText().toString().trim();
        String author = editTextAuthor.getText().toString().trim();
        String totalCopiesStr = editTextTotalCopies.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(author)) {
            editTextAuthor.setError("Author is required");
            editTextAuthor.requestFocus();
            return false;
        }

        int totalCopies = 0;
        if (!TextUtils.isEmpty(totalCopiesStr)) {
            totalCopies = Integer.parseInt(totalCopiesStr);
        }

        double price = 0.0;
        if (!TextUtils.isEmpty(priceStr)) {
            price = Double.parseDouble(priceStr);
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String language = spinnerLanguage.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // Update book
        currentBook = new Book(
            currentBook.getBookId(),
            title,
            author,
            category,
            language,
            price,
            totalCopies,
            totalCopies, // Available = Total for simplicity
            status
        );

        if (listener != null) {
            listener.onBookUpdated(currentBook);
        }

        Toast.makeText(requireContext(), "Book updated successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Book")
            .setMessage("Are you sure you want to delete \"" + currentBook.getTitle() + "\"?")
            .setPositiveButton("Delete", (dialog, which) -> {
                if (listener != null) {
                    listener.onBookDeleted(currentBook);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
