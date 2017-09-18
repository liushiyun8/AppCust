package com.eke.cust.tabmore.loc_collect_activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eke.cust.AppContext;
import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.R;
import com.eke.cust.UserLocationHelper;
import com.eke.cust.base.BaseActivity;
import com.eke.cust.http.Callback;
import com.eke.cust.http.DataApi;
import com.eke.cust.http.HttpError;
import com.eke.cust.net.ClientHelper;
import com.eke.cust.net.ServerUrl;
import com.eke.cust.tabmore.camera_activity.CameraPreview;
import com.eke.cust.tabmore.camera_activity.CameraPreview.OnCameraDisabledListener;
import com.eke.cust.utils.AbAppUtil;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.DensityUtil;
import com.eke.cust.utils.DialogUtil;
import com.eke.cust.utils.FileUtils;
import com.eke.cust.utils.GlobalSPA;
import com.eke.cust.utils.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Response;

public class LocCollectActivity extends BaseActivity implements OnClickListener, CameraPreview.OnCameraStatusListener, OnCameraDisabledListener{
private static final String TAG = "CameraActivity";
	private static final int UPLOAD_SUCCESS = 10;
	private static final int UPLOAD_FAIL = 11;
	public static final int MSG_FILTER_PARSE_START		= 0;
	public static final int MSG_FILTER_PARSE_DONE		= 1;
	
	private static final int REQUEST_LOCAL_IMG = 100;
	
	private static Context mContext  = null;	
	private boolean isHeightFirstSet = false;
	private boolean isPictureTaken	 = false;
	private boolean isTakingPicture	 = false; //是否正在拍照中
	
	public static int mPreviewWidth = 0;
	public static int mPreviewHeight = 0;
	public static int mXPosOffset = 0;
	public static int mYPosOffset = 0;
	
	//与裁剪图片相关的变量
	private int mCutPicXPos = 0;
	private int mCutPicYPos = 0;
	private int mCutPicWidth = 0;
	private int mCutPicHeight = 0;
		
	private int mPreviewShowXPos = 0;
	private int mPreviewShowYPos = 0;
	private int mPreviewShowXPosOffset = 0;
	private int mPreviewShowYPosOffset = 0;
	
	private int mCurrentFlashMode	= CameraPreview.FLASH_MODE_AUTO;
	private String mPhotoUrl 		= null; // 拍摄的照片的名字路径
	private String mPhotoUrlBackup 	= null; // 拍摄的照片的名字路径备份，主要用于删除照片
	private int mFromWhere		 = 0; // 记录是从哪里开启的camera
	
	private CameraPreview mCameraPreview;
	private ImageView mImageView_iv_back;
	private ImageView mImageView_iv_watermark;
	private ImageView mImageView_iv_final;
	private ImageView mButton_btn_capture;
	private TextView mTextView_tv_time;
	private TextView mTextView_tv_wireless_status;
	private TextView mTextView_tv_locat_status;
//	private ImageView mImageView_iv_menu;
	private ImageView mImageView_iv_light;
//	private ImageView mImageView_iv_oriention;
	
	private final int ACTION_OPEN_SYS_ALBUM	= 1;
	private final int ACTION_CUT_SYS_IMG	= 2;
	
	private Uri mAlbumImgUri;
	private DialogUtil mDialogUtil;
	
	private String mAddr;
	private String mDate;
	private double latitude;
	private double longitude;
	private String localImgPath;
	
