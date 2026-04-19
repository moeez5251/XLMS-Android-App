package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Book;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClientBookInfoFragment extends Fragment {

    private Book book;

    public static ClientBookInfoFragment newInstance(Book book) {
        ClientBookInfoFragment fragment = new ClientBookInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable("book");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_info, container, false);

        if (book != null) {
            populateData(view);
        }

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.buttonCancel).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        view.findViewById(R.id.buttonLendThisBook).setOnClickListener(v -> {
            Fragment checkoutFragment = CheckoutFragment.newInstance(book);
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                    .replace(R.id.fragment_container, checkoutFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void populateData(View view) {
        ((TextView) view.findViewById(R.id.textViewBookTitle)).setText(book.getTitle());
        ((TextView) view.findViewById(R.id.textViewAuthor)).setText(book.getAuthor());
        ((TextView) view.findViewById(R.id.textViewLanguage)).setText(book.getLanguage());
        ((TextView) view.findViewById(R.id.textViewPrice)).setText(String.format(Locale.getDefault(), "Rs %.0f", book.getPrice()));
        ((TextView) view.findViewById(R.id.textViewStatus)).setText(book.getStatus());
        ((TextView) view.findViewById(R.id.textViewTotalCopies)).setText(String.valueOf(book.getTotal()));
        ((TextView) view.findViewById(R.id.textViewAvailableCopies)).setText(String.valueOf(book.getAvailable()));
        ((TextView) view.findViewById(R.id.textViewLendedCopies)).setText(String.valueOf(book.getTotal() - book.getAvailable()));
        ((TextView) view.findViewById(R.id.textViewBookId)).setText("BK_" + book.getBookId());

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        ((TextView) view.findViewById(R.id.textViewCurrentDate)).setText("System Online: " + currentDate);
    }
}