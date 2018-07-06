package com.encore.auctionServer.model;

import java.util.Vector;

public class Room {
	public Vector<String> idList; //경매참여자들
	public int auctionNum; //경매 번호
	public int maxPrice; //최고 금액
	public String maxPriceId; //최고금액제시자
	public Stuff stuff; //경매물품
	
	public Room() {
	}
}
