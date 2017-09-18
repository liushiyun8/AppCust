package com.eke.cust.tabmine.profile_activity;

public class EstatePageNodeInfo {
	private int currentPage;
	private int pageSize;
	private int totalRecords;
	private String orderColumn;
	private boolean orderASC;
	private String districtname;
	private String estatename;
	private int recordStart;
	private int totalPages;
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}
	public boolean isOrderASC() {
		return orderASC;
	}
	public void setOrderASC(boolean orderASC) {
		this.orderASC = orderASC;
	}
	public String getDistrictname() {
		return districtname;
	}
	public void setDistrictname(String districtname) {
		this.districtname = districtname;
	}
	public String getEstatename() {
		return estatename;
	}
	public void setEstatename(String estatename) {
		this.estatename = estatename;
	}
	public int getRecordStart() {
		return recordStart;
	}
	public void setRecordStart(int recordStart) {
		this.recordStart = recordStart;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
}
