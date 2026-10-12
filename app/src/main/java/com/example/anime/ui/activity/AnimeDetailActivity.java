package com.example.anime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.anime.R;
import com.example.anime.adapter.EpisodeAdapter;
import com.example.anime.bean.Anime;
import com.example.anime.bean.Episode;
import com.example.anime.constants.AppConstants;
import com.example.anime.utils.AnimeDataUtils;
import com.example.anime.utils.BiliBiliApiService;
import com.example.anime.utils.FileUtils;
import com.example.anime.utils.HeimaoApiService;
import com.example.anime.utils.SPUtils;

import java.util.List;

public class AnimeDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvCountry;
    private TextView tvYear;
    private TextView tvRating;
    private TextView tvDescription;
    private TextView tvType;
    private ImageView ivCollect;
    private RecyclerView rvEpisodes;
    private LinearLayout llPlayFirst;

    private Anime anime;
    private String userId;
    private boolean isCollected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);

        userId = SPUtils.getUserId(this);
        String animeId = getIntent().getStringExtra(AppConstants.EXTRA_ANIME_ID);

        // 优先使用 Intent 直接传递的动漫对象（在线搜索结果可能不存在于本地数据）
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

        initViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        tvTitle = findViewById(R.id.tv_title);
        tvCountry = findViewById(R.id.tv_country);
        tvYear = findViewById(R.id.tv_year);
        tvRating = findViewById(R.id.tv_rating);
        tvDescription = findViewById(R.id.tv_description);
        tvType = findViewById(R.id.tv_type);
        ivCollect = findViewById(R.id.iv_collect);
        rvEpisodes = findViewById(R.id.rv_episodes);
        llPlayFirst = findViewById(R.id.ll_play_first);
    }

    private void loadData() {
        Glide.with(this)
                .load(anime.getCover())
                .placeholder(R.drawable.ic_placeholder)
                .into(ivCover);

        tvTitle.setText(anime.getTitle());
        tvCountry.setText(anime.getCountry());
        tvYear.setText(String.valueOf(anime.getYear()));
        tvRating.setText(String.valueOf(anime.getRating()));
        tvDescription.setText(anime.getDescription());

        // Build type string
        if (anime.getType() != null && !anime.getType().isEmpty()) {
            StringBuilder typeStr = new StringBuilder();
            for (int i = 0; i < anime.getType().size(); i++) {
                typeStr.append(anime.getType().get(i));
                if (i < anime.getType().size() - 1) {
                    typeStr.append(" / ");
                }
            }
            tvType.setText(typeStr.toString());
        }

        // Check collect status
        isCollected = FileUtils.isCollected(this, userId, anime.getId());
        updateCollectIcon();

        // Setup episodes
        setupEpisodes();

        // 异步从黑猫动漫获取封面图与剧集，失败时回退到B站
        fetchOnlineData();
    }

    /**
     * 优先从黑猫动漫(bmmdmm.com)获取真实封面图、评分和剧集列表
     */
    private void fetchOnlineData() {
        String showId = anime.getHeimaoShowId();
        if (showId != null && !showId.isEmpty()) {
            loadHeimaoDetail(showId);
            return;
        }

        // 没有黑猫ID，按标题搜索匹配
        HeimaoApiService.search(anime.getTitle(), new HeimaoApiService.HeimaoCallback<List<Anime>>() {
            @Override
            public void onSuccess(List<Anime> results) {
                if (results != null && !results.isEmpty()) {
                    loadHeimaoDetail(results.get(0).getHeimaoShowId());
                } else {
                    fetchRealCover();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                fetchRealCover();
            }
        });
    }

    /**
     * 加载黑猫动漫详情页，刷新封面、评分、简介和真实剧集列表
     */
    private void loadHeimaoDetail(String showId) {
        HeimaoApiService.getDetail(showId, new HeimaoApiService.HeimaoCallback<Anime>() {
            @Override
            public void onSuccess(final Anime online) {
                runOnUiThread(() -> {
                    anime.setHeimaoShowId(online.getHeimaoShowId());
                    if (online.getCover() != null && !online.getCover().isEmpty()) {
                        anime.setCover(online.getCover());
                        Glide.with(AnimeDetailActivity.this)
                                .load(online.getCover())
                                .placeholder(R.drawable.ic_placeholder)
                                .into(ivCover);
                    }
                    if (online.getRating() > 0) {
                        anime.setRating(online.getRating());
                        tvRating.setText(String.valueOf(online.getRating()));
                    }
                    if (anime.getDescription() == null || anime.getDescription().isEmpty()) {
                        anime.setDescription(online.getDescription());
                        tvDescription.setText(online.getDescription());
                    }
                    if (anime.getYear() <= 0 && online.getYear() > 0) {
                        anime.setYear(online.getYear());
                        tvYear.setText(String.valueOf(online.getYear()));
                    }
                    if (anime.getCountry() == null || anime.getCountry().isEmpty()) {
                        anime.setCountry(online.getCountry());
                        tvCountry.setText(online.getCountry());
                    }
                    if (online.getEpisodes() != null && !online.getEpisodes().isEmpty()) {
                        anime.setEpisodes(online.getEpisodes());
                        setupEpisodes();
                    }
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                fetchRealCover();
            }
        });
    }

    /**
     * 从B站API获取真实封面图并更新显示
     */
    private void fetchRealCover() {
        String seasonId = anime.getBiliSeasonId();
        if (seasonId == null || seasonId.isEmpty()) {
            // 没有season_id，尝试通过标题搜索
            BiliBiliApiService.searchBangumi(anime.getTitle(), new BiliBiliApiService.BiliCallback<String[]>() {
                @Override
                public void onSuccess(String[] data) {
                    if (data != null && data.length >= 2 && data[1] != null && !data[1].isEmpty()) {
                        runOnUiThread(() -> {
                            Glide.with(AnimeDetailActivity.this)
                                    .load(data[1])
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(ivCover);
                        });
                    }
                }

                @Override
                public void onFailure(String errorMsg) {
                    // 搜索失败，保持占位图
                }
            });
            return;
        }

        // 通过season_id获取封面图
        BiliBiliApiService.getAnimeCover(seasonId, new BiliBiliApiService.BiliCallback<String>() {
            @Override
            public void onSuccess(String coverUrl) {
                runOnUiThread(() -> {
                    Glide.with(AnimeDetailActivity.this)
                            .load(coverUrl)
                            .placeholder(R.drawable.ic_placeholder)
                            .into(ivCover);
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                // 获取失败，尝试搜索
                BiliBiliApiService.searchBangumi(anime.getTitle(), new BiliBiliApiService.BiliCallback<String[]>() {
                    @Override
                    public void onSuccess(String[] data) {
                        if (data != null && data.length >= 2 && data[1] != null && !data[1].isEmpty()) {
                            runOnUiThread(() -> {
                                Glide.with(AnimeDetailActivity.this)
                                        .load(data[1])
                                        .placeholder(R.drawable.ic_placeholder)
                                        .into(ivCover);
                            });
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        // 保持占位图
                    }
                });
            }
        });
    }

    private void setupEpisodes() {
        List<Episode> episodes = anime.getEpisodes();
        if (episodes == null || episodes.isEmpty()) {
            return;
        }

        rvEpisodes.setLayoutManager(new GridLayoutManager(this, 5));
        EpisodeAdapter adapter = new EpisodeAdapter(episodes, new EpisodeAdapter.OnEpisodeClickListener() {
            @Override
            public void onEpisodeClick(Episode episode) {
                Intent intent = new Intent(AnimeDetailActivity.this, VideoPlayActivity.class);
                intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
                intent.putExtra(AppConstants.EXTRA_ANIME, anime);
                intent.putExtra(AppConstants.EXTRA_EPISODE_NUMBER, episode.getEpisodeNumber());
                startActivity(intent);
            }
        });
        rvEpisodes.setAdapter(adapter);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollected) {
                    FileUtils.removeCollect(AnimeDetailActivity.this, userId, anime.getId());
                    Toast.makeText(AnimeDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                } else {
                    FileUtils.addCollect(AnimeDetailActivity.this, userId, anime.getId());
                    Toast.makeText(AnimeDetailActivity.this, "已添加收藏", Toast.LENGTH_SHORT).show();
                }
                isCollected = !isCollected;
                updateCollectIcon();
            }
        });

        llPlayFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Episode> episodes = anime.getEpisodes();
                if (episodes != null && !episodes.isEmpty()) {
                    Intent intent = new Intent(AnimeDetailActivity.this, VideoPlayActivity.class);
                    intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
                    intent.putExtra(AppConstants.EXTRA_ANIME, anime);
                    intent.putExtra(AppConstants.EXTRA_EPISODE_NUMBER, 1);
                    startActivity(intent);
                }
            }
        });
    }

    private void updateCollectIcon() {
        if (isCollected) {
            ivCollect.setImageResource(R.drawable.ic_collected);
        } else {
            ivCollect.setImageResource(R.drawable.ic_collect);
        }
    }
}
