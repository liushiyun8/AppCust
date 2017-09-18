package com.fab;


import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ScrollView;

public class ObservableWebview extends WebView {

    public interface OnScrollChangedListener {
        void onScrollChanged(WebView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener mOnScrollChangedListener;

    public ObservableWebview(Context context) {
        super(context);
    }

    public ObservableWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }
}