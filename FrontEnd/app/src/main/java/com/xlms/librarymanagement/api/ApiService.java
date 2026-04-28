package com.xlms.librarymanagement.api;

import com.xlms.librarymanagement.model.Member;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

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

    @POST("books/getbyID")
    Call<java.util.List<com.xlms.librarymanagement.model.Book>> getBookById(@Body GetByIdRequest request);

    @PUT("books/update")
    Call<MessageResponse> updateBook(@Body com.xlms.librarymanagement.model.Book book);

    @POST("books/get")
    Call<java.util.List<com.xlms.librarymanagement.model.Book>> getBooks();

    @POST("books/col")
    Call<java.util.List<com.google.gson.JsonObject>> getDistinctValues(@Body ColumnRequest columnRequest);
    @POST("books/insert")
    Call<MessageResponse> insertBook(@Body com.xlms.librarymanagement.model.Book book);

    @retrofit2.http.HTTP(method = "DELETE", path = "books/delete", hasBody = true)
    Call<MessageResponse> deleteBook(@Body List<String> bookIds);

    @POST("users/all")
    Call<java.util.List<com.xlms.librarymanagement.model.Member>> getAllUsers();

    @POST("users/update")
    Call<MessageResponse> updateUser(@Body Member request);

    @retrofit2.http.HTTP(method = "DELETE", path = "users/delete", hasBody = true)
    Call<MessageResponse> deleteUser(@Body List<String> userIds);

    @POST("users/activate")
    Call<MessageResponse> activateUser(@Body com.google.gson.JsonObject idObj);

    @POST("users/deactivate")
    Call<MessageResponse> deactivateUser(@Body List<String> userIds);
}
