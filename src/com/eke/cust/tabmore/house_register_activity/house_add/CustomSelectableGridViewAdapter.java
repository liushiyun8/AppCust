package com.eke.cust.tabmore.house_register_activity.house_add;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.eke.cust.R;

import java.util.ArrayList;
import java.util.HashMap;


public class CustomSelectableGridViewAdapter extends BaseAdapter {

    private Context context;
    private String[] mItems;
    private boolean[] mIsSelected;
    private HashMap<Integer, Boolean> map = new HashMap<>();

    public CustomSelectableGridViewAdapter(Context context, String[] menuItems, boolean[] selected) {
        this.context = context;
        this.mItems = menuItems;
        this.mIsSelected = selected;
        initSelect();
    }

    public void initSelect() {
        for (int i = 0; i < mItems.length; i++) {
            map.put(i, false);
        }
    }

    public void setSelect(int position) {
        map.put(position, !map.get(position));
        notifyDataSetChanged();
    }

    public void initSelect(String select[]) {
        for (int i = 0; i < mItems.length; i++) {
            String text = mItems[i];
            for (int j = 0; j < select.length; j++) {
                if (text.equals(select[j])) {
                    map.put(i, true);
                }
            }
        }

        notifyDataSetChanged();
    }

    public ArrayList<String> getSelect() {
        ArrayList<String> select = new ArrayList<>();
        for (int i = 0; i < mItems.length; i++) {
            if (map.get(i)) {
                select.add(mItems[i]);
            }
        }
        return select;

    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {

        return mItems[position];
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

        viewHolder.tv_maidian.setText(mItems[position]);

        if (map.get(position)) {
            viewHolder.tv_maidian.setBackgroundResource(R.drawable.bg_maidian_item_selected);
        } else {
            viewHolder.tv_maidian.setBackgroundResource(R.drawable.bg_maidian_item);
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
