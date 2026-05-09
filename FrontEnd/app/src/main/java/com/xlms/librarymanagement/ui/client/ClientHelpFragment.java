package com.xlms.librarymanagement.ui.client;

import android.app.AlertDialog;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.JsonObject;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.api.ApiClient;
import com.xlms.librarymanagement.api.MessageResponse;
import com.xlms.librarymanagement.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientHelpFragment extends Fragment {

    private List<AccordionItem> accordionItems = new ArrayList<>();
    private ViewGroup accordionContainer;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_help, container, false);

        sessionManager = new SessionManager(requireContext());
        accordionContainer = view.findViewById(R.id.accordionContainer);
        view.findViewById(R.id.buttonSubmitTicket).setOnClickListener(v -> showSubmitTicketDialog());

        setupAccordion(view, R.id.layoutQuestion1, R.id.textAnswer1, R.id.imageChevron1);
        setupAccordion(view, R.id.layoutQuestion2, R.id.textAnswer2, R.id.imageChevron2);
        setupAccordion(view, R.id.layoutQuestion3, R.id.textAnswer3, R.id.imageChevron3);
        setupAccordion(view, R.id.layoutQuestion4, R.id.textAnswer4, R.id.imageChevron4);
        setupAccordion(view, R.id.layoutQuestion5, R.id.textAnswer5, R.id.imageChevron5);
        setupAccordion(view, R.id.layoutQuestion6, R.id.textAnswer6, R.id.imageChevron6);

        return view;
    }

    private void setupAccordion(View root, int layoutId, int answerId, int chevronId) {
        View layout = root.findViewById(layoutId);
        View answer = root.findViewById(answerId);
        View chevron = root.findViewById(chevronId);

        AccordionItem item = new AccordionItem(layout, answer, chevron);
        accordionItems.add(item);

        layout.setOnClickListener(v -> {
            boolean isExpanding = answer.getVisibility() == View.GONE;
            
            // Use TransitionManager for smooth animation on the container
            TransitionManager.beginDelayedTransition(accordionContainer);

            if (isExpanding) {
                // Collapse all other items first
                for (AccordionItem otherItem : accordionItems) {
                    if (otherItem != item && otherItem.answer.getVisibility() == View.VISIBLE) {
                        otherItem.answer.setVisibility(View.GONE);
                        otherItem.chevron.animate().rotation(0).setDuration(200).start();
                    }
                }
                // Expand current item
                answer.setVisibility(View.VISIBLE);
                chevron.animate().rotation(180).setDuration(200).start();
            } else {
                // Collapse current item
                answer.setVisibility(View.GONE);
                chevron.animate().rotation(0).setDuration(200).start();
            }
        });
    }

    private static class AccordionItem {
        View layout;
        View answer;
        View chevron;

        AccordionItem(View layout, View answer, View chevron) {
            this.layout = layout;
            this.answer = answer;
            this.chevron = chevron;
        }
    }

    private void showSubmitTicketDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_submit_ticket, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextSubject = dialogView.findViewById(R.id.editTextSubject);
        EditText editTextIssue = dialogView.findViewById(R.id.editTextIssue);
        View buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);

        // Autofill with session data
        if (sessionManager != null) {
            editTextName.setText(sessionManager.getUserName());
            editTextEmail.setText(sessionManager.getUserEmail());
        }

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(v -> dialog.dismiss());
        buttonSubmit.setOnClickListener(v -> {
            String subject = editTextSubject.getText().toString().trim();
            String issue = editTextIssue.getText().toString().trim();

            if (subject.isEmpty() || issue.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loader
            progressBar.setVisibility(View.VISIBLE);
            buttonSubmit.setEnabled(false);

            JsonObject body = new JsonObject();
            body.addProperty("name", editTextName.getText().toString());
            body.addProperty("sender", editTextEmail.getText().toString());
            body.addProperty("subject", subject);
            body.addProperty("issue", issue);

            ApiClient.getApiService(requireContext()).submitTicket(body).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (buttonSubmit != null) buttonSubmit.setEnabled(true);
                    
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Ticket submitted successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed to submit ticket", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (buttonSubmit != null) buttonSubmit.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
