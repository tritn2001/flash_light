package com.lutech.flashlight.util;

import android.content.Context;
import android.content.SharedPreferences;

public class CheckLoginFirst {
    private final Context context;
    public SharedPreferences sharedPreferences;
    private final String share = "SHARE_PREFS";
    private SharedPreferences.Editor editor;
    public static final String IS_FIRST_OPEN = "IS_FIRST_OPEN";
    public static final String FIST_SET_LANG = "FIST_SET_LANG";

    public CheckLoginFirst(Context context) {
        this.context = context;
    }

    public void setIsFirstOpen(Boolean isFirstOpen) {
        sharedPreferences = context.getSharedPreferences(share, 0);
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_FIRST_OPEN, isFirstOpen);
        editor.apply();
    }

    public Boolean getIsFirstOpen() {
        sharedPreferences = context.getSharedPreferences(share, 0);
        return sharedPreferences.getBoolean(IS_FIRST_OPEN, false);
    }

    public void setFistSetLanguage(Boolean isFirstOpen) {
        sharedPreferences = context.getSharedPreferences(share, 0);
        editor = sharedPreferences.edit();
        editor.putBoolean(FIST_SET_LANG, isFirstOpen);
        editor.apply();
    }

    public Boolean getIsFirsSetLanguage() {
        sharedPreferences = context.getSharedPreferences(share, 0);
        return sharedPreferences.getBoolean(FIST_SET_LANG, false);
    }

}
