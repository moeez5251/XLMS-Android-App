package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class DashboardDataResponse {
    @SerializedName("Totalbooks")
    private int totalBooks;
    @SerializedName("Totalusers")
    private int totalUsers;
    @SerializedName("Totalborrowers")
    private int totalBorrowers;
    @SerializedName("availablebooks")
    private int availableBooks;
    @SerializedName("overduebooks")
    private int overdueBooks;

    public int getTotalBooks() { return totalBooks; }
    public int getTotalUsers() { return totalUsers; }
    public int getTotalBorrowers() { return totalBorrowers; }
    public int getAvailableBooks() { return availableBooks; }
    public int getOverdueBooks() { return overdueBooks; }
}
