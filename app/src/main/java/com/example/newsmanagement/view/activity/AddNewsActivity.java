package com.example.newsmanagement.view.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;
import com.example.newsmanagement.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNewsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etNewsCode;
    private View layoutSelectDate;
    private TextView tvNewsPublishDate;
    private Spinner spCategory;
    private EditText etNewsTitle;
    private EditText etNewsImageUrl;
    private ImageView ivNewsPreview;
    private EditText etNewsDetail;
    private TextView btnCancel;
    private TextView btnSaveNews;

    private List<Category> categoryList;
    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_news);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etNewsCode = findViewById(R.id.etNewsCode);
        layoutSelectDate = findViewById(R.id.layoutSelectDate);
        tvNewsPublishDate = findViewById(R.id.tvNewsPublishDate);
        spCategory = findViewById(R.id.spCategory);
        etNewsTitle = findViewById(R.id.etNewsTitle);
        etNewsImageUrl = findViewById(R.id.etNewsImageUrl);
        ivNewsPreview = findViewById(R.id.ivNewsPreview);
        etNewsDetail = findViewById(R.id.etNewsDetail);
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveNews = findViewById(R.id.btnSaveNews);
    }

    private void setupData() {
        selectedCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvNewsPublishDate.setText(dateFormat.format(new Date()));

        // Gợi ý mã tin tức mặc định
        etNewsCode.setText("TT-1083");

        // Nạp danh sách thể loại vào Spinner
        categoryList = new ArrayList<>();
        categoryList.add(new Category("TL01", "Thể thao", "", 0));
        categoryList.add(new Category("TL02", "Xã hội", "", 0));
        categoryList.add(new Category("TL03", "Pháp luật", "", 0));
        categoryList.add(new Category("TL04", "Ô tô - Xe máy", "", 0));
        categoryList.add(new Category("TL05", "Thế giới", "", 0));

        List<String> categoryNames = new ArrayList<>();
        for (Category cat : categoryList) {
            categoryNames.add(cat.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categoryNames
        );
        spCategory.setAdapter(spinnerAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        layoutSelectDate.setOnClickListener(v -> showDatePicker());
        btnSaveNews.setOnClickListener(v -> handleSaveNews());
    }

    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedCalendar.set(y, m, d);
            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
            tvNewsPublishDate.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void handleSaveNews() {
        String code = etNewsCode.getText().toString().trim();
        String title = etNewsTitle.getText().toString().trim();
        String detail = etNewsDetail.getText().toString().trim();
        String imageUrl = etNewsImageUrl.getText().toString().trim();
        String publishDate = tvNewsPublishDate.getText().toString().trim();

        if (code.isEmpty()) {
            etNewsCode.setError("Vui lòng nhập mã tin tức!");
            etNewsCode.requestFocus();
            return;
        }

        if (title.isEmpty()) {
            etNewsTitle.setError("Vui lòng nhập tiêu đề bài viết!");
            etNewsTitle.requestFocus();
            return;
        }

        if (detail.isEmpty()) {
            etNewsDetail.setError("Vui lòng nhập nội dung chi tiết bài viết!");
            etNewsDetail.requestFocus();
            return;
        }

        // Lấy thể loại được chọn từ Spinner
        int selectedIndex = spCategory.getSelectedItemPosition();
        Category selectedCategory = (selectedIndex >= 0 && selectedIndex < categoryList.size())
                ? categoryList.get(selectedIndex)
                : new Category("", "Chung", "", 0);

        News news = new News(code, title, detail, imageUrl, selectedCategory, "Admin", publishDate);

        Toast.makeText(this, "Đã lưu bài viết: " + news.getId(), Toast.LENGTH_SHORT).show();
        finish();
    }
}
