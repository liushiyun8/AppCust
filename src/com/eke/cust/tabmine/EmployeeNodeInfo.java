package com.eke.cust.tabmine;

import java.util.ArrayList;
import java.util.List;

public class EmployeeNodeInfo {
	private String empid;
	private String empname;
	private String tel;
	private long joindate;
	private String deptname;
	private String city;
	private String district;
	private String ekeestate;
	private List<EstateNodeInfo> listEstateGet = new ArrayList<EstateNodeInfo>();
	public String getEmpid() {
		return empid;
	}
	public void setEmpid(String empid) {
		this.empid = empid;
	}
	public String getEmpname() {
		return empname;
	}
	public void setEmpname(String empname) {
		this.empname = empname;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public long getJoindate() {
		return joindate;
	}
	public void setJoindate(long joindate) {
		this.joindate = joindate;
	}
	public String getDeptname() {
		return deptname;
	}
	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}
	public String getEkeestate() {
		return ekeestate;
	}
	public void setEkeestate(String ekeestate) {
		this.ekeestate = ekeestate;
	}
	public List<EstateNodeInfo> getListEstate() {
		return listEstateGet;
	}
	public void addListEstate(EstateNodeInfo node) {
		this.listEstateGet.add(node);
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public List<EstateNodeInfo> getListEstateGet() {
		return listEstateGet;
	}
	public void setListEstateGet(List<EstateNodeInfo> listEstateGet) {
		this.listEstateGet = listEstateGet;
	}
	
	
}
