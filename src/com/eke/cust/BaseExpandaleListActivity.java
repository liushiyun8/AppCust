package com.eke.cust;

import android.app.ActivityManager;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;

import java.util.List;

/**
 * Created by yangjie on 2016/4/12.
 */
public class BaseExpandaleListActivity extends ExpandableListActivity {
    private  boolean isActive = true;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
//            if (SharePreferenceUtil.getInstance(BaseExpandaleListActivity.this).isAutoLogin()) {
//                Intent intent = new Intent();
//                intent.setClass(BaseExpandaleListActivity.this,WelcomeActivity.class);
//                startActivity(intent);
//                finish();
//            } else  {
//                UIHelper.startToLogin(this);
//                finish();
//            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            //app 进入后台

            //全局变量isActive = false 记录当前已经进入后台
            isActive = false;
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

}
