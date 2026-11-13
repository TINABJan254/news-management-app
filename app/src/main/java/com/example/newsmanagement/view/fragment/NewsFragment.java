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
import com.example.newsmanagement.data.CategoryDatabaseHelper;
import com.example.newsmanagement.data.NewsDatabaseHelper;
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
    private LinearLayout categoryChipContainer;
    private RecyclerView newsRecyclerView;
    private NewsDatabaseHelper newsDatabaseHelper;
    private CategoryDatabaseHelper categoryDatabaseHelper;
    private List<String> loadedCategoryNames = new ArrayList<>();

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

    @Override
    public void onResume() {
        super.onResume();
        if (categoryDatabaseHelper != null) {
            List<Category> categories = categoryDatabaseHelper.getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
            if (!categoryNames.equals(loadedCategoryNames)) {
                setupCategoryChips(categories);
                loadedCategoryNames = categoryNames;
            }
        }
    }

    private void setupViews() {
        etSearch = rootView.findViewById(R.id.etSearch);
        categoryChipContainer = rootView.findViewById(R.id.categoryChipContainer);
        newsRecyclerView = rootView.findViewById(R.id.rvNews);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setupData() {
        categoryDatabaseHelper = new CategoryDatabaseHelper(getActivity());
        newsDatabaseHelper = new NewsDatabaseHelper(getActivity());
        setupCategoryChips(categoryDatabaseHelper.getAllCategories());
        List<News> newsList = newsDatabaseHelper.getAllNews();

        newsAdapter = new NewsFeedAdapter(getActivity(), newsList, this::openNews);
        newsRecyclerView.setAdapter(newsAdapter);
        newsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView,
                                   int dx, int dy) {
                if (dy <= 0 || !newsAdapter.hasMoreItems()) {
                    return;
                }
                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null
                        && layoutManager.findLastVisibleItemPosition()
                        >= newsAdapter.getItemCount() - 3) {
                    newsAdapter.loadNextPage();
                }
            }
        });
    }

    private void setupEvents() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newsAdapter.filter(s.toString());
                newsRecyclerView.scrollToPosition(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ((ImageView) rootView.findViewById(R.id.btnCategoryMenu)).setOnClickListener(
                v -> showCategorySheet());
    }

    private void setupCategoryChips(List<Category> categories) {
        categoryChipContainer.removeAllViews();
        TextView latestChip = createCategoryChip("Mới nhất", true);
        categoryChipContainer.addView(latestChip);
        selectedChip = latestChip;

        for (Category category : categories) {
            categoryChipContainer.addView(createCategoryChip(category.getName(), false));
        }

        loadedCategoryNames = new ArrayList<>();
        for (Category category : categories) {
            loadedCategoryNames.add(category.getName());
        }
    }

    private TextView createCategoryChip(String name, boolean selected) {
        TextView chip = new TextView(getActivity());
        chip.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(30)));
        chip.setGravity(android.view.Gravity.CENTER);
        chip.setPadding(dp(13), 0, dp(13), 0);
        chip.setText(name);
        chip.setTextSize(11);
        chip.setTextColor(ContextCompat.getColor(getActivity(),
                selected ? R.color.white : R.color.text_secondary));
        chip.setBackgroundResource(selected ? R.drawable.bg_selected : R.drawable.bg_chip_unselected);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chip.getLayoutParams();
        params.setMarginEnd(dp(16));
        chip.setLayoutParams(params);
        chip.setOnClickListener(v -> selectCategoryChip((TextView) v));
        return chip;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void selectCategoryChip(TextView chip) {
        selectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
        selectedChip.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_secondary));
        chip.setBackgroundResource(R.drawable.bg_selected);
        chip.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        selectedChip = chip;
        newsAdapter.filterByCategory(chip.getText().toString());
        newsRecyclerView.scrollToPosition(0);
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
        addCategoryOption(container, dialog, "Mới nhất");
        for (Category category : categoryDatabaseHelper.getAllCategories()) {
            addCategoryOption(container, dialog, category.getName());
        }
        dialog.show();
    }

    private void addCategoryOption(LinearLayout container, BottomSheetDialog dialog, String category) {
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

    private boolean categoryMatchesSelection(String category) {
        return category.equalsIgnoreCase(selectedChip.getText().toString());
    }

    private void selectCategory(String category) {
        for (int i = 0; i < categoryChipContainer.getChildCount(); i++) {
            TextView chip = (TextView) categoryChipContainer.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(category)) {
                selectCategoryChip(chip);
                break;
            }
        }
    }

    private void openNews(News news) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS, news);
        startActivity(intent);
    }
}
