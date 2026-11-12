package com.example.newsmanagement;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.example.newsmanagement.adapter.NewsAdapter;
import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewsManagementActivity extends AppCompatActivity {

    private ImageView btnBack;
    private View btnAddNewNews;
    private EditText etSearchNews;
    private RecyclerView rvNews;
    private NewsAdapter newsAdapter;
    private List<News> newsList;

    // Filter Chips
    private TextView chipAll, chipSports, chipSocial, chipVehicles, chipLaw, chipWorld;
    private TextView currentSelectedChip;

    // Date Range Filter
    private View layoutDateRangePicker;
    private TextView tvStartDate, tvEndDate;
    private View btnApplyDateFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_management);

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
        btnAddNewNews = findViewById(R.id.btnAddNewNews);
        etSearchNews = findViewById(R.id.etSearchNews);
        rvNews = findViewById(R.id.rvNews);

        chipAll = findViewById(R.id.chipAll);
        chipSports = findViewById(R.id.chipSports);
        chipSocial = findViewById(R.id.chipSocial);
        chipVehicles = findViewById(R.id.chipVehicles);
        chipLaw = findViewById(R.id.chipLaw);
        chipWorld = findViewById(R.id.chipWorld);
        currentSelectedChip = chipAll;

        layoutDateRangePicker = findViewById(R.id.layoutDateRangePicker);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnApplyDateFilter = findViewById(R.id.btnApplyDateFilter);

        rvNews.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupData() {
        newsList = new ArrayList<>();
        Category catVehicles = new Category("TL04", "Ô tô - Xe máy", "", 0);
        Category catSports = new Category("TL01", "Thể thao", "", 0);
        Category catSocial = new Category("TL02", "Xã hội", "", 0);
        Category catLaw = new Category("TL03", "Pháp luật", "", 0);

        newsList.add(new News("TT-1082", "Mẫu B-SUV Nhật Bản này được giảm hơn 200 triệu đồng cạnh tranh phân khúc", "", "", catVehicles, "Admin", "12/11/2026"));
        newsList.add(new News("TT-1081", "Đội tuyển quốc gia rốt ráo hoàn thiện chiến thuật trước thềm giải vô địch kh...", "", "", catSports, "Admin", "11/11/2026"));
        newsList.add(new News("TT-1080", "Tuyến tàu điện ngầm thứ hai chính thức vận hành thử nghiệm toàn tuyến an...", "", "", catSocial, "Admin", "10/11/2026"));
        newsList.add(new News("TT-1079", "Nghị định mới về bảo vệ dữ liệu người tiêu dùng trong giao dịch số có hiệu lực", "", "", catLaw, "Admin", "09/11/2026"));

        newsAdapter = new NewsAdapter(this, newsList, new NewsAdapter.OnNewsActionListener() {
            @Override
            public void onEdit(News news) {
                Toast.makeText(NewsManagementActivity.this, "Sửa bài viết: " + news.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(News news) {
                showDeleteConfirmDialog(news);
            }

            @Override
            public void onItemClick(News news) {
                Toast.makeText(NewsManagementActivity.this, "Xem chi tiết: " + news.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        rvNews.setAdapter(newsAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnAddNewNews.setOnClickListener(v -> Toast.makeText(this, "Mở form thêm mới tin tức", Toast.LENGTH_SHORT).show());

        // Tìm kiếm theo từ khóa
        etSearchNews.addTextChangedListener(new TextWatcher() {
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

        // Bắt sự kiện lọc theo thể loại
        View.OnClickListener chipClickListener = v -> {
            TextView clickedChip = (TextView) v;
            if (currentSelectedChip != null) {
                currentSelectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
                currentSelectedChip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }
            clickedChip.setBackgroundResource(R.drawable.bg_selected);
            clickedChip.setTextColor(Color.WHITE);
            currentSelectedChip = clickedChip;
            newsAdapter.filterByCategory(clickedChip.getText().toString());
        };

        chipAll.setOnClickListener(chipClickListener);
        chipSports.setOnClickListener(chipClickListener);
        chipSocial.setOnClickListener(chipClickListener);
        chipVehicles.setOnClickListener(chipClickListener);
        chipLaw.setOnClickListener(chipClickListener);
        chipWorld.setOnClickListener(chipClickListener);

        // Lọc theo ngày
        layoutDateRangePicker.setOnClickListener(v -> showDatePicker());
        btnApplyDateFilter.setOnClickListener(v -> {
            String start = tvStartDate.getText().toString();
            String end = tvEndDate.getText().toString();
            Toast.makeText(this, "Lọc tin từ " + start + " đến " + end, Toast.LENGTH_SHORT).show();
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = 2026;
        int month = 10;
        int day = 1;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String dateFormatted = String.format("%02d/%02d/%04d", m + 1, d, y);
            tvStartDate.setText(dateFormatted);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showDeleteConfirmDialog(News news) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null));

        TextView tvDeleteMessage = dialog.findViewById(R.id.tvDeleteMessage);
        View btnCancelDelete = dialog.findViewById(R.id.btnCancelDelete);
        View btnConfirmDelete = dialog.findViewById(R.id.btnConfirmDelete);

        tvDeleteMessage.setText("Bạn có chắc chắn muốn xóa bài viết \"" + news.getTitle() + "\" (" + news.getId() + ") không?");
        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());

        btnConfirmDelete.setOnClickListener(v -> {
            newsList.remove(news);
            newsAdapter.updateData(newsList);
            Toast.makeText(this, "Đã xóa bài viết: " + news.getId(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        formatDialogWindow(dialog);
        dialog.show();
    }

    private void formatDialogWindow(Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
