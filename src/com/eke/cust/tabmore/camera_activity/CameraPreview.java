package com.eke.cust.tabmore.camera_activity;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.eke.cust.BuildConfig;
import com.eke.cust.Constants;
import com.eke.cust.tabmore.loc_collect_activity.LocCollectActivity;
import com.eke.cust.utils.MyLog;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "CameraPreview";
	private SurfaceHolder mSurfaceHolder = null; 
    private Camera mCamera = null;
    private boolean mPreviewRunning = false;
    private MyPictureCallback mMyPictureCallback = null;
    private Context mContext;
    private Activity mActivity;
    private int mRotateDegrees;
    private int mCustomScreenOrientation = Configuration.ORIENTATION_PORTRAIT;
    
    public static final int FLASH_MODE_AUTO		= 0;
    public static final int FLASH_MODE_ON		= 1;
    public static final int FLASH_MODE_OFF		= 2;
    private String mCurretFlashMode				= Camera.Parameters.FLASH_MODE_AUTO;
    
    private static final int FRONT_CAMERA		= 0;
    private static final int BACK_CAMERA		= 1;
    private int mCameraPosition = FRONT_CAMERA; //0代表前置摄像头，1代表后置摄像头
    
    /** 监听接口 */ 
    private OnCameraStatusListener mOnCameraStatusListener = null;
    private OnCameraDisabledListener mOnCameraDisabledListener = null;

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		setKeepScreenOn(true);
		
		if (context instanceof Activity) {
			this.mActivity = ((Activity)context);
		}
		
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
		
		mMyPictureCallback = new MyPictureCallback();
		
		sdk = getSDKInt();  
	}
	
	public void setCustomScreenOrientation(int orientation) {
		this.mCustomScreenOrientation = orientation;
		
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		
		initCamera();
	}
	
	private int getSDKInt() {  
        // this is safe so that we don't need to use SDKInt which is only  
        // available after 1.6  
        try {  
            return Integer.parseInt(Build.VERSION.SDK);  
        } catch (Exception e) {  
            return 3; // default to target 1.5 cupcake  
        }  
    }  
	
	// 设置监听事件 
    public void setOnCameraStatusListener(OnCameraStatusListener listener) { 
        this.mOnCameraStatusListener = listener; 
    }

    public void setonCameraDisabledListener(OnCameraDisabledListener listener) {
    	this.mOnCameraDisabledListener = listener;
    }
    
    public void startPreview() {
    	mCamera.startPreview();
    }
    
    private static int getCameraDisplayOrientation(Activity activity, int cameraId) {
    	Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    	Camera.getCameraInfo(cameraId, cameraInfo);
    	
    	int degrees = 0;
    	switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
		case Surface.ROTATION_0:	
			degrees = 0;
			break;
			
		case Surface.ROTATION_90:	
			degrees = 90;
			break;
			
		case Surface.ROTATION_180:	
			degrees = 180;
			break;
			
		case Surface.ROTATION_270:
			degrees = 270;
			break;

		default:
			break;
		}
    	
    	int res = 0;
    	if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
    		res = (cameraInfo.orientation + degrees)%360;
    		res = (360 - res)%360;
    	}
    	else {
    		res = (cameraInfo.orientation - degrees + 360) % 360;
    	}
    	
    	return res;
    }
    
    //更改闪光灯模式
    public void changeFlashMode(String flash_mode) {
    	if (mPreviewRunning) {
			mCamera.stopPreview();
		}
    	Camera.Parameters parameters = mCamera.getParameters();
    	
//    	switch (flash_mode) {
//		case FLASH_MODE_AUTO:
//			mCurretFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
//			break;
//			
//		case FLASH_MODE_ON:
//			mCurretFlashMode = Camera.Parameters.FLASH_MODE_ON;
//			break;
//			
//		case FLASH_MODE_OFF:
//			mCurretFlashMode = Camera.Parameters.FLASH_MODE_OFF;
//			break;
//
//		default:
//			break;
//		}
    	
    	mCurretFlashMode = flash_mode;
    	    	
    	parameters.setFlashMode(mCurretFlashMode);
    	mCamera.setParameters(parameters); 
    	mCamera.startPreview();
    	
//    	initCamera();
    }
    
	private void initChangedCamera(int cameraId) {
    	mCamera.stopPreview();
    	mCamera.release();
    	mCamera = null;
    	
    	mCamera = Camera.open(cameraId);
    	try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    	
    	//在横屏情况下的长宽
    	int land_width = Constants.SCREEN_HEIGHT;
		int land_height = Constants.SCREEN_WIDTH;
		
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPictureFormat(PixelFormat.JPEG);
		
		List<Camera.Size> psizes_preview = parameters.getSupportedPreviewSizes();
		for (int i=0; i<psizes_preview.size(); i++) {
			if (BuildConfig.DEBUG) Log.d(TAG, "support preview: width = " + psizes_preview.get(i).width + ", height =" + psizes_preview.get(i).height);
		}
		Size bestPreviewSize = getBestPreviewSize(psizes_preview, land_height);
		if (bestPreviewSize.width != 0 && bestPreviewSize.height != 0)
			parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
				
		parameters.setJpegQuality(85);
		parameters.setRotation(270);
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO); //闪光灯模式
		mCamera.setParameters(parameters); 
