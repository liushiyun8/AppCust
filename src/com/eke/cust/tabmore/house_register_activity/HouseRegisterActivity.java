package com.eke.cust.tabmore.house_register_activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.tabhouse.OperatingHouseActivity;

import org.droidparts.annotation.inject.InjectView;

public class HouseRegisterActivity extends BaseActivity {
    @InjectView(id = R.id.btn_dengji, click = true)
    private Button mButton_btn_dengji;


    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_tab_more_house_register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("楼盘登记");
        registerLeftImageView(R.drawable.arrow_back);
        findViewById(R.id.btn_dengji).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.startActivity(HouseRegisterActivity.this,OperatingHouseActivity.class);
            }
        });

    }



}
