package com.eke.cust.global;

public class PageNodeInfo {
	private int currentPage = 1;
	private int pageSize = 20;
	private int totalRecords;
	private String orderColumn;
	private boolean orderASC;
	private String propertyid;
	private int totalPages = 1;
	private int recordStart;
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
	public String getPropertyid() {
		return propertyid;
	}
	public void setPropertyid(String propertyid) {
		this.propertyid = propertyid;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getRecordStart() {
		return recordStart;
	}
	public void setRecordStart(int recordStart) {
		this.recordStart = recordStart;
	}
}
