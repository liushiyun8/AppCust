package com.eke.cust.tabmore.camera_activity;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.eke.cust.BuildConfig;

public class MediaScanner {
	private static final String TAG = "MediaScanner";
	private Context mContext = null;
	private File mFilePath = null;
	private String mFileType = null;
	private MediaScannerConnection mediaScanConn = null;
	private MediaSannerClient client = null;

	public MediaScanner(Context context) {
		mContext = context;
	}
	
	public void scanAllMedia(String scan_path) {
		client = new MediaSannerClient();
		mediaScanConn = new MediaScannerConnection(mContext, client);
		scanfile(new File(scan_path));

	}

	class MediaSannerClient implements
			MediaScannerConnection.MediaScannerConnectionClient {
		 

		public void onMediaScannerConnected() {
			if (BuildConfig.DEBUG)
				Log.d(TAG, "---------> media service connected");

			if (mFilePath != null) {

				if (mFilePath.isDirectory()) {
					File[] files = mFilePath.listFiles();
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							if (files[i].isDirectory())
								scanForder(files[i]);
							else {
								mediaScanConn.scanFile(files[i].getAbsolutePath(), mFileType);
							}
						}
					}
				}
			}

			mFilePath = null;
			mFileType = null;
		}

		public void onScanCompleted(String path, Uri uri) {
			// TODO Auto-generated method stub
			mediaScanConn.disconnect();
			
			if (BuildConfig.DEBUG)
				Log.d(TAG, "media service disconnect  <--------- ");
		}
	}

	private void scanForder(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory())
						scanForder(files[i]);
					else {
						mediaScanConn.scanFile(files[i].getAbsolutePath(), mFileType);
					}
				}
			}
		}
		else {
			mediaScanConn.scanFile(file.getAbsolutePath(), mFileType);
		}
	}
	
	private void scanfile(File f) {
		if (BuildConfig.DEBUG)
			Log.d(TAG, "*** scanfile ");
		mFilePath = f;
		mediaScanConn.connect();
	}

	public void updateGallery(String filename) {
		MediaScannerConnection.scanFile(mContext, new String[] { filename },
				null, new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.d(TAG, "Scanned " + path + ":");
						Log.d(TAG, "-> uri=" + uri);
					}
				});
	}
	
	public void deleteFromGallery(String filepath) {
		String params[] = new String[]{filepath};
		mContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", params);
		mContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Thumbnails.DATA + " LIKE ?", params);
	}
}