package com.xlms.librarymanagement.model;

import java.io.Serializable;

public class BookInfo implements Serializable {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String language;
    private double price;
    private int totalCopies;
    private int availableCopies;
    private String status;

    public BookInfo(String bookId, String title, String author, String category,
                    String language, double price, int totalCopies, int availableCopies, String status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.language = language;
        this.price = price;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.status = status;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getLanguage() { return language; }
    public double getPrice() { return price; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public String getStatus() { return status; }
}
