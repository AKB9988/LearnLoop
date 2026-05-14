package com.example.learnloop.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client for the LearnLoop FastAPI backend.
 *
 * Usage:
 *   ApiService api = RetrofitClient.getInstance().getApiService();
 *   api.getActiveRequests("Bearer <token>").enqueue(...);
 *
 * NOTE: Update BASE_URL to your deployed FastAPI server address.
 */
public class RetrofitClient {

    // TODO: Replace with your actual FastAPI server URL
    private static final String BASE_URL = "https://scorebook-unlinked-relieving.ngrok-free.dev/"; // ngrok link

    private static RetrofitClient instance;
    private final ApiService apiService;

    private RetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder()
                            .header("ngrok-skip-browser-warning", "69420")
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * Thread-safe singleton accessor.
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    /**
     * Get the ApiService for making network calls.
     */
    public ApiService getApiService() {
        return apiService;
    }
}
