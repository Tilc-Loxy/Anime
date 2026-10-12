package com.example.anime.utils;

import com.example.anime.bean.Anime;
import com.example.anime.bean.Episode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 黑猫动漫 (bmmdmm.com) 网站数据源封装
 * 通过解析网站页面获取动漫资源：搜索、目录浏览、详情、封面图、剧集播放链接
 * 网站页面结构（经抓取验证）：
 *  - 搜索: /s_all?kw=关键词&ex=1
 *  - 目录: /list/?region=日本&label=热血&year=2024&status=连载&order=点击量&pagesize=24&pageindex=0
 *  - 详情: /show/{showId}.html
 *  - 播放: /play/{showId}-{来源}-{集数}.html
 */
public class HeimaoApiService {

    private static final String BASE_URL = "https://www.bmmdmm.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final Pattern SHOW_ID_PATTERN = Pattern.compile("/show/(\\d+)\\.html");

    private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public interface HeimaoCallback<T> {
        void onSuccess(T data);
        void onFailure(String errorMsg);
    }

    /**
     * 搜索动漫
     * @param keyword 关键词
     * @param callback 返回 List<Anime>（含 showId、封面、类型、年份、简介）
     */
    public static void search(String keyword, HeimaoCallback<List<Anime>> callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL + "/s_all").newBuilder()
                .addQueryParameter("kw", keyword)
                .addQueryParameter("ex", "1")
                .build();
        fetchAndParse(url.toString(), callback);
    }

    /**
     * 按网站目录浏览动漫
     * @param region 地区（日本/中国/美国/韩国），可为 null
     * @param label 类型标签（热血/恋爱等），可为 null
     * @param status 状态（连载/完结），可为 null
     * @param year 年份（0 表示不限）
     * @param order 排序（点击量），可为 null
     * @param pageIndex 页码（0 起始）
     */
    public static void getList(String region, String label, String status, int year,
                               String order, int pageIndex, HeimaoCallback<List<Anime>> callback) {
        HttpUrl.Builder b = HttpUrl.parse(BASE_URL + "/list").newBuilder();
        if (region != null && !region.isEmpty()) b.addQueryParameter("region", region);
        if (label != null && !label.isEmpty()) b.addQueryParameter("label", label);
        if (status != null && !status.isEmpty()) b.addQueryParameter("status", status);
        if (year > 0) b.addQueryParameter("year", String.valueOf(year));
        if (order != null && !order.isEmpty()) b.addQueryParameter("order", order);
        b.addQueryParameter("pagesize", "24");
        if (pageIndex > 0) b.addQueryParameter("pageindex", String.valueOf(pageIndex));
        fetchAndParse(b.build().toString(), callback);
    }

    /**
     * 获取动漫详情（封面、评分、地区、类型、年份、简介、剧集播放链接）
     * @param showId 黑猫动漫详情页ID
     * @param callback 返回 Anime（id=showId, episodes 含可播放的 WebView 链接）
     */
    public static void getDetail(String showId, HeimaoCallback<Anime> callback) {
        String url = BASE_URL + "/show/" + showId + ".html";
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
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
                    String html = response.body() != null ? response.body().string() : "";
                    Anime anime = parseDetail(Jsoup.parse(html, BASE_URL), showId);
                    if (anime == null) {
                        callback.onFailure("页面解析失败");
                    } else {
                        callback.onSuccess(anime);
                    }
                } catch (Exception e) {
                    callback.onFailure("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    /** 构建详情页链接 */
    public static String buildShowUrl(String showId) {
        return BASE_URL + "/show/" + showId + ".html";
    }

    /** 构建播放页链接（浏览器直接打开时使用） */
    public static String buildPlayUrl(String showId, int episodeIndex) {
        return BASE_URL + "/play/" + showId + "-0-" + episodeIndex + ".html";
    }

    private static void fetchAndParse(String url, HeimaoCallback<List<Anime>> callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
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
                    String html = response.body() != null ? response.body().string() : "";
                    Document doc = Jsoup.parse(html, BASE_URL);
                    List<Anime> list = parseAnimeList(doc);
                    callback.onSuccess(list);
                } catch (Exception e) {
                    callback.onFailure("数据解析失败: " + e.getMessage());
                }
            }
        });
    }

    /**
     * 解析搜索/目录页的 .lpic li 列表项
     */
    private static List<Anime> parseAnimeList(Document doc) {
        List<Anime> result = new ArrayList<>();
        Elements items = doc.select(".lpic ul li");
        for (Element li : items) {
            try {
                Element link = li.selectFirst("a[href*=/show/]");
                if (link == null) continue;
                Matcher m = SHOW_ID_PATTERN.matcher(link.attr("href"));
                if (!m.find()) continue;

                Anime anime = new Anime();
                anime.setHeimaoShowId(m.group(1));
                anime.setId("hm_" + m.group(1));

                Element img = li.selectFirst("img");
                if (img != null) {
                    anime.setCover(absoluteUrl(img.attr("src")));
                }

                Element titleEl = li.selectFirst("h2 a");
                if (titleEl != null) {
                    String title = titleEl.attr("title");
                    if (title.isEmpty()) title = titleEl.text();
                    anime.setTitle(title.trim());
                }
                if (anime.getTitle() == null || anime.getTitle().isEmpty()) continue;

                // 解析 span：最新集数 / 类型 / 上映日期
                for (Element span : li.select("> span")) {
                    String text = span.text().trim();
                    if (text.startsWith("类型：") || text.startsWith("类型:")) {
                        List<String> types = new ArrayList<>();
                        for (String t : text.substring(3).trim().split("\\s+")) {
                            if (!t.isEmpty()) types.add(t);
                        }
                        anime.setType(types);
                    } else if (text.startsWith("上映：") || text.startsWith("上映:")) {
                        anime.setYear(parseYear(text.substring(3).trim()));
                    }
                }

                Element descEl = li.selectFirst("> p");
                if (descEl != null) {
                    anime.setDescription(truncate(descEl.text().trim(), 150));
                }

                result.add(anime);
            } catch (Exception ignored) {
                // 单个条目解析失败不影响整体
            }
        }
        return result;
    }

    /**
     * 解析详情页：封面/标题/评分/地区/类型/年份/简介/剧集列表
     */
    private static Anime parseDetail(Document doc, String showId) {
        try {
            Anime anime = new Anime();
            anime.setId("hm_" + showId);
            anime.setHeimaoShowId(showId);

            Element thumbImg = doc.selectFirst(".thumb img");
            if (thumbImg != null) {
                anime.setCover(absoluteUrl(thumbImg.attr("src")));
            }

            Element titleEl = doc.selectFirst(".rate h1");
            if (titleEl != null) {
                anime.setTitle(titleEl.text().trim());
            }
            if (anime.getTitle() == null || anime.getTitle().isEmpty()) {
                return null;
            }

            Element scoreEl = doc.selectFirst(".score em");
            if (scoreEl != null) {
                try {
                    anime.setRating(Float.parseFloat(scoreEl.text().trim()));
                } catch (NumberFormatException ignored) {
                }
            }

            // 解析 .sinfo 中带 label 的字段：上映 / 地区 / 类型
            for (Element span : doc.select(".sinfo span, .sinfo p")) {
                Element label = span.selectFirst("label");
                if (label == null) continue;
                String labelText = label.text().trim();
                String value = span.text().replace(labelText, "").trim();
                if (labelText.startsWith("上映")) {
                    anime.setYear(parseYear(value));
                } else if (labelText.startsWith("地区")) {
                    Element regionLink = span.selectFirst("a");
                    if (regionLink != null) {
                        anime.setCountry(regionLink.text().trim());
                    }
                } else if (labelText.startsWith("类型")) {
                    List<String> types = new ArrayList<>();
                    for (Element a : span.select("a")) {
                        String t = a.text().trim();
                        if (!t.isEmpty()) types.add(t);
                    }
                    if (!types.isEmpty()) anime.setType(types);
                }
            }

            Element infoEl = doc.selectFirst(".info");
            if (infoEl != null) {
                anime.setDescription(infoEl.text().trim());
            }

            // 解析剧集列表（取第一个播放源"播放Ⅰ"）
            List<Episode> episodes = new ArrayList<>();
            Element firstSource = doc.selectFirst("div.movurl");
            if (firstSource != null) {
                int index = 0;
                for (Element a : firstSource.select("ul li a")) {
                    String href = a.attr("href");
                    if (href.isEmpty()) continue;
                    String epTitle = a.attr("title");
                    if (epTitle.isEmpty()) epTitle = a.text();
                    episodes.add(new Episode(++index, epTitle, BASE_URL + href));
                }
            }
            anime.setEpisodes(episodes);

            return anime;
        } catch (Exception e) {
            return null;
        }
    }

    private static String absoluteUrl(String src) {
        if (src == null || src.isEmpty()) return "";
        if (src.startsWith("//")) return "https:" + src;
        if (src.startsWith("/")) return BASE_URL + src;
        return src;
    }

    private static int parseYear(String text) {
        if (text == null) return 0;
        Matcher m = Pattern.compile("(19|20)\\d{2}").matcher(text);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group());
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    private static String truncate(String text, int max) {
        if (text == null || text.length() <= max) return text;
        return text.substring(0, max) + "…";
    }
}
