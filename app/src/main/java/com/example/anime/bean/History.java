package com.example.anime.bean;

import java.io.Serializable;

public class History implements Serializable {
    private String userId;
    private String animeId;
    private int episodeNumber;
    private long watchTime;

    public History() {}

    public History(String userId, String animeId, int episodeNumber, long watchTime) {
        this.userId = userId;
        this.animeId = animeId;
        this.episodeNumber = episodeNumber;
        this.watchTime = watchTime;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAnimeId() { return animeId; }
    public void setAnimeId(String animeId) { this.animeId = animeId; }

    public int getEpisodeNumber() { return episodeNumber; }
    public void setEpisodeNumber(int episodeNumber) { this.episodeNumber = episodeNumber; }

    public long getWatchTime() { return watchTime; }
    public void setWatchTime(long watchTime) { this.watchTime = watchTime; }
}
