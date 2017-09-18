/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eke.cust;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import java.util.List;

public class BaseNonFragmentActivity extends Activity {
    private  boolean isActive = true;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (!isActive) {
//            //app 从后台唤醒，进入前台
//            isActive = true;
//            if (SharePreferenceUtil.getInstance(BaseNonFragmentActivity.this).isAutoLogin()) {
//                Intent intent = new Intent();
//                intent.setClass(BaseNonFragmentActivity.this,WelcomeActivity.class);
//                startActivity(intent);
//                finish();
//            } else  {
//                Intent intent = new Intent();
//                intent.setClass(BaseNonFragmentActivity.this, LoginDialog.class);
//                startActivity(intent);
//                finish();
//            }
//
//        }
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
