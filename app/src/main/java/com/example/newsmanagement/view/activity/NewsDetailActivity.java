package com.example.newsmanagement.view.activity;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newsmanagement.model.News;
import com.example.newsmanagement.R;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NEWS = "extra_news_detail";
    private static final String FONT_SIZE_PREFERENCES = "article_display_preferences";
    private static final String FONT_SIZE_LEVEL_KEY = "font_size_level";
    private static final int FONT_SIZE_STEP_SP = 3;
    private static final int MAX_FONT_SIZE_LEVEL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        News news = IntentCompat.getSerializableExtra(getIntent(), EXTRA_NEWS, News.class);
        if (news == null) {
            finish();
            return;
        }

        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvAuthor = findViewById(R.id.tvDetailAuthor);
        TextView tvContent = findViewById(R.id.tvDetailContent);

        tvCategory.setText(news.getCategoryName());
        tvDate.setText(news.getPublishDate());
        tvTitle.setText(news.getTitle());
        tvAuthor.setText(news.getAuthor());
        tvContent.setText(news.getDetail().isEmpty()
                ? "Nội dung chi tiết bài viết đang được cập nhật."
                : news.getDetail());
        applySavedFontSizes(tvCategory, tvDate, tvTitle, tvAuthor, tvContent);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnMore).setOnClickListener(v -> showArticleOptions(
                v, tvCategory, tvDate, tvTitle, tvAuthor, tvContent));
    }

    private void applySavedFontSizes(
            TextView tvCategory,
            TextView tvDate,
            TextView tvTitle,
            TextView tvAuthor,
            TextView tvContent) {
        int level = getSharedPreferences(FONT_SIZE_PREFERENCES, MODE_PRIVATE)
                .getInt(FONT_SIZE_LEVEL_KEY, 0);
        level = Math.max(0, Math.min(level, MAX_FONT_SIZE_LEVEL));
        applyFontSizes(level, tvCategory, tvDate, tvTitle, tvAuthor, tvContent);
    }

    private void applyFontSizes(
            int level,
            TextView tvCategory,
            TextView tvDate,
            TextView tvTitle,
            TextView tvAuthor,
            TextView tvContent) {
        tvCategory.setTextSize(17 + level * FONT_SIZE_STEP_SP);
        tvDate.setTextSize(14 + level * FONT_SIZE_STEP_SP);
        tvTitle.setTextSize(20 + level * FONT_SIZE_STEP_SP);
        tvAuthor.setTextSize(14 + level * FONT_SIZE_STEP_SP);
        tvContent.setTextSize(16 + level * FONT_SIZE_STEP_SP);
    }

    private void showArticleOptions(
            View anchor,
            TextView tvCategory,
            TextView tvDate,
            TextView tvTitle,
            TextView tvAuthor,
            TextView tvContent) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_article_options, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                getResources().getDimensionPixelSize(R.dimen.article_options_width),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(8);

        popupView.findViewById(R.id.optionDisplay).setOnClickListener(v -> {
            popupWindow.dismiss();
            showFontSizeDialog(tvCategory, tvDate, tvTitle, tvAuthor, tvContent);
        });
        popupView.measure(
                View.MeasureSpec.makeMeasureSpec(popupWindow.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        popupWindow.showAsDropDown(anchor, -popupWindow.getWidth() + anchor.getWidth(),
                -anchor.getHeight() - popupView.getMeasuredHeight()
                        - getResources().getDimensionPixelSize(R.dimen.article_options_offset));
    }

    private void showFontSizeDialog(
            TextView tvCategory,
            TextView tvDate,
            TextView tvTitle,
            TextView tvAuthor,
            TextView tvContent) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_font_size, null);
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(dialogView);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.55f);
            dialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        SeekBar seekBar = dialogView.findViewById(R.id.fontSizeSeekBar);
        TextView btnClose = dialogView.findViewById(R.id.btnCloseFontSize);
        TextView btnSmall = dialogView.findViewById(R.id.btnSmallFont);
        TextView btnLarge = dialogView.findViewById(R.id.btnLargeFont);

        int savedLevel = getSharedPreferences(FONT_SIZE_PREFERENCES, MODE_PRIVATE)
                .getInt(FONT_SIZE_LEVEL_KEY, 0);
        savedLevel = Math.max(0, Math.min(savedLevel, MAX_FONT_SIZE_LEVEL));
        seekBar.setProgress(savedLevel);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                int level = Math.min(progress, MAX_FONT_SIZE_LEVEL);
                applyFontSizes(level, tvCategory, tvDate, tvTitle, tvAuthor, tvContent);
                getSharedPreferences(FONT_SIZE_PREFERENCES, MODE_PRIVATE)
                        .edit()
                        .putInt(FONT_SIZE_LEVEL_KEY, level)
                        .apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });
        btnSmall.setOnClickListener(v -> {
            seekBar.setProgress(0);
        });
        btnLarge.setOnClickListener(v -> {
            seekBar.setProgress(MAX_FONT_SIZE_LEVEL);
        });
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    getResources().getDimensionPixelSize(R.dimen.font_size_dialog_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
