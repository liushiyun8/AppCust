package com.eke.cust.tabmine;

import android.os.Bundle;
import android.view.View;

import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.utils.MyLog;

/**
 * 委托
 *
 * @author wujian
 */
public class EntrustActivity extends BaseActivity {

    @Override
    protected View onCreateContentView() {
        return inflateContentView(R.layout.activity_entrust);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new EntrustFragment()).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.d(TAG, "EntrustActivity---onResume");


    }
}
