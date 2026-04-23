package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class VerifyOtpRequest {
    @SerializedName("Email")
    private String email;
    @SerializedName("OTP")
    private String otp;

    public VerifyOtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}
