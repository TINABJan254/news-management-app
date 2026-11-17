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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public static final int PAGE_SIZE = 10;

    public interface OnNewsActionListener {
        void onEdit(News news);
        void onDelete(News news);
        void onItemClick(News news);
    }

    private final Context context;
    private final NewsDatabaseHelper newsDatabaseHelper;
    private final CategoryDatabaseHelper categoryDatabaseHelper;
    private final OnNewsActionListener listener;
    private final ExecutorService queryExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicLong latestRequest = new AtomicLong();
    private List<News> newsList = new ArrayList<>();
    private String searchQuery = "";
    private String selectedCategory = "";
    private String startDate = "";
    private String endDate = "";
    private int currentPage = 1;
    private int totalCount;
    private Runnable onDataLoadedListener;

    public NewsAdapter(
            Context context,
            NewsDatabaseHelper newsDatabaseHelper,
            CategoryDatabaseHelper categoryDatabaseHelper,
            OnNewsActionListener listener) {
        this.context = context;
        this.newsDatabaseHelper = newsDatabaseHelper;
        this.categoryDatabaseHelper = categoryDatabaseHelper;
        this.listener = listener;
        loadPage(1);
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
        Glide.with(holder.itemView)
                .load(news.getImageUrl())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(holder.ivNewsThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(news);
            }
        });
        holder.btnEditNews.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(news);
            }
        });
        holder.btnDeleteNews.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void setOnDataLoadedListener(Runnable listener) {
        onDataLoadedListener = listener;
    }

    public void filter(String query) {
        searchQuery = query == null ? "" : query.trim();
        loadPage(1);
    }

    public void filterByCategory(String categoryName) {
        selectedCategory = categoryName == null || categoryName.equalsIgnoreCase("Tất cả")
                ? "" : categoryName.trim();
        loadPage(1);
    }

    public void filterByDateRange(String startDate, String endDate) {
        this.startDate = startDate == null ? "" : startDate;
        this.endDate = endDate == null ? "" : endDate;
        loadPage(1);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageCount() {
        return (totalCount + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    public void goToPage(int page) {
        loadPage(Math.max(1, page));
    }

    public void goToPreviousPage() {
        goToPage(currentPage - 1);
    }

    public void goToNextPage() {
        goToPage(currentPage + 1);
    }

    public void refresh() {
        loadPage(currentPage);
    }

    private void loadPage(int page) {
        int requestedPage = Math.max(1, page);
        long requestId = latestRequest.incrementAndGet();
        queryExecutor.execute(() -> {
            String categoryId = categoryDatabaseHelper.getCategoryIdByName(selectedCategory);
            int count = newsDatabaseHelper.getNewsCount(
                    searchQuery, categoryId, startDate, endDate);
            List<News> pageData = newsDatabaseHelper.getNewsPage(
                    searchQuery, categoryId, startDate, endDate, requestedPage, PAGE_SIZE);
            mainHandler.post(() -> {
                if (requestId != latestRequest.get()) {
                    return;
                }
                totalCount = count;
                currentPage = count == 0
                        ? 1
                        : Math.min(requestedPage, getPageCount());
                if (currentPage != requestedPage) {
                    loadPage(currentPage);
                    return;
                }
                newsList = pageData;
                notifyDataSetChanged();
                if (onDataLoadedListener != null) {
                    onDataLoadedListener.run();
                }
            });
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        queryExecutor.shutdownNow();
        super.onDetachedFromRecyclerView(recyclerView);
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
