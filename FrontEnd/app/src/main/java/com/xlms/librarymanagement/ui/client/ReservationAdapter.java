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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        
        // Default status since it's not in DB
        holder.textViewStatus.setText("RESERVED");
        
        String formattedDate = formatDate(reservation.getReservationDate());
        holder.textViewReservationDate.setText("Reserved on: " + formattedDate);
        
        // Hide expiry if not available
        holder.textViewExpiryDate.setVisibility(View.GONE);
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "N/A";
        try {
            String cleanDate = dateStr.replace("Z", "+0000");
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            Date date = inputFormat.parse(cleanDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = inputFormat.parse(dateStr.split("T")[0]);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                return outputFormat.format(date);
            } catch (Exception e2) {
                return dateStr;
            }
        }
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
