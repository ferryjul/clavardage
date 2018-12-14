package clavardage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.awt.TextArea;

public class ConversationWriter implements Runnable {

	private Socket distantSocket; 
	private PrintWriter out;
	private boolean active;
	private History myHist;
	private TextArea messagesDisplay;

	public ConversationWriter(Socket mysock, TextArea ta, History h){
		this.myHist = h;
		this.messagesDisplay = ta;
		this.distantSocket = mysock;	
		try {
			this.out = new PrintWriter(distantSocket.getOutputStream(), true);
		} catch (Exception e) {
			System.err.println("Error creating listener (for conversation)");
			e.printStackTrace();
		}
	}

	public void close() {
		this.active = false;
	}

	public void send(String msg) {
		try {
			out.println(msg);
			synchronized(messagesDisplay) {
				messagesDisplay.append("Sent: " + "[" + (new java.util.Date()).toString() + "] "+ msg + "\n");
			}
			myHist.updateHist("Sent: " + "[" + (new java.util.Date()).toString() + "] " + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void run() {
		this.active = true;
		while(active) {
			
		}	
	}
}
