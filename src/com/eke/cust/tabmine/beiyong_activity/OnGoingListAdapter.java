package com.eke.cust.tabmine.beiyong_activity;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.eke.cust.R;

public class OnGoingListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<OnGoingNodeInfo> mItems;
	private Handler mHandler;

	public OnGoingListAdapter(Context context, List<OnGoingNodeInfo> menuItems) {
		this.context = context;
		this.mItems = menuItems;
				
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
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
			convertView = View.inflate(context, R.layout.layout_mine_ongoing_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
//		viewHolder.tv_xingzhi.setText(mItems.get(index).getHouse_status());
//		viewHolder.tv_jine.setText(mItems.get(index).getPrice());
//		viewHolder.tv_fangyuan.setText(mItems.get(index).getHouse_desc());
//		viewHolder.tv_genfang.setText(mItems.get(index).getHouse_status());
//		viewHolder.tv_qianyue.setText(mItems.get(index).getHouse_number());
//		viewHolder.tv_hetongzhuangtai.setText(mItems.get(index).getFloor());
		
		viewHolder.btn_next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mHandler != null) {
					Message msg = mHandler.obtainMessage();
					msg.what = OnGoingListActivity.MSG_NEXT;
					msg.arg1 = index;
					mHandler.sendMessage(msg);
				}
			}
		});
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_housenumber;
		public TextView tv_fangyuanxinxi;
		public TextView tv_xingzhi;
		public TextView tv_jine;
		public TextView tv_fangyuan;
		public TextView tv_genfang;
		public TextView tv_zhifuzhuangtai;
		public TextView tv_hetongzhuangtai;
		public TextView tv_beizhu;
		public Button btn_next;

		ViewHolder(View v) {
			tv_housenumber = (TextView) v.findViewById(R.id.tv_housenumber);
			tv_fangyuanxinxi = (TextView) v.findViewById(R.id.tv_fangyuanxinxi);
			tv_xingzhi = (TextView) v.findViewById(R.id.tv_xingzhi);
			tv_jine = (TextView) v.findViewById(R.id.tv_jine);
			tv_fangyuan = (TextView) v.findViewById(R.id.tv_fangyuan);
			tv_genfang = (TextView) v.findViewById(R.id.tv_genfang);
			tv_zhifuzhuangtai = (TextView) v.findViewById(R.id.tv_zhifuzhuangtai);
			tv_hetongzhuangtai = (TextView) v.findViewById(R.id.tv_hetongzhuangtai);
			tv_beizhu = (TextView) v.findViewById(R.id.tv_beizhu);
			btn_next = (Button) v.findViewById(R.id.btn_next);
		}
	}
}
