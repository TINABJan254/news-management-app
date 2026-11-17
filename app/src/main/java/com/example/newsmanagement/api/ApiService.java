package com.example.newsmanagement.api;

import com.example.newsmanagement.api.dto.LunarDayResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/ngay/{date}")
    Call<LunarDayResponse> getLunarDay(@Path("date") String date);
}
