package com.eke.cust.launch;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by wujian on 2017/4/3.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private List<View> views;

    public ViewPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int arg1, Object arg2) {
        viewGroup.removeView(views.get(arg1));
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int arg1) {
        viewGroup.addView(views.get(arg1), 0);
        return views.get(arg1);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

}
