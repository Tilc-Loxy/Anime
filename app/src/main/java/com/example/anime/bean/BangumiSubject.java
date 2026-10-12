package com.example.anime.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 番剧数据模型（适配 Kitsu API）
 * Kitsu API 文档: https://kitsu.docs.apiary.io/
 *
 * 原始 JSON 结构:
 * {
 *   "id": "46474",
 *   "attributes": {
 *     "titles": {"en": "...", "ja_jp": "...", "zh_cn": "..."},
 *     "canonicalTitle": "...",
 *     "synopsis": "...",
 *     "averageRating": "88.84",
 *     "popularityRank": 790,
 *     "ratingRank": 2,
 *     "startDate": "2023-09-29",
 *     "posterImage": {"large": "...", "medium": "..."},
 *     "episodeCount": 28,
 *     "status": "finished",
 *     "subtype": "TV"
 *   }
 * }
 */
public class BangumiSubject implements Serializable {

    private String id;

    private String name;          // 原名 (canonicalTitle 或 en)
    private String nameCn;        // 中文名 (titles.zh_cn)
    private String summary;       // 简介 (synopsis)
    private float score;          // 评分 (averageRating / 10)
    private int rank;             // 评分排名 (ratingRank)
    private int popularityRank;   // 人气排名
    private String date;          // 首播日期 (startDate)
    private String status;        // 状态: current/finished/unreleased/upcoming
    private String subtype;       // 类型: TV/OVA/Movie/Special
    private int episodeCount;     // 集数
    private String coverUrl;      // 封面图 (posterImage.large)
    private String youtubeVideoId;// 预告片 YouTube ID

    // ==================== Getters & Setters ====================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameCn() { return nameCn; }
    public void setNameCn(String nameCn) { this.nameCn = nameCn; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public int getPopularityRank() { return popularityRank; }
    public void setPopularityRank(int popularityRank) { this.popularityRank = popularityRank; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubtype() { return subtype; }
    public void setSubtype(String subtype) { this.subtype = subtype; }

    public int getEpisodeCount() { return episodeCount; }
    public void setEpisodeCount(int episodeCount) { this.episodeCount = episodeCount; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getYoutubeVideoId() { return youtubeVideoId; }
    public void setYoutubeVideoId(String youtubeVideoId) { this.youtubeVideoId = youtubeVideoId; }

    // ==================== 辅助方法 ====================

    /** 获取显示名称：优先中文名，其次原名 */
    public String getDisplayName() {
        if (nameCn != null && !nameCn.isEmpty()) {
            return nameCn;
        }
        return name;
    }

    /** 获取状态中文名 */
    public String getStatusCn() {
        if (status == null) return "未知";
        switch (status) {
            case "current": return "连载中";
            case "finished": return "已完结";
            case "unreleased": return "未播出";
            case "upcoming": return "即将播出";
            case "tba": return "待定";
            default: return status;
        }
    }

    /** 获取类型中文名 */
    public String getTypeName() {
        if (subtype == null) return "动画";
        switch (subtype) {
            case "TV": return "TV动画";
            case "OVA": return "OVA";
            case "movie": return "剧场版";
            case "special": return "特别篇";
            case "ONA": return "网络动画";
            case "music": return "音乐";
            default: return subtype;
        }
    }

    /** 评分格式化为 0-10 的字符串 */
    public String getScoreText() {
        if (score <= 0) return "N/A";
        return String.format("%.1f", score);
    }
}
