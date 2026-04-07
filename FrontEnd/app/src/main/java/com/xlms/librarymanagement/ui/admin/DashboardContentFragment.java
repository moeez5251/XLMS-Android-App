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

import java.util.ArrayList;
import java.util.List;

public class DashboardContentFragment extends Fragment {

    private LinearLayout statsContainer, barChartContainer, activityListContainer;
    private Button buttonAddBook;

    private static final int TOTAL_BOOKS = 29;
    private static final int LENDED_BOOKS = 74;
    private static final int AVAILABLE_BOOKS = 29;
    private static final int TOTAL_USERS = 12;
    private static final int OVERDUE_BOOKS = 0;

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
        barChartContainer = view.findViewById(R.id.barChartContainer);
        activityListContainer = view.findViewById(R.id.activityListContainer);
        buttonAddBook = view.findViewById(R.id.buttonAddBook);
        
        loadStatsData();
        setupBarChart();
        setupActivityList();
        setupAddBookButton();
    }

    private void setupAddBookButton() {
        if (buttonAddBook != null) {
            buttonAddBook.setOnClickListener(v -> {
                // Navigate to Manage Books tab then open Add Book
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

    private void loadStatsData() {
        if (statsContainer == null) return;
        
        addStatCard(TOTAL_BOOKS, getString(R.string.total_books), R.drawable.ic_book_bookmark, R.drawable.icon_background_secondary, false);
        addStatCard(LENDED_BOOKS, getString(R.string.lended_books), R.drawable.ic_book_edit, R.drawable.icon_background_primary, false);
        addStatCard(AVAILABLE_BOOKS, getString(R.string.available_books), R.drawable.ic_menu_book, R.drawable.icon_background_tertiary, false);
        addStatCard(TOTAL_USERS, getString(R.string.total_users), R.drawable.ic_group, R.drawable.icon_background_secondary, false);
        addStatCard(OVERDUE_BOOKS, getString(R.string.overdue_books), R.drawable.ic_alert_circle, R.drawable.icon_background_error, true);
    }

    private void addStatCard(int value, String label, int iconRes, int bgRes, boolean isOverdue) {
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

    private void setupBarChart() {
        if (barChartContainer == null) return;
        
        barChartContainer.removeAllViews();
        
        int[] heights = {25, 50, 75, 66, 100, 80};
        int spacing = 8;
        
        for (int i = 0; i < heights.length; i++) {
            LinearLayout barContainer = new LinearLayout(requireContext());
            barContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            barContainer.setOrientation(LinearLayout.VERTICAL);
            barContainer.setGravity(android.view.Gravity.BOTTOM);
            
            ((LinearLayout.LayoutParams) barContainer.getLayoutParams()).setMargins(
                spacing / 2, 0, spacing / 2, 0);
            
            View bar = new View(requireContext());
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, heights[i]);
            bar.setLayoutParams(barParams);
            bar.setBackgroundResource(R.drawable.chart_bar);
            
            barContainer.addView(bar);
            barChartContainer.addView(barContainer);
        }
    }

    private void setupActivityList() {
        if (activityListContainer == null) return;
        
        String[] activities = {
            "New book added: The Great Gatsby",
            "Book returned: 1984 by George Orwell",
            "New member registered: John Doe",
            "Book borrowed: To Kill a Mockingbird",
            "Overdue notice sent to Jane Smith"
        };
        
        String[] timestamps = {
            "2 hours ago",
            "5 hours ago",
            "1 day ago",
            "2 days ago",
            "3 days ago"
        };
        
        for (int i = 0; i < activities.length; i++) {
            View activityItem = LayoutInflater.from(requireContext())
                .inflate(R.layout.activity_item, activityListContainer, false);
            
            TextView activityText = activityItem.findViewById(R.id.activityText);
            TextView timestampText = activityItem.findViewById(R.id.timestampText);
            
            activityText.setText(activities[i]);
            timestampText.setText(timestamps[i]);
            
            activityListContainer.addView(activityItem);
        }
    }
}
