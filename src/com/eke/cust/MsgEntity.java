package com.eke.cust;

public class MsgEntity {

	private String people;
	private String kefu;
	
	public MsgEntity() {
		super();
	}
	public MsgEntity(String people, String kefu) {
		super();
		this.people = people;
		this.kefu = kefu;
	}
	public String getPeople() {
		return people;
	}
	public void setPeople(String people) {
		this.people = people;
	}
	public String getKefu() {
		return kefu;
	}
	public void setKefu(String kefu) {
		this.kefu = kefu;
	}
	@Override
	public String toString() {
		return "MsgEntity [people=" + people + ", kefu=" + kefu + "]";
	}
	
}
