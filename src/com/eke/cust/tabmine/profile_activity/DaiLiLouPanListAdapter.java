package com.eke.cust.tabmine.profile_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;

public class DaiLiLouPanListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<EstateNodeInfo> mItems;

	public DaiLiLouPanListAdapter(Context context, List<EstateNodeInfo> items) {
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
			convertView = View.inflate(context, R.layout.layout_mine_profile_daililoupan_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_item.setText((position+1) + "、" + mItems.get(index).getEstatename());
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_item;

		ViewHolder(View v) {
			tv_item = (TextView) v.findViewById(R.id.tv_item);
		}
	}
}
