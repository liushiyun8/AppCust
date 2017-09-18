package com.eke.cust.tabmore.camera_activity;

import android.os.Bundle;
import android.view.View;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;

public class LocalImagePreviewActivity extends BaseActivity {


    private int mFromWhere = 0;

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_tab_more_camera_preview);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLeftImageView(R.drawable.arrow_back);
        mFromWhere = getIntent().getIntExtra("from_where", 0);
        Bundle bundle = new Bundle();
        bundle.putInt("from_where", mFromWhere);
        LocalImagePreviewFragment localImagePreviewFragment = new LocalImagePreviewFragment();
        localImagePreviewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, localImagePreviewFragment).commit();
        setTitle("本地图片预览");
        if (mFromWhere == Constants.FROM_PROFILE_HEAD_SETUP) {//from profile head setup
            setTitle("选择头像");

        } else if (mFromWhere == Constants.FROM_SELECT_SHENFENZHENG_ZHENG) {
            setTitle("选择身份证正面照片");
        } else if (mFromWhere == Constants.FROM_SELECT_SHENFENZHENG_FAN) {
            setTitle("选择身份证反面照片");
        } else if (mFromWhere == Constants.FROM_ADD_HOUSE_FNAGYUANTU) {
            setTitle("选择房源图");
        }

    }


}
