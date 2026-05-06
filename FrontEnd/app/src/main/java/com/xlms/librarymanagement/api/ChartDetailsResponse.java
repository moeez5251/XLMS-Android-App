package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class ChartDetailsResponse {
    @SerializedName("returned")
    private int returned;
    @SerializedName("overdue")
    private int overdue;

    public int getReturned() { return returned; }
    public int getOverdue() { return overdue; }
}
