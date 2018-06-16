package com.joy.dl.daiandroidtools;

import android.content.Context;

/**
 * Created by dl on 2018/06/16 0016.
 */

public class UITools {

    public static int dip2px(float dpValue) {
        float scale = DApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    public static int px2dip(float pxValue) {
        float scale = DApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static int getScreenWidth() {
        return DApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return DApplication.getInstance().getResources().getDisplayMetrics().heightPixels;
    }
}
