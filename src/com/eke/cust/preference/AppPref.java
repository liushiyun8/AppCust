package com.eke.cust.preference;

import org.droidparts.util.AppUtils;

import com.eke.cust.Constants;
import com.eke.cust.model.CurrentUser;
import com.eke.cust.model.UserLocation;
import com.eke.cust.utils.JSONUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 关于app的配置信息
 */
public class AppPref extends BasePref {

    // 初次启动
    private final String KEY_FIRST_LAUNCH = "first_launch";
    // 用户token
    private final String KEY_UserToken = "key_usertoken";
    // 用户手机号
    private final String KEY_UserPhone = "key_phone";
    // 用户密码
    private final String KEY_UserPassword = "key_password";
    // 用户地理位置
    private final String KEY_UserLocation = "key_userlocation";
    // 用户
    private final String KEY_User = "key_user";
    // 城市id
    private final String KEY_CityId = "KEY_CityId";

    public AppPref(Context context) {
        super(context, AppUtils.getVersionName(context, false));
    }

    public void setNoFirstLaunch() {
        putBoolean(KEY_FIRST_LAUNCH, false);
    }

    public boolean firstLaunch() {
        return getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setUserToken(String usertoken) {

        putString(KEY_UserToken, usertoken);
    }

    public String userToken() {
        String  token=getString(KEY_UserToken, null);
        if(TextUtils.isEmpty(token)){
            token= Constants.DEFAULT_TOKEN;
        }
        return token;
    }

    public void setUserPhone(String phone) {
        putString(KEY_UserPhone, phone);
    }

    public String userPhone() {
        return getString(KEY_UserPhone, "");
    }
    public void setUserPassword(String password) {
        putString(KEY_UserPassword, password);
    }

    public String userPassword() {
        return getString(KEY_UserPassword, "");
    }

    //优质用户密码
    public String getMePassword() {
        return getString("Password", "");
    }

    public void setMePassword(String Password) {
        putString("Password", Password);
    }

    //用户类型
    public String getCustType() {
        return getString("custType", "");
    }

    public void setCustType(String custType) {
        putString("custType", custType);
    }

    public void setCityId(String cityid) {

        putString(KEY_CityId, cityid);
    }

    public String cityId() {
        return getString(KEY_CityId, "");
    }

    public void setUserLocation(UserLocation location) {
        putString(KEY_UserLocation, JSONUtils.toJson(location, false));
    }

    public UserLocation getUserLocation() {
        String location = getString(KEY_UserLocation, null);
        if (location == null) {
            return null;
        } else {
            return JSONUtils.fromJson(location, UserLocation.class);

        }

    }

    public void setUser(CurrentUser currentUser) {
        putString(KEY_User, JSONUtils.toJson(currentUser, false));
    }

    public CurrentUser getUser() {
        String location = getString(KEY_User, null);
        if (location == null) {
            return null;
        } else {
            return JSONUtils.fromJson(location, CurrentUser.class);

        }

    }

}
