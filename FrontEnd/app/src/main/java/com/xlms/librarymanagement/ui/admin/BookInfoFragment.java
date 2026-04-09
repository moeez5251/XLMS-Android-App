package com.xlms.librarymanagement.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.xlms.librarymanagement.model.BookInfo;
import com.xlms.librarymanagement.model.BookLending;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookInfoFragment extends Fragment {

    private static final String ARG_BOOK_INFO = "book_info";

    private TextView textViewBookTitle, textViewAuthor, textViewCategory, textViewLanguage;
    private TextView textViewPrice, textViewStatus, textViewTotalCopies, textViewAvailableCopies;
    private TextView textViewLendedCopies, textViewBookId, textViewCurrentDate;
    private ImageButton buttonBack, buttonMoreOptions;
    private Button buttonCancel, buttonLendThisBook;

    private BookInfo bookInfo;
    private OnBookInfoActionListener listener;

    public interface OnBookInfoActionListener {
        void onLendBookClick(BookInfo book);
        void onBack();
    }

    public static BookInfoFragment newInstance(BookInfo book) {
        BookInfoFragment fragment = new BookInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_INFO, book);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnBookInfoActionListener(OnBookInfoActionListener listener) {
        this.listener = listener;
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
            bookInfo = (BookInfo) getArguments().getSerializable(ARG_BOOK_INFO);
        }

        if (bookInfo == null) {
            Toast.makeText(requireContext(), "Error: No book data", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onBack();
            return;
        }

        initViews(view);
        populateFields();
        setupClickListeners();
    }

    private void initViews(View view) {
        textViewBookTitle = view.findViewById(R.id.textViewBookTitle);
        textViewAuthor = view.findViewById(R.id.textViewAuthor);
        textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewLanguage = view.findViewById(R.id.textViewLanguage);
        textViewPrice = view.findViewById(R.id.textViewPrice);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewTotalCopies = view.findViewById(R.id.textViewTotalCopies);
        textViewAvailableCopies = view.findViewById(R.id.textViewAvailableCopies);
        textViewLendedCopies = view.findViewById(R.id.textViewLendedCopies);
        textViewBookId = view.findViewById(R.id.textViewBookId);
        textViewCurrentDate = view.findViewById(R.id.textViewCurrentDate);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonMoreOptions = view.findViewById(R.id.buttonMoreOptions);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonLendThisBook = view.findViewById(R.id.buttonLendThisBook);
    }

    private void populateFields() {
        textViewBookTitle.setText(bookInfo.getTitle());
        textViewAuthor.setText(bookInfo.getAuthor());
        textViewCategory.setText(bookInfo.getCategory());
        textViewLanguage.setText(bookInfo.getLanguage());
        textViewPrice.setText(String.format(Locale.getDefault(), "£%.2f", bookInfo.getPrice()));
        textViewStatus.setText(bookInfo.getStatus());
        textViewTotalCopies.setText(String.valueOf(bookInfo.getTotalCopies()));
        textViewAvailableCopies.setText(String.valueOf(bookInfo.getAvailableCopies()));
        
        int lended = bookInfo.getTotalCopies() - bookInfo.getAvailableCopies();
        textViewLendedCopies.setText(String.valueOf(lended));
        
        textViewBookId.setText(bookInfo.getBookId());

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(Calendar.getInstance().getTime());
        textViewCurrentDate.setText("System Online: " + currentDate);
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });

        buttonMoreOptions.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "More options coming soon...", Toast.LENGTH_SHORT).show();
        });

        buttonCancel.setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });

        buttonLendThisBook.setOnClickListener(v -> {
            if (listener != null) listener.onLendBookClick(bookInfo);
        });
    }
}
