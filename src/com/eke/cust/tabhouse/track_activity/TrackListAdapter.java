package com.eke.cust.tabhouse.track_activity;

import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.utils.MyLog;

public class TrackListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<TrackNodeInfo> mItems;

	public TrackListAdapter(Context context, List<TrackNodeInfo> menuItems) {
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
			convertView = View.inflate(context, R.layout.layout_house_track_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_delete.setVisibility(View.GONE);
		viewHolder.tv_time.setText(mItems.get(index).getFollowdate());
		viewHolder.tv_name.setText(mItems.get(index).getEmpname());
		viewHolder.tv_content.setText(mItems.get(index).getContent());
		
		convertView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					HouseTrackActivity.downPointX = event.getX();
					MyLog.d("--", "x = " + event.getX());
					HouseTrackActivity.view_clicked = v;
				}
				return false;
			}
		});
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_time;
		public TextView tv_name;
		public TextView tv_content;
		public TextView tv_delete;

		ViewHolder(View v) {
			tv_time = (TextView) v.findViewById(R.id.tv_time);
			tv_name = (TextView) v.findViewById(R.id.tv_name);
			tv_content = (TextView) v.findViewById(R.id.tv_content);
			tv_delete = (TextView)v.findViewById(R.id.track_delete);
		}
	}
}