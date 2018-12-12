package clavardage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class ConversationListener implements Runnable {
private BufferedReader in;
private Socket distantSocket; 

	public ConversationListener(Socket mysock){
		this.distantSocket = mysock;
		try {
			this.in = new BufferedReader(new InputStreamReader(distantSocket.getInputStream()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String receive() {
		String input = null;
		try {
		    	input = in.readLine(); // ici bloquant
				} catch (IOException e) {
					System.err.println("Error receive ListenerSOcket");
					e.printStackTrace();
				}
		System.out.print("Message reçu : ");  
		System.out.println(input);  
		return input;
	}


	@Override
	public void run() {
		while(true){
			// Nothing	
			receive();
		}
	}
}
