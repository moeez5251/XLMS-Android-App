package com.xlms.librarymanagement.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UpdateUserRequest implements Serializable {
    @SerializedName("ID")
    private String id;
    
    @SerializedName("User_Name")
    private String name;
    
    @SerializedName("Email")
    private String email;
    
    @SerializedName("Role")
    private String role;
    
    @SerializedName("Membership_Type")
    private String membershipType;

    public UpdateUserRequest(String id, String name, String email, String role, String membershipType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.membershipType = membershipType;
    }
}
