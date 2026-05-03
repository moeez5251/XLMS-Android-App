package com.xlms.librarymanagement.ui.admin;

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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.LendedBookAdapter;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.model.Book;
import com.xlms.librarymanagement.model.BookInfo;
import com.xlms.librarymanagement.model.BookLending;
import com.xlms.librarymanagement.model.LendedBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LendedBooksFragment extends Fragment implements AdminDashboardActivity.Refreshable {

    private RecyclerView recyclerViewLendedBooks;
    private LendedBookAdapter lendedBookAdapter;
    private List<LendedBook> masterLendedBookList = new ArrayList<>();
    private TextView textViewTotalUsers;
    private EditText editTextSearch;
    private Button buttonAll, buttonReturned, buttonNotReturned, buttonLendBook;
    private ShimmerFrameLayout shimmerLendedBooks;

    private String currentFilter = "All";
    private String currentSearch = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lended_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        fetchLenders();
        setupSearch();
        setupFilterButtons();
    }

    private void initViews(View view) {
        recyclerViewLendedBooks = view.findViewById(R.id.recyclerViewLendedBooks);
        textViewTotalUsers = view.findViewById(R.id.textViewTotalUsers);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonAll = view.findViewById(R.id.buttonAll);
        buttonReturned = view.findViewById(R.id.buttonReturned);
        buttonNotReturned = view.findViewById(R.id.buttonNotReturned);
        buttonLendBook = view.findViewById(R.id.buttonLendBook);
        shimmerLendedBooks = view.findViewById(R.id.shimmerLendedBooks);
    }

    private void setupRecyclerView() {
        lendedBookAdapter = new LendedBookAdapter(new LendedBookAdapter.OnLendedBookClickListener() {
            @Override
            public void onBookClick(LendedBook book) {
                // Navigate using the real borrowerId
                LendedBookInfoFragment fragment = LendedBookInfoFragment.newInstance(Integer.parseInt(book.getBorrowerId()));
                openDetailFragment(fragment);
            }
        });
        recyclerViewLendedBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewLendedBooks.setAdapter(lendedBookAdapter);
    }

    private void fetchLenders() {
        if (shimmerLendedBooks != null) {
            shimmerLendedBooks.setVisibility(View.VISIBLE);
            shimmerLendedBooks.startShimmer();
        }
        recyclerViewLendedBooks.setVisibility(View.GONE);

        ApiClient.getApiService(requireContext()).getLenders().enqueue(new Callback<List<BookLending>>() {
            @Override
            public void onResponse(Call<List<BookLending>> call, Response<List<BookLending>> response) {
                if (shimmerLendedBooks != null) {
                    shimmerLendedBooks.stopShimmer();
                    shimmerLendedBooks.setVisibility(View.GONE);
                }
                recyclerViewLendedBooks.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    mapLendersToModel(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch lenders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookLending>> call, Throwable t) {
                if (shimmerLendedBooks != null) {
                    shimmerLendedBooks.stopShimmer();
                    shimmerLendedBooks.setVisibility(View.GONE);
                }
                recyclerViewLendedBooks.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapLendersToModel(List<BookLending> apiList) {
        masterLendedBookList.clear();
        for (BookLending apiItem : apiList) {
            String initial = apiItem.getLenderName() != null && !apiItem.getLenderName().isEmpty() 
                    ? apiItem.getLenderName().substring(0, 1).toUpperCase() : "?";
            
            masterLendedBookList.add(new LendedBook(
                String.valueOf(apiItem.getBorrowerId()),
                apiItem.getBookId(),
                apiItem.getUserId(),
                apiItem.getLenderName(),
                initial,
                apiItem.getBookTitle(),
                apiItem.getBookAuthor(),
                apiItem.getBookCategory(),
                apiItem.getCopiesLent(),
                formatIsoDate(apiItem.getIssuedDate()),
                formatIsoDate(apiItem.getDueDate()),
                apiItem.getStatus()
            ));
        }
        applyFilters();
    }

    private String formatIsoDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "N/A";
        try {
            String cleanDate = isoDate.replace("Z", "+0000");
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            java.util.Date date = inputFormat.parse(cleanDate);
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
                java.util.Date date = inputFormat.parse(isoDate.split("T")[0]);
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.US);
                return outputFormat.format(date);
            } catch (Exception e2) {
                return isoDate;
            }
        }
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

    private void setupFilterButtons() {
        View.OnClickListener filterListener = v -> {
            if (v == buttonAll) {
                currentFilter = "All";
            } else if (v == buttonReturned) {
                currentFilter = "Returned";
            } else if (v == buttonNotReturned) {
                currentFilter = "Not Returned";
            }
            updateFilterButtonStyles();
            applyFilters();
        };

        buttonAll.setOnClickListener(filterListener);
        buttonReturned.setOnClickListener(filterListener);
        buttonNotReturned.setOnClickListener(filterListener);

        if (buttonLendBook != null) {
            buttonLendBook.setOnClickListener(v -> openLendBookFragment(null));
        }
    }

    private void updateFilterButtonStyles() {
        if ("All".equals(currentFilter)) {
            buttonAll.setBackgroundResource(R.drawable.filter_chip_selected);
            buttonAll.setTextColor(getResources().getColor(R.color.white));
            buttonReturned.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonReturned.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonNotReturned.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonNotReturned.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
        } else if ("Returned".equals(currentFilter)) {
            buttonAll.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonAll.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonReturned.setBackgroundResource(R.drawable.filter_chip_selected);
            buttonReturned.setTextColor(getResources().getColor(R.color.white));
            buttonNotReturned.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonNotReturned.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
        } else {
            buttonAll.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonAll.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonReturned.setBackgroundResource(R.drawable.filter_chip_unselected);
            buttonReturned.setTextColor(getResources().getColor(R.color.on_secondary_fixed_variant));
            buttonNotReturned.setBackgroundResource(R.drawable.filter_chip_selected);
            buttonNotReturned.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void applyFilters() {
        List<LendedBook> filteredList = new ArrayList<>();

        for (LendedBook book : masterLendedBookList) {
            boolean matchSearch = true;
            boolean matchFilter = true;

            if (!currentSearch.isEmpty()) {
                String userName = book.getUserName() != null ? book.getUserName().toLowerCase(Locale.ROOT) : "";
                String userId = book.getUserId() != null ? book.getUserId().toLowerCase(Locale.ROOT) : "";
                String bookTitle = book.getBookTitle() != null ? book.getBookTitle().toLowerCase(Locale.ROOT) : "";
                String author = book.getAuthor() != null ? book.getAuthor().toLowerCase(Locale.ROOT) : "";

                matchSearch = userName.contains(currentSearch) ||
                              userId.contains(currentSearch) ||
                              bookTitle.contains(currentSearch) ||
                              author.contains(currentSearch);
            }

            if (!"All".equals(currentFilter)) {
                matchFilter = book.getStatus() != null && book.getStatus().trim().equalsIgnoreCase(currentFilter);
            }

            if (matchSearch && matchFilter) {
                filteredList.add(book);
            }
        }

        lendedBookAdapter.submitList(filteredList);
        textViewTotalUsers.setText(String.valueOf(filteredList.size()));
    }

    private void openLendBookFragment(Book book) {
        LendBookFragment fragment = LendBookFragment.newInstance(book);
        fragment.setOnLendBookActionListener(new LendBookFragment.OnLendBookActionListener() {
            @Override
            public void onBookLended() {
                fetchLenders();
                closeDetailScreen();
            }

            @Override
            public void onBack() {
                closeDetailScreen();
            }
        });
        openDetailFragment(fragment);
    }

    private void openDetailFragment(Fragment fragment) {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).openDetailScreen(fragment);
        }
    }

    @Override
    public void refreshData() {
        fetchLenders();
    }

    private void closeDetailScreen() {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).closeDetailScreen();
        }
    }
}
