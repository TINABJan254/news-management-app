package com.example.newsmanagement.view.adapter;

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
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder> {

    private static final int PAGE_SIZE = 10;

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    private final Context context;
    private final OnNewsClickListener listener;
    private final List<News> allNews;
    private List<News> filteredNews;
    private List<News> visibleNews;
    private String selectedCategory = "";
    private String searchQuery = "";

    public NewsFeedAdapter(Context context, List<News> newsList, OnNewsClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.allNews = new ArrayList<>(newsList);
        this.filteredNews = new ArrayList<>(newsList);
        this.visibleNews = new ArrayList<>();
        showFirstPage();
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
        Glide.with(holder.itemView)
                .load(news.getImageUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(holder.ivThumbnail);
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
        searchQuery = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        applyFilters();
    }

    public void filterByCategory(String category) {
        selectedCategory = category == null || category.equalsIgnoreCase("Mới nhất")
                ? "" : normalizeCategory(category);
        applyFilters();
    }

    private String normalizeCategory(String category) {
        return category;
    }

    private void applyFilters() {
        filteredNews = new ArrayList<>();
        for (News news : allNews) {
            boolean matchesQuery = searchQuery.isEmpty()
                    || news.getTitle().toLowerCase(Locale.ROOT).contains(searchQuery);
            boolean matchesCategory = selectedCategory.isEmpty()
                    || news.getCategoryName().equalsIgnoreCase(selectedCategory);
            if (matchesQuery && matchesCategory) {
                filteredNews.add(news);
            }
        }
        showFirstPage();
        notifyDataSetChanged();
    }

    public boolean hasMoreItems() {
        return visibleNews.size() < filteredNews.size();
    }

    public void loadNextPage() {
        if (!hasMoreItems()) {
            return;
        }
        int oldSize = visibleNews.size();
        int newSize = Math.min(oldSize + PAGE_SIZE, filteredNews.size());
        visibleNews.addAll(filteredNews.subList(oldSize, newSize));
        notifyItemRangeInserted(oldSize, newSize - oldSize);
    }

    private void showFirstPage() {
        int visibleItemCount = Math.min(PAGE_SIZE, filteredNews.size());
        visibleNews = new ArrayList<>(filteredNews.subList(0, visibleItemCount));
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