//		mCamera.setDisplayOrientation(90); //在2.2以上可以使用 
		
		Camera.Size defaultPreviewSize = mCamera.getParameters().getPreviewSize();
		if (BuildConfig.DEBUG) Log.d(TAG, "default preview : " + defaultPreviewSize.width + ":" + defaultPreviewSize.height);
		
	    int w = 0, h = 0;
	    //先判断预览的尺寸是否足够全屏显示
	    if (land_width > defaultPreviewSize.width) {
	    	w = land_width;
	    	float fw = (float)land_width / (float)defaultPreviewSize.width;
		    if (BuildConfig.DEBUG) Log.d(TAG, "ration " + fw );
	    	h = (int) (defaultPreviewSize.height * fw);
	    	
	    	//记录下偏移量
	    	CameraActivity.mXPosOffset = h - defaultPreviewSize.height;
	    	CameraActivity.mYPosOffset = 0;
	    	
	    	LocCollectActivity.mXPosOffset = h - defaultPreviewSize.height;
	    	LocCollectActivity.mYPosOffset = 0;
	    	
	    } else {
	    	w = defaultPreviewSize.width;
	    	h = defaultPreviewSize.height;
	    	CameraActivity.mXPosOffset = h - land_height;
	    	CameraActivity.mYPosOffset = w - land_width;
	    	
	    	LocCollectActivity.mXPosOffset = h - land_height;
	    	LocCollectActivity.mYPosOffset = w - land_width;
	    }	    
	    
	    //因为拍下的照片是旋转了90度的，所有这里长宽需要调换
	    CameraActivity.mPreviewWidth = h;
	    CameraActivity.mPreviewHeight = w;   
	    
	    LocCollectActivity.mPreviewWidth = h;
	    LocCollectActivity.mPreviewHeight = w;
	    
	    if (BuildConfig.DEBUG) Log.d(TAG, "the final preview : " + w + ":" + h);
	    
	    FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(w, h);
		this.setLayoutParams(localLayoutParams);
		
    	mCamera.startPreview();    	
    }
    
    //切换前后摄像头
    @SuppressLint("NewApi")
	public void changeCamera() {
    	int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); //得到摄像头的个数
        for(int i = 0; i < cameraCount; i++) {
        	Camera.getCameraInfo(i, cameraInfo); //得到每一个摄像头的信息
        	if (mCameraPosition == FRONT_CAMERA) { 
        		if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {
        			mCameraPosition = BACK_CAMERA;
        			initChangedCamera(i);        			
        			break;
        		}
        		
        	} else if (mCameraPosition == BACK_CAMERA) { 
        		if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        			mCameraPosition = FRONT_CAMERA;
        			initChangedCamera(i);        			
        			break;
        		}
        		
        	}
        }
    }
    
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) Log.d(TAG, "surfaceCreated");
		try {
			openCamera();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (this.mOnCameraDisabledListener != null) {
				this.mOnCameraDisabledListener.OnCameraDisabled();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) Log.d(TAG, "surfaceChanged: " + width + ", " + height);
		
		//在横屏情况下的长宽
		int land_width = Constants.SCREEN_HEIGHT;
		int land_height = Constants.SCREEN_WIDTH;
		
		initCamera();		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) Log.d(TAG, "surfaceDestroyed");
		releaseCamera();
	}
	
	private void openCamera() throws Exception {
		releaseCamera();
		
		try {
			mCamera = Camera.open(mCameraPosition);
			
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("Camera is disabled");
		}
		
		
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			releaseCamera();
		}
	}
	
	private void initCamera() {
		if (null != mCamera) {
			Camera.Parameters parameters = mCamera.getParameters();
			mCamera.stopPreview();
//			if (null != mActivity) {
//				mRotateDegrees = getCameraDisplayOrientation(this.mActivity, mCameraPosition);
//				if (BuildConfig.DEBUG) Log.d(TAG, "mRotateDegrees = " + mRotateDegrees);
//				mCamera.setDisplayOrientation(mRotateDegrees); //在2.2以上可以使用 
//			}
			
			if (this.mCustomScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
				mRotateDegrees = 90;
			}
			else {
				mRotateDegrees = 0;
			}
			
			MyLog.d(TAG, "**---orientation: " + this.mCustomScreenOrientation);
			
			mCamera.setDisplayOrientation(mRotateDegrees); //在2.2以上可以使用
			
			parameters.setPictureFormat(PixelFormat.JPEG);	
			
			List<Camera.Size> psizes_preview = parameters.getSupportedPreviewSizes();
			for (int i=0; i<psizes_preview.size(); i++) {
				if (BuildConfig.DEBUG) Log.d(TAG, "support preview: width = " + psizes_preview.get(i).width + ", height =" + psizes_preview.get(i).height);
			}
			Size bestPreviewSize = getBestPreviewSize(psizes_preview, getWidth());
			
			if (this.mCustomScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
				bestPreviewSize = getBestPreviewSize(psizes_preview, getWidth());
			}
			else {
				bestPreviewSize = getBestPreviewSize(psizes_preview, getHeight());
			}
			
			if (bestPreviewSize.width != 0 && bestPreviewSize.height != 0) {
				parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
				
				//在确定好preview大小之后，再确定picturesize大小
				List<Camera.Size> psizes_picture = parameters.getSupportedPictureSizes();
				for (int i=0; i<psizes_picture.size(); i++) {
					if (BuildConfig.DEBUG) Log.d(TAG, "support picture: width = " + psizes_picture.get(i).width + ", height =" + psizes_picture.get(i).height);
				}
				Size bestPicSize = getBestPictureSize(psizes_picture, getWidth(), bestPreviewSize);
				if (this.mCustomScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
					bestPicSize = getBestPictureSize(psizes_picture, getWidth(), bestPreviewSize);
				}
				else {
					bestPicSize = getBestPictureSize(psizes_picture, getHeight(), bestPreviewSize);
				}
				
				if (bestPicSize.width != 0 && bestPicSize.height != 0) {
					parameters.setPictureSize(bestPicSize.width, bestPicSize.height);
				}				
			}
					
			parameters.setJpegQuality(100);
			
//			parameters.setRotation(mRotateDegrees);
			if (this.mCustomScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
				parameters.set("orientation", "portrait"); 
			}
			else {
				parameters.set("orientation", "landscape"); 
			}
			
			parameters.setFlashMode(mCurretFlashMode); //闪光灯模式
			mCamera.setParameters(parameters); 
			
			if (sdk <= 4) {  
                // 1.5 & 1.6  
                Camera.Parameters parameters1 = mCamera.getParameters();  
                if (this.mCustomScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
    				parameters.set("orientation", "portrait"); 
    			}
    			else {
    				parameters.set("orientation", "landscape"); 
    			}
                mCamera.setParameters(parameters1);  
            } else {  
            	if (this.mCustomScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            		setDisplayOrientation(mCamera, 90);  
    			}
    			else {
    				setDisplayOrientation(mCamera, 0);  
    			}
                
            }   
			
			Camera.Size defaultPreviewSize = mCamera.getParameters().getPreviewSize();
			if (BuildConfig.DEBUG) Log.d(TAG, getWidth() +"--best preview size: " + defaultPreviewSize.width + ":" + defaultPreviewSize.height);
			
			Camera.Size defaultPictureSize = mCamera.getParameters().getPictureSize();
			if (BuildConfig.DEBUG) Log.d(TAG, "best picture size: " + defaultPictureSize.width + ":" + defaultPictureSize.height);
			
			int w = 0, h = 0;
		    //先判断预览的尺寸是否足够全屏显示
		    if (getWidth() > defaultPreviewSize.width) {
		    	w = getWidth();
		    	float fw = (float)getWidth() / (float)defaultPreviewSize.width;
			    if (BuildConfig.DEBUG) Log.d(TAG, "ratio =  " + fw );
		    	h = (int) (defaultPreviewSize.height * fw);
		    	
		    	//记录下偏移量
		    	CameraActivity.mXPosOffset = h - defaultPreviewSize.height;
		    	CameraActivity.mYPosOffset = 0;
		    	
		    	LocCollectActivity.mXPosOffset = h - defaultPreviewSize.height;
		    	LocCollectActivity.mYPosOffset = 0;
		    	
		    } else {
		    	w = defaultPreviewSize.width;
		    	h = defaultPreviewSize.height;
		    	CameraActivity.mXPosOffset = h - getWidth();
		    	CameraActivity.mYPosOffset = w - getHeight();
		    	
		    	LocCollectActivity.mXPosOffset = h - getWidth();
		    	LocCollectActivity.mYPosOffset = w - getHeight();
		    }	    
		    
		    //因为拍下的照片是旋转了90度的，所有这里长宽需要调换
//		    if (mRotateDegrees/90 == 1 || mRotateDegrees/90 == 3) {
//		    	CameraActivity.mPreviewWidth = h;
//			    CameraActivity.mPreviewHeight = w; 
//			}
//		    else {
		    	CameraActivity.mPreviewWidth = w;
			    CameraActivity.mPreviewHeight = h; 
			    
			    LocCollectActivity.mPreviewWidth = w;
			    LocCollectActivity.mPreviewHeight = h; 
//		    }
			    
			    
		    if (BuildConfig.DEBUG) Log.d(TAG, "the final preview : " + w + ":" + h);
		    
		    if (this.mCustomScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
		    	FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(h, w);
				this.setLayoutParams(localLayoutParams);
			}
			else {
				FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(w, h);
				this.setLayoutParams(localLayoutParams);
			}
		    		
			mCamera.startPreview();  
			mPreviewRunning = true;
		}
	}
	
	private int sdk = 3; 
	private void setDisplayOrientation(Camera camera, int angle) {  
        Method downPolymorphic;  
  
        if (sdk <= 4)  
            return;  
  
        try {  
            if (sdk > 4 && sdk < 8) {  
  
                // parameters for pictures created by a Camera service.  
                Camera.Parameters parameters = mCamera.getParameters();  
  
                // 2.0, 2.1  
                downPolymorphic = parameters.getClass().getMethod(  
                        "setRotation", new Class[] { int.class });  
                if (downPolymorphic != null)  
                    downPolymorphic.invoke(parameters, new Object[] { angle });  
  
                // Sets the Parameters for pictures from this Camera  
                // service.  
                mCamera.setParameters(parameters);  
  
            } else {  
  
                downPolymorphic = camera.getClass().getMethod(  
                        "setDisplayOrientation", new Class[] { int.class });  
                if (downPolymorphic != null)  
                    downPolymorphic.invoke(camera, new Object[] { angle });  
            }  
        } catch (Exception e) {  
        }  
    } 
	
	private void releaseCamera(){
		if (this.mCamera == null)
		      return;
		
		this.mCamera.stopPreview();
		this.mCamera.release();
		this.mCamera = null;		   
	}
	
	public void takePicture() {
		if (mCamera != null) {
			if (BuildConfig.DEBUG) Log.d(TAG, "start to take a picture");
			// 自动对焦
			mCamera.autoFocus(new AutoFocusCallback() {
				
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					// TODO Auto-generated method stub
					if (null != mOnCameraStatusListener) {
						if (mCameraPosition == BACK_CAMERA)
							mOnCameraStatusListener.onAutoFocus(success); 
                    } 
					
					if (success || mCameraPosition == FRONT_CAMERA) {
						if (BuildConfig.DEBUG) Log.d(TAG, "take a picture");
						mCamera.takePicture(null, null, mMyPictureCallback);
					}
				}
			});
		}
	}
	
	
	private final class MyPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// 停止照片拍摄 
