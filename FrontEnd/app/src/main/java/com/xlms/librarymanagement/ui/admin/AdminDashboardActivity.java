package com.xlms.librarymanagement.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
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
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContent);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(0);
        
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    isDrawerOpen = false;
                    backdropOverlay.setVisibility(View.GONE);
                    updateNavigationHighlight();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    isDrawerOpen = true;
                    backdropOverlay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                backdropOverlay.setAlpha(0.2f * slideOffset);
            }
        });
    }

    private void setupViewPager() {
        DashboardViewPagerAdapter adapter = new DashboardViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Sync ViewPager -> BottomNav & Sidebar
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
            // If detail screen is open, close it first
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }

            // Close bottom sheet if open
            if (isDrawerOpen) {
                closeBottomSheet();
            }

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

        // Open bottom sheet button (X icon in top bar)
        if (buttonOpenDrawer != null) {
            buttonOpenDrawer.setOnClickListener(v -> openBottomSheet());
        }

        // Notification Popup
        if (buttonNotifications != null) {
            buttonNotifications.setOnClickListener(v -> showNotificationPopup(v));
        }

        // Logout button
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

        // Navigation Links in Bottom Sheet
        setupNavigationLink(R.id.navDashboard, 0);
        setupNavigationLink(R.id.navResources, -1); // Opens Resources fragment
        setupNavigationLink(R.id.navManageBooks, 1);
        setupNavigationLink(R.id.navLendedBooks, -2); // Opens Lended Books fragment
        setupNavigationLink(R.id.navMembers, 2);
        setupNavigationLink(R.id.navNotifications, 3);
        setupNavigationLink(R.id.navProfile, 4);

        // Backdrop click closes bottom sheet
        if (backdropOverlay != null) {
            backdropOverlay.setOnClickListener(v -> closeBottomSheet());
        }
    }

    private void setupNavigationLink(int viewId, int viewPagerPosition) {
        View view = findViewById(viewId);
        if (view == null) return;

        view.setOnClickListener(v -> {
            // Close any detail screens first
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }

            // Handle special cases
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

    public void openBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        updateNavigationHighlight();
    }

    private void closeBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void updateNavigationHighlight() {
        // Reset all navigation items
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

        // Inflate popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_notifications, null);

        // Setup RecyclerView
        RecyclerView recyclerView = popupView.findViewById(R.id.recyclerViewPopupNotifications);
        LinearLayout layoutEmpty = popupView.findViewById(R.id.layoutPopupEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NotificationAdapter adapter = new NotificationAdapter();
        adapter.submitList(notificationList);
        recyclerView.setAdapter(adapter);

        // Show/hide empty state
        if (notificationList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }

        // View All button
        popupView.findViewById(R.id.buttonViewAll).setOnClickListener(v -> {
            notificationPopup.dismiss();
            // Navigate to Notifications tab
            viewPager.setCurrentItem(3, true);
            bottomNavigation.getMenu().getItem(3).setChecked(true);
        });

        // Create popup window
        notificationPopup = new PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        );
        notificationPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notificationPopup.setOutsideTouchable(true);
        notificationPopup.setElevation(8);

        // Show popup below the anchor view
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        
        int xPos = location[0] - (popupView.getMeasuredWidth() - anchorView.getWidth());
        int yPos = location[1] + anchorView.getHeight() + 16;

        // Measure popup first
        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                         View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        notificationPopup.showAtLocation(anchorView, Gravity.NO_GRAVITY, xPos, yPos);
    }

    private void setupBackStackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // When returning from a detail screen, update the sidebar selection
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // Sync with current ViewPager position
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

    // Public methods for fragments to navigate to detail screens
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
        // Dismiss popup if showing
        if (notificationPopup != null && notificationPopup.isShowing()) {
            notificationPopup.dismiss();
            return;
        }

        // Close bottom sheet if open
        if (isDrawerOpen) {
            closeBottomSheet();
            return;
        }

        // If detail screen is open, close it
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            closeDetailScreen();
            return;
        }

        // If not on tab 0 (Dashboard), navigate to tab 0
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, true);
            bottomNavigation.getMenu().getItem(0).setChecked(true);
            syncSidebarWithViewPager(0);
            return;
        }

        // If on tab 0, close the app
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
