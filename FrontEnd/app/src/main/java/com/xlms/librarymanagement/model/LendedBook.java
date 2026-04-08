package com.xlms.librarymanagement.model;

import java.io.Serializable;

public class LendedBook implements Serializable {
    private int bookId;
    private String userId;
    private String userName;
    private String userInitial;
    private String bookTitle;
    private String author;
    private String category;
    private int copies;
    private String issuedDate;
    private String dueDate;
    private String status; // "Returned" or "Not Returned"

    public LendedBook(int bookId, String userId, String userName, String userInitial,
                      String bookTitle, String author, String category, int copies,
                      String issuedDate, String dueDate, String status) {
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
    }

    public int getBookId() { return bookId; }
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
}
