package com.eke.cust.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class BasePref {

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	protected BasePref(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	protected void putString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	protected String getString(String key, String defaultValue) {
		return sp.getString(key, defaultValue);
	}

	protected void clear(String key) {
		editor.putString(key, "");
		editor.commit();
	}

	public void clear() {
		editor.clear();
		editor.commit();

	}

	protected void putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	protected boolean getBoolean(String key, boolean defaultValue) {
		return sp.getBoolean(key, defaultValue);
	}

	public void putInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void putLong(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	protected int getInt(String key, int defaultValue) {
		return sp.getInt(key, defaultValue);
	}

	protected Long getLong(String key, long defaultValue) {
		return sp.getLong(key, defaultValue);
	}

}
