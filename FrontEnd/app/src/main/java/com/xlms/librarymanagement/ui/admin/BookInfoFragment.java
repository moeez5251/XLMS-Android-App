package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private EditText editTitle, editAuthor, editPrice, editTotal, editPages;
    private TextView textViewBookId;
    private Spinner spinnerCategory, spinnerLanguage, spinnerStatus;
    private Button buttonSave, buttonDelete;
    private android.widget.ProgressBar progressBar;
    private String bookId;
    private ImageButton buttonBack;
    private LinearLayout contentLayout, shimmerLayout;

    private final List<String> languages = Arrays.asList(
            "English", "Urdu", "Arabic", "Spanish", "French", "German", "Chinese", "Japanese", "Korean",
            "Hindi", "Russian", "Portuguese", "Italian", "Turkish", "Bengali", "Punjabi", "Persian", "Greek",
            "Swahili", "Thai"
    );

    private final List<String> categories = Arrays.asList(
            "Fiction", "Non-fiction", "Fantasy", "Science Fiction", "Mystery", "Thriller",
            "Romance", "Historical", "Biography", "Autobiography", "Self-help", "Health & Wellness",
            "Science", "Mathematics", "Technology", "Business", "Economics", "Politics", "Philosophy",
            "Psychology", "Religion", "Spirituality", "Art & Design", "Photography", "Travel", "Cooking",
            "Children's", "Young Adult", "Comics & Graphic Novels", "Education", "Poetry", "Drama", "Law",
            "Language & Grammar", "Horror", "Adventure", "Humor", "Sports", "Music", "Parenting", "True Crime"
    );

    private final List<String> statuses = Arrays.asList("Available", "Reserved", "Out of stock");

    public interface OnBookChangedListener {
        void onBookChanged();
    }

    private OnBookChangedListener listener;

    public void setOnBookChangedListener(OnBookChangedListener listener) {
        this.listener = listener;
    }

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
        setupSpinners();
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
        editPages = view.findViewById(R.id.editPages);
        textViewBookId = view.findViewById(R.id.textViewBookId);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        progressBar = view.findViewById(R.id.progressBar);
        contentLayout = view.findViewById(R.id.contentLayout);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        buttonBack = view.findViewById(R.id.buttonBack);
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(catAdapter);
        
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages);
        spinnerLanguage.setAdapter(langAdapter);
        
        ArrayAdapter<String> statAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(statAdapter);
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

    private void fetchBookDetails() {
        showSkeleton(true);
        ApiClient.getApiService(requireContext())
            .getBookById(new GetByIdRequest(bookId))
            .enqueue(new Callback<List<Book>>() {
                @Override
                public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                    showSkeleton(false);
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        populateFields(response.body().get(0));
                    }
                }
                @Override
                public void onFailure(Call<List<Book>> call, Throwable t) {
                    showSkeleton(false);
                    Toast.makeText(requireContext(), "Error fetching details", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void populateFields(Book book) {
        editTitle.setText(book.getTitle());
        editAuthor.setText(book.getAuthor());
        editPrice.setText(String.valueOf((int)book.getPrice()));
        editTotal.setText(String.valueOf(book.getTotal()));
        editPages.setText(String.valueOf(book.getPages()));
        textViewBookId.setText(book.getBookId());

        setSpinnerSelection(spinnerCategory, categories, book.getCategory());
        setSpinnerSelection(spinnerLanguage, languages, book.getLanguage());
        setSpinnerSelection(spinnerStatus, statuses, book.getStatus());
    }

    private void setSpinnerSelection(Spinner spinner, List<String> list, String value) {
        int position = -1;
        for(int i=0; i<list.size(); i++) {
            if(list.get(i).equalsIgnoreCase(value)) {
                position = i;
                break;
            }
        }
        if (position >= 0) spinner.setSelection(position);
    }

    private void setupClickListeners() {
        buttonSave.setOnClickListener(v -> saveChanges());
        buttonBack.setOnClickListener(v -> requireActivity().onBackPressed());
        buttonDelete.setOnClickListener(v -> deleteBook());
    }

    private void saveChanges() {
        if (editTitle.getText().toString().isEmpty()) return;

        showSkeleton(true);
        
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
        updatedBook.setPages(Integer.parseInt(editPages.getText().toString()));

        ApiClient.getApiService(requireContext())
            .updateBook(updatedBook)
            .enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    showSkeleton(false);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onBookChanged();
                        requireActivity().onBackPressed();
                    } else {
                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    showSkeleton(false);
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void deleteBook() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Book")
                .setMessage("Are you sure you want to delete this book?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    showSkeleton(true);
                    List<String> ids = new ArrayList<>();
                    ids.add(bookId);
                    ApiClient.getApiService(requireContext()).deleteBook(ids).enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            showSkeleton(false);
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Book deleted", Toast.LENGTH_SHORT).show();
                                if (listener != null) listener.onBookChanged();
                                requireActivity().onBackPressed();
                            } else {
                                Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            showSkeleton(false);
                            Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
