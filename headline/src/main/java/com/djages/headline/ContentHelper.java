package com.djages.headline;


import android.content.Context;

import com.djages.common.DebugLog;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ll298lee on 5/8/14.
 */
public class ContentHelper {

    private static final Map<String, Integer> resIdMap;
    private static final Map<String, Integer> countryCodeMap;
    static {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("tw_presses", R.array.tw_presses);
        map.put("us_presses", R.array.us_presses);


        map.put("categories_15800", R.array.categories_15800);
        map.put("categories_15801", R.array.categories_15801);
        map.put("categories_15802", R.array.categories_15802);
        map.put("categories_15803", R.array.categories_15803);
        map.put("categories_15804", R.array.categories_15804);
        map.put("categories_15805", R.array.categories_15805);
        map.put("categories_15806", R.array.categories_15806);
        map.put("categories_15807", R.array.categories_15807);

        resIdMap = Collections.unmodifiableMap(map);

        Map<String, Integer> countryMap = new HashMap<String, Integer>();
        map.put("TW", 0);
        map.put("US",1);
        countryCodeMap = Collections.unmodifiableMap(countryMap);
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

        //get current selection in sp


        String country = mContext.getResources().getConfiguration().locale.getCountry();
        DebugLog.v(this, "country of the device: "+ country);
        int defaultCountry = 0;
        if(countryCodeMap.containsKey(country)){
            defaultCountry = countryCodeMap.get(country);
        }

        int countryIndex = SpHelper.getInt(SpHelper.KEY_COUNTRY_INDEX, defaultCountry);
        DebugLog.v(this, "country index in constructor: "+Integer.toString(countryIndex));
        _setCountry(countryIndex);
    }

    private void _setCountry(int index){
        mCountry = mCountryList[index];
        mPressList = mContext.getResources().getStringArray(resIdMap.get(mCountry));
        DebugLog.v(this, "country index: "+ Integer.toString(index));
        SpHelper.putInt(SpHelper.KEY_COUNTRY_INDEX, index);
        DebugLog.v(this, "country index saved: "+ Integer.toString(SpHelper.getInt(SpHelper.KEY_COUNTRY_INDEX,0)));
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
