package com.example.newsmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.R;
import com.example.newsmanagement.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public interface OnNewsActionListener {
        void onEdit(News news);
        void onDelete(News news);
        void onItemClick(News news);
    }

    private final Context context;
    private List<News> newsList;
    private List<News> newsListFull;
    private final OnNewsActionListener listener;

    public NewsAdapter(Context context, List<News> newsList, OnNewsActionListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.newsListFull = new ArrayList<>(newsList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        holder.tvNewsCode.setText(news.getId());
        holder.tvNewsCategory.setText(news.getCategoryName());
        holder.tvNewsDate.setText(news.getPublishDate());
        holder.tvNewsTitle.setText(news.getTitle());

        // Click xem chi tiết bài viết
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(news);
            }
        });

        // Click nút Sửa
        holder.btnEditNews.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(news);
            }
        });

        // Click nút Xóa
        holder.btnDeleteNews.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public void updateData(List<News> newList) {
        this.newsList = newList;
        this.newsListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // Tìm kiếm theo từ khóa (tiêu đề hoặc mã tin)
    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            newsList = new ArrayList<>(newsListFull);
        } else {
            String pattern = query.toLowerCase().trim();
            List<News> filtered = new ArrayList<>();
            for (News item : newsListFull) {
                if (item.getTitle().toLowerCase().contains(pattern) ||
                    item.getId().toLowerCase().contains(pattern)) {
                    filtered.add(item);
                }
            }
            newsList = filtered;
        }
        notifyDataSetChanged();
    }

    // Lọc theo thể loại
    public void filterByCategory(String categoryName) {
        if (categoryName == null || categoryName.equalsIgnoreCase("Tất cả")) {
            newsList = new ArrayList<>(newsListFull);
        } else {
            List<News> filtered = new ArrayList<>();
            for (News item : newsListFull) {
                if (item.getCategoryName().equalsIgnoreCase(categoryName)) {
                    filtered.add(item);
                }
            }
            newsList = filtered;
        }
        notifyDataSetChanged();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNewsThumbnail;
        TextView tvNewsCode;
        TextView tvNewsCategory;
        TextView tvNewsDate;
        TextView tvNewsTitle;
        View btnEditNews;
        View btnDeleteNews;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNewsThumbnail = itemView.findViewById(R.id.ivNewsThumbnail);
            tvNewsCode = itemView.findViewById(R.id.tvNewsCode);
            tvNewsCategory = itemView.findViewById(R.id.tvNewsCategory);
            tvNewsDate = itemView.findViewById(R.id.tvNewsDate);
            tvNewsTitle = itemView.findViewById(R.id.tvNewsTitle);
            btnEditNews = itemView.findViewById(R.id.btnEditNews);
            btnDeleteNews = itemView.findViewById(R.id.btnDeleteNews);
        }
    }
}
