package com.eke.cust.tabmine.mytask_activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;

public class MyTaskAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private String[] mNumbers;
	private String[] mItems;

	public MyTaskAdapter(Context context, String[] numbers, String[] items) {
		this.context = context;
		this.mNumbers = numbers;
		this.mItems = items;	
	}

	@Override
	public int getCount() {
		return mItems.length;
	}

	@Override
	public Object getItem(int position) {

		return mItems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int index = position;
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.layout_mine_mytask_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_number.setText(mNumbers[index]);
		viewHolder.tv_item.setText(mItems[index]);
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_number;
		public TextView tv_item;

		ViewHolder(View v) {
			tv_number = (TextView) v.findViewById(R.id.tv_number);
			tv_item = (TextView) v.findViewById(R.id.tv_item);
		}
	}
}
