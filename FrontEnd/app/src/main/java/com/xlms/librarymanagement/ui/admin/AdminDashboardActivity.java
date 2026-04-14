package com.xlms.librarymanagement.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.NotificationAdapter;
import com.xlms.librarymanagement.model.Notification;
import com.xlms.librarymanagement.ui.login.LoginActivity;
import com.xlms.librarymanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private FrameLayout mainContentFrame;
    private ImageButton buttonNotifications, buttonOpenDrawer;
    private View backdropOverlay;
    private LinearLayout bottomSheetContent;
    
    private PopupWindow notificationPopup;
    private List<Notification> notificationList;
    private boolean isDrawerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupBottomSheet();
        setupViewPager();
        setupBottomNavigation();
        setupClickListeners();
        loadDummyNotifications();
        setupBackStackListener();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        mainContentFrame = findViewById(R.id.mainContentFrame);
        buttonNotifications = findViewById(R.id.buttonNotifications);
        buttonOpenDrawer = findViewById(R.id.buttonOpenDrawer);
        backdropOverlay = findViewById(R.id.backdropOverlay);
        bottomSheetContent = findViewById(R.id.bottomSheetContent);
    }

    private void setupBottomSheet() {
        bottomSheetContent.post(() -> {
            // Set sheet height to 65% of screen so it doesn't cover everything
            int screenHeight = bottomSheetContent.getResources().getDisplayMetrics().heightPixels;
            ViewGroup.LayoutParams params = bottomSheetContent.getLayoutParams();
            params.height = (int) (screenHeight * 0.65);
            bottomSheetContent.setLayoutParams(params);
            bottomSheetContent.requestLayout();

            // Hide bottom sheet initially using translationY
            bottomSheetContent.setTranslationY(bottomSheetContent.getHeight());
        });
    }

    public void openBottomSheet() {
        if (isDrawerOpen) return;
        isDrawerOpen = true;
        backdropOverlay.setVisibility(View.VISIBLE);
        backdropOverlay.animate().alpha(0.2f).setDuration(300).start();
        // Slide up fully so entire sheet is visible
        bottomSheetContent.animate()
            .translationY(0)
            .setDuration(300)
            .setInterpolator(new DecelerateInterpolator())
            .start();
        updateNavigationHighlight();
    }

    private void closeBottomSheet() {
        if (!isDrawerOpen) return;
        isDrawerOpen = false;
        bottomSheetContent.animate()
            .translationY(bottomSheetContent.getHeight())
            .setDuration(300)
            .setInterpolator(new AccelerateInterpolator())
            .withEndAction(() -> {
                backdropOverlay.setVisibility(View.GONE);
                backdropOverlay.setAlpha(0);
            })
            .start();
    }

    private void setupViewPager() {
        DashboardViewPagerAdapter adapter = new DashboardViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigation.getMenu().getItem(position).setChecked(true);
                syncSidebarWithViewPager(position);
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            if (isDrawerOpen) closeBottomSheet();

            int position = 0;
            int id = item.getItemId();
            if (id == R.id.bottom_dashboard) position = 0;
            else if (id == R.id.bottom_books) position = 1;
            else if (id == R.id.bottom_members) position = 2;
            else if (id == R.id.bottom_alerts) position = 3;
            else if (id == R.id.bottom_profile) position = 4;

            viewPager.setCurrentItem(position, true);
            return true;
        });
    }

    private void setupClickListeners() {
        Button buttonLogout = findViewById(R.id.buttonLogout);

        if (buttonOpenDrawer != null) {
            buttonOpenDrawer.setOnClickListener(v -> openBottomSheet());
        }

        if (buttonNotifications != null) {
            buttonNotifications.setOnClickListener(v -> showNotificationPopup(v));
        }

        if (buttonLogout != null) {
            buttonLogout.setOnClickListener(v -> {
                SessionManager sessionManager = new SessionManager(this);
                sessionManager.clearSession();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        setupNavigationLink(R.id.navDashboard, 0);
        setupNavigationLink(R.id.navResources, -1);
        setupNavigationLink(R.id.navManageBooks, 1);
        setupNavigationLink(R.id.navLendedBooks, -2);
        setupNavigationLink(R.id.navMembers, 2);
        setupNavigationLink(R.id.navNotifications, 3);
        setupNavigationLink(R.id.navProfile, 4);

        if (backdropOverlay != null) {
            backdropOverlay.setOnClickListener(v -> closeBottomSheet());
        }
    }

    private void setupNavigationLink(int viewId, int viewPagerPosition) {
        View view = findViewById(viewId);
        if (view == null) return;

        view.setOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }

            if (viewPagerPosition == -1) {
                openResourcesScreen();
                closeBottomSheet();
                return;
            } else if (viewPagerPosition == -2) {
                openLendedBooksScreen();
                closeBottomSheet();
                return;
            }

            viewPager.setCurrentItem(viewPagerPosition, true);
            closeBottomSheet();
        });
    }

    private void updateNavigationHighlight() {
        int[] navIds = {R.id.navDashboard, R.id.navResources, R.id.navManageBooks,
                       R.id.navLendedBooks, R.id.navMembers, R.id.navNotifications, R.id.navProfile};
        
        int currentNavId = getCurrentNavItemId();
        
        for (int navId : navIds) {
            View navItem = findViewById(navId);
            if (navItem == null) continue;

            LinearLayout layout = (LinearLayout) navItem;
            ImageView icon = (ImageView) layout.getChildAt(0);
            TextView text = (TextView) layout.getChildAt(1);

            if (navId == currentNavId) {
                layout.setBackgroundResource(R.drawable.nav_item_selected_background);
                icon.setColorFilter(getResources().getColor(R.color.primary));
                text.setTextColor(getResources().getColor(R.color.primary));
                text.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                layout.setBackground(null);
                icon.setColorFilter(getResources().getColor(R.color.on_surface_variant));
                text.setTextColor(getResources().getColor(R.color.on_surface_variant));
                text.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private int getCurrentNavItemId() {
        int position = viewPager.getCurrentItem();
        switch (position) {
            case 0: return R.id.navDashboard;
            case 1: return R.id.navManageBooks;
            case 2: return R.id.navMembers;
            case 3: return R.id.navNotifications;
            case 4: return R.id.navProfile;
            default: return R.id.navDashboard;
        }
    }

    private void loadDummyNotifications() {
        notificationList = new ArrayList<>();
        notificationList.add(new Notification(
            Notification.TYPE_WARNING,
            "Book Overdue",
            "\"The Republic of Plato\" is now 3 days overdue.",
            "2h ago"
        ));
        notificationList.add(new Notification(
            Notification.TYPE_INFO,
            "Reservation Ready",
            "Your reserved copy of \"Modern Architecture\" is ready.",
            "5h ago"
        ));
        notificationList.add(new Notification(
            Notification.TYPE_SUCCESS,
            "Renewal Successful",
            "You have successfully extended the loan period.",
            "Yesterday"
        ));
    }

    private void showNotificationPopup(View anchorView) {
        if (notificationPopup != null && notificationPopup.isShowing()) {
            notificationPopup.dismiss();
            return;
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_notifications, null);

        RecyclerView recyclerView = popupView.findViewById(R.id.recyclerViewPopupNotifications);
        LinearLayout layoutEmpty = popupView.findViewById(R.id.layoutPopupEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NotificationAdapter adapter = new NotificationAdapter();
        adapter.submitList(notificationList);
        recyclerView.setAdapter(adapter);

        if (notificationList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }

        popupView.findViewById(R.id.buttonViewAll).setOnClickListener(v -> {
            notificationPopup.dismiss();
            viewPager.setCurrentItem(3, true);
            bottomNavigation.getMenu().getItem(3).setChecked(true);
        });

        notificationPopup = new PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        );
        notificationPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notificationPopup.setOutsideTouchable(true);
        notificationPopup.setElevation(8);

        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        
        int xPos = location[0] - (popupView.getMeasuredWidth() - anchorView.getWidth());
        int yPos = location[1] + anchorView.getHeight() + 16;

        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                         View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        notificationPopup.showAtLocation(anchorView, Gravity.NO_GRAVITY, xPos, yPos);
    }

    private void setupBackStackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                int currentPos = viewPager.getCurrentItem();
                syncSidebarWithViewPager(currentPos);
            }
        });
    }

    private void syncSidebarWithViewPager(int position) {
        bottomNavigation.getMenu().getItem(position).setChecked(true);
        if (isDrawerOpen) {
            updateNavigationHighlight();
        }
    }

    public void openDetailScreen(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_right_in,
                0,
                0,
                R.anim.slide_right_out
            )
            .add(R.id.mainContentFrame, fragment)
            .addToBackStack("detail")
            .commit();
    }

    public void openLendedBooksScreen() {
        LendedBooksFragment fragment = new LendedBooksFragment();
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_right_in,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_right_out
            )
            .add(R.id.mainContentFrame, fragment)
            .addToBackStack("lended_books")
            .commit();
    }

    public void openResourcesScreen() {
        ResourcesFragment fragment = new ResourcesFragment();
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_right_in,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_right_out
            )
            .add(R.id.mainContentFrame, fragment)
            .addToBackStack("resources")
            .commit();
    }

    public void closeDetailScreen() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onBackPressed() {
        if (notificationPopup != null && notificationPopup.isShowing()) {
            notificationPopup.dismiss();
            return;
        }

        if (isDrawerOpen) {
            closeBottomSheet();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            closeDetailScreen();
            return;
        }

        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, true);
            bottomNavigation.getMenu().getItem(0).setChecked(true);
            syncSidebarWithViewPager(0);
            return;
        }

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationPopup != null) {
            notificationPopup.dismiss();
        }
    }
}
