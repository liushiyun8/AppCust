package com.eke.cust.widget;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.eke.cust.R;

/**
 * Created by wujian on 16/8/21.
 */

public class GradientVerticalTextView extends TextView {

    public GradientVerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public GradientVerticalTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    protected void onLayout(boolean changed,
                            int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getPaint().setShader(new LinearGradient(
                    0, 0, 0, getHeight(),
                    ContextCompat.getColor(getContext(), R.color.start_color),  ContextCompat.getColor(getContext(),R.color.second_color),
                    Shader.TileMode.CLAMP));
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // TODO Auto-generated method stub
        if ("".equals(text) || text == null || text.length() == 0) {
            return;
        }
        int m = text.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < m; i++) {
            CharSequence index = text.toString().subSequence(i, i + 1);
            sb.append(index + "\n");
        }
        super.setText(sb, type);
    }
}
