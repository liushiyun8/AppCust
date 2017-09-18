package com.eke.cust.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eke.cust.R;

public class MenuAdapter extends BaseAdapter {
	Context context;
	LayoutInflater inflater;
	public List<MenuEntity> datalist = new ArrayList<MenuEntity>();

	public MenuAdapter(Context context, List<MenuEntity> datalist) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.datalist = datalist;
	}

	public final class ViewHolder {
		public TextView tv;
		public ImageView iv1;
		public ImageView iv2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.menulisttext, null);
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
			holder.iv2 = (ImageView) convertView.findViewById(R.id.iv2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MenuEntity menuentity = datalist.get(position);
		holder.tv.setText(menuentity.text);
		holder.iv1.setImageResource(menuentity.image);
		return convertView;
	}

	public void addWdItem(MenuEntity branch) {
		datalist.add(branch);
	}

	public void resetData() {
		// datalist.clear();
		notifyDataSetChanged();
	}

	public int getCount() {
		// return datalist.size();
		return datalist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public static class MenuEntity {
		public int image;
		public String text;

		public MenuEntity(int image, String text) {
			this.image = image;
			this.text = text;
		}

	}
}
