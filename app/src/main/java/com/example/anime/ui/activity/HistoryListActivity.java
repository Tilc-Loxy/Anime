package com.example.anime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;
import com.example.anime.adapter.AnimeAdapter;
import com.example.anime.bean.Anime;
import com.example.anime.constants.AppConstants;
import com.example.anime.utils.FileUtils;
import com.example.anime.utils.SPUtils;

import java.util.List;

public class HistoryListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_list);

        initViews();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvHistory = findViewById(R.id.rv_collect);

        ivBack.setOnClickListener(v -> finish());
    }

    private void loadData() {
        String userId = SPUtils.getUserId(this);
        List<Anime> histories = FileUtils.getHistoryAnimes(this, userId);

        tvTitle.setText("观看历史 (" + histories.size() + ")");

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        AnimeAdapter adapter = new AnimeAdapter(histories, anime -> {
            Intent intent = new Intent(this, AnimeDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
            startActivity(intent);
        });
        rvHistory.setAdapter(adapter);
    }
}
