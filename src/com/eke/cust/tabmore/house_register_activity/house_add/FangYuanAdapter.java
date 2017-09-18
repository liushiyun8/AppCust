package com.eke.cust.tabmore.house_register_activity.house_add;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FangYuanAdapter extends BaseAdapter {

	private Context context;
	private List<String> mItems;
	private ImageLoader imageLoader;

	public FangYuanAdapter(Context context, List<String> menuItems) {
		this.context = context;
		this.mItems = menuItems;
		imageLoader = ImageLoader.getInstance();
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
//		if (convertView == null) {
			convertView = View.inflate(context, R.layout.layout_add_house_list_item_fangyuantu, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
		
		if (position == mItems.size()-1) {
			viewHolder.iv_item.setImageResource(R.drawable.fangyuandengji_add);
		}
		else {
			imageLoader.displayImage("file://"+mItems.get(position), viewHolder.iv_item, AppContext.mDisplayImageOptions_no_round_corner);
		}
		
		
		return convertView;
	}

	private class ViewHolder {
		public ImageView iv_item;

		ViewHolder(View v) {
			iv_item = (ImageView) v.findViewById(R.id.iv_item);
		}
	}
}
