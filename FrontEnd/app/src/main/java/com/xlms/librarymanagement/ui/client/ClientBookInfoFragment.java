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

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.ApiService;
import com.xlms.librarymanagement.api.GetByIdRequest;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.Book;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientBookInfoFragment extends Fragment {

    private Book book;
    private android.widget.ProgressBar progressBar;
    private View loadingOverlay;
    private View mainContent;

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

        progressBar = view.findViewById(R.id.progressBar);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        mainContent = view.findViewById(R.id.mainContent);

        com.xlms.librarymanagement.utils.SessionManager sessionManager = new com.xlms.librarymanagement.utils.SessionManager(requireContext());
        String role = sessionManager.getUserRole();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        if (book != null) {
            populateData(view, isAdmin);
            fetchBookDetails(view, isAdmin);
        }

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        View buttonSave = view.findViewById(R.id.buttonSave);
        View buttonDelete = view.findViewById(R.id.buttonDelete);
        View buttonLend = view.findViewById(R.id.buttonLend);

        if (isAdmin) {
            buttonSave.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
            buttonLend.setVisibility(View.GONE);
            
            buttonSave.setOnClickListener(v -> updateBookOnServer(view));
            
            buttonDelete.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete Book")
                        .setMessage("Are you sure you want to delete this book?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteBookFromServer())
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        } else {
            buttonSave.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            
            android.widget.Button btnLend = (android.widget.Button) buttonLend;
            if (book.getStatus() != null && book.getStatus().equalsIgnoreCase("Reserved")) {
                buttonLend.setVisibility(View.GONE);
            } else if (book.getAvailable() <= 0) {
                btnLend.setText("Reserve this Book");
                btnLend.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        androidx.core.content.ContextCompat.getColor(requireContext(), R.color.secondary)));
                
                btnLend.setOnClickListener(v -> {
                    Fragment reservationFragment = ReservationFragment.newInstance(book);
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                            .replace(R.id.fragment_container, reservationFragment)
                            .addToBackStack(null)
                            .commit();
                });
                buttonLend.setVisibility(View.VISIBLE);
            } else {
                btnLend.setText("Lend this Book");
                btnLend.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary)));
                btnLend.setOnClickListener(v -> {
                    Fragment checkoutFragment = CheckoutFragment.newInstance(book);
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                            .replace(R.id.fragment_container, checkoutFragment)
                            .addToBackStack(null)
                            .commit();
                });
                buttonLend.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private void fetchBookDetails(View view, boolean isAdmin) {
        if (book == null) return;
        
        showLoading(true);
        ApiService apiService = ApiClient.getApiService(requireContext());
        apiService.getBookById(new GetByIdRequest(book.getBookId())).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(@NonNull Call<List<Book>> call, @NonNull Response<List<Book>> response) {
                if (isAdded()) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        book = response.body().get(0);
                        populateData(view, isAdmin);
                        updateLendButtonState(view);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Book>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Error refreshing data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLendButtonState(View view) {
        View buttonLend = view.findViewById(R.id.buttonLend);
        if (buttonLend == null || buttonLend.getVisibility() == View.GONE) return;

        android.widget.Button btnLend = (android.widget.Button) buttonLend;
        if (book.getStatus() != null && book.getStatus().equalsIgnoreCase("Reserved")) {
            buttonLend.setVisibility(View.GONE);
        } else if (book.getAvailable() <= 0) {
            btnLend.setText("Reserve this Book");
            btnLend.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.secondary)));
            btnLend.setOnClickListener(v -> {
                Fragment reservationFragment = ReservationFragment.newInstance(book);
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .replace(R.id.fragment_container, reservationFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else {
            btnLend.setText("Lend this Book");
            btnLend.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary)));
            btnLend.setOnClickListener(v -> {
                Fragment checkoutFragment = CheckoutFragment.newInstance(book);
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out)
                        .replace(R.id.fragment_container, checkoutFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }

    private void updateBookOnServer(View view) {
        android.widget.EditText editTitle = view.findViewById(R.id.editTitle);
        android.widget.EditText editAuthor = view.findViewById(R.id.editAuthor);
        android.widget.EditText editPrice = view.findViewById(R.id.editPrice);
        android.widget.EditText editTotal = view.findViewById(R.id.editTotal);
        android.widget.EditText editPages = view.findViewById(R.id.editPages);
        android.widget.Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        android.widget.Spinner spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        android.widget.Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        book.setTitle(editTitle.getText().toString());
        book.setAuthor(editAuthor.getText().toString());
        try {
            book.setPrice(Double.parseDouble(editPrice.getText().toString()));
            book.setTotal(Integer.parseInt(editTotal.getText().toString()));
            book.setPages(Integer.parseInt(editPages.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (spinnerCategory != null) book.setCategory(spinnerCategory.getSelectedItem().toString());
        if (spinnerLanguage != null) book.setLanguage(spinnerLanguage.getSelectedItem().toString());
        if (spinnerStatus != null) book.setStatus(spinnerStatus.getSelectedItem().toString());

        showLoading(true);
        ApiService apiService = ApiClient.getApiService(requireContext());
        apiService.updateBook(book).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (isAdded()) {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Book updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update book", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteBookFromServer() {
        showLoading(true);
        ApiService apiService = ApiClient.getApiService(requireContext());
        java.util.List<String> ids = java.util.Collections.singletonList(book.getBookId());
        apiService.deleteBook(ids).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (isAdded()) {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Book deleted", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete book", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showLoading(boolean loading) {
        if (loadingOverlay != null) loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void populateData(View view, boolean isAdmin) {
        android.widget.EditText editTitle = view.findViewById(R.id.editTitle);
        android.widget.EditText editAuthor = view.findViewById(R.id.editAuthor);
        android.widget.EditText editPrice = view.findViewById(R.id.editPrice);
        android.widget.EditText editTotal = view.findViewById(R.id.editTotal);
        android.widget.EditText editPages = view.findViewById(R.id.editPages);
        TextView textViewBookId = view.findViewById(R.id.textViewBookId);
        
        android.widget.Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);
        android.widget.Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        android.widget.Spinner spinnerLanguage = view.findViewById(R.id.spinnerLanguage);

        TextView textViewCategory = view.findViewById(R.id.textViewCategory);
        TextView textViewLanguage = view.findViewById(R.id.textViewLanguage);
        TextView textViewStatus = view.findViewById(R.id.textViewStatus);

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

        // --- Handle Category ---
        if (isAdmin) {
            if (spinnerCategory != null) {
                spinnerCategory.setVisibility(View.VISIBLE);
                if (textViewCategory != null) textViewCategory.setVisibility(View.GONE);
                
                String[] categories = {"Fiction", "Non-Fiction", "Science", "History", "Business", "Biography", "Fantasy", "Classic", "Self-Help", "Romance"};
                android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
                
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equalsIgnoreCase(book.getCategory())) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            if (spinnerCategory != null) spinnerCategory.setVisibility(View.GONE);
            if (textViewCategory != null) {
                textViewCategory.setVisibility(View.VISIBLE);
                textViewCategory.setText(book.getCategory());
            }
        }

        // --- Handle Language ---
        if (isAdmin) {
            if (spinnerLanguage != null) {
                spinnerLanguage.setVisibility(View.VISIBLE);
                if (textViewLanguage != null) textViewLanguage.setVisibility(View.GONE);
                
                String[] languages = {"English", "Urdu", "Spanish", "French", "German", "Chinese", "Arabic", "Portuguese"};
                android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLanguage.setAdapter(adapter);
                
                for (int i = 0; i < languages.length; i++) {
                    if (languages[i].equalsIgnoreCase(book.getLanguage())) {
                        spinnerLanguage.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            if (spinnerLanguage != null) spinnerLanguage.setVisibility(View.GONE);
            if (textViewLanguage != null) {
                textViewLanguage.setVisibility(View.VISIBLE);
                textViewLanguage.setText(book.getLanguage());
            }
        }
        
        // --- Handle Status ---
        if (isAdmin) {
            if (spinnerStatus != null) {
                spinnerStatus.setVisibility(View.VISIBLE);
                if (textViewStatus != null) textViewStatus.setVisibility(View.GONE);
                
                String[] statuses = {"Available", "Reserved", "Out of Stock", "Borrowed"};
                android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statuses);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStatus.setAdapter(adapter);
                
                for (int i = 0; i < statuses.length; i++) {
                    if (statuses[i].equalsIgnoreCase(book.getStatus())) {
                        spinnerStatus.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            if (spinnerStatus != null) spinnerStatus.setVisibility(View.GONE);
            if (textViewStatus != null) {
                textViewStatus.setVisibility(View.VISIBLE);
                textViewStatus.setText(book.getStatus());
            }
        }
    }
}
