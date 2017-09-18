package com.eke.cust.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

/**
 * 异步加载图片工具类
 * 
 * 
 * 
 */
public class LoadImageUtil {
	private LoadImageUtil() {
	}

	private static LoadImageUtil instance = null;

	public static synchronized LoadImageUtil getInstance() {
		if (instance == null) {
			instance = new LoadImageUtil();
		}
		return instance;
	}

	/**
	 * 从内存卡中异步加载本地图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public void displayFromSDCard(String uri, ImageView imageView) {
		// String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
		ImageLoader.getInstance().displayImage("file://" + uri, imageView);
	}

	/**
	 * 从assets文件夹中异步加载图片
	 * 
	 * @param imageName
	 *            图片名称，带后缀的，例如：1.png
	 * @param imageView
	 */
	public void dispalyFromAssets(String imageName, ImageView imageView) {
		// String imageUri = "assets://image.png"; // from assets
		ImageLoader.getInstance().displayImage("assets://" + imageName,
				imageView);
	}

	/**
	 * 从drawable中异步加载本地图片
	 * 
	 * @param imageId
	 * @param imageView
	 */
	public void displayFromDrawable(int imageId, ImageView imageView) {
		// String imageUri = "drawable://" + R.drawable.image; // from drawables
		// (only images, non-9patch)
		ImageLoader.getInstance().displayImage("drawable://" + imageId,
				imageView);
	}

	/**
	 * 从内容提提供者中抓取图片
	 */
	public void displayFromContent(String uri, ImageView imageView) {
		// String imageUri = "content://media/external/audio/albumart/13"; //
		// from content provider
		ImageLoader.getInstance().displayImage("content://" + uri, imageView);
	}

	// TODO:���Ǵ�ͼ
	public void displayImage(String uri, final ImageView imageView) {

		ImageLoader.getInstance().displayImage(uri, imageView,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap loadedImage) {

					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});
	}

	// TODO:���Ǵ�ͼ
	public void displayImage(String uri, final ImageView imageView,
			final int defaultImage) {

		ImageLoader.getInstance().displayImage(uri, imageView,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						imageView.setImageResource(defaultImage);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						imageView.setImageBitmap(loadedImage);

					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						imageView.setImageResource(defaultImage);

					}
				});
	}

	public void displayImage(String uri, final ImageView imageView,
			ImageLoadingListener imageLoadingListener) {

		ImageLoader.getInstance().displayImage(uri, imageView,
				imageLoadingListener);
	}

	public void clear(String uri) {
		DiskCacheUtils.removeFromCache(uri, ImageLoader.getInstance().getDiskCache());
		MemoryCacheUtils.removeFromCache(uri, ImageLoader.getInstance().getMemoryCache());
	}
}
