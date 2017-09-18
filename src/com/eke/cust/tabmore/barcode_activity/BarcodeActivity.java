package com.eke.cust.tabmore.barcode_activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DensityUtil;

import org.droidparts.annotation.inject.InjectView;
import org.json.JSONException;
import org.json.JSONObject;

public class BarcodeActivity extends BaseActivity {
	@InjectView(id=R.id.iv_barcode)
	private ImageView mIv_barcode;

	
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
							if (request_url.equals(ServerUrl.METHOD_getAboutUsUrl)) {
								JSONObject obj_data = jsonObject.getJSONObject("data");
								String APPCustUrl = obj_data.optString("APPCustUrl", "");
								Bitmap qrcodeBitmap = QRCodeUtil.createQRImage(APPCustUrl, DensityUtil.dip2px(BarcodeActivity.this, 360),
										DensityUtil.dip2px(BarcodeActivity.this, 360), BitmapFactory.decodeResource(getResources(), R.drawable.icon_logo), null);
								if (qrcodeBitmap != null) {
									mIv_barcode.setImageBitmap(qrcodeBitmap);
								}
							}

						}
						else if (result.equals(Constants.RESULT_ERROR)) {
							String errorMsg = jsonObject.optString("errorMsg", "出错!");
							Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					}
					
					break;
					
				case Constants.TAG_FAIL:
					Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				case Constants.TAG_EXCEPTION:
					Toast.makeText(getApplicationContext(), "请求出错!", Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		}
	};
	protected View onCreateContentView() {
		return inflateContentView(R.layout.activity_tab_more_barcode);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("二维码下载APP");
		registerLeftImageView(R.drawable.arrow_back);
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(BarcodeActivity.this,
				ServerUrl.METHOD_getAboutUsUrl, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
		
	}




}
