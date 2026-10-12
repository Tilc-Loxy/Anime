package com.example.anime.constants;

public class AppConstants {
    // SharedPreferences Keys
    public static final String SP_NAME = "anime_viewer_sp";
    public static final String KEY_IS_LOGIN = "is_login";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_NICKNAME = "nickname";

    // File Names
    public static final String FILE_USERS = "users.json";
    public static final String FILE_ANIME = "anime.json";
    public static final String FILE_COLLECT = "collect.json";
    public static final String FILE_HISTORY = "history.json";

    // Countries
    public static final String COUNTRY_CN = "中国";
    public static final String COUNTRY_JP = "日本";
    public static final String COUNTRY_US = "美国";
    public static final String COUNTRY_KR = "韩国";

    public static final String[] COUNTRIES = {COUNTRY_CN, COUNTRY_JP, COUNTRY_US, COUNTRY_KR};

    // Anime Types
    public static final String TYPE_HOT = "热血";
    public static final String TYPE_LOVE = "恋爱";
    public static final String TYPE_COMEDY = "搞笑";
    public static final String TYPE_SCI_FI = "科幻";
    public static final String TYPE_MYSTERY = "悬疑";
    public static final String TYPE_CAMPUS = "校园";
    public static final String TYPE_ADVENTURE = "冒险";

    public static final String[] ANIME_TYPES = {TYPE_HOT, TYPE_LOVE, TYPE_COMEDY, TYPE_SCI_FI, TYPE_MYSTERY, TYPE_CAMPUS, TYPE_ADVENTURE};

    // Years
    public static final int[] YEARS = {2024, 2023, 2022, 2021, 2020, 2019, 2018};

    // Sort Types
    public static final int SORT_HEAT = 0;
    public static final int SORT_RATING = 1;
    public static final int SORT_NEW = 2;

    // Intent Extra Keys
    public static final String EXTRA_ANIME_ID = "anime_id";
    public static final String EXTRA_ANIME = "anime";
    public static final String EXTRA_EPISODE_NUMBER = "episode_number";
    public static final String EXTRA_SEARCH_KEYWORD = "search_keyword";
    public static final String EXTRA_COUNTRY = "country";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_YEAR = "year";
    public static final String EXTRA_SORT_TYPE = "sort_type";
}
