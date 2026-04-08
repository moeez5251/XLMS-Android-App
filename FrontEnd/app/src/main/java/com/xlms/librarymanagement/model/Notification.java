package com.xlms.librarymanagement.model;

public class Notification {
    public static final int TYPE_WARNING = 0;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_SUCCESS = 2;
    public static final int TYPE_SYSTEM = 3;

    private int type;
    private String title;
    private String description;
    private String time;

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
