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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.ApiService;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.Book;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationFragment extends Fragment {

    private Book selectedBook;
    private ShimmerFrameLayout shimmerViewContainer;
    private View contentLayout;
    private View loadingOverlay;

    public static ReservationFragment newInstance(Book book) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedBook = (Book) getArguments().getSerializable("book");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        initViews(view);
        setupBookDetails(view);

        view.findViewById(R.id.buttonBack).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.buttonConfirmReservation).setOnClickListener(v -> handleReservation());

        // Simulate a small delay for shimmer effect visibility
        view.postDelayed(() -> {
            if (isAdded()) {
                shimmerViewContainer.stopShimmer();
                shimmerViewContainer.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
            }
        }, 800);

        return view;
    }

    private void initViews(View view) {
        shimmerViewContainer = view.findViewById(R.id.shimmerViewContainer);
        contentLayout = view.findViewById(R.id.contentLayout);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
    }

    private void setupBookDetails(View view) {
        if (selectedBook == null) return;

        ((TextView) view.findViewById(R.id.textViewTitle)).setText(selectedBook.getTitle());
        ((TextView) view.findViewById(R.id.textViewAuthor)).setText(selectedBook.getAuthor());
        ((TextView) view.findViewById(R.id.textViewCategory)).setText(selectedBook.getCategory());
    }

    private void handleReservation() {
        if (selectedBook == null) return;

        showLoading(true);

        JsonObject body = new JsonObject();
        body.addProperty("book_id", selectedBook.getBookId());
        // reservation_date is optional, backend uses current date by default

        ApiService apiService = ApiClient.getApiService(requireContext());
        apiService.reserveBook(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (isAdded()) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        // Pop back twice to go back to catalog (since we came from BookInfo)
                        // Or just go back once to BookInfo
                        getParentFragmentManager().popBackStack();
                    } else {
                        String errorMsg = "Reservation failed";
                        try {
                            if (response.errorBody() != null) {
                                JsonObject errorJson = JsonParser.parseString(response.errorBody().string()).getAsJsonObject();
                                if (errorJson.has("error") && errorJson.get("error").isJsonPrimitive()) {
                                    errorMsg = errorJson.get("error").getAsString();
                                } else if (errorJson.has("message")) {
                                    errorMsg = errorJson.get("message").getAsString();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (isAdded()) {
                    showLoading(false);
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showLoading(boolean loading) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        View btn = getView().findViewById(R.id.buttonConfirmReservation);
        if (btn != null) btn.setEnabled(!loading);
    }
}
