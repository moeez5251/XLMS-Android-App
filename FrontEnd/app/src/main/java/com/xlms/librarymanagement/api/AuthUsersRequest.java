package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class AuthUsersRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;

    public AuthUsersRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
