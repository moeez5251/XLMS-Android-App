package com.xlms.librarymanagement.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.MessageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourcesFragment extends Fragment implements AdminDashboardActivity.Refreshable {

    private EditText editName, editEmail, editWebsite;
    private ProgressBar progressBar;
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

        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editWebsite = view.findViewById(R.id.editWebsite);
        progressBar = view.findViewById(R.id.progressBar);
        buttonAddResource = view.findViewById(R.id.buttonAddResource);

        buttonAddResource.setOnClickListener(v -> addResource());
    }

    private void addResource() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String web = editWebsite.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(web)) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonAddResource.setEnabled(false);

        JsonObject body = new JsonObject();
        body.addProperty("Name", name);
        body.addProperty("Email", email);
        body.addProperty("Website", web);

        com.xlms.librarymanagement.api.ApiClient.getApiService(requireContext()).addResource(body).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                progressBar.setVisibility(View.GONE);
                buttonAddResource.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Resource added", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                buttonAddResource.setEnabled(true);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        editName.setText("");
        editEmail.setText("");
        editWebsite.setText("");
    }

    @Override
    public void refreshData() {
        // Refresh resources if applicable
    }

}
