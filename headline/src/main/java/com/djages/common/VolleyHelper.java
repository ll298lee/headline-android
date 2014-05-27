package com.djages.common;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.djages.headline.CustomApplication;

/**
 * Created by ll298lee on 4/17/14.
 */
public class VolleyHelper {
    private static VolleyHelper mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleyHelper(){
        mRequestQueue = Volley.newRequestQueue(CustomApplication.getAppContext());

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(cacheSize);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    public static VolleyHelper getInstance(){
        if(mInstance == null){
            mInstance = new VolleyHelper();
        }
        return mInstance;
    }

    private RequestQueue _getRequestQueue(){
        return mRequestQueue;
    }

    private ImageLoader _getImageLoader(){
        return mImageLoader;
    }

    public static RequestQueue getRequestQueue(){
        return getInstance()._getRequestQueue();
    }

    public static ImageLoader getImageLoader(){
        return getInstance()._getImageLoader();
    }




}
