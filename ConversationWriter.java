package clavardage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class ConversationWriter implements Runnable {

	private Socket distantSocket; 
	private PrintWriter out;
	private boolean active;

	public ConversationWriter(Socket mysock){
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
