package com.xlms.librarymanagement.ui.client;

import android.app.AlertDialog;
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

public class ClientHelpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client_help, container, false);

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

        layout.setOnClickListener(v -> {
            if (answer.getVisibility() == View.GONE) {
                answer.setVisibility(View.VISIBLE);
                chevron.animate().rotation(180).setDuration(200).start();
            } else {
                answer.setVisibility(View.GONE);
                chevron.animate().rotation(0).setDuration(200).start();
            }
        });
    }

    private void showSubmitTicketDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_submit_ticket, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.buttonSubmit).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ticket submitted successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}
