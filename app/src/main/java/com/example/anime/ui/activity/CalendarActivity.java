package com.example.anime.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;
import com.example.anime.adapter.CalendarAdapter;
import com.example.anime.utils.BangumiApiService;

import java.util.List;

/**
 * 每日放送 - 调用 Bangumi API 获取每周番剧放送表
 */
public class CalendarActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvCalendar;
    private ProgressBar progressBar;
    private CalendarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initViews();
        loadCalendar();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        rvCalendar = findViewById(R.id.rv_calendar);
        progressBar = findViewById(R.id.progress_bar);

        ivBack.setOnClickListener(v -> finish());

        rvCalendar.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CalendarAdapter(null);
        rvCalendar.setAdapter(adapter);
    }

    private void loadCalendar() {
        progressBar.setVisibility(View.VISIBLE);

        BangumiApiService.getDailyCalendar(new BangumiApiService.BangumiCallback<List<BangumiApiService.DaySchedule>>() {
            @Override
            public void onSuccess(List<BangumiApiService.DaySchedule> data) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    adapter.updateData(data);
                    if (data == null || data.isEmpty()) {
                        Toast.makeText(CalendarActivity.this, "暂无热门番剧数据", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CalendarActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
