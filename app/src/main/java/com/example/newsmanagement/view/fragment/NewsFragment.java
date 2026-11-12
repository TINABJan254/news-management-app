package com.example.newsmanagement.view.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.R;
import com.example.newsmanagement.view.activity.NewsDetailActivity;
import com.example.newsmanagement.view.adapter.NewsFeedAdapter;
import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private EditText etSearch;
    private NewsFeedAdapter newsAdapter;
    private TextView selectedChip;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_news_feed, container, false);
        rootView.findViewById(R.id.bottomNavigation).setVisibility(View.GONE);
        setupViews();
        setupData();
        setupEvents();
        return rootView;
    }

    private void setupViews() {
        etSearch = rootView.findViewById(R.id.etSearch);
        selectedChip = rootView.findViewById(R.id.chipLatest);
        RecyclerView recyclerView = rootView.findViewById(R.id.rvNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        newsList.add(new News("TT-1081", "Bắt giam người lái xe gây tai nạn rồi bỏ trốn khỏi hiện trường",
                "", "", world, "Admin", "1 giờ trước"));
        newsList.add(new News("TT-1080", "Mưa lớn làm nhiều cây trong các tuyến phố ở Hà Nội bị đổ",
                "", "", social, "Admin", "2 giờ trước"));
        newsList.add(new News("TT-1079", "Sản phụ bị đá đè đa chấn thương đã tử vong",
                "", "", social, "Admin", "2 giờ trước"));
        newsList.add(new News("TT-1078", "Hơn 1.500 người tham dự màn đồng diễn yoga lớn nhất tại Thái",
                "", "", life, "Admin", "3 giờ trước"));
        newsList.add(new News("TT-1077", "Đội tuyển quốc gia hoàn thiện chiến thuật trước giải vô địch",
                "", "", sports, "Admin", "4 giờ trước"));

        newsAdapter = new NewsFeedAdapter(getActivity(), newsList, this::openNews);
        ((RecyclerView) rootView.findViewById(R.id.rvNews)).setAdapter(newsAdapter);
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

        int[] chipIds = {R.id.chipLatest, R.id.chipSports, R.id.chipSocial,
                R.id.chipWorld, R.id.chipLife};
        View.OnClickListener chipListener = view -> {
            TextView chip = (TextView) view;
            selectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
            selectedChip.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_secondary));
            chip.setBackgroundResource(R.drawable.bg_selected);
            chip.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            selectedChip = chip;
            newsAdapter.filterByCategory(chip.getText().toString());
        };
        for (int chipId : chipIds) {
            rootView.findViewById(chipId).setOnClickListener(chipListener);
        }
        ((ImageView) rootView.findViewById(R.id.btnCategoryMenu)).setOnClickListener(
                v -> showCategorySheet());
    }

    private void showCategorySheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_news_categories);
        View sheet = dialog.findViewById(R.id.categorySheet);
        if (sheet != null) {
            sheet.setBackgroundResource(R.drawable.bg_bottom_sheet);
        }
        LinearLayout container = dialog.findViewById(R.id.categoryOptions);
        if (container == null) {
            dialog.show();
            return;
        }
        String[] categories = {"Mới nhất", "Bóng đá / Thể thao", "Xã hội", "Thế giới",
                "Kinh doanh", "Bất động sản", "Sức khỏe", "Ô tô - Xe máy",
                "Pháp luật", "Công nghệ"};
        for (String category : categories) {
            TextView option = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.item_news_category_option, container, false);
            option.setText(category);
            option.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, categoryMatchesSelection(category) ? R.drawable.ic_check : 0, 0);
            option.setOnClickListener(v -> {
                selectCategory(category);
                dialog.dismiss();
            });
            container.addView(option);
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
        int[] chipIds = {R.id.chipLatest, R.id.chipSports, R.id.chipSocial,
                R.id.chipWorld, R.id.chipLife};
        for (int chipId : chipIds) {
            TextView chip = rootView.findViewById(chipId);
            if (chip.getText().toString().equals(chipName)) {
                selectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
                selectedChip.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_secondary));
                chip.setBackgroundResource(R.drawable.bg_selected);
                chip.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                selectedChip = chip;
                break;
            }
        }
        newsAdapter.filterByCategory(category);
    }

    private void openNews(News news) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS, news);
        startActivity(intent);
    }
}
