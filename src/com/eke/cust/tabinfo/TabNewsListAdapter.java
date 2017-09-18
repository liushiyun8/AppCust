package com.eke.cust.tabinfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eke.cust.R;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.StringCheckHelper;

import java.util.List;

public class TabNewsListAdapter extends BaseAdapter {

    protected static final String TAG = "NewsListAdapter";
    private Context context;
    private List<TabNewsNodeInfo> mItems;

    public TabNewsListAdapter(Context context, List<TabNewsNodeInfo> menuItems) {
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
            convertView = View.inflate(context, R.layout.layout_news_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TabNewsNodeInfo tabNewsNodeInfo = mItems.get(index);
        if (tabNewsNodeInfo != null) {
            viewHolder.new_image.setImageResource(R.drawable.icon_def_75);
            if (!StringCheckHelper.isEmpty(tabNewsNodeInfo.newsicon)) {
                viewHolder.new_image.setImageBitmap(BitmapUtils.stringtoBitmap(tabNewsNodeInfo.newsicon));
            } else {
                viewHolder.new_image.setImageResource(R.drawable.icon_def_75);
            }
            viewHolder.iv_dot.setImageResource(tabNewsNodeInfo.getDotId());
            viewHolder.tv_content.setText(tabNewsNodeInfo.getTitle());
        }


        return convertView;
    }

    private class ViewHolder {
        public ImageView iv_dot;
        public ImageView new_image;
        public TextView tv_content;

        ViewHolder(View v) {
            iv_dot = (ImageView) v.findViewById(R.id.iv_dot);
            new_image = (ImageView) v.findViewById(R.id.new_image);
            tv_content = (TextView) v.findViewById(R.id.tv_content);
        }
    }
}
