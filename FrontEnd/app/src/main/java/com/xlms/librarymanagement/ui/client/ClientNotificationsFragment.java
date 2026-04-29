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

public class ClientNotificationsFragment extends Fragment {

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private LinearLayout layoutNotifications, layoutEmptyState, layoutSkeleton;
    private View layoutLoadingOverlay;
    private Button buttonClearAll, buttonRefresh;

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
        loadDummyData();
        setupClickListeners();
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
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter();
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }

    private void loadDummyData() {
        if (layoutSkeleton != null) {
            layoutSkeleton.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
        }

        // Simulate network delay
        recyclerViewNotifications.postDelayed(() -> {
            if (!isAdded()) return;
            if (layoutSkeleton != null) layoutSkeleton.setVisibility(View.GONE);
            recyclerViewNotifications.setVisibility(View.VISIBLE);

            notificationList.clear();
            notificationList.add(new Notification(Notification.TYPE_WARNING, "Book Overdue", "The Republic of Plato is now 3 days overdue.", "2h ago"));
            notificationList.add(new Notification(Notification.TYPE_INFO, "Reservation Ready", "Your reserved copy of Modern Architecture is ready.", "5h ago"));
            notificationList.add(new Notification(Notification.TYPE_SUCCESS, "Renewal Successful", "You have successfully extended the loan period.", "Yesterday"));

            notificationAdapter.submitList(notificationList);
            updateEmptyState();
        }, 1000);
    }

    private void setupClickListeners() {
        if (buttonClearAll != null) {
            buttonClearAll.setOnClickListener(v -> {
                if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.VISIBLE);
                
                // Simulate API call
                v.postDelayed(() -> {
                    if (!isAdded()) return;
                    if (layoutLoadingOverlay != null) layoutLoadingOverlay.setVisibility(View.GONE);
                    notificationList.clear();
                    notificationAdapter.clear();
                    updateEmptyState();
                    Toast.makeText(requireContext(), "All notifications cleared", Toast.LENGTH_SHORT).show();
                }, 800);
            });
        }
        if (buttonRefresh != null) {
            buttonRefresh.setOnClickListener(v -> {
                loadDummyData();
            });
        }
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
