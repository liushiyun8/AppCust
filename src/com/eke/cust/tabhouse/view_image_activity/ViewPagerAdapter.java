package com.eke.cust.tabhouse.view_image_activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.eke.cust.AppContext;
import com.eke.cust.chat.widget.photoview.EasePhotoView;
import com.eke.cust.utils.BitmapUtils;
import com.eke.cust.utils.FileUtils;
import com.eke.cust.utils.MyLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
	private static final String TAG = "ViewPagerAdapter";

	private List<ViewImageNode> views = null;
	private ViewPager mViewPager = null;
	private Activity activity;
	private float downPos = 0;
	private Handler mHandler;
	private ImageLoader imageLoader;
	
	public ViewPagerAdapter(List<ViewImageNode> views, ViewPager viewPager, Activity activity, Handler handler) {
		this.views = views;
		this.mViewPager = viewPager;
		this.activity = activity;
		this.mHandler = handler;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
//		((ViewPager) arg0).removeView(views.get(arg1));
		((ViewPager) arg0).removeView((View)arg2);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
//		((ViewPager) container).addView(views.get(position), 0);
//		if (position == views.size() - 1) {
//
//		}
//		return views.get(position);
		final int index = position;
		EasePhotoView photoView = new EasePhotoView(container.getContext());
		photoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
              LayoutParams.WRAP_CONTENT));
		
		photoView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				MyLog.e("TAG", "clicked...");
				if (mHandler != null) {
					MyLog.e("TAG", "clicked...in");
					mHandler.sendEmptyMessage(ViewImageActivity.MSG_SHOW_SET_BTN);
				}
				return true;
			}
		});
		
		imageLoader.displayImage(views.get(position).getUrl(), photoView, AppContext.mDisplayImageOptions_no_round_corner, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap loadedImage) {
				// TODO Auto-generated method stub
				final String storageDir = FileUtils.getPicCache(activity);
				
//				String datestr = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(new Date());
				final String imgpath = views.get(index).getPicId()+".jpg";
				File jpgFile = new File(storageDir, imgpath);
				if (!jpgFile.exists()) {
					new BitmapUtils(activity).compressBmpToFile(loadedImage, jpgFile, 60, false);
				}
				
				views.get(index).setLocalImgPath(storageDir + File.separator + imgpath);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
		});

//		photoView.setImageResource(R.drawable.arrow_green);
		container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        return photoView;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
	
	@Override
    public void notifyDataSetChanged() {         
          super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {          
          return POSITION_NONE;
    }

}
