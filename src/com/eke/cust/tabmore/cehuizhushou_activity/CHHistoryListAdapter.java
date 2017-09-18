package com.eke.cust.tabmore.cehuizhushou_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.utils.DateUtil;

public class CHHistoryListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<CHHistoryNodeInfo> mItems;

	public CHHistoryListAdapter(Context context, List<CHHistoryNodeInfo> menuItems, boolean isNeedMargin) {
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
			convertView = View.inflate(context, R.layout.layout_more_chzs_history_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		CHHistoryNodeInfo node = mItems.get(index);
		
		viewHolder.tv_loupan.setText(node.getEstatename());
		viewHolder.tv_tijiaoshijian.setText(DateUtil.getDateToString(node.getRevtime()));		
		viewHolder.tv_tupian.setText("");		
		viewHolder.tv_zhuangtai.setText(node.getStatus());
		viewHolder.tv_houtaiyijian.setText(node.getReviewword());
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_loupan;
		public TextView tv_tijiaoshijian;
		public TextView tv_tupian;
		public TextView tv_zhuangtai;
		public TextView tv_houtaiyijian;

		ViewHolder(View v) {
			tv_loupan = (TextView) v.findViewById(R.id.tv_loupan);
			tv_tijiaoshijian = (TextView) v.findViewById(R.id.tv_tijiaoshijian);
			tv_tupian = (TextView) v.findViewById(R.id.tv_tupian);
			tv_zhuangtai = (TextView) v.findViewById(R.id.tv_zhuangtai);
			tv_houtaiyijian = (TextView) v.findViewById(R.id.tv_houtaiyijian);
		}
	}
}
