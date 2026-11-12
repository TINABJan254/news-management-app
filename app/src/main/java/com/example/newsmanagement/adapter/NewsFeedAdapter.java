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
import java.util.Locale;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder> {

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    private final Context context;
    private final OnNewsClickListener listener;
    private final List<News> allNews;
    private List<News> visibleNews;
    private String selectedCategory = "";

    public NewsFeedAdapter(Context context, List<News> newsList, OnNewsClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.allNews = new ArrayList<>(newsList);
        this.visibleNews = new ArrayList<>(newsList);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_feed, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = visibleNews.get(position);
        holder.tvCategory.setText(news.getCategoryName());
        holder.tvTime.setText(news.getPublishDate());
        holder.tvTitle.setText(news.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNewsClick(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visibleNews.size();
    }

    public void filter(String query) {
        String normalizedQuery = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        applyFilters(normalizedQuery);
    }

    public void filterByCategory(String category) {
        selectedCategory = category == null || category.equalsIgnoreCase("Mới nhất")
                ? "" : normalizeCategory(category);
        applyFilters("");
    }

    private String normalizeCategory(String category) {
        return category.equalsIgnoreCase("Bóng đá")
                || category.equalsIgnoreCase("Bóng đá / Thể thao")
                ? "Thể thao" : category;
    }

    private void applyFilters(String query) {
        visibleNews = new ArrayList<>();
        for (News news : allNews) {
            boolean matchesQuery = query.isEmpty()
                    || news.getTitle().toLowerCase(Locale.ROOT).contains(query);
            boolean matchesCategory = selectedCategory.isEmpty()
                    || news.getCategoryName().equalsIgnoreCase(selectedCategory);
            if (matchesQuery && matchesCategory) {
                visibleNews.add(news);
            }
        }
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivThumbnail;
        final TextView tvCategory;
        final TextView tvTime;
        final TextView tvTitle;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivNewsThumbnail);
            tvCategory = itemView.findViewById(R.id.tvNewsCategory);
            tvTime = itemView.findViewById(R.id.tvNewsTime);
            tvTitle = itemView.findViewById(R.id.tvNewsTitle);
        }
    }
}
