package com.xlms.librarymanagement.model;

import java.io.Serializable;

public class BookLending implements Serializable {
    private String lenderName;
    private String lenderEmail;
    private String bookTitle;
    private String bookCategory;
    private String bookAuthor;
    private String issuedDate;
    private String dueDate;
    private int copiesLent;
    private double perDayFine;

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
}
