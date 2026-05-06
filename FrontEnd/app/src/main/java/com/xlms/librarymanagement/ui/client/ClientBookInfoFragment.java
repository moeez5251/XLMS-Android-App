package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

        com.xlms.librarymanagement.utils.SessionManager sessionManager = new com.xlms.librarymanagement.utils.SessionManager(requireContext());
        String role = sessionManager.getUserRole();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        if (book != null) {
            populateData(view, isAdmin);
        }

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        View buttonCancel = view.findViewById(R.id.buttonCancel);
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        View buttonSave = view.findViewById(R.id.buttonSave);
        View buttonDelete = view.findViewById(R.id.buttonDelete);
        View buttonLend = view.findViewById(R.id.buttonLend);

        if (isAdmin) {
            buttonSave.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            buttonLend.setVisibility(View.GONE);
            
            buttonSave.setOnClickListener(v -> {
                // Admin save logic could be implemented here or redirected to Admin BookInfoFragment
                Toast.makeText(requireContext(), "Admin Save functionality coming soon", Toast.LENGTH_SHORT).show();
            });
            
            buttonDelete.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Admin Delete functionality coming soon", Toast.LENGTH_SHORT).show();
            });
        } else {
            buttonSave.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            buttonLend.setVisibility(View.VISIBLE);
            
            buttonLend.setOnClickListener(v -> {
                Fragment checkoutFragment = CheckoutFragment.newInstance(book);
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .replace(R.id.fragment_container, checkoutFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        return view;
    }

    private void populateData(View view, boolean isAdmin) {
        android.widget.EditText editTitle = view.findViewById(R.id.editTitle);
        android.widget.EditText editAuthor = view.findViewById(R.id.editAuthor);
        android.widget.EditText editPrice = view.findViewById(R.id.editPrice);
        android.widget.EditText editTotal = view.findViewById(R.id.editTotal);
        android.widget.EditText editPages = view.findViewById(R.id.editPages);
        TextView textViewBookId = view.findViewById(R.id.textViewBookId);
        
        // Since textViewStatus is missing in the layout, we'll use spinnerStatus if we want to show it,
        // but for a quick fix that also supports Admin, we'll just check if it exists or use a toast.
        // Better: let's use the spinnerStatus selection if it's there.
        android.widget.Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);
        android.widget.Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        android.widget.Spinner spinnerLanguage = view.findViewById(R.id.spinnerLanguage);

        if (editTitle != null) {
            editTitle.setText(book.getTitle());
            editTitle.setEnabled(isAdmin);
        }
        if (editAuthor != null) {
            editAuthor.setText(book.getAuthor());
            editAuthor.setEnabled(isAdmin);
        }
        if (editPrice != null) {
            editPrice.setText(String.format(Locale.getDefault(), "%.0f", book.getPrice()));
            editPrice.setEnabled(isAdmin);
        }
        if (editTotal != null) {
            editTotal.setText(String.valueOf(book.getTotal()));
            editTotal.setEnabled(isAdmin);
        }
        if (editPages != null) {
            editPages.setText(String.valueOf(book.getPages()));
            editPages.setEnabled(isAdmin);
        }
        if (textViewBookId != null) {
            textViewBookId.setText("BK_" + book.getBookId());
        }

        // Handle spinners for Admin, disable for Client
        if (spinnerCategory != null) spinnerCategory.setEnabled(isAdmin);
        if (spinnerLanguage != null) spinnerLanguage.setEnabled(isAdmin);
        
        if (spinnerStatus != null) {
            spinnerStatus.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            spinnerStatus.setEnabled(isAdmin);
        }
        
        TextView textViewStatus = view.findViewById(R.id.textViewStatus);
        if (textViewStatus != null) {
            textViewStatus.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
            textViewStatus.setText(book.getStatus());
        }
    }
}