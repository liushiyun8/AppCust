package com.eke.cust.tabinfo.recentdeal_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eke.cust.R;

public class RecentDealListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<RecentDealNodeInfo> mItems;

	public RecentDealListAdapter(Context context, List<RecentDealNodeInfo> menuItems) {
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
			convertView = View.inflate(context, R.layout.layout_recentdeal_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
//		viewHolder.iv_head.setText(mItems.get(index).getHead());
		viewHolder.tv_signdate.setText(mItems.get(index).getContractdate());
		viewHolder.tv_houseid.setText(mItems.get(index).getPropertyno());
		viewHolder.tv_rent.setText(mItems.get(index).getPrice());
		viewHolder.tv_housenumber.setText("[" + mItems.get(index).getEstatename() + "]" + mItems.get(index).getBuildno() + mItems.get(index).getRoomno());
		viewHolder.tv_panyuan.setText(mItems.get(index).getEkesource());
		viewHolder.tv_genfang.setText(mItems.get(index).getEmpname());
		viewHolder.tv_signer.setText(mItems.get(index).getEmpname());
		
		return convertView;
	}

	private class ViewHolder {
		public ImageView iv_source;
		public TextView tv_signdate;
		public TextView tv_houseid;
		public TextView tv_rent_type;
		public TextView tv_rent;
		public TextView tv_housenumber;
		public TextView tv_panyuan;
		public TextView tv_genfang;
		public TextView tv_signer;

		ViewHolder(View v) {
			iv_source = (ImageView) v.findViewById(R.id.iv_source);
			tv_signdate = (TextView) v.findViewById(R.id.tv_signdate);
			tv_rent_type = (TextView) v.findViewById(R.id.tv_rent_type);
			tv_rent = (TextView) v.findViewById(R.id.tv_rent);
			tv_houseid = (TextView) v.findViewById(R.id.tv_houseid);
			tv_housenumber = (TextView) v.findViewById(R.id.tv_housenumber);
			tv_panyuan = (TextView) v.findViewById(R.id.tv_panyuan);
			tv_genfang = (TextView) v.findViewById(R.id.tv_genfang);
			tv_signer = (TextView) v.findViewById(R.id.tv_signer);
		}
	}
}
