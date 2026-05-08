package com.xlms.librarymanagement.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class LendedBook implements Serializable {
    @SerializedName("Borrower_ID")
    private String borrowerId;
    
    @SerializedName("Book_ID")
    private String bookId;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("Name")
    private String userName;
    
    private String userInitial;
    
    @SerializedName("BookTitle")
    private String bookTitle;
    
    @SerializedName("Author")
    private String author;
    
    @SerializedName("Category")
    private String category;
    
    @SerializedName("CopiesLent")
    private int copies;
    
    @SerializedName("IssuedDate")
    private String issuedDate;
    
    @SerializedName("DueDate")
    private String dueDate;
    
    @SerializedName("Status")
    private String status; // "Returned" or "Not Returned"
    
    @SerializedName("FinePerDay")
    private int finePerDay;

    public LendedBook() {}

    public LendedBook(String borrowerId, String bookId, String userId, String userName, String userInitial,
                      String bookTitle, String author, String category, int copies,
                      String issuedDate, String dueDate, String status, int finePerDay) {
        this.borrowerId = borrowerId;
        this.bookId = bookId;
        this.userId = userId;
        this.userName = userName;
        this.userInitial = userInitial;
        this.bookTitle = bookTitle;
        this.author = author;
        this.category = category;
        this.copies = copies;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.status = status;
        this.finePerDay = finePerDay;
    }

    public String getBorrowerId() { return borrowerId; }
    public String getBookId() { return bookId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserInitial() { return userInitial; }
    public String getBookTitle() { return bookTitle; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getCopies() { return copies; }
    public String getIssuedDate() { return issuedDate; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public int getFinePerDay() { return finePerDay; }
}
