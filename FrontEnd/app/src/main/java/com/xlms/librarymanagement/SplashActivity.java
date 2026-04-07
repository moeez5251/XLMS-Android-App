package com.xlms.librarymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.xlms.librarymanagement.ui.admin.AdminDashboardActivity;
import com.xlms.librarymanagement.ui.client.ClientDashboardActivity;
import com.xlms.librarymanagement.ui.login.LoginActivity;
import com.xlms.librarymanagement.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500;
    private View brandingContent, progressBar;
    private Animation progressAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        startAnimations();
        navigateBasedOnSession();
    }

    private void initViews() {
        brandingContent = findViewById(R.id.brandingContent);
        progressBar = findViewById(R.id.progressBar);
    }

    private void startAnimations() {
        try {
            progressAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_progress_sweep);
            if (progressBar != null && progressAnimation != null) {
                progressBar.startAnimation(progressAnimation);
            }

            if (brandingContent != null) {
                brandingContent.setAlpha(0f);
                brandingContent.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(200)
                    .setListener(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateBasedOnSession() {
        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                String role = sessionManager.getUserRole();
                if ("ADMIN".equals(role)) {
                    intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, ClientDashboardActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressAnimation != null) {
            progressAnimation.cancel();
            progressAnimation = null;
        }
        if (brandingContent != null) {
            brandingContent.animate().cancel();
        }
    }
}
