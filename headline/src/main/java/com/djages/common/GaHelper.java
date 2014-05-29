package com.djages.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.djages.headline.CustomApplication;
import com.djages.headline.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by ll298lee on 5/28/14.
 * Helper class for Google Analytics v4
 */
public class GaHelper {
    public enum TrackerName{
        APP_TRACKER
    }
    //static
    public static final String UI_ACTION = "ui_action";
    private static GaHelper sInstance;


    //member
    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();


    public static GaHelper getInstance(){
        if(sInstance == null){
            sInstance = new GaHelper();
        }
        return sInstance;
    }

    public static Tracker getTracker(TrackerName trackerId){
        return getInstance()._getTracker(trackerId);
    }

    public static void sendScreenView(String path){
        Tracker tracker = getTracker(TrackerName.APP_TRACKER);
        tracker.setScreenName(path);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public static void sendEvent(String category, String action, String label){
        Tracker tracker = getTracker(TrackerName.APP_TRACKER);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }


    private GaHelper(){


    }

    private synchronized Tracker _getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(CustomApplication.getInstance());
            boolean isDebuggable = (0 != (CustomApplication.getInstance().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
            if (isDebuggable){
                analytics.setDryRun(true);
            }
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : analytics.newTracker(R.xml.app_tracker);

            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }


}
