package com.xlms.librarymanagement.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("users/register")
    Call<MessageResponse> register(@Body RegisterRequest registerRequest);

    @POST("mail/verify")
    Call<MessageResponse> verifyOtp(@Body VerifyOtpRequest verifyOtpRequest);

    @POST("mail/resend")
    Call<MessageResponse> resendOtp(@Body OtpRequest otpRequest);

    @retrofit2.http.GET("other/getbookdata")
    Call<DashboardDataResponse> getDashboardData();

    @retrofit2.http.GET("notifications/get")
    Call<java.util.List<com.xlms.librarymanagement.model.Notification>> getNotifications();
}
