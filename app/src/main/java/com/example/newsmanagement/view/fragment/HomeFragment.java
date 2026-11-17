package com.example.newsmanagement.view.fragment;

import android.app.Fragment;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsmanagement.R;
import com.example.newsmanagement.api.ApiService;
import com.example.newsmanagement.api.RetrofitClient;
import com.example.newsmanagement.api.dto.FuelPrice;
import com.example.newsmanagement.api.dto.WeatherResponse;
import com.example.newsmanagement.api.dto.LunarDayResponse;
import com.example.newsmanagement.data.NewsDatabaseHelper;
import com.example.newsmanagement.view.activity.NewsDetailActivity;
import com.example.newsmanagement.view.adapter.NewsFeedAdapter;
import com.example.newsmanagement.model.News;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final double HANOI_LATITUDE = 21.0285;
    private static final double HANOI_LONGITUDE = 105.8542;
    private FusedLocationProviderClient locationClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        view.findViewById(R.id.bottomNavigation).setVisibility(View.GONE);
        setupLatestNews(view);
        loadLunarDate(view);
        loadWeather(view);
        loadFuelPrices(view);
        return view;
    }

    private void loadFuelPrices(View view) {
        TextView fuelPrices = view.findViewById(R.id.tvFuelPrices);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String date = dateFormat.format(new java.util.Date());

        RetrofitClient.getApiService().getFuelPrices(
                        "https://giaxanghomnay.com/api/pvdate/" + date)
                .enqueue(new Callback<List<List<FuelPrice>>>() {
                    @Override
                    public void onResponse(
                            Call<List<List<FuelPrice>>> call,
                            Response<List<List<FuelPrice>>> response) {
                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().isEmpty()) {
                            fuelPrices.setText("Không tải được giá xăng");
                            return;
                        }

                        FuelPrice ron95 = null;
                        FuelPrice e5 = null;
                        for (FuelPrice fuel : response.body().get(0)) {
                            if (fuel.title == null) {
                                continue;
                            }
                            if (fuel.title.contains("RON 95") && fuel.title.contains("Mức 3")) {
                                ron95 = fuel;
                            } else if (fuel.title.contains("E5 RON 92")) {
                                e5 = fuel;
                            }
                        }

                        fuelPrices.setText("E10 R95 - " + formatPrice(ron95)
                                + "\nE5 R92  - " + formatPrice(e5));
                    }

                    @Override
                    public void onFailure(
                            Call<List<List<FuelPrice>>> call,
                            Throwable throwable) {
                        fuelPrices.setText("Không có kết nối");
                    }
                });
    }

    private String formatPrice(FuelPrice fuel) {
        if (fuel == null) {
            return "--";
        }
        return NumberFormat.getNumberInstance(new Locale("vi", "VN"))
                .format(fuel.zone1Price);
    }

    private void loadWeather(View view) {
        locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            new CancellationTokenSource().getToken())
                    .addOnSuccessListener(location -> {
                        if (location == null) {
                            requestWeather(view, HANOI_LATITUDE, HANOI_LONGITUDE, "Hà Nội");
                        } else {
                            requestWeather(view, location.getLatitude(), location.getLongitude(),
                                    "Vị trí của bạn");
                        }
                    })
                    .addOnFailureListener(exception ->
                            requestWeather(view, HANOI_LATITUDE, HANOI_LONGITUDE, "Hà Nội"));
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_PERMISSION_REQUEST);
        }
    }

    private void requestWeather(View view, double latitude, double longitude, String locationName) {
        TextView locationText = view.findViewById(R.id.tvWeatherLocation);
        TextView temperatureText = view.findViewById(R.id.tvWeatherTemperature);
        TextView humidityText = view.findViewById(R.id.tvWeatherHumidity);
        locationText.setText("☁ " + locationName);

        RetrofitClient.getApiService().getCurrentWeather(
                        "https://api.open-meteo.com/v1/forecast",
                        latitude,
                        longitude,
                        "temperature_2m,relative_humidity_2m,weather_code",
                        "Asia/Bangkok")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call,
                                           Response<WeatherResponse> response) {
                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().current == null) {
                            temperatureText.setText("--°C");
                            humidityText.setText("Không tải được thời tiết");
                            return;
                        }

                        WeatherResponse weather = response.body();
                        temperatureText.setText(String.format(Locale.ROOT, "%.0f°C",
                                weather.current.temperature));
                        humidityText.setText("Độ ẩm " + weather.current.humidity + "%");
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable throwable) {
                        temperatureText.setText("--°C");
                        humidityText.setText("Không có kết nối");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && getView() != null) {
            boolean permissionGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    break;
                }
            }
            if (permissionGranted) {
                loadWeather(getView());
            } else {
                requestWeather(getView(), HANOI_LATITUDE, HANOI_LONGITUDE, "Hà Nội");
            }
        }
    }

    private void loadLunarDate(View view) {
        TextView solarDate = view.findViewById(R.id.tvLunarSolarDate);
        TextView lunarDate = view.findViewById(R.id.tvLunarDate);
        TextView lunarMonth = view.findViewById(R.id.tvLunarMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String date = dateFormat.format(new java.util.Date());

        ApiService service = RetrofitClient.getApiService();

        service.getLunarDay(date).enqueue(new Callback<LunarDayResponse>() {
            @Override
            public void onResponse(
                    Call<LunarDayResponse> call,
                    Response<LunarDayResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    lunarDate.setText("--");
                    lunarMonth.setText("Không tải được lịch âm");
                    return;
                }

                LunarDayResponse data = response.body();
                solarDate.setText("🗓️ " + data.ngayDuong);
                if (data.amLich != null) {
                    lunarDate.setText(String.valueOf(data.amLich.ngay));
                    lunarMonth.setText("Tháng " + data.amLich.thang
                            + (data.canChi == null ? "" : " \n " + data.canChi.nam));
                } else {
                    lunarDate.setText("--");
                    lunarMonth.setText("Không có dữ liệu âm lịch");
                }
            }

            @Override
            public void onFailure(
                    Call<LunarDayResponse> call,
                    Throwable throwable) {
                lunarDate.setText("--");
                lunarMonth.setText("Không có kết nối");
            }
        });
    }

    private void setupLatestNews(View view) {
        NewsDatabaseHelper databaseHelper = new NewsDatabaseHelper(getActivity());
        List<News> latestNews = databaseHelper.getNewsPage("", "", "", "", 1, 5);
        databaseHelper.close();

        TextView category = view.findViewById(R.id.tvFeaturedCategory);
        TextView title = view.findViewById(R.id.tvFeaturedTitle);
        ImageView featuredImage = view.findViewById(R.id.ivFeaturedNews);
        View featuredView = view.findViewById(R.id.featuredNews);
        RecyclerView recyclerView = view.findViewById(R.id.rvLatestNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (latestNews.isEmpty()) {
            featuredView.setVisibility(View.GONE);
            recyclerView.setAdapter(new NewsFeedAdapter(
                    getActivity(), new ArrayList<>(), this::openNews));
            return;
        }

        int newsCount = Math.min(5, latestNews.size());
        News featuredNews = latestNews.get(0);
        List<News> newsList = new ArrayList<>(latestNews.subList(1, newsCount));

        category.setText(featuredNews.getCategoryName() + " · " + featuredNews.getPublishDate());
        title.setText(featuredNews.getTitle());
        Glide.with(featuredImage)
                .load(featuredNews.getImageUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(featuredImage);
        featuredView.setOnClickListener(v -> openNews(featuredNews));
        recyclerView.setAdapter(new NewsFeedAdapter(getActivity(), newsList, this::openNews));
    }

    

        

    private void openNews(News news) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS, news);
        startActivity(intent);
    }
}
