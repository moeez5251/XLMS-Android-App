package com.xlms.libraryadmin.ui.admin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xlms.libraryadmin.R;
import com.xlms.libraryadmin.ui.login.LoginActivity;

/**
 * Admin Dashboard with Bottom Navigation
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private ImageButton buttonNotifications, buttonLogout;
    private Button buttonAddBook;
    private TextView textViewWelcome;
    private LinearLayout statsContainer, barChartContainer, activityListContainer;
    private BottomNavigationView bottomNavigation;

    private static final int TOTAL_BOOKS = 29;
    private static final int LENDED_BOOKS = 74;
    private static final int AVAILABLE_BOOKS = 29;
    private static final int TOTAL_USERS = 12;
    private static final int OVERDUE_BOOKS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupClickListeners();
        setupBottomNavigation();
        loadStatsData();
        setupBarChart();
        setupActivityList();
    }

    private void initViews() {
        buttonNotifications = findViewById(R.id.buttonNotifications);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonAddBook = findViewById(R.id.buttonAddBook);
        textViewWelcome = findViewById(R.id.textViewWelcome);
        statsContainer = findViewById(R.id.statsContainer);
        barChartContainer = findViewById(R.id.barChartContainer);
        activityListContainer = findViewById(R.id.activityListContainer);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupClickListeners() {
        buttonNotifications.setOnClickListener(v -> {
            // TODO: Open notifications
        });
        
        buttonLogout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        });
        
        buttonAddBook.setOnClickListener(v -> {
            // TODO: Open Add Book screen
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.bottom_dashboard);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_dashboard) {
                // Already on dashboard
                return true;
            } else if (itemId == R.id.bottom_books) {
                // TODO: Navigate to Books
                return true;
            } else if (itemId == R.id.bottom_members) {
                // TODO: Navigate to Members
                return true;
            } else if (itemId == R.id.bottom_alerts) {
                // TODO: Navigate to Alerts
                return true;
            } else if (itemId == R.id.bottom_profile) {
                // TODO: Navigate to Profile
                return true;
            }
            return false;
        });
    }

    private void loadStatsData() {
        if (statsContainer == null) return;
        
        addStatCard(TOTAL_BOOKS, getString(R.string.total_books), R.drawable.ic_auto_stories, R.drawable.icon_background_secondary);
        addStatCard(LENDED_BOOKS, getString(R.string.lended_books), R.drawable.ic_import_contacts, R.drawable.icon_background_primary);
        addStatCard(AVAILABLE_BOOKS, getString(R.string.available_books), R.drawable.ic_menu_book, R.drawable.icon_background_tertiary);
        addStatCard(TOTAL_USERS, getString(R.string.total_users), R.drawable.ic_group, R.drawable.icon_background_secondary);
        addStatCard(OVERDUE_BOOKS, getString(R.string.overdue_books), R.drawable.ic_report, R.drawable.icon_background_error);
    }

    private void addStatCard(int value, String label, int iconRes, int bgRes) {
        View card = LayoutInflater.from(this).inflate(R.layout.stat_card_simple, statsContainer, false);
        
        ImageView icon = card.findViewById(R.id.statIcon);
        View iconBg = card.findViewById(R.id.iconBackground);
        TextView valueText = card.findViewById(R.id.statValue);
        TextView labelText = card.findViewById(R.id.statLabel);
        
        icon.setImageResource(iconRes);
        iconBg.setBackgroundResource(bgRes);
        valueText.setText(String.valueOf(value));
        labelText.setText(label);
        
        statsContainer.addView(card);
    }

    private void setupBarChart() {
        if (barChartContainer == null) return;
        
        barChartContainer.removeAllViews();
        
        int[] heights = {25, 50, 75, 66, 100, 80};
        int spacing = getResources().getDimensionPixelSize(R.dimen.chart_bar_spacing);
        
        for (int i = 0; i < heights.length; i++) {
            LinearLayout barContainer = new LinearLayout(this);
            barContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            barContainer.setOrientation(LinearLayout.VERTICAL);
            barContainer.setGravity(Gravity.BOTTOM);
            
            ((LinearLayout.LayoutParams) barContainer.getLayoutParams()).setMargins(
                spacing / 2, 0, spacing / 2, 0);
            
            View bar = new View(this);
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
            View activityItem = LayoutInflater.from(this)
                .inflate(R.layout.activity_item, activityListContainer, false);
            
            TextView activityText = activityItem.findViewById(R.id.activityText);
            TextView timestampText = activityItem.findViewById(R.id.timestampText);
            
            activityText.setText(activities[i]);
            timestampText.setText(timestamps[i]);
            
            activityListContainer.addView(activityItem);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
