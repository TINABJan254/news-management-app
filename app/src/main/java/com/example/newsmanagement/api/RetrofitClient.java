package com.example.newsmanagement.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl("https://liu.com.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private RetrofitClient() {
    }

    public static ApiService getApiService() {
        return RETROFIT.create(ApiService.class);
    }
}
