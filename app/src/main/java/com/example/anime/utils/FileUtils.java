package com.example.anime.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.anime.bean.Anime;
import com.example.anime.bean.Collect;
import com.example.anime.bean.History;
import com.example.anime.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final Gson gson = new Gson();

    private static File getFile(Context context, String fileName) {
        File dir = new File(context.getFilesDir(), "data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, fileName);
    }

    // User Operations
    public static List<User> getUsers(Context context) {
        File file = getFile(context, "users.json");
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            Type type = new TypeToken<List<User>>(){}.getType();
            return gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveUsers(Context context, List<User> users) {
        File file = getFile(context, "users.json");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(users));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(Context context, User user) {
        List<User> users = getUsers(context);
        users.add(user);
        saveUsers(context, users);
    }

    public static User findUserByUsername(Context context, String username) {
        List<User> users = getUsers(context);
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Anime Operations
    public static List<Anime> getAnimes(Context context) {
        File file = getFile(context, "anime.json");
        if (!file.exists()) {
            return new ArrayList<>();
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

    public static void saveAnimes(Context context, List<Anime> animes) {
        File file = getFile(context, "anime.json");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(animes));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Anime findAnimeById(Context context, String animeId) {
        List<Anime> animes = getAnimes(context);
        for (Anime anime : animes) {
            if (anime.getId().equals(animeId)) {
                return anime;
            }
        }
        return null;
    }

    // Collect Operations
    public static List<Collect> getCollects(Context context) {
        File file = getFile(context, "collect.json");
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            Type type = new TypeToken<List<Collect>>(){}.getType();
            return gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveCollects(Context context, List<Collect> collects) {
        File file = getFile(context, "collect.json");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(collects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCollected(Context context, String userId, String animeId) {
        List<Collect> collects = getCollects(context);
        for (Collect collect : collects) {
            if (collect.getUserId().equals(userId) && collect.getAnimeId().equals(animeId)) {
                return true;
            }
        }
        return false;
    }

    public static void addCollect(Context context, String userId, String animeId) {
        if (!isCollected(context, userId, animeId)) {
            List<Collect> collects = getCollects(context);
            collects.add(new Collect(userId, animeId, System.currentTimeMillis()));
            saveCollects(context, collects);
        }
    }

    public static void removeCollect(Context context, String userId, String animeId) {
        List<Collect> collects = getCollects(context);
        collects.removeIf(c -> c.getUserId().equals(userId) && c.getAnimeId().equals(animeId));
        saveCollects(context, collects);
    }

    public static List<Anime> getCollectedAnimes(Context context, String userId) {
        List<Collect> collects = getCollects(context);
        List<Anime> result = new ArrayList<>();
        List<Anime> animes = getAnimes(context);
        for (Collect collect : collects) {
            if (collect.getUserId().equals(userId)) {
                for (Anime anime : animes) {
                    if (anime.getId().equals(collect.getAnimeId())) {
                        result.add(anime);
                        break;
                    }
                }
            }
        }
        return result;
    }

    // History Operations
    public static List<History> getHistories(Context context) {
        File file = getFile(context, "history.json");
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            Type type = new TypeToken<List<History>>(){}.getType();
            return gson.fromJson(sb.toString(), type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveHistories(Context context, List<History> histories) {
        File file = getFile(context, "history.json");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(histories));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addHistory(Context context, String userId, String animeId, int episodeNumber) {
        List<History> histories = getHistories(context);
        // Remove existing history for this anime
        histories.removeIf(h -> h.getUserId().equals(userId) && h.getAnimeId().equals(animeId));
        // Add new history
        histories.add(0, new History(userId, animeId, episodeNumber, System.currentTimeMillis()));
        // Keep only last 100 records
        if (histories.size() > 100) {
            histories = histories.subList(0, 100);
        }
        saveHistories(context, histories);
    }

    public static List<Anime> getHistoryAnimes(Context context, String userId) {
        List<History> histories = getHistories(context);
        List<Anime> result = new ArrayList<>();
        List<Anime> animes = getAnimes(context);
        for (History history : histories) {
            if (history.getUserId().equals(userId)) {
                for (Anime anime : animes) {
                    if (anime.getId().equals(history.getAnimeId())) {
                        result.add(anime);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static void clearHistory(Context context, String userId) {
        List<History> histories = getHistories(context);
        histories.removeIf(h -> h.getUserId().equals(userId));
        saveHistories(context, histories);
    }

    // Avatar Operations
    public static void saveAvatar(Context context, String userId, Bitmap bitmap) {
        File dir = new File(context.getFilesDir(), "avatars");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File avatarFile = new File(dir, userId + ".png");
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(avatarFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAvatarPath(Context context, String userId) {
        File dir = new File(context.getFilesDir(), "avatars");
        File avatarFile = new File(dir, userId + ".png");
        return avatarFile.exists() ? avatarFile.getAbsolutePath() : null;
    }
}
