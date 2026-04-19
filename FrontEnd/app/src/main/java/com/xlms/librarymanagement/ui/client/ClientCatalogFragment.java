package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientCatalogFragment extends Fragment {

    private RecyclerView recyclerViewBooks;
    private BookCatalogAdapter bookAdapter;
    private List<Book> masterBookList;
    private EditText editTextSearch;

    private String currentLanguage = "All";
    private String currentAuthor = "All";
    private String currentAvailability = "All";

    private int currentPage = 1;
    private final int PAGE_SIZE = 6; // 3 rows of 2 columns
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private List<Book> filteredBookList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadDummyData();
        setupSearch();
        setupFilterChips();
    }

    private void initViews(View view) {
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        editTextSearch = view.findViewById(R.id.editTextSearch);
    }

    private void setupRecyclerView() {
        masterBookList = new ArrayList<>();
        bookAdapter = new BookCatalogAdapter(requireContext(), book -> {
            ClientBookInfoFragment infoFragment = ClientBookInfoFragment.newInstance(book);
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .replace(R.id.fragment_container, infoFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Set up GridLayoutManager with responsive columns
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int spanCount;
        if (screenWidth >= 1200) {
            spanCount = 4;
        } else if (screenWidth >= 800) {
            spanCount = 3;
        } else {
            spanCount = 2;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        recyclerViewBooks.setLayoutManager(layoutManager);
        recyclerViewBooks.setAdapter(bookAdapter);

        // Add scroll listener for pagination
        recyclerViewBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // check for scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loadNextPage();
                        }
                    }
                }
            }
        });
    }

    private void loadNextPage() {
        isLoading = true;
        // Simulate a small delay for loading effect
        recyclerViewBooks.postDelayed(() -> {
            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, filteredBookList.size());

            if (start < filteredBookList.size()) {
                List<Book> nextItems = filteredBookList.subList(0, end);
                bookAdapter.submitList(new ArrayList<>(nextItems));
                currentPage++;
                if (end >= filteredBookList.size()) {
                    isLastPage = true;
                }
            } else {
                isLastPage = true;
            }
            isLoading = false;
        }, 500);
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
        masterBookList.add(new Book("77-66554-D", "To Kill a Mockingbird", "Harper Lee", "Classic", "English", 14.99, 10, 10, "Available"));
        masterBookList.add(new Book("33-44771-E", "Pride and Prejudice", "Jane Austen", "Romance", "English", 13.50, 8, 6, "Available"));
        masterBookList.add(new Book("11-22334-F", "The Hobbit", "J.R.R. Tolkien", "Fantasy", "English", 17.99, 12, 3, "Limited"));

        applyFilters();
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterChips() {
        // Language filter chip
        View languageChip = requireView().findViewById(R.id.chipLanguage);
        if (languageChip != null) {
            languageChip.setOnClickListener(v -> showLanguageDialog());
        }

        // Author filter chip
        View authorChip = requireView().findViewById(R.id.chipAuthor);
        if (authorChip != null) {
            authorChip.setOnClickListener(v -> showAuthorDialog());
        }

        // Availability filter chip
        View availabilityChip = requireView().findViewById(R.id.chipAvailability);
        if (availabilityChip != null) {
            availabilityChip.setOnClickListener(v -> showAvailabilityDialog());
        }

        // Reset filters
        TextView resetFilters = requireView().findViewById(R.id.textViewResetFilters);
        if (resetFilters != null) {
            resetFilters.setOnClickListener(v -> resetAllFilters());
        }
    }

    private void showLanguageDialog() {
        String[] languages = {"All", "English", "Hebrew", "Portuguese", "Spanish", "French", "German"};
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Language")
                .setItems(languages, (dialog, which) -> {
                    currentLanguage = languages[which];
                    updateFilterChip(R.id.textViewLanguageLabel, "Language: " + currentLanguage);
                    applyFilters();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAuthorDialog() {
        String[] authors = {"All", "Jason Fried", "F. Scott Fitzgerald", "Daniel Kahneman",
                           "Yuval Noah Harari", "Paulo Coelho", "James Clear", "George Orwell",
                           "Harper Lee", "Jane Austen", "J.R.R. Tolkien"};
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Author")
                .setItems(authors, (dialog, which) -> {
                    currentAuthor = authors[which];
                    updateFilterChip(R.id.textViewAuthorLabel, "Author: " + currentAuthor);
                    applyFilters();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAvailabilityDialog() {
        String[] availabilities = {"All", "Available", "Limited", "Out of Stock"};
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Availability")
                .setItems(availabilities, (dialog, which) -> {
                    currentAvailability = availabilities[which];
                    updateFilterChip(R.id.textViewAvailabilityLabel, "Availability: " + currentAvailability);

                    // Update availability chip appearance
                    View availabilityChip = requireView().findViewById(R.id.chipAvailability);
                    if (availabilityChip != null) {
                        int bgColor;
                        switch (currentAvailability) {
                            case "Available":
                                bgColor = R.color.primary;
                                break;
                            case "Limited":
                                bgColor = R.color.status_limited_bg;
                                break;
                            case "Out of Stock":
                                bgColor = R.color.status_out_of_stock_bg;
                                break;
                            default:
                                bgColor = R.color.surface_container_lowest;
                                break;
                        }
                        availabilityChip.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(
                                        ContextCompat.getColor(requireContext(), bgColor)));

                        // Update text color and icon
                        TextView availabilityLabel = requireView().findViewById(R.id.textViewAvailabilityLabel);
                        if (availabilityLabel != null) {
                            availabilityLabel.setTextColor(
                                            currentAvailability.equals("All")
                                                    ? ContextCompat.getColor(requireContext(), R.color.on_surface_variant)
                                                    : android.graphics.Color.WHITE);
                        }

                        android.widget.ImageView icon = requireView().findViewById(android.R.id.icon); // This won't work, we need to find the actual image view
                        // For simplicity, we'll just update the text and background
                    }

                    applyFilters();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateFilterChip(int textViewId, String text) {
        TextView textView = requireView().findViewById(textViewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    private void resetAllFilters() {
        currentLanguage = "All";
        currentAuthor = "All";
        currentAvailability = "All";

        // Reset chip appearances
        updateFilterChip(R.id.textViewLanguageLabel, "Language: All");
        updateFilterChip(R.id.textViewAuthorLabel, "Author: All");
        updateFilterChip(R.id.textViewAvailabilityLabel, "Availability: All");

        // Reset availability chip to primary
        View availabilityChip = requireView().findViewById(R.id.chipAvailability);
        if (availabilityChip != null) {
            availabilityChip.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.primary)));

            TextView availabilityLabel = requireView().findViewById(R.id.textViewAvailabilityLabel);
            if (availabilityLabel != null) {
                availabilityLabel.setTextColor(android.graphics.Color.WHITE);
            }
        }

        editTextSearch.setText("");
        applyFilters();
        Toast.makeText(requireContext(), "Filters reset", Toast.LENGTH_SHORT).show();
    }

    private void applyFilters() {
        filteredBookList.clear();

        for (Book book : masterBookList) {
            boolean matchSearch = true;
            boolean matchLanguage = true;
            boolean matchAuthor = true;
            boolean matchAvailability = true;

            // Search filter
            if (!editTextSearch.getText().toString().trim().isEmpty()) {
                String searchTerm = editTextSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
                String title = book.getTitle().toLowerCase(Locale.ROOT);
                String author = book.getAuthor().toLowerCase(Locale.ROOT);
                String id = book.getBookId().toLowerCase(Locale.ROOT);
                String category = book.getCategory().toLowerCase(Locale.ROOT);
                String language = book.getLanguage().toLowerCase(Locale.ROOT);
                String status = book.getStatus().toLowerCase(Locale.ROOT);

                matchSearch = title.contains(searchTerm) || author.contains(searchTerm) ||
                              id.contains(searchTerm) || category.contains(searchTerm) ||
                              language.contains(searchTerm) || status.contains(searchTerm);
            }

            // Language filter
            if (!currentLanguage.equals("All")) {
                matchLanguage = book.getLanguage().equalsIgnoreCase(currentLanguage);
            }

            // Author filter
            if (!currentAuthor.equals("All")) {
                matchAuthor = book.getAuthor().equalsIgnoreCase(currentAuthor);
            }

            // Availability filter
            if (!currentAvailability.equals("All")) {
                matchAvailability = book.getStatus().equalsIgnoreCase(currentAvailability);
            }

            if (matchSearch && matchLanguage && matchAuthor && matchAvailability) {
                filteredBookList.add(book);
            }
        }

        // Reset pagination for new filter results
        currentPage = 1;
        isLastPage = false;
        loadNextPage();
    }
}