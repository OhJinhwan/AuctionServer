package com.encore.auctionServer.model;

import java.util.ArrayList;

public class Alarm {
	ArrayList<String> alarmList;
	
	public Alarm(ArrayList<String> alarmList) {
		this.alarmList = alarmList;
	}

	public ArrayList<String> getAlarmList() {
		return alarmList;
	}

	public void setAlarmList(ArrayList<String> alarmList) {
		this.alarmList = alarmList;
	}
}
