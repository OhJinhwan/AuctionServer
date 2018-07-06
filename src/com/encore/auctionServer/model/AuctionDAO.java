package com.encore.auctionServer.model;

import java.util.ArrayList;
import java.util.Date;

public class AuctionDAO {
	public int isLoginSuccess(String id, String password) {return 0;}
	//로그인 성공 여부(-1실패, 0탈퇴  1구매자 2판매자 3관리자)
	public boolean setJoin(Member m) {return true;}
	//회원 가입
	public void setRejoin(String id) {}
	//회원탈퇴 철회
	public String getID(String name, String phone) {return null;}
	//아이디 찾기
	public boolean isIDExisting(String id){return true;}
	//아이디 존재여부
	public String getPasswordQuestion(String id){return null;}
	//비밀번호 질문얻기
	public String getPassword(String id, String answer){return null;}
	//비밀번호 찾기
	public void setLeave(String id) {}
	//회원탈퇴
	public void setSeller(Member m) {}
	//판매자 등록
	public Member getMember(String id) {return null;}
	//회원정보얻기
	public boolean setMember(Member m) {return true;}
	//회원정보변경
	public ArrayList<Stuff> getStuffList(String tableType, String pageNum, String orderType) {return null;}
	//물품정보리스트얻기(tableType 0 : 과거 1: 현재 2: 예정, 페이지당 10줄, orderType 0: 날짜올림차순 1:날짜내림차순)
	public ArrayList<String[]> getNoticeList(String pageNum) {return null;}
	//게시판얻기(pageNum 1 : 0~10번까지 pageNum 2: 11~20번까지 ...1페이지는 공지사항 맨위로, 최신순정렬, 올린내용일부분만)
	public String getNoticeText(int no){return null;}
	//글번호에 해당하는 모든 내용 얻기
	public boolean setNotice(String[] noticeContent) {return true;}
	//게시글 올리기
	public ArrayList<Buy> getPurchasedList(String id) {return null;}
	//구매한 물품 리스트
	public ArrayList<Stuff> getBasketList(String id) {return null;}
	//장바구니 물품 리스트
	public ArrayList<Member> getSubscribeList(String id) {return null;}
	//구독 리스트
	public ArrayList<Alarm> getAlarmList(String id) {return null;}
	//알림 리스트
	public boolean setReport(String id) {return false;}
	//신고
	public void setRegStuff(Stuff s) {}
	//판매물품등록
	public int getExpectedPrice(Stuff s){return 1;}
	//예상 판매금액 받아보기--> 물품종류별 판매된 평균금액
	public boolean setCancelAuction(int AuctionNum) {return false;}
	//판매취소
	public Stuff getStuff(int AuctionNum) {return null;}
	//물품정보 얻기
	public boolean setStuff(Stuff s){return false;}
	//물품정보 수정
	public int getBalance() {return 0;}
	//총 수익금(서버) 얻기
	public ArrayList<Stuff> getNowStuffList(Date d, String pageNum) {return null;}
	//경매중인 물품의리스트얻기(Date이후로, pageNum( 1 -> 1~9번, 2-> 10~18번 ...) 최신순 정렬
	public ArrayList<Member> getBlackList(String pageNum) {return null;}
	//블랙리스트 얻기
	public boolean setBuy(int AuctionNum) {return true;}
	//물품 구매
	public boolean setAdjustGrade(String id, String grade) {return true;}
	//등급 조정
	public void setSubscribe(String id, String subscribeID) {}
	//구독
}
