package com.example.newsmanagement.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.newsmanagement.model.Category;
import com.example.newsmanagement.model.News;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "news_articles.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NEWS = "news";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DETAIL = "detail";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_DESCRIPTION = "category_description";
    private static final String COLUMN_CATEGORY_ARTICLE_COUNT = "category_article_count";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_PUBLISH_DATE = "publish_date";

    public NewsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NEWS + " ("
                + COLUMN_ID + " TEXT PRIMARY KEY NOT NULL, "
                + COLUMN_TITLE + " TEXT NOT NULL, "
                + COLUMN_DETAIL + " TEXT NOT NULL, "
                + COLUMN_IMAGE_URL + " TEXT NOT NULL DEFAULT '', "
                + COLUMN_CATEGORY_ID + " TEXT NOT NULL, "
                + COLUMN_CATEGORY_NAME + " TEXT NOT NULL, "
                + COLUMN_CATEGORY_DESCRIPTION + " TEXT NOT NULL DEFAULT '', "
                + COLUMN_CATEGORY_ARTICLE_COUNT + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_AUTHOR + " TEXT NOT NULL, "
                + COLUMN_PUBLISH_DATE + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        onCreate(db);
    }

    public List<News> getAllNews() {
        List<News> newsList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_NEWS,
                null,
                null,
                null,
                null,
                null,
                "rowid DESC")) {
            while (cursor.moveToNext()) {
                newsList.add(fromCursor(cursor));
            }
        }

        return newsList;
    }

    public boolean newsExists(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                TABLE_NEWS,
                new String[]{COLUMN_ID},
                COLUMN_ID + " = ?",
                new String[]{id},
                null,
                null,
                null)) {
            return cursor.moveToFirst();
        }
    }

    public String getNextNewsId() {
        SQLiteDatabase db = getReadableDatabase();
        String nextId = "TT-0001";

        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(CAST(SUBSTR(" + COLUMN_ID + ", 4) AS INTEGER)) "
                        + "FROM " + TABLE_NEWS
                        + " WHERE " + COLUMN_ID + " LIKE 'TT-%'",
                null)) {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                nextId = String.format(Locale.ROOT, "TT-%04d", cursor.getInt(0) + 1);
            }
        }

        return nextId;
    }

    public long insertNews(News news) {
        return getWritableDatabase().insertOrThrow(
                TABLE_NEWS,
                null,
                toContentValues(news));
    }

    public int updateNews(News news) {
        return getWritableDatabase().update(
                TABLE_NEWS,
                toContentValues(news),
                COLUMN_ID + " = ?",
                new String[]{news.getId()});
    }

    public int deleteNews(News news) {
        return getWritableDatabase().delete(
                TABLE_NEWS,
                COLUMN_ID + " = ?",
                new String[]{news.getId()});
    }

    private static ContentValues toContentValues(News news) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, news.getId());
        values.put(COLUMN_TITLE, news.getTitle());
        values.put(COLUMN_DETAIL, news.getDetail());
        values.put(COLUMN_IMAGE_URL, news.getImageUrl());
        values.put(COLUMN_CATEGORY_ID, news.getCategoryId());
        values.put(COLUMN_CATEGORY_NAME, news.getCategoryName());
        values.put(COLUMN_CATEGORY_DESCRIPTION,
                news.getCategory() != null ? news.getCategory().getDescription() : "");
        values.put(COLUMN_CATEGORY_ARTICLE_COUNT,
                news.getCategory() != null ? news.getCategory().getArticleCount() : 0);
        values.put(COLUMN_AUTHOR, news.getAuthor());
        values.put(COLUMN_PUBLISH_DATE, news.getPublishDate());
        return values;
    }

    private static News fromCursor(Cursor cursor) {
        Category category = new Category(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ARTICLE_COUNT)));

        return new News(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
                category,
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISH_DATE)));
    }
}
