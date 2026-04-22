package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("token")
    private String token;
    @SerializedName("userid")
    private String userId;
    @SerializedName("role")
    private String role;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
