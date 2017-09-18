package foundation.base.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.Serializable;

/**
 * wujian   通用的ViewHolder
 * Created by lzw on 14-9-21.
 */
public class ViewHolder {

    private SparseArray<View> views;
    private int mPosition;
    private View convertView;
    private Context ctx;
    private Serializable data;

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    public ViewHolder() {

    }

    public ViewHolder(Context ctx, int position, View convertView, ViewGroup parent) {
        this.ctx = ctx;
        this.mPosition = position;
        this.convertView = convertView;
        views = new SparseArray<>();
        convertView.setTag(this);

    }

    public static ViewHolder getViewHolder(Context ctx, int position, int layoutId, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(layoutId, null);
            return new ViewHolder(ctx, position, convertView, parent);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
            return (ViewHolder) convertView.getTag();
        }

    }


    /**
     * 获取一个TextView
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(int id) {
        View childView = views.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            views.put(id, childView);
        }
        return (T) childView;
    }

    public int getPosition() {

        return mPosition;
    }

    public View getConvertView() {

        return convertView;
    }

    /**
     * 设置textview  文本
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView textView = findViewById(viewId);
        textView.setText(text);
        return this;
    }
    public ViewHolder setLeftText(int viewId, String text) {
        TextView textView = findViewById(viewId);
        textView.setText(text);
        return this;
    }
    /**
     * 设置textview  文本
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setCheckedTextViewText(int viewId, String text) {
        CheckedTextView textView = findViewById(viewId);
        textView.setText(text);
        return this;
    }
    public ViewHolder setLeftCheckedTextViewText(int viewId, String text) {
        CheckedTextView textView = findViewById(viewId);
        textView.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        textView.setText(text);
        return this;
    }
    public CheckedTextView getCheckedTextView(int viewId) {
        CheckedTextView textView = findViewById(viewId);
        return textView;
    }
    /**
     * 设置imageview  图片
     *
     * @param viewId
     * @param resid
     * @return
     */
    public ViewHolder setBackgroundResource(int viewId, int resid) {
        ImageView imageView = findViewById(viewId);
        imageView.setBackgroundResource(resid);
        return this;
    }
    public ViewHolder setImageResource(int viewId, int resid) {
        ImageView imageView = findViewById(viewId);
        imageView.setImageResource(resid);
        return this;
    }
    public ViewHolder setStart(int viewId, int count) {
        RatingBar ratingBar = findViewById(viewId);
        ratingBar.setNumStars(count);
        return this;
    }
    public ImageView getImageView(int viewId) {
        ImageView imageView = findViewById(viewId);
        return imageView;
    }
    /**
     * 设置imageview  Bitmap
     *
     * @param viewId
     * @param bitmap
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = findViewById(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }



}
