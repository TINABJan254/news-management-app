package com.example.newsmanagement;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class ManagementActivity extends AppCompatActivity {

    private TextView tvTotalArticles;
    private TextView tvTotalCategories;
    private MaterialCardView cardArticlesCount;
    private MaterialCardView cardCategoriesCount;
    private MaterialCardView cardManageNews;
    private MaterialCardView cardManageCategories;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();
        setupEvents();
    }

    private void initViews() {
        tvTotalArticles = findViewById(R.id.tvTotalArticles);
        tvTotalCategories = findViewById(R.id.tvTotalCategories);
        cardArticlesCount = findViewById(R.id.cardArticlesCount);
        cardCategoriesCount = findViewById(R.id.cardCategoriesCount);
        cardManageNews = findViewById(R.id.cardManageNews);
        cardManageCategories = findViewById(R.id.cardManageCategories);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Mặc định chọn tab Quản lý
        bottomNavigation.setSelectedItemId(R.id.nav_management);
    }

    private void setupEvents() {
        // Sự kiện click Thẻ Quản lý Tin tức
        cardManageNews.setOnClickListener(v -> {
            Toast.makeText(this, "Mở Quản lý Tin tức", Toast.LENGTH_SHORT).show();
            // TODO: Chuyển sang màn hình danh sách Tin tức
        });

        // Sự kiện click Thẻ Quản lý Thể loại
        cardManageCategories.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, CategoryManagementActivity.class));
        });

        // Sự kiện Bottom Navigation Bar
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(this, "Chuyển sang Trang chủ", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_news) {
                Toast.makeText(this, "Chuyển sang Tin tức", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_management) {
                return true;
            }
            return false;
        });
    }
}
