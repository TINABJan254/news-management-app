package com.example.newsmanagement.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;
import com.example.newsmanagement.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditNewsActivity extends AppCompatActivity {

    public static final String EXTRA_NEWS = "extra_news";
    private static final String STATE_SELECTED_DATE = "selected_date";

    private ImageView btnBack;
    private EditText etNewsCode;
    private View layoutSelectDate;
    private TextView tvNewsPublishDate;
    private Spinner spCategory;
    private EditText etNewsTitle;
    private EditText etNewsImageUrl;
    private EditText etNewsDetail;
    private TextView btnCancel;
    private TextView btnUpdateNews;

    private News news;
    private List<Category> categoryList;
    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_news);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,
                    Math.max(systemBars.bottom, keyboard.bottom));
            return insets;
        });

        initViews();
        news = IntentCompat.getSerializableExtra(getIntent(), EXTRA_NEWS, News.class);
        if (news == null || news.getId() == null || news.getId().trim().isEmpty()) {
            Toast.makeText(this, "Không tìm thấy bài viết cần chỉnh sửa.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupData(savedInstanceState);
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
        etNewsDetail = findViewById(R.id.etNewsDetail);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdateNews = findViewById(R.id.btnUpdateNews);

    }

    private void setupData(Bundle savedInstanceState) {
        selectedCalendar = Calendar.getInstance();

        // Nạp danh sách thể loại vào Spinner
        categoryList = new ArrayList<>();
        categoryList.add(new Category("TL01", "Thể thao", "", 0));
        categoryList.add(new Category("TL02", "Xã hội", "", 0));
        categoryList.add(new Category("TL03", "Pháp luật", "", 0));
        categoryList.add(new Category("TL04", "Ô tô - Xe máy", "", 0));
        categoryList.add(new Category("TL05", "Thế giới", "", 0));

        // Giữ lại thể loại hiện tại nếu bài viết thuộc danh mục ngoài dữ liệu mẫu.
        int selectedCategoryIndex = 0;
        if (news.getCategory() != null) {
            selectedCategoryIndex = -1;
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId().equalsIgnoreCase(news.getCategoryId())) {
                    categoryList.set(i, news.getCategory());
                    selectedCategoryIndex = i;
                    break;
                }
            }
            if (selectedCategoryIndex == -1) {
                categoryList.add(news.getCategory());
                selectedCategoryIndex = categoryList.size() - 1;
            }
        }

        List<String> categoryNames = new ArrayList<>();
        for (Category cat : categoryList) {
            categoryNames.add(cat.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_news_category_spinner,
                categoryNames
        );
        spinnerAdapter.setDropDownViewResource(R.layout.item_news_category_spinner);
        spCategory.setAdapter(spinnerAdapter);
        spCategory.setSelection(selectedCategoryIndex);

        // Fill dữ liệu sẵn vào các trường
        etNewsCode.setText(news.getId());
        etNewsTitle.setText(news.getTitle());
        etNewsDetail.setText(news.getDetail());
        etNewsImageUrl.setText(news.getImageUrl());

        if (news.getPublishDate() != null && !news.getPublishDate().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false);
                Date parsedDate = sdf.parse(news.getPublishDate());
                if (parsedDate != null) {
                    selectedCalendar.setTime(parsedDate);
                }
            } catch (ParseException ignored) {
                // Dùng ngày hiện tại nếu dữ liệu cũ không có ngày hợp lệ.
            }
        }

        if (savedInstanceState != null) {
            selectedCalendar.setTimeInMillis(savedInstanceState.getLong(
                    STATE_SELECTED_DATE, selectedCalendar.getTimeInMillis()));
        }
        updateDateLabel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedCalendar != null) {
            outState.putLong(STATE_SELECTED_DATE, selectedCalendar.getTimeInMillis());
        }
    }

    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvNewsPublishDate.setText(dateFormat.format(selectedCalendar.getTime()));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        layoutSelectDate.setOnClickListener(v -> showDatePicker());
        btnUpdateNews.setOnClickListener(v -> handleUpdateNews());
    }

    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedCalendar.set(y, m, d);
            updateDateLabel();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void handleUpdateNews() {
        String title = etNewsTitle.getText().toString().trim();
        String detail = etNewsDetail.getText().toString().trim();
        String imageUrl = etNewsImageUrl.getText().toString().trim();
        String publishDate = tvNewsPublishDate.getText().toString().trim();

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

        int selectedIndex = spCategory.getSelectedItemPosition();
        Category selectedCategory = (selectedIndex >= 0 && selectedIndex < categoryList.size())
                ? categoryList.get(selectedIndex)
                : new Category("", "Chung", "", 0);

        news.setTitle(title);
        news.setDetail(detail);
        news.setImageUrl(imageUrl);
        news.setCategory(selectedCategory);
        news.setPublishDate(publishDate);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NEWS, news);
        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
