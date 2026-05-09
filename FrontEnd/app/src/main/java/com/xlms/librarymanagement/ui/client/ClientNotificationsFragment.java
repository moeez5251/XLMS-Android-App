package com.xlms.librarymanagement.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.NotificationAdapter;
import com.xlms.librarymanagement.model.Notification;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ClientNotificationsFragment extends Fragment {

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private LinearLayout layoutNotifications, layoutEmptyState;
    private ShimmerFrameLayout shimmerLayout;
    private View layoutLoadingOverlay;
    private Button buttonReadAll, buttonRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        fetchNotifications();
        setupClickListeners();
    }

    private void initViews(View view) {
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        layoutNotifications = view.findViewById(R.id.layoutNotifications);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        layoutLoadingOverlay = view.findViewById(R.id.layoutLoadingOverlay);
        buttonReadAll = view.findViewById(R.id.buttonClearAll);
        if (buttonReadAll != null) {
            buttonReadAll.setText("Read All");
        }
        buttonRefresh = view.findViewById(R.id.buttonRefresh);
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter();
        notificationAdapter.setOnNotificationClickListener(notification -> markAsRead(notification));
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }

    private void markAsRead(Notification notification) {
        if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.VISIBLE);
        
        JsonObject body = new JsonObject();
        body.addProperty("NotificationId", notification.getId());
        
        ApiClient.getApiService(requireContext()).markAsReadAll(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!isAdded()) return;
                if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.GONE);
                
                if (response.isSuccessful()) {
                    fetchNotifications(); // Refresh list to remove read notification
                } else {
                    Toast.makeText(getContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (!isAdded()) return;
                if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNotifications() {
        if (shimmerLayout != null) {
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
            recyclerViewNotifications.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        }

        ApiClient.getApiService(requireContext()).getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (!isAdded()) return;
                if (shimmerLayout != null) {
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    notificationAdapter.submitList(notificationList);
                    recyclerViewNotifications.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
                }
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                if (!isAdded()) return;
                if (shimmerLayout != null) {
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                }
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void setupClickListeners() {
        if (buttonReadAll != null) {
            buttonReadAll.setOnClickListener(v -> markAllAsRead());
        }
        if (buttonRefresh != null) {
            buttonRefresh.setOnClickListener(v -> fetchNotifications());
        }
    }

    private void markAllAsRead() {
        if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.VISIBLE);
        
        ApiClient.getApiService(requireContext()).markAsReadAll(new JsonObject()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!isAdded()) return;
                if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.GONE);
                
                if (response.isSuccessful()) {
                    notificationList.clear();
                    notificationAdapter.clear();
                    updateEmptyState();
                    Toast.makeText(requireContext(), "All notifications marked as read", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (!isAdded()) return;
                if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (notificationList.isEmpty()) {
            layoutNotifications.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutNotifications.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
}
