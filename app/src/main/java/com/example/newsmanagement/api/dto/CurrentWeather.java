package com.example.newsmanagement.api.dto;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather {
    @SerializedName("temperature_2m")
    public double temperature;

    @SerializedName("relative_humidity_2m")
    public int humidity;

    @SerializedName("weather_code")
    public int weatherCode;
}
