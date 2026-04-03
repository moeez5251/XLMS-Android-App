package com.xlms.librarymanagement;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xlms.librarymanagement.ui.login.LoginActivity;

/**
 * Splash Activity - Entry point for XLMS Library Management System
 * Displays branding with smooth animations and navigates to Login screen
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private static final int LOGO_ANIM_DELAY = 0;
    private static final int TITLE_ANIM_DELAY = 300;
    private static final int SUBTITLE_ANIM_DELAY = 500;

    // UI Components
    private View logoContainer;
    private TextView appTitle;
    private TextView appSubtitle;
    private View progressBar;
    private TextView versionText;

    // Animations
    private Animation progressAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        initViews();

        // Set app version from manifest
        setAppVersion();

        // Start animations
        startAnimations();

        // Navigate to Login after delay
        navigateToLogin();
    }

    private void initViews() {
        logoContainer = findViewById(R.id.brandingContent);
        appTitle = findViewById(R.id.appTitle);
        appSubtitle = findViewById(R.id.appSubtitle);
        progressBar = findViewById(R.id.progressBar);
        versionText = findViewById(R.id.versionText);
    }

    private void setAppVersion() {
        try {
            String versionName = getPackageManager()
                .getPackageInfo(getPackageName(), 0)
                .versionName;
            if (versionText != null) {
                versionText.setText("Version" + versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startAnimations() {
        try {
            // 1. Progress bar sweep animation (continuous)
            progressAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_progress_sweep);
            if (progressBar != null && progressAnimation != null) {
                progressBar.startAnimation(progressAnimation);
            }

            // 2. Logo animation - Scale + Fade In
            if (logoContainer != null) {
                logoContainer.setScaleX(0.5f);
                logoContainer.setScaleY(0.5f);
                logoContainer.setAlpha(0f);
                logoContainer.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(LOGO_ANIM_DELAY)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(null);
            }

            // 3. Title animation - Fade In + Slide Up
            if (appTitle != null) {
                appTitle.setAlpha(0f);
                appTitle.setTranslationY(20f);
                appTitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(700)
                    .setStartDelay(TITLE_ANIM_DELAY)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(null);
            }

            // 4. Subtitle animation - Fade In
            if (appSubtitle != null) {
                appSubtitle.setAlpha(0f);
                appSubtitle.animate()
                    .alpha(1f)
                    .setDuration(700)
                    .setStartDelay(SUBTITLE_ANIM_DELAY)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(null);
            }

        } catch (Exception e) {
            // If animation fails, continue without it
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, SPLASH_DURATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up animations
        if (progressAnimation != null) {
            progressAnimation.cancel();
            progressAnimation = null;
        }
        // Cancel all animations
        if (logoContainer != null) {
            logoContainer.animate().cancel();
        }
        if (appTitle != null) {
            appTitle.animate().cancel();
        }
        if (appSubtitle != null) {
            appSubtitle.animate().cancel();
        }
    }
}
