package com.djages.headline;

import android.app.Application;
import android.content.Context;

import com.djages.common.ResolutionHelper;

/**
 * Created by ll298lee on 4/17/14.
 */
public class CustomApplication extends Application{
    private static CustomApplication sInstance;
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        setAppContext(getApplicationContext());
        ResolutionHelper.init(getApplicationContext());
    }



    public static CustomApplication getInstance(){
        return sInstance;
    }
    public static Context getAppContext() {
        return sAppContext;
    }
    public void setAppContext(Context appContext) {
        sAppContext = appContext;
    }



}
