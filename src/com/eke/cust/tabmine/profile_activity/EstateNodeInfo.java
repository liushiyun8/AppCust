package com.eke.cust.tabmine.profile_activity;

public class EstateNodeInfo {
	public String estateid;
	public String estatename;
	public String areaname;
	public String districtname;
	public double lat;
	public double lon;


	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}



	private boolean isSelected = false;

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getDistrictname() {
		return districtname;
	}

	public void setDistrictname(String districtname) {
		this.districtname = districtname;
	}

	public String getEstateid() {
		return estateid;
	}
	public void setEstateid(String estateid) {
		this.estateid = estateid;
	}
	public String getEstatename() {
		return estatename;
	}
	public void setEstatename(String estatename) {
		this.estatename = estatename;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
