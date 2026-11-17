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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newsmanagement.data.CategoryDatabaseHelper;
import com.example.newsmanagement.data.NewsDatabaseHelper;
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

    public static final String EXTRA_NEWS = "extra_new_news";

    private ImageView btnBack;
    private View layoutSelectDate;
    private TextView tvNewsPublishDate;
    private Spinner spCategory;
    private EditText etNewsTitle;
    private EditText etNewsAuthor;
    private EditText etNewsImageUrl;
    private ImageView ivNewsPreview;
    private EditText etNewsDetail;
    private TextView btnCancel;
    private TextView btnSaveNews;

    private List<Category> categoryList;
    private Calendar selectedCalendar;
    private CategoryDatabaseHelper categoryDatabaseHelper;
    private NewsDatabaseHelper newsDatabaseHelper;

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
        layoutSelectDate = findViewById(R.id.layoutSelectDate);
        tvNewsPublishDate = findViewById(R.id.tvNewsPublishDate);
        spCategory = findViewById(R.id.spCategory);
        etNewsTitle = findViewById(R.id.etNewsTitle);
        etNewsAuthor = findViewById(R.id.etNewsAuthor);
        etNewsImageUrl = findViewById(R.id.etNewsImageUrl);
        ivNewsPreview = findViewById(R.id.ivNewsPreview);
        etNewsDetail = findViewById(R.id.etNewsDetail);
        btnCancel = findViewById(R.id.btnCancel);
        btnSaveNews = findViewById(R.id.btnSaveNews);
    }

    private void setupData() {
        selectedCalendar = Calendar.getInstance();
        categoryDatabaseHelper = new CategoryDatabaseHelper(this);
        newsDatabaseHelper = new NewsDatabaseHelper(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvNewsPublishDate.setText(dateFormat.format(new Date()));

        // Nạp danh sách thể loại vào Spinner
        categoryList = categoryDatabaseHelper.getAllCategories();

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
        String code = newsDatabaseHelper.getNextNewsId();
        String title = etNewsTitle.getText().toString().trim();
        String author = etNewsAuthor.getText().toString().trim();
        String detail = etNewsDetail.getText().toString().trim();
        String imageUrl = etNewsImageUrl.getText().toString().trim();
        String publishDate = tvNewsPublishDate.getText().toString().trim();

        if (title.isEmpty()) {
            etNewsTitle.setError("Vui lòng nhập tiêu đề bài viết!");
            etNewsTitle.requestFocus();
            return;
        }

        if (author.isEmpty()) {
            etNewsAuthor.setError("Vui lòng nhập tác giả bài viết!");
            etNewsAuthor.requestFocus();
            return;
        }

        if (detail.isEmpty()) {
            etNewsDetail.setError("Vui lòng nhập nội dung chi tiết bài viết!");
            etNewsDetail.requestFocus();
            return;
        }

        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm thể loại trước khi tạo tin tức.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thể loại được chọn từ Spinner
        int selectedIndex = spCategory.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= categoryList.size()) {
            Toast.makeText(this, "Vui lòng chọn thể loại.", Toast.LENGTH_SHORT).show();
            return;
        }
        Category selectedCategory = categoryList.get(selectedIndex);

        News news = new News(code, title, detail, imageUrl, selectedCategory, author, publishDate);

        newsDatabaseHelper.insertNews(news);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_NEWS, news);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
