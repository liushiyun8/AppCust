package com.eke.cust.tabmore.cehuizhushou_activity;

import java.util.List;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ChzsViewPagerAdapter extends PagerAdapter {
	private static final String TAG = "ViewPagerAdapter";

	private List<View> views = null;
	private ViewPager mViewPager = null;
	private Activity activity;

	public ChzsViewPagerAdapter(List<View> views, ViewPager viewPager, Activity activity) {
		this.views = views;
		this.mViewPager = viewPager;
		this.activity = activity;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
//		((ViewPager) arg0).removeView(views.get(arg1));
		((ViewPager) arg0).removeView((View)arg2);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(View view, int position) {
		((ViewPager) view).addView(views.get(position), 0);
		if (position == views.size() - 1) {

		}
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
	
	@Override
    public void notifyDataSetChanged() {         
          super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {          
          return POSITION_NONE;
    }

}
