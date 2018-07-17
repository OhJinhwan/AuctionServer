package com.encore.auctionServer.model;

import java.io.Serializable;
import java.util.Date;

public class Stuff implements Serializable{
	//now
	private int auctionno;//경매번호
	private String sellerid;//판매자 아이디
	private String stuffname;//물품명
	private int minprice;//경매시작가
	private String chat;//채팅방유무
	private int upprice;//호가 시작 단위
	private String explain;//설명
	private Date timestart; //경매시작날짜
	private Date timeend; //경매종료날짜
	private String stuffkind; //물품 종류
	private String grade; //물품 등급
	private String success; //유찰 여부
	private int price;//낙찰가
	
	public Stuff() {
		
	}

	public Stuff(int auctionno, String sellerid, String stuffname, int minprice, String chat, int upprice,
			String explain, Date timestart, Date timeend, String stuffkind, String grade, String success, int price) {
		super();
		this.auctionno = auctionno;
		this.sellerid = sellerid;
		this.stuffname = stuffname;
		this.minprice = minprice;
		this.chat = chat;
		this.upprice = upprice;
		this.explain = explain;
		this.timestart = timestart;
		this.timeend = timeend;
		this.stuffkind = stuffkind;
		this.grade = grade;
		this.success = success;
		this.price = price;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public int getAuctionno() {
		return auctionno;
	}

	public void setAuctionno(int auctionno) {
		this.auctionno = auctionno;
	}

	public String getSellerid() {
		return sellerid;
	}

	public void setSellerid(String sellerid) {
		this.sellerid = sellerid;
	}

	public String getStuffname() {
		return stuffname;
	}

	public void setStuffname(String stuffname) {
		this.stuffname = stuffname;
	}

	public int getMinprice() {
		return minprice;
	}

	public void setMinprice(int minprice) {
		this.minprice = minprice;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getChat() {
		return chat;
	}

	public void setChat(String chat) {
		this.chat = chat;
	}

	public int getUpprice() {
		return upprice;
	}

	public void setUpprice(int upprice) {
		this.upprice = upprice;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getStuffkind() {
		return stuffkind;
	}

	public void setStuffkind(String stuffkind) {
		this.stuffkind = stuffkind;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public Date getTimestart() {
		return timestart;
	}

	public void setTimestart(Date timestart) {
		this.timestart = timestart;
	}

	public Date getTimeend() {
		return timeend;
	}

	public void setTimeend(Date timeend) {
		this.timeend = timeend;
	}	
}