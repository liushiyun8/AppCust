package com.eke.cust.tabinfo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.base.adapter.CommonListAdapter;
import com.eke.cust.base.adapter.ViewHolder;
import com.eke.cust.model.HouseSource;
import com.eke.cust.utils.BitmapUtils;

import java.util.List;

/**
 * 房屋租售
 * 
 * @author wujian
 * 
 */
public class RentSellAdapter extends CommonListAdapter<HouseSource> {

	protected static final String TAG = null;

	public RentSellAdapter(Context context, int layoutId,
			List<HouseSource> list) {
		super(context, layoutId, list);

	}

	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	@Override
	public void convert(ViewHolder holder, HouseSource house, int position) {
		RelativeLayout layout = holder.findViewById(R.id.item_bg);
		ImageView huseImage = holder.findViewById(R.id.iv_house_image);
		TextView huseName = holder.findViewById(R.id.house_name);
		TextView huseInfo = holder.findViewById(R.id.house_info);
		TextView seeCount = holder.findViewById(R.id.tv_see_count);
		String name = house.estatename;
		huseName.setText(!TextUtils.isEmpty(name) ? name : "");
		if (house.ekeheadpic != null && !house.ekeheadpic.equals("")) {
			huseImage.setImageBitmap(BitmapUtils
					.stringtoBitmap(house.ekeheadpic));
		} else {
			huseImage.setImageResource(R.drawable.house_source);
		}
		StringBuilder feature = new StringBuilder();
		String info = "";
		if (house.listEkefeature != null) {
			for (int i = 0; i < house.listEkefeature.size(); i++) {
				feature.append(house.listEkefeature.get(i).detail).append(" ");
			}
		}
		if (house.type == 1) {
			// 出售
			layout.setBackgroundResource(R.drawable.item_sell_bg);

			info = house.square + "平" + " " + house.price + house.unitname
					+ " " + house.propertydirection + " " + feature.toString();

		} else {
			// 出租
			info = house.square + "平" + " " + house.rentprice
					+ house.rentunitname + " " + house.propertydirection + " "
					+ feature.toString();

			layout.setBackgroundResource(R.drawable.item_rent_bg);

		}
		huseInfo.setText(info);
		seeCount.setText(house.propertyno);


	}
}
