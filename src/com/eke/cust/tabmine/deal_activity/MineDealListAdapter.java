package com.eke.cust.tabmine.deal_activity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.utils.DateUtil;

public class MineDealListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<MyDealNodeInfo> mItems;

	public MineDealListAdapter(Context context, List<MyDealNodeInfo> menuItems) {
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
			convertView = View.inflate(context, R.layout.layout_mine_deal_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		MyDealNodeInfo node = mItems.get(index);
		
		viewHolder.tv_housenumber.setText(node.getPropertyno());
		viewHolder.tv_fangyuanxinxi.setText(node.getEstatename() + " " + node.getBuildno() + node.getRoomno());
		viewHolder.tv_zudanchengjiao.setText(node.getPrice() + "");
//		viewHolder.tv_shuifei.setText(node.getPropertytax());
		viewHolder.tv_yezhuzhanghao.setText("");
		viewHolder.tv_panyuan.setText(node.getEkesource());
		viewHolder.tv_genfang.setText(node.getEmpname());
		viewHolder.tv_qianyue.setText(node.getEmpname());
		viewHolder.tv_qianyueriqi.setText(DateUtil.getDateToString1(node.getContractdate()));
		viewHolder.tv_hetong_status.setText(node.getStatus());
		viewHolder.tv_beizhu.setText(node.getRemark());
		
		return convertView;
	}

	private class ViewHolder {
		public TextView tv_housenumber;
		public TextView tv_fangyuanxinxi;
		public TextView tv_zudanchengjiao;
		public TextView tv_shuifei;
		public TextView tv_yezhuzhanghao;
		public TextView tv_panyuan;
		public TextView tv_genfang;
		public TextView tv_qianyue;
		public TextView tv_qianyueriqi;
		public TextView tv_hetong_status;
		public TextView tv_beizhu;

		ViewHolder(View v) {
			tv_housenumber = (TextView) v.findViewById(R.id.tv_housenumber);
			tv_fangyuanxinxi = (TextView) v.findViewById(R.id.tv_fangyuanxinxi);
			tv_zudanchengjiao = (TextView) v.findViewById(R.id.tv_zudanchengjiao);
			tv_shuifei = (TextView) v.findViewById(R.id.tv_shuifei);
			tv_yezhuzhanghao = (TextView) v.findViewById(R.id.tv_yezhuzhanghao);
			tv_panyuan = (TextView) v.findViewById(R.id.tv_panyuan);
			tv_genfang = (TextView) v.findViewById(R.id.tv_genfang);
			tv_qianyue = (TextView) v.findViewById(R.id.tv_qianyue);
			tv_qianyueriqi = (TextView) v.findViewById(R.id.tv_qianyueriqi);
			tv_hetong_status = (TextView) v.findViewById(R.id.tv_hetong_status);
			tv_beizhu = (TextView) v.findViewById(R.id.tv_beizhu);
		}
	}
}
