package com.eke.cust.launch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.main.MainActivity;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.ScreenUtils;
import com.eke.cust.widget.BaseAnimationListener;
import com.eke.cust.widget.CircleIndicator;
import com.eke.cust.widget.DirectionalViewPager;
import com.eke.cust.widget.GradientVerticalTextView;
import com.eke.cust.widget.VerticalTextView;

import org.droidparts.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 *
 * @author: lichunyu
 * @since: 2017-08-24 16:52
 */
public class GuideActivity extends BaseActivity implements CircleIndicator.OnCirclePageChangeListener, View.OnClickListener {
    private ImageView page_one_leftcloud;//云朵
    //pageone  动画
    private VerticalTextView fragment1_text2, fragment1_text3;
    private Button mStartButton;
    private CircleIndicator circleIndicator;
    private View view1, view2, view3, view4, view5;
    private List<View> views;
    private DirectionalViewPager pager;
    private ViewPagerAdapter vpAdapter;
    // region 第一页动画
    ImageView mIvAppIcon;
    GradientVerticalTextView mPageOneText;
    TextView mPageOneText1;
    LinearLayout mPageOneText2;
    ImageView mPageOneImage;
    private Animation animationTop, pageoneTextAnimation, pageoneTextAnimation1, pageoneTextAnimation2, pageOneImageAnimation;

    //左侧 动画1
    private Animation leftToRight, leftToRight1;

    //左侧 动画3
    private Animation push_left1_in, push_left1_in1, push_left1_in2;

    //右侧 动画1
    private Animation push_right_in, push_right_in1;


    //动画三
    private Animation push_right1_in, push_right1_in1, push_right1_in2;

    //第二页动画
    private PageTwoAnimationListener pageTwoAnimationListener;
    private ImageView welcom2_dw, iv_welcom2;
    private RelativeLayout layout_welcom2_dw;
    private ImageView mIvTwoPageText2;
    private GradientVerticalTextView pageTwotext1;
    private Animation animationImageRightToLeft, animationImageRightToLeft1, animationImageFromTop, animationTextRightToLeft, animationTextRightToLeft1,
            animationText1, animationText2, animationText3;
    private Animation animationLeftcloud;


    //第三页动画
    private PageThreeAnimationListener pageThreeAnimationListener;
    private RelativeLayout layout_welcom3_dw;

    private ImageView iv_welcom3, welcom3_dw;
    private GradientVerticalTextView fragment3_text1;
    private RelativeLayout layout_page3_text;
    private VerticalTextView fragment3_text2, fragment3_text3;
    private Animation animationThreeText1, animationThreeText2, animationThreeText3;
    private Animation animationGo;


    //第四页动画
    private PageFourAnimationListener pageFourAnimationListener;
    private ImageView welcom4_title, iv_welcom4;
    private GradientVerticalTextView fragment4_text1;
    private VerticalTextView fragment4_text2, fragment4_text3;

