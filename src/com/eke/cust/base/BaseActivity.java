package com.eke.cust.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.chat.ChatHelper;
import com.eke.cust.launch.WelcomeActivity;
import com.eke.cust.utils.MyLog;
import com.umeng.analytics.MobclickAgent;

import org.droidparts.Injector;
import org.droidparts.util.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import app.AppManager;
import foundation.LoadingHandler;
import foundation.notification.NotificationListener;
import foundation.util.ThreadUtils;
import foundation.widget.NavigationBar;

/**
 * 程序基类
 * 
 * @author wujian 2014-3-21 22:43:27
 */
public abstract class BaseActivity extends AppCompatActivity implements NotificationListener {
	protected boolean isActive = true;
	protected Context mContext;
	protected String TAG = getClass().getSimpleName();

	protected LinearLayout _rootView;
	protected NavigationBar navigationBar;
	protected RelativeLayout _containerLayout;
	protected View _contentView;
	protected int _contaninerViewId = 100;
	protected int _navBarViewId = 101;

	protected View onCreateContentView() {
		return null;
	}

	protected View createView() {
		_rootView = new LinearLayout(this);
		_rootView.setOrientation(LinearLayout.VERTICAL);
		_rootView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		// 头部
		navigationBar = onCreateNavigationBar();
		if (navigationBar != null) {
			navigationBar.setLayoutParams(new LinearLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));
			// noinspection ResourceType
			navigationBar.setId(_navBarViewId);
			_rootView.addView(navigationBar);
		}
		// 内容区
		_containerLayout = new RelativeLayout(this);
		_containerLayout.setLayoutParams(new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		// noinspection ResourceType
		_containerLayout.setId(_contaninerViewId);
		_rootView.addView(_containerLayout);

		// 嵌入内容区
		_contentView = onCreateContentView();
		if (_contentView != null) {
			_containerLayout.addView(_contentView);
		}

		return _rootView;
	}

	protected void onPostInject() {

	}




