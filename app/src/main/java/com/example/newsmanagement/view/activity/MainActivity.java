package com.example.newsmanagement.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.newsmanagement.R;
import com.example.newsmanagement.view.fragment.HomeFragment;
import com.example.newsmanagement.view.fragment.ManagementFragment;
import com.example.newsmanagement.view.fragment.NewsFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private Fragment newsFragment;
    private Fragment managementFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            newsFragment = new NewsFragment();
            managementFragment = new ManagementFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.mainContent, homeFragment, "home")
                    .add(R.id.mainContent, newsFragment, "news")
                    .add(R.id.mainContent, managementFragment, "management")
                    .hide(newsFragment)
                    .hide(managementFragment)
                    .commit();
        } else {
            homeFragment = getFragmentManager().findFragmentByTag("home");
            newsFragment = getFragmentManager().findFragmentByTag("news");
            managementFragment = getFragmentManager().findFragmentByTag("management");
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                showFragment(homeFragment, newsFragment, managementFragment);
            } else if (item.getItemId() == R.id.nav_news) {
                showFragment(newsFragment, homeFragment, managementFragment);
            } else if (item.getItemId() == R.id.nav_management) {
                showFragment(managementFragment, homeFragment, newsFragment);
            } else {
                return false;
            }
            return true;
        });
    }

    private void showFragment(Fragment fragment, Fragment hiddenFirst, Fragment hiddenSecond) {
        if (fragment == null) {
            return;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.show(fragment);
        if (hiddenFirst != null) {
            transaction.hide(hiddenFirst);
        }
        if (hiddenSecond != null) {
            transaction.hide(hiddenSecond);
        }
        transaction.commit();
    }
}
