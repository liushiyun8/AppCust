package foundation.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import foundation.log.LogUtils;

/**
 * AppUtils
 * <ul>
 * <li>{@link AppUtils#isNamedProcess(Context, String)}</li>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-5-07
 */
public class AppUtils {

	private AppUtils() {
		throw new AssertionError();
	}

	/**
	 * whether this process is named with processName
	 *
	 * @param context
	 * @param processName
	 * @return <ul>
	 *         return whether this process is named with processName
	 *         <li>if context is null, return false</li>
	 *         <li>if {@link ActivityManager#getRunningAppProcesses()} is null,
	 *         return false</li>
	 *         <li>if one process of
	 *         {@link ActivityManager#getRunningAppProcesses()} is equal to
	 *         processName, return true, otherwise return false</li>
	 *         </ul>
	 */
	public static boolean isNamedProcess(Context context, String processName) {
		if (context == null) {
			return false;
		}

		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processInfoList = manager
				.getRunningAppProcesses();
		if (ListUtils.isEmpty(processInfoList)) {
			return false;
		}

		for (RunningAppProcessInfo processInfo : processInfoList) {
			if (processInfo != null
					&& processInfo.pid == pid
					&& ObjectUtils.isEquals(processName,
							processInfo.processName)) {
				return true;
			}
		}
		return false;
	}



	//在进程中去寻找当前APP的信息，判断是否在前台运行
	public static boolean isApplicationInBackground2(Context context) {
		ActivityManager activityManager =(ActivityManager) context.getSystemService(
				Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();
		List<RunningAppProcessInfo>appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return true;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 *
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取App安装包信息
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public static String readAssets(Context context, String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		try {
			AssetManager assetManager = context.getAssets();
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					assetManager.open(fileName)));
			String line;
			while ((line = bf.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	@SuppressWarnings("deprecation")
	public static void copy(Context ctx, String content) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) ctx
					.getSystemService(Context.CLIPBOARD_SERVICE);

			clipboardManager.setText(content);

		} else {
			ClipboardManager cmb = (ClipboardManager) ctx
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(content);

		}
	}


    public static int getVersionCode(Context ctx) {
        try {
			PackageInfo e = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return e.versionCode;
        } catch (NameNotFoundException var3) {
			throw new IllegalArgumentException(var3);
		}
	}

	/**
	 * 获取包名
	 */
	public static String getMyPackageName(Context context)
	{
		return context.getPackageName();
	}

	/**
	 * * 获取应用版本号(用于系统识别)
	 * * @return 当前应用的版本号
	 */
	public static int getAppVersionCode(Context context)
	{
		try
		{
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * * 获取应用版本名称(用于用户识别)
	 * * @return 当前应用的版本名称
	 */
	public static String getAppVersionName(Context context)
	{
		try
		{
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取系统版本号
	 */
	public static int getAndroidSDKVersion()
	{
		int version = 0;
		try
		{
			version = Integer.valueOf(Build.VERSION.SDK);
		} catch (NumberFormatException e)
		{
			LogUtils.e(e.toString());
		}
		return version;
	}

	/**
	 * 获取应用程序最大可用内存
	 *
	 * @return
	 */
	public static int getMaxMemory()
	{
		return (int) Runtime.getRuntime().maxMemory();
	}

	/**
	 * 获取设备唯一识别码
	 *
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context)
	{
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	/**
	 * 判断当前程序是否正在运行
	 */
	public static boolean isProcessRunning(Context context)
	{
		if (context == null)
			return false;

		String processName = getMyPackageName(context);
		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
		if (processInfoList == null || processInfoList.size() == 0)
		{
			return false;
		}

		for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList)
		{
			if (processInfo != null && processInfo.pid == pid
					&& ObjectUtils.isEquals(processName, processInfo.processName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查当前程序是否处于后台，需要权限android.permission.GET_TASKS
	 */
	public static boolean isApplicationInBackground(Context context)
	{
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
		if (taskList != null && !taskList.isEmpty())
		{
			ComponentName topActivity = taskList.get(0).topActivity;
			if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断某个界面是否在前台
	 *
	 * @param context
	 * @param className 某个界面名称
	 */
	public static boolean isForeground(Context context, String className)
	{
		if (context == null || TextUtils.isEmpty(className))
		{
			return false;
		}

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0)
		{
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
}
