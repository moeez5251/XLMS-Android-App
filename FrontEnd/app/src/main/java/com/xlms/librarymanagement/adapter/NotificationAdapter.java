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

import java.util.ArrayList;
import java.util.List;

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

        holder.textViewTitle.setText(notification.getTitle());
        holder.textViewDescription.setText(notification.getDescription());
        holder.textViewTime.setText(notification.getTime());

        // Update icon and background based on type
        int iconRes, bgColor, iconColor;
        switch (notification.getType()) {
            case Notification.TYPE_WARNING:
                iconRes = R.drawable.ic_warning;
                bgColor = R.color.error_container;
                iconColor = R.color.on_error_container;
                break;
            case Notification.TYPE_INFO:
                iconRes = R.drawable.ic_menu_book;
                bgColor = R.color.secondary_container;
                iconColor = R.color.on_secondary_container;
                break;
            case Notification.TYPE_SUCCESS:
                iconRes = R.drawable.ic_check_circle;
                bgColor = R.color.surface_container_high;
                iconColor = R.color.primary;
                break;
            case Notification.TYPE_SYSTEM:
                iconRes = R.drawable.ic_notifications_active;
                bgColor = R.color.tertiary_fixed;
                iconColor = R.color.on_tertiary_fixed;
                break;
            default:
                iconRes = R.drawable.ic_notifications;
                bgColor = R.color.surface_container_high;
                iconColor = R.color.on_surface_variant;
                break;
        }

        holder.imageViewIcon.setImageResource(iconRes);
        holder.imageViewIcon.setColorFilter(ContextCompat.getColor(holder.imageViewIcon.getContext(), iconColor));
        holder.layoutIcon.setBackgroundResource(bgColor);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void clear() {
        notificationList.clear();
        notifyDataSetChanged();
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
