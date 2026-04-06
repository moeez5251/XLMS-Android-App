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
import com.xlms.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> displayList;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
        void onBookLongClick(Book book);
    }

    public BookAdapter(OnBookClickListener listener) {
        this.listener = listener;
        this.displayList = new ArrayList<>();
    }

    /**
     * Updates the list of books to display and refreshes the view.
     */
    public void submitList(List<Book> newList) {
        this.displayList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = displayList.get(position);

        holder.textViewBookId.setText(book.getBookId());
        holder.textViewBookTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewCategory.setText(book.getCategory());
        holder.textViewLanguage.setText(book.getLanguage());
        holder.textViewPrice.setText("$" + String.format("%.2f", book.getPrice()));
        holder.textViewTotal.setText(String.valueOf(book.getTotal()));
        holder.textViewAvailable.setText(String.valueOf(book.getAvailable()));
        holder.textViewStatus.setText(book.getStatus());

        updateStatusBadge(holder.layoutStatus, holder.textViewStatus, book.getStatus());

        // Click listeners
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onBookLongClick(book);
            return true;
        });
    }

    private void updateStatusBadge(LinearLayout layoutStatus, TextView textViewStatus, String status) {
        int bgColor, textColor;

        switch (status) {
            case "Available":
                bgColor = R.color.status_available_bg;
                textColor = R.color.available_text;
                break;
            case "Limited":
                bgColor = R.color.status_limited_bg;
                textColor = R.color.limited_text;
                break;
            case "Out of Stock":
                bgColor = R.color.status_out_of_stock_bg;
                textColor = R.color.out_of_stock_text;
                break;
            default:
                bgColor = R.color.surface_container_high;
                textColor = R.color.on_surface_variant;
                break;
        }

        layoutStatus.setBackgroundTintList(ContextCompat.getColorStateList(layoutStatus.getContext(), bgColor));
        textViewStatus.setTextColor(ContextCompat.getColor(textViewStatus.getContext(), textColor));
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookId, textViewBookTitle, textViewAuthor, textViewCategory;
        TextView textViewLanguage, textViewPrice, textViewTotal, textViewAvailable, textViewStatus;
        LinearLayout layoutStatus;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookId = itemView.findViewById(R.id.textViewBookId);
            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewLanguage = itemView.findViewById(R.id.textViewLanguage);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewTotal = itemView.findViewById(R.id.textViewTotal);
            textViewAvailable = itemView.findViewById(R.id.textViewAvailable);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            layoutStatus = itemView.findViewById(R.id.layoutStatus);
        }
    }
}