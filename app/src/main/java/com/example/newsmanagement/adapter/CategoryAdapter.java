package com.example.newsmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.R;
import com.example.newsmanagement.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryActionListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    private final Context context;
    private List<Category> categoryList;
    private List<Category> categoryListFull;
    private final OnCategoryActionListener listener;

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryActionListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.categoryListFull = new ArrayList<>(categoryList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryCode.setText(category.getId());
        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryDesc.setText(category.getDescription());
        holder.tvArticleCount.setText(category.getArticleCount() + " bài");

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(category);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            categoryList = new ArrayList<>(categoryListFull);
        } else {
            String filterPattern = query.toLowerCase().trim();
            List<Category> filteredList = new ArrayList<>();
            for (Category item : categoryListFull) {
                if (item.getName().toLowerCase().contains(filterPattern) ||
                    item.getId().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
            categoryList = filteredList;
        }
        notifyDataSetChanged();
    }

    public void updateData(List<Category> newList) {
        this.categoryList = newList;
        this.categoryListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryCode;
        TextView tvCategoryName;
        TextView tvCategoryDesc;
        TextView tvArticleCount;
        View btnEdit;
        View btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryCode = itemView.findViewById(R.id.tvCategoryCode);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDesc = itemView.findViewById(R.id.tvCategoryDesc);
            tvArticleCount = itemView.findViewById(R.id.tvArticleCount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
