package com.eke.cust.tabhouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.global.ColorBorderDrawable;

public class MaidianGridViewAdapter extends BaseAdapter {
	protected static final String TAG = null;
	private Context context;
	private List<MaidianNodeInfo> mItems;
	private List<ColorBorderDrawable> mBgColors = new ArrayList<ColorBorderDrawable>();
	private ColorBorderDrawable mBgSelected;
	private HashMap<Integer,Boolean> hashMap=new HashMap<>();

	public MaidianGridViewAdapter(Context context, List<MaidianNodeInfo> menuItems) {
		this.context = context;
		this.mItems = menuItems;
		for (int i = 0; i < mItems.size(); i++) {
			mBgColors.add(new ColorBorderDrawable(false));
		}
		mBgSelected = new ColorBorderDrawable(true);
	}
	public void updateBgColors(int size) {
		mItems.clear();
		for (int i = 0; i < size; i++) {
			mBgColors.add(new ColorBorderDrawable(false));
		}
	}
	public void updateBgColors(String select[]) {
		for (int i = 0; i <mItems.size() ; i++) {
			String detail=mItems.get(i).detail;
			for (int j = 0; j < select.length; j++) {
				if(detail.equals(select[j])){
					mBgColors.add(new ColorBorderDrawable(true));
					mItems.get(i).isSelected=true;
				}
			}
		}
		notifyDataSetChanged();
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
			convertView = View.inflate(context, R.layout.dlg_maidian_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tv_maidian.setText(mItems.get(position).getDetail());

		if (mItems.get(position).isSelected()) {
			viewHolder.tv_maidian.setBackgroundDrawable(mBgSelected);
		}
		else {
			viewHolder.tv_maidian.setBackgroundDrawable(mBgColors.get(index));
		}

		return convertView;
	}

	private class ViewHolder {
		public TextView tv_maidian;

		ViewHolder(View v) {
			tv_maidian = (TextView) v.findViewById(R.id.tv_maidian);
		}
	}
}
