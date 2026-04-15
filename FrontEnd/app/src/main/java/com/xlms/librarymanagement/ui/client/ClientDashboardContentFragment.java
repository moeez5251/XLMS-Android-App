package com.xlms.librarymanagement.ui.client;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.xlms.librarymanagement.R;

/**
 * Client Dashboard Content Fragment - Shows welcome, metrics, and charts
 */
public class ClientDashboardContentFragment extends Fragment {

    private TextView textViewGreeting;
    private TextView textCirclePercentage;
    private TextView textOverdueCount;
    private TextView textReturnedCount;
    private CircularProgressIndicator progressCircleBorrowed;
    private LinearLayout barChartContainer;

    // Dummy data
    private int lendedBooks = 0;
    private int overdueBooks = 0;
    private int reservedBooks = 0;
    private int returnedBooks = 9;
    private int totalBorrowed = 9;

    private final int[] monthlyActivity = {40, 80, 53, 100, 67, 93, 47, 33, 73, 60, 87, 100};
    private final String[] monthLabels = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                                          "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_dashboard_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupMetricCards();
        setupCircularChart();
        setupBarChart();
    }

    private void initViews(View view) {
        textViewGreeting = view.findViewById(R.id.textViewGreeting);
        textCirclePercentage = view.findViewById(R.id.textCirclePercentage);
        textOverdueCount = view.findViewById(R.id.textOverdueCount);
        textReturnedCount = view.findViewById(R.id.textReturnedCount);
        progressCircleBorrowed = view.findViewById(R.id.progressCircleBorrowed);
        barChartContainer = view.findViewById(R.id.barChartContainer);

        if (getArguments() != null) {
            String userName = getArguments().getString("USER_NAME", "User");
            textViewGreeting.setText("Hello " + userName);
        }
    }

    private void setupMetricCards() {
        // Lended Books Card
        View cardLended = getView().findViewById(R.id.cardLendedBooks);
        setupCard(cardLended,
                R.id.metricIcon, R.id.metricValue, R.id.metricLabel,
                R.drawable.ic_menu_book,
                String.valueOf(lendedBooks),
                "Lended",
                R.color.primary);

        // Overdue Books Card
        View cardOverdue = getView().findViewById(R.id.cardOverdueBooks);
        setupCard(cardOverdue,
                R.id.metricIconOverdue, R.id.metricValueOverdue, R.id.metricLabelOverdue,
                R.drawable.ic_event_busy,
                String.valueOf(overdueBooks),
                "Overdue",
                R.color.error);

        // Reserved Books Card
        View cardReserved = getView().findViewById(R.id.cardReservedBooks);
        setupCard(cardReserved,
                R.id.metricIconReserved, R.id.metricValueReserved, R.id.metricLabelReserved,
                R.drawable.ic_bookmark_add,
                String.valueOf(reservedBooks),
                "Reserved",
                R.color.secondary_container);
    }

    private void setupCard(View card, int iconId, int valueId, int labelId,
                           int iconRes, String valueText, String labelText, int bgTintColor) {
        if (card == null) return;

        View icon = card.findViewById(iconId);
        TextView value = card.findViewById(valueId);
        TextView label = card.findViewById(labelId);

        if (icon != null && icon instanceof android.widget.ImageView) {
            icon.setBackgroundResource(R.drawable.metric_icon_background);
            ((android.widget.ImageView) icon).setImageResource(iconRes);

            android.graphics.drawable.Drawable drawable =
                    ((android.widget.ImageView) icon).getDrawable();
            if (drawable != null) drawable.setTint(0xFFFFFFFF);

            android.graphics.drawable.Drawable bg = icon.getBackground();
            if (bg instanceof GradientDrawable) {
                ((GradientDrawable) bg).setTint(
                        getResources().getColor(bgTintColor, null));
            }
        }

        if (value != null) {
            value.setText(valueText);
            if (bgTintColor == R.color.error) {
                value.setTextColor(getResources().getColor(R.color.error, null));
            } else if (bgTintColor == R.color.secondary_container) {
                value.setTextColor(getResources().getColor(
                        R.color.on_secondary_container, null));
            } else {
                value.setTextColor(getResources().getColor(R.color.primary, null));
            }
        }

        if (label != null) {
            label.setText(labelText);
            label.setTextColor(getResources().getColor(
                    R.color.on_surface_variant, null));
        }
    }

    private void setupCircularChart() {
        int percentage = totalBorrowed > 0
                ? (returnedBooks * 100 / totalBorrowed) : 100;
        progressCircleBorrowed.setProgress(percentage);
        textCirclePercentage.setText(percentage + "%");

        textOverdueCount.setText(overdueBooks + " (" + (100 - percentage) + "%)");
        textReturnedCount.setText(returnedBooks + " (" + percentage + ".0%)");
    }

    private void setupBarChart() {
        barChartContainer.removeAllViews();

        int maxValue = 0;
        for (int v : monthlyActivity) if (v > maxValue) maxValue = v;

        int barWidth = getResources().getDisplayMetrics().widthPixels
                / (monthlyActivity.length * 2 + 4);
        if (barWidth < 12) barWidth = 12;
        if (barWidth > 36) barWidth = 36;

        int gapPx = (int) (4 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < monthlyActivity.length; i++) {
            LinearLayout group = new LinearLayout(requireContext());
            group.setOrientation(LinearLayout.VERTICAL);
            group.setGravity(android.view.Gravity.BOTTOM
                    | android.view.Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    barWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            if (i < monthlyActivity.length - 1) lp.setMargins(0, 0, gapPx, 0);
            group.setLayoutParams(lp);

            // Bar
            View bar = new View(requireContext());
            int barHeight = (int) (monthlyActivity[i] * 1.5f);
            bar.setLayoutParams(new LinearLayout.LayoutParams(barWidth, barHeight));
            bar.setBackgroundResource(R.drawable.bar_chart_item_background);

            android.graphics.drawable.Drawable bg = bar.getBackground();
            if (bg instanceof GradientDrawable) {
                if (monthlyActivity[i] == maxValue) {
                    ((GradientDrawable) bg).setTint(
                            getResources().getColor(R.color.primary, null));
                } else {
                    ((GradientDrawable) bg).setTint(
                            getResources().getColor(R.color.surface_container, null));
                }
            }

            // Month label
            TextView monthLabel = new TextView(requireContext());
            monthLabel.setText(monthLabels[i]);
            monthLabel.setTextSize(8);
            boolean highlight = monthlyActivity[i] == maxValue;
            monthLabel.setTextColor(getResources().getColor(
                    highlight ? R.color.primary : R.color.on_surface_variant, null));
            monthLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            monthLabel.setGravity(android.view.Gravity.CENTER);
            monthLabel.setPadding(0, (int) (4 * getResources().getDisplayMetrics().density), 0, 0);

            group.addView(bar);
            group.addView(monthLabel);
            barChartContainer.addView(group);
        }
    }
}
