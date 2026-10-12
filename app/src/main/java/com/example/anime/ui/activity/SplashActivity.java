package com.example.anime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anime.R;
import com.example.anime.utils.AnimeDataUtils;
import com.example.anime.utils.SPUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize anime data (forceRefresh=true 用于更新B站视频地址，确认数据更新后可改回false)
        AnimeDataUtils.initAnimeData(this, true);

        new Handler().postDelayed(() -> {
            if (SPUtils.isLogin(this)) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 1500);
    }
}
