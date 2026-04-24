package com.xlms.librarymanagement.api;

import android.content.Context;
import com.xlms.librarymanagement.utils.SessionManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;
import android.preference.PreferenceManager;

public class ApiClient {
    private static final String BASE_URL = "http://127.0.0.1:5000/api/";
    private static Retrofit retrofit = null;
    private static Set<String> cookies = new HashSet<>();

    public static ApiService getApiService(Context context) {
        final Context appContext = context.getApplicationContext();

        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder();

                            // Add stored cookies to outgoing request
                            for (String cookie : cookies) {
                                requestBuilder.addHeader("Cookie", cookie);
                            }

                            // Add Authorization header
                            SessionManager sessionManager = new SessionManager(appContext);
                            String token = sessionManager.getAuthToken();
                            if (token != null && !token.isEmpty() && original.header("Authorization") == null) {
                                requestBuilder.addHeader("Authorization", "Bearer " + token);
                            }

                            Response response = chain.proceed(requestBuilder.build());

                            // Save new cookies from incoming response
                            if (!response.headers("Set-Cookie").isEmpty()) {
                                for (String header : response.headers("Set-Cookie")) {
                                    cookies.add(header);
                                }
                            }
                            return response;
                        }
                    })
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public static void resetClient() {
        retrofit = null;
        cookies.clear(); // Clear cookies on client reset
    }
}
