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
import com.xlms.librarymanagement.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private LinearLayout layoutNotifications, layoutEmptyState;
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

        try {
            initViews(view);
            setupRecyclerView();
            loadDummyData();
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error loading notifications", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        layoutNotifications = view.findViewById(R.id.layoutNotifications);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        buttonClearAll = view.findViewById(R.id.buttonClearAll);
        buttonRefresh = view.findViewById(R.id.buttonRefresh);
    }

    private void setupRecyclerView() {
        if (recyclerViewNotifications == null) return;
        
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter();
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);
    }

    private void loadDummyData() {
        if (notificationList == null) return;
        
        notificationList.clear();
        
        notificationList.add(new Notification(
            Notification.TYPE_WARNING,
            "Book Overdue",
            "\"The Republic of Plato\" is now 3 days overdue. Please return it to avoid further fines.",
            "2h ago"
        ));

        notificationList.add(new Notification(
            Notification.TYPE_INFO,
            "Reservation Ready",
            "Your reserved copy of \"Modern Architecture\" is ready for pickup at the Central Desk.",
            "5h ago"
        ));

        notificationList.add(new Notification(
            Notification.TYPE_SUCCESS,
            "Renewal Successful",
            "You have successfully extended the loan period for 3 items in your collection.",
            "Yesterday"
        ));

        notificationList.add(new Notification(
            Notification.TYPE_SYSTEM,
            "Library Hours Update",
            "The Reading Room will be closing early this Friday at 6:00 PM for maintenance.",
            "Oct 12"
        ));

        if (notificationAdapter != null) {
            notificationAdapter.submitList(notificationList);
        }
        updateEmptyState();
    }

    private void setupClickListeners() {
        if (buttonClearAll != null) {
            buttonClearAll.setOnClickListener(v -> {
                if (notificationList != null) {
                    notificationList.clear();
                }
                if (notificationAdapter != null) {
                    notificationAdapter.clear();
                }
                updateEmptyState();
                Toast.makeText(requireContext(), "All notifications cleared", Toast.LENGTH_SHORT).show();
            });
        }

        if (buttonRefresh != null) {
            buttonRefresh.setOnClickListener(v -> {
                loadDummyData();
                Toast.makeText(requireContext(), "Notifications refreshed", Toast.LENGTH_SHORT).show();
            });
        }
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
