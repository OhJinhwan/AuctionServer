package com.encore.auctionServer.view;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ServerView extends JFrame {
	JLabel lb_server, lb_server_working, lb_db, lb_db_working, lb_log;
	public JButton bt_start_n_stop, bt_manager, bt_end;
	public JTextField tf_log;
	
	public ServerView() {
		this.setTitle("경매 서버");
		
		lb_server = new JLabel(new ImageIcon("image/server.png"));
		lb_server_working = new JLabel(new ImageIcon("image/red.png"));
		lb_db = new JLabel(new ImageIcon("image/database.png"));
		lb_db_working = new JLabel(new ImageIcon("image/red.png"));
		lb_log = new JLabel();
		bt_start_n_stop = new JButton("서버 시작");
		bt_manager = new JButton("서버 관리");
		bt_end = new JButton("서버 종료");
		tf_log = new JTextField();
		
		this.setLayout(null);
		lb_server.setBounds(100, 50, 120, 120);
		lb_server_working.setBounds(135, 175, 50, 50);
		lb_server_working.setVisible(false);
		lb_server_working.setBounds(135, 175, 50, 50);
		lb_db.setBounds(300, 50, 120, 120);
		lb_db_working.setBounds(340, 175, 50, 50);
		lb_db_working.setVisible(false);
		bt_start_n_stop.setBounds(50, 250, 100, 30);
		bt_start_n_stop.setFocusable(false);
		bt_manager.setBounds(200, 250, 100, 30);
		bt_manager.setFocusable(false);
		bt_end.setBounds(350, 250, 100, 30);
		bt_end.setFocusable(false);
		tf_log.setBounds(50, 320, 400, 150);
		tf_log.setFocusable(false);
		
		this.add(lb_server);
		this.add(lb_server_working);
		this.add(lb_db);
		this.add(lb_db_working);
		this.add(bt_start_n_stop);		
		this.add(bt_manager);		
		this.add(bt_end);	
		this.add(tf_log);
		
		this.setVisible(true);
		this.setBounds(300, 300, 500, 550);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void serverWorking() {
		new Thread() {
			public void run() {
				try {
					lb_server_working.setVisible(true);
					sleep(1000);
					lb_server_working.setVisible(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public void dbWorking() {
		new Thread() {
			public void run() {
				try {
					lb_db_working.setVisible(true);
					sleep(1000);
					lb_db_working.setVisible(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	public boolean showConfirm(String msg) {
		if(JOptionPane.showConfirmDialog(this, msg, "확인", JOptionPane.CANCEL_OPTION)==0) return true;
		else return false;
	}
	
	public static void main(String[] args) {//for test
		
	}
}