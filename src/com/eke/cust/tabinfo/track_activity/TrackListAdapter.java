package com.eke.cust.tabinfo.track_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.tabinfo.FollowNodeInfo;
import com.eke.cust.utils.DensityUtil;

public class TrackListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<FollowNodeInfo> mItems;
	private boolean mIsNeedMargin = false;

	public TrackListAdapter(Context context, List<FollowNodeInfo> menuItems, boolean isNeedMargin) {
		this.context = context;
		this.mItems = menuItems;
		this.mIsNeedMargin = isNeedMargin;
				
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
			convertView = View.inflate(context, R.layout.layout_track_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		FollowNodeInfo node = mItems.get(index);
		
		viewHolder.tv_name.setText(node.getEstatename() + " " + node.getBuildno() + node.getRoomno() + " " + node.getEmpname());		
		viewHolder.tv_content.setText(node.getContent());		
		viewHolder.tv_time.setText("(" + node.getFollowdate().substring(5) + ")");
		
		if(mIsNeedMargin) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.tv_content.getLayoutParams();
			params.leftMargin = DensityUtil.dip2px(context, 30);
			viewHolder.tv_content.setLayoutParams(params);
		}
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_time;
		public TextView tv_name;
		public TextView tv_content;

		ViewHolder(View v) {
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_content = (TextView) v.findViewById(R.id.tv_content);
		}
	}
}
