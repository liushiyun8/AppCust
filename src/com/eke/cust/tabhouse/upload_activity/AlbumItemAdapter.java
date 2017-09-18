package com.eke.cust.tabhouse.upload_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.eke.cust.R;
import com.eke.cust.utils.LoadImageUtil;

import java.util.List;

import foundation.widget.imageview.CheckableImageView;


public class AlbumItemAdapter extends BaseAdapter {
    private Context context;
    private List<PhoteBean> list;
    private LayoutInflater layoutInflater;
    private GridView gridView;

    public AlbumItemAdapter(List<PhoteBean> list, Context context, GridView gridView) {
        this.context = context;
        this.list = list;
        this.gridView = gridView;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.album_item_images_item_view, parent, false);
            holder = new Holder();
            holder.imageView = (CheckableImageView) convertView.findViewById(R.id.image_item);
            holder.rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        LoadImageUtil.getInstance().displayFromSDCard(list.get(position).getImagePath(), holder.imageView);
        holder.imageView.setChecked(list.get(position).isSelect());


        return convertView;
    }


    // 更新指定UI
    public void refreshItem(int position) {

        int visiblePosition = gridView.getFirstVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (position - visiblePosition >= 0) {
            //得到要更新的item的view
            View view = gridView.getChildAt(position - visiblePosition);
            //调用adapter更新界面
            getView(position, view, gridView);
        }

    }

    class Holder {
        CheckableImageView imageView;
        RelativeLayout rl_item;
    }
}
