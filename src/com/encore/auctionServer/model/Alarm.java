package com.encore.auctionServer.model;

import java.io.Serializable;

public class Alarm implements Serializable{

	private String id; //아이디
	private String alarm; //알림

	public Alarm() {

	}

	public Alarm(String id, String alarm) {
		super();
		this.id = id;
		this.alarm = alarm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	@Override
	public String toString() {
		return "Alarm [id=" + id + ", alarm=" + alarm + "]";
	}
}