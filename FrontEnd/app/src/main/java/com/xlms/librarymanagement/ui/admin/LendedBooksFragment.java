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

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.LendedBookAdapter;
import com.xlms.librarymanagement.model.BookInfo;
import com.xlms.librarymanagement.model.BookLending;
import com.xlms.librarymanagement.model.LendedBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LendedBooksFragment extends Fragment {

    private RecyclerView recyclerViewLendedBooks;
    private LendedBookAdapter lendedBookAdapter;
    private List<LendedBook> masterLendedBookList;
    private TextView textViewTotalUsers;
    private EditText editTextSearch;
    private Button buttonAll, buttonReturned, buttonNotReturned, buttonLendBook;

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
        loadDummyData();
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
    }

    private void setupRecyclerView() {
        masterLendedBookList = new ArrayList<>();
        lendedBookAdapter = new LendedBookAdapter(new LendedBookAdapter.OnLendedBookClickListener() {
            @Override
            public void onBookClick(LendedBook book) {
                // Open Book Info with dummy book data
                BookInfo bookInfo = createBookInfoFromLended(book);
                openBookInfoFragment(bookInfo);
            }

            @Override
            public void onMoreOptionsClick(LendedBook book, View anchorView) {
                Toast.makeText(requireContext(), "Options for: " + book.getBookTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewLendedBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewLendedBooks.setAdapter(lendedBookAdapter);
    }

    private BookInfo createBookInfoFromLended(LendedBook lended) {
        double price = 25.00;
        int total = 10;
        int available = "Returned".equals(lended.getStatus()) ? 7 : 3;
        String status = available > 0 ? "Available" : "Out of Stock";
        
        return new BookInfo(
            "BK_" + lended.getBookId(),
            lended.getBookTitle(),
            lended.getAuthor(),
            lended.getCategory(),
            "English",
            price,
            total,
            available,
            status
        );
    }

    private void loadDummyData() {
        masterLendedBookList.clear();
        masterLendedBookList.add(new LendedBook(2, "M6ea45869", "Moeez", "M", "Rework", "Jason Fried", "Business", 7, "29/09/2025", "30/09/2025", "Returned"));
        masterLendedBookList.add(new LendedBook(4, "K9bb21004", "Elena", "E", "Deep Work", "Cal Newport", "Productivity", 1, "25/09/2025", "02/10/2025", "Not Returned"));
        masterLendedBookList.add(new LendedBook(12, "S1aa98234", "Samir", "S", "Atomic Habits", "James Clear", "Self-Help", 3, "27/09/2025", "04/10/2025", "Not Returned"));
        masterLendedBookList.add(new LendedBook(8, "A3cc12345", "Ali", "A", "The Republic", "Plato", "Philosophy", 2, "20/09/2025", "27/09/2025", "Returned"));
        masterLendedBookList.add(new LendedBook(15, "F5dd67890", "Fatima", "F", "Modern Architecture", "Le Corbusier", "Arts", 1, "28/09/2025", "05/10/2025", "Not Returned"));
        
        applyFilters();
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
                String userName = book.getUserName().toLowerCase(Locale.ROOT);
                String userId = book.getUserId().toLowerCase(Locale.ROOT);
                String bookTitle = book.getBookTitle().toLowerCase(Locale.ROOT);
                String author = book.getAuthor().toLowerCase(Locale.ROOT);

                matchSearch = userName.contains(currentSearch) ||
                              userId.contains(currentSearch) ||
                              bookTitle.contains(currentSearch) ||
                              author.contains(currentSearch);
            }

            if (!"All".equals(currentFilter)) {
                matchFilter = book.getStatus().equalsIgnoreCase(currentFilter);
            }

            if (matchSearch && matchFilter) {
                filteredList.add(book);
            }
        }

        lendedBookAdapter.submitList(filteredList);
        textViewTotalUsers.setText(String.valueOf(filteredList.size()));
    }

    // Navigation Methods
    private void openBookInfoFragment(BookInfo bookInfo) {
        BookInfoFragment fragment = BookInfoFragment.newInstance(bookInfo);
        fragment.setOnBookInfoActionListener(new BookInfoFragment.OnBookInfoActionListener() {
            @Override
            public void onLendBookClick(BookInfo book) {
                openLendBookFragment(book);
            }

            @Override
            public void onBack() {
                closeDetailFragment();
            }
        });
        openDetailFragment(fragment);
    }

    private void openLendBookFragment(BookInfo bookInfo) {
        BookInfo book = bookInfo != null ? bookInfo : createDummyBookInfo();
        LendBookFragment fragment = LendBookFragment.newInstance(book);
        fragment.setOnLendBookActionListener(new LendBookFragment.OnLendBookActionListener() {
            @Override
            public void onBookLended(BookLending lending) {
                // Add to lended books list
                String userId = "U" + System.currentTimeMillis() % 10000;
                String initial = lending.getLenderName().substring(0, 1).toUpperCase();
                masterLendedBookList.add(0, new LendedBook(
                    masterLendedBookList.size() + 1,
                    userId,
                    lending.getLenderName(),
                    initial,
                    lending.getBookTitle(),
                    lending.getBookAuthor(),
                    lending.getBookCategory(),
                    lending.getCopiesLent(),
                    lending.getIssuedDate(),
                    lending.getDueDate(),
                    "Not Returned"
                ));
                applyFilters();
                closeDetailFragment();
                Toast.makeText(requireContext(), "Book lended to " + lending.getLenderName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBack() {
                closeDetailFragment();
            }
        });
        openDetailFragment(fragment);
    }

    private BookInfo createDummyBookInfo() {
        return new BookInfo(
            "BK_001",
            "The Republic of Plato",
            "Plato",
            "Philosophy",
            "English",
            25.00,
            10,
            7,
            "Available"
        );
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
}
