package com.encore.auctionServer.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import com.encore.auctionServer.model.AuctionDAO;
import com.encore.auctionServer.model.Buy;
import com.encore.auctionServer.model.Member;
import com.encore.auctionServer.model.Stuff;
import com.encore.auctionServer.view.ServerView;

public class Server {
	AuctionDAO dao;
	ServerSocket serverSocket;
	ServerSocket serverSocket_ob;
	Accept accept;
	ServerView serverView;
	Date date;
	
	static final int SERVER_PORT_NUM = 5000;
	static final int SERVER_OB_PORT_NUM = 5001;
	
	/*  
	  	C_MSG(client massage) - 클라이언트로부터의 메시지
		S_MSG(server massage) - 서버로부터의 메시지
		REQ(request) - 응답이 필요한 요청
		REG(register) - 응답이 필요하지 않은 요청
	*/
	static final String C_MSG_REQ_LOGIN = "C_MSG_REQ_LOGIN";
	static final String C_MSG_REQ_ID_CHECK = "C_MSG_REQ_ID_CHECK";
	static final String C_MSG_REQ_JOIN = "C_MSG_REQ_JOIN";
	static final String C_MSG_REQ_CHANGE_INFO = "C_MSG_REQ_CHANGE_INFO";
	static final String C_MSG_REQ_REJOIN = "C_MSG_REQ_REJOIN";
	static final String C_MSG_REQ_PAST_LIST = "C_MSG_REQ_PAST_LIST";
	static final String C_MSG_REQ_PRESENT_LIST = "C_MSG_REQ_PRESENT_LIST";
	static final String C_MSG_REQ_FUTURE_LIST = "C_MSG_REQ_FUTURE_LIST";
	static final String C_MSG_REQ_PURCHASED_LIST = "C_MSG_REQ_PURCHASED_LIST";
	static final String C_MSG_REQ_BLACK_LIST = "C_MSG_REQ_BLACK_LIST";
	static final String C_MSG_REQ_SUBSCRIBE_LIST = "C_MSG_REQ_SUBSCRIBE_LIST";
	static final String C_MSG_REQ_ALARM_LIST = "C_MSG_REQ_ALARM_LIST";
	static final String C_MSG_REQ_PRODUCT_INFO = "C_MSG_REQ_PRODUCT_INFO";
	static final String C_MSG_REQ_BALANCE_INFO = "C_MSG_REQ_BALANCE_INFO";
	static final String C_MSG_REQ_BUY = "C_MSG_REQ_BUY";
	static final String C_MSG_REQ_WRITE = "C_MSG_REQ_WRITE";
	static final String C_MSG_REQ_WRITE_NOTICE = "C_MSG_REQ_WRITE_NOTICE";
	static final String C_MSG_REQ_EXPECT = "C_MSG_REQ_EXPECT";
	static final String C_MSG_REQ_CANCEL_REG_STUFF = "C_MSG_REQ_CANCEL_REG_STUFF";
	static final String C_MSG_REQ_ENTER_ROOM = "C_MSG_REQ_ENTER_ROOM";
	static final String C_MSG_REQ_STUFF_IMG = "C_MSG_REQ_STUFF_IMG";
	static final String C_MSG_REQ_ALL_MEMBER = "C_MSG_REQ_ALL_MEMBER";
	static final String C_MSG_REQ_MEMBER = "C_MSG_REQ_MEMBER";
	static final String C_MSG_REG_CHATTING_TEXT = "C_MSG_REG_CHATTING_TEXT";
	static final String C_MSG_REG_KICK = "C_MSG_REG_KICK";
	static final String C_MSG_REG_SUBSCRIBE = "C_MSG_REG_SUBSCRIBE";
	static final String C_MSG_REG_PRODUCT = "C_MSG_REG_PRODUCT";
	static final String C_MSG_REG_LEAVE = "C_MSG_REG_LEAVE";
	static final String C_MSG_REG_SELLER = "C_MSG_REG_SELLER";
	static final String C_MSG_REG_ASKING = "C_MSG_REG_ASKING";
	static final String C_MSG_REG_START_PRICE = "C_MSG_REG_START_PRICE";
	static final String C_MSG_REG_ASKING_UNIT = "C_MSG_REG_ASKING_UNIT";
	static final String C_MSG_REG_AGREE_SELLER = "C_MSG_REG_AGREE_SELLER";
	static final String C_MSG_REG_AGREE_STUFF = "C_MSG_REG_AGREE_STUFF";
	static final String C_MSG_REG_ADJUST_GRADE = "C_MSG_REG_ADJUST_GRADE";
	static final String C_MSG_REG_FREEZE = "C_MSG_REG_FREEZE";
	static final String C_MSG_REG_LEAVE_ROOM = "C_MSG_REG_LEAVE_ROOM";
	static final String C_MSG_REG_ID = "C_MSG_REG_ID";
	static final String C_MSG_REG_STUFF = "C_MSG_REG_STUFF";
	static final String S_MSG_RESULT = "S_MSG_RESULT";
	static final String S_MSG_REG_ASKING_UNIT = "S_MSG_REG_ASKING_UNIT";
	static final String S_MSG_REG_CHANGE_ASKING = "S_MSG_REG_CHANGE_ASKING";
	static final String S_MSG_REG_CHANGE_ASKING_ID = "S_MSG_REG_CHANGE_ASKING_ID";
	static final String S_MSG_REG_CHATTING_TEXT = "S_MSG_REG_CHATTING_TEXT";
	static final String S_MSG_REG_KICK = "S_MSG_REG_KICK";
	static final String S_MSG_REG_FREEZE = "S_MSG_REG_FREEZE";
	static final String S_MSG_REG_ALARM = "S_MSG_REG_ALARM";
	static final String S_MSG_RES_ENTER_ROOM = "S_MSG_RES_ENTER_ROOM";
	static final String S_MSG_RES_ID_CHECK = "S_MSG_RES_ID_CHECK";
	static final String S_MSG_REG_NEW_ENTER = "S_MSG_REG_NEW_ENTER";
	static final String S_MSG_REG_NEW_LEAVE = "S_MSG_REG_NEW_LEAVE";
	static final String S_MSG_RES_LOGIN = "S_MSG_RES_LOGIN";
	static final String S_MSG_RES_JOIN = "S_MSG_RES_JOIN";
	static final String S_MSG_REG_WINNER = "S_MSG_REG_WINNER";
	static final String S_MSG_RES_CHANGE_INFO = "S_MSG_RES_CHANGE_INFO";
	
