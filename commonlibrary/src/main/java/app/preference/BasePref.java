package app.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class BasePref {
    //用户
    protected String key_for_user = "KEY_FOR_USER";

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

    protected int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }
    public static boolean putString(Context context, String key, String value)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(Context context, String key)
    {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defaultValue)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public static boolean putInt(Context context, String key, int value)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(Context context, String key)
    {
        return getInt(context, key, -1);
    }

    public static int getInt(Context context, String key, int defaultValue)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    public static boolean putLong(Context context, String key, long value)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(Context context, String key)
    {
        return getLong(context, key, -1);
    }

    public static long getLong(Context context, String key, long defaultValue)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public static boolean putFloat(Context context, String key, float value)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static float getFloat(Context context, String key)
    {
        return getFloat(context, key, -1);
    }

    public static float getFloat(Context context, String key, float defaultValue)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    public static boolean putBoolean(Context context, String key, boolean value)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(Context context, String key)
    {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    public static void remove(Context context, String key)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key).commit();
    }

    public static boolean containKey(Context context, String key)
    {
        SharedPreferences settings = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return settings.contains(key);
    }
}
