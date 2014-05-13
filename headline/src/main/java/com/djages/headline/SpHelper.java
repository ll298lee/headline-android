package com.djages.headline;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ll298lee on 5/12/14.
 */
public class SpHelper {
    public static final String KEY_COUNTRY_INDEX = "country_index";
    public static final String KEY_PRESS_TAB_INDEX = "press_tab_index";



    private static SpHelper sInstance = null;
    private SharedPreferences mDefaultSp;

    private SpHelper(){
        mDefaultSp = PreferenceManager
                .getDefaultSharedPreferences(CustomApplication.getAppContext());
    }

    public static SpHelper getInstance(){
        if(sInstance == null){
            sInstance = new SpHelper();
        }
        return sInstance;
    }

    public static String getString(String key, String defaultResult){
        return  getInstance().mDefaultSp.getString(key, defaultResult);
    }

    public static int getInt(String key, int defaultResult){
        return getInstance().mDefaultSp.getInt(key, defaultResult);
    }

    public static boolean getBoolean(String key, boolean defaultResult){
        return getInstance().mDefaultSp.getBoolean(key, defaultResult);
    }

    public static void putString(String key, String input){
        SharedPreferences.Editor editor = getInstance().mDefaultSp.edit();
        editor.putString(key, input);
        editor.commit();
    }

    public static void putInt(String key, int input){
        SharedPreferences.Editor editor = getInstance().mDefaultSp.edit();
        editor.putInt(key, input);
        editor.commit();
    }

    public static void putBoolean(String key, boolean input){
        SharedPreferences.Editor editor = getInstance().mDefaultSp.edit();
        editor.putBoolean(key, input);
        editor.commit();
    }

}
