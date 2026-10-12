package com.example.anime.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anime.R;
import com.example.anime.bean.Anime;
import com.example.anime.bean.Episode;
import com.example.anime.constants.AppConstants;
import com.example.anime.utils.AnimeDataUtils;
import com.example.anime.utils.FileUtils;
import com.example.anime.utils.SPUtils;

import java.util.List;

public class VideoPlayActivity extends AppCompatActivity {

    private ImageView ivBack;
    private WebView webView;
    private TextView tvTitle;
    private TextView tvEpisode;
    private ProgressBar progressBar;

    private Anime anime;
    private List<Episode> episodes;
    private int currentEpisodeNumber = 1;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        userId = SPUtils.getUserId(this);
        String animeId = getIntent().getStringExtra(AppConstants.EXTRA_ANIME_ID);
        currentEpisodeNumber = getIntent().getIntExtra(AppConstants.EXTRA_EPISODE_NUMBER, 1);

        // 优先使用 Intent 直接传递的动漫对象（在线动漫可能不存在于本地数据）
        Object extra = getIntent().getSerializableExtra(AppConstants.EXTRA_ANIME);
        if (extra instanceof Anime) {
            anime = (Anime) extra;
        } else if (animeId != null) {
            anime = AnimeDataUtils.getAnimeById(this, animeId);
        }

        if (anime == null) {
            Toast.makeText(this, "动漫信息不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        episodes = anime.getEpisodes();

        initViews();
        loadData();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        webView = findViewById(R.id.web_view);
        tvTitle = findViewById(R.id.tv_title);
        tvEpisode = findViewById(R.id.tv_episode);
        progressBar = findViewById(R.id.progress_bar);

        // 配置 WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // 使用桌面UA，避免黑猫动漫播放页在移动端跳转异常
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(VideoPlayActivity.this, "加载失败: " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        tvTitle.setText(anime.getTitle());

        ivBack.setOnClickListener(v -> finish());

        if (episodes != null && !episodes.isEmpty()) {
            playEpisode(currentEpisodeNumber);
        } else {
            Toast.makeText(this, "暂无播放地址", Toast.LENGTH_SHORT).show();
        }
    }

    private void playEpisode(int episodeNumber) {
        if (episodes == null || episodes.isEmpty()) return;

        Episode targetEpisode = null;
        for (Episode episode : episodes) {
            if (episode.getEpisodeNumber() == episodeNumber) {
                targetEpisode = episode;
                break;
            }
        }

        if (targetEpisode == null) {
            targetEpisode = episodes.get(0);
            episodeNumber = episodes.get(0).getEpisodeNumber();
        }

        currentEpisodeNumber = episodeNumber;
        tvEpisode.setText("第" + episodeNumber + "集");

        // 保存观看历史
        FileUtils.addHistory(this, userId, anime.getId(), episodeNumber);

        // 显示加载进度
        progressBar.setVisibility(View.VISIBLE);

        // 加载播放器页面
        webView.loadUrl(targetEpisode.getVideoUrl());
    }

    public void onPreEpisode(View view) {
        if (currentEpisodeNumber > 1) {
            playEpisode(currentEpisodeNumber - 1);
        } else {
            Toast.makeText(this, "已经是第一集了", Toast.LENGTH_SHORT).show();
        }
    }

    public void onNextEpisode(View view) {
        if (episodes != null && currentEpisodeNumber < episodes.size()) {
            playEpisode(currentEpisodeNumber + 1);
        } else {
            Toast.makeText(this, "已经是最后一集了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }
}
