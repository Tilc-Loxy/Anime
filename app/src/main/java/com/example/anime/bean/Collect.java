package com.example.anime.bean;

import java.io.Serializable;

public class Collect implements Serializable {
    private String userId;
    private String animeId;
    private long collectTime;

    public Collect() {}

    public Collect(String userId, String animeId, long collectTime) {
        this.userId = userId;
        this.animeId = animeId;
        this.collectTime = collectTime;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAnimeId() { return animeId; }
    public void setAnimeId(String animeId) { this.animeId = animeId; }

    public long getCollectTime() { return collectTime; }
    public void setCollectTime(long collectTime) { this.collectTime = collectTime; }
}
