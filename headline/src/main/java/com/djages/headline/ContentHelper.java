package com.djages.headline;


import android.content.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ll298lee on 5/8/14.
 */
public class ContentHelper {

    private static final Map<String, Integer> resIdMap;
    static {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("tw_presses", R.array.tw_presses);
        map.put("us_presses", R.array.us_presses);
        map.put("categories_100", R.array.categories_100);
        map.put("categories_101", R.array.categories_101);
        map.put("categories_102", R.array.categories_102);
        resIdMap = Collections.unmodifiableMap(map);
    }




    private static ContentHelper sInstance = null;
    private String[] mCountryList;
    private String mCountry;

    private String[] mPressList;
    private int mPress;

    private Context mContext;

    private ContentHelper(){
        mContext = CustomApplication.getAppContext();
        mCountryList = mContext.getResources().getStringArray(R.array.country_list);

        //TODO Saved current selection in sp
        _setCountry(0);
    }

    private void _setCountry(int index){
        mCountry = mCountryList[index];
        mPressList = mContext.getResources().getStringArray(resIdMap.get(mCountry));

    }

    private void _setPress(int press){
        mPress = press;
    }


    public static ContentHelper getInstance(){
        if(sInstance == null){
            sInstance = new ContentHelper();
        }
        return sInstance;
    }

    public static String getCountry(){
        return getInstance().mCountry;
    }

    public static void setCountry(int index){
        getInstance()._setCountry(index);
    }

    public static int getPress(){ return getInstance().mPress; }

    public static void setPress(int press) { getInstance()._setPress(press); }

    public static String[] getPressNameList(){
        String [] pressList = getInstance().mPressList;
        String[] names = new String[pressList.length];

        for (int i=0; i<pressList.length;i++){
            names[i] = pressList[i].split(",")[1];
        }
        return names;
    }

    public static int[] getPressCodeList(){
        String [] pressList = getInstance().mPressList;
        int[] codes = new int[pressList.length];

        for (int i=0; i<pressList.length;i++){
            codes[i] = Integer.parseInt(pressList[i].split(",")[0]);
        }
        return codes;
    }

    public static String[] getCategoryList(int pressCode){
        return getInstance().mContext.getResources().getStringArray(resIdMap.get("categories_"+pressCode));

    }



}
