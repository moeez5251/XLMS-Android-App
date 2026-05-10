package com.xlms.librarymanagement.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.JsonObject;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.adapter.NotificationAdapter;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.model.Notification;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.ui.login.LoginActivity;
import com.xlms.librarymanagement.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Client Dashboard - Main screen for library users/clients
 * Features: Dashboard, Book Catalog, Account, Search, Help, Notifications
 */
public class ClientDashboardActivity extends AppCompatActivity {

    private TextView textViewToolbarTitle;
    private View backdropOverlay;
    private View bottomSheetContent;
    private View buttonOpenDrawer;
    private TextView textViewNavbarInitial;
    private TextView textViewUserInitial;
    private BottomNavigationView bottomNavigation;
    private ImageButton buttonNotifications;
    private View notificationDot;
    private List<Notification> notificationList = new ArrayList<>();

    private String userName = "User";
    private String userEmail = "";
    private boolean isBottomSheetOpen = false;

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
        setupBottomSheet();
        loadMainFragment();
        setupClickListeners();
        fetchNotifications();
    }

    private void initViews() {
        textViewToolbarTitle = findViewById(R.id.textViewToolbarTitle);
        backdropOverlay = findViewById(R.id.backdropOverlay);
        bottomSheetContent = findViewById(R.id.bottomSheetContent);
        buttonOpenDrawer = findViewById(R.id.buttonOpenDrawer);
        textViewNavbarInitial = findViewById(R.id.textViewNavbarInitial);
        textViewUserInitial = findViewById(R.id.textViewUserInitial);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        buttonNotifications = findViewById(R.id.buttonNotifications);
        notificationDot = findViewById(R.id.notificationDot);

        // Set user info in sheet
        android.widget.TextView textUserName = findViewById(R.id.textViewUserName);
        android.widget.TextView textUserEmail = findViewById(R.id.textViewUserEmail);
        android.widget.TextView textUserRole = findViewById(R.id.textViewUserRole);

        if (textUserName != null) {
            textUserName.setText(userName);
        }
        if (textUserEmail != null) {
            textUserEmail.setText(userEmail);
        }

        SessionManager session = new SessionManager(this);
        if (textUserRole != null) {
            textUserRole.setText(session.getUserRole());
        }

        // Set initials
        if (userName != null && !userName.isEmpty()) {
            String initial = userName.substring(0, 1).toUpperCase();
            if (textViewNavbarInitial != null) textViewNavbarInitial.setText(initial);
            if (textViewUserInitial != null) textViewUserInitial.setText(initial);
        }
    }

    private void fetchNotifications() {
        ApiClient.getApiService(this).getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    if (notificationDot != null) {
                        notificationDot.setVisibility(notificationList.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {}
        });
    }

    private void setupBottomSheet() {
        // Backdrop click to close
        backdropOverlay.setOnClickListener(v -> closeBottomSheet());

        // Nav Item Clicks
        findViewById(R.id.navClientDashboard).setOnClickListener(v -> {
            navigateToTab(0);
            updateBottomSheetSelection(0);
            closeBottomSheet();
        });
        findViewById(R.id.navClientCatalog).setOnClickListener(v -> {
            navigateToTab(1);
            updateBottomSheetSelection(1);
            closeBottomSheet();
        });
        findViewById(R.id.navClientAccount).setOnClickListener(v -> {
            navigateToTab(2);
            updateBottomSheetSelection(2);
            closeBottomSheet();
        });
        findViewById(R.id.navClientHelp).setOnClickListener(v -> {
            navigateToTab(3);
            updateBottomSheetSelection(3);
            closeBottomSheet();
        });
        findViewById(R.id.navClientNotifications).setOnClickListener(v -> {
            navigateToTab(4);
            updateBottomSheetSelection(4);
            closeBottomSheet();
        });

        // Logout
        findViewById(R.id.buttonLogout).setOnClickListener(v -> handleLogout());
    }

    private void updateBottomSheetSelection(int index) {
        int[] ids = {R.id.navClientDashboard, R.id.navClientCatalog, R.id.navClientAccount, R.id.navClientHelp, R.id.navClientNotifications};
        int primaryColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary);
        int variantColor = androidx.core.content.ContextCompat.getColor(this, R.color.on_surface_variant);

        for (int i = 0; i < ids.length; i++) {
            android.widget.LinearLayout item = findViewById(ids[i]);
            if (item == null) continue;

            ImageView icon = (ImageView) item.getChildAt(0);
            android.widget.TextView text = (android.widget.TextView) item.getChildAt(1);

            if (i == index) {
                item.setBackgroundResource(R.drawable.nav_item_selected_background);
                icon.setImageTintList(android.content.res.ColorStateList.valueOf(primaryColor));
                text.setTextColor(primaryColor);
                text.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                item.setBackground(null);
                icon.setImageTintList(android.content.res.ColorStateList.valueOf(variantColor));
                text.setTextColor(variantColor);
                text.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    public void updateToolbarTitle(int index) {
        if (textViewToolbarTitle == null) return;
        switch (index) {
            case 0: textViewToolbarTitle.setText("Dashboard"); break;
            case 1: textViewToolbarTitle.setText("Book Catalog"); break;
            case 2: textViewToolbarTitle.setText("My Account"); break;
            case 3: textViewToolbarTitle.setText("Help & Support"); break;
            case 4: textViewToolbarTitle.setText("Notifications"); break;
        }
    }

    private void navigateToTab(int index) {
        Fragment mainFrag = getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
        if (mainFrag instanceof ClientDashboardMainFragment) {
            ((ClientDashboardMainFragment) mainFrag).setCurrentItem(index);
        }
        
        updateToolbarTitle(index);
        
        // Sync BottomNavigation
        if (index == 0) bottomNavigation.setSelectedItemId(R.id.bottom_client_dashboard);
        else if (index == 1) bottomNavigation.setSelectedItemId(R.id.bottom_client_catalog);
        else if (index == 2) bottomNavigation.setSelectedItemId(R.id.bottom_client_account);
        else if (index == 3) bottomNavigation.setSelectedItemId(R.id.bottom_client_help);
        else if (index == 4) bottomNavigation.setSelectedItemId(R.id.bottom_client_notifications);
    }

    private void openBottomSheet() {
        // Sync selection before showing
        Fragment mainFrag = getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
        if (mainFrag instanceof ClientDashboardMainFragment) {
            updateBottomSheetSelection(((ClientDashboardMainFragment) mainFrag).getCurrentItem());
        }

        isBottomSheetOpen = true;
        backdropOverlay.setVisibility(View.VISIBLE);
        backdropOverlay.setAlpha(0f);
        backdropOverlay.animate().alpha(0.5f).setDuration(300).start();

        bottomSheetContent.setVisibility(View.VISIBLE);
        
        // Force height to ~75% of screen
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int targetHeight = (int) (screenHeight * 0.75);
        android.view.ViewGroup.LayoutParams params = bottomSheetContent.getLayoutParams();
        params.height = targetHeight;
        bottomSheetContent.setLayoutParams(params);

        bottomSheetContent.post(() -> {
            bottomSheetContent.setTranslationY(targetHeight);
            bottomSheetContent.animate()
                    .translationY(0)
                    .setDuration(350)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        });
    }

    private void closeBottomSheet() {
        isBottomSheetOpen = false;
        backdropOverlay.animate().alpha(0f).setDuration(300).withEndAction(() -> 
                backdropOverlay.setVisibility(View.GONE)).start();

        bottomSheetContent.animate()
                .translationY(bottomSheetContent.getHeight() + 100)
                .setDuration(300)
                .setInterpolator(new android.view.animation.AccelerateInterpolator())
                .withEndAction(() -> bottomSheetContent.setVisibility(View.GONE))
                .start();
    }

    private void loadMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContentFrame, new ClientDashboardMainFragment(), "MAIN_FRAGMENT")
                .commit();
    }

    private void setupClickListeners() {
        if (buttonOpenDrawer != null) {
            buttonOpenDrawer.setOnClickListener(v -> openBottomSheet());
        }
        buttonNotifications.setOnClickListener(v -> {
            fetchNotifications();
            showNotificationsPopup(v);
        });
    }

    private void showNotificationsPopup(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_notifications, null);
        
        RecyclerView recyclerView = popupView.findViewById(R.id.recyclerViewPopupNotifications);
        android.widget.LinearLayout layoutEmpty = popupView.findViewById(R.id.layoutPopupEmpty);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NotificationAdapter adapter = new NotificationAdapter();
        adapter.submitList(notificationList);
        adapter.setOnNotificationClickListener(notification -> {
            markAsRead(notification);
        });
        recyclerView.setAdapter(adapter);

        if (notificationList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }

        android.widget.PopupWindow popupWindow = new android.widget.PopupWindow(
                popupView,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(20);
        popupWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        
        popupView.findViewById(R.id.buttonViewAll).setOnClickListener(v -> {
            navigateToTab(4);
            popupWindow.dismiss();
        });

        popupWindow.showAsDropDown(anchorView, -250, 10);
    }

    private void markAsRead(Notification notification) {
        JsonObject body = new JsonObject();
        body.addProperty("NotificationId", notification.getId());
        
        ApiClient.getApiService(this).markAsReadAll(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    fetchNotifications(); // Refresh both badge and popup data
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {}
        });
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
        if (isBottomSheetOpen) {
            closeBottomSheet();
            return;
        }

        // Check if the main fragment has any back stack entries (e.g., BookInfo -> Catalog)
        Fragment mainFrag = getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
        if (mainFrag instanceof ClientDashboardMainFragment) {
            ClientDashboardMainFragment cdmf = (ClientDashboardMainFragment) mainFrag;
            if (cdmf.getChildFragmentManager().getBackStackEntryCount() > 0) {
                cdmf.getChildFragmentManager().popBackStack();
                return;
            }
            if (cdmf.getCurrentItem() != 0) {
                cdmf.setCurrentItem(0);
                return;
            }
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
