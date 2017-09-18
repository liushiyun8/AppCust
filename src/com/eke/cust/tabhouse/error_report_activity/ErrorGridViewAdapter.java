package com.eke.cust.tabhouse.error_report_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;

public class ErrorGridViewAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<ErrorNodeInfo> mItems;

	public ErrorGridViewAdapter(Context context, List<ErrorNodeInfo> menuItems) {
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
			convertView = View.inflate(context, R.layout.layut_house_info_error_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_error.setText(mItems.get(position).getName());
		
		if (mItems.get(position).isSelected()) {
			viewHolder.tv_error.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.btn_radio_on_pressed), null, null, null);
		}
		else {
			viewHolder.tv_error.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.btn_radio_off), null, null, null);
		}
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_error;

		ViewHolder(View v) {
			tv_error = (TextView) v.findViewById(R.id.tv_error);
		}
	}
}
