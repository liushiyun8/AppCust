package com.eke.cust.tabhouse;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.tabmine.EstateNodeInfo;

public class HouseFilterEstateListAdapter extends BaseAdapter {
	private Context context;
	private List<EstateNodeInfo> mItems;

	public HouseFilterEstateListAdapter(Context context, List<EstateNodeInfo> items) {
		this.context = context;
		this.mItems = items;
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
			convertView = View.inflate(context, R.layout.layout_tab_house_action_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
			
		EstateNodeInfo node = mItems.get(index);
		viewHolder.tv_action.setText(node.getEstatename());
		
		if (node.isSelected()) {
			viewHolder.tv_action.setBackgroundColor(0xffff9e67);
		}
		else {
			viewHolder.tv_action.setBackgroundColor(0xff767676);
		}
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_action;

		ViewHolder(View v) {
			tv_action = (TextView) v.findViewById(R.id.tv_action);
		}
	}
}

