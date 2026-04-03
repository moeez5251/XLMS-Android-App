package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;

public class PlaceholderFragment extends Fragment {

    private static final String TAG = "PlaceholderFragment";
    private static final String ARG_TITLE = "title";
    private String title;

    public static PlaceholderFragment newInstance(String title) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        try {
            FrameLayout root = new FrameLayout(requireContext());
            root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
            
            // CRITICAL FIX: Set solid background color to prevent overlap issues
            root.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface));

            TextView textView = new TextView(requireContext());
            textView.setText(title != null ? title : "Placeholder");
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
            // Fallback
            TextView errorText = new TextView(requireContext());
            errorText.setText("Error loading tab");
            errorText.setGravity(Gravity.CENTER);
            return errorText;
        }
    }
}
