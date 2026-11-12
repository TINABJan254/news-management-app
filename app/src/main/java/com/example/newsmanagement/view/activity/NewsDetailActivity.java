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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
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

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnMore).setOnClickListener(v -> showArticleOptions(v, tvContent));
    }

    private void showArticleOptions(View anchor, TextView tvContent) {
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
            showFontSizeDialog(tvContent);
        });
        popupWindow.showAsDropDown(anchor, -popupWindow.getWidth() + anchor.getWidth(),
                -anchor.getHeight() - getResources().getDimensionPixelSize(R.dimen.article_options_offset));
    }

    private void showFontSizeDialog(TextView tvContent) {
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

        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                tvContent.setTextSize(12 + progress * 2);
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
            tvContent.setTextSize(12);
        });
        btnLarge.setOnClickListener(v -> {
            seekBar.setProgress(2);
            tvContent.setTextSize(16);
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
