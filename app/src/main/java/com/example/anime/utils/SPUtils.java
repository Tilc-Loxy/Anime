package com.example.anime.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.anime.constants.AppConstants;

public class SPUtils {
    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(AppConstants.SP_NAME, Context.MODE_PRIVATE);
    }

    public static void setLogin(Context context, boolean isLogin) {
        getSp(context).edit().putBoolean(AppConstants.KEY_IS_LOGIN, isLogin).apply();
    }

    public static boolean isLogin(Context context) {
        return getSp(context).getBoolean(AppConstants.KEY_IS_LOGIN, false);
    }

    public static void saveUserId(Context context, String userId) {
        getSp(context).edit().putString(AppConstants.KEY_USER_ID, userId).apply();
    }

    public static String getUserId(Context context) {
        return getSp(context).getString(AppConstants.KEY_USER_ID, "");
    }

    public static void saveUsername(Context context, String username) {
        getSp(context).edit().putString(AppConstants.KEY_USERNAME, username).apply();
    }

    public static String getUsername(Context context) {
        return getSp(context).getString(AppConstants.KEY_USERNAME, "");
    }

    public static void saveNickname(Context context, String nickname) {
        getSp(context).edit().putString(AppConstants.KEY_NICKNAME, nickname).apply();
    }

    public static String getNickname(Context context) {
        return getSp(context).getString(AppConstants.KEY_NICKNAME, "");
    }

    public static void clearUser(Context context) {
        SharedPreferences.Editor editor = getSp(context).edit();
        editor.remove(AppConstants.KEY_IS_LOGIN);
        editor.remove(AppConstants.KEY_USER_ID);
        editor.remove(AppConstants.KEY_USERNAME);
        editor.remove(AppConstants.KEY_NICKNAME);
        editor.apply();
    }
}
