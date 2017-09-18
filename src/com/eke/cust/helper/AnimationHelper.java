package com.eke.cust.helper;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Created by wujian on 16/8/10.
 * 分享 底部移动框
 */

public class AnimationHelper {
    /**
     * 设置图像移动动画效果
     * @param v
     * @param startX
     * @param toX
     * @param startY
     * @param toY
     */
    public static void SetImageSlide(View v, int startX, int toX, int startY, int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
        anim.setDuration(100);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }
}
