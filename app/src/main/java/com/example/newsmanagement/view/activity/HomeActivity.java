package com.example.newsmanagement.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.view.adapter.NewsFeedAdapter;
import com.example.newsmanagement.R;
import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        setupLatestNews();
        setupNavigation();
    }

    private void setupLatestNews() {
        Category social = new Category("TL02", "Xã hội", "", 0);
        Category world = new Category("TL05", "Thế giới", "", 0);
        Category sports = new Category("TL01", "Thể thao", "", 0);

        News featuredNews = new News("TT-1080",
                "Miền Bắc mưa đến khi nào?",
                "Dự báo thời tiết mới nhất khu vực miền Bắc.",
                "", social, "Admin", "6 giờ trước");

        List<News> newsList = new ArrayList<>();
        newsList.add(new News("TT-1079",
                "Mưa lớn làm nhiều cây đổ tại Hà Nội",
                "Thông tin mới nhất từ hiện trường.",
                "", social, "Admin", "2 giờ trước"));
        newsList.add(new News("TT-1078",
                "Nhà lãnh đạo EU tổ chức họp khẩn",
                "Các nước châu Âu thảo luận những vấn đề quan trọng.",
                "", world, "Admin", "5 giờ trước"));
        newsList.add(new News("TT-1077",
                "Đội tuyển quốc gia chuẩn bị cho giải đấu mới",
                "Đội tuyển đang hoàn thiện chiến thuật.",
                "", sports, "Admin", "7 giờ trước"));

        TextView tvFeaturedCategory = findViewById(R.id.tvFeaturedCategory);
        TextView tvFeaturedTitle = findViewById(R.id.tvFeaturedTitle);
        TextView tvFeaturedTime = findViewById(R.id.tvFeaturedTime);
        tvFeaturedCategory.setText(featuredNews.getCategoryName() + " · " + featuredNews.getPublishDate());
        tvFeaturedTitle.setText(featuredNews.getTitle());
        tvFeaturedTime.setText("34 nghìn lượt xem");
        findViewById(R.id.featuredNews).setOnClickListener(v -> openNews(featuredNews));

        RecyclerView rvLatestNews = findViewById(R.id.rvLatestNews);
        rvLatestNews.setLayoutManager(new LinearLayoutManager(this));
        rvLatestNews.setAdapter(new NewsFeedAdapter(this, newsList, this::openNews));
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            }
            if (item.getItemId() == R.id.nav_news) {
                navigateTo(NewsFeedActivity.class);
                return true;
            }
            navigateTo(ManagementActivity.class);
            return true;
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void openNews(News news) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS, news);
        startActivity(intent);
    }
}
