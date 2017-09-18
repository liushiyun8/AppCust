package com.eke.cust.model;

import java.io.Serializable;

public class House implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 楼盘Id -1 null时为自定义楼盘
	public String estateid;
	// (自定义楼盘)经度
	public double opestatelon;
	// (自定义楼盘)纬度
	public double opestatelat;
	// (自定义楼盘)名
	public String opestatename;
	// (自定义楼盘)所属区县
	public String opestatedistrict;
	//名字
	public String houseName;

}
