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

        // Get user info from intent
        userEmail = getIntent().getStringExtra("USER_EMAIL");
        userName = getIntent().getStringExtra("USER_NAME");
        if (userName == null || userName.isEmpty()) {
            userName = "User";
        }

        initViews();
        setupDrawer();
        setupBottomNavigation();
        setupClickListeners();
        loadDefaultFragment();
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
            if (id == R.id.navClientDashboard) {
                loadFragment(new ClientDashboardContentFragment(), false);
            } else if (id == R.id.navClientCatalog) {
                Toast.makeText(this, "Catalog coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navClientAccount) {
                Toast.makeText(this, "Account coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navClientHelp) {
                Toast.makeText(this, "Help coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navClientNotifications) {
                Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navClientLogout) {
                handleLogout();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_client_dashboard) {
                loadFragment(new ClientDashboardContentFragment(), false);
                return true;
            } else if (id == R.id.bottom_client_catalog) {
                Toast.makeText(this, "Catalog coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.bottom_client_account) {
                Toast.makeText(this, "Account coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.bottom_client_help) {
                Toast.makeText(this, "Help coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.bottom_client_exit) {
                handleLogout();
                return true;
            }
            return false;
        });
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

        // Check if current fragment is dashboard
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainContentFrame);
        if (!(currentFragment instanceof ClientDashboardContentFragment)) {
            loadFragment(new ClientDashboardContentFragment(), false);
            bottomNavigation.getMenu().getItem(0).setChecked(true);
            return;
        }

        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
