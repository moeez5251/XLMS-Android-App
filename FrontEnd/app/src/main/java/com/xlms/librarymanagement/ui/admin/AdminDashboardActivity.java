package com.xlms.librarymanagement.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.ui.login.LoginActivity;
import com.xlms.librarymanagement.utils.SessionManager;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private FrameLayout mainContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupViewPager();
        setupBottomNavigation();
        setupClickListeners();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        mainContentFrame = findViewById(R.id.mainContentFrame);
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

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                // Close any detail screens first
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                }

                int position = 0;
                int id = item.getItemId();

                if (id == R.id.nav_dashboard) position = 0;
                else if (id == R.id.nav_members) position = 2;
                else if (id == R.id.nav_notifications) position = 3;
                else if (id == R.id.nav_profile) position = 4;

                viewPager.setCurrentItem(position, true);
                bottomNavigation.getMenu().getItem(position).setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }

    /**
     * Opens a detail screen (Add Book / Info Book) on TOP of the current screen.
     * Uses 'add' transaction so the ViewPager remains visible in the background.
     */
    public void openDetailScreen(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_right_in,  // Enter: Slide in from right
                0,                      // Exit: Current screen stays put (it's behind)
                0,                      // PopEnter: Current screen stays put (it's revealed)
                R.anim.slide_right_out  // PopExit: Top screen slides out to right
            )
            .add(R.id.mainContentFrame, fragment) // Use .add instead of .replace
            .addToBackStack("detail")
            .commit();
    }

    /**
     * Closes the top detail screen.
     */
    public void closeDetailScreen() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onBackPressed() {
        // If detail screen is open, close it
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            closeDetailScreen();
            return;
        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
