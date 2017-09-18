package com.eke.cust.model;

import java.io.Serializable;

/**
 * 用户定位信息
 * 
 * @author wujian
 * 
 */
public class UserLocation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public   String  address;
	public   String  city;
	public   String  citycode;
	public   double  mLongitude;
	public   double  mLatitude;

}
