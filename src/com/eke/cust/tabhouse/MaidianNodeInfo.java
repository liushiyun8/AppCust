package com.eke.cust.tabhouse;

public class MaidianNodeInfo {
	public String featureid;
	public String detail;
	public boolean isSelected = false;
	
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getFeatureid() {
		return featureid;
	}
	public void setFeatureid(String featureid) {
		this.featureid = featureid;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
}
