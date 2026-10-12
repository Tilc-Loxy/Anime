package com.example.anime.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * B站 API 服务封装
 * 用于获取番剧封面图等信息
 */
public class BiliBiliApiService {

    private static final String BASE_URL = "https://api.bilibili.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 10; SM-G975F) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36";
    private static final String REFERER = "https://www.bilibili.com/";

    private static OkHttpClient client;
    private static Gson gson;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    public interface BiliCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMsg);
    }

    /**
     * 获取番剧封面图URL
     * @param seasonId B站番剧season_id
     * @param callback 回调
     */
    public static void getAnimeCover(String seasonId, BiliCallback<String> callback) {
        String url = BASE_URL + "/pgc/view/web/season?season_id=" + seasonId;

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Referer", REFERER)
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

                    int code = json.has("code") ? json.get("code").getAsInt() : -1;
                    if (code != 0) {
                        String message = json.has("message") ? json.get("message").getAsString() : "未知错误";
                        callback.onFailure("B站API错误: " + message);
                        return;
                    }

                    JsonObject result = json.getAsJsonObject("result");
                    if (result != null && result.has("cover") && !result.get("cover").isJsonNull()) {
                        String coverUrl = result.get("cover").getAsString();
                        // B站封面图支持https
                        if (coverUrl.startsWith("http://")) {
                            coverUrl = coverUrl.replace("http://", "https://");
                        }
                        callback.onSuccess(coverUrl);
                    } else {
                        callback.onFailure("未找到封面图");
                    }
                } catch (Exception e) {
                    callback.onFailure("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    /**
     * 搜索番剧，返回第一个结果的season_id和封面图
     * @param keyword 搜索关键词
     * @param callback 回调，返回 String[]{seasonId, coverUrl, title}
     */
    public static void searchBangumi(String keyword, BiliCallback<String[]> callback) {
        String url = BASE_URL + "/x/web-interface/search/type?search_type=media_bangumi&keyword="
                + urlEncode(keyword);

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Referer", REFERER)
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

                    int code = json.has("code") ? json.get("code").getAsInt() : -1;
                    if (code != 0) {
                        callback.onFailure("搜索失败");
                        return;
                    }

                    JsonObject data = json.getAsJsonObject("data");
                    if (data == null || !data.has("result") || data.get("result").isJsonNull()) {
                        callback.onFailure("未找到相关番剧");
                        return;
                    }

                    com.google.gson.JsonArray results = data.getAsJsonArray("result");
                    if (results == null || results.size() == 0) {
                        callback.onFailure("未找到相关番剧");
                        return;
                    }

                    // 取第一个结果
                    JsonObject first = results.get(0).getAsJsonObject();
                    String seasonId = first.has("season_id") ? first.get("season_id").getAsString() : "";
                    String cover = first.has("cover") ? first.get("cover").getAsString() : "";
                    String title = first.has("title") ? first.get("title").getAsString() : keyword;

                    if (cover.startsWith("http://")) {
                        cover = cover.replace("http://", "https://");
                    }

                    callback.onSuccess(new String[]{seasonId, cover, title});
                } catch (Exception e) {
                    callback.onFailure("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    private static String urlEncode(String text) {
        try {
            return java.net.URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {
            return text;
        }
    }
}