    //第五页动画
    private PageFiveAnimationListener pageFiveAnimationListener;
    private ImageView welcom5_title, iv_welcom5;
    private GradientVerticalTextView fragment5_text1;
    private VerticalTextView fragment5_text2, fragment5_text3;
    private TextView mTxtVersion;
    private int currentPage;
    //云朵动画


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppContext.getInstance().getAppPref().firstLaunch()) {
            UIHelper.startActivity(this, WelcomeActivity.class);
            finish();
            return;
        }
        setContentView(R.layout.activity_guide);
        pager = (DirectionalViewPager) findViewById(R.id.pager);
        circleIndicator = (CircleIndicator) findViewById(R.id.CircleIndicator);
        mTxtVersion = (TextView) findViewById(R.id.txt_version);
        mTxtVersion.setText(String.format("v%s", AppUtils.getVersionName(mContext, false)));
        findViewById(R.id.bt_start).setOnClickListener(this);
        page_one_leftcloud = (ImageView) findViewById(R.id.page_one_leftcloud);
        mStartButton = (Button) findViewById(R.id.bt_start);


        views = new ArrayList<View>();
        view1 = LayoutInflater.from(this).inflate(R.layout.layout_guide_one, null);
        view2 = LayoutInflater.from(this).inflate(R.layout.layout_guide_two, null);
        view3 = LayoutInflater.from(this).inflate(R.layout.layout_guide_three, null);
        view4 = LayoutInflater.from(this).inflate(R.layout.layout_guide_four, null);
        view5 = LayoutInflater.from(this).inflate(R.layout.layout_guide_five, null);

        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);


        //云朵移动
        int[] location = new int[2];
        page_one_leftcloud.getLocationOnScreen(location);
        float fromYDelta = location[1];
        animationLeftcloud = new TranslateAnimation(-200, DensityUtil.getScreenWidth(this), fromYDelta, fromYDelta);
        animationLeftcloud.setDuration(10000);
        animationLeftcloud.setRepeatCount(-1);
        animationLeftcloud.setInterpolator(new AccelerateInterpolator());
        animationLeftcloud.setRepeatMode(Animation.START_ON_FIRST_FRAME);
        page_one_leftcloud.startAnimation(animationLeftcloud);


        pager.setOrientation(DirectionalViewPager.VERTICAL);
        animationImageRightToLeft = AnimationUtils.loadAnimation(this, R.anim.text_right_to_left);

        //左侧进入
        leftToRight = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        leftToRight1 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);


        push_left1_in = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        push_left1_in1 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        push_left1_in2 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);

        //右侧
        push_right_in = AnimationUtils.loadAnimation(this, R.anim.image_right_to_left);
        push_right_in1 = AnimationUtils.loadAnimation(this, R.anim.image_right_to_left);


        push_right1_in = AnimationUtils.loadAnimation(this, R.anim.image_right_to_left);
        push_right1_in1 = AnimationUtils.loadAnimation(this, R.anim.image_right_to_left);
        push_right1_in2 = AnimationUtils.loadAnimation(this, R.anim.image_right_to_left);

        animationText1 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationText2 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationThreeText1 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationThreeText2 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationThreeText3 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);


        initPageOne();
        initPageTwo();
        initPageThree();
        initPageFour();
        initPageFive();
        showOnePageAnimation();
        vpAdapter = new ViewPagerAdapter(views);
        pager.setAdapter(vpAdapter);
        circleIndicator.setViewPager(pager);
        circleIndicator.addOnPageChangeListener(this);
        mStartButton.getLocationOnScreen(location);
        float fromXDelta = location[0];
        float toYDelta = location[1];
        animationGo = new TranslateAnimation(fromXDelta, fromXDelta, toYDelta + ScreenUtils.dp2px(mContext, 100), toYDelta);
        animationGo.setDuration(1000);
        animationGo.setAnimationListener(new startAnimationListener());


    }

    @Override
    public void changePage(int position) {
        currentPage = position;
        switch (position) {
            case 0:
                showOnePageAnimation();
                cancleTwoPageAnimation();
                mTxtVersion.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);
                break;
            case 1:
                cancleOnePageAnimation();
                mTxtVersion.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);
                pageThreeStop();
                pageTwoStart();
                break;
            case 2:
                cancleTwoPageAnimation();
                mTxtVersion.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);
                pageThreeStart();
                break;
            case 3:
                mTxtVersion.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);
                pageThreeStop();
                pageFourStart();
                break;
            case 4:
                pageFiveStart();
                buttonStart();
                break;


        }
    }

    private class startAnimationListener extends BaseAnimationListener {


        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == animationGo) {
                mStartButton.setVisibility(View.VISIBLE);
            }
        }


    }


    //region  第一屏动画
    private void initPageOne() {
        mIvAppIcon = (ImageView) view1.findViewById(R.id.iv_app_icon);
        mPageOneText = (GradientVerticalTextView) view1.findViewById(R.id.page_one_text);
        mPageOneText1 = (TextView) view1.findViewById(R.id.page_one_text1);
        mPageOneText2 = (LinearLayout) view1.findViewById(R.id.page_one_text2);
        mPageOneImage = (ImageView) view1.findViewById(R.id.page_one_image);
        animationTop = AnimationUtils.loadAnimation(this, R.anim.tutorail_top);
        pageoneTextAnimation = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text1);
        pageoneTextAnimation1 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        pageoneTextAnimation2 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        pageOneImageAnimation = AnimationUtils.loadAnimation(this, R.anim.tutorail_image_from_top);
        animationTop.setAnimationListener(new PageOneAnimationListener());
        pageoneTextAnimation.setAnimationListener(new PageOneAnimationListener());
        pageoneTextAnimation1.setAnimationListener(new PageOneAnimationListener());
        pageoneTextAnimation2.setAnimationListener(new PageOneAnimationListener());
        pageOneImageAnimation.setAnimationListener(new PageOneAnimationListener());


    }

    private void showOnePageAnimation() {
        mIvAppIcon.setVisibility(View.INVISIBLE);
        mPageOneText.setVisibility(View.INVISIBLE);
        mPageOneText1.setVisibility(View.INVISIBLE);
        mPageOneText2.setVisibility(View.INVISIBLE);
        mIvAppIcon.startAnimation(animationTop);
        mPageOneText.startAnimation(pageoneTextAnimation);
        mPageOneImage.startAnimation(pageOneImageAnimation);
    }

    private void cancleOnePageAnimation() {
        mIvAppIcon.clearAnimation();
        mPageOneText.clearAnimation();
        mPageOneText1.clearAnimation();
        mPageOneText2.clearAnimation();
        mPageOneImage.clearAnimation();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_start) {
            Intent intent = new Intent(GuideActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            AppContext.getInstance().getAppPref().setNoFirstLaunch();
        }

    }

    private class PageOneAnimationListener extends BaseAnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            if (currentPage == 0) {
                if (animation == animationTop) {
                    mIvAppIcon.setVisibility(View.VISIBLE);
                } else if (animation == pageoneTextAnimation) {
                    mPageOneText1.startAnimation(pageoneTextAnimation1);
                    mPageOneText.setVisibility(View.VISIBLE);
                } else if (animation == pageoneTextAnimation1) {
                    mPageOneText1.setVisibility(View.VISIBLE);
                    mPageOneText2.startAnimation(pageoneTextAnimation2);
                } else if (animation == pageoneTextAnimation2) {
                    mPageOneText2.setVisibility(View.VISIBLE);
                } else if (animation == pageOneImageAnimation) {
                    mPageOneImage.setVisibility(View.VISIBLE);
                }
            }

        }


    }


    //endregion
    //region 第二屏動畫
    private void initPageTwo() {
        pageTwoAnimationListener = new PageTwoAnimationListener();
        //定位
        welcom2_dw = (ImageView) view2.findViewById(R.id.welcom2_dw);

        layout_welcom2_dw = (RelativeLayout) view2.findViewById(R.id.layout_welcom2_dw);
        //图片
        iv_welcom2 = (ImageView) view2.findViewById(R.id.iv_welcom2);

        //多维度找房
        pageTwotext1 = (GradientVerticalTextView) view2.findViewById(R.id.pagetwo_text1);
        mIvTwoPageText2 = (ImageView) view2.findViewById(R.id.iv_pagetwo_text2);

        animationImageRightToLeft = AnimationUtils.loadAnimation(this, R.anim.image_right_to_left);
        animationImageRightToLeft1 = AnimationUtils.loadAnimation(this, R.anim.image_right_to_left);

        animationTextRightToLeft = AnimationUtils.loadAnimation(this, R.anim.text_right_to_left);
        animationTextRightToLeft1 = AnimationUtils.loadAnimation(this, R.anim.text_right_to_left);


        animationImageFromTop = AnimationUtils.loadAnimation(this, R.anim.tutorail_image_from_top);
        animationTextRightToLeft.setAnimationListener(pageTwoAnimationListener);
        animationTextRightToLeft1.setAnimationListener(pageTwoAnimationListener);

        animationImageRightToLeft.setAnimationListener(pageTwoAnimationListener);
        animationImageRightToLeft1.setAnimationListener(pageTwoAnimationListener);
        animationImageFromTop.setAnimationListener(pageTwoAnimationListener);


        leftToRight.setAnimationListener(pageTwoAnimationListener);
        leftToRight1.setAnimationListener(pageTwoAnimationListener);


    }

    private void pageTwoStart() {
        pageTwotext1.setVisibility(View.INVISIBLE);
        mIvTwoPageText2.setVisibility(View.INVISIBLE);
        welcom2_dw.setImageResource(R.drawable.welcom2_dw);
        layout_welcom2_dw.startAnimation(animationTop);
        iv_welcom2.startAnimation(animationImageFromTop);
        pageTwotext1.startAnimation(leftToRight);
        mIvTwoPageText2.startAnimation(leftToRight1);

    }

    private void cancleTwoPageAnimation() {
        pageTwotext1.clearAnimation();
        mIvTwoPageText2.clearAnimation();
        welcom2_dw.clearAnimation();
        iv_welcom2.clearAnimation();
        pageTwotext1.setVisibility(View.INVISIBLE);
        mIvTwoPageText2.setVisibility(View.INVISIBLE);

    }

    private class PageTwoAnimationListener extends BaseAnimationListener {


        @Override
        public void onAnimationEnd(Animation animation) {
            if (currentPage == 1) {
                if (animation == leftToRight) {
                    pageTwotext1.setVisibility(View.VISIBLE);
                } else if (animation == leftToRight1) {
                    mIvTwoPageText2.setVisibility(View.VISIBLE);
                    mIvTwoPageText2.setImageResource(R.drawable.page_two_text2);
                    mIvTwoPageText2.startAnimation(animationTextRightToLeft);
                } else if (animation == animationTextRightToLeft) {
                    mIvTwoPageText2.setImageResource(R.drawable.page_two_text2);
                    welcom2_dw.setImageResource(R.drawable.welcom2_dw);
                    welcom2_dw.startAnimation(animationImageRightToLeft1);
                    mIvTwoPageText2.startAnimation(animationTextRightToLeft1);
                } else if (animation == animationTextRightToLeft1) {
                    mIvTwoPageText2.setImageResource(R.drawable.page_two_text2);
                    welcom2_dw.setImageResource(R.drawable.welcom2_dw);
                    welcom2_dw.startAnimation(animationImageRightToLeft);
                    mIvTwoPageText2.startAnimation(animationTextRightToLeft);
                } else if (animation == animationImageRightToLeft1) {
                    mIvTwoPageText2.setImageResource(R.drawable.page_two_text1);
                    welcom2_dw.setImageResource(R.drawable.icon_heart);
                    welcom2_dw.startAnimation(animationImageRightToLeft);
                    mIvTwoPageText2.startAnimation(animationTextRightToLeft);
                } else if (animation == animationImageRightToLeft) {
                    mIvTwoPageText2.setImageResource(R.drawable.page_two_text1);
                    welcom2_dw.setImageResource(R.drawable.icon_heart);
                    welcom2_dw.startAnimation(animationImageRightToLeft1);
                    mIvTwoPageText2.startAnimation(animationTextRightToLeft1);
                } else if (animation == animationImageFromTop) {
                    iv_welcom2.setVisibility(View.VISIBLE);
                }

            }

        }


    }


    //endregion
    //region 第三屏動畫
    private void initPageThree() {
        pageThreeAnimationListener = new PageThreeAnimationListener();

        iv_welcom3 = (ImageView) view3.findViewById(R.id.iv_welcom3);
        welcom2_dw.setImageResource(R.drawable.icon_chat_message);

        welcom3_dw = (ImageView) view3.findViewById(R.id.welcom3_dw);
        fragment3_text1 = (GradientVerticalTextView) view3.findViewById(R.id.fragment3_text1);
        layout_page3_text = (RelativeLayout) view3.findViewById(R.id.layout_page3_text);

        layout_welcom3_dw = (RelativeLayout) view3.findViewById(R.id.layout_welcom3_dw);
        fragment3_text2 = (VerticalTextView) view3.findViewById(R.id.fragment3_text2);
        fragment3_text3 = (VerticalTextView) view3.findViewById(R.id.fragment3_text3);


        animationText1 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationText2 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationText3 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);

        push_left1_in.setAnimationListener(pageThreeAnimationListener);
        push_left1_in1.setAnimationListener(pageThreeAnimationListener);
        push_left1_in2.setAnimationListener(pageThreeAnimationListener);


        push_right1_in.setAnimationListener(pageThreeAnimationListener);
        push_right1_in1.setAnimationListener(pageThreeAnimationListener);
        push_right1_in2.setAnimationListener(pageThreeAnimationListener);


    }

    private void pageThreeStart() {
        pageThreeStop();
        layout_page3_text.setVisibility(View.INVISIBLE);
        fragment3_text2.setVisibility(View.INVISIBLE);
        iv_welcom3.startAnimation(animationImageFromTop);
        layout_welcom3_dw.startAnimation(animationTop);
        fragment3_text1.startAnimation(push_left1_in);

    }

    private void pageThreeStop() {
        push_left1_in.cancel();
        push_left1_in1.cancel();
        push_left1_in2.cancel();
        fragment3_text1.clearAnimation();
        fragment3_text2.clearAnimation();
        fragment3_text1.clearAnimation();
        layout_page3_text.clearAnimation();
        layout_welcom3_dw.clearAnimation();
        push_right1_in.cancel();
        push_right1_in1.cancel();
        push_right1_in2.cancel();
    }

    private class PageThreeAnimationListener extends BaseAnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == push_left1_in) {
                fragment3_text1.setVisibility(View.VISIBLE);
                fragment3_text2.startAnimation(push_left1_in1);
            } else if (animation == push_left1_in1) {
                fragment3_text2.setVisibility(View.VISIBLE);
                layout_page3_text.startAnimation(push_left1_in2);
            } else if (animation == push_left1_in2) {
                layout_page3_text.setVisibility(View.VISIBLE);
                fragment3_text3.setText("全程客服");
                fragment3_text3.setTextColor(Color.parseColor("#77bd40"));
                fragment3_text3.startAnimation(push_right1_in);
                welcom3_dw.setImageResource(R.drawable.icon_chat_message);
                welcom3_dw.startAnimation(push_right1_in);
            } else if (animation == push_right1_in) {
                welcom3_dw.setImageResource(R.drawable.icon_search_house);
                welcom3_dw.startAnimation(push_right1_in1);
                fragment3_text3.setText("所见即所得");
                fragment3_text3.setTextColor(Color.parseColor("#d0a490"));
                fragment3_text3.startAnimation(push_right1_in1);
            } else if (animation == push_right1_in1) {
                welcom3_dw.setImageResource(R.drawable.icon_house_money);
                welcom3_dw.startAnimation(push_right1_in2);
                fragment3_text3.setText("资金监管");
                fragment3_text3.setTextColor(Color.parseColor("#a3d0e9"));
                fragment3_text3.startAnimation(push_right1_in2);
            } else if (animation == push_right1_in2) {
                welcom3_dw.setImageResource(R.drawable.icon_chat_message);
                welcom3_dw.startAnimation(push_right1_in);
                fragment3_text3.setText("全程客服");
                fragment3_text3.setTextColor(Color.parseColor("#77bd40"));
                fragment3_text3.startAnimation(push_right1_in);
            }
        }

    }


    //endregion
    //region 第四屏動畫Four
    private void initPageFour() {
        pageFourAnimationListener = new PageFourAnimationListener();
        welcom4_title = (ImageView) view4.findViewById(R.id.welcom4_title);
        iv_welcom4 = (ImageView) view4.findViewById(R.id.iv_welcom4);
        fragment4_text1 = (GradientVerticalTextView) view4.findViewById(R.id.fragment4_text1);
        fragment4_text2 = (VerticalTextView) view4.findViewById(R.id.fragment4_text2);
        fragment4_text3 = (VerticalTextView) view4.findViewById(R.id.fragment4_text3);


    }

    private void pageFourStart() {
        fragment4_text1.setVisibility(View.INVISIBLE);
        fragment4_text2.setVisibility(View.INVISIBLE);
        fragment4_text3.setVisibility(View.INVISIBLE);
        iv_welcom4.startAnimation(animationImageFromTop);
        welcom4_title.startAnimation(animationTop);
        fragment4_text1.startAnimation(animationText1);
        animationText1.setAnimationListener(pageFourAnimationListener);
        animationText2.setAnimationListener(pageFourAnimationListener);
        animationText3.setAnimationListener(pageFourAnimationListener);
    }

    private void pageFourStop() {
        if (animationImageRightToLeft != null) {
            animationImageRightToLeft.cancel();
        }

    }

    private class PageFourAnimationListener extends BaseAnimationListener {


        @Override
        public void onAnimationEnd(Animation animation) {

            if (animation == animationText1) {
                fragment4_text1.setVisibility(View.VISIBLE);
                fragment4_text2.startAnimation(animationText2);
            } else if (animation == animationText2) {
                fragment4_text2.setVisibility(View.VISIBLE);
                fragment4_text3.startAnimation(animationText3);

            } else if (animation == animationText3) {
                fragment4_text3.setVisibility(View.VISIBLE);
            }

        }

    }


    //endregion
    //region 第五屏動畫
    private void initPageFive() {
        pageFiveAnimationListener = new PageFiveAnimationListener();
        welcom5_title = (ImageView) view5.findViewById(R.id.welcom5_title);
        iv_welcom5 = (ImageView) view5.findViewById(R.id.iv_welcom5);
        fragment5_text1 = (GradientVerticalTextView) view5.findViewById(R.id.fragment5_text1);
        fragment5_text2 = (VerticalTextView) view5.findViewById(R.id.fragment5_text2);
        fragment5_text3 = (VerticalTextView) view5.findViewById(R.id.fragment5_text3);
        animationText1 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationText2 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
        animationText3 = AnimationUtils.loadAnimation(this, R.anim.tutorail_one_text2);
    }

    private void pageFiveStart() {
        iv_welcom5.startAnimation(animationImageFromTop);
        welcom5_title.startAnimation(animationTop);
        fragment5_text1.setVisibility(View.INVISIBLE);
        fragment5_text2.setVisibility(View.INVISIBLE);
        fragment5_text3.setVisibility(View.INVISIBLE);
        fragment5_text1.startAnimation(animationText1);


        animationText1.setAnimationListener(pageFiveAnimationListener);
        animationText2.setAnimationListener(pageFiveAnimationListener);
        animationText3.setAnimationListener(pageFiveAnimationListener);
    }

    private void pageFiveStop() {
        if (animationImageRightToLeft != null) {
            animationImageRightToLeft.cancel();
        }
    }

    private class PageFiveAnimationListener extends BaseAnimationListener {


        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == animationText1) {
                fragment5_text1.setVisibility(View.VISIBLE);
                fragment5_text2.startAnimation(animationText2);
            } else if (animation == animationText2) {
                fragment5_text2.setVisibility(View.VISIBLE);
                fragment5_text3.startAnimation(animationText3);
            } else if (animation == animationText3) {
                fragment5_text3.setVisibility(View.VISIBLE);
            }


//
        }

    }


    //endregion


    private void buttonStart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTxtVersion.setVisibility(View.VISIBLE);
            }
        }, 800);
        mStartButton.setVisibility(View.VISIBLE);
        mStartButton.startAnimation(animationGo);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
