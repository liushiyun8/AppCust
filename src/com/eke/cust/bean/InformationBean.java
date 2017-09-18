package com.eke.cust.bean;

import java.util.ArrayList;

public class InformationBean {
	public String result;
	public Bean data;

	public class Bean {
		public ArrayList<listNewsBean> listNews;
		public ArrayList<listFollowBean> listFollow;
	}

	public class listNewsBean {
		public String newsid;
		public String title;
		public int regdate;
	}

	public class listFollowBean {
		public String followid;
		public String propertyid;
		public String estatename;
		public String roomno;
		public String buildno;
		public String empname;
		public String content;
		public int followdate;
	}
}
