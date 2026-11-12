package com.example.newsmanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tạm thời điều hướng trực tiếp sang màn hình Quản lý
        startActivity(new Intent(this, ManagementActivity.class));
        finish();
    }
}
