package com.example.newsmanagement.view.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.core.os.BundleCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.R;
import com.example.newsmanagement.data.NewsDatabaseHelper;
import com.example.newsmanagement.data.CategoryDatabaseHelper;
import com.example.newsmanagement.view.adapter.NewsAdapter;
import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewsManagementActivity extends AppCompatActivity {

    private static final String STATE_NEWS_LIST = "news_list";
    private static final String STATE_CATEGORY_CHIP = "category_chip";

    private ImageView btnBack;
    private View btnAddNewNews;
    private EditText etSearchNews;
    private RecyclerView rvNews;
    private NewsAdapter newsAdapter;
    private List<News> newsList;
    private NewsDatabaseHelper newsDatabaseHelper;

    // Filter Chips
    private TextView chipAll;
    private TextView currentSelectedChip;
    private LinearLayout layoutCategoryChips;
    private CategoryDatabaseHelper categoryDatabaseHelper;

    // Date Range Filter
    private View layoutDateRangePicker;
    private TextView tvStartDate, tvEndDate;
    private View btnApplyDateFilter;
    private View btnClearDateFilter;

    private final ActivityResultLauncher<Intent> editNewsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                News updatedNews = IntentCompat.getSerializableExtra(
                        result.getData(), EditNewsActivity.EXTRA_NEWS, News.class);
                if (updatedNews == null) {
                    return;
                }
                for (int i = 0; i < newsList.size(); i++) {
                    if (newsList.get(i).getId().equals(updatedNews.getId())) {
                        newsList.set(i, updatedNews);
                        newsAdapter.updateData(newsList);
                        break;
                    }
                }
            });

    private final ActivityResultLauncher<Intent> addNewsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                News addedNews = IntentCompat.getSerializableExtra(
                        result.getData(), AddNewsActivity.EXTRA_NEWS, News.class);
                if (addedNews == null) {
                    return;
                }
                newsList.add(0, addedNews);
                newsAdapter.updateData(newsList);
            });

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
        setupData(savedInstanceState);
        setupEvents();
        if (savedInstanceState != null) {
            selectCategoryChip(savedInstanceState.getString(STATE_CATEGORY_CHIP, "Tất cả"));
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAddNewNews = findViewById(R.id.btnAddNewNews);
        etSearchNews = findViewById(R.id.etSearchNews);
        rvNews = findViewById(R.id.rvNews);

        layoutCategoryChips = findViewById(R.id.layoutCategoryChips);

        layoutDateRangePicker = findViewById(R.id.layoutDateRangePicker);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnApplyDateFilter = findViewById(R.id.btnApplyDateFilter);
        btnClearDateFilter = findViewById(R.id.btnClearDateFilter);

        rvNews.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_NEWS_LIST, new ArrayList<>(newsList));
        outState.putString(STATE_CATEGORY_CHIP, currentSelectedChip.getText().toString());
    }

    @SuppressWarnings("unchecked")
    private void setupData(Bundle savedInstanceState) {
        newsDatabaseHelper = new NewsDatabaseHelper(this);
        categoryDatabaseHelper = new CategoryDatabaseHelper(this);
        newsList = new ArrayList<>();
        setupCategoryChips();

        if (savedInstanceState != null) {
            ArrayList<News> savedNews = BundleCompat.getSerializable(
                    savedInstanceState, STATE_NEWS_LIST, ArrayList.class);
            if (savedNews != null) {
                newsList = savedNews;
            }
        } else {
            newsList = newsDatabaseHelper.getAllNews();
        }

        newsAdapter = new NewsAdapter(this, newsList, new NewsAdapter.OnNewsActionListener() {
            @Override
            public void onEdit(News news) {
                Intent intent = new Intent(NewsManagementActivity.this, EditNewsActivity.class);
                intent.putExtra(EditNewsActivity.EXTRA_NEWS, news);
                editNewsLauncher.launch(intent);
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
        btnAddNewNews.setOnClickListener(v -> {
            addNewsLauncher.launch(new Intent(this, AddNewsActivity.class));
        });

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

        for (int i = 0; i < layoutCategoryChips.getChildCount(); i++) {
            layoutCategoryChips.getChildAt(i).setOnClickListener(chipClickListener);
        }

        // Lọc theo ngày
        layoutDateRangePicker.setOnClickListener(v -> showStartDatePicker());
        btnApplyDateFilter.setOnClickListener(v -> {
            String start = tvStartDate.getText().toString();
            String end = tvEndDate.getText().toString();
            if (!isValidDateRange(start, end)) {
                Toast.makeText(this, "Khoảng thời gian không hợp lệ.", Toast.LENGTH_SHORT).show();
                return;
            }
            newsAdapter.filterByDateRange(start, end);
        });
        btnClearDateFilter.setOnClickListener(v -> {
            tvStartDate.setText("Từ ngày");
            tvEndDate.setText("Đến ngày");
            newsAdapter.filterByDateRange("", "");
        });
    }

    private void setupCategoryChips() {
        layoutCategoryChips.removeAllViews();
        chipAll = createCategoryChip("Tất cả", true);
        layoutCategoryChips.addView(chipAll);
        currentSelectedChip = chipAll;

        for (Category category : categoryDatabaseHelper.getAllCategories()) {
            layoutCategoryChips.addView(createCategoryChip(category.getName(), false));
        }
    }

    private TextView createCategoryChip(String name, boolean selected) {
        TextView chip = new TextView(this);
        chip.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chip.setText(name);
        chip.setTextSize(12.5f);
        chip.setTextColor(ContextCompat.getColor(this,
                selected ? R.color.white : R.color.text_secondary));
        chip.setBackgroundResource(selected ? R.drawable.bg_selected : R.drawable.bg_chip_unselected);
        chip.setPadding(dp(14), dp(6), dp(14), dp(6));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chip.getLayoutParams();
        params.setMarginEnd(dp(8));
        chip.setLayoutParams(params);
        chip.setClickable(true);
        chip.setFocusable(true);
        return chip;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void selectCategoryChip(String categoryName) {
        for (int i = 0; i < layoutCategoryChips.getChildCount(); i++) {
            TextView chip = (TextView) layoutCategoryChips.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(categoryName)) {
                chip.performClick();
                return;
            }
        }
        chipAll.performClick();
    }

    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String dateFormatted = String.format("%02d/%02d/%04d", d, m + 1, y);
            tvStartDate.setText(dateFormatted);
            showEndDatePicker();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            tvEndDate.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private boolean isValidDateRange(String start, String end) {
        try {
            java.text.SimpleDateFormat format =
                    new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ROOT);
            format.setLenient(false);
            return !format.parse(start).after(format.parse(end));
        } catch (java.text.ParseException exception) {
            return false;
        }
    }

    private void showDeleteConfirmDialog(News news) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_confirm);

        TextView tvDeleteMessage = dialog.findViewById(R.id.tvDeleteMessage);
        View btnCancelDelete = dialog.findViewById(R.id.btnCancelDelete);
        View btnConfirmDelete = dialog.findViewById(R.id.btnConfirmDelete);

        tvDeleteMessage.setText("Bạn có chắc chắn muốn xóa bài viết \"" + news.getTitle() + "\" (" + news.getId() + ") không?");
        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());

        btnConfirmDelete.setOnClickListener(v -> {
            if (newsDatabaseHelper.deleteNews(news) != 1) {
                Toast.makeText(this, "Không thể xóa bài viết.", Toast.LENGTH_SHORT).show();
                return;
            }
            newsList.remove(news);
            newsAdapter.updateData(newsList);
            Toast.makeText(this, "Đã xóa bài viết: " + news.getId(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
        formatDialogWindow(dialog);
    }

    private void formatDialogWindow(Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
