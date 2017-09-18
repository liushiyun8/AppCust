package com.eke.cust.global;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.GlobalSPA;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class GlobalDaiKan {
	private Activity mActivity;
	private Context mContext;
	private Button mFloatView;
	private WindowManager mWindowManager;

	private	LinearLayout mFloatLayout ;
	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);

			if (msg != null) {
				switch (msg.what) {
				case Constants.NO_NETWORK:
					break;

				case Constants.TAG_SUCCESS:
					Bundle bundle = msg.getData();
					String request_url = bundle.getString("request_url");
					String resp = bundle.getString("resp");
					try {
						JSONObject jsonObject = new JSONObject(resp);
						String result = jsonObject.optString("result", "");
						if (result.equals(Constants.RESULT_SUCCESS)) {
							if (request_url.equals(ServerUrl.METHOD_startGuide)) {
								GlobalSPA.getInstance(mContext)
										.setBooleanValueForKey(
												GlobalSPA.KEY_DAIKAN, true);
								mFloatView
										.setBackgroundResource(R.drawable.bg_daikan_selected);
								mFloatView.setTextColor(0xffffffff);
							} else if (request_url
									.equals(ServerUrl.METHOD_endGuide)) {
								GlobalSPA.getInstance(mContext)
										.setBooleanValueForKey(
												GlobalSPA.KEY_DAIKAN, false);
								mFloatView
										.setBackgroundResource(R.drawable.bg_daikan);
								mFloatView.setTextColor(0x00ffffff);

							}
						} else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg",
									"出错!");
							Toast.makeText(mContext.getApplicationContext(),
									errorMsg, Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(mContext.getApplicationContext(),
								"请求出错!", Toast.LENGTH_SHORT).show();
					}

					break;

				case Constants.TAG_FAIL:
					Toast.makeText(mContext.getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(mContext.getApplicationContext(), "请求出错!",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}
	};

	public GlobalDaiKan(Activity activity, Context context) {
		this.mActivity = activity;
		this.mContext = context;
	}
	

	public void createGenFangFloatView() {
		// 获取LayoutParams对象
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

		// 获取的是LocalWindowManager对象
		mWindowManager = this.mActivity.getWindowManager();
		// Log.i(TAG, "mWindowManager1--->" +
		// this.mActivity.getWindowManager());
		// mWindowManager = getWindow().getWindowManager();
		// Log.i(TAG, "mWindowManager2--->" +
		// this.mActivity.getWindow().getWindowManager());

		// 获取的是CompatModeWrapper对象
		// mWindowManager = (WindowManager)
		// mActivity.getApplication().getSystemService(Context.WINDOW_SERVICE);
		// Log.i(TAG, "mWindowManager3--->" + mWindowManager);
		wmParams.type = LayoutParams.TYPE_BASE_APPLICATION;
		wmParams.format = PixelFormat.RGBA_8888;
		;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = DensityUtil.dip2px(mActivity, 50);
		wmParams.y = 0;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		LayoutInflater inflater = this.mActivity.getLayoutInflater();// LayoutInflater.from(getApplication());

		 mFloatLayout = (LinearLayout) inflater.inflate(
				R.layout.float_layout, null);
		mWindowManager.addView(mFloatLayout, wmParams);
		mFloatView = (Button) mFloatLayout.findViewById(R.id.float_id);
		mFloatView.setAlpha(0.5f);

		// 绑定触摸移动监听
		// mFloatView.setOnTouchListener(new OnTouchListener()
		// {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event)
		// {
		// // // TODO Auto-generated method stub
		// // wmParams.x = (int)event.getRawX() - mFloatLayout.getWidth()/2;
		// // //25为状态栏高度
		// // wmParams.y = (int)event.getRawY() - mFloatLayout.getHeight()/2 -
		// 40;
		// // mWindowManager.updateViewLayout(mFloatLayout, wmParams);
		// return false;
		// }
		// });

		// 绑定点击监听
		mFloatView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showGlobalGenFangDlg(mFloatView);
			}
		});

	}

	private void disableOrEnableBtnCloseOnAlertDialog(DialogInterface dialog,
			boolean disableOrEnable) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, disableOrEnable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isBtnCanAct = false;

	private void showGlobalGenFangDlg(final Button btn) {
		final Dialog dlg = new Dialog(this.mActivity, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(this.mActivity);
		View viewContent = inflater.inflate(R.layout.dlg_global_genfang, null);
		isBtnCanAct = false;

		final RelativeLayout rl_bg = (RelativeLayout) viewContent
				.findViewById(R.id.rl_bg);
		final TextView tv_error_tip = (TextView) viewContent
				.findViewById(R.id.tv_error_tip);
		final EditText mEditText_et_content_phone;

		TextView tv_tip;
		final Button mButton_btn_start;
		mEditText_et_content_phone = (EditText) viewContent
				.findViewById(R.id.et_content_phone);
		mButton_btn_start = (Button) viewContent.findViewById(R.id.btn_start);
		tv_tip = (TextView) viewContent.findViewById(R.id.tv_tip);

		final boolean daikan_status = GlobalSPA.getInstance(this.mActivity)
				.getBooleanValueForKey(GlobalSPA.KEY_DAIKAN);
		if (daikan_status) {
			mEditText_et_content_phone.setHint("请再输入客户手机号");
			tv_tip.setText("如想放弃带看记录，请填：12345678900");
			mButton_btn_start.setText("结束带看");
		} else {
			mEditText_et_content_phone.setHint("客户手机号");
			tv_tip.setText("注：客户手机仅后台能看，请放心填写");
			mButton_btn_start.setText("启动带看");
		}

		rl_bg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
			}
		});

		mEditText_et_content_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String str = s.toString();

				if (str.length() == 1) {
					if (str.equals("1") || str.equals("3")) {
						tv_error_tip.setVisibility(View.INVISIBLE);
					} else {
						tv_error_tip.setVisibility(View.VISIBLE);
					}
				} else if (str.length() >= 2) {
					if (str.substring(0, 1).equals("1")
							|| str.substring(0, 1).equals("3")) {
						tv_error_tip.setVisibility(View.INVISIBLE);

						if (str.length() == 11) {
							mButton_btn_start.setBackgroundDrawable(mContext
									.getResources().getDrawable(
											R.drawable.bg_daikan_btn));
							isBtnCanAct = true;
						} else {
							mButton_btn_start.setBackgroundDrawable(mContext
									.getResources().getDrawable(
											R.drawable.bg_daikan_btn_disabled));
							isBtnCanAct = false;
						}
					} else {
						tv_error_tip.setVisibility(View.VISIBLE);
					}
				} else {
					tv_error_tip.setVisibility(View.INVISIBLE);
				}
			}
		});

		mButton_btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phone = mEditText_et_content_phone.getText().toString()
						.trim();
				if (daikan_status) {

					if (phone.equals("12345678900")) {
						dlg.dismiss();
						endGuide(phone);
					} else {
						if (isBtnCanAct) {
							dlg.dismiss();
							endGuide(phone);
						}
					}
				} else {
					if (isBtnCanAct) {
						dlg.dismiss();
						String location = GlobalSPA.getInstance(mContext)
								.getStringValueForKey(GlobalSPA.KEY_LOCATION);
						if (location == null) {
							location = "";
						}
						startGuide(phone, location);
					}
				}

			}
		});

		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {

				}
				return false;
			}
		});

		DisplayMetrics dm = new DisplayMetrics();
		this.mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = window.getAttributes();
		// lp.width = dm.widthPixels*2/3;
		// lp.dimAmount = 0.2f;
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);

		dlg.show();
	}

	public void updateViewStatus() {
		if (mFloatView != null) {
			final boolean daikan_status = GlobalSPA.getInstance(this.mActivity)
					.getBooleanValueForKey(GlobalSPA.KEY_DAIKAN);
			if (daikan_status) {
				mFloatView.setBackgroundResource(R.drawable.bg_daikan_selected);
				mFloatView.setTextColor(0xffffffff);
			} else {
				mFloatView.setBackgroundResource(R.drawable.bg_daikan);
				mFloatView.setTextColor(0x00ffffff);
			}
		}
	}
	
	

	private void startGuide(String custtel, String description) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("custtel", custtel);
			obj.put("description", description);

			ClientHelper clientHelper = new ClientHelper(mActivity,
					ServerUrl.METHOD_startGuide, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在启动带看...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void endGuide(String custtel) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("custtel", custtel);

			ClientHelper clientHelper = new ClientHelper(mActivity,
					ServerUrl.METHOD_endGuide, obj.toString(), mHandler);
			clientHelper.setShowProgressMessage("正在结束带看...");
			clientHelper.isShowProgress(true);
			clientHelper.sendPost(true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeFloatView() {
//		 if (mWindowManager != null && mFloatLayout != null) {
//		 mWindowManager.removeView(mFloatView);
		 
//		 }
	}
}
