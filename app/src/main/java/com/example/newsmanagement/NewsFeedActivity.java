package com.example.newsmanagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.adapter.NewsFeedAdapter;
import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView rvNews;
    private NewsFeedAdapter newsAdapter;
    private TextView selectedChip;
    private BottomNavigationView bottomNavigation;
    private ImageView btnCategoryMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_feed);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        rvNews = findViewById(R.id.rvNews);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnCategoryMenu = findViewById(R.id.btnCategoryMenu);
        selectedChip = findViewById(R.id.chipLatest);
        rvNews.setLayoutManager(new LinearLayoutManager(this));
        bottomNavigation.setSelectedItemId(R.id.nav_news);
    }

    private void setupData() {
        Category vehicles = new Category("TL04", "Ô tô - Xe máy", "", 0);
        Category sports = new Category("TL01", "Thể thao", "", 0);
        Category world = new Category("TL05", "Thế giới", "", 0);
        Category social = new Category("TL02", "Xã hội", "", 0);
        Category life = new Category("TL06", "Đời sống", "", 0);

        List<News> newsList = new ArrayList<>();
        newsList.add(new News("TT-1082",
                "Mẫu B-SUV Nhật Bản này được giảm hơn 200 triệu đồng mới về ngang đối thủ",
                "", "", vehicles, "Admin", "1 giờ trước"));
        newsList.add(new News("TT-1081",
                "Bắt giam người lái xe gây tai nạn rồi bỏ trốn khỏi hiện trường",
                "", "", world, "Admin", "1 giờ trước"));
        newsList.add(new News("TT-1080",
                "Mưa lớn làm nhiều cây trong các tuyến phố ở Hà Nội bị đổ",
                "", "", social, "Admin", "2 giờ trước"));
        newsList.add(new News("TT-1079",
                "Sản phụ bị đá đè đa chấn thương đã tử vong",
                "", "", social, "Admin", "2 giờ trước"));
        newsList.add(new News("TT-1078",
                "Hơn 1.500 người tham dự màn đồng diễn yoga lớn nhất tại Thái",
                "", "", life, "Admin", "3 giờ trước"));
        newsList.add(new News("TT-1077",
                "Đội tuyển quốc gia hoàn thiện chiến thuật trước giải vô địch",
                "", "", sports, "Admin", "4 giờ trước"));

        newsAdapter = new NewsFeedAdapter(this, newsList,
                news -> {
                    Intent intent = new Intent(this, NewsDetailActivity.class);
                    intent.putExtra(NewsDetailActivity.EXTRA_NEWS, news);
                    startActivity(intent);
                });
        rvNews.setAdapter(newsAdapter);
    }

    private void setupEvents() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newsAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        int[] chipIds = {
                R.id.chipLatest, R.id.chipSports, R.id.chipSocial,
                R.id.chipWorld, R.id.chipLife
        };
        View.OnClickListener chipListener = view -> {
            TextView chip = (TextView) view;
            selectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
            selectedChip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            chip.setBackgroundResource(R.drawable.bg_selected);
            chip.setTextColor(ContextCompat.getColor(this, R.color.white));
            selectedChip = chip;
            newsAdapter.filterByCategory(chip.getText().toString());
        };
        for (int chipId : chipIds) {
            findViewById(chipId).setOnClickListener(chipListener);
        }
        btnCategoryMenu.setOnClickListener(v -> showCategorySheet());

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_news) {
                return true;
            }
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.nav_management) {
                startActivity(new Intent(this, ManagementActivity.class));
                finish();
                return true;
            }
            Toast.makeText(this, "Trang chủ đang được phát triển", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void showCategorySheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_news_categories);

        View sheet = dialog.findViewById(R.id.categorySheet);
        if (sheet != null) {
            sheet.setBackgroundResource(R.drawable.bg_bottom_sheet);
        }

        LinearLayout categoryContainer = dialog.findViewById(R.id.categoryOptions);
        if (categoryContainer == null) {
            dialog.show();
            return;
        }

        String[] categories = {
                "Mới nhất", "Bóng đá / Thể thao", "Xã hội", "Thế giới",
                "Kinh doanh", "Bất động sản", "Sức khỏe", "Ô tô - Xe máy",
                "Pháp luật", "Công nghệ"
        };
        for (String category : categories) {
            TextView option = (TextView) getLayoutInflater().inflate(
                    R.layout.item_news_category_option, categoryContainer, false);
            option.setText(category);
            option.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, categoryMatchesSelection(category) ? R.drawable.ic_check : 0, 0);
            option.setOnClickListener(v -> {
                selectCategory(category);
                dialog.dismiss();
            });
            categoryContainer.addView(option);
        }

        dialog.show();
    }

    private boolean categoryMatchesSelection(String category) {
        String selected = selectedChip.getText().toString();
        return category.equals("Mới nhất") && selected.equals("Mới nhất")
                || category.equals("Bóng đá / Thể thao")
                && (selected.equals("Bóng đá") || selected.equals("Thể thao"));
    }

    private void selectCategory(String category) {
        String chipName = category.equals("Bóng đá / Thể thao") ? "Bóng đá" : category;
        TextView chip = findChipByName(chipName);
        if (chip != null) {
            selectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
            selectedChip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            chip.setBackgroundResource(R.drawable.bg_selected);
            chip.setTextColor(ContextCompat.getColor(this, R.color.white));
            selectedChip = chip;
        }
        newsAdapter.filterByCategory(category);
    }

    private TextView findChipByName(String name) {
        int[] chipIds = {
                R.id.chipLatest, R.id.chipSports, R.id.chipSocial,
                R.id.chipWorld, R.id.chipLife
        };
        for (int chipId : chipIds) {
            TextView chip = findViewById(chipId);
            if (chip.getText().toString().equals(name)) {
                return chip;
            }
        }
        return null;
    }
}
