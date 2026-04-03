package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;

/**
 * Placeholder fragment for sidebar navigation screens
 */
public class PlaceholderFragment extends Fragment {

    private static final String TAG = "PlaceholderFragment";
    private static final String ARG_TITLE = "title";
    private String title;

    public static PlaceholderFragment newInstance(String title) {
        Log.d(TAG, "newInstance called for: " + title);
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate for: " + (getArguments() != null ? getArguments().getString(ARG_TITLE) : "null"));
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "onCreateView for: " + title);
            
            FrameLayout root = new FrameLayout(requireContext());
            FrameLayout.LayoutParams rootParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
            root.setLayoutParams(rootParams);
            
            // Different background color for each tab to verify page switching
            int bgColor;
            switch (title) {
                case "Books":
                    bgColor = 0xFFE3F2FD; // Light blue
                    break;
                case "Members":
                    bgColor = 0xFFF3E5F5; // Light purple
                    break;
                case "Alerts":
                    bgColor = 0xFFFFF3E0; // Light orange
                    break;
                case "Profile":
                    bgColor = 0xFFE8F5E9; // Light green
                    break;
                default:
                    bgColor = ContextCompat.getColor(requireContext(), R.color.surface);
                    break;
            }
            root.setBackgroundColor(bgColor);
            Log.d(TAG, "Set background color for: " + title + " (color: " + Integer.toHexString(bgColor) + ")");

            TextView textView = new TextView(requireContext());
            textView.setText(title);
            textView.setTextSize(24);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            textView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
            
            root.addView(textView);
            return root;
        } catch (Exception e) {
            Log.e(TAG, "Error creating view: " + e.getMessage(), e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return new TextView(requireContext());
        }
    }
}
