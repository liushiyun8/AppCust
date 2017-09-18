package com.eke.cust.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by wujian on 2016/8/7.
 */

public class RadioButtonCenter extends RadioButton {
    public RadioButtonCenter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 拿到真实图片的高度，用控件的测量高度减去真实高度/2就是
        // padding值，将该padding值设置给topPadding就行了，其它padding保持正常的值
        Drawable[] drawables = getCompoundDrawables();
        Drawable topDrawable = drawables[1];
        if (topDrawable != null) {
            int padding = (getMeasuredHeight() - topDrawable.getBounds().height()) / 2;
            setPadding(getPaddingLeft(), padding, getPaddingRight(), getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
