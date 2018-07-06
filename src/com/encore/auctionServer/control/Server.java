package com.encore.auctionServer.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import com.encore.auctionServer.model.AuctionDAO;
import com.encore.auctionServer.model.Buy;
import com.encore.auctionServer.model.Member;
import com.encore.auctionServer.model.Room;
import com.encore.auctionServer.model.Stuff;

public class Server {
	AuctionDAO dao;
	ServerSocket serverSocket;
	Accept accept;
	
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
	static final String C_MSG_REQ_PRODUCT_INFO = "C_MSG_REQ_PRODUCT_INFO";
	static final String C_MSG_REQ_BALANCE_INFO = "C_MSG_REQ_BALANCE_INFO";
	static final String C_MSG_REQ_BUY = "C_MSG_REQ_BUY";
	static final String C_MSG_REQ_WRITE = "C_MSG_REQ_WRITE";
	static final String C_MSG_REQ_WRITE_NOTICE = "C_MSG_REQ_WRITE_NOTICE";
	static final String C_MSG_REQ_EXPECT = "C_MSG_REQ_EXPECT";
	static final String C_MSG_REQ_CANCEL_REG_STUFF = "C_MSG_REQ_CANCEL_REG_STUFF";
	static final String C_MSG_REQ_ENTER_ROOM = "C_MSG_REQ_ENTER_ROOM";
	static final String C_MSG_REG_CHATTING_TEXT = "C_MSG_REG_CHATTING_TEXT";
	static final String C_MSG_REG_KICK = "C_MSG_REG_KICK";
	static final String C_MSG_REG_ID = "C_MSG_REG_ID";
	static final String C_MSG_REG_SUBSCRIBE = "C_MSG_REG_SUBSCRIBE";
	static final String C_MSG_REG_PRODUCT = "C_MSG_REG_PRODUCT";
	static final String C_MSG_REG_LEAVE = "C_MSG_REG_LEAVE";
	static final String C_MSG_REG_SELLER = "C_MSG_REG_SELLER";
	static final String C_MSG_REG_ASKING = "C_MSG_REG_ASKING";
	static final String C_MSG_REG_DEPORT = "C_MSG_REG_DEPORT";
	static final String C_MSG_REG_START_PRICE = "C_MSG_REG_START_PRICE";
	static final String C_MSG_REG_ASKING_PRICE = "C_MSG_REG_ASKING_PRICE";
	static final String C_MSG_REG_AGREE_SELLER = "C_MSG_REG_AGREE_SELLER";
	static final String C_MSG_REG_AGREE_PRODUCT = "C_MSG_REG_AGREE_PRODUCT";
	static final String C_MSG_REG_ADJUST_GRADE = "C_MSG_REG_ADJUST_GRADE";
	static final String C_MSG_REG_FREEZE = "C_MSG_REG_FREEZE";
	static final String C_MSG_REG_LEAVE_ROOM = "C_MSG_REG_LEAVE_ROOM";
	static final String S_MSG_RESULT = "S_MSG_RESULT";
	static final String S_MSG_REG_CHANGE_ASKING_PRICE = "S_MSG_REG_CHANGE_ASKING_PRICE";
	static final String S_MSG_REG_NOTICE = "S_MSG_REG_NOTICE";
	static final String S_MSG_REG_KICK = "S_MSG_REG_KICK";
	
