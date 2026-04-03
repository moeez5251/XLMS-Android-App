package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.xlms.librarymanagement.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

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
        
        ImageButton buttonMenu = findViewById(R.id.buttonMenu);
        ImageButton buttonLogout = findViewById(R.id.buttonLogout);

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
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            });
        }
    }

    private void setupViewPager() {
        DashboardViewPagerAdapter adapter = new DashboardViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 1. Enable smooth swiping (WhatsApp style)
        // We remove the custom transformer to allow native side-by-side sliding.
        // viewPager.setPageTransformer(null); // Default is null, which is the standard slide.
        
        // 2. Pre-load neighbors for instant swipe response
        viewPager.setOffscreenPageLimit(2); 
        
        // 3. Ensure user can swipe
        viewPager.setUserInputEnabled(true);

        // 4. Sync ViewPager -> BottomNav
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
            int position = 0;
            int id = item.getItemId();
            
            if (id == R.id.bottom_dashboard) position = 0;
            else if (id == R.id.bottom_books) position = 1;
            else if (id == R.id.bottom_members) position = 2;
            else if (id == R.id.bottom_alerts) position = 3;
            else if (id == R.id.bottom_profile) position = 4;

            // True enables the smooth scroll animation
            viewPager.setCurrentItem(position, true); 
            return true;
        });
    }

    private void setupClickListeners() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        }
    }
}
