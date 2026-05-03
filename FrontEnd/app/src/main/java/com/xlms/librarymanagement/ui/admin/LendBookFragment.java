package com.xlms.librarymanagement.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.Book;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LendBookFragment extends Fragment {

    private Spinner spinnerTitle, spinnerAuthor, spinnerCategory;
    private EditText editLenderName, editEmail, editPhone, editCopies, editFine;
    private TextView textViewIssuedDate, textViewDueDate;
    private Button buttonSubmit;

    private List<Book> allBooks = new ArrayList<>();
    private final Calendar dueDateCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public interface OnLendBookActionListener {
        void onBookLended();
        void onBack();
    }

    private OnLendBookActionListener listener;

    public void setOnLendBookActionListener(OnLendBookActionListener listener) {
        this.listener = listener;
    }

    private String preSelectedTitle;

    public static LendBookFragment newInstance(Book book) {
        LendBookFragment fragment = new LendBookFragment();
        if (book != null) {
            Bundle args = new Bundle();
            args.putString("pre_title", book.getTitle());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            preSelectedTitle = getArguments().getString("pre_title");
        }
        return inflater.inflate(R.layout.fragment_lend_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        fetchBooks();
    }

    private void initViews(View view) {
        spinnerTitle = view.findViewById(R.id.spinnerBookTitle);
        spinnerAuthor = view.findViewById(R.id.spinnerAuthor);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        editLenderName = view.findViewById(R.id.editTextLenderName);
        editEmail = view.findViewById(R.id.editTextEmail);
        editPhone = view.findViewById(R.id.editTextPhone);
        editCopies = view.findViewById(R.id.editTextCopies);
        editFine = view.findViewById(R.id.editTextFine);
        textViewIssuedDate = view.findViewById(R.id.textViewCurrentDate);
        textViewDueDate = view.findViewById(R.id.editTextDueDate);
        buttonSubmit = view.findViewById(R.id.buttonLendBook);

        // Set initial issued date
        textViewIssuedDate.setText(dateFormat.format(Calendar.getInstance().getTime()));

        textViewDueDate.setOnClickListener(v -> showDatePicker());
        buttonSubmit.setOnClickListener(v -> submitLending());

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });

        view.findViewById(R.id.buttonCancel).setOnClickListener(v -> {
            if (listener != null) listener.onBack();
        });
    }

    private void fetchBooks() {
        ApiClient.getApiService(requireContext()).getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(@NonNull Call<List<Book>> call, @NonNull Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allBooks = response.body();
                    setupBookSpinners();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Book>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load books: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupBookSpinners() {
        if (allBooks == null || allBooks.isEmpty()) return;

        List<String> titles = new ArrayList<>();
        for (Book b : allBooks) {
            if (b.getTitle() != null && !titles.contains(b.getTitle())) {
                titles.add(b.getTitle());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, titles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTitle.setAdapter(adapter);

        if (preSelectedTitle != null) {
            int pos = titles.indexOf(preSelectedTitle);
            if (pos >= 0) {
                spinnerTitle.setSelection(pos);
            }
        }

        spinnerTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTitle = titles.get(position);
                updateLinkedSpinners(selectedTitle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateLinkedSpinners(String title) {
        List<String> authors = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        for (Book b : allBooks) {
            if (title.equals(b.getTitle())) {
                if (b.getAuthor() != null && !authors.contains(b.getAuthor())) {
                    authors.add(b.getAuthor());
                }
                if (b.getCategory() != null && !categories.contains(b.getCategory())) {
                    categories.add(b.getCategory());
                }
            }
        }

        ArrayAdapter<String> authorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, authors);
        authorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAuthor.setAdapter(authorAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            dueDateCalendar.set(year, month, day);
            textViewDueDate.setText(dateFormat.format(dueDateCalendar.getTime()));
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        
        // Due date must be after today
        dialog.getDatePicker().setMinDate(now.getTimeInMillis() + 86400000); // 24 hours later
        dialog.show();
    }

    private void submitLending() {
        String name = editLenderName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String copiesStr = editCopies.getText().toString().trim();
        String fineStr = editFine.getText().toString().trim();
        String dueDateStr = textViewDueDate.getText().toString().trim();
        String issuedDateStr = textViewIssuedDate.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            editLenderName.setError("Lender name required");
            editLenderName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Valid email required");
            editEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Phone number required");
            editPhone.requestFocus();
            return;
        }
        if (spinnerTitle.getSelectedItem() == null) {
            Toast.makeText(requireContext(), "Please select a book", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(dueDateStr) || dueDateStr.equalsIgnoreCase("Select due date")) {
            Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(copiesStr)) {
            editCopies.setError("Copies required");
            editCopies.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(fineStr)) {
            editFine.setError("Fine per day required");
            editFine.requestFocus();
            return;
        }

        // Prepare request body matching API expectations
        JsonObject body = new JsonObject();
        body.addProperty("Lendername", name);
        body.addProperty("Email", email);
        body.addProperty("PhoneNumber", phone);
        body.addProperty("BookTitle", spinnerTitle.getSelectedItem().toString());
        body.addProperty("Author", spinnerAuthor.getSelectedItem().toString());
        body.addProperty("Category", spinnerCategory.getSelectedItem().toString());
        body.addProperty("IssuedDate", issuedDateStr);
        body.addProperty("DueDate", dueDateStr);
        body.addProperty("CopiesLent", Integer.parseInt(copiesStr));
        body.addProperty("Fine", Double.parseDouble(fineStr));
        body.addProperty("Role", "User"); // Default role as required by API for user creation

        buttonSubmit.setEnabled(false);
        ApiClient.getApiService(requireContext()).insertLender(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                buttonSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Book issued successfully", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onBookLended();
                } else {
                    String errorMsg = "Failed to issue book";
                    try {
                        if (response.errorBody() != null) {
                            String errorStr = response.errorBody().string();
                            JsonObject errorJson = JsonParser.parseString(errorStr).getAsJsonObject();
                            if (errorJson.has("error")) {
                                errorMsg = errorJson.get("error").getAsString();
                            } else if (errorJson.has("message")) {
                                errorMsg = errorJson.get("message").getAsString();
                            }
                        }
                    } catch (Exception ignored) {
                    }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                buttonSubmit.setEnabled(true);
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
