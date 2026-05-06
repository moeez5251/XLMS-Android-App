package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class MyStatsResponse {
    @SerializedName("lended")
    private int lended;
    @SerializedName("overdue")
    private int overdue;
    @SerializedName("reserved")
    private int reserved;

    public int getLended() { return lended; }
    public int getOverdue() { return overdue; }
    public int getReserved() { return reserved; }
}