	public Server() {
		// 서버 소켓 생성 및 바인딩
		try {
			dao = new AuctionDAO();
			serverSocket = new ServerSocket();	
			serverSocket.bind(new InetSocketAddress("localhost", 5001));
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
		accept.stopAccept();
		System.out.println("# 서버 중지");
	}
	
	public void endServer() { //서버 종료 시 호출 
		try {
			stopServer();
			if(serverSocket!=null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class Accept extends Thread {
		ArrayList<Room> roomList;
		InputStream is;
		ExecutorService executorService; // 스레드풀
		Vector<Service> connections;
		
		public Accept() {
			connections = new Vector<>();
			
			// 스레드풀 생성
			executorService = Executors.newFixedThreadPool(
					Runtime.getRuntime().availableProcessors()
			);
		}
		
		@Override
		public void run() { //여러 클라이언트 접속
			while(true) {
				try {
					System.out.println("# 접속 대기중 ...");
					Socket socket = serverSocket.accept();
					System.out.println("클라이언트 접속 [" + socket.getInetAddress().getHostAddress() + "]");
					connections.add(new Service(socket));
				} catch (IOException e) {
					e.printStackTrace();
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
					iterator.remove();
				}

				// 스레드풀 종료
				if(executorService!=null && !executorService.isShutdown()) { 
					executorService.shutdown(); 
				}
			} catch (Exception e) { }
		}
		
		class Service extends Thread { // Service객체 한개 -> 클라이언트 한개
			Socket socket;
			ObjectOutputStream out;
			ObjectInputStream in;
			String id;
			
			public Service(Socket socket) {
				try {
					this.socket = socket;
					out = new ObjectOutputStream(socket.getOutputStream());
					in = new ObjectInputStream(socket.getInputStream());
					executorService.submit(this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void run() { //클라이언트가 보내는 메시지를 읽기
				try {
//					Vector<ImageIcon> v = new Vector<>();
//					v.add(new ImageIcon("image/server.png"));
//					out.writeObject(v);
//					out.flush();
					while(true) {
						String msgPack = (String)in.readObject();
						String type = getType(msgPack);
						String msg = getMsg(msgPack);
						
						switch(type) {
							case "C_MSG_REQ_LOGIN" : {
								int loginRes = dao.isLoginSuccess(msg.split(":")[0], msg.split(":")[1]);
								sendMsg(loginRes+"", S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_ID_CHECK" : {
								if(dao.isIDExisting(msg)) sendMsg("true", S_MSG_RESULT);
								else sendMsg("false", S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_JOIN" : {
								Member m = Member.convertToMember(msg);
								if(dao.setJoin(m)) sendMsg("true", S_MSG_RESULT);
								else sendMsg("false", S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_CHANGE_INFO" : {
								Member m = null;
								if(dao.setMember(m)) sendMsg("true", S_MSG_RESULT);
								else sendMsg("false", S_MSG_RESULT);
								break; 
							}
							case "C_MSG_REQ_REJOIN" : {
								dao.setRejoin(msg);
								break; 
							}
							case "C_MSG_REQ_PAST_LIST" :
							case "C_MSG_REQ_PRESENT_LIST" :
							case "C_MSG_REQ_FUTURE_LIST" : {
								String tableType = msg.split(":")[0];
								String pageNum = msg.split(":")[1];
								String orderType = msg.split(":")[2];
								ArrayList<Stuff> stuffList = dao.getStuffList(tableType, pageNum, orderType);
								ArrayList<String> stringList = new ArrayList<>();
								for(int i=0; i<stuffList.size(); ++i) {
									stringList.add(stuffList.get(i).convertToString());
								}
								out.writeObject(stringList);
								out.flush();
								break;
							} 
							case "C_MSG_REQ_PURCHASED_LIST" : {
								ArrayList<Buy> purchasedList = dao.getPurchasedList(msg);
								ArrayList<String> stringList = new ArrayList<>();
								for(int i=0; i<purchasedList.size(); ++i) {
									stringList.add(purchasedList.get(i).convertToString());
								}
								out.writeObject(stringList);
								out.flush();
								break;
							}
							case "C_MSG_REQ_BLACK_LIST" : {
								ArrayList<Member> blackList = dao.getBlackList(msg);
								ArrayList<String> stringList = new ArrayList<>();
								for(int i=0; i<blackList.size(); ++i) {
									stringList.add(blackList.get(i).convertToString());
								}
								out.writeObject(stringList);
								out.flush();
								break;
							}
							case "C_MSG_REQ_SUBSCRIBE_LIST" : {
								ArrayList<Member> subscribeList = dao.getSubscribeList(msg);
								ArrayList<String> stringList = new ArrayList<>();
								for(int i=0; i<subscribeList.size(); ++i) {
									stringList.add(subscribeList.get(i).convertToString());
								}
								out.writeObject(stringList);
								out.flush();
								break;
							}
							case "C_MSG_REQ_PRODUCT_INFO" : {
								Stuff stuff = dao.getStuff(Integer.parseInt(msg));
								sendMsg(stuff.convertToString(), S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_BALANCE_INFO" : {
								sendMsg(dao.getBalance()+"", S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_BUY" : {
								if(dao.setBuy(Integer.parseInt(msg))) sendMsg("true", S_MSG_RESULT);
								else sendMsg("false", S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_WRITE" :
							case "C_MSG_REQ_WRITE_NOTICE" : {
								String[] noticeContent = (String[]) in.readObject();
								if(dao.setNotice(noticeContent)) sendMsg("true", S_MSG_RESULT);
								else sendMsg("false", S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_EXPECT" : {
								Stuff s = Stuff.converToStuff(msg);
								int expectedPrice = dao.getExpectedPrice(s);
								sendMsg(expectedPrice+"", S_MSG_RESULT);
							}
							case "C_MSG_REQ_CANCEL_REG_STUFF" : {
								if(dao.setCancelAuction(Integer.parseInt(msg))) sendMsg("true", S_MSG_RESULT);
								else sendMsg("false", S_MSG_RESULT);
								break;
							}
							case "C_MSG_REQ_ENTER_ROOM" : {
								//별도 관리 필요
							}	
							case "C_MSG_REG_CHATTING_TEXT" : {
								//별도 관리 필요
							}
							case "C_MSG_REG_ID" : {
								id = msg;
							}
							case "C_MSG_REG_SUBSCRIBE" : {
								dao.setSubscribe(msg.split(":")[0], msg.split(":")[1]);
								break;
							}
							case "C_MSG_REG_PRODUCT" : {
								dao.setRegStuff(Stuff.converToStuff(msg));
								break;
							}
							case "C_MSG_REG_LEAVE" : {
								dao.setLeave(msg);
								break;
							}
							case "C_MSG_REG_SELLER" : {
								dao.setSeller(Member.convertToMember(msg));
								break;
							}
							case "C_MSG_REG_ASKING" : {
								String roomNum = msg.split(":")[0];
								String askingPrice = msg.split(":")[1];
								//해당 방의 모든 사람에게 변화된 호가 전송
							}
							case "C_MSG_REG_DEPORT" : {
								
							}
							case "C_MSG_REG_KICK" : {
		                        String id = msg.split(":")[0];
		                        String auctionNum = msg.split(":")[1];
		                        
		                        for(int i = 0; i < connections.size(); ++i) {//전체 클라이언트(Service들)
		                           Service s = connections.get(i);
		                           if(s.id.equals(id))
		                              s.send("", S_MSG_REG_KICK);
		                        }
		                        
		                        for(int i = 0; i < roomList.size(); ++i) {
		                           Room room = roomList.get(i);
		                           if(room.auctionNum == Integer.parseInt(auctionNum)) {
		                              for(int j = 0; j < room.idList.size(); ++j) {
		                                 if(room.idList.get(i).equals(id)) {
		                                    room.idList.remove(i);
		                                 }
		                              }
		                           }
		                        }
		                        break;
		                     }
		                     case "C_MSG_REG_LEAVE_ROOM" : {
		                        String id = msg.split(":")[0];
		                        String auctionNum = msg.split(":")[1];
		                        
		                        for(int i = 0; i < roomList.size(); ++i) {
		                           Room room = roomList.get(i);
		                           if(room.auctionNum == Integer.parseInt(auctionNum)) {
		                              for(int j = 0; j < room.idList.size(); ++j) {
		                                 if(room.idList.get(i).equals(id)) {
		                                    room.idList.remove(i);
		                                 }
		                              }
		                           }
		                        }
		                        break;
		                    }
							case "C_MSG_REG_START_PRICE" :
							case "C_MSG_REG_ASKING_PRICE" : {
							}
							case "C_MSG_REG_AGREE_SELLER" :
							case "C_MSG_REG_AGREE_PRODUCT" :
							case "C_MSG_REG_ADJUST_GRADE" :
							case "C_MSG_REG_FREEZE" :
						}
					}
				} catch (Exception e) {
					try {
						connections.remove(Service.this);
						System.out.println("# 클라이언트 통신 두절: " + socket.getRemoteSocketAddress());
						socket.close();
					} catch (IOException e1) {
						//e1.printStackTrace();
					}
				}
			}
			
			private void send(String msg, String type) { //다른 클라이언트에게 보내는 메시지
				// 보내기 작업 생성
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {
							out.writeObject(type + ":" +msg);
							out.flush();
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

			private void sendMsg(String msg, String type) throws IOException { //해당 클라이언트에게 보내는 메시지
				out.writeObject(type + ":" +msg);
				out.flush();
			}
			
			private String getType(String msg) {
				return msg.split(":")[0];
			}
			
			private String getMsg(String msg) {
				return msg.split(":")[1];
			}
			
//			Vector<ImageIcon> v = new Vector<>();
//			v.add(new ImageIcon("image/server.png"));
//			out.writeObject(v);
//			out.flush();
			
//			public void messageTo(String msg) throws IOException { //특정 클라이언트에게 메시지 보내기
//					out.write((msg + "\n").getBytes());
//			}
//			
//			public void messageAll(String msg) { //접속되어진 모든 클라이언트에게 메시지 보내기
//				for(int i = 0; i < v.size(); ++i) {//전체 클라이언트(Service들)
//					Service s = v.get(i);
//					try {
//					s.messageTo(msg);
//					} catch (IOException e) {
//						v.remove(i--); //해당 벡터를 삭제하는 순간 벡터사이즈가 줄어들기 때문에 -1을 해주여야 한다.
//					}
//					/* catch하지 않고 throws한다면 에러가 발생한 다음 client부터 메시지 전달을 보장할 수 없다.
//					      접속 끊긴 클라이언트를 벡터에서 삭제해준다.                                   */
//				}
//			}
		}
	}
	
	public static void main(String[] args) {
		new Server();
	}
}
