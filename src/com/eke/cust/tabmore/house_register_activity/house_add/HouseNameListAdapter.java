package com.eke.cust.tabmore.house_register_activity.house_add;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;

public class HouseNameListAdapter extends BaseAdapter {
	private Context context;
	private List<HouseNameNodeInfo> mItems;

	public HouseNameListAdapter(Context context, List<HouseNameNodeInfo> menuItems) {
		this.context = context;
		this.mItems = menuItems;
				
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {

		return mItems.get(position);
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
			convertView = View.inflate(context, R.layout.layout_house_add_name_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		HouseNameNodeInfo node = mItems.get(index);
		
		viewHolder.tv_name.setText(node.getEstatename());
		
		if(node.isSelected()) {
			viewHolder.tv_name.setBackgroundColor(0xffb2b2fe);
		}
		else {
			viewHolder.tv_name.setBackgroundColor(0xffffffff);
		}
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_name;

		ViewHolder(View v) {
			tv_name = (TextView) v.findViewById(R.id.tv_name);
		}
	}
}
