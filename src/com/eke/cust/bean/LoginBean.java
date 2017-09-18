package com.eke.cust.bean;

import java.util.ArrayList;

public class LoginBean {
	public String result;
	public Bean data;

	public class Bean {
		public String empno;
		public String token;
		public String ekeworkcityid;
		public String imgurl;
		public String mapperenable;
		public String locatcollectenable;
		public boolean guide;
		public ArrayList<listEstateBean> listEstate;
	}

	public class listEstateBean {
		public String estateid;
		public String estatename;
		public String ekeworkcityid;
		public boolean guide;
	}

}
