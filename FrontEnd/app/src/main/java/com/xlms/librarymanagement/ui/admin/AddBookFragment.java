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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Book;

import java.util.UUID;

public class AddBookFragment extends Fragment {

    private EditText editTextTitle, editTextAuthor, editTextTotalCopies, editTextPrice, editTextPages;
    private Spinner spinnerCategory, spinnerLanguage, spinnerStatus;
    private Button buttonCancel, buttonSave;
    private TextView textViewTitle;

    private OnBookActionListener listener;

    public interface OnBookActionListener {
        void onBookAdded(Book book);
        void onCancel();
    }

    public void setOnBookActionListener(OnBookActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupSpinners();
        setupClickListeners();
    }

    private void initViews(View view) {
        textViewTitle = view.findViewById(R.id.textViewTitle);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextAuthor = view.findViewById(R.id.editTextAuthor);
        editTextTotalCopies = view.findViewById(R.id.editTextTotalCopies);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextPages = view.findViewById(R.id.editTextPages);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonSave = view.findViewById(R.id.buttonSave);
    }

    private void setupSpinners() {
        // Category Spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.book_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Language Spinner
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.book_languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        // Status Spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.book_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupClickListeners() {
        buttonCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel();
        });

        buttonSave.setOnClickListener(v -> {
            if (validateAndSave()) {
                if (listener != null) listener.onCancel();
            }
        });
    }

    private boolean validateAndSave() {
        String title = editTextTitle.getText().toString().trim();
        String author = editTextAuthor.getText().toString().trim();
        String totalCopiesStr = editTextTotalCopies.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String pagesStr = editTextPages.getText().toString().trim();

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

        int pages = 0;
        if (!TextUtils.isEmpty(pagesStr)) {
            pages = Integer.parseInt(pagesStr);
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String language = spinnerLanguage.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // Generate Book ID
        String bookId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Book newBook = new Book(bookId, title, author, category, language, price, totalCopies, totalCopies, status);

        if (listener != null) {
            listener.onBookAdded(newBook);
        }

        Toast.makeText(requireContext(), "Book added successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
