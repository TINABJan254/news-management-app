package com.example.newsmanagement.view.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.example.newsmanagement.R;
import com.example.newsmanagement.data.CategoryDatabaseHelper;
import com.example.newsmanagement.data.NewsDatabaseHelper;

import java.text.NumberFormat;

public class ManagementActivity extends AppCompatActivity {

    private TextView tvTotalArticles;
    private TextView tvTotalCategories;
    private MaterialCardView cardArticlesCount;
    private MaterialCardView cardCategoriesCount;
    private MaterialCardView cardManageNews;
    private MaterialCardView cardManageCategories;
    private BottomNavigationView bottomNavigation;
    private CategoryDatabaseHelper categoryDatabaseHelper;
    private NewsDatabaseHelper newsDatabaseHelper;

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

        categoryDatabaseHelper = new CategoryDatabaseHelper(this);
        newsDatabaseHelper = new NewsDatabaseHelper(this);
        initViews();
        setupEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        tvTotalArticles.setText(numberFormat.format(newsDatabaseHelper.getNewsCount()));
        tvTotalCategories.setText(numberFormat.format(categoryDatabaseHelper.getCategoryCount()));
    }

    @Override
    protected void onDestroy() {
        newsDatabaseHelper.close();
        categoryDatabaseHelper.close();
        super.onDestroy();
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
            startActivity(new android.content.Intent(this, NewsManagementActivity.class));
        });

        // Sự kiện click Thẻ Quản lý Thể loại
        cardManageCategories.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, CategoryManagementActivity.class));
        });

        // Sự kiện Bottom Navigation Bar
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                navigateTo(HomeActivity.class);
                return true;
            } else if (itemId == R.id.nav_news) {
                navigateTo(NewsFeedActivity.class);
                return true;
            } else if (itemId == R.id.nav_management) {
                return true;
            }
            return false;
        });
    }

    private void navigateTo(Class<?> activityClass) {
        android.content.Intent intent = new android.content.Intent(this, activityClass);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
