package com.example.newsmanagement.api.dto;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("current")
    public CurrentWeather current;
}
