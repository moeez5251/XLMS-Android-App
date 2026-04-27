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
        String[] categories = {
            "Fiction", "Non-fiction", "Fantasy", "Science Fiction", "Mystery", "Thriller",
            "Romance", "Historical", "Biography", "Autobiography", "Self-help", "Health & Wellness",
            "Science", "Mathematics", "Technology", "Business", "Economics", "Politics", "Philosophy",
            "Psychology", "Religion", "Spirituality", "Art & Design", "Photography", "Travel", "Cooking",
            "Children's", "Young Adult", "Comics & Graphic Novels", "Education", "Poetry", "Drama", "Law",
            "Language & Grammar", "Horror", "Adventure", "Humor", "Sports", "Music", "Parenting", "True Crime"
        };
        
        String[] languages = {
            "English", "Urdu", "Arabic", "Spanish", "French", "German", "Chinese", "Japanese", "Korean",
            "Hindi", "Russian", "Portuguese", "Italian", "Turkish", "Bengali", "Punjabi", "Persian", "Greek",
            "Swahili", "Thai"
        };

        String[] statuses = {"Available", "Reserved", "Out of stock"};

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(catAdapter);
        
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages);
        spinnerLanguage.setAdapter(langAdapter);
        
        ArrayAdapter<String> statAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(statAdapter);
    }

    private void setupClickListeners() {
        buttonCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel();
        });

        buttonSave.setOnClickListener(v -> {
            validateAndSave();
        });
    }

    private void saveBookToApi(Book book) {
        buttonSave.setEnabled(false);
        buttonSave.setText("Adding...");

        com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext())
            .insertBook(book)
            .enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.MessageResponse>() {
                @Override
                public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.MessageResponse> response) {
                    buttonSave.setEnabled(true);
                    buttonSave.setText("Save");
                    if (!isAdded()) return;
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Book " + book.getTitle() + " added!", Toast.LENGTH_SHORT).show();
                        // Notify that we added a book successfully
                        if (listener != null) listener.onBookAdded(book);
                    } else {
                        Toast.makeText(requireContext(), "Failed to add book", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.MessageResponse> call, Throwable t) {
                    buttonSave.setEnabled(true);
                    buttonSave.setText("Save");
                    if (!isAdded()) return; // Safety check
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private boolean validateAndSave() {
        String title = editTextTitle.getText().toString().trim();
        String author = editTextAuthor.getText().toString().trim();
        String totalCopiesStr = editTextTotalCopies.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String pagesStr = editTextPages.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author) || TextUtils.isEmpty(totalCopiesStr) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(pagesStr)) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        int totalCopies = Integer.parseInt(totalCopiesStr);
        double price = Double.parseDouble(priceStr);
        int pages = Integer.parseInt(pagesStr);
        
        String category = spinnerCategory.getSelectedItem().toString();
        String language = spinnerLanguage.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        Book newBook = new Book(null, title, author, category, language, price, totalCopies, totalCopies, status);
        newBook.setPages(pages);

        saveBookToApi(newBook);
        return true;
    }
}
