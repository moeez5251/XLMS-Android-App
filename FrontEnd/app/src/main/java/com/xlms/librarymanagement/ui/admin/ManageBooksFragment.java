package com.xlms.librarymanagement.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.BookAdapter;
import com.xlms.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageBooksFragment extends Fragment {

    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private List<Book> masterBookList;
    private TextView textViewTotalBooks;
    private EditText editTextSearch;

    private String currentCategory = "All";
    private String currentStatus = "All";
    private String currentSearch = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadDummyData();
        setupSearch();
        setupClickListeners();
        
        applyFilters();
    }

    private void initViews(View view) {
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        textViewTotalBooks = view.findViewById(R.id.textViewTotalBooks);
        editTextSearch = view.findViewById(R.id.editTextSearch);
    }

    private void setupRecyclerView() {
        masterBookList = new ArrayList<>();
        bookAdapter = new BookAdapter(new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                openBookInfoFragment(book);
            }

            @Override
            public void onBookLongClick(Book book) {
                openBookInfoFragment(book);
            }
        });
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void loadDummyData() {
        masterBookList.clear();
        masterBookList.add(new Book("14-88219-X", "Rework", "Jason Fried", "Business", "English", 15.00, 8, 8, "Available"));
        masterBookList.add(new Book("07-43203-1", "The Great Gatsby", "F. Scott Fitzgerald", "Classic", "English", 12.50, 12, 4, "Limited"));
        masterBookList.add(new Book("99-10293-A", "Thinking, Fast and Slow", "Daniel Kahneman", "Science", "English", 22.00, 5, 0, "Out of Stock"));
        masterBookList.add(new Book("42-11882-X", "Sapiens", "Yuval Noah Harari", "History", "Hebrew", 18.99, 15, 11, "Available"));
        masterBookList.add(new Book("21-33490-C", "The Alchemist", "Paulo Coelho", "Fiction", "Portuguese", 10.00, 20, 20, "Available"));
        masterBookList.add(new Book("55-11223-B", "Atomic Habits", "James Clear", "Self-Help", "English", 16.00, 10, 2, "Limited"));
        masterBookList.add(new Book("88-99001-Z", "1984", "George Orwell", "Classic", "English", 9.99, 15, 15, "Available"));
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString().trim().toLowerCase(Locale.ROOT);
                applyFilters();
            }
        });
    }

    private void setupClickListeners() {
        Button buttonAddBook = requireView().findViewById(R.id.buttonAddBook);
        if (buttonAddBook != null) {
            buttonAddBook.setOnClickListener(v -> openAddBookFragment());
        }

        Button buttonFilters = requireView().findViewById(R.id.buttonFilters);
        if (buttonFilters != null) {
            buttonFilters.setOnClickListener(v -> showFilterDialog());
        }
    }

    // Navigation Methods
    private void openAddBookFragment() {
        AddBookFragment fragment = new AddBookFragment();
        fragment.setOnBookActionListener(new AddBookFragment.OnBookActionListener() {
            @Override
            public void onBookAdded(Book book) {
                masterBookList.add(0, book);
                applyFilters();
                closeDetailFragment();
                Toast.makeText(requireContext(), "Book added: " + book.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                closeDetailFragment();
            }
        });

        openDetailFragment(fragment);
    }

    private void openBookInfoFragment(Book book) {
        BookInfoFragment fragment = BookInfoFragment.newInstance(book);
        fragment.setOnBookInfoActionListener(new BookInfoFragment.OnBookInfoActionListener() {
            @Override
            public void onBookUpdated(Book updatedBook) {
                int index = masterBookList.indexOf(book);
                if (index >= 0) {
                    masterBookList.set(index, updatedBook);
                    applyFilters();
                }
                closeDetailFragment();
                Toast.makeText(requireContext(), "Book updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBookDeleted(Book book) {
                masterBookList.remove(book);
                applyFilters();
                closeDetailFragment();
                Toast.makeText(requireContext(), "Book deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBack() {
                closeDetailFragment();
            }
        });

        openDetailFragment(fragment);
    }

    private void openDetailFragment(Fragment fragment) {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).openDetailScreen(fragment);
        }
    }

    private void closeDetailFragment() {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).closeDetailScreen();
        }
    }

    private void applyFilters() {
        List<Book> filteredList = new ArrayList<>();

        for (Book book : masterBookList) {
            boolean matchSearch = true;
            boolean matchCategory = true;
            boolean matchStatus = true;

            if (!currentSearch.isEmpty()) {
                String title = book.getTitle().toLowerCase(Locale.ROOT);
                String author = book.getAuthor().toLowerCase(Locale.ROOT);
                String id = book.getBookId().toLowerCase(Locale.ROOT);
                String cat = book.getCategory().toLowerCase(Locale.ROOT);
                String lang = book.getLanguage().toLowerCase(Locale.ROOT);
                String stat = book.getStatus().toLowerCase(Locale.ROOT);

                matchSearch = title.contains(currentSearch) || author.contains(currentSearch) ||
                              id.contains(currentSearch) || cat.contains(currentSearch) ||
                              lang.contains(currentSearch) || stat.contains(currentSearch) ||
                              String.valueOf(book.getPrice()).contains(currentSearch) ||
                              String.valueOf(book.getTotal()).contains(currentSearch) ||
                              String.valueOf(book.getAvailable()).contains(currentSearch);
            }

            if (!currentCategory.equals("All")) {
                matchCategory = book.getCategory().equalsIgnoreCase(currentCategory);
            }

            if (!currentStatus.equals("All")) {
                matchStatus = book.getStatus().equalsIgnoreCase(currentStatus);
            }

            if (matchSearch && matchCategory && matchStatus) {
                filteredList.add(book);
            }
        }

        bookAdapter.submitList(filteredList);
        textViewTotalBooks.setText(String.valueOf(filteredList.size()));
    }

    private void showFilterDialog() {
        String[] options = {"Filter by Category", "Filter by Status", "Reset All Filters"};
        new AlertDialog.Builder(requireContext())
            .setTitle("Manage Filters")
            .setItems(options, (dialog, which) -> {
                if (which == 0) showCategoryDialog();
                else if (which == 1) showStatusDialog();
                else resetAllFilters();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showCategoryDialog() {
        String[] categories = {"All", "Business", "Classic", "Science", "History", "Fiction", "Self-Help"};
        new AlertDialog.Builder(requireContext())
            .setTitle("Select Category")
            .setItems(categories, (dialog, which) -> {
                currentCategory = categories[which];
                currentStatus = "All";
                editTextSearch.setText("");
                applyFilters();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showStatusDialog() {
        String[] statuses = {"All", "Available", "Limited", "Out of Stock"};
        new AlertDialog.Builder(requireContext())
            .setTitle("Select Status")
            .setItems(statuses, (dialog, which) -> {
                currentStatus = statuses[which];
                currentCategory = "All";
                editTextSearch.setText("");
                applyFilters();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void resetAllFilters() {
        currentCategory = "All";
        currentStatus = "All";
        currentSearch = "";
        editTextSearch.setText("");
        applyFilters();
        Toast.makeText(requireContext(), "Filters reset", Toast.LENGTH_SHORT).show();
    }
}
