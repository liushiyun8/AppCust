package com.eke.cust.tabhouse.xianshifankui_activity;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.utils.DateUtil;

public class FanKuiListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<FanKuiNodeInfo> mItems;
	private Handler mHandler;

	public FanKuiListAdapter(Context context, Handler handler, List<FanKuiNodeInfo> menuItems) {
		this.context = context;
		this.mItems = menuItems;
		this.mHandler = handler;
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
			convertView = View.inflate(context, R.layout.layout_house_fankui_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FanKuiNodeInfo node = mItems.get(index);
		viewHolder.tv_baogaoren.setText(node.getEmpname() + "\n" + node.getEmpno());
		String baogao_time = DateUtil.getDateToString(node.getAssigndate());
		if (baogao_time.length() > 12) {
			viewHolder.tv_baogao_time.setText(baogao_time.substring(0, 10) + "\n" + baogao_time.substring(11));
		}
		
		viewHolder.tv_content.setText(node.getContent());
		
		String fankui_time = DateUtil.getDateToString(node.getScheduledate());
		if (fankui_time.length() > 12) {
			viewHolder.tv_fankui_time.setText(fankui_time.substring(0, 10) + "\n" + fankui_time.substring(11));
		}
		
		if (node.isSelected()) {
			viewHolder.checkbox.setChecked(true);
		}
		else {
			viewHolder.checkbox.setChecked(false);
		}
		viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mHandler != null) {
					Message msg = mHandler.obtainMessage();
					msg.what = XianshifankuiActivity.MSG_CHECK_CHANGED;
					msg.arg1 = index;
					mHandler.sendMessage(msg);
				}
			}
		});
		
		
		return convertView;
	}

	private class ViewHolder {
		public CheckBox checkbox;
		public TextView tv_baogaoren;
		public TextView tv_baogao_time;
		public TextView tv_content;
		public TextView tv_fankui_time;

		ViewHolder(View v) {
			checkbox = (CheckBox)v.findViewById(R.id.checkbox);
			tv_baogaoren = (TextView) v.findViewById(R.id.tv_baogaoren);
			tv_baogao_time = (TextView) v.findViewById(R.id.tv_baogao_time);
			tv_content = (TextView) v.findViewById(R.id.tv_content);
			tv_fankui_time = (TextView) v.findViewById(R.id.tv_fankui_time);
		}
	}
}