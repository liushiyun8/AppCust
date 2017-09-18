package com.eke.cust.tabhouse.view_image_activity;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewImageNode {
	private Context mContext;
	private String mUrl;
	private Handler mHandler;
	private String localImgPath;
	private String mPicId;
	
	public ViewImageNode(Context context, Handler handler, String url, String picid) {
		this.mContext = context;
		this.mHandler = handler;
		this.mUrl = url;
		this.mPicId = picid;		
	}
	
	public View genView(ImageLoader imageLoader, String url) {
		
//		PhotoView pv = new PhotoView(mContext);
//        pv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT));
//        
//        pv.setImageResource(R.drawable.arrow_green);
//        RelativeLayout layout = new RelativeLayout(mContext);
//        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT));
//        layout.setGravity(Gravity.CENTER);
//        layout.addView(pv);
//
//        return layout;
//        
//		LayoutInflater inflater = LayoutInflater.from(mContext);
//		View view = inflater.inflate(R.layout.layout_view_image_list_item, null);
//		this.mUrl = url;
//		
//		PhotoView photoView = (PhotoView)view.findViewById(R.id.iv_photo);
//		photoView.setImageResource(R.drawable.arrow_green);
//		
//		photoView.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//				MyLog.e("TAG", "clicked...");
//				if (mHandler != null) {
//					MyLog.e("TAG", "clicked...in");
//					mHandler.sendEmptyMessage(ViewImageActivity.MSG_SHOW_SET_BTN);
//				}
//				return true;
//			}
//		});
//		
//		
//		imageLoader.displayImage(url, photoView, EkeApplication.mDisplayImageOptions_no_round_corner, new ImageLoadingListener() {
//			
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap loadedImage) {
//				// TODO Auto-generated method stub
//				final String storageDir = FileUtils.getTakePictureStorageDirectory();
//				if (null != storageDir) {
//					File folderFile = new File(storageDir);
//					if(!folderFile.exists()){
//						folderFile.mkdir();
//					}
//				}
//				String datestr = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(new Date());
//				final String imgpath = "temp_"+datestr+".jpg";
//				File jpgFile = new File(storageDir, imgpath);
//				new BitmapUtils(mContext).compressBmpToFile(loadedImage, jpgFile, 60);
//				
//				localImgPath = storageDir + File.separator + imgpath;
//			}
//			
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
		return new View(mContext);
	}
	
	public String getUrl() {
		return this.mUrl;
	}
	
	public String getPicId() {
		return this.mPicId;
	}

	public void setLocalImgPath(String path) {
		this.localImgPath = path;
	}
	
	public String getLocalImgPath() {
		return this.localImgPath;
	}
}
