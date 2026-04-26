package com.xlms.librarymanagement.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.xlms.librarymanagement.model.BookInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageBooksFragment extends Fragment {

    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private List<Book> masterBookList;
    private TextView textViewTotalBooks;
    private EditText editTextSearch;
    private LinearLayout skeletonContainer;

    private List<String> categoriesList = new ArrayList<>();
    private List<String> statusesList = new ArrayList<>();
    private List<String> languagesList = new ArrayList<>();
    private String currentCategory = "All";
    private String currentStatus = "All";
    private String currentLanguage = "All";
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
        fetchBooks();
        fetchFilterOptions();
        setupSearch();
        setupClickListeners();
        
        applyFilters();
    }

    private void showSkeleton(boolean show) {
        if (skeletonContainer == null) return;
        if (show) {
            skeletonContainer.removeAllViews();
            skeletonContainer.setVisibility(View.VISIBLE);
            recyclerViewBooks.setVisibility(View.GONE);
            
            android.view.animation.Animation shimmerAnim = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.shimmer_animation);
            for (int i = 0; i < 5; i++) {
                View skeleton = LayoutInflater.from(requireContext()).inflate(R.layout.layout_skeleton_book_item, skeletonContainer, false);
                View shimmerView = skeleton.findViewById(R.id.shimmerView);
                if (shimmerView != null) shimmerView.startAnimation(shimmerAnim);
                skeletonContainer.addView(skeleton);
            }
        } else {
            skeletonContainer.setVisibility(View.GONE);
            recyclerViewBooks.setVisibility(View.VISIBLE);
        }
    }

    private void fetchBooks() {
        showSkeleton(true);
        com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext()).getBooks().enqueue(new retrofit2.Callback<List<Book>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Book>> call, retrofit2.Response<List<Book>> response) {
                showSkeleton(false);
                if (response.isSuccessful() && response.body() != null) {
                    masterBookList.clear();
                    masterBookList.addAll(response.body());
                    applyFilters();
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch books", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Book>> call, Throwable t) {
                showSkeleton(false);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFilterOptions() {
        com.xlms.librarymanagement.api.ApiService apiService = com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext());
        
        // Fetch Categories
        apiService.getDistinctValues(new com.xlms.librarymanagement.api.ColumnRequest(java.util.Collections.singletonList("Category")))
                .enqueue(new retrofit2.Callback<List<com.google.gson.JsonObject>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.google.gson.JsonObject>> call, retrofit2.Response<List<com.google.gson.JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList.clear();
                    categoriesList.add("All");
                    for (com.google.gson.JsonObject obj : response.body()) {
                        if (obj.has("Category")) categoriesList.add(obj.get("Category").getAsString());
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<com.google.gson.JsonObject>> call, Throwable t) {}
        });

        // Fetch Statuses
        apiService.getDistinctValues(new com.xlms.librarymanagement.api.ColumnRequest(java.util.Collections.singletonList("Status")))
                .enqueue(new retrofit2.Callback<List<com.google.gson.JsonObject>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.google.gson.JsonObject>> call, retrofit2.Response<List<com.google.gson.JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusesList.clear();
                    statusesList.add("All");
                    for (com.google.gson.JsonObject obj : response.body()) {
                        if (obj.has("Status")) statusesList.add(obj.get("Status").getAsString());
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<com.google.gson.JsonObject>> call, Throwable t) {}
        });

        // Fetch Languages
        apiService.getDistinctValues(new com.xlms.librarymanagement.api.ColumnRequest(java.util.Collections.singletonList("Language")))
                .enqueue(new retrofit2.Callback<List<com.google.gson.JsonObject>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.google.gson.JsonObject>> call, retrofit2.Response<List<com.google.gson.JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    languagesList.clear();
                    languagesList.add("All");
                    for (com.google.gson.JsonObject obj : response.body()) {
                        if (obj.has("Language")) languagesList.add(obj.get("Language").getAsString());
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<com.google.gson.JsonObject>> call, Throwable t) {}
        });
    }
    private void initViews(View view) {
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        textViewTotalBooks = view.findViewById(R.id.textViewTotalBooks);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        skeletonContainer = view.findViewById(R.id.skeletonContainer);
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
                fetchBooks(); // Refresh from server
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
        BookInfoFragment fragment = BookInfoFragment.newInstance(book.getBookId());
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
            boolean matchLanguage = true;

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

            if (!currentLanguage.equals("All")) {
                matchLanguage = book.getLanguage().equalsIgnoreCase(currentLanguage);
            }

            if (matchSearch && matchCategory && matchStatus && matchLanguage) {
                filteredList.add(book);
            }
        }

        bookAdapter.submitList(filteredList);
        textViewTotalBooks.setText(String.valueOf(filteredList.size()));
    }

    private void showFilterDialog() {
        String[] options = {"Filter by Category", "Filter by Status", "Filter by Language", "Reset All Filters"};
        new AlertDialog.Builder(requireContext())
            .setTitle("Manage Filters")
            .setItems(options, (dialog, which) -> {
                if (which == 0) showCategoryDialog();
                else if (which == 1) showStatusDialog();
                else if (which == 2) showLanguageDialog();
                else resetAllFilters();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showCategoryDialog() {
        if (categoriesList.isEmpty()) {
            Toast.makeText(requireContext(), "Categories still loading...", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] categories = categoriesList.toArray(new String[0]);
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
        if (statusesList.isEmpty()) {
            Toast.makeText(requireContext(), "Statuses still loading...", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] statuses = statusesList.toArray(new String[0]);
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

    private void showLanguageDialog() {
        if (languagesList.isEmpty()) {
            Toast.makeText(requireContext(), "Languages still loading...", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] languages = languagesList.toArray(new String[0]);
        new AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setItems(languages, (dialog, which) -> {
                currentLanguage = languages[which];
                currentCategory = "All";
                currentStatus = "All";
                editTextSearch.setText("");
                applyFilters();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void resetAllFilters() {
        currentCategory = "All";
        currentStatus = "All";
        currentLanguage = "All";
        currentSearch = "";
        editTextSearch.setText("");
        applyFilters();
        Toast.makeText(requireContext(), "Filters reset", Toast.LENGTH_SHORT).show();
    }
}
