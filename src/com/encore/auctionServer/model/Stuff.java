package com.encore.auctionServer.model;

public class Stuff {
	String name;
	String age;
	
	public Stuff(String name, String age) {
		this.name = name;
		this.age = age;
	}
	
	public String convertToString() {
		String str = name+":"+age;
		return str;
	}
	public static Stuff converToStuff(String str) {
		String[] strList = str.split(":");
		
		return new Stuff(strList[0], strList[1]);
	}
}
