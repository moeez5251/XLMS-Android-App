package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("message")
    private String message;
    @SerializedName(value = "userId", alternate = {"user_id", "userid", "userID"})
    private String userId;
    @SerializedName("token")
    private String token;
    @SerializedName("role")
    private String role;
    @SerializedName("error")
    private String error;

    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getError() { return error; }
}
