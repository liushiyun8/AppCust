package com.eke.cust.tabhouse.upload_activity;

import java.io.Serializable;

public class PhoteBean  implements Serializable{
	private String displayname;
	public String pictype;
	private String id;
	private String imageid;
    private boolean select;
    private String date;
    private long dateLong;
    private String imagePath;
    private int imageId;
    
	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getImageid() {
		return imageid;
	}

	public void setImageid(String imageid) {
		this.imageid = imageid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getDateLong() {
		return dateLong;
	}

	public void setDateLong(long dateLong) {
		this.dateLong = dateLong;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

}
