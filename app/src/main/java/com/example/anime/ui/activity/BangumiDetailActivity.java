package com.example.anime.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.anime.R;
import com.example.anime.bean.BangumiSubject;
import com.example.anime.utils.BangumiApiService;

public class BangumiDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvOriginalName;
    private TextView tvType;
    private TextView tvDate;
    private TextView tvRating;
    private TextView tvRank;
    private TextView tvRatingCount;
    private TextView tvDescription;
    private TextView tvBangumiLink;
    private ProgressBar progressBar;

    private String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangumi_detail);

        subjectId = getIntent().getStringExtra("subject_id");
        if (subjectId == null || subjectId.isEmpty()) {
            Toast.makeText(this, "番剧ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadDetail();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        tvTitle = findViewById(R.id.tv_title);
        tvOriginalName = findViewById(R.id.tv_original_name);
        tvType = findViewById(R.id.tv_type);
        tvDate = findViewById(R.id.tv_date);
        tvRating = findViewById(R.id.tv_rating);
        tvRank = findViewById(R.id.tv_rank);
        tvRatingCount = findViewById(R.id.tv_rating_count);
        tvDescription = findViewById(R.id.tv_description);
        tvBangumiLink = findViewById(R.id.tv_bangumi_link);
        progressBar = findViewById(R.id.progress_bar);

        ivBack.setOnClickListener(v -> finish());
    }

    private void loadDetail() {
        progressBar.setVisibility(View.VISIBLE);

        BangumiApiService.getSubjectDetail(subjectId, new BangumiApiService.BangumiCallback<BangumiSubject>() {
            @Override
            public void onSuccess(BangumiSubject data) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    displaySubject(data);
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(BangumiDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displaySubject(BangumiSubject subject) {
        // 封面
        Glide.with(this)
                .load(subject.getCoverUrl())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(ivCover);

        // 标题
        tvTitle.setText(subject.getDisplayName());
        tvOriginalName.setText(subject.getName() != null ? subject.getName() : "");

        // 类型
        tvType.setText(subject.getTypeName());

        // 日期
        if (subject.getDate() != null && !subject.getDate().isEmpty()) {
            tvDate.setText(subject.getDate());
        } else {
            tvDate.setText("日期未知");
        }

        // 评分
        if (subject.getScore() > 0) {
            tvRating.setText(String.valueOf(subject.getScore()));
        } else {
            tvRating.setText("暂无评分");
        }

        // 排名
        if (subject.getRank() > 0) {
            tvRank.setText("排名: #" + subject.getRank());
        } else {
            tvRank.setText("排名: 暂无");
        }

        // 集数 & 状态
        if (subject.getEpisodeCount() > 0) {
            tvRatingCount.setText("集数: " + subject.getEpisodeCount() + " | " + subject.getStatusCn());
        } else {
            tvRatingCount.setText("状态: " + subject.getStatusCn());
        }

        // 简介
        String summary = subject.getSummary();
        if (summary != null && !summary.isEmpty()) {
            tvDescription.setText(summary);
        } else {
            tvDescription.setText("暂无简介");
        }

        // Kitsu 外链
        tvBangumiLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://kitsu.io/anime/" + subject.getId()));
            startActivity(browserIntent);
        });
    }
}
