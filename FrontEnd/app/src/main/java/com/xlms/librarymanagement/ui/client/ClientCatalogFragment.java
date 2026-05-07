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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.ApiService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientCatalogFragment extends Fragment {

    private RecyclerView recyclerViewBooks;
    private BookCatalogAdapter bookAdapter;
    private List<Book> masterBookList = new ArrayList<>();
    private EditText editTextSearch;
    private ShimmerFrameLayout shimmerViewContainer;

    private String currentLanguage = "All";
    private String currentAuthor = "All";
    private String currentAvailability = "All";

    private List<String> dynamicLanguages = new ArrayList<>();
    private List<String> dynamicAuthors = new ArrayList<>();
    private List<String> dynamicAvailabilities = new ArrayList<>();

    private int currentPage = 1;
    private final int PAGE_SIZE = 6;
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
        fetchBooks();
        setupSearch();
        setupFilterChips();
    }

    private void initViews(View view) {
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        shimmerViewContainer = view.findViewById(R.id.shimmerViewContainer);
    }

    private void setupRecyclerView() {
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

    private void fetchBooks() {
        shimmerViewContainer.startShimmer();
        shimmerViewContainer.setVisibility(View.VISIBLE);
        recyclerViewBooks.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApiService(requireContext());
        apiService.getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (isAdded()) {
                    shimmerViewContainer.stopShimmer();
                    shimmerViewContainer.setVisibility(View.GONE);
                    recyclerViewBooks.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null) {
                        masterBookList = response.body();
                        extractDynamicFilters();
                        applyFilters();
                    } else {
                        Toast.makeText(requireContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                if (isAdded()) {
                    shimmerViewContainer.stopShimmer();
                    shimmerViewContainer.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void extractDynamicFilters() {
        Set<String> languages = new HashSet<>();
        Set<String> authors = new HashSet<>();
        Set<String> availabilities = new HashSet<>();

        for (Book book : masterBookList) {
            if (book.getLanguage() != null && !book.getLanguage().isEmpty()) {
                languages.add(book.getLanguage());
            }
            if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
                authors.add(book.getAuthor());
            }
            if (book.getStatus() != null && !book.getStatus().isEmpty()) {
                availabilities.add(book.getStatus());
            }
        }

        dynamicLanguages.clear();
        dynamicLanguages.add("All");
        dynamicLanguages.addAll(languages);
        Collections.sort(dynamicLanguages.subList(1, dynamicLanguages.size()));

        dynamicAuthors.clear();
        dynamicAuthors.add("All");
        dynamicAuthors.addAll(authors);
        Collections.sort(dynamicAuthors.subList(1, dynamicAuthors.size()));

        dynamicAvailabilities.clear();
        dynamicAvailabilities.add("All");
        dynamicAvailabilities.addAll(availabilities);
        Collections.sort(dynamicAvailabilities.subList(1, dynamicAvailabilities.size()));
    }

    private void loadNextPage() {
        if (filteredBookList.isEmpty()) {
            bookAdapter.submitList(new ArrayList<>());
            return;
        }

        isLoading = true;
        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredBookList.size());

        if (start < filteredBookList.size()) {
            List<Book> nextItems = new ArrayList<>(filteredBookList.subList(0, end));
            bookAdapter.submitList(nextItems);
            currentPage++;
            if (end >= filteredBookList.size()) {
                isLastPage = true;
            }
        } else {
            isLastPage = true;
        }
        isLoading = false;
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
        View resetFiltersBtn = requireView().findViewById(R.id.resetFilters);
        if (resetFiltersBtn != null) {
            resetFiltersBtn.setOnClickListener(v -> resetAllFilters());
        }
        
        TextView resetFiltersText = requireView().findViewById(R.id.textViewResetFilters);
        if (resetFiltersText != null) {
            resetFiltersText.setOnClickListener(v -> resetAllFilters());
        }
    }

    private void showLanguageDialog() {
        if (dynamicLanguages.isEmpty()) return;
        
        String[] languages = dynamicLanguages.toArray(new String[0]);
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
        if (dynamicAuthors.isEmpty()) return;

        String[] authors = dynamicAuthors.toArray(new String[0]);
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
        if (dynamicAvailabilities.isEmpty()) return;

        String[] availabilities = dynamicAvailabilities.toArray(new String[0]);
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Availability")
                .setItems(availabilities, (dialog, which) -> {
                    currentAvailability = availabilities[which];
                    updateFilterChip(R.id.textViewAvailabilityLabel, "Availability: " + currentAvailability);

                    // Update availability chip appearance
                    View availabilityChip = requireView().findViewById(R.id.chipAvailability);
                    if (availabilityChip != null) {
                        int bgColor;
                        if (currentAvailability.equalsIgnoreCase("Available")) {
                            bgColor = R.color.primary;
                        } else if (currentAvailability.equalsIgnoreCase("All")) {
                            bgColor = R.color.surface_container_lowest;
                        } else {
                            bgColor = R.color.status_limited_bg;
                        }

                        availabilityChip.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(
                                        ContextCompat.getColor(requireContext(), bgColor)));

                        TextView availabilityLabel = requireView().findViewById(R.id.textViewAvailabilityLabel);
                        if (availabilityLabel != null) {
                            availabilityLabel.setTextColor(
                                            currentAvailability.equals("All")
                                                    ? ContextCompat.getColor(requireContext(), R.color.on_surface_variant)
                                                    : android.graphics.Color.WHITE);
                        }
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

        updateFilterChip(R.id.textViewLanguageLabel, "Language: All");
        updateFilterChip(R.id.textViewAuthorLabel, "Author: All");
        updateFilterChip(R.id.textViewAvailabilityLabel, "Availability: All");

        View availabilityChip = requireView().findViewById(R.id.chipAvailability);
        if (availabilityChip != null) {
            availabilityChip.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.surface_container_lowest)));

            TextView availabilityLabel = requireView().findViewById(R.id.textViewAvailabilityLabel);
            if (availabilityLabel != null) {
                availabilityLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant));
            }
        }

        editTextSearch.setText("");
        applyFilters();
    }

    private void applyFilters() {
        filteredBookList.clear();
        String searchTerm = editTextSearch.getText().toString().trim().toLowerCase(Locale.ROOT);

        for (Book book : masterBookList) {
            boolean matchSearch = true;
            boolean matchLanguage = true;
            boolean matchAuthor = true;
            boolean matchAvailability = true;

            // Search filter
            if (!searchTerm.isEmpty()) {
                String title = book.getTitle() != null ? book.getTitle().toLowerCase(Locale.ROOT) : "";
                String author = book.getAuthor() != null ? book.getAuthor().toLowerCase(Locale.ROOT) : "";
                String category = book.getCategory() != null ? book.getCategory().toLowerCase(Locale.ROOT) : "";

                matchSearch = title.contains(searchTerm) || author.contains(searchTerm) || category.contains(searchTerm);
            }

            // Language filter
            if (!currentLanguage.equals("All")) {
                matchLanguage = book.getLanguage() != null && book.getLanguage().equalsIgnoreCase(currentLanguage);
            }

            // Author filter
            if (!currentAuthor.equals("All")) {
                matchAuthor = book.getAuthor() != null && book.getAuthor().equalsIgnoreCase(currentAuthor);
            }

            // Availability filter
            if (!currentAvailability.equals("All")) {
                matchAvailability = book.getStatus() != null && book.getStatus().equalsIgnoreCase(currentAvailability);
            }

            if (matchSearch && matchLanguage && matchAuthor && matchAvailability) {
                filteredBookList.add(book);
            }
        }

        currentPage = 1;
        isLastPage = false;
        loadNextPage();
    }
}