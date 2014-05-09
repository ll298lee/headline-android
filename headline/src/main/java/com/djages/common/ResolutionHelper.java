package com.djages.common;

import android.content.Context;

/**
 * Created by lingwei on 11/3/13.
 */
public class ResolutionHelper {
    public static float sDensity;
    public static int sWidthPixels;
    public static int sHeightPixels;
    public static int sWidthDp;
    public static int sHeightDp;



    public static void init(Context context){
        sDensity = context.getResources().getDisplayMetrics().density;
        sWidthPixels = context.getResources().getDisplayMetrics().widthPixels;
        sHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        sWidthDp = Math.round(sWidthPixels / sDensity);
        sHeightDp = Math.round(sHeightPixels / sDensity);
    }

    public static int dpToPx(int dp){
        return Math.round((float)dp * sDensity);
    }

    public static int[] getImageDimension(int maxWidth, int maxHeight, int width, int height){
        int w, h;

        if(width > maxWidth && height > maxHeight) {
            if(width / (double) maxWidth > height / (double) maxHeight) {
                w = (int) maxWidth;
                h = (int) ((double) maxWidth / width * height);
            } else {
                h = (int) maxHeight;
                w = (int) ((double) maxHeight / height * width);
            }
        } else if(width > maxWidth) {
            w = (int) maxWidth;
            h = (int) ((double) maxWidth / width * height);
        } else if(height > maxHeight) {
            h = (int) maxHeight;
            w = (int) ((double) maxHeight / height * width);
        } else {
            w = width;
            h = height;
        }

        int[] rtn = {w, h};
        return rtn;
    }
}
