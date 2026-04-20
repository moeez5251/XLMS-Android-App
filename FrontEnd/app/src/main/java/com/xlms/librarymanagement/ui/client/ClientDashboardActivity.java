package com.xlms.librarymanagement.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

/**
 * Client Dashboard - Main screen for library users/clients
 * Features: Dashboard, Book Catalog, Account, Search, Help, Notifications
 */
public class ClientDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;
    private ImageButton buttonNotifications;
    private ImageView buttonOpenDrawer;

    private String userName = "User";
    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);

        // Get user info from session
        SessionManager sessionManager = new SessionManager(this);
        userName = sessionManager.getUserName();
        userEmail = sessionManager.getUserEmail();

        if (userName == null || userName.isEmpty()) {
            userName = "User";
        }

        initViews();
        setupDrawer();
        loadMainFragment();
        setupClickListeners();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        buttonNotifications = findViewById(R.id.buttonNotifications);
        buttonOpenDrawer = findViewById(R.id.buttonOpenClientDrawer);

        // Set user info in nav header
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            android.widget.TextView textUserName = headerView.findViewById(R.id.textViewNavUserName);
            android.widget.TextView textUserRole = headerView.findViewById(R.id.textViewNavRole);
            if (textUserName != null) {
                textUserName.setText(userName);
            }
            if (textUserRole != null) {
                textUserRole.setText("Lead Researcher");
            }
        }
    }

    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment mainFrag = getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
            if (id == R.id.navClientDashboard) {
                if (mainFrag instanceof ClientDashboardMainFragment) {
                    ((ClientDashboardMainFragment) mainFrag).setCurrentItem(0);
                }
            } else if (id == R.id.navClientCatalog) {
                if (mainFrag instanceof ClientDashboardMainFragment) {
                    ((ClientDashboardMainFragment) mainFrag).setCurrentItem(1);
                }
            } else if (id == R.id.navClientAccount) {
                if (mainFrag instanceof ClientDashboardMainFragment) {
                    ((ClientDashboardMainFragment) mainFrag).setCurrentItem(2);
                }
            } else if (id == R.id.navClientHelp) {
                if (mainFrag instanceof ClientDashboardMainFragment) {
                    ((ClientDashboardMainFragment) mainFrag).setCurrentItem(3);
                }
            } else if (id == R.id.navClientNotifications) {
                Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navClientLogout) {
                handleLogout();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContentFrame, new ClientDashboardMainFragment(), "MAIN_FRAGMENT")
                .commit();
    }

    private void setupClickListeners() {
        buttonOpenDrawer.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        buttonNotifications.setOnClickListener(v ->
            Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadDefaultFragment() {
        Bundle args = new Bundle();
        args.putString("USER_NAME", userName);

        ClientDashboardContentFragment fragment = new ClientDashboardContentFragment();
        fragment.setArguments(args);

        loadFragment(fragment, false);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContentFrame, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void handleLogout() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.clearSession();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
