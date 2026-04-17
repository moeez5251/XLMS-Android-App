package com.xlms.librarymanagement.ui.client;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookCatalogAdapter extends RecyclerView.Adapter<BookCatalogAdapter.BookViewHolder> {

    private List<Book> bookList;
    private Context context;

    public BookCatalogAdapter(Context context) {
        this.context = context;
        this.bookList = new ArrayList<>();
    }

    public void submitList(List<Book> newList) {
        this.bookList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_catalog, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewCategory.setText(book.getCategory().toUpperCase());
        holder.textViewPrice.setText(String.format("PKR %.0f", book.getPrice()));

        // Set availability text and background based on status
        String status = book.getStatus();
        holder.textViewStatus.setText(status);

        if ("Available".equalsIgnoreCase(status)) {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_badge_available);
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.available_text));
        } else {
            holder.textViewStatus.setBackgroundResource(R.drawable.bg_status_badge_reserved);
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.limited_text));
        }

        // Set a random-looking gradient or color based on category for the cover
        int[] gradients = {R.drawable.bg_book_cover_gradient}; // Could add more for variety
        holder.coverArea.setBackgroundResource(gradients[0]);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewAuthor, textViewPrice, textViewStatus, textViewCategory;
        View coverArea;
        ImageView imageViewBookIcon;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            coverArea = itemView.findViewById(R.id.imageViewBookIcon).getParent() instanceof View ? (View) itemView.findViewById(R.id.imageViewBookIcon).getParent() : null;
        }
    }
}