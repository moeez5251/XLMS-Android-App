package com.xlms.librarymanagement.ui.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.LendedBook;
import java.util.List;

import android.widget.Button;

public class LendingHistoryAdapter extends RecyclerView.Adapter<LendingHistoryAdapter.ViewHolder> {

    private List<LendedBook> lendingList;
    private OnReturnClickListener returnListener;

    public interface OnReturnClickListener {
        void onReturnClick(LendedBook lending);
    }

    public LendingHistoryAdapter(List<LendedBook> lendingList, OnReturnClickListener listener) {
        this.lendingList = lendingList;
        this.returnListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lending_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LendedBook lending = lendingList.get(position);
        holder.textViewBookTitle.setText(lending.getBookTitle() + " (" + lending.getCopies() + ")");
        holder.textViewIssuedDate.setText("Issued: " + lending.getIssuedDate());
        holder.textViewDueDate.setText("Due: " + lending.getDueDate());
        holder.textViewStatus.setText(lending.getStatus());

        if ("Returned".equalsIgnoreCase(lending.getStatus())) {
            holder.statusIndicator.setBackgroundResource(R.drawable.bg_status_returned);
            holder.textViewStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.status_returned_text));
            holder.buttonReturn.setVisibility(View.GONE);
        } else {
            holder.statusIndicator.setBackgroundResource(R.drawable.bg_status_not_returned);
            holder.textViewStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.status_not_returned_text));
            holder.buttonReturn.setVisibility(View.VISIBLE);
            holder.buttonReturn.setOnClickListener(v -> {
                if (returnListener != null) returnListener.onReturnClick(lending);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            boolean expanded = holder.detailsLayout.getVisibility() == View.VISIBLE;
            holder.detailsLayout.setVisibility(expanded ? View.GONE : View.VISIBLE);
            holder.imageViewExpand.setRotation(expanded ? -90 : 0);
        });
    }

    @Override
    public int getItemCount() {
        return lendingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookTitle, textViewIssuedDate, textViewDueDate, textViewStatus;
        LinearLayout detailsLayout;
        ImageView imageViewExpand;
        View statusIndicator;
        Button buttonReturn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewIssuedDate = itemView.findViewById(R.id.textViewIssuedDate);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            imageViewExpand = itemView.findViewById(R.id.imageViewExpand);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            buttonReturn = itemView.findViewById(R.id.buttonReturn);
        }
    }
}
