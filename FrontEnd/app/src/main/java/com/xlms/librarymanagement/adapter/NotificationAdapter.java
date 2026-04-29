package com.xlms.librarymanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter() {
        this.notificationList = new ArrayList<>();
    }

    public void submitList(List<Notification> newList) {
        this.notificationList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        String message = notification.getDescription() != null ? notification.getDescription().toLowerCase() : "";

        // Default values for "Information" type
        String displayTitle = "Information";
        int iconRes = R.drawable.ic_notifications;
        int bgColorRes = R.drawable.icon_background_secondary;
        int iconColor = R.color.primary;

        // Keyword-based inference to provide better titles and icons
        if (message.contains("overdue") || message.contains("late") || message.contains("warning") || 
            message.contains("expired") || message.contains("fine") || message.contains("alert")) {
            displayTitle = "Action Required";
            iconRes = R.drawable.ic_alert_circle;
            bgColorRes = R.drawable.icon_background_error;
            iconColor = R.color.error;
        } else if (message.contains("success") || message.contains("returned") || message.contains("added") || 
                   message.contains("confirmed") || message.contains("approved") || message.contains("received") || 
                   message.contains("completed")) {
            displayTitle = "Task Completed";
            iconRes = R.drawable.ic_check_circle;
            bgColorRes = R.drawable.icon_background_tertiary;
            iconColor = R.color.primary;
        } else if (message.contains("system") || message.contains("update") || message.contains("maintenance") || 
                   message.contains("security") || message.contains("login") || message.contains("password")) {
            displayTitle = "System Update";
            iconRes = R.drawable.ic_settings;
            bgColorRes = R.drawable.icon_background_secondary;
            iconColor = R.color.primary;
        } else if (message.contains("borrowed") || message.contains("issued") || message.contains("checkout") || 
                   message.contains("book")) {
            displayTitle = "Book Activity";
            iconRes = R.drawable.ic_menu_book;
            bgColorRes = R.drawable.icon_background_primary;
            iconColor = R.color.white;
        }

        holder.textViewTitle.setText(displayTitle);
        holder.textViewDescription.setText(notification.getDescription());
        holder.textViewTime.setText(getRelativeTime(notification.getTime()));

        holder.imageViewIcon.setImageResource(iconRes);
        holder.imageViewIcon.setColorFilter(ContextCompat.getColor(holder.imageViewIcon.getContext(), iconColor));
        holder.layoutIcon.setBackgroundResource(bgColorRes);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void clear() {
        notificationList.clear();
        notifyDataSetChanged();
    }

    public static String getRelativeTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) return "Just now";
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(timestamp);
            if (date == null) return timestamp;

            long time = date.getTime();
            long now = System.currentTimeMillis();
            long diff = now - time;

            if (diff < 60000) {
                return "Just now";
            } else if (diff < 3600000) {
                long mins = diff / 60000;
                return mins + (mins == 1 ? " min ago" : " mins ago");
            } else if (diff < 86400000) {
                long hours = diff / 3600000;
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else {
                long days = diff / 86400000;
                return days + (days == 1 ? " day ago" : " days ago");
            }
        } catch (Exception e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(timestamp);
                if (date != null) {
                    long diff = System.currentTimeMillis() - date.getTime();
                    if (diff < 60000) return "Just now";
                    if (diff < 3600000) return (diff/60000) + " mins ago";
                    if (diff < 86400000) return (diff/3600000) + " hours ago";
                    return (diff/86400000) + " days ago";
                }
            } catch (Exception e2) {}
            return timestamp;
        }
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewTime;
        ImageView imageViewIcon;
        LinearLayout layoutIcon;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            layoutIcon = itemView.findViewById(R.id.layoutIcon);
        }
    }
}
