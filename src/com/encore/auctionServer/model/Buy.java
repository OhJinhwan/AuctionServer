package com.encore.auctionServer.model;

import java.io.Serializable;
import java.util.Date;


public class Buy implements Serializable{
   private String id; //아이디
   private int auctionno; //경매번호
   private String stuffname; //구매물품명
   private int price; //가격
   private Date buydate; //구매날짜
   
   public Buy() {
      
   }

   public Buy(String id, int auctionno, String stuffname, int price, Date buydate) {
      super();
      this.id = id;
      this.auctionno = auctionno;
      this.stuffname = stuffname;
      this.price = price;
      this.buydate = buydate;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public int getAuctionno() {
      return auctionno;
   }

   public void setAuctionno(int auctionno) {
      this.auctionno = auctionno;
   }

   public String getStuffname() {
      return stuffname;
   }

   public void setStuffname(String stuffname) {
      this.stuffname = stuffname;
   }

   public int getPrice() {
      return price;
   }

   public void setPrice(int price) {
      this.price = price;
   }

   public Date getBuydate() {
      return buydate;
   }

   public void setBuydate(Date buydate) {
      this.buydate = buydate;
   }
   
}