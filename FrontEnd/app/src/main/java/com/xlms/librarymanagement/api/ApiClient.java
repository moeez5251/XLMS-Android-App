package com.xlms.librarymanagement.api;

import android.content.Context;
import androidx.annotation.Nullable;
import com.xlms.librarymanagement.utils.SessionManager;
import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://127.0.0.1:5000/api/";
    private static Retrofit retrofit = null;
    private static Set<String> cookies = new HashSet<>();

    public static ApiService getApiService(Context context) {
        final Context appContext = context.getApplicationContext();
        final SessionManager sessionManager = new SessionManager(appContext);

        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .authenticator(new Authenticator() {
                        @Nullable
                        @Override
                        public Request authenticate(@Nullable Route route, Response response) throws IOException {
                            if (responseCount(response) >= 2) return null;

                            SessionManager sessionManager = new SessionManager(appContext);
                            String newToken = sessionManager.getAuthToken();

                            if (newToken != null) {
                                return response.request().newBuilder()
                                        .header("Authorization", "Bearer " + newToken)
                                        .build();
                            }
                            return null;
                        }
                    })
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder();

                            String token = sessionManager.getAuthToken();
                            Set<String> savedCookies = sessionManager.getCookies();
                            
                            if (token != null && !token.isEmpty()) {
                                if (original.header("Authorization") == null) {
                                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                                }
                            }
                            
                            // Always send saved cookies (which includes the refresh token)
                            if (!savedCookies.isEmpty()) {
                                StringBuilder cookieHeader = new StringBuilder();
                                for (String cookie : savedCookies) {
                                    // Extract only the key=value part of the cookie
                                    String cookieValue = cookie.split(";")[0];
                                    if (cookieHeader.length() > 0) cookieHeader.append("; ");
                                    cookieHeader.append(cookieValue);
                                }
                                requestBuilder.header("Cookie", cookieHeader.toString());
                            }
                            
                            // Proceed with the request
                            Response response = chain.proceed(requestBuilder.build());

                            // Check if backend sent a new token in response headers
                            String newToken = response.header("X-New-Token");
                            if (newToken != null && !newToken.isEmpty()) {
                                // Update session with new token
                                String email = sessionManager.getUserEmail();
                                String role = sessionManager.getUserRole();
                                String userId = sessionManager.getUserId();
                                sessionManager.saveSession(email, role, null, userId, newToken);
                            }

                            // Save cookies persistently
                            if (!response.headers("Set-Cookie").isEmpty()) {
                                Set<String> newCookies = new HashSet<>(response.headers("Set-Cookie"));
                                sessionManager.saveCookies(newCookies);
                            }
                            return response;
                        }
                    })
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    public static void resetClient() {
        retrofit = null;
        cookies.clear();
    }
}
