package com.eke.cust.utils;

/**
 * Created by  on 15/6/25.
 */
public class ClickUtils {

    private static long mLastClickTime = 0;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if ( 0 < timeD && timeD < 1000) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }

    private static long mLastLikeClickTime = 0;
    public static boolean isSubmitLikes() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastLikeClickTime;
        if (timeD > 1000) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }


}
