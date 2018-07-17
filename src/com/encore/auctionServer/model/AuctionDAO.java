package com.encore.auctionServer.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.Properties;

import com.encore.auctionServer.model.Member;
import com.encore.auctionServer.view.ServerView;

public class AuctionDAO {
   Connection conn;
   PreparedStatement pstmt;
   PreparedStatement pstmt2;
   ResultSet rs;

   Properties pro;

   ServerView serverView;
   
   public AuctionDAO(ServerView serverView) {
      try {
         pro = new Properties();
         pro.load(new FileReader("conn/conn.properties"));

         Class.forName(pro.getProperty("driver"));
         
         this.serverView = serverView;
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }//생성자

   private void connect() {
      try {
         conn = DriverManager.getConnection(pro.getProperty("url"), pro);
         //System.out.println("DB연결 성공!!");
         serverView.dbWorking();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }//connect

   private void disconnect() {
      try {
         if(rs!=null) rs.close();
         if(pstmt!=null) pstmt.close();
         if(conn!=null) conn.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }//disconnect

   public int isLoginSuccess(String id, String pw) {
      connect();
      try {
         String sql="select count(*) from member where id=? and pw=? and seller='구매자'";
         String sql2="select count(*) from member where id=? and pw=? and seller='판매자'";
         
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id); 
         pstmt.setString(2, pw);
         
         rs = pstmt.executeQuery();
         rs.next();
         int cnt = rs.getInt(1);
         if(cnt==1) return 1;//로그인 성공
         
         pstmt2 = conn.prepareStatement(sql2);
         pstmt2.setString(1, id); 
         pstmt2.setString(2, pw);
         
         rs = pstmt2.executeQuery();
         rs.next();
         int cnt2 = rs.getInt(1);
         if(cnt2==1) return 2;
         
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return -1;
   }//로그인 성공 여부(-1실패, 0탈퇴  1구매자 2판매자 3관리자)

   public boolean setJoin(Member m) {
      connect();
      try {
         String sql="insert into member(id,pw,name,email,ssn1,ssn2,phone,addr,card,ask,answer,seller) values (?,?,?,?,?,?,?,?,?,?,?,'구매자')";

         pstmt = conn.prepareStatement(sql);//sql문을 미리 전달!!
         //pstmt.setString(물음표 순서(1~), 설정할 데이터);

         pstmt.setString(1, m.getId());
         pstmt.setString(2, m.getPw());
         pstmt.setString(3, m.getName());
         pstmt.setString(4, m.getEmail());
         pstmt.setInt(5, m.getSsn1());
         pstmt.setInt(6, m.getSsn2());
         pstmt.setString(7, m.getPhone());
         pstmt.setString(8, m.getAddr());
         pstmt.setInt(9, m.getCard());
         pstmt.setString(10, m.getAsk());
         pstmt.setString(11, m.getAnswer());
         pstmt.executeUpdate();//sql문 실행 요청!!

         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//회원 가입

   //public void setRejoin(String id) {}
   //회원탈퇴 철회

   public String getID(String name, String phone) {
      connect();
      try {
         String sql = "select id from member where name=? and phone=?";
         pstmt = conn.prepareStatement(sql);//sql문 전달 ----> DB서버(sql문 파싱)
         pstmt.setString(1, name);
         pstmt.setString(2, phone);
         rs = pstmt.executeQuery();//실행요청

         rs.next();
         String id = rs.getString("id");
         return id;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      //return false;
      return null;
   }//아이디 찾기

   public boolean isIDExisting(String id){
      //중복된 아이디 존재 true 리턴
      connect();
      try {
         String sql = "select count(*) count from member where id=?";
         pstmt = conn.prepareStatement(sql);//sql문 전달 ----> DB서버(sql문 파싱)
         pstmt.setString(1, id);
         rs = pstmt.executeQuery();//실행요청

         rs.next();
         int count = rs.getInt("count");//rs.getInt(1);
         if(count==1) return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//아이디 존재여부

   public String getPasswordQuestion(String id){
      connect();
      try {
         String sql = "select ask from member where id=?";
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1,id);
         rs = pstmt.executeQuery();

         rs.next();
         String ask = rs.getString("ask");
         return ask;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return null;
   }//비밀번호 질문얻기

   public String getPassword(String id, String answer){
      connect();
      try {
         String sql = "select pw from member where id=? and answer=?";
         pstmt = conn.prepareStatement(sql);//sql문 전달 ----> DB서버(sql문 파싱)
         pstmt.setString(1, id);
         pstmt.setString(2, answer);
         rs = pstmt.executeQuery();//실행요청

         rs.next();
         String pw = rs.getString("pw");
         return pw;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      //return false;
      return null;
   }//비밀번호 찾기

   public boolean setLeave(String id) {
      connect();
      try {
         String sql = "delete from member where id=?";
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id);
         int t = pstmt.executeUpdate();
         if(t==1) return true;//삭제된 행의 수가 1일때 
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//회원탈퇴

   public boolean requestSell(Stuff s) {///////////////////////
      connect();
      try {
         String sql="insert into stuff values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
         pstmt = conn.prepareStatement(sql);//sql문을 미리 전달!!
         //pstmt.setString(물음표 순서(1~), 설정할 데이터);

         pstmt.setInt(1, s.getAuctionno());
         pstmt.setString(2, s.getSellerid());
         pstmt.setString(3, s.getStuffname());
         pstmt.setInt(4, s.getMinprice());
         pstmt.setInt(5, s.getPrice());
         pstmt.setString(6, s.getChat());
         pstmt.setInt(7, s.getUpprice());
         pstmt.setString(8, s.getExplain());
         pstmt.setDate(9, (java.sql.Date) s.getTimestart());
         pstmt.setDate(10, (java.sql.Date) s.getTimeend());
         pstmt.setString(11, s.getStuffkind());
         pstmt.setString(12, s.getGrade());
         pstmt.setString(13, s.getSuccess());
         pstmt.setInt(14, s.getPrice());
         pstmt.executeUpdate();//sql문 실행 요청!!

         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//판매 상품 등록 신청

   public boolean standby(Member m) {
      connect();
      try {
         String sql="insert into standby values (?,?)";
         pstmt = conn.prepareStatement(sql);//sql문을 미리 전달!!
         //pstmt.setString(물음표 순서(1~), 설정할 데이터);

         pstmt.setString(1, m.getId());
         pstmt.setDate(2, (java.sql.Date) m.getApplydate());
         pstmt.executeUpdate();//sql문 실행 요청!!

         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//판매자 대기 테이블에 등록

   public boolean setSeller(String id) {
      connect();
      try {
         String sql="insert into seller values (?,0)";
         String sql2="update member set seller='판매자' where id=?";
         
         pstmt = conn.prepareStatement(sql);//sql문을 미리 전달!!
         //pstmt.setString(물음표 순서(1~), 설정할 데이터);
         pstmt.setString(1,id);
         int t1 = pstmt.executeUpdate();//sql문 실행 요청!!
         
         pstmt2 = conn.prepareStatement(sql2);//데이터를 제외한 sql문 미리 전달
         pstmt2.setString(1, id);

         int t2 = pstmt2.executeUpdate();//sql문 실행 요청
         
         if(t1==1 && t2==1) return true;//t: 수정된 행의 갯수
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//판매자 테이블에 등록

   public boolean standbyDelete(String id) {
      connect();
      try {
         String sql = "delete from standby where id=?";
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id);
         int t = pstmt.executeUpdate();
         if(t==1) return true;//삭제된 행의 수가 1일때 
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//판매자 대기 테이블에 삭제

   public Member getMember(String id) {
      connect();
      Member m = null;//null ---> 결과값 없음을 표현

      try {
         String sql = "select pw,name,ssn1,ssn2,phone,addr,seller,card,total,grade,ask,answer, email from member where id=?";
         pstmt = conn.prepareStatement(sql);//sql문 미리 전달
         //?수만큼의 데이터 설정
         pstmt.setString(1, id);
         rs = pstmt.executeQuery();
         if(rs.next()) {//아이디 일치하는 행을 찾았다면
            //return new Member();
            m = new Member(id, rs.getString("pw"),rs.getString("name"),rs.getString("email"),
                  rs.getInt("ssn1"),rs.getInt("ssn2"),rs.getString("phone"),rs.getString("addr"),
                  rs.getString("seller"),rs.getInt("card"),rs.getInt("total"),
                  rs.getString("grade"),rs.getString("ask"),rs.getString("answer"));
         }
         else {//아이디 일치하는 행을 찾지 못했다면
            return null; //전달할 데이터 없음!!
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      //return null;
      return m;
   }//회원정보얻기

   public boolean setMember(Member m) {
      connect();

      try {
         String sql = "update member set pw=?, phone=?, ask=?, answer=? where id=?";
         pstmt = conn.prepareStatement(sql);//데이터를 제외한 sql문 미리 전달
         pstmt.setString(1, m.getPw());
         pstmt.setString(2, m.getPhone());
         pstmt.setString(3, m.getAsk());
         pstmt.setString(4, m.getAnswer());
         pstmt.setString(5, m.getId());
         int t = pstmt.executeUpdate();
         if(t==1) return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//회원정보변경



   public boolean setRegStuff(Stuff s) {
      connect();
      try {
         String sql = "insert into request(sellerid, stuffname, timestart, timeend, minprice, chat, upprice,  explain, stuffkind)"
               + "values(?,?,?,?,?,?,?,?,?)";
         // price, success는 추후에 변경 예정
         pstmt = conn.prepareStatement(sql);// sql문을 미리 전달!!
         // pstmt.setString(물음표 순서(1~), 설정할 데이터);

         pstmt.setString(1, s.getSellerid());
         pstmt.setString(2, s.getStuffname());
         pstmt.setString(3,
               "to_date(to_char(sysdate + (1/24/60*3), 'yyyy/mm/dd hh24:mi:\"00\"'),'yyyy/mm/dd hh24:mi:ss')");
         pstmt.setString(4,
               "to_date(to_char(sysdate + (1/24/60*6), 'yyyy/mm/dd hh24:mi:\"00\"'),'yyyy/mm/dd hh24:mi:ss')");
         pstmt.setInt(5, s.getMinprice());
         pstmt.setString(6, s.getChat());
         pstmt.setInt(7, s.getUpprice());
         pstmt.setString(8, s.getExplain());
         pstmt.setString(9, s.getStuffkind());
         pstmt.executeUpdate();

         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }// 판매물품등록 INSERT

   public boolean setStuff(Stuff s) {
      connect();
      try {
         String sql = "update request set stuffname=?, sellerid=?, minprice=?, upprice=?, stuffkind=?, chat=? "
               + "where auctionno=?";
         pstmt = conn.prepareStatement(sql);

         pstmt.setString(1, s.getStuffname());
         pstmt.setString(2, s.getSellerid());
         pstmt.setInt(3, s.getMinprice());
         pstmt.setInt(4, s.getUpprice());
         pstmt.setString(5, s.getStuffkind());
         pstmt.setString(6, s.getChat());
         pstmt.setInt(7, s.getAuctionno());
         int t = pstmt.executeUpdate();
         if (t == 1) {
            return true;
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }// 물품정보 수정 UPDATE

   public boolean setCancelAuction(int auctionNo, Stuff s) {
      connect();
      try {
         String sql = "delete from request where auctionno=?";
         pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, auctionNo);
         int t = pstmt.executeUpdate();
         if (t == 1) {
            return true;
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }// 물품 삭제 DELETE

   public Vector<Stuff> getStuffListFuture(String orderType, int pageNum) {
      connect();
      Vector<Stuff> list = new Vector<>();
      Vector<Stuff> lists = new Vector<>();
      try {
         String sql = "select  auctionno, stuffname, sellerid, explain, minprice, upprice, timestart, stuffkind, chat  from request where sysdate<timestart order by timestart "+orderType;
         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();

         while (rs.next()) {
            list.add(new Stuff(rs.getInt("auctionno"), rs.getString("sellerid"), rs.getString("stuffname"),
                  rs.getInt("minprice"), rs.getString("chat"), rs.getInt("upprice"), rs.getString("explain"),
                  rs.getDate("timestart"), null, rs.getString("stuffkind"), null, null, 0));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      if(list.size() / (pageNum*30) != 0) {
         for(int i=(pageNum-1)*30; i<(pageNum)*30; i++) {
            lists.add(list.get(i));
         }
      }else {
         for(int i=(pageNum-1)*30; i<list.size(); i++) {
            lists.add(list.get(i));
         }
      }
      return lists;
   }

   public Vector<Stuff> getStuffListNow( int pageNum) {
      connect();
      Vector<Stuff> list = new Vector<>();
      Vector<Stuff> lists = new Vector<>();
      try {
         String sql = "select  auctionno, stuffname, sellerid, explain, minprice, upprice, timestart, timeend, stuffkind, chat,grade  from request where sysdate between timestart and timeend";
         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();

         while (rs.next()) {
            list.add(new Stuff(rs.getInt("auctionno"), rs.getString("sellerid"), rs.getString("stuffname"),
                  rs.getInt("minprice"), rs.getString("chat"), rs.getInt("upprice"), rs.getString("explain"),
                  rs.getDate("timestart"), rs.getDate("timeend"), rs.getString("stuffkind"),
                  rs.getString("grade"), null, 0));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      if(list.size() / (pageNum*9) != 0) {
         for(int i=(pageNum-1)*9; i<(pageNum)*9; i++) {
            lists.add(list.get(i));
         }
      }else {
         for(int i=(pageNum-1)*9; i<list.size(); i++) {
            lists.add(list.get(i));
         }
      }
      return lists;
   }// getStuffListNow

   public Vector<Stuff> getStuffListLast(String orderType, int pageNum) {
      connect();
      Vector<Stuff> list = new Vector<>();
      Vector<Stuff> lists = new Vector<>();
      try {
         String sql = "select  auctionno, stuffname, sellerid, price,explain, upprice, timeend, stuffkind, grade,success  from request where sysdate > timeend order by timestart "+orderType;
         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();

         while (rs.next()) {
            list.add(new Stuff(rs.getInt("auctionno"), rs.getString("sellerid"), rs.getString("stuffname"),
                  0, null, 0, rs.getString("explain"),
                  null, rs.getDate("timeend"), rs.getString("stuffkind"),
                  rs.getString("grade"), rs.getString("success"), rs.getInt("price")));
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      
      if(list.size() / (pageNum*30) != 0) {
         for(int i=(pageNum-1)*30; i<(pageNum)*30; i++) {
            lists.add(list.get(i));
         }
      }else {
         for(int i=(pageNum-1)*30; i<list.size(); i++) {
            lists.add(list.get(i));
         }
      }
      return lists;
   }// getStuffListLast

   public Vector<NoticeList> getNoticeList(int pageNum) { 

      connect();
      Vector<NoticeList> list = new Vector<>();
      Vector<NoticeList> lists = new Vector<>();


      try {
         String sql="select * from noticelist order by no";

         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();

         while(rs.next()) {
            NoticeList n = new NoticeList(rs.getInt("no"), 
                  rs.getString("content"), 
                  rs.getString("post"), 
                  rs.getDate("day"), 
                  rs.getString("notice"));
            list.add(n);
         }
         
         if(list.size() / (pageNum*9) != 0) {
            for(int i=(pageNum-1)*9; i<(pageNum)*9; i++) {
               lists.add(list.get(i));
            }
         }else {
            for(int i=(pageNum-1)*9; i<list.size(); i++) {
               lists.add(list.get(i));
            }
         }

      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return lists;
   }//게시판얻기(pageNum 1 : 0~10번까지 pageNum 2: 11~20번까지 ...1페이지는 공지사항 맨위로, 최신순정렬, 올린내용일부분만)

   public NoticeList getNoticeText(int no){
      connect();
      NoticeList n = null;
      try {
         String sql = "select no,content,post,day,notice from noticelist order by day";
         pstmt = conn.prepareStatement(sql);//sql문 미리 전달
         //?수만큼의 데이터 설정
         pstmt.setInt(1, no);
         rs = pstmt.executeQuery();
         if(rs.next()) {
            n = new NoticeList(no, rs.getString("content"), rs.getString("post"), rs.getDate("day"), rs.getString("notice"));
         }
         else {
            return null; //전달할 데이터 없음!!
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return n;
   }//글번호에 해당하는 모든 내용 얻기

   public boolean setNotice(NoticeList n) {
      connect();
      try {
         String sql="insert into noticelist values (notice_seq.nextval,?,?,sysdate,?);";

         pstmt = conn.prepareStatement(sql);//sql문을 미리 전달!!
         //pstmt.setString(물음표 순서(1~), 설정할 데이터);

         pstmt.setInt(1, n.getNo());
         pstmt.setString(2, n.getContent());
         pstmt.setString(3, n.getPost());
         pstmt.setDate(4, (java.sql.Date) n.getDay());
         pstmt.setString(5, n.getNotice());
         pstmt.executeUpdate();//sql문 실행 요청!!

         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//게시글 올리기

   public Vector<Buy> getPurchasedList(String id) {
      connect();
      Vector<Buy> list = new Vector<>();

      try {
         String sql = "select auctionno,stuffname,price,buydate from buy where id=?";
         pstmt = conn.prepareStatement(sql);//sql문 미리 전달
         //?수만큼의 데이터 설정
         pstmt.setString(1, id);
         rs = pstmt.executeQuery();
         while(rs.next()) {//아이디 일치하는 행을 찾았다면
            Buy b = new Buy(id, 
                  rs.getInt("autionno"), 
                  rs.getString("stuffname"), 
                  rs.getInt("price"),
                  rs.getDate("buydate"));
            list.add(b);
         }
         
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return list;
      //return null;
   }//구매한 물품 리스트

   public Vector<Stuff> getBasketList(String id) {

      connect();
      Vector<Stuff> list = new Vector<>();

      try {
         String sql="select auctionno from basket where id=?";

         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();

         while(rs.next()) {
            Stuff s = new Stuff(rs.getInt("auctionno"), 
                  null, null, 0, null, 0,
                  null, null, null, null, null,
                  null, 0
                  );
            list.add(s);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return null;
   }//장바구니 물품 리스트

   public Vector<Member> getSubscribeList(String id) {

      connect();
      Vector<Member> list = new Vector<>();

      try {
         String sql="select id, pass from bookmark where id=?";

         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id);
         rs = pstmt.executeQuery();

         while(rs.next()) {
            Member m = new Member(rs.getString("id"), rs.getString("pw"), 
                  null, null, 0, 0, null, null, null, 
                  0, 0, null, null, null);
            list.add(m);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return null;
   }//구독 리스트

   public boolean setSubscribe(Member m) {
      connect();
      try {
         String sql="insert into bookmark values (?,?)";
         pstmt = conn.prepareStatement(sql);//sql문을 미리 전달!!
         //pstmt.setString(물음표 순서(1~), 설정할 데이터);

         pstmt.setString(1, m.getId());
         pstmt.setString(2, m.getSellerid());
         pstmt.executeUpdate();//sql문 실행 요청!!

         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//구독 등록

   public Vector<Alarm> getAlarmList(String id) {

      connect();
      Vector<Alarm> list = new Vector<>();

      try {
         String sql="select id, alarm from alarm where id=?";

         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id);
         rs = pstmt.executeQuery();

         while(rs.next()) {
            Alarm a = new Alarm(
                  rs.getString("id"),
                  rs.getString("alarm"));
            list.add(a);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return list;
   }//알림 리스트

   public boolean setReport(String id) {

      connect();

      try {
         String sql="insert into report (id) values (?)";

         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, id);
         rs = pstmt.executeQuery();

         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      } 
      return false;
   }//신고

   public int getExpectedPrice(Stuff s){

      connect();
      int price=0;
      try {
         String sql = "select avg(price) from request where stuffkind=?";

         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, s.getStuffkind());
         rs = pstmt.executeQuery();
         if(rs.next()) {
            price = rs.getInt("avg(price)");
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return price;
   }//예상 판매금액 받아보기--> 물품종류별 판매된 평균금액

   public boolean setCancelAuction(int auctionNum) {

      connect();

      try {
         String sql="delete from request where auctionno=?";
         pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, auctionNum);
         int t = pstmt.executeUpdate();

         if(t==1) {
            return true;
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//판매취소

   public Stuff getStuff(int auctionNum) {

      connect();
      Stuff s = null;

      try {
         String sql = "select * from request "
               +"where auctionno=?";

         pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, auctionNum);
         rs = pstmt.executeQuery();

         if(rs.next()) {
         s = new Stuff(auctionNum, 
               rs.getString("sellerid"), 
               rs.getString("stuffname"),
               rs.getInt("minprice"),
               rs.getString("chat"),
               rs.getInt("upprice"),
               rs.getString("explain"),
               rs.getDate("timestart"),
               rs.getDate("timeend"),
               rs.getString("stuffkind"),
               rs.getString("grade"),
               rs.getString("success"),
               rs.getInt("price")
               );
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return s;
   }//물품정보 얻기

   public int getBalance() {
      connect();

      try {
         String sql="select total from daystotal";

         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();

         int total = rs.getInt("total");

         return total;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      } return 0;
   }//총 수익금(서버) 얻기

   public Vector<Member> getBlackList() {

      connect();
      Vector<Member> list = new Vector<>();

      try {
         String sql="select id from report";

         pstmt = conn.prepareStatement(sql);
         rs = pstmt.executeQuery();

         while(rs.next()) {
            Member bl = new Member(rs.getString("id"), null, 
                  null, null, 0, 0, null, null, null, 
                  0, 0, null, null, null);
            list.add(bl);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return list;
   }//블랙리스트 얻기

   public boolean setBuy(int auctionNum, Buy b) {
      connect();

      try {
         String sql="delete from basket where auctionno=?";
         String sql2="insert into buy (id,auctionno,stuffname,price,buydate) "
               +"values (?,?,?,?,?)";

         pstmt = conn.prepareStatement(sql);
         pstmt.setInt(1, auctionNum);
         int t = pstmt.executeUpdate();

         pstmt2 = conn.prepareStatement(sql2);
         pstmt2.setString(1, b.getId());
         pstmt2.setInt(1, b.getAuctionno());
         pstmt2.setString(1, b.getStuffname());
         pstmt2.setInt(1, b.getPrice());
         pstmt2.setString(1, "to_date(to_char(sysdate, 'yyyy/mm/dd hh24:mi:\"00\"'),'yyyy/mm/dd hh24:mi:ss')");
         int t2 = pstmt2.executeUpdate();

         if(t==1 && t2==1) {
            return true;
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }
      return false;
   }//물품 구매

   public boolean setAdjustGrade(String id, String grade) {
      connect();

      try {
         String sql="update member set grade=? where id=?";

         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, grade);
         pstmt.setString(2, id);
         rs = pstmt.executeQuery();
         return true;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         disconnect();
      }return false;
   }//등급 조정
}