package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
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
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.GetByIdRequest;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookInfoFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private EditText editTitle, editAuthor, editPrice, editTotal;
    private TextView textViewBookId, textViewStatus;
    private Spinner spinnerCategory, spinnerLanguage, spinnerStatus;
    private Button buttonSave;
    private android.widget.ProgressBar progressBar;
    private String bookId;
    private ImageButton buttonBack;

    private List<String> categories = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private List<String> statuses = Arrays.asList("Available", "Limited", "Out of Stock");

    public static BookInfoFragment newInstance(String bookId) {
        BookInfoFragment fragment = new BookInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
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
        
        if (getArguments() != null) {
            bookId = getArguments().getString(ARG_BOOK_ID);
        }

        initViews(view);
        fetchFilterOptions();
        fetchBookDetails();
        setupClickListeners();
    }

    private void initViews(View view) {
        editTitle = view.findViewById(R.id.editTitle);
        editAuthor = view.findViewById(R.id.editAuthor);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        editPrice = view.findViewById(R.id.editPrice);
        editTotal = view.findViewById(R.id.editTotal);
        textViewBookId = view.findViewById(R.id.textViewBookId);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        buttonSave = view.findViewById(R.id.buttonSave);
        progressBar = view.findViewById(R.id.progressBar);
        buttonBack = view.findViewById(R.id.buttonBack);
    }

    private void fetchFilterOptions() {
        com.xlms.librarymanagement.api.ApiService apiService = ApiClient.getApiService(requireContext());
        
        apiService.getDistinctValues(new com.xlms.librarymanagement.api.ColumnRequest(java.util.Collections.singletonList("Category")))
            .enqueue(new Callback<List<com.google.gson.JsonObject>>() {
                @Override
                public void onResponse(Call<List<com.google.gson.JsonObject>> call, Response<List<com.google.gson.JsonObject>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (com.google.gson.JsonObject obj : response.body()) {
                            if (obj.has("Category")) categories.add(obj.get("Category").getAsString());
                        }
                        setupSpinners();
                    }
                }
                @Override public void onFailure(Call<List<com.google.gson.JsonObject>> call, Throwable t) {}
            });

        apiService.getDistinctValues(new com.xlms.librarymanagement.api.ColumnRequest(java.util.Collections.singletonList("Language")))
            .enqueue(new Callback<List<com.google.gson.JsonObject>>() {
                @Override
                public void onResponse(Call<List<com.google.gson.JsonObject>> call, Response<List<com.google.gson.JsonObject>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (com.google.gson.JsonObject obj : response.body()) {
                            if (obj.has("Language")) languages.add(obj.get("Language").getAsString());
                        }
                        setupSpinners();
                    }
                }
                @Override public void onFailure(Call<List<com.google.gson.JsonObject>> call, Throwable t) {}
            });
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(catAdapter);
        
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages);
        spinnerLanguage.setAdapter(langAdapter);
        
        ArrayAdapter<String> statAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(statAdapter);
    }

    private void fetchBookDetails() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext())
            .getBookById(new GetByIdRequest(bookId))
            .enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        populateFields(response.body().get(0));
                    }
                }
                @Override
                public void onFailure(Call<List<Book>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error fetching details", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void populateFields(Book book) {
        if (editTitle != null) editTitle.setText(book.getTitle());
        if (editAuthor != null) editAuthor.setText(book.getAuthor());
        if (editPrice != null) editPrice.setText(String.valueOf(book.getPrice()));
        if (editTotal != null) editTotal.setText(String.valueOf(book.getTotal()));
        if (textViewBookId != null) textViewBookId.setText(book.getBookId());
        if (textViewStatus != null) textViewStatus.setText(book.getStatus());

        setSpinnerSelection(spinnerCategory, categories, book.getCategory());
        setSpinnerSelection(spinnerLanguage, languages, book.getLanguage());
        setSpinnerSelection(spinnerStatus, statuses, book.getStatus());
    }

    private void setSpinnerSelection(Spinner spinner, List<String> list, String value) {
        int position = list.indexOf(value);
        if (position >= 0) spinner.setSelection(position);
    }

    private void setupClickListeners() {
        buttonSave.setOnClickListener(v -> saveChanges());
        buttonBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void saveChanges() {
        progressBar.setVisibility(View.VISIBLE);
        
        Book updatedBook = new Book(
            bookId, 
            editTitle.getText().toString(),
            editAuthor.getText().toString(),
            spinnerCategory.getSelectedItem().toString(),
            spinnerLanguage.getSelectedItem().toString(),
            Double.parseDouble(editPrice.getText().toString()),
            Integer.parseInt(editTotal.getText().toString()),
            0,
            spinnerStatus.getSelectedItem().toString()
        );

        ApiClient.getApiService(requireContext())
            .updateBook(updatedBook)
            .enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error saving changes", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
