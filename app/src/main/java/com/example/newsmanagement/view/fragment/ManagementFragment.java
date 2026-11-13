package com.example.newsmanagement.view.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.newsmanagement.R;
import com.example.newsmanagement.data.CategoryDatabaseHelper;
import com.example.newsmanagement.data.NewsDatabaseHelper;
import com.example.newsmanagement.view.activity.CategoryManagementActivity;
import com.example.newsmanagement.view.activity.NewsManagementActivity;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;

public class ManagementFragment extends Fragment {

    private CategoryDatabaseHelper categoryDatabaseHelper;
    private NewsDatabaseHelper newsDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_management, container, false);
        categoryDatabaseHelper = new CategoryDatabaseHelper(getActivity());
        newsDatabaseHelper = new NewsDatabaseHelper(getActivity());
        view.findViewById(R.id.bottomNavigation).setVisibility(View.GONE);
        MaterialCardView manageNews = view.findViewById(R.id.cardManageNews);
        MaterialCardView manageCategories = view.findViewById(R.id.cardManageCategories);
        manageNews.setOnClickListener(v -> startActivity(
                new Intent(getActivity(), NewsManagementActivity.class)));
        manageCategories.setOnClickListener(v -> startActivity(
                new Intent(getActivity(), CategoryManagementActivity.class)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCounts();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshCounts();
        }
    }

    private void refreshCounts() {
        View view = getView();
        if (view == null) {
            return;
        }
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        TextView tvTotalArticles = view.findViewById(R.id.tvTotalArticles);
        TextView tvTotalCategories = view.findViewById(R.id.tvTotalCategories);
        tvTotalArticles.setText(numberFormat.format(newsDatabaseHelper.getNewsCount()));
        tvTotalCategories.setText(numberFormat.format(categoryDatabaseHelper.getCategoryCount()));
    }

    @Override
    public void onDestroyView() {
        newsDatabaseHelper.close();
        categoryDatabaseHelper.close();
        super.onDestroyView();
    }
}
