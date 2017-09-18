package com.eke.cust.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by wfpb on 15/7/7. TabBar
 */
public class TabBar extends RelativeLayout {

	public interface TabBarClickListener {

		void onClickIndex(int index);
	}

	protected int _index = -1;

	public void selectIndex(int index, boolean notify) {
		_index = index;

		if (notify) {
			onClickIndex(index);
		}
	}

	protected TabBarClickListener _tabBarClickListener;

	public void setTabBarClickListener(TabBarClickListener listener) {
		_tabBarClickListener = listener;
	}

	public TabBar(Context context) {
		super(context);
		initWithContext(context);
	}

	public TabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public TabBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initWithContext(context);
	}

	protected void initWithContext(Context context) {
	}

	protected void onClickIndex(int index) {

		if (index == _index)
			return;
		_index = index;

		if (_tabBarClickListener != null) {
			_tabBarClickListener.onClickIndex(index);
		}
	}
}
