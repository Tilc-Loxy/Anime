package com.example.anime.utils;

import com.example.anime.bean.BangumiSubject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Kitsu API 服务封装
 * 文档: https://kitsu.docs.apiary.io/
 *
 * 提供三个核心接口:
 * 1. 搜索番剧  - GET /api/edge/anime?filter[text]=keyword
 * 2. 番剧详情  - GET /api/edge/anime/{id}
 * 3. 热门番剧  - GET /api/edge/anime?sort=-popularityRank (替代每日放送)
 */
public class BangumiApiService {

    private static final String BASE_URL = "https://kitsu.io/api/edge";
    private static final String USER_AGENT = "AnimeViewer/1.0";
    private static final String ACCEPT = "application/vnd.api+json";

    private static OkHttpClient client;
    private static Gson gson;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    // ==================== 回调接口 ====================

    public interface BangumiCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMsg);
    }

    // ==================== JSON 解析工具 ====================

    /**
     * 从 Kitsu API 的单个 data 对象解析出 BangumiSubject
     */
    private static BangumiSubject parseKitsuAnime(JsonObject item) {
        BangumiSubject subject = new BangumiSubject();

        try {
            // id
            subject.setId(item.get("id").getAsString());

            JsonObject attr = item.getAsJsonObject("attributes");
            if (attr == null) return subject;

            // 标题
            JsonObject titles = attr.has("titles") && !attr.get("titles").isJsonNull()
                    ? attr.getAsJsonObject("titles") : null;
            String zhCn = null, en = null, jaJp = null;
            if (titles != null) {
                zhCn = getStringSafe(titles, "zh_cn");
                en = getStringSafe(titles, "en");
                jaJp = getStringSafe(titles, "ja_jp");
            }
            String canonical = getStringSafe(attr, "canonicalTitle");

            subject.setNameCn(zhCn);
            // 原名优先日文，其次英文，最后 canonical
            if (jaJp != null && !jaJp.isEmpty()) {
                subject.setName(jaJp);
            } else if (en != null && !en.isEmpty()) {
                subject.setName(en);
            } else {
                subject.setName(canonical);
            }

            // 简介
            subject.setSummary(getStringSafe(attr, "synopsis"));

            // 评分 (averageRating 是 0-100 的字符串，转为 0-10)
            String ratingStr = getStringSafe(attr, "averageRating");
            if (ratingStr != null && !ratingStr.isEmpty()) {
                try {
                    subject.setScore(Float.parseFloat(ratingStr) / 10f);
                } catch (NumberFormatException e) {
                    subject.setScore(0);
                }
            }

            // 排名
            if (attr.has("ratingRank") && !attr.get("ratingRank").isJsonNull()) {
                subject.setRank(attr.get("ratingRank").getAsInt());
            }
            if (attr.has("popularityRank") && !attr.get("popularityRank").isJsonNull()) {
                subject.setPopularityRank(attr.get("popularityRank").getAsInt());
            }

            // 日期
            subject.setDate(getStringSafe(attr, "startDate"));

            // 状态 & 类型
            subject.setStatus(getStringSafe(attr, "status"));
            subject.setSubtype(getStringSafe(attr, "subtype"));

            // 集数
            if (attr.has("episodeCount") && !attr.get("episodeCount").isJsonNull()) {
                subject.setEpisodeCount(attr.get("episodeCount").getAsInt());
            }

            // 封面图
            if (attr.has("posterImage") && !attr.get("posterImage").isJsonNull()) {
                JsonObject poster = attr.getAsJsonObject("posterImage");
                String cover = getStringSafe(poster, "large");
                if (cover == null || cover.isEmpty()) {
                    cover = getStringSafe(poster, "medium");
                }
                if (cover == null || cover.isEmpty()) {
                    cover = getStringSafe(poster, "small");
                }
                subject.setCoverUrl(cover);
            }

            // YouTube 预告片
            subject.setYoutubeVideoId(getStringSafe(attr, "youtubeVideoId"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return subject;
    }

    /** 安全获取字符串值 */
    private static String getStringSafe(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }

    // ==================== 1. 搜索番剧 ====================

    /**
     * 搜索番剧 (Kitsu API)
     * @param keyword 关键词
     * @param limit  返回数量上限
     * @param callback 回调
     */
    public static void searchAnime(String keyword, int limit, BangumiCallback<List<BangumiSubject>> callback) {
        String url = BASE_URL + "/anime?filter[text]=" + urlEncode(keyword)
                + "&page[limit]=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", ACCEPT)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        callback.onFailure("请求失败，状态码: " + response.code());
                        return;
                    }
                    String jsonStr = response.body() != null ? response.body().string() : "";
                    JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
                    JsonArray dataArray = json.getAsJsonArray("data");

                    List<BangumiSubject> subjects = new ArrayList<>();
                    if (dataArray != null) {
                        for (JsonElement element : dataArray) {
                            subjects.add(parseKitsuAnime(element.getAsJsonObject()));
                        }
                    }
                    callback.onSuccess(subjects);
                } catch (Exception e) {
                    callback.onFailure("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    // ==================== 2. 获取番剧详情 ====================

    /**
     * 获取番剧详情 (Kitsu API)
     * @param animeId 番剧ID (Kitsu ID)
     * @param callback  回调
     */
    public static void getSubjectDetail(String animeId, BangumiCallback<BangumiSubject> callback) {
        String url = BASE_URL + "/anime/" + animeId;

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", ACCEPT)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        callback.onFailure("请求失败，状态码: " + response.code());
                        return;
                    }
                    String jsonStr = response.body() != null ? response.body().string() : "";
                    JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
                    JsonObject dataObj = json.getAsJsonObject("data");

                    BangumiSubject subject = parseKitsuAnime(dataObj);
                    callback.onSuccess(subject);
                } catch (Exception e) {
                    callback.onFailure("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    // ==================== 3. 热门番剧 (替代每日放送) ====================

    /**
     * 获取热门番剧榜 (Kitsu API, 按人气排名)
     * 替代原 Bangumi 每日放送功能
     * @param callback 回调
     */
    public static void getDailyCalendar(BangumiCallback<List<DaySchedule>> callback) {
        String url = BASE_URL + "/anime?page[limit]=30&sort=-popularityRank";

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", ACCEPT)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        callback.onFailure("请求失败，状态码: " + response.code());
                        return;
                    }
                    String jsonStr = response.body() != null ? response.body().string() : "";
                    JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
                    JsonArray dataArray = json.getAsJsonArray("data");

                    List<BangumiSubject> allAnime = new ArrayList<>();
                    if (dataArray != null) {
                        for (JsonElement element : dataArray) {
                            allAnime.add(parseKitsuAnime(element.getAsJsonObject()));
                        }
                    }

                    // 按状态分组，模拟"每日放送"的分组展示
                    List<DaySchedule> schedules = new ArrayList<>();

                    DaySchedule current = new DaySchedule();
                    current.weekdayId = 0;
                    current.weekdayEn = "Currently Airing";
                    current.weekdayCn = "连载中";
                    current.subjects = new ArrayList<>();

                    DaySchedule finished = new DaySchedule();
                    finished.weekdayId = 1;
                    finished.weekdayEn = "Finished";
                    finished.weekdayCn = "已完结";
                    finished.subjects = new ArrayList<>();

                    DaySchedule upcoming = new DaySchedule();
                    upcoming.weekdayId = 2;
                    upcoming.weekdayEn = "Upcoming";
                    upcoming.weekdayCn = "即将播出";
                    upcoming.subjects = new ArrayList<>();

                    for (BangumiSubject s : allAnime) {
                        String status = s.getStatus();
                        if ("current".equals(status)) {
                            current.subjects.add(s);
                        } else if ("upcoming".equals(status) || "unreleased".equals(status) || "tba".equals(status)) {
                            upcoming.subjects.add(s);
                        } else {
                            finished.subjects.add(s);
                        }
                    }

                    if (!current.subjects.isEmpty()) schedules.add(current);
                    if (!upcoming.subjects.isEmpty()) schedules.add(upcoming);
                    if (!finished.subjects.isEmpty()) schedules.add(finished);

                    callback.onSuccess(schedules);
                } catch (Exception e) {
                    callback.onFailure("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    // ==================== URL 编码 ====================

    private static String urlEncode(String text) {
        try {
            return java.net.URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {
            return text;
        }
    }

    // ==================== 每日放送数据结构 ====================

    /**
     * 每日放送 - 某一组的番剧列表
     */
    public static class DaySchedule {
        public int weekdayId;
        public String weekdayEn;
        public String weekdayCn;
        public List<BangumiSubject> subjects;
    }
}
