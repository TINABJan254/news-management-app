package com.example.newsmanagement.api.dto;

import com.google.gson.annotations.SerializedName;

public class FuelPrice {
    public String title;

    @SerializedName("zone1_price")
    public int zone1Price;

    @SerializedName("zone2_price")
    public int zone2Price;
}
