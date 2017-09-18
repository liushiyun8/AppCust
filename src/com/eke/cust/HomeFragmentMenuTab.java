package com.eke.cust;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.eke.cust.notification.NotificationKey;
import com.eke.cust.tabhouse.SearchHouseFragment;
import com.eke.cust.tabinfo.FragmentMenuInfo;
import com.eke.cust.tabmine.FragmentMine;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import foundation.base.fragment.BaseFragment;
import foundation.notification.NotificationCenter;
import foundation.notification.NotificationListener;

/**
 * 资讯界面
 *测试 提交代码编译apk
 * @author wujian
 */
public class HomeFragmentMenuTab extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    public static final int TAB_MENU_INFO = 0;
    public static final int TAB_MENU_HOUSE_SOURCE = 1;
    public static final int TAB_MENU_MINE = 2;
    public static final int TAB_MENU_MORE = 3;


    private RadioGroup mGroup;
    private RadioButton mRb_menu_info;
    private RadioButton mRb_menu_house_source;
    private RadioButton mRb_menu_mine;
    private RadioButton mRb_menu_entrust;
    private RadioButton mRb_menu_more;
    public LinearLayout mLl_base;
    protected List<Fragment> _fragments = new ArrayList<Fragment>();
    protected FragmentManager _fragmentManager;
    protected ArrayList<Class<? extends BaseFragment>> _fragmentClasses = new ArrayList<Class<? extends BaseFragment>>();

    public int mTabIndex = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment_menu_tab,
                container, false);

        mGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        mRb_menu_info = (RadioButton) view.findViewById(R.id.rb_menu_info);
        mRb_menu_house_source = (RadioButton) view
                .findViewById(R.id.rb_menu_house_source);
        mRb_menu_mine = (RadioButton) view.findViewById(R.id.rb_menu_mine);
        mRb_menu_entrust = (RadioButton) view.findViewById(R.id.rb_menu_entrust);
        mRb_menu_more = (RadioButton) view.findViewById(R.id.rb_menu_more);
        mLl_base = (LinearLayout) view.findViewById(R.id.ll_fragment);

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showBottom();
        }
    }

    private void showBottom() {
        if (AppContext.getInstance().isLogin()) {
            if(mTabIndex==2){
                mRb_menu_mine.setChecked(true);
            }
            mRb_menu_mine.setVisibility(View.VISIBLE);
            mRb_menu_entrust.setVisibility(View.GONE);
        } else {
            if(mTabIndex==2){
                mRb_menu_entrust.setChecked(true);
            }
            mRb_menu_entrust.setVisibility(View.VISIBLE);
            mRb_menu_mine.setVisibility(View.GONE);
        }
    }


    protected ArrayList<Class<? extends BaseFragment>> fragmentClasses() {
        ArrayList<Class<? extends BaseFragment>> result = new ArrayList<Class<? extends BaseFragment>>();
        result.add(FragmentMenuInfo.class);
        result.add(SearchHouseFragment.class);
        result.add(FragmentMine.class);
        result.add(FragmentMenuMore.class);
        return result;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _fragmentManager = getFragmentManager();
        _fragmentClasses = this.fragmentClasses();
        Assert.assertTrue("_fragmentClasses.size() == 0", _fragmentClasses.size() != 0);
        for (int i = 0; i < _fragmentClasses.size(); i++) {
            if (_fragmentManager.findFragmentByTag(i + "") != null) {
                _fragments.add(_fragmentManager.findFragmentByTag(i + ""));
            } else {
                _fragments.add(null);
            }
        }
        mGroup.setOnCheckedChangeListener(this);
        mLl_base.setTag(true);
        mRb_menu_info.setChecked(true);
    }




    public void setSelectedPage(int index) {
        mTabIndex = index;
    }


    protected void setFragmentSelection(int index) {
        FragmentTransaction transaction = _fragmentManager.beginTransaction();
        setSelectedPage(index);
        for (int i = 0; i < _fragments.size(); i++) {
            if (i == index) {
                continue;
            }

            Fragment fragment = _fragments.get(i);
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
        Fragment fragment = _fragments.get(index);
        if (fragment == null) {
            Class<? extends BaseFragment> clazz = _fragmentClasses.get(index);
            try {
                fragment = clazz.newInstance();
                Assert.assertTrue("fragment == null", fragment != null);
                transaction.add(containerViewId(), fragment, "" + index);
                _fragments.set(index, fragment);
            } catch (Exception e) {
                Assert.assertTrue("clazz.newInstance() exception", false);
            }
        } else {
            fragment = _fragmentManager.findFragmentByTag("" + mTabIndex);

            transaction.show(fragment);

        }
        transaction.commitAllowingStateLoss();

    }

    protected int containerViewId() {

        return R.id.ll_menu_content;
    }


    public void updateView() {
        if (mTabIndex == TAB_MENU_HOUSE_SOURCE) {

        }
    }

    public void initViewAnimation(boolean isshow) {

        float[] up2down;
        float[] down2up;
        up2down = new float[]{0f, 1f};
        down2up = new float[]{1f, 0f};
        if (isshow) {
            Animation mShowAction = AnimationUtils.loadAnimation(getActivity(), R.anim.push_bottom_out);
//            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                    down2up[0], Animation.RELATIVE_TO_SELF, down2up[1]);
//            mShowAction.setDuration(200);
            mLl_base.clearAnimation();
            mLl_base.startAnimation(mShowAction);
            mLl_base.setTag(false);
            mShowAction.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLl_base.setVisibility(View.GONE);
                    mLl_base.invalidate();
                    mLl_base.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
//            TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                    Animation.RELATIVE_TO_SELF, up2down[0], Animation.RELATIVE_TO_SELF, up2down[1]);
//            mHiddenAction.setDuration(200);
            Animation mHiddenAction = AnimationUtils.loadAnimation(getActivity(), R.anim.push_bottom_in);

            mLl_base.setTag(true);
            mHiddenAction.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLl_base.setVisibility(View.VISIBLE);
                    mLl_base.invalidate();
                    mLl_base.clearAnimation();

                }
            });
            mLl_base.startAnimation(mHiddenAction);

        }


    }





    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == mRb_menu_info.getId()) {
            setFragmentSelection(TAB_MENU_INFO);
        } else if (checkedId == mRb_menu_house_source.getId()) {
            setFragmentSelection(TAB_MENU_HOUSE_SOURCE);
        } else if (checkedId == mRb_menu_mine.getId() || checkedId == mRb_menu_entrust.getId()) {
            setFragmentSelection(TAB_MENU_MINE);
        }
        if (checkedId == mRb_menu_more.getId()) {
            setFragmentSelection(TAB_MENU_MORE);
        }

    }


}
