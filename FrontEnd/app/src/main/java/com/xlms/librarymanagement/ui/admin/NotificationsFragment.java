package com.xlms.librarymanagement.ui.admin;

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
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.Notification;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements AdminDashboardActivity.Refreshable {

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private LinearLayout layoutNotifications, layoutEmptyState, layoutSkeleton;
    private View layoutLoadingOverlay;
    private Button buttonClearAll, buttonRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
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

    @Override
    public void refreshData() {
        fetchNotifications();
    }

    private void initViews(View view) {
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        layoutNotifications = view.findViewById(R.id.layoutNotifications);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        layoutSkeleton = view.findViewById(R.id.layoutSkeleton);
        layoutLoadingOverlay = view.findViewById(R.id.layoutLoadingOverlay);
        buttonClearAll = view.findViewById(R.id.buttonClearAll);
        buttonRefresh = view.findViewById(R.id.buttonRefresh);
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter();
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }

    private void fetchNotifications() {
        if (layoutSkeleton != null) {
            layoutSkeleton.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        }
        
        ApiClient.getApiService(requireContext()).getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (!isAdded()) return;
                
                if (layoutSkeleton != null) layoutSkeleton.setVisibility(View.GONE);
                recyclerViewNotifications.setVisibility(View.VISIBLE);
                
                if (response.isSuccessful() && response.body() != null) {
                    notificationList = response.body();
                    notificationAdapter.submitList(notificationList);
                    updateEmptyState();
                }
            }
            @Override public void onFailure(Call<List<Notification>> call, Throwable t) {
                if (!isAdded()) return;
                if (layoutSkeleton != null) layoutSkeleton.setVisibility(View.GONE);
                recyclerViewNotifications.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Error fetching", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        if (buttonClearAll != null) {
            buttonClearAll.setText("Read All");
            buttonClearAll.setOnClickListener(v -> markAllAsRead());
        }

        if (buttonRefresh != null) {
            buttonRefresh.setOnClickListener(v -> fetchNotifications());
        }
    }

    private void markAllAsRead() {
        if (layoutLoadingOverlay != null) {
            layoutLoadingOverlay.setVisibility(View.VISIBLE);
        }
        
        com.google.gson.JsonObject body = new com.google.gson.JsonObject();
        body.addProperty("status", "read");
        ApiClient.getApiService(requireContext()).markAsReadAll(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (!isAdded()) return;
                if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.GONE);
                
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "All marked as read", Toast.LENGTH_SHORT).show();
                    fetchNotifications();
                }
            }
            @Override public void onFailure(Call<MessageResponse> call, Throwable t) {
                if (!isAdded()) return;
                if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (layoutNotifications != null && layoutEmptyState != null) {
            if (notificationList == null || notificationList.isEmpty()) {
                layoutNotifications.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            } else {
                layoutNotifications.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            }
        }
    }
}
