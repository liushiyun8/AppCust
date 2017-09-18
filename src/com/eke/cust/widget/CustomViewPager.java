package com.eke.cust.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager{

	public CustomViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		try
	    {
	        return super.onInterceptTouchEvent(event);
	    }
	    catch (IllegalArgumentException e)
	    {
	    }
	    catch (ArrayIndexOutOfBoundsException e)
	    {

	    }
	    return false;
	}
	
	
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		return false;
////		return super.dispatchTouchEvent(ev);
//	}
//
//	@Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        try {
//            return super.onTouchEvent(ev);
//        } catch (IllegalArgumentException ex) {
//            // ignore it
//        }
//        return false;
//    }
	
}
