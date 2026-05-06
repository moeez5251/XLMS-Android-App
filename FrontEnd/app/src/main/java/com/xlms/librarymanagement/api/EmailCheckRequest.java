package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class EmailCheckRequest {
    @SerializedName("email")
    private String email;

    public EmailCheckRequest(String email) {
        this.email = email;
    }
}
