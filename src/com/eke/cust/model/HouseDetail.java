package com.eke.cust.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 房屋详情
 * 
 * @author wujian
 * 
 */
public class HouseDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String propertyid;
	public String propertyno;

	public String estatename;
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

	public String floorall;
	public long handoverdate;
	public String propertydirection;
	public String propertyfurniture;
	public String propertydecoration;
	public ArrayList<Ekefeature> listEkefeature;
	public String ekeheadpic;
	public long followtime;
	public boolean flagnoneagencyrent; //  这个字段=1，出现老人图标。！=1 则是妹子。
	public String flagdepositescrow;


	public HouseVo vo;

}
