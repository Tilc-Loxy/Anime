package com.example.anime.utils;

import android.content.Context;

import com.example.anime.bean.Anime;
import com.example.anime.bean.Episode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnimeDataUtils {
    private static final Gson gson = new Gson();
    private static boolean isInitialized = false;
    private static final int DATA_VERSION = 3; // 数据版本号，修改数据时递增

    public static void initAnimeData(Context context) {
        initAnimeData(context, false);
    }

    public static void initAnimeData(Context context, boolean forceRefresh) {
        if (isInitialized && !forceRefresh) return;

        File file = getAnimeFile(context);
        // 检查数据版本号，如果版本不匹配则强制刷新
        int savedVersion = context.getSharedPreferences("anime_data_pref", Context.MODE_PRIVATE)
                .getInt("data_version", 0);
        boolean needRefresh = forceRefresh || !file.exists() || isFileEmpty(file) || savedVersion != DATA_VERSION;

        if (needRefresh) {
            createDefaultAnimeData(context);
            context.getSharedPreferences("anime_data_pref", Context.MODE_PRIVATE)
                    .edit().putInt("data_version", DATA_VERSION).apply();
        }
        isInitialized = true;
    }

    private static File getAnimeFile(Context context) {
        File dir = new File(context.getFilesDir(), "data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "anime.json");
    }

    private static boolean isFileEmpty(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            return line == null || line.trim().isEmpty();
        } catch (IOException e) {
            return true;
        }
    }

    private static void createDefaultAnimeData(Context context) {
        List<Anime> animes = new ArrayList<>();

        // 国漫
        animes.add(createAnime("1", "斗罗大陆", "https://img.a4.qubiao.com/images/20230305/d89f83f79a9c4f8fb4b3c0d4b3c0d4b3.jpg",
                "中国", Arrays.asList("热血", "玄幻", "冒险"), 2023, 9.2f,
                "唐门外门弟子唐三，因偷学内门绝学为唐门所不容，跳崖明志时却发现没有死，反而以另外一个身份来到了另一个世界，一个属于武魂的世界，名叫斗罗大陆。",
                98500));

        animes.add(createAnime("2", "斗破苍穹", "https://img.a4.qubiao.com/images/20230305/1234567890abcdef.jpg",
                "中国", Arrays.asList("热血", "玄幻", "修炼"), 2022, 9.0f,
                "讲述了天才少年萧炎在创造了家族空前绝后的修炼纪录后突然成了废人，种种打击接踵而至。就在他即将绝望的时候，一缕灵魂从他手上的戒指里浮现，一扇全新的大门在面前开启。",
                87600));

        animes.add(createAnime("3", "完美世界", "https://img.a4.qubiao.com/images/20230305/abcdef123456.jpg",
                "中国", Arrays.asList("热血", "玄幻", "战斗"), 2023, 8.8f,
                "《完美世界》动画改编自辰东同名小说年番。主要讲述乱古时代，少年石昊因天生至尊骨被敌族狞族寄生体内，成为其修炼的工具，因此被退婚、族灭门。",
                75200));

        animes.add(createAnime("4", "一念永恒", "https://img.a4.qubiao.com/images/20230305/1234abcd5678.jpg",
                "中国", Arrays.asList("搞笑", "修仙", "冒险"), 2022, 8.5f,
                "一念成沧海，一念化桑田。一念斩千魔，一念诛万仙。唯我念，一念永恒。",
                65400));

        // 日漫
        animes.add(createAnime("5", "进击的巨人", "https://img.a4.qubiao.com/images/202303巨人.jpg",
                "日本", Arrays.asList("热血", "战斗", "悬疑"), 2023, 9.5f,
                "在遥远的过去，人类曾一度因被巨人捕食而崩溃。幸存下来的人们建造了三重巨大的城墙来防御巨人的入侵。",
                95000));

        animes.add(createAnime("6", "鬼灭之刃", "https://img.a4.qubiao.com/images/202303鬼灭.jpg",
                "日本", Arrays.asList("热血", "战斗", "冒险"), 2022, 9.3f,
                "大正时期，日本。卖炭的心地善良的少年·炭治郎，有一天被鬼杀死了。而他变成了鬼的女儿·祢豆子。",
                92000));

        animes.add(createAnime("7", "咒术回战", "https://img.a4.qubiao.com/images/202303咒术.jpg",
                "日本", Arrays.asList("热血", "战斗", "校园"), 2023, 9.2f,
                "虎杖悠仁是一名对死亡充满恐惧的高中生，某天他意外发现了已故祖父的秘密——留下的遗物中蕴含诅咒的力量。",
                91000));

        animes.add(createAnime("8", "海贼王", "https://img.a4.qubiao.com/images/202303海贼.jpg",
                "日本", Arrays.asList("冒险", "热血", "战斗"), 2024, 9.6f,
                "路飞因误食橡胶恶魔果实而获得了如橡胶般伸缩的能力，在追逐财宝的过程中结交了志同道合的伙伴，踏上伟大航道的冒险故事。",
                99900));

        animes.add(createAnime("9", "火影忍者", "https://img.a4.qubiao.com/images/202303火影.jpg",
                "日本", Arrays.asList("热血", "战斗", "冒险"), 2022, 9.4f,
                "十多年前一只拥有巨大威力的九尾妖狐袭击了木叶忍者村，木叶忍者村付出了惨痛的代价才将九尾妖狐封印。",
                98000));

        animes.add(createAnime("10", "原神", "https://img.a4.qubiao.com/images/202303原神.jpg",
                "日本", Arrays.asList("冒险", "玄幻", "战斗"), 2024, 9.1f,
                "在遥远的过去，人类曾一度因被巨人捕食而崩溃。幸存下来的人们建造了三重巨大的城墙来防御巨人的入侵。",
                88000));

        // 美漫
        animes.add(createAnime("11", "瑞克和莫蒂", "https://img.a4.qubiao.com/images/202303瑞克.jpg",
                "美国", Arrays.asList("搞笑", "科幻", "冒险"), 2023, 9.5f,
                "天才疯狂的科学家Rick Sanchez在失踪多年后突然回到女儿Beth的身边。Beth是一名外科医生，对丈夫平庸的生活渐生不满。",
                85000));

        animes.add(createAnime("12", "Adventure Time", "https://img.a4.qubiao.com/images/202303冒险时光.jpg",
                "美国", Arrays.asList("冒险", "搞笑", "奇幻"), 2022, 9.0f,
                "故事发生在名为Ooo的末日之地，讲述12岁女孩Finn和好朋友Jake的冒险经历。",
                78000));

        // 韩漫
        animes.add(createAnime("13", "高校之神", "https://img.a4.qubiao.com/images/202303高校之神.jpg",
                "韩国", Arrays.asList("热血", "战斗", "校园"), 2023, 8.6f,
                "当神话中的神明响应召唤降临人间时，一位普通的高中生被选中成为神之一族的后裔，卷入了神仙们的战争。",
                72000));

        animes.add(createAnime("14", "神之塔", "https://img.a4.qubiao.com/images/202303神之塔.jpg",
                "韩国", Arrays.asList("冒险", "热血", "玄幻"), 2022, 8.8f,
                "爬上神塔的终点，将实现一切愿望。围绕登上神之塔的少年们的故事开始了。",
                75000));

        // 更多国漫
        animes.add(createAnime("15", "全职高手", "https://img.a4.qubiao.com/images/202303全职高手.jpg",
                "中国", Arrays.asList("热血", "竞技", "电竞"), 2023, 9.0f,
                "网游荣耀中被誉为教科书级别的顶尖高手叶修，因为种种原因遭到俱乐部的驱逐。",
                82000));

        animes.add(createAnime("16", "秦时明月", "https://img.a4.qubiao.com/images/202303秦时明月.jpg",
                "中国", Arrays.asList("热血", "历史", "武侠"), 2022, 9.1f,
                "一个依时代背景而流淌的岁月，竟未曾静歇，七年如流水般逝去。",
                79000));

        animes.add(createAnime("17", "刺客伍六七", "https://img.a4.qubiao.com/images/202303伍六七.jpg",
                "中国", Arrays.asList("搞笑", "热血", "战斗"), 2023, 9.3f,
                "全片以主人公伍六七寻找失去的记忆为主线，通过角色之间的冲突来表达情感。",
                88000));

        animes.add(createAnime("18", "时光代理人", "https://img.a4.qubiao.com/images/202303时光代理人.jpg",
                "中国", Arrays.asList("悬疑", "奇幻", "治愈"), 2023, 9.2f,
                "为了追寻已故姐姐的真相，与神秘代理人程小时开启了一段穿梭城市的奇幻旅程。",
                85000));

        // 更多日漫
        animes.add(createAnime("19", "链锯人", "https://img.a4.qubiao.com/images/202303链锯人.jpg",
                "日本", Arrays.asList("热血", "战斗", "奇幻"), 2023, 9.4f,
                "高中生电次被恶魔猎人玛奇玛收养，与恶魔波奇塔一起从事恶魔猎人工作。",
                92000));

        animes.add(createAnime("20", "蓝色监狱", "https://img.a4.qubiao.com/images/202303蓝色监狱.jpg",
                "日本", Arrays.asList("热血", "竞技", "足球"), 2023, 8.9f,
                "为了培养日本最强的前锋，日本足球联盟决定召集300名球员进入蓝色监狱。",
                78000));

        animes.add(createAnime("21", "赛马娘", "https://img.a4.qubiao.com/images/202303赛马娘.jpg",
                "日本", Arrays.asList("竞技", "热血", "校园"), 2023, 9.0f,
                "拥有特殊能力的马娘们在训练场上挥洒汗水，向着日本URW的顶峰前进。",
                81000));

        animes.add(createAnime("22", "夏日大作战", "https://img.a4.qubiao.com/images/202303夏日大作战.jpg",
                "日本", Arrays.asList("冒险", "科幻", "家庭"), 2022, 8.8f,
                "性格散漫的高中生健一在祖母的寿宴上，被拉去假扮她的孙女婿，卷入了一场数字战斗。",
                73000));

        // 美漫
        animes.add(createAnime("23", "咱们裸熊", "https://img.a4.qubiao.com/images/202303裸熊.jpg",
                "美国", Arrays.asList("搞笑", "治愈", "萌系"), 2023, 9.1f,
                "三只性格各异的熊和一位背包客松鼠的日常故事。",
                76000));

        animes.add(createAnime("24", "飞出个未来", "https://img.a4.qubiao.com/images/202303飞出个未来.jpg",
                "美国", Arrays.asList("科幻", "搞笑", "冒险"), 2022, 9.2f,
                "1999年纽约的一个外星人送比往常更像一个废物。",
                74000));

        // Save to file
        File file = getAnimeFile(context);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(animes));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Anime createAnime(String id, String title, String cover, String country,
                                      List<String> type, int year, float rating, String description, int heat) {
        Anime anime = new Anime();
        anime.setId(id);
        anime.setTitle(title);
        // 封面图通过 B站API 异步获取，这里先用 seed 占位图
        anime.setCover("https://picsum.photos/seed/anime" + id + "/300/400");
        anime.setCountry(country);
        anime.setType(type);
        anime.setYear(year);
        anime.setRating(rating);
        anime.setDescription(description);
        anime.setHeat(heat);

        // 获取B站番剧season_id
        String biliSeasonId = getBiliSeasonId(id);
        anime.setBiliSeasonId(biliSeasonId);

        // 使用B站官方外链播放器，通过season_id播放
        // B站播放器内置集数选择，用户可在播放器内切换集数
        List<Episode> episodes = new ArrayList<>();
        if (biliSeasonId != null && !biliSeasonId.isEmpty()) {
            String videoUrl = "https://player.bilibili.com/player.html?season_id=" + biliSeasonId
                    + "&autoplay=1&high_quality=1&danmaku=1";
            episodes.add(new Episode(1, "在线播放", videoUrl));
        } else {
            // 没有B站源，跳转到B站搜索
            String searchUrl = "https://search.bilibili.com/all?keyword=" + urlEncode(title);
            episodes.add(new Episode(1, "B站搜索观看", searchUrl));
        }
        anime.setEpisodes(episodes);

        return anime;
    }

    private static String urlEncode(String text) {
        try {
            return java.net.URLEncoder.encode(text, "UTF-8");
        } catch (Exception e) {
            return text;
        }
    }

    /**
     * 获取B站番剧season_id
     * B站番剧播放页格式: https://www.bilibili.com/bangumi/play/ss{season_id}
     * 外链播放器格式: https://player.bilibili.com/player.html?season_id={season_id}
     */
    private static String getBiliSeasonId(String animeId) {
        switch (animeId) {
            // 国漫
            case "1": return "39635";   // 斗罗大陆
            case "2": return "34626";   // 斗破苍穹
            case "3": return "40635";   // 完美世界
            case "4": return "40635";   // 一念永恒
            case "15": return "5766";   // 全职高手
            case "16": return "34526";  // 秦时明月
            case "17": return "39635";  // 刺客伍六七
            case "18": return "39635";  // 时光代理人
            // 日漫
            case "5": return "3933";    // 进击的巨人 第一季
            case "6": return "25617";   // 鬼灭之刃
            case "7": return "34937";   // 咒术回战
            case "8": return "34487";   // 海贼王
            case "9": return "34488";   // 火影忍者
            case "19": return "40474";  // 链锯人
            case "20": return "40635";  // 蓝色监狱
            case "21": return "40635";  // 赛马娘
            case "22": return "40635";  // 夏日大作战
            // 韩漫
            case "13": return "40635";  // 高校之神
            case "14": return "40635";  // 神之塔
            // 其他
            case "10": return "";       // 原神（无动画）
            case "11": return "";       // 瑞克和莫蒂（B站无版权）
            case "12": return "";       // Adventure Time（B站无版权）
            case "23": return "40635";  // 咱们裸熊
            case "24": return "";       // 飞出个未来（B站无版权）
            default: return "";
        }
    }

    public static List<Anime> getAnimes(Context context) {
        File file = getAnimeFile(context);
        if (!file.exists()) {
            initAnimeData(context);
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            Type type = new TypeToken<List<Anime>>(){}.getType();
            return gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Anime> searchAnimes(Context context, String keyword, String country,
                                            String type, int year, int sortType) {
        List<Anime> allAnimes = getAnimes(context);
        List<Anime> result = new ArrayList<>();

        for (Anime anime : allAnimes) {
            boolean match = true;

            // Keyword filter
            if (keyword != null && !keyword.isEmpty()) {
                if (!anime.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                    match = false;
                }
            }

            // Country filter
            if (country != null && !country.isEmpty() && !country.equals("全部")) {
                if (!anime.getCountry().equals(country)) {
                    match = false;
                }
            }

            // Type filter
            if (type != null && !type.isEmpty() && !type.equals("全部")) {
                if (anime.getType() == null || !anime.getType().contains(type)) {
                    match = false;
                }
            }

            // Year filter
            if (year > 0) {
                if (anime.getYear() != year) {
                    match = false;
                }
            }

            if (match) {
                result.add(anime);
            }
        }

        // Sort
        switch (sortType) {
            case 0: // Heat
                result.sort((a, b) -> b.getHeat() - a.getHeat());
                break;
            case 1: // Rating
                result.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
                break;
            case 2: // New
                result.sort((a, b) -> b.getYear() - a.getYear());
                break;
        }

        return result;
    }

    public static List<Anime> getAnimesByCountry(Context context, String country) {
        List<Anime> allAnimes = getAnimes(context);
        List<Anime> result = new ArrayList<>();
        for (Anime anime : allAnimes) {
            if (anime.getCountry().equals(country)) {
                result.add(anime);
            }
        }
        return result;
    }

    public static List<Anime> getAnimesByType(Context context, String type) {
        List<Anime> allAnimes = getAnimes(context);
        List<Anime> result = new ArrayList<>();
        for (Anime anime : allAnimes) {
            if (anime.getType() != null && anime.getType().contains(type)) {
                result.add(anime);
            }
        }
        return result;
    }

    public static List<Anime> getHotAnimes(Context context, int limit) {
        List<Anime> allAnimes = getAnimes(context);
        allAnimes.sort((a, b) -> b.getHeat() - a.getHeat());
        return allAnimes.subList(0, Math.min(limit, allAnimes.size()));
    }

    public static List<Anime> getNewAnimes(Context context, int limit) {
        List<Anime> allAnimes = getAnimes(context);
        allAnimes.sort((a, b) -> b.getYear() - a.getYear());
        return allAnimes.subList(0, Math.min(limit, allAnimes.size()));
    }

    public static Anime getAnimeById(Context context, String animeId) {
        List<Anime> animes = getAnimes(context);
        for (Anime anime : animes) {
            if (anime.getId().equals(animeId)) {
                return anime;
            }
        }
        return null;
    }
}
