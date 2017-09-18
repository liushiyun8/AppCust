package com.eke.cust.tabinfo.colleague_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.tabmine.EstateNodeInfo;

public class ColleagueListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
//	private List<ColleagueNodeInfo> mItems;
	private List<EstateNodeInfo> mItems;
	private boolean mIsNeedMargin = false;

	public ColleagueListAdapter(Context context, List<EstateNodeInfo> menuItems, boolean isNeedMargin) {
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
			convertView = View.inflate(context, R.layout.layout_colleague_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
//		viewHolder.iv_head.setText(mItems.get(index).getHead());
//		viewHolder.tv_name.setText(mItems.get(index).getName());
//		viewHolder.tv_gen.setText(mItems.get(index).getGen() + "");
//		viewHolder.tv_tu.setText(mItems.get(index).getTu() + "");
//		viewHolder.tv_da.setText(mItems.get(index).getDa() + "");
//		viewHolder.tv_dai.setText(mItems.get(index).getDai() + "");
		viewHolder.tv_estate.setText(mItems.get(index).getEstatename());
		
		return convertView;
	}

	private class ViewHolder {
//		public ImageView iv_head;
//		public TextView tv_name;
//		public TextView tv_gen;
//		public TextView tv_tu;
//		public TextView tv_da;
//		public TextView tv_dai;
		public TextView tv_estate;

		ViewHolder(View v) {
//			iv_head = (ImageView) v.findViewById(R.id.iv_head);
//			tv_name = (TextView) v.findViewById(R.id.tv_name);
//			tv_gen = (TextView) v.findViewById(R.id.tv_gen);
//			tv_tu = (TextView) v.findViewById(R.id.tv_tu);
//			tv_da = (TextView) v.findViewById(R.id.tv_da);
			tv_estate = (TextView) v.findViewById(R.id.tv_estate);
		}
	}
}