//			mCamera.stopPreview(); 
//			mCamera = null; 
 
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "MyPictureCallback");
			}
            // 调用结束事件 
            if (null != mOnCameraStatusListener) { 
            	mOnCameraStatusListener.onPictureTaken(data); 
            	if (BuildConfig.DEBUG) {
    				Log.d(TAG, "MyPictureCallback...");
    			}
            }
			
		}
		
	}
	
	/** 
     * 相机拍照监听接口 
     */ 
    public interface OnCameraStatusListener {
 
        // 相机拍照结束事件 
        void onPictureTaken(byte[] data); 
 
        // 拍摄时自动对焦事件 
        void onAutoFocus(boolean success); 
    } 
    
    /*
     * 相机被禁用接口
     */
    public interface OnCameraDisabledListener {
    	// 相机被禁止掉
    	void OnCameraDisabled();
    }
    
    //--------------------------------------------
    private Size getBestPreviewSize(List<Camera.Size> list, int th){  
        Collections.sort(list, new CameraSizeComparatorUpper());
        // 先判断是不是竖屏模式
        boolean isPortrait = false;
        if (getWidth() < getHeight()) {
        	isPortrait = true;
        }
        int i = 0;
        
        //先查找有没有与屏幕尺寸相同的，有的话就返回
        for(Size s:list){  
        	// 竖屏模式下长宽要对调
        	if (isPortrait) {
        		if(s.height == getWidth() && s.width == getHeight()){
                    if (BuildConfig.DEBUG) Log.d(TAG, "same : best preview size:w = " + s.height + ", h = " + s.width);  
                    return list.get(i);    
                } 
        	}
        	else {
        		if(s.height == getHeight() && s.width == getWidth()){
                    if (BuildConfig.DEBUG) Log.d(TAG, "same : best preview size:w = " + s.width + ", h = " + s.height);  
                    return list.get(i);   
                } 
        	}
             
            i++;  
        }
        
        i = 0;        
        for(Size s:list){  
        	// 竖屏模式下长宽要对调
        	if (isPortrait) {
        		if(((s.height >= th) && equalRate(s, 1.3333f)) || ((s.height >= th) && equalRate(s, 1.7777f))){
                    if (BuildConfig.DEBUG) Log.d(TAG, "best preview size:w = " + s.height + ", h = " + s.width);  
                    break;  
                } 
        	}
        	else {
        		if(((s.width >= th) && equalRate(s, 1.3333f)) || ((s.width >= th) && equalRate(s, 1.7777f))){
                    if (BuildConfig.DEBUG) Log.d(TAG, "best preview size:w = " + s.width + ", h = " + s.height);  
                    break;  
                } 
        	}
             
            i++;  
        }
        
        if (i== list.size()) {
        	list.get(0).height = 0;
        	list.get(0).width = 0;
        	return list.get(0);
        }
        
        return list.get(i);  
    }  
    
    private Size getBestPictureSize(List<Camera.Size> list, int th, Size bestPreviewSize){  
        Collections.sort(list, new CameraSizeComparatorUpper());    
        // 先判断是不是竖屏模式
        boolean isPortrait = false;
        if (getWidth() < getHeight()) {
        	isPortrait = true;
        }
        
        int i = 0;  
        for(Size s:list){  
        	//首先寻找配置与previewsize相同的
        	if (s.width == bestPreviewSize.width && s.height == bestPreviewSize.height) {
        		if (BuildConfig.DEBUG) Log.d(TAG, "find same....");
        		break;
        	}
        	
        	// 竖屏模式下长宽要对调
        	if (isPortrait) {
        		if(((s.height >= th) && equalRate(s, 1.3333f) && equalRate(bestPreviewSize, 1.3333f)) || ((s.height >= th) && equalRate(s, 1.7777f) && equalRate(bestPreviewSize, 1.7777f))){
                    if (BuildConfig.DEBUG) Log.d(TAG, "port: best preview size:w = " + s.height + ", h = " + s.width);  
                    break;  
                } 
        	}
        	else {
        		if(((s.width >= th) && equalRate(s, 1.3333f) && equalRate(bestPreviewSize, 1.3333f)) || ((s.width >= th) && equalRate(s, 1.7777f) && equalRate(bestPreviewSize, 1.7777f))){
                    if (BuildConfig.DEBUG) Log.d(TAG, "land: best preview size:w = " + s.width + ", h = " + s.height);  
                    break;  
                } 
        	}
        	
            i++;  
        }  
        
        if (i== list.size()) {
        	list.get(0).height = 0;
        	list.get(0).width = 0;
        	return list.get(0);
        }
  
        return list.get(i);  
    }    
    
    public boolean equalRate(Size s, float rate){  
        float r = (float)(s.width)/(float)(s.height);  
        if(Math.abs(r - rate) <= 0.2) {  
            return true;  
        }  
        else{  
            return false;  
        }  
    }  
    
    public class CameraSizeComparator implements Comparator<Camera.Size>{  
        //按降序排列          
		@Override
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){  
	            return 0;  
	        }  
	        else if(lhs.width > rhs.width){  
	            return -1;  
	        }  
	        else{  
	            return 1;  
	        }  
		}  
          
    } 
    
    public class CameraSizeComparatorUpper implements Comparator<Camera.Size>{  
        //按升序排列          
		@Override
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){  
	            return 0;  
	        }  
	        else if(lhs.width > rhs.width){  
	            return 1;  
	        }  
	        else{  
	            return -1;  
	        }  
		}  
          
    }  
    
    // 判断摄像头是否能够使用
    public static boolean checkCameraCanUse() {
    	boolean canUse = true;
    	Camera camera = null;
    	try {
    		camera = Camera.open();
    		
		} catch (Exception e) {
			// TODO: handle exception
			canUse = false;
		}
    	
    	if (canUse) {
    		camera.release();
    		camera = null;
    	}
    	
    	return canUse;
    }
}