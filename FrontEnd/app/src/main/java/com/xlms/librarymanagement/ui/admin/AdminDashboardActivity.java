package com.xlms.librarymanagement.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.NotificationAdapter;
import com.xlms.librarymanagement.model.Notification;
import com.xlms.librarymanagement.ui.login.LoginActivity;
import com.xlms.librarymanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private FrameLayout mainContentFrame;
    private ImageButton buttonNotifications;
    
    private PopupWindow notificationPopup;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupViewPager();
        setupBottomNavigation();
        setupClickListeners();
        loadDummyNotifications();
        setupBackStackListener();
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

        // Also sync the navigation view (sidebar)
        NavigationView navigationView = findViewById(R.id.navigationView);
        if (navigationView != null) {
            int navItemId = -1;
            if (position == 0) navItemId = R.id.nav_dashboard;
            else if (position == 1) navItemId = R.id.nav_manage_books;
            else if (position == 2) navItemId = R.id.nav_members;
            else if (position == 3) navItemId = R.id.nav_notifications;
            else if (position == 4) navItemId = R.id.nav_profile;

            if (navItemId != -1) {
                navigationView.getMenu().setGroupCheckable(0, true, true);
                navigationView.getMenu().findItem(navItemId).setChecked(true);
            }
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        mainContentFrame = findViewById(R.id.mainContentFrame);
        buttonNotifications = findViewById(R.id.buttonNotifications);
    }

    private void setupViewPager() {
        DashboardViewPagerAdapter adapter = new DashboardViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Sync ViewPager -> BottomNav
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            // If detail screen is open, close it first
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
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
        ImageButton buttonMenu = findViewById(R.id.buttonMenu);
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        if (buttonMenu != null) {
            buttonMenu.setOnClickListener(v -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        if (buttonLogout != null) {
            buttonLogout.setOnClickListener(v -> {
                // Clear session
                SessionManager sessionManager = new SessionManager(this);
                sessionManager.clearSession();

                // Redirect to login
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        // Notification Popup
        if (buttonNotifications != null) {
            buttonNotifications.setOnClickListener(v -> showNotificationPopup(v));
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                // Close any detail screens first
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                }

                int position = 0;
                int id = item.getItemId();

                if (id == R.id.nav_dashboard) position = 0;
                else if (id == R.id.nav_manage_books) position = 1;
                else if (id == R.id.nav_resources) {
                    openResourcesScreen();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                else if (id == R.id.nav_members) position = 2;
                else if (id == R.id.nav_notifications) position = 3;
                else if (id == R.id.nav_profile) position = 4;
                else if (id == R.id.nav_lended_books) {
                    openLendedBooksScreen();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }

                viewPager.setCurrentItem(position, true);
                bottomNavigation.getMenu().getItem(position).setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
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

        // If detail screen is open, close it
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            closeDetailScreen();
            return;
        }

        // If sidebar is open, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
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
