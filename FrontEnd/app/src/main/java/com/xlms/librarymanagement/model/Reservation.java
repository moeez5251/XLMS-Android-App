package com.xlms.librarymanagement.model;

import java.io.Serializable;

public class Reservation implements Serializable {
    private int id;
    private int bookId;
    private String bookTitle;
    private String author;
    private String userEmail;
    private String reservationDate;
    private String expiryDate;
    private String status; // "Pending", "Fulfilled", "Cancelled", "Expired"

    public Reservation(int id, int bookId, String bookTitle, String author, String userEmail, String reservationDate, String expiryDate, String status) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.userEmail = userEmail;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getAuthor() { return author; }
    public String getUserEmail() { return userEmail; }
    public String getReservationDate() { return reservationDate; }
    public String getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
}
