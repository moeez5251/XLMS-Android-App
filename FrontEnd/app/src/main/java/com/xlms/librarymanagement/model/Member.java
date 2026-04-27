package com.xlms.librarymanagement.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Member implements Serializable {
    @SerializedName("User_id")
    private String userId;
    
    @SerializedName("User_Name")
    private String name;
    
    @SerializedName("Email")
    private String email;
    
    @SerializedName("Role")
    private String role;
    
    @SerializedName("Membership_Type")
    private String membershipType;
    
    @SerializedName("Cost")
    private double cost;
    
    @SerializedName("Status")
    private String status; // "Active" or "Deactivated"
    private boolean isSelected;

    public Member(String userId, String name, String email, String role, 
                  String membershipType, double cost, String status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.membershipType = membershipType;
        this.cost = cost;
        this.status = status;
        this.isSelected = false;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
