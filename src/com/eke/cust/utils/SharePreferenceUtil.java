package com.eke.cust.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.eke.cust.Constants;

/**
 * Created by Administrator on 2016/4/7.
 */
public class SharePreferenceUtil {
    private static SharePreferenceUtil instance = null;

    private SharedPreferences sharedPreferences;
    private String userName;
    private String userPwd;
    private Boolean isAutoLogin;
    private SharePreferenceUtil(Context context)
    {
        sharedPreferences = context.getSharedPreferences("userInfo",
                Activity.MODE_PRIVATE);
        userName = sharedPreferences.getString(Constants.LOGIN_USER_NAME, "");
        userPwd = sharedPreferences.getString(Constants.LOGIN_USER_PWD, "");
        isAutoLogin = sharedPreferences.getBoolean(Constants.LOGIN_IS_AUTOLOGIN,false);
    }
    public static SharePreferenceUtil getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new SharePreferenceUtil(context);
        }

        return instance;
    }
    // 设置值
    public void setValue(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }
    // 获取值
    public String getValue(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
    public void setUserName(String paramUserName) {
        this.userName= paramUserName;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.LOGIN_USER_NAME, paramUserName);
        editor.apply();
    }
    public void setUserPwd(String paramUserPwd) {
        this.userPwd = paramUserPwd;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.LOGIN_USER_PWD, paramUserPwd);
        editor.apply();
    }
    public void setIsAutoLogin(boolean paramIsAutoLogin) {
        this.isAutoLogin = paramIsAutoLogin;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.LOGIN_IS_AUTOLOGIN, paramIsAutoLogin);
        editor.apply();
    }
    public String getUserName() {
         return userName;
    }
    public String getUserPwd() {
        return userPwd;
    }
    public boolean isAutoLogin() {
        return isAutoLogin;
    }
}
