package com.eke.cust.tabhouse;

import java.util.ArrayList;
import java.util.List;

public class HouseSourceNodeInfo {
//	private Bitmap houseImg;
	private String propertyid;
	public String estatename;
	private String estateid;
	private String roomno;
	private String buildno;
	private String trade;
	private double rentprice;
	private double price;
	private String unitname;
	private String rentunitname;
	private String ekesurvey;
	private String propertyno;
	private String countf;
	private String countt;
	private String countw;
	private String county;
	private double square;
	private int floor;
	private String floorall;
	private long handoverdate;
	private String empfollowid;
	private String ekeheadpic;
	private String isFollowApply;
	private String ekefeature;
	private List<MaidianNodeInfo> ekefeaturelistC = new ArrayList<MaidianNodeInfo>();
	private String content;
	private String empname;
	private int schedulenum;
	private long assigndate;
	private long scheduledate;
	private String collectempid;
	
//	public Bitmap getHouseImg() {
//		return houseImg;
//	}
//	public void setHouseImg(Bitmap houseImg) {
//		this.houseImg = houseImg;
//	}
	public String getPropertyid() {
		return propertyid;
	}
	public void setPropertyid(String propertyid) {
		this.propertyid = propertyid;
	}
	public String getEstatename() {
		return estatename;
	}
	public void setEstatename(String estatename) {
		this.estatename = estatename;
	}
	public String getTrade() {
		return trade;
	}
	public void setTrade(String trade) {
		this.trade = trade;
	}
	
	public String getUnitname() {
		return unitname;
	}
	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}
	public String getRentunitname() {
		return rentunitname;
	}
	public void setRentunitname(String rentunitname) {
		this.rentunitname = rentunitname;
	}
	public String getEkesurvey() {
		return ekesurvey;
	}
	public void setEkesurvey(String ekesurvey) {
		this.ekesurvey = ekesurvey;
	}
	public String getPropertyno() {
		return propertyno;
	}
	public void setPropertyno(String propertyno) {
		this.propertyno = propertyno;
	}
	public String getCountf() {
		return countf;
	}
	public void setCountf(String countf) {
		this.countf = countf;
	}
	public String getCountt() {
		return countt;
	}
	public void setCountt(String countt) {
		this.countt = countt;
	}
	public String getCountw() {
		return countw;
	}
	public void setCountw(String countw) {
		this.countw = countw;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	
	public long getHandoverdate() {
		return handoverdate;
	}
	public void setHandoverdate(long handoverdate) {
		this.handoverdate = handoverdate;
	}
	public int getSchedulenum() {
		return schedulenum;
	}
	public void setSchedulenum(int schedulenum) {
		this.schedulenum = schedulenum;
	}
	public String getEmpfollowid() {
		return empfollowid;
	}
	public void setEmpfollowid(String empfollowid) {
		this.empfollowid = empfollowid;
	}
	public String getEkefeature() {
		return ekefeature;
	}
	public void setEkefeature(String ekefeature) {
		this.ekefeature = ekefeature;
	}
	
	public List<MaidianNodeInfo> getEkeMaidian() {
		return this.ekefeaturelistC;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getScheduledate() {
		return scheduledate;
	}
	public void setScheduledate(long scheduledate) {
		this.scheduledate = scheduledate;
	}
	
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public double getRentprice() {
		return rentprice;
	}
	public void setRentprice(double rentprice) {
		this.rentprice = rentprice;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getSquare() {
		return square;
	}
	public void setSquare(double square) {
		this.square = square;
	}
	
	public String getEstateid() {
		return estateid;
	}
	public void setEstateid(String estateid) {
		this.estateid = estateid;
	}
	public String getRoomno() {
		return roomno;
	}
	public void setRoomno(String roomno) {
		this.roomno = roomno;
	}
	public String getBuildno() {
		return buildno;
	}
	public void setBuildno(String buildno) {
		this.buildno = buildno;
	}
	public String getFloorall() {
		return floorall;
	}
	public void setFloorall(String floorall) {
		this.floorall = floorall;
	}
	public List<MaidianNodeInfo> getEkefeaturelistC() {
		return ekefeaturelistC;
	}
	public void setEkefeaturelistC(List<MaidianNodeInfo> ekefeaturelistC) {
		this.ekefeaturelistC = ekefeaturelistC;
	}
	public String getEmpname() {
		return empname;
	}
	public void setEmpname(String empname) {
		this.empname = empname;
	}
	public long getAssigndate() {
		return assigndate;
	}
	public void setAssigndate(long assigndate) {
		this.assigndate = assigndate;
	}
	public String getIsFollowApply() {
		return isFollowApply;
	}
	public void setIsFollowApply(String isFollowApply) {
		this.isFollowApply = isFollowApply;
	}
	public String getEkeheadpic() {
		return ekeheadpic;
	}
	public void setEkeheadpic(String ekeheadpic) {
		this.ekeheadpic = ekeheadpic;
	}
	public String getCollectempid() {
		return collectempid;
	}
	public void setCollectempid(String collectempid) {
		this.collectempid = collectempid;
	}
}
