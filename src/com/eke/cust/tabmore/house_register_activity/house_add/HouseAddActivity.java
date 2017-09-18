package com.eke.cust.tabmore.house_register_activity.house_add;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.helper.UIHelper;

public class HouseAddActivity extends BaseActivity  {

	private View mView_view_hor_select_line;
	private ViewPager mViewPager;

	private FragmentAdapter mFragmentAdapter;
	private FragmentAddZhuZhai mFragmentAddZhuZhai;

	private List<Fragment> fragmentList = new ArrayList<Fragment>();

	private int currentIndex;
	private int screenWidth;
	private static final float MenuCount = 4.f;

	public static final int ADD_ZHUZHAI_FANGYUANTU = 100;

	@Override
	protected View onCreateContentView() {
		return inflateContentView(R.layout.activity_tab_more_house_register_add);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("租售委托");
		registerLeftImageView(R.drawable.arrow_back);
		registerRightImageView(R.drawable.icon_house_hitory);
		initActivity();
		initTabLineWidth();

		mViewPager.setEnabled(false);
	}

	private void initActivity() {
		mView_view_hor_select_line = (View) findViewById(R.id.view_hor_select_line);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mFragmentAddZhuZhai = new FragmentAddZhuZhai();
		mFragmentAddZhuZhai.setContextThis(mContext);
		fragmentList.add(mFragmentAddZhuZhai);


		mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
				fragmentList);
		mViewPager.setAdapter(mFragmentAdapter);
		mViewPager.setCurrentItem(0);

		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				currentIndex = position;
			}

			/**
			 * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
			 * offsetPixels:当前页面偏移的像素位置
			 */
			@Override
			public void onPageScrolled(int position, float offset,
					int offsetPixels) {
				// TODO Auto-generated method stub
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mView_view_hor_select_line
						.getLayoutParams();
				if (currentIndex == position) {// 0->1, 1->2, 2->3
					lp.leftMargin = (int) (offset * (screenWidth / MenuCount) + currentIndex
							* (screenWidth / MenuCount));
				} else if (currentIndex != position) {// 1->0, 2->1, 3->2
					lp.leftMargin = (int) (-(1 - offset)
							* (screenWidth / MenuCount) + currentIndex
							* (screenWidth / MenuCount));
				}

				mView_view_hor_select_line.setLayoutParams(lp);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initTabLineWidth() {
		DisplayMetrics dpMetrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(dpMetrics);
		screenWidth = dpMetrics.widthPixels;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mView_view_hor_select_line
				.getLayoutParams();
		lp.width = screenWidth / 4;
		mView_view_hor_select_line.setLayoutParams(lp);
	}

	public void back(View view) {
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String img_url = null;
			if (null != data) {
				img_url = data.getStringExtra("img_url");
			}
			if (img_url == null) {
				return;
			}

			switch (currentIndex) {
			case 0: {
				mFragmentAddZhuZhai.updateFangYuanTu(img_url);
				break;
			}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void goNext() {
		super.goNext();
		UIHelper.startActivity(this, HouseHistoryActivity.class);
	}


}
