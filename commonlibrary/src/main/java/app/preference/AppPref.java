package app.preference;

import android.content.Context;

import app.BaseAppContext;
import foundation.util.AppUtils;


public class AppPref extends BasePref {
    private static final String USER_ACCOUNT = "user_account";
    private static final String TIME = "time";
    // 初次启动
    private final String KEY_APP_VERSION_CODE = "app_version_code";

    public AppPref(Context context) {
        super(context, AppUtils.getAppName(context));
    }

    //保存用户账号
    public void saveUserAccount(String user_account) {
        putString(USER_ACCOUNT, user_account);
    }

    //获取用户用户账号
    public String getUserAccount() {
        return getString(USER_ACCOUNT, "");
    }
    //保存请求时间
    public void saveTime(String time) {
      //  putString(TIME, time);
    }

    //获取请求shi
    public String getTime() {
        return getString(TIME, "");
    }

    public void setVersionCode() {
        int versionCode = AppUtils.getVersionCode(BaseAppContext.getInstance());
        putInt(KEY_APP_VERSION_CODE, versionCode);
    }

    public int getVersionCode() {
        return getInt(KEY_APP_VERSION_CODE, 0);
    }


}
