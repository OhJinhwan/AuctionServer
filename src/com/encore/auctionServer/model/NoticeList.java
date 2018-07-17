package com.encore.auctionServer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NoticeList {
	private int no; //글번호
	private String content; //글내용
	private String post; //게시자
	private Date day; //게시 날짜
	private String notice; //공지사항여부

	public NoticeList() {

	}

	public NoticeList(int no, String content, String post, Date day, String notice) {
		super();
		this.no = no;
		this.content = content;
		this.post = post;
		this.day = day;
		this.notice = notice;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public HashMap<String, String> convertBtoH() {
		HashMap<String, String> map = new HashMap<>();
		map.put("글번호", no+"");
		map.put("글내용",content);
		map.put("게시자", post);
		map.put("게시날짜", day+"");
		map.put("공지사항여부", "notice");
		return map;
	}

	public static NoticeList convertHtoB(HashMap<String, String> map) throws ParseException {

		String sDate = map.get("게시날짜");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date sd = sdf.parse(sDate);

		NoticeList n = new NoticeList(Integer.parseInt(map.get("글번호")), 
				map.get("글내용"), 
				map.get("게시자"), 
				sd, 
				map.get("공지사항여부"));
		return n;
	}
}