package com.xlms.librarymanagement.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    public static final int TYPE_WARNING = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_SUCCESS = 2;
    public static final int TYPE_SYSTEM = 3;

    @SerializedName("Message")
    private String description;
    
    @SerializedName("CreatedAt")
    private String time;
    
    @SerializedName("IsRead")
    private boolean isRead;

    private int type = TYPE_INFO; // Default type
    private String title = "System Notification"; // Default title

    public Notification(int type, String title, String description, String time) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.time = time;
    }

    public int getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTime() { return time; }
}
