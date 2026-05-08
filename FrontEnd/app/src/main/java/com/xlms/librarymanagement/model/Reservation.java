package com.xlms.librarymanagement.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Reservation implements Serializable {
    @SerializedName("Reservation_ID")
    private int id;
    
    @SerializedName("User_ID")
    private String userId;
    
    @SerializedName("Book_ID")
    private String bookId;
    
    @SerializedName("Reserved_Date")
    private String reservationDate;
    
    // Virtual fields populated via frontend join
    private String bookTitle;
    private String author;

    public Reservation(int id, String userId, String bookId, String reservationDate) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.reservationDate = reservationDate;
    }

    public int getId() { return id; }
    public String getUserId() { return userId; }
    public String getBookId() { return bookId; }
    public String getReservationDate() { return reservationDate; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