	static final String NO_CONTENT = "NO_CONTENT";
	
	public Server(ServerView serverView) {
		// 서버 소켓 생성 및 바인딩
		try {
			this.serverView = serverView;
			dao = new AuctionDAO(serverView);
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", SERVER_PORT_NUM));
			serverSocket_ob = new ServerSocket();
			serverSocket_ob.bind(new InetSocketAddress("localhost", SERVER_OB_PORT_NUM));
			
		} catch(Exception e) {
			System.err.println("# 서버 구성 실패");
			//e.printStackTrace();
		}
	}
	
	public void startServer() { //서버 시작 시 호출			
		accept = new Accept();
		accept.start();
		System.out.println("# 서버 시작");
	}
	
	public void stopServer() { //서버 중지 시 호출
		if(accept!= null)accept.stopAccept();
		System.out.println("# 서버 중지");
	}
	
	public void endServer() { //서버 종료 시 호출 
		try {
			stopServer();
			if(serverSocket!=null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if(serverSocket_ob!=null && !serverSocket_ob.isClosed()) {
				serverSocket_ob.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class Accept extends Thread {
		Vector<Service> connections;
		Vector<Room> roomList;
		ExecutorService executorService; // 스레드풀
		
		public Accept() {
			connections = new Vector<>();
			roomList = new Vector<>();
			new TimeChecker().start();
			
			// 스레드풀 생성
			executorService = Executors.newFixedThreadPool(
					Runtime.getRuntime().availableProcessors()
			);
		}
		
		class TimeChecker extends Thread {
			@Override
			public void run() {
				try {
					while(true) {
						date = new Date();
						sleep(1000);
						for(int i=0; i<roomList.size(); ++i) {
							Room room = roomList.get(i);
							Date endTime = room.stuff.getTimeend();
							if(date.after(endTime)) {
								for(int j=0; j<connections.size(); ++j) {
									Service service = connections.get(j);
									if(room.maxPriceId.equals(service.id)) {
										service.sendMSG(room.stuff.getAuctionno()+"", S_MSG_REG_WINNER, room); //낙찰한 사람에게 알려주기
									}
								}
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void run() { //여러 클라이언트 접속
			while(true) {
				try {
					System.out.println("# 접속 대기중 ...");
					Socket socket = serverSocket.accept();
					Socket socket_ob = serverSocket_ob.accept();
					System.out.println("클라이언트 접속 [" + socket.getInetAddress().getHostAddress() + "]");
					connections.add(new Service(socket, socket_ob));
				} catch (IOException e) {
					System.err.println("# 접속 대기 해제");
					//e.printStackTrace();
					break;
				}
			}
		}
		
		public void stopAccept() { // 서버 종료 시 호출
			try {
				// 모든 소켓 닫기
				Iterator<Service> iterator = connections.iterator();
				while(iterator.hasNext()) {
					Service service = iterator.next();
					service.socket.close();
					service.socket_ob.close();
					iterator.remove();
				}

				// 스레드풀 종료
				if(executorService!=null && !executorService.isShutdown()) { 
					executorService.shutdown(); 
				}
			} catch (Exception e) { }
		}
		
		class Room { //Room객체 한개 -> 경매방 한개, 클라이언트 여러명
			public Vector<String> idList; //경매참여자들(클라이언트들)
			public Vector<Service> serviceList; //경매참여자들(클라이언트들)
			
			public int maxIdCnt; //최대 참여 인원
			public int maxPrice; //최고 금액
			public String maxPriceId = ""; //최고금액제시자
			public Stuff stuff; //경매물품
			
			public Room(Stuff stuff) { //생성자
				idList = new Vector<>();
				serviceList = new Vector<>();
				maxIdCnt = 100; //최대 100명 입장가능
				this.stuff = stuff;
			}
		}
		
		class Service extends Thread { // Service객체 한개 -> 클라이언트 한개
			Socket socket;
			Socket socket_ob;
			OutputStream out;
			BufferedReader in;
			ObjectOutputStream out_ob;
			ObjectInputStream in_ob;
			String id;
			
			public Service(Socket socket, Socket socket_ob) {
				try {
					this.socket = socket;
					this.socket_ob = socket_ob;
					out = socket.getOutputStream();
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out_ob = new ObjectOutputStream(socket_ob.getOutputStream());
					in_ob = new ObjectInputStream(socket_ob.getInputStream());
					executorService.submit(this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void run() { //클라이언트가 보내는 메시지를 읽기
				try {
					while(true) {
						String msgPack = in.readLine();
						if(msgPack != null) {
							//System.out.println(msgPack);
							String type = getType(msgPack);
							String msg = getMsg(msgPack);
							
							String log = "Received <"+type+"> from <"+Service.this.id+"> : "+msg;
							serverView.tf_log.setText(serverView.tf_log.getText()+log+"\n");
							serverView.serverWorking();
							
							switch(type) {
								case C_MSG_REQ_STUFF_IMG : {
									Vector<Stuff> stuffList = dao.getStuffListNow(Integer.parseInt(msg));
									Vector<ImageIcon> icList = new Vector<>();
									for(int i=0; i<stuffList.size(); ++i) {
										icList.add(new ImageIcon("image/"+stuffList.get(i).getStuffname()+".jpg"));
									}
									sendObject(icList);
									
									for(int i=0; i<stuffList.size(); ++i) { //현재 경매중인데 방이 생성되어 있지 않다면 생성하기
										boolean existflag = false;
										Stuff stuff = stuffList.get(i);
										int aucionNo = stuff.getAuctionno();
										for(int j=0; j<roomList.size(); ++j) {
											if(roomList.get(j).stuff.getAuctionno() == aucionNo) {
												existflag = true;
												break;
											}
										}
										if(!existflag) {
											Room room = new Room(stuff);
											roomList.add(room);
										}
									}
									break;
								}
								case C_MSG_REQ_LOGIN : { //로그인 요청
									String id = msg.split(",")[0];
									String password = msg.split(",")[1];
									this.id = id;
									
									int loginResult = dao.isLoginSuccess(id, password);
									sendMSG(loginResult+"", S_MSG_RES_LOGIN);
									break;
								}
								case C_MSG_REQ_ID_CHECK : { //아이디 체크 요청
									if(dao.isIDExisting(msg)) sendMSG("true", S_MSG_RES_ID_CHECK);
									else sendMSG("false", S_MSG_RES_ID_CHECK);
									break;
								}
								case C_MSG_REQ_JOIN : { //회원가입 요청
									Member m = (Member)in_ob.readObject();
									if(dao.setJoin(m)) sendMSG("true", S_MSG_RES_JOIN);
									else sendMSG("false", S_MSG_RES_JOIN);
									break;
								}
								case C_MSG_REQ_CHANGE_INFO : { //개인정보 변경 요청
									Member m = (Member)in_ob.readObject();
									if(dao.setMember(m)) sendMSG("true", S_MSG_RES_CHANGE_INFO);
									else sendMSG("false", S_MSG_RES_CHANGE_INFO);
									break; 
								}
								case C_MSG_REQ_REJOIN : { //재가입 요청
//									if(dao.setRejoin(msg)) sendMSG("true", S_MSG_RESULT);
//									else sendMSG("false", S_MSG_RESULT);
									break; 
								}
								case C_MSG_REQ_PRESENT_LIST : { //현재물품리스트 요청
									Vector<Stuff> stuffList = dao.getStuffListNow(Integer.parseInt(msg));
									sendObject(stuffList);
									break;
								}
								case C_MSG_REQ_PAST_LIST : {//과거물품리스트 요청
									String pageNum = msg.split(",")[0];
									String orderType = msg.split(",")[1];
									Vector<Stuff> stuffList = dao.getStuffListLast(orderType, Integer.parseInt(pageNum));
									sendObject(stuffList);
									break;
								}
								case C_MSG_REQ_FUTURE_LIST : { //미래물품리스트 요청
									String pageNum = msg.split(",")[0];
									String orderType = msg.split(",")[1];
									Vector<Stuff> stuffList = dao.getStuffListFuture(orderType, Integer.parseInt(pageNum));
									sendObject(stuffList);
									break;
								} 
								case C_MSG_REQ_PURCHASED_LIST : { //구매목록 요청
									Vector<Buy> purchasedList = dao.getPurchasedList(msg);
									sendObject(purchasedList);
									break;
								}
								case C_MSG_REQ_BLACK_LIST : { //블랙리스트 요청
									Vector<Member> blackList = dao.getBlackList();
									sendObject(blackList);
									break;
								}
								case C_MSG_REQ_SUBSCRIBE_LIST : { //구독리스트 요청
									Vector<Member> subscribeList = dao.getSubscribeList(msg);
									sendObject(subscribeList);
									break;
								}
								case C_MSG_REQ_PRODUCT_INFO : { //물품 정보 요청
									Stuff stuff = dao.getStuff(Integer.parseInt(msg));
									sendObject(stuff);
									break;
								}
								case C_MSG_REQ_BALANCE_INFO : { //총 수익금(서버) 정보 요청
									sendMSG(dao.getBalance()+"", S_MSG_RESULT);
									break;
								}
								case C_MSG_REQ_BUY : { //물품구매(바로구매) 요청
//									if(dao.setBuy(Integer.parseInt(msg))) sendMSG("true", S_MSG_RESULT);
//									else sendMSG("false", S_MSG_RESULT);
									break;
								}
								case C_MSG_REQ_WRITE :
								case C_MSG_REQ_WRITE_NOTICE : { //게시판 글 등록 요청
//									String[] noticeContent = (String[]) in_ob.readObject();
//									if(dao.setNotice(noticeContent)) sendMSG("true", S_MSG_RESULT);
//									else sendMSG("false", S_MSG_RESULT);
									break;
								}
								case C_MSG_REQ_EXPECT : { //예상 판매금액 요청
									Stuff s = (Stuff)in_ob.readObject();
									int expectedPrice = dao.getExpectedPrice(s);
									sendMSG(expectedPrice+"", S_MSG_RESULT);
									break;
								}
								case C_MSG_REQ_CANCEL_REG_STUFF : { //물품 판매 등록 취소 요청
									if(dao.setCancelAuction(Integer.parseInt(msg))) sendMSG("true", S_MSG_RESULT);
									else sendMSG("false", S_MSG_RESULT);
									break;
								}
								case C_MSG_REQ_ENTER_ROOM : { //경매방 입장 요청
									int auctionNum = Integer.parseInt(msg);
									boolean enter_flag = false;
									
									for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 찾으며
										Room room = roomList.get(idx);
										if(room.stuff.getAuctionno() == auctionNum) {//해당 경매번호인 방을 찾아서
											if(room.idList.size() < room.maxIdCnt && date.before(room.stuff.getTimeend())) { //꽉차지 않았으면
												room.idList.addElement(this.id); //자신의 아이디와
												room.serviceList.addElement(this); //서비스를 등록하고
												sendMSG("true", S_MSG_RES_ENTER_ROOM); //결과를 알려준다
												sendObject(dao.getStuff(auctionNum));
												for(int i=0; i<room.serviceList.size(); ++i) { //경매방의 모든 클라이언트들에게
													Service service = room.serviceList.get(i);
													if(service == Service.this) continue;
													service.sendMSG(this.id, S_MSG_REG_NEW_ENTER, room); //입장한 id를 전달한다
												}
											} else {
												sendMSG("false", S_MSG_RES_ENTER_ROOM);
											}
											enter_flag = true;
											break;
										}
									}
//									if(!enter_flag && dao.isSelling(auctionNum)) { //판매중이지만 경매방이 생성되어 있지 않다면
//										Room room = new Room(auctionNum); //새로운 경매방을 생성하여
//										room.idList.addElement(this.id); //자신의 아이디와
//										room.serviceList.addElement(this); //서비스를 등록하고
//										sendMSG("true", S_MSG_RES_ENTER_ROOM); //결과를 알려준다.
//										sendObject(dao.getStuff(auctionNum));
//										roomList.add(room); //룸리스트에 새로 생성한 경매방을 넣어준다.
//										System.out.println("경매"+room.auctionNum);
//									}
									break;
								}
								case C_MSG_REQ_ALL_MEMBER : { //채팅방에 있는 사람들의 아이디 요청
									int auctionNum = Integer.parseInt(msg);
									for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 찾으며
										Room room = roomList.get(idx);
										if(room.stuff.getAuctionno() == auctionNum) {//해당 경매번호인 방을 찾아서
												for(int i = 0; i < room.idList.size(); ++i) {
													sendMSG(room.idList.get(i), S_MSG_REG_NEW_ENTER);
												}
										}
									}
									break;
								}
								case C_MSG_REQ_MEMBER : {
									Member member = dao.getMember(msg);
									sendObject(member);
									break;
								}
								case C_MSG_REG_CHATTING_TEXT : { //채팅 요청
									String auctionNum = msg.split(",")[0]; //경매 번호
									String chatting_text = msg.split(",")[1]; //채팅내용

									for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 찾으며
										Room room = roomList.get(idx);
										if(room.stuff.getAuctionno() == Integer.parseInt(auctionNum)) {//해당 경매번호인 방을 찾으면
											for(int i=0; i<room.serviceList.size(); ++i) { //경매방의 모든 클라이언트들에게
												Service service = room.serviceList.get(i);
												service.sendMSG(chatting_text, S_MSG_REG_CHATTING_TEXT, room); //채팅 메시지를 전달한다
											}
											break;
										}
									}
									break;
								}
								case C_MSG_REG_SUBSCRIBE : { //구독 요청
//									dao.setSubscribe(msg.split(",")[0], msg.split(",")[1]);
									break;
								}
								case C_MSG_REG_PRODUCT : { //물품 판매 등록 요청
									dao.setRegStuff((Stuff)in_ob.readObject());
									break;
								}
								case C_MSG_REG_LEAVE : { //탈퇴 요청
									dao.setLeave(msg);
									break;
								}
								case C_MSG_REG_SELLER : { //판매자 등록 요청
									dao.setSeller(msg);
									break;
								}
								case C_MSG_REG_ASKING : { //입찰하기 요청
									String auctionNum = msg.split(",")[0]; //경매 번호
									String askingPrice = msg.split(",")[1]; //입찰금액
									
									for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 찾으며
										Room room = roomList.get(idx);
										if(room.stuff.getAuctionno() == Integer.parseInt(auctionNum)) {//해당 경매번호인 방을 찾아서
											room.maxPrice = Integer.parseInt(askingPrice); //최고금액을 갱신하고
											for(int i=0; i<room.serviceList.size(); ++i) { //경매방의 모든 클라이언트들에게 
												Service service = room.serviceList.get(i);
												service.sendMSG(askingPrice+","+auctionNum, S_MSG_REG_CHANGE_ASKING, room); //변화된 최고 금액을 알려준다
												service.sendMSG(this.id, S_MSG_REG_CHANGE_ASKING_ID, room); //제시자도 알려준다
											}
											break;
										}
									}
									break;
								}
								case C_MSG_REG_KICK : { //경매방에서 한 클라이언트 강퇴 요청
			                        String auctionNum = msg.split(",")[0]; //경매번호
			                        String id = msg.split(",")[1]; //강퇴시키고 싶은 클라이언트 아이디
			                        
			                        for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 검색하여
			                           Room room = roomList.get(idx);
			                           if(room.stuff.getAuctionno() == Integer.parseInt(auctionNum)) { //해당 경매번호인 방을 찾고
			                              for(int i=0; i < room.serviceList.size(); ++i) { //방안의 모든 클라이언트를 검색하여
			                            	 Service service = room.serviceList.get(i);
			                                 if(service.id.equals(id)) { //강퇴 대상 클라이언트에게만
			                                	service.sendMSG("", S_MSG_REG_KICK, room); //강퇴명령을 내린다
			                                	room.serviceList.remove(service); //서비스를 지우고
			                                	room.idList.remove(id); //아이디를 지운다
			                                    break;
			                                 }
			                              }
			                              break;
			                           }
			                        }
			                        break;
			                     }
			                     case C_MSG_REG_LEAVE_ROOM : { //경매 방에서 나가기 요청
				                        String auctionNum = msg; //경매번호
	
				                        for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 검색하여
				                           Room room = roomList.get(idx);
				                           if(room.stuff.getAuctionno() == Integer.parseInt(auctionNum)) { //해당 경매번호인 방을 찾고
				                              for(int i=0; i < room.serviceList.size(); ++i) { //방안의 모든 클라이언트를 검색하여
				                            	 Service service = room.serviceList.get(i);
				                                 service.sendMSG(this.id, S_MSG_REG_NEW_LEAVE, room); //나간 아이디를 알려준다
				                              }
				                              for(int i=0; i < room.serviceList.size(); ++i) { //방안의 모든 클라이언트를 검색하여
				                            	 Service service = room.serviceList.get(i);
				                                 if(service.id.equals(this.id)) { //나가고싶은 클라이언트만
				                                	room.serviceList.remove(this); //서비스를 지우고
				                                	room.idList.remove(this.id); //아이디를 지운다
				                                	break;
				                                 }
				                              }
				                              //if(room.serviceList.size() == 0) roomList.remove(room); //아무도 없으면 방 지우기
				                              break;
				                           }
				                        }
				                        break;
			                    }
								case C_MSG_REG_START_PRICE : { //경매 시작금액 재설정 요청
//									String auctionNum = msg.split(",")[0];
//									String newStartPrice = msg.split(",")[1];
//									dao.setStartPrice(Integer.parseInt(auctionNum), Integer.parseInt(newStartPrice));
									break;
								}
								case C_MSG_REG_ASKING_UNIT : { //호가 단위 변경 요청
									String auctionNum = msg.split(",")[0]; //경매 번호
									String newAskingPrice = msg.split(",")[1]; //새로운 호가
									
									for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 찾으며
										Room room = roomList.get(idx);
										if(room.stuff.getAuctionno() == Integer.parseInt(auctionNum)) {//해당 경매번호인 방을 찾으면
											for(int i=0; i<room.serviceList.size(); ++i) { //경매방의 모든 클라이언트들에게
												Service service = room.serviceList.get(i);
												service.sendMSG(newAskingPrice, S_MSG_REG_ASKING_UNIT, room); //변화된 호가를 알려준다
											}
											break;
										}
									}
									break;
								}
								case C_MSG_REG_AGREE_SELLER : {
//									dao.setSellerAgree(msg);
									break;
								}
								case C_MSG_REG_AGREE_STUFF : {
//									dao.setStuffAgree(Integer.parseInt(msg));
									break;
								}
								case C_MSG_REG_ADJUST_GRADE : {
									String id = msg.split(",")[0];
									String newGrade = msg.split(",")[1];
									dao.setAdjustGrade(id, newGrade);
									break;
								}
								case C_MSG_REG_FREEZE : { //채팅방 얼리기 요청
									String auctionNum = msg.split(",")[0]; //경매 번호
									String freezingTime = msg.split(",")[1]; //얼리는 시간
									
									for(int idx=0; idx<roomList.size(); ++idx) { //모든 경매방을 찾으며
										Room room = roomList.get(idx);
										if(room.stuff.getAuctionno() == Integer.parseInt(auctionNum)) {//해당 경매번호인 방을 찾으면
											for(int i=0; i<room.serviceList.size(); ++i) { //경매방의 모든 클라이언트들에게
												Service service = room.serviceList.get(i);
												service.sendMSG(freezingTime, S_MSG_REG_FREEZE, room); //채팅방 얼림을 알려준다
											}
											break;
										}
									}
									break;
								}
								case C_MSG_REG_ID : {
									this.id = msg;
									break;
								}
								case C_MSG_REG_STUFF : {
									Stuff s = (Stuff)in_ob.readObject();
									dao.setRegStuff(s);
									break;
								}
							}//switch
						} //if
					} //while
				} catch (Exception e) {
					try {
						//e.printStackTrace();
						connections.remove(Service.this);
						System.out.println("# 클라이언트 통신 두절  id : "+id+" [" + socket.getRemoteSocketAddress()+"]");
						socket.close();
					} catch (IOException e1) {}
				} //try
			} //run
			
			private void sendMSG(String msg, String type, Room room) { //방에 입장한 클라이언트에게 보내는 메시지
				// 보내기 작업 생성
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {
							out.write((type+":"+msg+"\n").getBytes());
						} catch(Exception e) {
							try {
								//e.printStackTrace();
								connections.remove(Service.this);
								if(room != null) room.serviceList.remove(Service.this);
								//room : 입장한 경매방, 입장한 상태가 아닌경우 null
								System.out.println("# 클라이언트 통신 두절  id : "+id+" [" + socket.getRemoteSocketAddress()+"]");
								socket.close();
							} catch (IOException e2) {}
						}
					}
				};
				// 스레드풀에서 처리
				executorService.submit(runnable);
			}

			private void sendMSG(String msg, String type) throws IOException {
				// 보내기 작업 생성
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {
							out.write((type+":"+msg+"\n").getBytes());
							String log = "Sent <"+type+"> type of message from server to <"+Service.this.id+"> : "+msg;
							serverView.tf_log.setText(serverView.tf_log.getText()+log+"\n");
							serverView.serverWorking();
						} catch(Exception e) {
							try {
								//e.printStackTrace();
								connections.remove(Service.this);
								System.out.println("# 클라이언트 통신 두절  id : "+id+" [" + socket.getRemoteSocketAddress()+"]");
								socket.close();
							} catch (IOException e2) {}
						}
					}
				};
				// 스레드풀에서 처리
				executorService.submit(runnable);
			}
			
			private void sendObject(Object obj) {
				// 보내기 작업 생성
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {
							out_ob.writeObject(obj);
							out_ob.flush();
							String log = "Sent <Object> type of message from server to <"+Service.this.id+">";
							serverView.tf_log.setText(serverView.tf_log.getText()+log+"\n");
							serverView.serverWorking();
						} catch(Exception e) {
							try {
								connections.remove(Service.this);
								System.out.println("# 클라이언트 통신 두절: " + socket.getRemoteSocketAddress());
								socket.close();
							} catch (IOException e2) {}
						}
					}
				};
				// 스레드풀에서 처리
				executorService.submit(runnable);
			}
			
			private String getType(String msg) {
				return msg.split(":")[0];
			}
			
			private String getMsg(String msg) {
				return msg.split(":")[1];
			}
		}
	}
}