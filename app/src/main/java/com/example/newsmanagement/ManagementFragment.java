package com.example.newsmanagement;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

public class ManagementFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_management, container, false);
        view.findViewById(R.id.bottomNavigation).setVisibility(View.GONE);
        MaterialCardView manageNews = view.findViewById(R.id.cardManageNews);
        MaterialCardView manageCategories = view.findViewById(R.id.cardManageCategories);
        manageNews.setOnClickListener(v -> startActivity(
                new Intent(getActivity(), NewsManagementActivity.class)));
        manageCategories.setOnClickListener(v -> startActivity(
                new Intent(getActivity(), CategoryManagementActivity.class)));
        return view;
    }
}
