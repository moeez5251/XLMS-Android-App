package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class AuthUsersResponse {
    @SerializedName("userID")
    private String userId;
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("refreshToken")
    private String refreshToken;
    
    @SerializedName("role")
    private String role;

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getRole() {
        return role;
    }
}
