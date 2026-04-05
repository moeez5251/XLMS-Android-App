package com.xlms.librarymanagement.model;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String language;
    private double price;
    private int total;
    private int available;
    private String status; // "Available", "Limited", "Out of Stock"

    public Book(String bookId, String title, String author, String category, 
                String language, double price, int total, int available, String status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.language = language;
        this.price = price;
        this.total = total;
        this.available = available;
        this.status = status;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getLanguage() { return language; }
    public double getPrice() { return price; }
    public int getTotal() { return total; }
    public int getAvailable() { return available; }
    public String getStatus() { return status; }
}
