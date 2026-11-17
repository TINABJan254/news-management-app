package com.example.newsmanagement.api.dto;

import com.google.gson.annotations.SerializedName;

public class LunarDayResponse {
    @SerializedName("ngay_duong")
    public String ngayDuong;

    @SerializedName("am_lich")
    public AmLich amLich;

    @SerializedName("can_chi")
    public CanChi canChi;
}
