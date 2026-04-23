package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("User_Name")
    private String userName;
    @SerializedName("Email")
    private String email;
    @SerializedName("Role")
    private String role;
    @SerializedName("Membership_Type")
    private String membershipType;
    @SerializedName("Password")
    private String password;

    public RegisterRequest(String userName, String email, String role, String membershipType, String password) {
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.membershipType = membershipType;
        this.password = password;
    }
}
