package com.example.anime.bean;

import java.io.Serializable;
import java.util.List;

public class Anime implements Serializable {
    private String id;
    private String title;
    private String cover;
    private String country;
    private List<String> type;
    private int year;
    private float rating;
    private String description;
    private List<Episode> episodes;
    private int heat;
    private String biliSeasonId;  // B站番剧season_id，用于播放和获取封面
    private String heimaoShowId;  // 黑猫动漫(bmmdmm.com)详情页ID，用于在线资源和封面

    public Anime() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public List<String> getType() { return type; }
    public void setType(List<String> type) { this.type = type; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Episode> getEpisodes() { return episodes; }
    public void setEpisodes(List<Episode> episodes) { this.episodes = episodes; }

    public int getHeat() { return heat; }
    public void setHeat(int heat) { this.heat = heat; }

    public String getBiliSeasonId() { return biliSeasonId; }
    public void setBiliSeasonId(String biliSeasonId) { this.biliSeasonId = biliSeasonId; }

    public String getHeimaoShowId() { return heimaoShowId; }
    public void setHeimaoShowId(String heimaoShowId) { this.heimaoShowId = heimaoShowId; }
}
