package com.example.newsmanagement;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.adapter.NewsFeedAdapter;
import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        view.findViewById(R.id.bottomNavigation).setVisibility(View.GONE);
        setupLatestNews(view);
        return view;
    }

    private void setupLatestNews(View view) {
        Category social = new Category("TL02", "Xã hội", "", 0);
        Category world = new Category("TL05", "Thế giới", "", 0);
        Category sports = new Category("TL01", "Thể thao", "", 0);
        News featuredNews = new News("TT-1080", "Miền Bắc mưa đến khi nào?",
                "Dự báo thời tiết mới nhất khu vực miền Bắc.",
                "", social, "Admin", "6 giờ trước");

        List<News> newsList = new ArrayList<>();
        newsList.add(new News("TT-1079", "Mưa lớn làm nhiều cây đổ tại Hà Nội",
                "Thông tin mới nhất từ hiện trường.", "", social, "Admin", "2 giờ trước"));
        newsList.add(new News("TT-1078", "Nhà lãnh đạo EU tổ chức họp khẩn",
                "Các nước châu Âu thảo luận những vấn đề quan trọng.",
                "", world, "Admin", "5 giờ trước"));
        newsList.add(new News("TT-1077", "Đội tuyển quốc gia chuẩn bị cho giải đấu mới",
                "Đội tuyển đang hoàn thiện chiến thuật.",
                "", sports, "Admin", "7 giờ trước"));

        TextView category = view.findViewById(R.id.tvFeaturedCategory);
        TextView title = view.findViewById(R.id.tvFeaturedTitle);
        TextView time = view.findViewById(R.id.tvFeaturedTime);
        category.setText(featuredNews.getCategoryName() + " · " + featuredNews.getPublishDate());
        title.setText(featuredNews.getTitle());
        time.setText("34 nghìn lượt xem");
        view.findViewById(R.id.featuredNews).setOnClickListener(v -> openNews(featuredNews));

        RecyclerView recyclerView = view.findViewById(R.id.rvLatestNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new NewsFeedAdapter(getActivity(), newsList, this::openNews));
    }

    private void openNews(News news) {
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_NEWS, news);
        startActivity(intent);
    }
}
