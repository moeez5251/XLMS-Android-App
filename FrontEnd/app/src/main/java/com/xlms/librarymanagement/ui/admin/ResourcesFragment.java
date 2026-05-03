package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xlms.librarymanagement.R;

public class ResourcesFragment extends Fragment implements AdminDashboardActivity.Refreshable {

    private Button buttonAddResource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resources, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonAddResource = view.findViewById(R.id.buttonAddResource);

        buttonAddResource.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Add Resource coming soon...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void refreshData() {
        // Refresh resources if applicable
    }
}
