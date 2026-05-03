package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Book;
import com.xlms.librarymanagement.model.Notification;
import com.xlms.librarymanagement.ui.components.PieChartView;
import com.xlms.librarymanagement.ui.components.StackedAreaChartView;
import com.xlms.librarymanagement.ui.components.MonthData;

import java.util.ArrayList;
import java.util.List;

public class DashboardContentFragment extends Fragment implements AdminDashboardActivity.Refreshable {

    private LinearLayout statsContainer, activityListContainer;
    private Button buttonAddBook;
    private PieChartView pieChartView;
    private StackedAreaChartView stackedAreaChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        statsContainer = view.findViewById(R.id.statsContainer);
        activityListContainer = view.findViewById(R.id.activityListContainer);
        buttonAddBook = view.findViewById(R.id.buttonAddBook);
        pieChartView = view.findViewById(R.id.pieChartView);
        stackedAreaChart = view.findViewById(R.id.stackedAreaChart);
        
        showSkeletonLoader();
        fetchDashboardData();
        setupAddBookButton();
    }

    private void showSkeletonLoader() {
        if (statsContainer == null) return;
        statsContainer.removeAllViews();
        android.view.animation.Animation shimmerAnim = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.shimmer_animation);
        
        for (int i = 0; i < 4; i++) {
            View skeleton = LayoutInflater.from(requireContext()).inflate(R.layout.layout_skeleton_stat_card, statsContainer, false);
            View shimmerView = skeleton.findViewById(R.id.shimmerView);
            if (shimmerView != null) {
                shimmerView.startAnimation(shimmerAnim);
            }
            statsContainer.addView(skeleton);
        }
    }

    private void setupAddBookButton() {
        if (buttonAddBook != null) {
            buttonAddBook.setOnClickListener(v -> {
                openAddBookFragment();
            });
        }
    }

    private void openAddBookFragment() {
        AddBookFragment fragment = new AddBookFragment();
        fragment.setOnBookActionListener(new AddBookFragment.OnBookActionListener() {
            @Override
            public void onBookAdded(Book book) {
                Toast.makeText(requireContext(), "Book added: " + book.getTitle(), Toast.LENGTH_SHORT).show();
                closeDetailFragment();
                fetchDashboardData(); // Refresh data
            }

            @Override
            public void onCancel() {
                closeDetailFragment();
            }
        });
        openDetailFragment(fragment);
    }

    private void openDetailFragment(Fragment fragment) {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).openDetailScreen(fragment);
        }
    }

    private void closeDetailFragment() {
        if (getActivity() instanceof AdminDashboardActivity) {
            ((AdminDashboardActivity) getActivity()).closeDetailScreen();
        }
    }

    private void fetchDashboardData() {
        if (!isAdded()) return;
        com.xlms.librarymanagement.api.ApiService apiService = com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext());
        
        // Fetch Stats
        apiService.getDashboardData().enqueue(new retrofit2.Callback<com.xlms.librarymanagement.api.DashboardDataResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.xlms.librarymanagement.api.DashboardDataResponse> call, retrofit2.Response<com.xlms.librarymanagement.api.DashboardDataResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(getContext(), "Failed to load dashboard data", Toast.LENGTH_SHORT).show();
                    loadStatsData(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.xlms.librarymanagement.api.DashboardDataResponse> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadStatsData(null);
            }
        });

        // Fetch Notifications for Recent Activity
        apiService.getNotifications().enqueue(new retrofit2.Callback<List<Notification>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Notification>> call, retrofit2.Response<List<Notification>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    setupActivityList(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Notification>> call, Throwable t) {
                // Keep default placeholder logic if needed
            }
        });
    }

    private void updateUI(com.xlms.librarymanagement.api.DashboardDataResponse data) {
        loadStatsData(data);
        setupPieChartLegend(data);
        if (pieChartView != null && data != null) {
            pieChartView.setData(data.getTotalBorrowers(), data.getAvailableBooks(), data.getOverdueBooks());
        }
        setupStackedAreaChart();
    }

    private void loadStatsData(com.xlms.librarymanagement.api.DashboardDataResponse data) {
        if (statsContainer == null) return;
        statsContainer.removeAllViews();
        
        int totalBooks = data != null ? data.getTotalBooks() : 0;
        int lendedBooks = data != null ? data.getTotalBorrowers() : 0;
        int availableBooks = data != null ? data.getAvailableBooks() : 0;
        int totalUsers = data != null ? data.getTotalUsers() : 0;
        int overdueBooks = data != null ? data.getOverdueBooks() : 0;
        
        addStatCard(totalBooks, "Total Books", R.drawable.ic_book_bookmark, R.drawable.icon_background_secondary, false);
        addStatCard(lendedBooks, "Lended Books", R.drawable.ic_book_edit, R.drawable.icon_background_primary, false);
        addStatCard(availableBooks, "Available Books", R.drawable.ic_menu_book, R.drawable.icon_background_tertiary, false);
        addStatCard(totalUsers, "Total Users", R.drawable.ic_group, R.drawable.icon_background_secondary, false);
        addStatCard(overdueBooks, "Overdue Books", R.drawable.ic_alert_circle, R.drawable.icon_background_error, true);
    }

    private void setupStackedAreaChart() {
        if (stackedAreaChart == null) return;
        
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        
        List<MonthData> chartData = new ArrayList<>();
        for (String month : months) {
            int desktop = (int) (Math.random() * 250) + 50;
            int mobile = (int) (Math.random() * 150) + 30;
            chartData.add(new MonthData(month, desktop, mobile));
        }
        
        stackedAreaChart.setData(chartData);
    }

    private void setupPieChartLegend(com.xlms.librarymanagement.api.DashboardDataResponse data) {
        LinearLayout legendContainer = getView().findViewById(R.id.pieChartLegend);
        if (legendContainer == null || data == null) return;
        legendContainer.removeAllViews();

        addLegendItem(legendContainer, "Lended", data.getTotalBorrowers(), "#fe4c00");
        addLegendItem(legendContainer, "Available", data.getAvailableBooks(), "#00e597");
        addLegendItem(legendContainer, "OverDue", data.getOverdueBooks(), "#0092f6");
    }

    private void addLegendItem(LinearLayout container, String type, int value, String colorHex) {
        if (!isAdded()) return;
        LinearLayout itemLayout = new LinearLayout(requireContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        itemLayout.setPadding(0, 8, 0, 8);

        View colorBox = new View(requireContext());
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(40, 40);
        boxParams.setMargins(0, 0, 16, 0);
        colorBox.setLayoutParams(boxParams);
        colorBox.setBackgroundColor(android.graphics.Color.parseColor(colorHex));

        TextView textView = new TextView(requireContext());
        textView.setText(type + ": " + value);
        textView.setTextSize(14);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);

        itemLayout.addView(colorBox);
        itemLayout.addView(textView);
        container.addView(itemLayout);
    }

    private void addStatCard(int value, String label, int iconRes, int bgRes, boolean isOverdue) {
        if (!isAdded()) return;
        View card = LayoutInflater.from(requireContext()).inflate(R.layout.stat_card_simple, statsContainer, false);
        
        ImageView icon = card.findViewById(R.id.statIcon);
        View iconBg = card.findViewById(R.id.iconBackground);
        TextView valueText = card.findViewById(R.id.statValue);
        TextView labelText = card.findViewById(R.id.statLabel);
        
        icon.setImageResource(iconRes);
        iconBg.setBackgroundResource(bgRes);
        
        if (isOverdue) {
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.error));
            valueText.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
        }
        
        valueText.setText(String.valueOf(value));
        labelText.setText(label);
        
        statsContainer.addView(card);
    }

    private void setupActivityList(List<Notification> notifications) {
        if (activityListContainer == null || !isAdded()) return;
        activityListContainer.removeAllViews();
        
        int count = Math.min(notifications.size(), 5);
        for (int i = 0; i < count; i++) {
            Notification notification = notifications.get(i);
            View activityItem = LayoutInflater.from(requireContext()).inflate(R.layout.activity_item, activityListContainer, false);
            
            TextView activityText = activityItem.findViewById(R.id.activityText);
            TextView timestampText = activityItem.findViewById(R.id.timestampText);
            
            activityText.setText(notification.getDescription());
            timestampText.setText(com.xlms.librarymanagement.adapter.NotificationAdapter.getRelativeTime(notification.getTime()));
            
            activityListContainer.addView(activityItem);
        }
    }

    @Override
    public void refreshData() {
        fetchDashboardData();
    }

    private void setupActivityList() {
        if (activityListContainer == null) return;
        activityListContainer.removeAllViews();
        
        String[] activities = {
            "Dashboard initialised",
            "Waiting for server activity..."
        };
        
        for (String act : activities) {
            View activityItem = LayoutInflater.from(requireContext()).inflate(R.layout.activity_item, activityListContainer, false);
            TextView activityText = activityItem.findViewById(R.id.activityText);
            activityText.setText(act);
            activityListContainer.addView(activityItem);
        }
    }
}
