/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eke.cust.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eke.cust.R;

import java.util.Locale;

/**
 * 滑动指示器pstsindicatorcolor颜色 在视图的底部的全宽度的线pstsunderlinecolor颜色
 * 选项卡之间的分隔pstsdividercolor颜色 滑动指示器pstsindicatorheightheight
 * 在视图的底部的全宽度的线pstsunderlineheight高度 pstsdividerpadding顶部和底部填充的分频器
 * pststabpaddingleftright左、右填充每个选项卡 pstsscrolloffset卷轴被选择的标签的偏移
 * pststabbackground背景绘制的每个标签，应该是一个statelistdrawable
 * pstsshouldexpand如果设置为TRUE，每个标签都给予同样的重量，默认为false
 * pststextallcaps如果为真，所有选项卡标题都是大写，默认为true
 */
public class SlidingTabStrip extends HorizontalScrollView {

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor};
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private LinearLayout tabsContainer;
    public boolean isSelect = true;

    private int tabCount;

    private int currentPosition = 0;
    private int selectedPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;


    private boolean shouldExpand = false;

    private int scrollOffset = 52;
    private int dividerPadding = 12;
    private int tabPadding = 10;
    private int dividerWidth = 0;
    private int minWidth = 100;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;


    private int tabBackgroundResId = R.color.transparent;
    private int tabSelectBackgroundResId = R.color.transparent;

    private Locale locale;

    private OnTabClickListener onTabClickListener;

    private int images[];

    public int[] getImages() {
        return images;
    }

    public void setImages(int[] images) {
        tabCount = images.length;
        this.images = images;
    }

    public SlidingTabStrip(Context context) {
        this(context, null);
    }

    public SlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public SlidingTabStrip(Context context, AttributeSet attrs,
                           int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);
        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);


        dividerPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);

        dividerWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);


        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);


        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs,
                R.styleable.PagerSlidingTabStrip);


        // tab文字选中时的颜色,默认和滑动指示器的颜色一致
        tabSelectBackgroundResId = a.getResourceId(
                R.styleable.PagerSlidingTabStrip_selectedTabBackground,
                0);


                dividerPadding = a.getDimensionPixelSize(
                        R.styleable.PagerSlidingTabStrip_pstsDividerPadding,
                        dividerPadding);
                tabPadding = a.getDimensionPixelSize(
                        R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight,
                        tabPadding);
                tabBackgroundResId = a.getResourceId(
                        R.styleable.PagerSlidingTabStrip_pstsTabBackground,
                        tabBackgroundResId);
                minWidth = a.getResourceId(
                        R.styleable.PagerSlidingTabStrip_pstsTabTextMinWidth,
                        minWidth);
        shouldExpand = a
                .getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand,
                        shouldExpand);
        scrollOffset = a
                .getDimensionPixelSize(
                        R.styleable.PagerSlidingTabStrip_pstsScrollOffset,
                        scrollOffset);


        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void onTabSelected(int position) {
        selectedPosition = position;
        currentPosition = position;
        updateTabStyles();
        invalidate();
    }

    public void clearSelect() {
        isSelect = false;
        clearTabStyles();
        invalidate();
    }

    public void setTitle(int images[]) {
        this.images = images;
        //
        if (images == null) {
            throw new IllegalStateException("titles instance.");
        }

        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();
        //
        tabCount = images.length;

        for (int i = 0; i < tabCount; i++) {

            addTextTab(i, images[i]);

        }

        updateTabStyles();


    }

    private void addTextTab(final int position, int resid) {
        ImageView tab = new ImageView(getContext());
        tab.setImageResource(resid);
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(position);
                }
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer
                .addView(tab, position, shouldExpand ? expandedTabLayoutParams
                        : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            v.setBackgroundResource(tabBackgroundResId);
            if (v instanceof ImageView) {

                ImageView tab = (ImageView) v;

                // setAllCaps() is only available from API 14, so the upper case
                // is made manually if we are on a
                // pre-ICS-build

                if (i == selectedPosition) {
                    tab.setBackgroundResource(tabSelectBackgroundResId);
                }
            }
        }

    }

    public void clearTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);


        }

    }

    public void updateTabStyles(int position) {
        this.selectedPosition = position;
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            v.setBackgroundResource(tabBackgroundResId);
            if (v instanceof ImageView) {
                v.setBackgroundResource(tabBackgroundResId);
                if (i == selectedPosition) {
                    v.setBackgroundResource(tabSelectBackgroundResId);
                }
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();


        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates
        // between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset)
                    * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset)
                    * lineRight);
        }



    }






    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        notifyDataSetChanged();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }













    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
        updateTabStyles();
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnTabClickListener {
        void onTabClick(int position);
    }
}
