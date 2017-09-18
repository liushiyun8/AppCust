package com.eke.cust.tabhouse.upload_activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.tabmore.camera_activity.LocalImagePreviewActivityNew;
import com.eke.cust.widget.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

public class PhotoDateAdapterNew extends BaseAdapter {

    private Context context;
    private List<PhoteBean> list;
    private List<String> photoDateList;
    private Handler mHandler;
    private int from_where = 0;

    public PhotoDateAdapterNew(Context context, List<PhoteBean> list, List<String> photoDate, Handler handler, int from) {
        this.context = context;
        this.list = list;
        this.photoDateList = photoDate;
        this.mHandler = handler;
        this.from_where = from;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return photoDateList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return photoDateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_photo_date_list_item, null);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.gridview = (NoScrollGridView) convertView.findViewById(R.id.album_item_gridv);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

//        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), Integer.parseInt(list.get(position).getId()), Thumbnails.MICRO_KIND, null);

        holder.tv_date.setText(photoDateList.get(position));

        final List<PhoteBean> sublist = new ArrayList<PhoteBean>();
        sublist.clear();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDate().equals(photoDateList.get(position))) {
                sublist.add(list.get(i));
            }
        }
        final AlbumItemAdapter adapter = new AlbumItemAdapter(sublist, context, holder.gridview);
        holder.gridview.setAdapter(adapter);

        if (from_where == Constants.FROM_CANERA) {
            holder.gridview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        message.what = Constants.FROM_CANERA;
                        bundle.putString("img_path", sublist.get(position).getImagePath());
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            });
        } else if (from_where == LocalImagePreviewActivityNew.FROM_PROFILE_HEAD_SETUP ||
                from_where == LocalImagePreviewActivityNew.FROM_SELECT_SHENFENZHENG_ZHENG ||
                from_where == LocalImagePreviewActivityNew.FROM_SELECT_SHENFENZHENG_FAN ||
                from_where == LocalImagePreviewActivityNew.FROM_ADD_HOUSE_FNAGYUANTU) {
            holder.gridview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        if (from_where == LocalImagePreviewActivityNew.FROM_PROFILE_HEAD_SETUP) {
                            message.what = LocalImagePreviewActivityNew.MSG_IMG_SELECTED;
                        } else {
                            message.what = LocalImagePreviewActivityNew.MSG_IMG_CLICKED;
                        }
                        bundle.putString("img_path", sublist.get(position).getImagePath());
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            });
        } else {
            holder.gridview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    if (mHandler != null) {
                        Message message = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        message.what = Constants.SHOW_BIG_IMG;
                        bundle.putString("img_path", sublist.get(position).getImagePath());
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            });

            holder.gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    // TODO Auto-generated method stub
                    for (int i = 0; i < list.size(); i++) {
                        if (!list.get(i).getId().equals(sublist.get(position).getId())) {
                            list.get(i).setSelect(false);
                        }
                    }

                    if (sublist.get(position).isSelect()) {
                        sublist.get(position).setSelect(false);
                        if (mHandler != null) {
                            Message message = mHandler.obtainMessage();
                            Bundle bundle = new Bundle();
                            message.what = Constants.NOT_SHOW_UPLOAD_BTN;
                            bundle.putString("img_path", sublist.get(position).getImagePath());
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    } else {
                        sublist.get(position).setSelect(true);
                        if (mHandler != null) {
                            Message message = mHandler.obtainMessage();
                            Bundle bundle = new Bundle();
                            message.what = Constants.SHOW_UPLOAD_BTN;
                            bundle.putString("img_path", sublist.get(position).getImagePath());
                            bundle.putString("img_name", sublist.get(position).getDisplayname());
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                    }

                    adapter.refreshItem(position);

                    return true;
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        private TextView tv_date;
        private NoScrollGridView gridview;
    }
}
