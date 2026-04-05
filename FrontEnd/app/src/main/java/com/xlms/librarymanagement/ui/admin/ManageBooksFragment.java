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
import com.xlms.librarymanagement.adapter.BookAdapter;
import com.xlms.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage Books Fragment - Admin screen to view and manage library books
 */
public class ManageBooksFragment extends Fragment {

    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private TextView textViewTotalBooks;
    private EditText editTextSearch;

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
        setupClickListeners();
    }

    private void initViews(View view) {
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        textViewTotalBooks = view.findViewById(R.id.textViewTotalBooks);
        editTextSearch = view.findViewById(R.id.editTextSearch);
    }

    private void setupRecyclerView() {
        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList);
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void loadDummyData() {
        bookList.clear();
        
        bookList.add(new Book("14-88219-X", "Rework", "Jason Fried", "Business", "English", 15.00, 8, 8, "Available"));
        bookList.add(new Book("07-43203-1", "The Great Gatsby", "F. Scott Fitzgerald", "Classic", "English", 12.50, 12, 4, "Limited"));
        bookList.add(new Book("99-10293-A", "Thinking, Fast and Slow", "Daniel Kahneman", "Science", "English", 22.00, 5, 0, "Out of Stock"));
        bookList.add(new Book("42-11882-X", "Sapiens", "Yuval Noah Harari", "History", "Hebrew", 18.99, 15, 11, "Available"));
        bookList.add(new Book("21-33490-C", "The Alchemist", "Paulo Coelho", "Fiction", "Portuguese", 10.00, 20, 20, "Available"));
        
        bookAdapter.notifyDataSetChanged();
        textViewTotalBooks.setText(String.valueOf(bookList.size()));
    }

    private void setupClickListeners() {
        Button buttonAddBook = requireView().findViewById(R.id.buttonAddBook);
        if (buttonAddBook != null) {
            buttonAddBook.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Add Book dialog coming soon...", Toast.LENGTH_SHORT).show();
            });
        }

        Button buttonFilters = requireView().findViewById(R.id.buttonFilters);
        if (buttonFilters != null) {
            buttonFilters.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Filters coming soon...", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
