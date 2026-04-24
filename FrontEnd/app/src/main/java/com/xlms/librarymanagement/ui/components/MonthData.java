package com.xlms.librarymanagement.ui.components;

public class MonthData {
    private String month;
    private int desktop;
    private int mobile;

    public MonthData(String month, int desktop, int mobile) {
        this.month = month;
        this.desktop = desktop;
        this.mobile = mobile;
    }

    public String getMonth() {
        return month;
    }

    public int getDesktop() {
        return desktop;
    }

    public int getMobile() {
        return mobile;
    }
}
