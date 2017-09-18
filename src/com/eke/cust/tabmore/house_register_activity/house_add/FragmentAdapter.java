package com.eke.cust.tabmore.house_register_activity.house_add;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
	List<Fragment> fragmentList = new ArrayList<Fragment>();
	
	public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragmentList.size();
	}

}
