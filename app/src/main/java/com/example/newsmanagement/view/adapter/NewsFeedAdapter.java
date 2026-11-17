package com.example.newsmanagement.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsmanagement.R;
import com.example.newsmanagement.data.CategoryDatabaseHelper;
import com.example.newsmanagement.data.NewsDatabaseHelper;
import com.example.newsmanagement.model.News;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder> {

    public static final int PAGE_SIZE = 10;

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    private final Context context;
    private final OnNewsClickListener listener;
    private final NewsDatabaseHelper newsDatabaseHelper;
    private final CategoryDatabaseHelper categoryDatabaseHelper;
    private final ExecutorService queryExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicLong latestRequest = new AtomicLong();
    private List<News> visibleNews = new ArrayList<>();
    private String selectedCategory = "";
    private String searchQuery = "";
    private int currentPage = 1;
    private int totalCount;

    public NewsFeedAdapter(
            Context context,
            NewsDatabaseHelper newsDatabaseHelper,
            CategoryDatabaseHelper categoryDatabaseHelper,
            OnNewsClickListener listener) {
        this.context = context;
        this.newsDatabaseHelper = newsDatabaseHelper;
        this.categoryDatabaseHelper = categoryDatabaseHelper;
        this.listener = listener;
        loadPage(1);
    }

    public NewsFeedAdapter(
            Context context,
            List<News> newsList,
            OnNewsClickListener listener) {
        this.context = context;
        this.newsDatabaseHelper = null;
        this.categoryDatabaseHelper = null;
        this.listener = listener;
        this.visibleNews = new ArrayList<>(newsList);
        this.totalCount = visibleNews.size();
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
        searchQuery = query == null ? "" : query.trim();
        loadPage(1);
    }

    public void filterByCategory(String category) {
        selectedCategory = category == null || category.equalsIgnoreCase("Mới nhất")
                ? "" : category.trim();
        loadPage(1);
    }

    public boolean hasMoreItems() {
        return currentPage < getPageCount();
    }

    public void loadNextPage() {
        if (hasMoreItems()) {
            loadPage(currentPage + 1);
        }
    }

    public void refresh() {
        loadPage(currentPage);
    }

    private int getPageCount() {
        return (totalCount + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    private void loadPage(int page) {
        int requestedPage = Math.max(1, page);
        long requestId = latestRequest.incrementAndGet();
        queryExecutor.execute(() -> {
            String categoryId = categoryDatabaseHelper.getCategoryIdByName(selectedCategory);
            int count = newsDatabaseHelper.getNewsCount(
                    searchQuery, categoryId, "", "");
            List<News> pageData = newsDatabaseHelper.getNewsPage(
                    searchQuery, categoryId, "", "", requestedPage, PAGE_SIZE);
            mainHandler.post(() -> {
                if (requestId != latestRequest.get()) {
                    return;
                }
                int previousPage = currentPage;
                totalCount = count;
                currentPage = count == 0
                        ? 1
                        : Math.min(requestedPage, getPageCount());
                if (currentPage != requestedPage) {
                    loadPage(currentPage);
                    return;
                }
                if (requestedPage > 1 && previousPage == requestedPage - 1) {
                    int oldItemCount = visibleNews.size();
                    visibleNews = new ArrayList<>(visibleNews);
                    visibleNews.addAll(pageData);
                    notifyItemRangeInserted(oldItemCount, pageData.size());
                } else {
                    visibleNews = pageData;
                    notifyDataSetChanged();
                }
            });
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        queryExecutor.shutdownNow();
        super.onDetachedFromRecyclerView(recyclerView);
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
