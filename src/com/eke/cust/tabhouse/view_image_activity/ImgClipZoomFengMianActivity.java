package com.eke.cust.tabhouse.view_image_activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.http.Callback;
import com.eke.cust.http.DataApi;
import com.eke.cust.http.HttpError;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.utils.DialogUtil;
import com.eke.cust.utils.FileUtils;
import com.eke.cust.utils.MyLog;
import com.eke.cust.widget.imgclipzoom.ClipImageLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Response;


public class ImgClipZoomFengMianActivity extends BaseActivity {
	private ClipImageLayout mClipImageLayout;
	private DialogUtil mDialogUtil;
	private static final int UPLOAD_SUCCESS = 10;
	private static final int UPLOAD_FAIL = 11;
	private String propertyid;
	
	private Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			super.dispatchMessage(msg);
			
			if (msg != null) {
				switch (msg.what) {
				case UPLOAD_SUCCESS:
				{
					mDialogUtil.dissmissProgressDialog();
					
					Bundle bundle = msg.getData();
					String resp = bundle.getString("res");
					if (resp.startsWith("[")) {
						resp = resp.substring(1, resp.length()-1);
					}
					try {
						JSONObject jsonObject = new JSONObject(resp);
						String result = jsonObject.optString("result", "");
						if (result.equals(Constants.RESULT_SUCCESS)) {
							Toast.makeText(getApplicationContext(), "设置成功!", Toast.LENGTH_SHORT).show();	
						}
						else  {
							AppContext.mHouseSourceNodeSetFengMian = null;
							String errorMsg = jsonObject.optString("errorMsg", "设置失败!");
							Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						AppContext.mHouseSourceNodeSetFengMian = null;
						Toast.makeText(getApplicationContext(), "失败!", Toast.LENGTH_SHORT).show();
					}
					
					finish();
				}
					break;
					
				case UPLOAD_FAIL:
				{
					mDialogUtil.dissmissProgressDialog();
					Toast.makeText(getApplicationContext(), "设置失败!", Toast.LENGTH_SHORT).show();
					AppContext.mHouseSourceNodeSetFengMian = null;
					finish();
				}
					break;
					
				case Constants.NO_NETWORK:
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_cut_fengmian);
		mDialogUtil = new DialogUtil(ImgClipZoomFengMianActivity.this);
		
		initActivity();
	}
	
	private void initActivity() {
		mClipImageLayout = (ClipImageLayout)findViewById(R.id.clipImageLayout);
		
		String imgUrl = getIntent().getStringExtra("img_url");
		propertyid = getIntent().getStringExtra("propertyid");
		
		int degreee = readBitmapDegree(imgUrl);
		Bitmap bitmap = createBitmap(imgUrl);
		if (bitmap != null) {
			if (degreee == 0) {
				mClipImageLayout.setImageBitmap(bitmap);
			} else {
				mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
			}
		} 
	}
	
	public void Confirm(View v) {
		mDialogUtil.setProgressMessage("正在设置...");
		mDialogUtil.showProgressDialog();
		
		Bitmap bitmap = mClipImageLayout.clip();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 100;
		bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > 40) {
			baos.reset();
			options -= 2;
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		
		String imgFilePath = FileUtils.getPicCache(ImgClipZoomFengMianActivity.this);
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		final String temp_file = imgFilePath + File.separator + sf.format(new Date()) + ".png";
		
		try {
			byte[] data = baos.toByteArray();
			
			if (AppContext.mHouseSourceNodeSetFengMian != null) {
				AppContext.mHouseSourceNodeSetFengMian.setEkeheadpic(new String(Base64.encode(data, Base64.DEFAULT)));
			}
			
			FileOutputStream fos = new FileOutputStream(temp_file);
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				String upload_path = ServerUrl.METHOD_PropertyHeadPic +
						"?token=" + AppContext
						.getInstance().getAppPref().userToken()+
						"&propertyid=" + propertyid;
				MyLog.d("UP", "upload: " + upload_path);
				MyLog.d("UP", "img: " + temp_file);
													
				DataApi.uploadFile(new File(temp_file), upload_path, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                    	MyLog.d("UP", "res: " + s);
                        if (null != s) {
                        	Message message = mHandler.obtainMessage();
                        	Bundle bundle = new Bundle();
                        	bundle.putString("res", s);
                        	message.what = UPLOAD_SUCCESS;
                        	message.setData(bundle);
                        	mHandler.sendMessage(message);
                        }else{
                        	mHandler.sendEmptyMessage(UPLOAD_FAIL);
                        }
                    }

                    @Override
                    public void failure(HttpError error) {
                    	MyLog.d("UP", "res: " + error.getLocalizedMessage());
                    	mHandler.sendEmptyMessage(UPLOAD_FAIL);
                    }
                });
			}
		}).start();						
		
	}
	
	/**
	 * 创建图片
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap createBitmap(String path) {
		if (path == null) {
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		FileInputStream is = null;
		Bitmap bitmap = null;
		try {
			is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	// 读取图像的旋转度
	private int readBitmapDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// 旋转图片
	private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return resizedBitmap;
	}
}
