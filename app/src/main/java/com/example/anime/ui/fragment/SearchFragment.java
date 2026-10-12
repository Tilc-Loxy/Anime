package com.example.anime.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime.R;
import com.example.anime.adapter.AnimeAdapter;
import com.example.anime.bean.Anime;
import com.example.anime.constants.AppConstants;
import com.example.anime.ui.activity.AnimeDetailActivity;
import com.example.anime.utils.AnimeDataUtils;
import com.example.anime.utils.HeimaoApiService;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearchKeyword;
    private Spinner spinnerCountry;
    private Spinner spinnerType;
    private Spinner spinnerYear;
    private RadioGroup rgSort;
    private Button btnSearch;
    private RecyclerView rvSearchResult;
    private Switch swOnlineSearch;
    private LinearLayout llBilibiliJump;
    private Button btnBilibiliSearch;

    private List<Anime> masterResults;   // 本次搜索的完整结果（筛选前）
    private List<Anime> searchResults;   // 分类筛选+排序后的展示列表
    private String selectedCountry = "全部";
    private String selectedType = "全部";
    private int selectedYear = 0;
    private int selectedSortType = AppConstants.SORT_HEAT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupSpinners();
        setupListeners();
        loadAllAnimes();
    }

    private void initViews(View view) {
        etSearchKeyword = view.findViewById(R.id.et_search_keyword);
        spinnerCountry = view.findViewById(R.id.spinner_country);
        spinnerType = view.findViewById(R.id.spinner_type);
        spinnerYear = view.findViewById(R.id.spinner_year);
        rgSort = view.findViewById(R.id.rg_sort);
        btnSearch = view.findViewById(R.id.btn_search);
        rvSearchResult = view.findViewById(R.id.rv_search_result);
        swOnlineSearch = view.findViewById(R.id.sw_online_search);
        llBilibiliJump = view.findViewById(R.id.ll_bilibili_jump);
        btnBilibiliSearch = view.findViewById(R.id.btn_bilibili_search);
    }

    private void setupSpinners() {
        // Country spinner
        List<String> countries = new ArrayList<>();
        countries.add("全部");
        for (String country : AppConstants.COUNTRIES) {
            countries.add(country);
        }
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        // Type spinner
        List<String> types = new ArrayList<>();
        types.add("全部");
        for (String type : AppConstants.ANIME_TYPES) {
            types.add(type);
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Year spinner
        List<String> years = new ArrayList<>();
        years.add("全部");
        for (int year : AppConstants.YEARS) {
            years.add(String.valueOf(year));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
    }

    private void setupListeners() {
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedCountry = "全部";
                } else {
                    selectedCountry = AppConstants.COUNTRIES[position - 1];
                }
                // 切换分类条件后，立即重新筛选当前结果
                refreshDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedType = "全部";
                } else {
                    selectedType = AppConstants.ANIME_TYPES[position - 1];
                }
                // 切换分类条件后，立即重新筛选当前结果
                refreshDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedYear = 0;
                } else {
                    selectedYear = AppConstants.YEARS[position - 1];
                }
                // 切换分类条件后，立即重新筛选当前结果
                refreshDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        rgSort.setOnCheckedChangeListener((group, checkedId) -> {
            int previous = selectedSortType;
            if (checkedId == R.id.rb_heat) {
                selectedSortType = AppConstants.SORT_HEAT;
            } else if (checkedId == R.id.rb_rating) {
                selectedSortType = AppConstants.SORT_RATING;
            } else if (checkedId == R.id.rb_new) {
                selectedSortType = AppConstants.SORT_NEW;
            }
            // 切换排序规则后，立即对当前已展示的结果重新排序
            if (previous != selectedSortType) {
                refreshDisplay();
            }
        });

        btnSearch.setOnClickListener(v -> performSearch());

        btnBilibiliSearch.setOnClickListener(v -> openBilibiliSearch());
    }

    private void openBilibiliSearch() {
        String keyword = etSearchKeyword.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = "https://search.bilibili.com/bangumi?keyword=" + Uri.encode(keyword);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "无法打开浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 按当前选择的排序规则对结果列表排序
     * 注：在线搜索结果没有热度/评分数据（为0），此时排序保持网站原有相关度顺序
     */
    private void sortResults(List<Anime> list) {
        if (list == null) return;
        switch (selectedSortType) {
            case AppConstants.SORT_HEAT:
                list.sort((a, b) -> b.getHeat() - a.getHeat());
                break;
            case AppConstants.SORT_RATING:
                list.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
                break;
            case AppConstants.SORT_NEW:
                list.sort((a, b) -> b.getYear() - a.getYear());
                break;
        }
    }

    /**
     * 按当前分类条件（国家/类型/年份）筛选完整结果并排序后展示
     * 注：黑猫在线搜索结果不含地区数据，因此在线搜索前会重置地区筛选
     */
    private void refreshDisplay() {
        List<Anime> filtered = new ArrayList<>();
        if (masterResults != null) {
            for (Anime anime : masterResults) {
                boolean match = true;
                if (!selectedCountry.equals("全部")
                        && (anime.getCountry() == null || !anime.getCountry().equals(selectedCountry))) {
                    match = false;
                }
                if (match && !selectedType.equals("全部")
                        && (anime.getType() == null || !anime.getType().contains(selectedType))) {
                    match = false;
                }
                if (match && selectedYear > 0 && anime.getYear() != selectedYear) {
                    match = false;
                }
                if (match) {
                    filtered.add(anime);
                }
            }
        }
        sortResults(filtered);
        searchResults = filtered;
        updateLocalRecyclerView();
        if (filtered.isEmpty()) {
            llBilibiliJump.setVisibility(View.VISIBLE);
        } else {
            llBilibiliJump.setVisibility(View.GONE);
        }
    }

    private void performSearch() {
        String keyword = etSearchKeyword.getText().toString().trim();

        if (swOnlineSearch.isChecked()) {
            // 黑猫动漫在线搜索
            if (keyword.isEmpty()) {
                Toast.makeText(requireContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
                return;
            }
            searchHeimao(keyword, false);
        } else {
            // 本地搜索
            String country = selectedCountry.equals("全部") ? null : selectedCountry;
            String type = selectedType.equals("全部") ? null : selectedType;
            int year = selectedYear;

            masterResults = AnimeDataUtils.searchAnimes(requireContext(), keyword, country, type, year, selectedSortType);
            refreshDisplay();
            if (searchResults.isEmpty() && keyword != null && !keyword.isEmpty()) {
                // 本地无结果，自动用黑猫动漫在线搜索
                searchHeimao(keyword, false);
            }
        }
    }

    /**
     * 黑猫动漫(bmmdmm.com)在线搜索
     * @param fromLocalEmpty true 表示本地搜索无结果后的自动回退，失败时不再提示错误
     */
    private void searchHeimao(String keyword, boolean fromLocalEmpty) {
        // 黑猫在线搜索结果不含地区数据，重置地区筛选避免结果被清空
        if (spinnerCountry.getSelectedItemPosition() != 0) {
            spinnerCountry.setSelection(0);
            selectedCountry = "全部";
        }
        btnSearch.setEnabled(false);
        btnSearch.setText("搜索中...");

        HeimaoApiService.search(keyword, new HeimaoApiService.HeimaoCallback<List<Anime>>() {
            @Override
            public void onSuccess(List<Anime> data) {
                requireActivity().runOnUiThread(() -> {
                    btnSearch.setEnabled(true);
                    btnSearch.setText(R.string.search);
                    masterResults = data;
                    refreshDisplay();
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                requireActivity().runOnUiThread(() -> {
                    btnSearch.setEnabled(true);
                    btnSearch.setText(R.string.search);
                    if (!fromLocalEmpty) {
                        llBilibiliJump.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadAllAnimes() {
        masterResults = AnimeDataUtils.getAnimes(requireContext());
        refreshDisplay();
    }

    private void updateLocalRecyclerView() {
        rvSearchResult.setLayoutManager(new LinearLayoutManager(requireContext()));
        AnimeAdapter adapter = new AnimeAdapter(searchResults, anime -> {
            Intent intent = new Intent(requireContext(), AnimeDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_ANIME_ID, anime.getId());
            intent.putExtra(AppConstants.EXTRA_ANIME, anime);
            startActivity(intent);
        });
        rvSearchResult.setAdapter(adapter);
    }
}
