package com.encore.auctionServer.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.encore.auctionServer.view.ServerView;

public class ServerController {
	Server server;
	ServerView serverView;
	
	public ServerController() {
		serverView = new ServerView();
		server = new Server();
		serverView.setVisible(true);
		
		eventUp();
	}
	
	private void eventUp() {
		serverView.bt_start_n_stop.addActionListener(new ActionListener() {
			boolean start_n_stop_flag = false; //ture : 켜져있음 false : 꺼져있음
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!start_n_stop_flag) { //서버가 꺼져있으면
					server.startServer();
					serverView.bt_start_n_stop.setText("서버 정지");
					start_n_stop_flag = true;
				} else if(serverView.showConfirm("정말 정지시키시겠습니까?")){ //서버가 켜져있으면
					server.stopServer();
					serverView.bt_start_n_stop.setText("서버 시작");
					start_n_stop_flag = false;
				}
			}
		});
		
		serverView.bt_manager.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		serverView.bt_end.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				server.endServer();
				System.exit(0);
			}
		});
		
		serverView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				server.endServer();
			}
		});
	}
	
	public static void main(String[] args) {
		new ServerController();
	}
}
