package com.eke.cust.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.eke.cust.Constants;
import com.eke.cust.HomeFragmentMenuTab;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.helper.UIHelper;
import com.eke.cust.utils.DoubleClickExitUtils;
import com.eke.cust.utils.MyLog;
import com.tencent.bugly.beta.Beta;

import java.util.List;

import foundation.permissions.AfterPermissionGranted;
import foundation.permissions.AppSettingsDialog;
import foundation.permissions.EasyPermissions;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity2";
    private HomeFragmentMenuTab mHomeFragmentMenuTab;
    //
    // private InviteMessgeDao inviteMessgeDao;
    // private UserDao userDao;
    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;

    private DoubleClickExitUtils duClickExitHelper;

    //申请权限

    private static final int PERMISSIONS = 0x01;
    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.READ_PHONE_STATE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean(Constants.ACCOUNT_REMOVED,
                false)) {
            ChatHelper.getInstance().logout(false, null);
            UIHelper.startToLogin(this);
            return;
        } else if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false)) {
            UIHelper.startToLogin(this);
            return;
        }
        duClickExitHelper = new DoubleClickExitUtils(this);

        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        mHomeFragmentMenuTab = (HomeFragmentMenuTab) fm.findFragmentById(R.id.fragment_tab);

        MyLog.e(TAG, "----> " + getFilesDir().toString() + " , " + getCacheDir().getPath());

        Beta.checkUpgrade( false,false );//检查版本号
        requestPermissions();
    }


    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private BroadcastReceiver internalDebugReceiver;


    @Override
    protected void onResume() {
        super.onResume();

        if (mHomeFragmentMenuTab != null) {
            mHomeFragmentMenuTab.updateView();
        }

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }
        try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
    }


    //请求app需要的权限
    @AfterPermissionGranted(PERMISSIONS)
    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "申请app需要的权限",
                    PERMISSIONS, permissions);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


}
