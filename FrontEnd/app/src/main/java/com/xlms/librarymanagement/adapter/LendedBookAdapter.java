package com.xlms.librarymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.LendedBook;

import java.util.ArrayList;
import java.util.List;

public class LendedBookAdapter extends RecyclerView.Adapter<LendedBookAdapter.LendedBookViewHolder> {

    private List<LendedBook> displayList;
    private OnLendedBookClickListener listener;

    public interface OnLendedBookClickListener {
        void onBookClick(LendedBook book);
    }

    public LendedBookAdapter(OnLendedBookClickListener listener) {
        this.listener = listener;
        this.displayList = new ArrayList<>();
    }

    public void submitList(List<LendedBook> newList) {
        this.displayList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LendedBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lended_book, parent, false);
        return new LendedBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LendedBookViewHolder holder, int position) {
        LendedBook book = displayList.get(position);

        holder.textViewBookId.setText("B_ID: " + book.getBorrowerId());
        holder.textViewUserId.setText(book.getUserId());
        holder.textViewUserName.setText(book.getUserName());
        holder.textViewUserAvatar.setText(book.getUserInitial());
        holder.textViewBookTitle.setText(book.getBookTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewCategory.setText(book.getCategory());
        holder.textViewCopies.setText(String.valueOf(book.getCopies()));
        holder.textViewIssuedDate.setText(book.getIssuedDate());
        holder.textViewDueDate.setText(book.getDueDate());
        holder.textViewStatus.setText(book.getStatus());

        // Update status badge appearance
        updateStatusBadge(holder.layoutStatus, holder.textViewStatus, book.getStatus());

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
    }

    private void updateStatusBadge(LinearLayout layoutStatus, TextView textViewStatus, String status) {
        int bgColor, textColor;

        if ("Returned".equals(status)) {
            bgColor = R.color.status_returned_bg;
            textColor = R.color.status_returned_text;
        } else {
            bgColor = R.color.status_not_returned_bg;
            textColor = R.color.status_not_returned_text;
        }

        layoutStatus.setBackgroundTintList(ContextCompat.getColorStateList(layoutStatus.getContext(), bgColor));
        textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), textColor));
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class LendedBookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookId, textViewUserId, textViewUserName, textViewUserAvatar;
        TextView textViewBookTitle, textViewAuthor, textViewCategory;
        TextView textViewCopies, textViewIssuedDate, textViewDueDate, textViewStatus;
        LinearLayout layoutStatus;

        LendedBookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookId = itemView.findViewById(R.id.textViewBookId);
            textViewUserId = itemView.findViewById(R.id.textViewUserId);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewUserAvatar = itemView.findViewById(R.id.textViewUserAvatar);
            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewCopies = itemView.findViewById(R.id.textViewCopies);
            textViewIssuedDate = itemView.findViewById(R.id.textViewIssuedDate);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            layoutStatus = itemView.findViewById(R.id.layoutStatus);
        }
    }
}