	private String mCurretFlashMode	= Camera.Parameters.FLASH_MODE_AUTO;
	private int mScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
		
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage (Message msg) {
			super.handleMessage(msg);	
						
			switch(msg.what) {
			case UPLOAD_SUCCESS:
			{
				mDialogUtil.dissmissProgressDialog();
//				Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
				Bundle bundle = msg.getData();
				String data = bundle.getString("data");
				if (data != null && data.length() > 0) {
					try {
						JSONObject jsonObject = new JSONObject(data);
						String result = jsonObject.optString("result", "");
						if (result.equals(Constants.RESULT_SUCCESS)) {
							String path = jsonObject.optString("data", "");
							
							JSONObject obj = new JSONObject();
							obj.put("lon", longitude + "");
							obj.put("lat", latitude + "");
							obj.put("location", mAddr);
							obj.put("picurl", path);
							
							ClientHelper clientHelper = new ClientHelper(LocCollectActivity.this,
									ServerUrl.METHOD_insertLocatCollect, obj.toString(), mHandler);
							clientHelper.setShowProgressMessage("正在上传...");
							clientHelper.isShowProgress(true);
							clientHelper.sendPost(true);
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
				}
			}
				break;
				
			case UPLOAD_FAIL:
			{
				mDialogUtil.dissmissProgressDialog();
				Toast.makeText(getApplicationContext(), "上传失败!", Toast.LENGTH_SHORT).show();
				startPreviewAgain();
			}
				break;
				
			case UserLocationHelper.MSG_GLOBAL_LOCATION:
			{
				Bundle bundle = msg.getData();
				latitude = bundle.getDouble("latitude");
				longitude = bundle.getDouble("longitude");
				mAddr = bundle.getString("addr");
				
				if (BuildConfig.DEBUG) {
					MyLog.d(TAG, "latitude = " + latitude + ",longitude = " + longitude + ", addr = " + mAddr);
					MyLog.d(TAG, android.os.Build.VERSION.SDK + " = " + android.os.Build.VERSION.RELEASE );
				}
				
				if (mAddr != null && !mAddr.equals("")) {
					mTextView_tv_locat_status.setTextColor(0xff66cc00);
				}
				
//				mDialogUtil.dissmissProgressDialog();
//				mTextView_tv_shuoshuchengshi.setText(mCityName);
				
				
			}
				break;
				
			case UserLocationHelper.MSG_GLOBAL_LOCATION_FAIL:
			{
//				mDialogUtil.dissmissProgressDialog();
//				showLocationFailDlg();
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
						if (request_url.equals(ServerUrl.METHOD_getSystemTime)) {
							mDate = jsonObject.optString("data", "");
						}
						else if (request_url.equals(ServerUrl.METHOD_insertLocatCollect)) {
							Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
							startPreviewAgain();
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
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//set no title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//set to full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_tab_more_loc_collection);
		mContext = this;
		mDialogUtil = new DialogUtil(LocCollectActivity.this);
		initActivity();
		
		new UserLocationHelper(LocCollectActivity.this, mHandler);
		
		JSONObject obj = new JSONObject();
		ClientHelper clientHelper = new ClientHelper(LocCollectActivity.this,
				ServerUrl.METHOD_getSystemTime, obj.toString(), mHandler);
		clientHelper.setShowProgressMessage("正在获取数据...");
		clientHelper.isShowProgress(true);
		clientHelper.sendPost(true);
		
		MyLog.d(TAG, "onCreate");
	}
	
	private void initActivity() {
		mCameraPreview = (CameraPreview)findViewById(R.id.sv_camera_preview);
		mImageView_iv_back = (ImageView)findViewById(R.id.iv_back);
		mImageView_iv_watermark = (ImageView)findViewById(R.id.iv_watermark);
		mImageView_iv_final = (ImageView)findViewById(R.id.iv_final);
		mButton_btn_capture = (ImageView)findViewById(R.id.btn_capture);
		mTextView_tv_time = (TextView)findViewById(R.id.tv_time);
		mTextView_tv_wireless_status = (TextView)findViewById(R.id.tv_wireless_status);
		mTextView_tv_locat_status = (TextView)findViewById(R.id.tv_locat_status);
//		mImageView_iv_menu = (ImageView)findViewById(R.id.iv_menu);
		mImageView_iv_light = (ImageView)findViewById(R.id.iv_light);
//		mImageView_iv_oriention = (ImageView)findViewById(R.id.iv_oriention);
		
		mCameraPreview.setOnCameraStatusListener(this);
		mCameraPreview.setonCameraDisabledListener(this);
		
		mImageView_iv_back.setOnClickListener(this);
		mButton_btn_capture.setOnClickListener(this);
//		mImageView_iv_menu.setOnClickListener(this);
		mImageView_iv_light.setOnClickListener(this);
//		mImageView_iv_oriention.setOnClickListener(this);
		
		if (AbAppUtil.isNetworkAvailable(mContext)) {
			mTextView_tv_wireless_status.setTextColor(0xff66cc00);
		}
		
		mTextView_tv_locat_status.setText("Locat");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override  
	protected void onResume() { 
		super.onResume();  
		MyLog.d(TAG, "onResume");
    }
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyLog.d(TAG, "onDestroy");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {		
		case R.id.iv_back:
		{
			finish();
		}
			break;
			
		case R.id.btn_capture:
		{
			if (isTakingPicture) 
				break;
			
			isTakingPicture = true;
			mCameraPreview.takePicture();
		}
			break;
			
//		case R.id.iv_menu:
//		{
//			Intent intent = new Intent(LocCollectActivity.this, LocalImagePreviewActivity.class);
//			startActivityForResult(intent, REQUEST_LOCAL_IMG);
//		}
//			break;
			
		case R.id.iv_light:
		{
			if (mCurretFlashMode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
				mCurretFlashMode = Camera.Parameters.FLASH_MODE_ON;
				mImageView_iv_light.setImageResource(R.drawable.camera_light_on);
			}
			else if (mCurretFlashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
				mCurretFlashMode = Camera.Parameters.FLASH_MODE_OFF;
				mImageView_iv_light.setImageResource(R.drawable.camera_light_off);
			}
			else if (mCurretFlashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
				mCurretFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
				mImageView_iv_light.setImageResource(R.drawable.camera_light_auto);
			}
			mCameraPreview.changeFlashMode(mCurretFlashMode);
		}
			break;
			
//		case R.id.iv_oriention:
//		{
//			if (mScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
////				mScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
////				MyLog.d(TAG, "---orientation: " + mScreenOrientation);
////				mCameraPreview.setCustomScreenOrientation(mScreenOrientation);
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//				
//			}
//			else  if (mScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
////				mScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;
////				MyLog.d(TAG, "---orientation: " + mScreenOrientation);
////				mCameraPreview.setCustomScreenOrientation(mScreenOrientation);
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//				
//			}
//			
//		}
//			break;
			
		default:
			break;
		}
	}	
	
	
	private Uri createAlbumCutImgUri() {
		String storageDir = FileUtils.getTakePictureStorageDirectory();
		String datestr = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
		if (BuildConfig.DEBUG) Log.d(TAG, "file://" + storageDir + File.separator + datestr+".jpg");
		mPhotoUrl = storageDir + File.separator + datestr+".jpg";
		mPhotoUrlBackup = storageDir + File.separator + datestr+".jpg";
		return Uri.parse("file://" + storageDir + File.separator + datestr+".jpg");
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_LOCAL_IMG:
				if (null != data) {
				}
				break;
			}
		}	
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override  
	public void onWindowFocusChanged(boolean hasFocus) {
	    // TODO Auto-generated method stub  
	    super.onWindowFocusChanged(hasFocus);
	}

	//拍照成功后执行下一步
	private void pictureTakenToNextStep() {
		isPictureTaken = true;
		isTakingPicture = false;
	}
	
	
	@Override
	public void onPictureTaken(byte[] data) {
		// TODO Auto-generated method stub
		String storageDir = FileUtils.getTakePictureStorageDirectory();
		if (null != storageDir) {
			File folderFile = new File(storageDir);
			if(!folderFile.exists()){
				folderFile.mkdir();
			}
			if (BuildConfig.DEBUG) Log.d(TAG, "taken...");
			
			if (null != data) {
				if (BuildConfig.DEBUG) Log.d(TAG, "taken..1.");
				Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				if (BuildConfig.DEBUG) Log.d(TAG, "mPreviewWidth = " + mPreviewWidth + ", mPreviewHeight = " + mPreviewHeight);
				//将尺寸缩放到预览的尺寸
	            Bitmap sizeBitmap = null;
	            if (mScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
	            	sizeBitmap = rotaingImageView(90, Bitmap.createScaledBitmap(mBitmap, mPreviewWidth, mPreviewHeight, true)); 
				}
	            else {
	            	sizeBitmap = rotaingImageView(0, Bitmap.createScaledBitmap(mBitmap, mPreviewWidth, mPreviewHeight, true));
	            }
	            
	            
	            //保存jpg图像
	            Date date = new Date();
//	            String datestr = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(date);
	            String datestr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
	            String datestr = GlobalSPA.getInstance(LocCollectActivity.this).getStringValueForKey(GlobalSPA.KEY_EMPNO);
				File jpgFile = new File(storageDir, datestr1+".jpg");
				
				localImgPath = storageDir + File.separator + datestr1+".jpg";
				
				//记录下拍摄的照片的名字，然后回传给启动camera的activity
				mPhotoUrl = jpgFile.toString();
				mPhotoUrlBackup = jpgFile.toString();
				
				BitmapFactory.Options opts = new BitmapFactory.Options(); 
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(getResources(), R.drawable.camera_watermark, opts);  
				
				if (BuildConfig.DEBUG) Log.d(TAG, "mPreviewWidth = " + mPreviewWidth/5 + ", mPreviewHeight = " + (int)( ((double)(mPreviewWidth/5))/((double)opts.outWidth)*opts.outHeight));
				
				//Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.camera_watermark);
				//Bitmap scaled_watermark = Bitmap.createScaledBitmap(watermark, mPreviewHeight/5,(int)( ((double)(mPreviewHeight/5))/((double)opts.outWidth)*opts.outHeight), false);
				
				Bitmap finalBitmap = new BitmapUtils(mContext).compressBmpToFile(doodle(sizeBitmap, null, datestr1, datestr), jpgFile);
				
//				mButton_btn_capture.setVisibility(View.INVISIBLE);
//				mImageView_iv_back.setVisibility(View.INVISIBLE);
//				mTextView_tv_time.setVisibility(View.VISIBLE);
//				mTextView_tv_time.setText(datestr1 + "\n" + datestr);
//				mImageView_iv_watermark.setVisibility(View.VISIBLE);
//				mImageView_iv_watermark.setImageBitmap(scaled_watermark);
				mImageView_iv_final.setImageBitmap(finalBitmap);
				mImageView_iv_final.setVisibility(View.VISIBLE);
				
				int picOriention = readPictureDegree(mPhotoUrl);
				if (BuildConfig.DEBUG) Log.d(TAG, "picOriention: " + picOriention);
				
				showActionDlg();
				
				//mHandler.postDelayed(runnable, 2000);
			}
		}
		
		
	}
	
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
	    //旋转图片 动作  
	    Matrix matrix = new Matrix();;  
	    matrix.postRotate(angle);  
	    System.out.println("angle2=" + angle);  
	    // 创建新的图片  
	    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
	            bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
	    return resizedBitmap;  
	}  
	
	public Bitmap doodle(Bitmap src, Bitmap watermark, String time, String time1)  
    {  
        // 另外创建一张图片  
        Bitmap newb = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图  
        Canvas canvas = new Canvas(newb);  
        canvas.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入原图片src  
        //canvas.drawBitmap(watermark, (src.getWidth() - watermark.getWidth()) / 2, (src.getHeight() - watermark.getHeight()) / 2, null); // 涂鸦图片画到原图片中间位置  
        
        if(time!=null)
        {          
            TextPaint textPaint=new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTypeface(Typeface.DEFAULT);
            textPaint.setTextSize(30);
            textPaint.bgColor = Color.WHITE;
            
            Paint paintBg = new Paint();
            paintBg.setColor(Color.WHITE);
            
            float x = DensityUtil.dip2px(mContext, 10);
            
            if (mScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            	canvas.drawRect(x-10, mPreviewWidth-DensityUtil.dip2px(mContext, 52)-30, 
            			x-10 + 580, mPreviewWidth-DensityUtil.dip2px(mContext, 36) + 55, paintBg);
            	
            	canvas.drawText(mDate, x, mPreviewWidth-DensityUtil.dip2px(mContext, 52), textPaint); 
            	canvas.drawText(longitude + ";" + latitude, x, mPreviewWidth-DensityUtil.dip2px(mContext, 36), textPaint); 
                canvas.drawText(time1 + ", " + mAddr, x, mPreviewWidth-DensityUtil.dip2px(mContext, 20), textPaint);
			}
            else {
            	canvas.drawRect(x-10, mPreviewHeight-DensityUtil.dip2px(mContext, 52)-30, 
            			x-10 + 580, mPreviewHeight-DensityUtil.dip2px(mContext, 36) + 55, paintBg);
            	
            	canvas.drawText(mDate, x, mPreviewHeight-DensityUtil.dip2px(mContext, 52), textPaint); 
            	canvas.drawText(longitude + ";" + latitude, x, mPreviewHeight-DensityUtil.dip2px(mContext, 36), textPaint); 
                canvas.drawText(time1 + ", " + mAddr, x, mPreviewHeight-DensityUtil.dip2px(mContext, 20), textPaint);
            }
            
        }
        
        canvas.save(Canvas.ALL_SAVE_FLAG);  
        canvas.restore();  
          
//        watermark.recycle();  
//        watermark = null;  
          
        return newb;  
    }  
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
//			mImageView_iv_final.setVisibility(View.GONE);
//			pictureTakenToNextStep();
//			mCameraPreview.startPreview();
			
			showActionDlg();
		}
	};
	
	public static int readPictureDegree(String path) {  
	    int degree  = 0;  
	    try {  
	        ExifInterface exifInterface = new ExifInterface(path);  
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
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
	
	private void showActionDlg() {
		final Dialog dlg = new Dialog(LocCollectActivity.this, R.style.dialog);
		LayoutInflater inflater = LayoutInflater.from(LocCollectActivity.this);
		View viewContent = inflater.inflate(R.layout.dlg_loc_collection_acion, null);
		
		final TextView tv_tip = (TextView)viewContent.findViewById(R.id.tv_tip);
		tv_tip.setText("所在地：" + mAddr + "\n现在上传？");
		
		final Button btn_chakan = (Button)viewContent.findViewById(R.id.btn_chakan);
		btn_chakan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				mHandler.postDelayed(runnable, 2000);
			}
		});
		
		final Button btn_fangqi = (Button)viewContent.findViewById(R.id.btn_fangqi);
		btn_fangqi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				
				startPreviewAgain();
			}
		});
		
		final Button btn_shangchuan = (Button)viewContent.findViewById(R.id.btn_shangchuan);
		btn_shangchuan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				if (localImgPath != null) {
					mDialogUtil.setProgressMessage("请稍候...");
					mDialogUtil.showProgressDialog();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String upload_path = ServerUrl.METHOD_EkeLocatCollectPic + 
									"&token=" + AppContext
									.getInstance().getAppPref().userToken();
							MyLog.d("f", "----img = " + localImgPath );									
							DataApi.uploadFile(new File(localImgPath), upload_path, new Callback<String>() {
			                    @Override
			                    public void success(String s, Response response) {
			                    	MyLog.d("f", "----res = " + s );
			                        if (null != s) {
			                        	Message message = mHandler.obtainMessage();
			                        	message.what = UPLOAD_SUCCESS;
			                        	Bundle bundle = new Bundle();
			                        	bundle.putString("data", s);
			                        	message.setData(bundle);
			                        	mHandler.sendMessage(message);
			                        }else{
			                        	mHandler.sendEmptyMessage(UPLOAD_FAIL);
			                        }
			                    }

			                    @Override
			                    public void failure(HttpError error) {
			                    	MyLog.e("f", "----error = " + error );
			                    	mHandler.sendEmptyMessage(UPLOAD_FAIL);
			                    }
			                });
						}
					}).start();	
				}
			}
		});
		
		dlg.setContentView(viewContent);
		dlg.setCanceledOnTouchOutside(false);
		
		DisplayMetrics  dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		Window window = dlg.getWindow();
		window.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp =window.getAttributes();
		lp.width = dm.widthPixels - DensityUtil.dip2px(LocCollectActivity.this, 30);
		lp.dimAmount = 0.5f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(lp);
		
		dlg.show();
	}
	
	private void startPreviewAgain() {
		mImageView_iv_final.setVisibility(View.GONE);
		pictureTakenToNextStep();
		mCameraPreview.startPreview();
	}
	
	@Override
	public void onAutoFocus(boolean success) {
		// TODO Auto-generated method stub
		if (success) {			
		} else {
			isTakingPicture = false;
		}
	}

	@Override
	public void OnCameraDisabled() {
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) {
			Log.e(TAG, "camera is disabled");
		}
		
		new AlertDialog.Builder(LocCollectActivity.this)
		.setTitle(getString(R.string.main_bottom_tab_camera_caution))
		.setMessage(getString(R.string.main_bottom_tab_camera_caution_content))
		.setPositiveButton(getString(R.string.str_confirm), 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
//		 if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			mScreenOrientation = Configuration.ORIENTATION_LANDSCAPE;
//			MyLog.d(TAG, "---orientation: " + mScreenOrientation);
//			mCameraPreview.setCustomScreenOrientation(mScreenOrientation);
//			mImageView_iv_oriention.setImageResource(R.drawable.camera_l2p);
//		 }
//		 else  if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//			mScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
//			MyLog.d(TAG, "---orientation: " + mScreenOrientation);
//			mCameraPreview.setCustomScreenOrientation(mScreenOrientation);
//			mImageView_iv_oriention.setImageResource(R.drawable.camera_p2l);
//		 }
	}

	
	
}
