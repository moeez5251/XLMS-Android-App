package com.xlms.librarymanagement.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BookLending implements Serializable {
    @SerializedName("Name")
    private String lenderName;
    
    @SerializedName("Email")
    private String lenderEmail;
    
    @SerializedName("BookTitle")
    private String bookTitle;
    
    @SerializedName("Category")
    private String bookCategory;
    
    @SerializedName("Author")
    private String bookAuthor;
    
    @SerializedName("IssuedDate")
    private String issuedDate;
    
    @SerializedName("DueDate")
    private String dueDate;
    
    @SerializedName("CopiesLent")
    private int copiesLent;
    
    @SerializedName("FinePerDay")
    private double perDayFine;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("Book_ID")
    private String bookId;

    @SerializedName("Status")
    private String status;

    @SerializedName("Borrower_ID")
    private int borrowerId;

    public BookLending() {}

    public String getLenderName() { return lenderName; }
    public void setLenderName(String lenderName) { this.lenderName = lenderName; }

    public String getLenderEmail() { return lenderEmail; }
    public void setLenderEmail(String lenderEmail) { this.lenderEmail = lenderEmail; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookCategory() { return bookCategory; }
    public void setBookCategory(String bookCategory) { this.bookCategory = bookCategory; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public int getCopiesLent() { return copiesLent; }
    public void setCopiesLent(int copiesLent) { this.copiesLent = copiesLent; }

    public double getPerDayFine() { return perDayFine; }
    public void setPerDayFine(double perDayFine) { this.perDayFine = perDayFine; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getBorrowerId() { return borrowerId; }
    public void setBorrowerId(int borrowerId) { this.borrowerId = borrowerId; }
}
