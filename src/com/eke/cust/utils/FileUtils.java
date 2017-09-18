package com.eke.cust.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
	private static final String TAG = "FileUtils";
	
	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
	
	private static String mDataCacheRootPath = null;
	
	private final static String FOLDER_NAME = "/eke";
	private final static String FOLDER_PICTEMP = "/pictemp";
	
	
	public FileUtils(Context context){
		mDataCacheRootPath = context.getCacheDir().getPath();
	}
	
	public static String getPicCache(Context context) {
		String path = context.getCacheDir().getPath() + FOLDER_PICTEMP;
		File cachedir = new File(path);
		if (!cachedir.exists()) {
			cachedir.mkdir();
		}
		
		if (cachedir != null) {
			File[] files = cachedir.listFiles();
			if (files.length > 10) {
				int delCount = files.length - 10;
				for (int i = 0; i < delCount; i++) {
					if (!files[i].isDirectory()) {
						files[i].delete();
					}
					
				}
			}
		}
		
		return path;
	}
	
	public static String getTakePictureStorageDirectory() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath + FOLDER_NAME : mDataCacheRootPath + FOLDER_NAME;
	}
	
	public String getStoragePath() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath : mDataCacheRootPath;
	}
	
	public static String getStorageDirectory(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath + FOLDER_NAME : mDataCacheRootPath + FOLDER_NAME;
	}
	
	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException{
		if(bitmap == null){
			return;
		}
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		File file = new File(path + File.separator + fileName);		
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
		Log.d(TAG, "----> img path = " + file.toString());
	}
	
	public Bitmap getBitmap(String fileName){
		return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
	}
	
	public boolean isFileExists(String fileName){
		return new File(getStorageDirectory() + File.separator + fileName).exists();
	}
	
	public long getFileSize(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName).length();
	}
	
	
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if(! dirFile.exists()){
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		
		dirFile.delete();
	}
}
