package com.eke.cust.utils;

import android.content.Context;

public class GlobalSPA {
	private SharedPreferencesActions mSP = null;
	private static GlobalSPA instance = null;
	public static final String SHAREDPREFERENCE_NAME = "eke_config";
	
	public static final String KEY_IS_NOT_FIRST_LOGIN = "KEY_IS_NOT_FIRST_LOGIN";
	
	public static final String KEY_PHONE = "KEY_PHONE";
	public static final String KEY_PWD = "KEY_PWD";
	public static final String KEY_EMPID = "KEY_EMPID";
	public static final String KEY_EMPNO = "KEY_EMPNO";
	public static final String KEY_EMPNAME = "KEY_EMPNAME";
	public static final String KEY_DEPTNAME= "KEY_DEPTNAME";	
	public static final String KEY_TOKEN = "KEY_TOKEN";
	public static final String KEY_CITY = "KEY_CITY";
	public static final String KEY_CITYID = "KEY_CITYID";
	public static final String KEY_EKEMAPPER = "KEY_EKEMAPPER";
	public static final String KEY_LOCATCOLLECTENABLE = "KEY_LOCATCOLLECTENABLE";
	
	public static final String KEY_IMG_URL_PUB = "KEY_IMG_URL_PUB";//APP,web等插图
	public static final String KEY_IMG_URL_PROPERTY = "KEY_IMG_URL_PROPERTY";//房源
	public static final String KEY_IMG_URL_ESTATE = "KEY_IMG_URL_ESTATE";//楼盘
	
	public static final String KEY_DAIKAN = "KEY_DAIKAN";//是否正在带看的标识
	public static final String KEY_LOCATION = "KEY_LOCATION";
	
	public static final String KEY_USERDAILIESTATES = "KEY_USERDAILIESTATES";
	
	public GlobalSPA(Context context) {
		if (mSP == null) {
			mSP = new SharedPreferencesActions(context, SHAREDPREFERENCE_NAME);
		}
	}
	
	public static GlobalSPA getInstance(Context context) {
		if (instance == null) {
			synchronized (GlobalSPA.class) {
				if (instance == null) {
					instance = new GlobalSPA(context);
				}
			}
		}
		return instance;
	}	
	
	public boolean sp_is_first_app_run() {
		if (!mSP.SPA_GetBoolean("IS_NOT_FIRST_RUN")) {
			mSP.SPA_PutBoolean("IS_NOT_FIRST_RUN", true);
			return true;
		}
				
		return false;
	}
	
	public String getStringValueForKey (String key) {
		if (mSP == null) {
			return null;
		}
		
		return mSP.SPA_GetString(key);
	}
	
	public void setStringValueForKey (String key, String value) {
		if (mSP == null) {
			return;
		}
		
		mSP.SPA_PutString(key, value);
	}
	
	public Boolean getBooleanValueForKey (String key) {
		if (mSP == null) {
			return null;
		}
		
		return mSP.SPA_GetBoolean(key);
	}
	
	public void setBooleanValueForKey (String key, Boolean value) {
		if (mSP == null) {
			return;
		}
		
		mSP.SPA_PutBoolean(key, value);
	}
	
	public int getIntValueForKey (String key) {
		if (mSP == null) {
			return -1;
		}
		
		return mSP.SPA_GetInt(key);
	}
	
	public void setIntValueForKey (String key, int value) {
		if (mSP == null) {
			return;
		}
		
		mSP.SPA_PutInt(key, value);
	}
	
	public void removeKey(String key) {
		if (mSP == null) {
			return;
		}
		
		mSP.SPA_RemoveKey(key);
	}
	
}