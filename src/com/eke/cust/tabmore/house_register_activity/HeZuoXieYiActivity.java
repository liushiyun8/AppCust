package com.eke.cust.tabmore.house_register_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.AppContext;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.global.ToastUtils;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.MyLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class HeZuoXieYiActivity  extends BaseActivity{
	private static final String TAG = "HeZuoXieYiActivity";
	private static final int MSG_SHOW_DINGWEIMA = 10;
	private ScrollView mSv_xieyi;
	private ImageView mIv_xieyi;
	private ImageLoader imageLoader;
	private String dingweima = "1007";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			if (msg != null) {
				switch (msg.what) {
				case MSG_SHOW_DINGWEIMA:
				{
					if (dingweima != null) {
						new ToastUtils(HeZuoXieYiActivity.this).showToast(dingweima, Gravity.BOTTOM|Gravity.RIGHT);
					}
						
				}
					break;
					
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
							if (request_url.equals(ServerUrl.METHOD_queryListByLinkNOs)) {
								JSONArray array_data = jsonObject.optJSONArray("data");
								if (array_data != null) {
									for (int i = 0; i < array_data.length(); i++) {
										JSONObject object = array_data.getJSONObject(i);
										String url = object.optString("url");
										imageLoader.displayImage(url, mIv_xieyi, AppContext.mDisplayImageOptions_no_round_corner,
												new ImageLoadingListener() {
													
													@Override
													public void onLoadingStarted(String imageUri, View view) {
														// TODO Auto-generated method stub
														
													}
													
													@Override
													public void onLoadingFailed(String imageUri, View view,
															FailReason failReason) {
														// TODO Auto-generated method stub
														
													}
													
													@Override
													public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
														// TODO Auto-generated method stub
														
														if (loadedImage != null) {
															int bmp_width = loadedImage.getWidth();
															int bmp_height = loadedImage.getHeight();
															MyLog.d(TAG, "width: " + bmp_width + ", height: " + bmp_height);
															
															DisplayMetrics  dm = new DisplayMetrics();
															getWindowManager().getDefaultDisplay().getMetrics(dm);
															int screen_width = dm.widthPixels;
													        
													        Matrix matrix = new Matrix(); 
													        float scale = (float)screen_width/(float)bmp_width;
													        matrix.postScale(scale, scale); 
													        Bitmap resizeBmp = Bitmap.createBitmap(loadedImage,0,0,loadedImage.getWidth(),loadedImage.getHeight(),matrix,true);
													        mIv_xieyi.setImageBitmap(resizeBmp);
														}
														
													}
													
													@Override
													public void onLoadingCancelled(String imageUri, View view) {
														// TODO Auto-generated method stub
														
													}
												});
										break;
									}
								}
							}
						}
						else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg", "出错!");
							Toast.makeText(HeZuoXieYiActivity.this.getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(HeZuoXieYiActivity.this.getApplicationContext(), "出错!", Toast.LENGTH_SHORT).show();
					}
					
					break;
					
				case Constants.TAG_FAIL:
					Toast.makeText(HeZuoXieYiActivity.this.getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(HeZuoXieYiActivity.this.getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				
					
				default:
					break;
				}
				
			}
		
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_more_house_register_hezuoxieyi);
		
		imageLoader = ImageLoader.getInstance();
		
		initActivity();
		

		
	}
	
	private void initActivity() {
		mSv_xieyi = (ScrollView)findViewById(R.id.sv_xieyi);
		mIv_xieyi = (ImageView)findViewById(R.id.iv_xieyi);
		
		mSv_xieyi.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				{
					mHandler.postDelayed(runnable_count_time, 500);
				}
					break;
					
				case MotionEvent.ACTION_UP:
				{
					if (!isDingweimaShown) {					
						mHandler.removeCallbacks(runnable_count_time);
					}
					
					countLongPressedTime = 0;
					isDingweimaShown = false;
				}
					break;
				}
				return false;
			}
		});
	}
	
	private int countLongPressedTime = 0;
	private boolean isDingweimaShown = false;
	private Runnable runnable_count_time = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (++countLongPressedTime == 20) {
				countLongPressedTime = 0;
				isDingweimaShown = true;
				mHandler.sendEmptyMessage(MSG_SHOW_DINGWEIMA);
				
				mHandler.removeCallbacks(this);
				return;
			}
			
			mHandler.postDelayed(this, 500);
			
		}
	};
	
	public void BackClicked(View view) {
		finish();
	}
}