package com.xlms.librarymanagement.ui.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xlms.librarymanagement.R;
import com.xlms.librarymanagement.model.Reservation;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private List<Reservation> reservationList;

    public ReservationAdapter(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.textViewBookTitle.setText(reservation.getBookTitle());
        holder.textViewAuthor.setText(reservation.getAuthor());
        holder.textViewStatus.setText(reservation.getStatus().toUpperCase());
        holder.textViewReservationDate.setText(holder.itemView.getContext().getString(R.string.label_reserved_at, reservation.getReservationDate()));
        holder.textViewExpiryDate.setText(holder.itemView.getContext().getString(R.string.label_expires_at, reservation.getExpiryDate()));
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookTitle, textViewAuthor, textViewStatus, textViewReservationDate, textViewExpiryDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewReservationDate = itemView.findViewById(R.id.textViewReservationDate);
            textViewExpiryDate = itemView.findViewById(R.id.textViewExpiryDate);
        }
    }
}
