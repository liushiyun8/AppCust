package com.eke.cust.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 防止与子View 滑动冲突
 */
public class HackyViewPager extends ViewPager {
	public boolean mEnabled = true;

	private static final String TAG = "HackyViewPager";

	public HackyViewPager(Context context) {
		super(context);
	}

	public HackyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return mEnabled ? super.onInterceptTouchEvent(ev) : false;
		} catch (IllegalArgumentException e) {
			// 不理会
			Log.d(TAG, "hacky viewpager error1");
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			// 不理会
			Log.e(TAG, "hacky viewpager error2");
			return false;
		}
	}
	public void setPagingEnabled(boolean enabled) {
		mEnabled = enabled;
	}
}
