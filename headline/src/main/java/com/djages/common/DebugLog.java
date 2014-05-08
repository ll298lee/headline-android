package com.djages.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.djages.headline.CustomApplication;

/**
 * Created by ll298lee on 5/2/14.
 */
public class DebugLog {
    private static DebugLog sInstance = null;
    private boolean mIsDebuggable = false;

    private DebugLog(boolean isDebuggable){
        mIsDebuggable = isDebuggable;
    }

    private boolean isDebuggable(){
        return mIsDebuggable;
    }

    public static DebugLog getInstance(){
        if(sInstance == null){
            Context context = CustomApplication.getAppContext();
            boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
            sInstance = new DebugLog(isDebuggable);
        }
        return sInstance;
    }

    public static int d(Object obj, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.d(obj.getClass().getSimpleName(), msg);
        }
        return 0;
    }

    public static int e(Object obj, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.e (obj.getClass().getSimpleName(), msg);
        }
        return 0;
    }

    public static int i(Object obj, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.i(obj.getClass().getSimpleName(), msg);
        }
        return 0;
    }

    public static int v(Object obj, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.v(obj.getClass().getSimpleName(), msg);
        }
        return 0;
    }

    public static int w(Object obj, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.w(obj.getClass().getSimpleName(), msg);
        }
        return 0;
    }

    public static int d(String str, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.d (str, msg);
        }
        return 0;
    }

    public static int e(String str, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.e (str, msg);
        }
        return 0;
    }

    public static int i(String str, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.i (str, msg);
        }
        return 0;
    }

    public static int v(String str, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.v(str, msg);
        }
        return 0;
    }

    public static int w(String str, String msg){
        if(getInstance().isDebuggable()){
            return android.util.Log.w(str, msg);
        }
        return 0;
    }



}
