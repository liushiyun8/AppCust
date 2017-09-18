package com.eke.cust.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesActions {
	private Context mContext = null;
	private SharedPreferences mPreferences = null;
	private Editor mPreferences_edit = null;
	
	public SharedPreferencesActions(Context context, String spName) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mPreferences = mContext.getSharedPreferences(spName, Context.MODE_PRIVATE);
		mPreferences_edit = mPreferences.edit();
	}
	
	public String SPA_GetString(String key) {
		return mPreferences.getString(key, null);
	}
	
	public int SPA_GetInt(String key) {
		return mPreferences.getInt(key, -1);
	}
	
	public float SPA_GetFloat(String key) {
		return mPreferences.getFloat(key, 0);
	}
	
	public long SPA_GetLong(String key) {
		return mPreferences.getLong(key, 0);
	}
	
	public boolean SPA_GetBoolean(String key) {
		return mPreferences.getBoolean(key, false);
	}
	
	public void SPA_PutString(String key, String value) {
		mPreferences_edit.putString(key, value);
		mPreferences_edit.commit();
	}
	
	public void SPA_PutInt(String key, int value) {
		mPreferences_edit.putInt(key, value);
		mPreferences_edit.commit();
	}
	
	public void SPA_PutFloat(String key, float value) {
		mPreferences_edit.putFloat(key, value);
		mPreferences_edit.commit();
	}
	
	public void SPA_PutLong(String key, long value) {
		mPreferences_edit.putLong(key, value);
		mPreferences_edit.commit();
	}
	
	public void SPA_PutBoolean(String key, boolean value) {
		mPreferences_edit.putBoolean(key, value);
		mPreferences_edit.commit();
	}
	
	public void SPA_RemoveKey(String key) {
		mPreferences_edit.remove(key);
		mPreferences_edit.commit();
	}
	
}