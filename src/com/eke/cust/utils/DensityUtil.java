package com.eke.cust.utils;

import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;

import com.eke.cust.AppContext;

public class DensityUtil {

	// private static final String TAG = "DensityUtil";

	private DensityUtil() {
	}
	public static int dpToPx(int val) {
		return (int) TypedValue.applyDimension(1, (float)val, AppContext.getInstance().getResources().getDisplayMetrics());
	}
	public static int dpToPx(Context context, int dp) {
		int px = Math.round(dp * getPixelScaleFactor(context));
		return px;
	}
	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	public static int pxToDp(Context context, int px) {
		int dp = Math.round(px / getPixelScaleFactor(context));
		return dp;
	}

	private static float getPixelScaleFactor(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static int getWidth(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return (view.getMeasuredWidth());
	}

	public static int getHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return (view.getMeasuredHeight());
	}	

    /**
     * 
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view) {
//        int height = view.getMeasuredHeight();
//        if(0 < height){
//            return height;
//        }
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredWidth(View view) {
//        int width = view.getMeasuredWidth();
//        if(0 < width){
//            return width;
//        }
        calcViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 
     *
     * @param view
     */
    public static void calcViewMeasure(View view) {
//        int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int height = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        view.measure(width,height);

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }


}
