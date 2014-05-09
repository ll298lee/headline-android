package com.djages.common;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by ll298lee on 5/9/14.
 */
public class Utils {
    /*
    * show toast message
     */
    public static void showToast(Context context, String message){
        Toast toast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, ResolutionHelper.dpToPx(100));
        toast.show();
    }
}
