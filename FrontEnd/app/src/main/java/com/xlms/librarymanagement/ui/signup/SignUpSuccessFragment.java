package com.xlms.librarymanagement.ui.signup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;

/**
 * Sign Up Success Fragment - Step 3 of 3 in the registration flow
 * Displays success confirmation and navigates to dashboard
 */
public class SignUpSuccessFragment extends Fragment {

    // UI Components
    private Button buttonContinueToDashboard;

    // Listener
    private OnSuccessCompleteListener mListener;

    public interface OnSuccessCompleteListener {
        void onContinueToDashboard();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSuccessCompleteListener) {
            mListener = (OnSuccessCompleteListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
    }

    private void initViews(View view) {
        buttonContinueToDashboard = view.findViewById(R.id.buttonContinueToDashboard);
    }

    private void setupClickListeners() {
        // Continue to Dashboard button
        buttonContinueToDashboard.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onContinueToDashboard();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