	// endregion

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(this.createView());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
			localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
		}
		Injector.inject(this);
		mContext = this;

		boolean isImmersive = false;
		Log.e(TAG,"我被调用了0000！");
		if (hasKitKat() && !hasLollipop()) {
			Log.e(TAG,"我被调用了1111！");
			isImmersive = true;
//            透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		} else if (hasLollipop()) {
			Log.e(TAG,"我被调用了！");
			Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			isImmersive = true;
		}
		MIUISetStatusBarLightMode(getWindow(),true);
		FlymeSetStatusBarLightMode(getWindow(),true);

		onPostInject();

		_loadingHandler = new LoadingHandler(this);

		AppManager.getAppManager().addActivity(this);
		AppManager.getAppManager().addMemory(this);

		showNavigationBar(false);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(mContext);
		MobclickAgent.onPageStart(TAG);
		// NotificationCenter.defaultCenter.addListener(NotificationKey.InvalidToken,
		// this);
		if (!isActive) {
			//app 从后台唤醒，进入前台
			isActive = true;
			if (AppContext.getInstance().isLogin()) {
				Intent intent = new Intent();
				intent.setClass(this,WelcomeActivity.class);
				startActivity(intent);
			}
		} else {
			// onresume时，取消notification显示
			ChatHelper.getInstance().getNotifier().reset();

		}
		MyLog.d(TAG, isActive == true ? "onResume 前台" : "onResume 后台");
		if (!isActive) {
			// app 从后台唤醒，进入前台
			isActive = true;
		}
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(mContext);
		MobclickAgent.onPageEnd(TAG);
		MyLog.d(TAG, isActive == true ? "onPause 前台" : "onPause 后台");

	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		AppManager.getAppManager().finalize(this);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
		// NotificationCenter.defaultCenter.removeListener(NotificationKey.InvalidToken,
		// this);

	}

	protected void setSoftInputMode() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	protected void hideSoftInputView() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			View currentFocus = getCurrentFocus();
			if (currentFocus != null) {
				manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	// region Loading dialog

	private LoadingHandler _loadingHandler;

	public void showLoading() {
		ThreadUtils.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_loadingHandler.showLoading();
			}
		});

	}

	public void showLoading(String message) {
		_loadingHandler.showLoading(message);
	}

	public void updateLoading(String message) {
		_loadingHandler.updateLoading(message);
	}

	public void hideLoading() {
		ThreadUtils.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_loadingHandler.hideLoading();
			}
		});
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	protected boolean isShouldHideKeyboard(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	// View v = getCurrentFocus();
	// if (isShouldHideKeyboard(v, ev)) {
	// hideKeyboard(v.getWindowToken());
	// }
	// }
	// return super.dispatchTouchEvent(ev);
	// }
	/**
	 * 获取InputMethodManager，隐藏软键盘
	 * 
	 * @param token
	 */
	protected void hideKeyboard(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	// endregion

	protected ViewGroup getContainer() {
		return _containerLayout;
	}

	protected void showNavigationBar(boolean show) {

		if (navigationBar == null)
			return;

		if (show) {
			navigationBar.setVisibility(View.VISIBLE);
		} else {
			navigationBar.setVisibility(View.GONE);
		}
	}

	protected NavigationBar onCreateNavigationBar() {
		NavigationBar navigationBar = new NavigationBar(this);
		return navigationBar;
	}

	protected void goBack() {
		finish();
	}

	protected void goNext() {
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		goBack();
	}

	protected void setTitle(String title) {
		if (navigationBar == null)
			return;

		if (Strings.isNotEmpty(title)) {
			showNavigationBar(true);
		}
		navigationBar.setTitle(title);
	}


	public void setTitle(int title) {
		setTitle(getResourceString(title));
	}

	protected void registerRightImageView(final int resid) {
		if (navigationBar == null)
			return;
		showNavigationBar(true);
		navigationBar.setRightImage(resid, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goNext();
			}
		});

	}


	protected void registerRightTextView(final String resid) {
		if (navigationBar == null)
			return;
		showNavigationBar(true);
		navigationBar.setRightText(resid, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goNext();

			}
		});
	}

	protected void registerLeftImageView(int resid) {
		if (navigationBar == null)
			return;
		showNavigationBar(true);
		navigationBar.registerBack(resid, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack();

			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!isAppOnForeground()) {
			// app 进入后台
			// 全局变量isActive = false 记录当前已经进入后台
			isActive = false;
		}
		MyLog.d(TAG, isActive == true ? "onStop 前台" : "onStop后台");

	}

	protected String getResourceString(int resId) {
		String result = null;
		try {
			result = this.getResources().getString(resId);
		} catch (Exception e) {
		}
		return result;
	}

	protected View inflateContentView(int resId) {
		return getLayoutInflater().inflate(resId, _containerLayout, false);
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}
	public boolean onNotification(Notification notification) {

		return false;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static boolean hasLollipop() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			try {
				WindowManager.LayoutParams lp = window.getAttributes();
				Field darkFlag = WindowManager.LayoutParams.class
						.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class
						.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(lp);
				if (dark) {
					value |= bit;
				} else {
					value &= ~bit;
				}
				meizuFlags.setInt(lp, value);
				window.setAttributes(lp);
				result = true;
			} catch (Exception e) {

			}
		}
		return result;
	}

	public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			Class clazz = window.getClass();
			try {
				int darkModeFlag = 0;
				Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
				Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
				darkModeFlag = field.getInt(layoutParams);
				Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
				if (dark) {
					extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
				} else {
					extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
				}
				result = true;
			} catch (Exception e) {

			}
		}
		return result;
	}

	public static int getStatusBarHeight() {
		return getInternalDimensionSize(Resources.getSystem(),"status_bar_height");
	}
	private static int getInternalDimensionSize(Resources res, String key) {
		int result = 0;
		int resourceId = res.getIdentifier(key, "dimen", "android");
		if (resourceId > 0) {
			result = res.getDimensionPixelSize(resourceId);
		}
		return result;
	}

}
