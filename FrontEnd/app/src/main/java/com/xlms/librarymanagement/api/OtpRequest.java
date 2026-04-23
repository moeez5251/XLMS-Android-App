package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {
    @SerializedName("Name")
    private String name;
    @SerializedName("Email")
    private String email;

    public OtpRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
