package com.eke.cust.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eke.cust.R;

import java.util.List;

import foundation.widget.imageview.CircleImageView;


public class MessageAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	List<String> listdata;

	public MessageAdapter(Context context, List<String> listdata) {
		super();
		this.context = context;
		this.listdata = listdata;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listdata.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		viewHolder holder = null;
		if (convertView == null) {
			holder = new viewHolder();
			convertView = inflater.inflate(R.layout.message_item, null);
			holder.img = (CircleImageView) convertView
					.findViewById(R.id.msg_item_title);
			holder.name = (TextView) convertView
					.findViewById(R.id.msg_item_name);
			holder.lin = (LinearLayout) convertView
					.findViewById(R.id.msg_item_hang);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.img.setImageResource(R.drawable.people);
		} else if (position == 1) {
			holder.img.setImageResource(R.drawable.kefu);
		}
		holder.name.setText(listdata.get(position));
		holder.lin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// FIXME ��ת������
				// TODO Auto-generated method stub
				// Intent intent = new Intent(context, ChatActivity.class);
				//
				// intent.putExtra("userId", listdata.get(position));
				// context.startActivity(intent);
			}
		});
		return convertView;
	}

	class viewHolder {
		private CircleImageView img;
		private TextView name;
		private LinearLayout lin;
	}
}
