package com.example.newsmanagement.api;

import com.example.newsmanagement.api.dto.LunarDayResponse;
import com.example.newsmanagement.api.dto.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    @GET("api/ngay/{date}")
    Call<LunarDayResponse> getLunarDay(@Path("date") String date);

    @GET
    Call<WeatherResponse> getCurrentWeather(
            @Url String url,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current") String current,
            @Query("timezone") String timezone);
}
