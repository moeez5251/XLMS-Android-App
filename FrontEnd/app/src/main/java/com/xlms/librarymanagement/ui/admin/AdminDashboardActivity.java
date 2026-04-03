package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.xlms.librarymanagement.R;

/**
 * Admin Dashboard with Bottom Navigation and Swipe Gestures
 * Uses FrameLayout + manual fragment transactions + swipe gestures
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboard";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton buttonMenu;
    private BottomNavigationView bottomNavigation;
    private View fragmentContainer;
    
    private Fragment currentFragment;
    private final Fragment[] fragments = new Fragment[5];
    private int currentFragmentIndex = 0;
    
    // Swipe gesture detector
    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_admin_dashboard);
            Log.d(TAG, "=== onCreate started ===");
            
            initViews();
            Log.d(TAG, "Views initialized");
            
            setupFragments();
            Log.d(TAG, "Fragments created");
            
            setupBottomNavigation();
            Log.d(TAG, "Bottom navigation setup");
            
            setupClickListeners();
            Log.d(TAG, "Click listeners setup");
            
            setupSwipeGesture();
            Log.d(TAG, "Swipe gesture setup");
            
            // Show Dashboard by default
            switchFragment(0);
            bottomNavigation.setSelectedItemId(R.id.bottom_dashboard);
            
            Log.d(TAG, "=== onCreate completed ===");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        buttonMenu = findViewById(R.id.buttonMenu);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        
        Log.d(TAG, "drawerLayout: " + (drawerLayout != null));
        Log.d(TAG, "navigationView: " + (navigationView != null));
        Log.d(TAG, "buttonMenu: " + (buttonMenu != null));
        Log.d(TAG, "bottomNavigation: " + (bottomNavigation != null));
        Log.d(TAG, "fragmentContainer: " + (fragmentContainer != null));
    }

    private void setupFragments() {
        fragments[0] = new DashboardContentFragment();
        fragments[1] = PlaceholderFragment.newInstance("Books");
        fragments[2] = PlaceholderFragment.newInstance("Members");
        fragments[3] = PlaceholderFragment.newInstance("Alerts");
        fragments[4] = PlaceholderFragment.newInstance("Profile");
        
        Log.d(TAG, "Created 5 fragments");
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int position = -1;
                
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_dashboard) {
                    position = 0;
                } else if (itemId == R.id.bottom_books) {
                    position = 1;
                } else if (itemId == R.id.bottom_members) {
                    position = 2;
                } else if (itemId == R.id.bottom_alerts) {
                    position = 3;
                } else if (itemId == R.id.bottom_profile) {
                    position = 4;
                }
                
                if (position >= 0 && position != currentFragmentIndex) {
                    Log.d(TAG, ">>> Bottom nav tapped, switching to: " + position);
                    switchFragment(position);
                }
                return true;
            });
        }
    }

    private void setupSwipeGesture() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffX = e2.getX() - e1.getX();
                    float diffY = e2.getY() - e1.getY();
                    
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        // Horizontal swipe detected
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                // Swipe Right - go to previous tab
                                onSwipeRight();
                            } else {
                                // Swipe Left - go to next tab
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Pass touch events to gesture detector
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void onSwipeLeft() {
        // Swipe left - go to next tab
        int nextIndex = currentFragmentIndex + 1;
        if (nextIndex < fragments.length) {
            Log.d(TAG, ">>> Swipe Left detected, switching to: " + nextIndex);
            switchFragment(nextIndex);
            bottomNavigation.getMenu().getItem(nextIndex).setChecked(true);
        }
    }

    private void onSwipeRight() {
        // Swipe right - go to previous tab
        int prevIndex = currentFragmentIndex - 1;
        if (prevIndex >= 0) {
            Log.d(TAG, ">>> Swipe Right detected, switching to: " + prevIndex);
            switchFragment(prevIndex);
            bottomNavigation.getMenu().getItem(prevIndex).setChecked(true);
        }
    }

    private void switchFragment(int position) {
        if (position < 0 || position >= fragments.length || position == currentFragmentIndex) {
            Log.d(TAG, "Ignoring fragment switch (already on " + position + ")");
            return;
        }
        
        // Determine animation based on direction
        boolean isForward = position > currentFragmentIndex;
        int enterAnim = isForward ? R.anim.slide_right_in : R.anim.slide_left_in;
        int exitAnim = isForward ? R.anim.slide_left_out : R.anim.slide_right_out;
        
        Log.d(TAG, "Switching from fragment " + currentFragmentIndex + " to: " + position + 
              " (Direction: " + (isForward ? "Forward →" : "← Backward") + ")");
        
        Fragment fragment = fragments[position];
        
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim)
            .replace(R.id.fragmentContainer, fragment)
            .commit();
        
        currentFragment = fragment;
        currentFragmentIndex = position;
        
        Log.d(TAG, "Fragment switch complete. Current index: " + currentFragmentIndex);
    }

    private void setupClickListeners() {
        if (buttonMenu != null) {
            buttonMenu.setOnClickListener(v -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                int position = -1;
                
                if (itemId == R.id.nav_dashboard) {
                    position = 0;
                } else if (itemId == R.id.nav_members) {
                    position = 2;
                } else if (itemId == R.id.nav_notifications) {
                    position = 3;
                } else if (itemId == R.id.nav_profile) {
                    position = 4;
                }
                
                if (position >= 0) {
                    switchFragment(position);
                    bottomNavigation.getMenu().getItem(position).setChecked(true);
                }
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
