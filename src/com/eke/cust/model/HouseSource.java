package com.eke.cust.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 房屋
 * 
 * @author wujian
 * 
 */
public class HouseSource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String propertyid;
	public String estatename;
	public String roomno;
	public String buildno;
	public String estateid;
	public String trade;
	public String rentprice;
	public String price;
	public String unitname;
	public String rentunitname;
	public String countf;
	public String countt;
	public String countw;
	public String county;
	public String square;
	public String floor;
	public String ekesurvey;
	public String propertyno;
	public String propertytype;

	public String floorall;
	public long handoverdate;
	public String propertydirection;
	public String propertyfurniture;
	public String propertydecoration;
	public ArrayList<Ekefeature> listEkefeature;
	public String ekeheadpic;
	public String flagnoneagencyrent;
	public String flagdepositescrow;
	public long followtime;
	public String empid;
	public String cityid;
	public String lon;
	public String lat;
	public String minrentprice;
	public String maxrentprice;
	public String minprice;
	public String maxprice;
	public String minsquare;
	public String maxsquare;
	public String empfollowid;
	public String custid;
	public int totalPages;
	public int recordStart;
	public String iscollect;

	// 类型
	public int type;
}
