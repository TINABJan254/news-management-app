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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    private String searchQuery = "";
    private String selectedCategory = "";
    private String startDate = "";
    private String endDate = "";

    public NewsAdapter(Context context, List<News> newsList, OnNewsActionListener listener) {
        this.context = context;
        this.newsList = new ArrayList<>(newsList);
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
        this.newsListFull = new ArrayList<>(newList);
        applyFilters();
    }

    // Tìm kiếm theo từ khóa (tiêu đề hoặc mã tin)
    public void filter(String query) {
        searchQuery = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        applyFilters();
    }

    // Lọc theo thể loại
    public void filterByCategory(String categoryName) {
        selectedCategory = categoryName == null || categoryName.equalsIgnoreCase("Tất cả")
                ? "" : categoryName;
        applyFilters();
    }

    public void filterByDateRange(String startDate, String endDate) {
        this.startDate = startDate == null ? "" : startDate;
        this.endDate = endDate == null ? "" : endDate;
        applyFilters();
    }

    private void applyFilters() {
        List<News> filtered = new ArrayList<>();
        for (News item : newsListFull) {
            boolean matchesQuery = searchQuery.isEmpty()
                    || item.getTitle().toLowerCase(Locale.ROOT).contains(searchQuery)
                    || item.getId().toLowerCase(Locale.ROOT).contains(searchQuery);
            boolean matchesCategory = selectedCategory.isEmpty()
                    || item.getCategoryName().equalsIgnoreCase(selectedCategory);
            boolean matchesDate = matchesDateRange(item.getPublishDate());
            if (matchesQuery && matchesCategory && matchesDate) {
                filtered.add(item);
            }
        }
        newsList = filtered;
        notifyDataSetChanged();
    }

    private boolean matchesDateRange(String publishDate) {
        if (startDate.isEmpty() && endDate.isEmpty()) {
            return true;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
            format.setLenient(false);
            long date = format.parse(publishDate).getTime();
            long start = startDate.isEmpty() ? Long.MIN_VALUE : format.parse(startDate).getTime();
            long end = endDate.isEmpty() ? Long.MAX_VALUE : format.parse(endDate).getTime();
            return date >= start && date <= end;
        } catch (ParseException exception) {
            return false;
        }
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
