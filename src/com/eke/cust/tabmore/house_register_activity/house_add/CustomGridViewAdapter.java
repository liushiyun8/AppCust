package com.eke.cust.tabmore.house_register_activity.house_add;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;

public class CustomGridViewAdapter extends BaseAdapter {

	private Context context;
	private String[] mItems;

	public CustomGridViewAdapter(Context context, String[] menuItems) {
		this.context = context;
		this.mItems = menuItems;
				
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
			convertView = View.inflate(context, R.layout.dlg_maidian_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_maidian.setText(mItems[position]);
		
//		if (mItems.get(position).isSelected()) {
//			viewHolder.tv_maidian.setBackgroundResource(R.drawable.bg_maidian_item_selected);
//		}
//		else {
//			viewHolder.tv_maidian.setBackgroundResource(R.drawable.bg_maidian_item);
//		}
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_maidian;

		ViewHolder(View v) {
			tv_maidian = (TextView) v.findViewById(R.id.tv_maidian);
		}
	}
}
