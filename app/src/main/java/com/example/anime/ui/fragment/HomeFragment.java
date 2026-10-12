package com.example.anime.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.anime.R;
import com.example.anime.adapter.AnimeAdapter;
import com.example.anime.adapter.StaticBannerAdapter;
import com.example.anime.bean.Anime;
import com.example.anime.constants.AppConstants;
import com.example.anime.ui.activity.AnimeDetailActivity;
import com.example.anime.ui.activity.AnimeListActivity;
import com.example.anime.ui.activity.CalendarActivity;
import com.example.anime.utils.AnimeDataUtils;
import com.example.anime.utils.HeimaoApiService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final long AUTO_SCROLL_INTERVAL = 4000;

    private ViewPager2 bannerViewPager;
    private LinearLayout llBannerIndicator;
    private RecyclerView rvHotAnime;
    private RecyclerView rvNewAnime;
    private LinearLayout llCountryCn;
    private LinearLayout llCountryJp;
    private LinearLayout llCountryUs;
    private LinearLayout llCountryKr;
    private LinearLayout llCalendar;
    private TextView tvSeeMoreHot;
    private TextView tvSeeMoreNew;

    private List<Anime> hotAnimes;
    private List<Anime> newAnimes;
    private AnimeAdapter hotAdapter;
    private AnimeAdapter newAdapter;
    private Handler handler;
    private Runnable autoScrollRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadData();
        setupBanner();
        setupRecyclerViews();
        setupListeners();
        loadOnlineData();
    }

    private void initViews(View view) {
        bannerViewPager = view.findViewById(R.id.banner_view_pager);
        llBannerIndicator = view.findViewById(R.id.ll_banner_indicator);
        rvHotAnime = view.findViewById(R.id.rv_hot_anime);
        rvNewAnime = view.findViewById(R.id.rv_new_anime);
        llCountryCn = view.findViewById(R.id.ll_country_cn);
        llCountryJp = view.findViewById(R.id.ll_country_jp);
        llCountryUs = view.findViewById(R.id.ll_country_us);
        llCountryKr = view.findViewById(R.id.ll_country_kr);
        llCalendar = view.findViewById(R.id.ll_calendar);
        tvSeeMoreHot = view.findViewById(R.id.tv_see_more_hot);
        tvSeeMoreNew = view.findViewById(R.id.tv_see_more_new);
    }

    private void loadData() {
        hotAnimes = new ArrayList<>(AnimeDataUtils.getHotAnimes(requireContext(), 10));
        newAnimes = new ArrayList<>(AnimeDataUtils.getNewAnimes(requireContext(), 10));
    }

    private void setupBanner() {
        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner_1);
        bannerImages.add(R.drawable.banner_2);
        bannerImages.add(R.drawable.banner_3);
        bannerImages.add(R.drawable.banner_4);
        bannerImages.add(R.drawable.banner_5);
        bannerImages.add(R.drawable.banner_6);

        StaticBannerAdapter adapter = new StaticBannerAdapter(bannerImages);
        bannerViewPager.setAdapter(adapter);

        setupBannerIndicator(bannerImages.size());

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateBannerIndicator(position);
            }
        });

        startAutoScroll();
    }

    private void setupBannerIndicator(int count) {
        llBannerIndicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            View indicator = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            params.setMargins(4, 0, 4, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(i == 0 ? R.drawable.banner_indicator_active : R.drawable.banner_indicator_inactive);
            llBannerIndicator.addView(indicator);
        }
    }

    private void updateBannerIndicator(int position) {
        for (int i = 0; i < llBannerIndicator.getChildCount(); i++) {
            llBannerIndicator.getChildAt(i).setBackgroundResource(
                    i == position ? R.drawable.banner_indicator_active : R.drawable.banner_indicator_inactive);
        }
    }

    private void startAutoScroll() {
        handler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int totalItems = bannerViewPager.getAdapter() != null ? bannerViewPager.getAdapter().getItemCount() : 0;
                int nextItem = (currentItem + 1) % totalItems;
                bannerViewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, AUTO_SCROLL_INTERVAL);
            }
        };
        handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_INTERVAL);
    }

    private void stopAutoScroll() {
        if (handler != null && autoScrollRunnable != null) {
            handler.removeCallbacks(autoScrollRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAutoScroll();
    }

    private void setupRecyclerViews() {
        // Hot anime
        rvHotAnime.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        hotAdapter = new AnimeAdapter(hotAnimes, anime -> {
            Intent intent = new Intent(requireContext(), AnimeDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
            intent.putExtra(AppConstants.EXTRA_ANIME, anime);
            startActivity(intent);
        });
        rvHotAnime.setAdapter(hotAdapter);

        // New anime
        rvNewAnime.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        newAdapter = new AnimeAdapter(newAnimes, anime -> {
            Intent intent = new Intent(requireContext(), AnimeDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
            intent.putExtra(AppConstants.EXTRA_ANIME, anime);
            startActivity(intent);
        });
        rvNewAnime.setAdapter(newAdapter);
    }

    /**
     * 从黑猫动漫(bmmdmm.com)在线加载热门和新番数据，失败时保留本地数据
     */
    private void loadOnlineData() {
        // 热门：按网站点击量排序
        HeimaoApiService.getList(null, null, null, 0, "点击量", 0,
                new HeimaoApiService.HeimaoCallback<List<Anime>>() {
                    @Override
                    public void onSuccess(List<Anime> data) {
                        if (isAdded() && data != null && !data.isEmpty()) {
                            requireActivity().runOnUiThread(() -> {
                                hotAnimes.clear();
                                hotAnimes.addAll(data);
                                hotAdapter.notifyDataSetChanged();
                            });
                        }
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        // 保留本地热门数据
                    }
                });

        // 新番：当年网站目录
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        HeimaoApiService.getList(null, null, null, currentYear, null, 0,
                new HeimaoApiService.HeimaoCallback<List<Anime>>() {
                    @Override
                    public void onSuccess(List<Anime> data) {
                        if (isAdded() && data != null && !data.isEmpty()) {
                            requireActivity().runOnUiThread(() -> {
                                newAnimes.clear();
                                newAnimes.addAll(data);
                                newAdapter.notifyDataSetChanged();
                            });
                        }
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        // 保留本地新番数据
                    }
                });
    }

    private void setupListeners() {
        tvSeeMoreHot.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AnimeListActivity.class);
            intent.putExtra(AppConstants.EXTRA_SORT_TYPE, AppConstants.SORT_HEAT);
            startActivity(intent);
        });

        tvSeeMoreNew.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AnimeListActivity.class);
            intent.putExtra(AppConstants.EXTRA_SORT_TYPE, AppConstants.SORT_NEW);
            startActivity(intent);
        });

        llCountryCn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AnimeListActivity.class);
            intent.putExtra(AppConstants.EXTRA_COUNTRY, AppConstants.COUNTRY_CN);
            startActivity(intent);
        });

        llCountryJp.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AnimeListActivity.class);
            intent.putExtra(AppConstants.EXTRA_COUNTRY, AppConstants.COUNTRY_JP);
            startActivity(intent);
        });

        llCountryUs.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AnimeListActivity.class);
            intent.putExtra(AppConstants.EXTRA_COUNTRY, AppConstants.COUNTRY_US);
            startActivity(intent);
        });

        llCountryKr.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AnimeListActivity.class);
            intent.putExtra(AppConstants.EXTRA_COUNTRY, AppConstants.COUNTRY_KR);
            startActivity(intent);
        });

        llCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CalendarActivity.class);
            startActivity(intent);
        });
    }
}
