package com.eke.cust.tabhouse;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.tabhouse.view_image_activity.ViewImageActivity;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DateUtil;
import com.eke.cust.utils.GlobalSPA;

public class HouseSourceListAdapter extends BaseAdapter {

	protected static final String TAG = null;
	private Context context;
	private List<HouseSourceNodeInfo> mItems;
	private boolean mIsRent = true;
	private boolean mIsForCollect = false;

	public HouseSourceListAdapter(Context context,
			List<HouseSourceNodeInfo> menuItems, boolean isrent) {
		this.context = context;
		this.mItems = menuItems;
		this.mIsRent = isrent;
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
			convertView = View.inflate(context,
					R.layout.layout_house_source_list_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final HouseSourceNodeInfo node = mItems.get(index);

		if (node.getEkesurvey() != null) {
			if (node.getEkesurvey().equals("null")) {
				viewHolder.rl_is_checked.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.rl_is_checked.setVisibility(View.VISIBLE);
			}

		} else {
			viewHolder.rl_is_checked.setVisibility(View.INVISIBLE);
		}

		if (mIsRent) {
			if (node.getRentunitname() != null
					&& !node.getRentunitname().equals("null")
					&& !node.getRentunitname().equals("")) {
				viewHolder.tv_price.setText(node.getRentprice()
						+ node.getRentunitname());

			} else {
				viewHolder.tv_price.setText("" + node.getRentprice());

			}
		} else {
			if (node.getUnitname() != null
					&& !node.getUnitname().equals("null")
					&& !node.getUnitname().equals("")) {
				viewHolder.tv_price.setText(node.getRentprice()
						+ node.getUnitname());
			} else {
				viewHolder.tv_price.setText("" + node.getPrice());

			}
		}

		viewHolder.tv_housing_estate.setText(node.getEstatename());

		viewHolder.tv_house_desc.setText(node.getCountf() + "房"
				+ node.getCountt() + "厅" + node.getCountw() + "卫"
				+ node.getCounty() + "阳" + " " + node.getSquare() + "平");

		Date now = new Date();
		if (node.getHandoverdate() == 0
				|| node.getHandoverdate() <= now.getTime()) {
			viewHolder.tv_house_status.setText("随时入住");
		} else {
			viewHolder.tv_house_status.setText(DateUtil.getDateToString3(node
					.getHandoverdate()) + "前入住");
		}

		viewHolder.tv_number.setText(node.getPropertyno());
		String Floorall = node.getFloorall();
		if (StringUtils.isEmpty(Floorall) || Floorall.equals("null")) {
			Floorall = "";
		}
		viewHolder.tv_floor.setText(node.getFloor() + "/" + Floorall + "层   ");

		viewHolder.iv_house.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean canset = false;
				if (node.getEmpfollowid() != null
						&& !node.getEmpfollowid().equals("null")) {
					if (node.getEmpfollowid().equals(
							GlobalSPA.getInstance(context)
									.getStringValueForKey(GlobalSPA.KEY_EMPID))) {
						canset = true;
					}
				}

				AppContext.mHouseSourceNodeSetFengMian = node;

				Intent intent = new Intent(context, ViewImageActivity.class);
				intent.putExtra("id", node.getPropertyid());
				intent.putExtra("canset", canset);
				context.startActivity(intent);
			}
		});

		if (node.getEkeheadpic() != null && !node.getEkeheadpic().equals("")) {
			viewHolder.iv_house.setImageBitmap(BitmapUtils.stringtoBitmap(node
					.getEkeheadpic()));
		} else {
			viewHolder.iv_house.setImageResource(R.drawable.house_source);
		}

		if (node.getEmpfollowid() == null
				|| node.getEmpfollowid().equals("null")) {
			viewHolder.iv_genfang.setVisibility(View.VISIBLE);
			viewHolder.iv_genfang.setImageResource(R.drawable.xinpan);// 新房
		} else if (node.getEmpfollowid().equals(
				GlobalSPA.getInstance(context).getStringValueForKey(
						GlobalSPA.KEY_EMPID))) {
			viewHolder.iv_genfang.setVisibility(View.VISIBLE);
			viewHolder.iv_genfang.setImageResource(R.drawable.genfang);
		} else {
			viewHolder.iv_genfang.setVisibility(View.GONE);
		}

		List<MaidianNodeInfo> maidianList = node.getEkeMaidian();



		if (node.getScheduledate() != 0) {
			String time = DateUtil.getScheduledateToString(node
					.getScheduledate());
			viewHolder.tv_xianshigenjin_time.setText(time);

		}

		if (node.getContent() != null && !node.getContent().equals("null")
				&& !node.getContent().equals("")) {
			viewHolder.rl_xianshigenjin.setVisibility(View.VISIBLE);

			StringBuilder stringBuilder = new StringBuilder();


			stringBuilder.append(String.format("[%s条]",
					node.getSchedulenum())).append(" ").append(node.getContent());
			viewHolder.tv_xianshigenjin_tip.setText(stringBuilder.toString());
//			viewHolder.tv_xianshigenjin_number.setText(String.format("[%s条]",
//					node.getSchedulenum()));
		} else {
			viewHolder.rl_xianshigenjin.setVisibility(View.GONE);
		}

		if (node.getCollectempid() != null
				&& !node.getCollectempid().equals("")) {
			viewHolder.iv_collect.setVisibility(View.VISIBLE);
		} else {
			viewHolder.iv_collect.setVisibility(View.INVISIBLE);
		}

		if (mIsForCollect) {
			viewHolder.iv_collect.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	public void setForCollect() {
		this.mIsForCollect = true;
	}

	public class ViewHolder {
		public ImageView iv_house;
		public ImageView iv_genfang;
		public TextView tv_housing_estate;
		public TextView tv_price;
		public TextView tv_house_desc;
		public TextView tv_house_status;
		public TextView tv_number;
		public TextView tv_floor;
		public RelativeLayout rl_is_checked;
		private LinearLayout mLinearLayout_ll_maidian;

		private RelativeLayout rl_xianshigenjin;
		private TextView tv_xianshigenjin_tip;
		private TextView tv_xianshigenjin_number;
		private TextView tv_xianshigenjin_time;
		public ImageView iv_collect;

		ViewHolder(View v) {
			iv_house = (ImageView) v.findViewById(R.id.iv_house);
			iv_genfang = (ImageView) v.findViewById(R.id.iv_genfang);
			tv_housing_estate = (TextView) v
					.findViewById(R.id.tv_housing_estate);
			tv_price = (TextView) v.findViewById(R.id.tv_price);
			tv_house_desc = (TextView) v.findViewById(R.id.tv_house_desc);
			tv_house_status = (TextView) v.findViewById(R.id.tv_house_update_time);
			tv_number = (TextView) v.findViewById(R.id.tv_number);
			tv_floor = (TextView) v.findViewById(R.id.tv_floor);
			rl_is_checked = (RelativeLayout) v.findViewById(R.id.rl_is_checked);
			mLinearLayout_ll_maidian = (LinearLayout) v
					.findViewById(R.id.ll_maidian);

			rl_xianshigenjin = (RelativeLayout) v
					.findViewById(R.id.rl_xianshigenjin);
			tv_xianshigenjin_tip = (TextView) v
					.findViewById(R.id.tv_xianshigenjin_tip);
			tv_xianshigenjin_number = (TextView) v
					.findViewById(R.id.tv_xianshigenjin_number);
			tv_xianshigenjin_time = (TextView) v
					.findViewById(R.id.tv_xianshigenjin_time);
			iv_collect = (ImageView) v.findViewById(R.id.iv_collect);
		}
	}
}
