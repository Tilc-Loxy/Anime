package com.example.anime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;
import com.example.anime.adapter.AnimeAdapter;
import com.example.anime.bean.Anime;
import com.example.anime.constants.AppConstants;
import com.example.anime.utils.AnimeDataUtils;
import com.example.anime.utils.HeimaoApiService;

import java.util.ArrayList;
import java.util.List;

public class AnimeListActivity extends AppCompatActivity {

    /** 网站目录每页数量，用于判断是否还有下一页 */
    private static final int PAGE_SIZE = 24;

    private ImageView ivBack;
    private TextView tvTitle;
    private RecyclerView rvAnimeList;

    private final List<Anime> animeList = new ArrayList<>();
    private AnimeAdapter adapter;
    private LinearLayoutManager layoutManager;

    // 在线目录参数
    private String keyword;
    private String country;
    private String type;
    private String order;
    private int year;
    private int pageIndex = 0;
    private boolean onlineMode = true;
    private boolean loading = false;
    private boolean hasMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);

        readIntentParams();
        initViews();
        setupRecyclerView();
        loadFirstPage();
    }

    private void readIntentParams() {
        keyword = getIntent().getStringExtra(AppConstants.EXTRA_SEARCH_KEYWORD);
        country = getIntent().getStringExtra(AppConstants.EXTRA_COUNTRY);
        type = getIntent().getStringExtra(AppConstants.EXTRA_TYPE);
        year = getIntent().getIntExtra(AppConstants.EXTRA_YEAR, 0);
        int sortType = getIntent().getIntExtra(AppConstants.EXTRA_SORT_TYPE, AppConstants.SORT_HEAT);

        // 排序映射为网站目录的排序参数
        if (sortType == AppConstants.SORT_HEAT) {
            order = "点击量";
        } else {
            order = null;
        }
    }

    private String buildTitle() {
        if (keyword != null && !keyword.isEmpty()) {
            return "搜索: " + keyword;
        }
        if (country != null && !country.isEmpty()) {
            return country + "动漫";
        }
        if (type != null && !type.isEmpty()) {
            return type + "类型";
        }
        if (year > 0) {
            return year + "年动漫";
        }
        return order != null ? "热门动漫" : "动漫列表";
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        rvAnimeList = findViewById(R.id.rv_anime_list);

        tvTitle.setText(buildTitle());
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        rvAnimeList.setLayoutManager(layoutManager);
        adapter = new AnimeAdapter(animeList, anime -> {
            Intent intent = new Intent(AnimeListActivity.this, AnimeDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
            intent.putExtra(AppConstants.EXTRA_ANIME, anime);
            startActivity(intent);
        });
        rvAnimeList.setAdapter(adapter);

        // 滚动到接近底部时加载下一页
        rvAnimeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!onlineMode || loading || !hasMore) return;
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                if (lastVisible >= animeList.size() - 6) {
                    loadMore();
                }
            }
        });
    }

    private void loadFirstPage() {
        pageIndex = 0;
        hasMore = true;
        onlineMode = true;
        animeList.clear();
        adapter.notifyDataSetChanged();
        loadMore();
    }

    private void loadMore() {
        if (loading || !onlineMode || !hasMore) return;
        loading = true;

        HeimaoApiService.HeimaoCallback<List<Anime>> callback = new HeimaoApiService.HeimaoCallback<List<Anime>>() {
            @Override
            public void onSuccess(List<Anime> data) {
                runOnUiThread(() -> {
                    loading = false;
                    boolean firstPage = pageIndex == 0;
                    if (firstPage) {
                        animeList.clear();
                    }
                    animeList.addAll(data);
                    hasMore = data.size() >= PAGE_SIZE;
                    pageIndex++;
                    adapter.notifyDataSetChanged();

                    // 首页无数据时回退到本地数据
                    if (firstPage && animeList.isEmpty()) {
                        loadLocalData();
                    }
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                runOnUiThread(() -> {
                    loading = false;
                    if (pageIndex == 0) {
                        loadLocalData();
                    } else {
                        hasMore = false;
                        Toast.makeText(AnimeListActivity.this, "加载更多失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        if (keyword != null && !keyword.isEmpty()) {
            HeimaoApiService.search(keyword, callback);
        } else {
            HeimaoApiService.getList(country, type, null, year, order, pageIndex, callback);
        }
    }

    /**
     * 网络不可用或无结果时，回退到本地内置数据
     */
    private void loadLocalData() {
        onlineMode = false;
        hasMore = false;
        animeList.clear();

        if (keyword != null && !keyword.isEmpty()) {
            animeList.addAll(AnimeDataUtils.searchAnimes(this, keyword, country, type, year, AppConstants.SORT_HEAT));
        } else if (country != null && !country.isEmpty()) {
            animeList.addAll(AnimeDataUtils.getAnimesByCountry(this, country));
        } else if (type != null && !type.isEmpty()) {
            animeList.addAll(AnimeDataUtils.getAnimesByType(this, type));
        } else if (year > 0) {
            animeList.addAll(AnimeDataUtils.searchAnimes(this, null, null, null, year, AppConstants.SORT_NEW));
        } else {
            animeList.addAll(AnimeDataUtils.searchAnimes(this, null, null, null, 0, AppConstants.SORT_HEAT));
        }

        adapter.notifyDataSetChanged();
        Toast.makeText(this, "在线数据加载失败，已显示离线数据", Toast.LENGTH_SHORT).show();
    }
}
