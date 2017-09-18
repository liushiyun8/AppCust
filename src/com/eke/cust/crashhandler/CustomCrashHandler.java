package com.eke.cust.crashhandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.eke.cust.AppContext;

public class CustomCrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CustomCrashHandler";
	
	public static final String BROADCAST_CRASH = "com.custom.eke.crash";

	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();

	private static String mDataCacheRootPath = null;

//	private final static String FOLDER_NAME = "/custom_crash";
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CustomCrashHandler INSTANCE = new CustomCrashHandler();
	private Context mContext;
	private Map<String, String> infos = new HashMap<String, String>();

	private SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	private String mCrashFileName = "";
	private String mDevModel = "";

	private CustomCrashHandler() {
	}

	public static CustomCrashHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		handleException(ex);
		mDefaultHandler.uncaughtException(thread, ex);

	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		
		collectDeviceInfo(mContext);
		saveCrashInfo2File(ex);
		return true;
	}

	
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	public String getStoragePath() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath : mDataCacheRootPath;
	}
	
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			if (key.equals("MODEL")) {
				mDevModel = value;
			}
			
			sb.append(key + "=" + value + "\n");
			
		}

		long timestamp = System.currentTimeMillis();
		String time = formatter.format(new Date());
		File crash_forderFile = new File(getStoragePath());
		mCrashFileName = crash_forderFile.toString() + File.separator
				+ "eke_crash-" + time + "-" + timestamp + ".txt";
		

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {

			FileOutputStream fos = new FileOutputStream(mCrashFileName);
			fos.write(sb.toString().getBytes());
			fos.close();
			return mCrashFileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
	
}