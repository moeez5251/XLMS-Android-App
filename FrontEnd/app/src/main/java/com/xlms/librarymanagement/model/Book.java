package com.xlms.librarymanagement.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Book implements Serializable {
    @SerializedName("Book_ID")
    private String bookId;
    
    @SerializedName("Book_Title")
    private String title;
    
    @SerializedName("Author")
    private String author;
    
    @SerializedName("Category")
    private String category;
    
    @SerializedName("Language")
    private String language;
    
    @SerializedName("Price")
    private double price;
    
    @SerializedName("Total_Copies")
    private int total;
    
    @SerializedName("Available")
    private int available;
    
    @SerializedName("Status")
    private String status;
    
    @SerializedName("Pages")
    private int pages;

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
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
}
