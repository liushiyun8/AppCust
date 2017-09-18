package com.eke.cust.tabhouse.upload_activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.eke.cust.utils.DateUtil;

public class LocalPhotoUtil {
	private static final String TAG = "LocalPhotoUtil";
	private Context mContext;
	private int totalPage = 0;
	private int currentPage = 0;
	private final int ITEM_PER_PAGE = 7;
	
	private static final String[]STORE_IMAGES={
		MediaStore.Images.Media.DISPLAY_NAME,
		MediaStore.Images.Media.LATITUDE,
		MediaStore.Images.Media.LONGITUDE,
		MediaStore.Images.Media._ID,
		MediaStore.Images.Media.BUCKET_ID,
		MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
		MediaStore.Images.Media.DATE_TAKEN,
		MediaStore.Images.Media.DATA,
	};
	
	private List<PhoteBean>list;
	
	private List<String> mAllDateWithPhotos = new ArrayList<String>();//存储所有有照片的日期
	private List<String> mAllDateWithPhotosFilter = new ArrayList<String>();//存储所有有照片的日期
	
	public LocalPhotoUtil(Context context) {
		this.mContext = context;
	}
	
	public List<PhoteBean> getPhotos(){
		List<PhoteBean>list = new ArrayList<PhoteBean>();
		Cursor cursor = MediaStore.Images.Media.query(mContext.getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
		//Map<String, PhoteBean>countMap = new HashMap<>();
		PhoteBean photo =null;
		while (cursor.moveToNext()) {
			String id = cursor.getString(3);
			String displayname = cursor.getString(0);
			//String imgid =cursor.getString(6);
			photo = new PhoteBean();
			photo.setDisplayname(displayname);
			photo.setId(id);
			//photo.setImageid(imgid);
			
			photo.setDate(DateUtil.getDateToString1(cursor.getLong(6)));
			photo.setDateLong(cursor.getLong(6));
			photo.setImagePath(cursor.getString(7));
			
			list.add(photo);
		}
		cursor.close();
		
		Collections.sort(list, new PhotoComparator());
		
		mAllDateWithPhotos.clear();
		for (int i = 0; i < list.size(); i++) {
			boolean isDateExist = false;
			for (int j = 0; j < mAllDateWithPhotos.size(); j++) {
				if (list.get(i).getDate().equals(mAllDateWithPhotos.get(j))) {
					isDateExist = true;
					break;
				}
			}
			
			if (!isDateExist) {
				mAllDateWithPhotos.add(list.get(i).getDate());
			}
		}
		
		for (int i = 0; i < mAllDateWithPhotos.size(); i++) {
			Log.d(TAG, i + ": " + mAllDateWithPhotos.get(i));
		}
		
		totalPage = mAllDateWithPhotos.size()/ITEM_PER_PAGE + ((mAllDateWithPhotos.size()%ITEM_PER_PAGE > 0)?1:0);
		if (totalPage > 0) {
			currentPage = 1;
		}
		
		mAllDateWithPhotosFilter.clear();
		if (totalPage > 0) {
			int addCount = 0;
			if (mAllDateWithPhotos.size() >= ITEM_PER_PAGE) {
				addCount = ITEM_PER_PAGE;
			}
			else {
				addCount = mAllDateWithPhotos.size()%ITEM_PER_PAGE;
			}
			
			for (int i = 0; i < addCount; i++) {
				mAllDateWithPhotosFilter.add(mAllDateWithPhotos.get(i));
			}
		}
		
		return list;
	}

	private class PhotoComparator implements Comparator<PhoteBean> {

		@Override
		public int compare(PhoteBean lhs, PhoteBean rhs) {
			// TODO Auto-generated method stub
			long l = lhs.getDateLong();
			long r = rhs.getDateLong();
			if (l > r) {
				return -1;
			}
			
			if (l < r) {
				return 1;
			}
			
			return 0;
		}
		
	}
	
	public int getItemsPerPage() {
		return this.ITEM_PER_PAGE;
	}
	
	public int getTotalPages() {
		return this.totalPage;
	}
	
	public int getCurrentPage() {
		return this.currentPage;
	}
	
	public void setCurrentPage(int page) {
		this.currentPage = page;
	}
	
	public List<String> getImageDateList() {
		return mAllDateWithPhotos;
	}
	
	public List<String> getImageDateListFilter() {
		return mAllDateWithPhotosFilter;
	}
}
