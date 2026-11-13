package com.example.newsmanagement.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.newsmanagement.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "news_management.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";

    public CategoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " ("
                + COLUMN_ID + " TEXT PRIMARY KEY NOT NULL, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT NOT NULL DEFAULT '')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE categories_new ("
                    + COLUMN_ID + " TEXT PRIMARY KEY NOT NULL, "
                    + COLUMN_NAME + " TEXT NOT NULL, "
                    + COLUMN_DESCRIPTION + " TEXT NOT NULL DEFAULT '')");
            db.execSQL("INSERT INTO categories_new ("
                    + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_DESCRIPTION + ") "
                    + "SELECT " + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_DESCRIPTION
                    + " FROM " + TABLE_CATEGORIES);
            db.execSQL("DROP TABLE " + TABLE_CATEGORIES);
            db.execSQL("ALTER TABLE categories_new RENAME TO " + TABLE_CATEGORIES);
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_CATEGORIES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " ASC")) {
            while (cursor.moveToNext()) {
                categories.add(fromCursor(cursor));
            }
        }

        return categories;
    }

    public boolean categoryExists(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                TABLE_CATEGORIES,
                new String[]{COLUMN_ID},
                COLUMN_ID + " = ?",
                new String[]{id},
                null,
                null,
                null)) {
            return cursor.moveToFirst();
        }
    }

    public Category getCategoryById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                TABLE_CATEGORIES,
                null,
                COLUMN_ID + " = ?",
                new String[]{id},
                null,
                null,
                null)) {
            return cursor.moveToFirst() ? fromCursor(cursor) : null;
        }
    }

    public String getNextCategoryId() {
        SQLiteDatabase db = getReadableDatabase();
        String nextId = "TL01";

        try (Cursor cursor = db.rawQuery(
                "SELECT MAX(CAST(SUBSTR(" + COLUMN_ID + ", 3) AS INTEGER)) "
                        + "FROM " + TABLE_CATEGORIES
                        + " WHERE " + COLUMN_ID + " LIKE 'TL%'",
                null)) {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                nextId = String.format(Locale.ROOT, "TL%02d", cursor.getInt(0) + 1);
            }
        }

        return nextId;
    }

    public long insertCategory(Category category) {
        return getWritableDatabase().insertOrThrow(
                TABLE_CATEGORIES,
                null,
                toContentValues(category));
    }

    public int updateCategory(Category category) {
        return getWritableDatabase().update(
                TABLE_CATEGORIES,
                toContentValues(category),
                COLUMN_ID + " = ?",
                new String[]{category.getId()});
    }

    public int deleteCategory(Category category) {
        return getWritableDatabase().delete(
                TABLE_CATEGORIES,
                COLUMN_ID + " = ?",
                new String[]{category.getId()});
    }

    private static ContentValues toContentValues(Category category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, category.getId());
        values.put(COLUMN_NAME, category.getName());
        values.put(COLUMN_DESCRIPTION, category.getDescription());
        return values;
    }

    private static Category fromCursor(Cursor cursor) {
        return new Category(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                0);
    }
}
