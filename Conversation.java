package clavardage;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Conversation implements Runnable {

	private String distantID;
	private Socket distantSocket;
	private ConversationListener convList;
	private ConversationWriter convWrit;
	// private History currentHistory

	public Conversation(Socket dSocket) {
		// Create Sender and Receiver threads
		this.distantSocket = dSocket;
		convList = new ConversationListener(distantSocket);

		Thread treceiv = new Thread(convList);
		treceiv.start();

		convWrit = new ConversationWriter(this.distantSocket);
		Thread Tsend = new Thread(convList);
		Tsend.start();
	}

	public void sendMsg(String m) {
		convWrit.send(m);
	}

	public void closeConversation(){
		try {
			distantSocket.close();
		} catch (IOException e) {
					System.err.println("Error close Conversation");
					e.printStackTrace();
			}


	}

	public void run() {}
	




}
