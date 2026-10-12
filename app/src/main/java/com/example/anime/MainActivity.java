package com.example.anime;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anime.utils.AnimeDataUtils;
import com.example.anime.utils.SPUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize anime data
        AnimeDataUtils.initAnimeData(this);

        new Handler().postDelayed(() -> {
            if (SPUtils.isLogin(this)) {
                // Already logged in, go to main
                // For now just finish splash
            } else {
                // Not logged in, will redirect via splash
            }
            // This is handled by splash activity
        }, 1500);
    }
}
