package com.eke.cust.tabmine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eke.cust.R;

public class MineListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private int[] mIcons;
	private String[] mItems;

	public MineListAdapter(Context context, int[] icons, String[] items) {
		this.context = context;
		this.mIcons = icons;
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
			convertView = View.inflate(context, R.layout.layout_mine_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.iv_item.setImageResource(mIcons[index]);
		viewHolder.tv_item.setText(mItems[index]);
		
		return convertView;
	}

	private class ViewHolder {
		public ImageView iv_item;
		public TextView tv_item;

		ViewHolder(View v) {
			iv_item = (ImageView) v.findViewById(R.id.iv_item);
			tv_item = (TextView) v.findViewById(R.id.tv_item);
		}
	}
}
