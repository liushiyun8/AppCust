package com.eke.cust.tabinfo.message_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eke.cust.R;

public class MessageActivityMessageListAdapter extends BaseAdapter {

	protected static final String TAG = "MessageActivityMessageListAdapter";
	private Context context;
	private List<MessageActivityMessageNodeInfo> mItems;

	public MessageActivityMessageListAdapter(Context context, List<MessageActivityMessageNodeInfo> menuItems) {
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
			convertView = View.inflate(context, R.layout.layout_msg_activity_msg_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.iv_header.setImageBitmap(mItems.get(index).getBitmap());
		viewHolder.tv_name.setText(mItems.get(index).getName());
		viewHolder.tv_msg_num.setText(mItems.get(index).getNum1() + "/" + mItems.get(index).getNum2());
		
		return convertView;
	}

	private class ViewHolder {
		public ImageView iv_header;
		public TextView tv_name;
		public TextView tv_msg_num;

		ViewHolder(View v) {
			iv_header = (ImageView) v.findViewById(R.id.iv_header);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_msg_num = (TextView) v.findViewById(R.id.tv_msg_num);
		}
	}
}
