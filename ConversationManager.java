package clavardage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.net.InetAddress;



public class ConversationManager implements Runnable {

	private ServerSocket listenerSocket;
	
	int port = 8042;

	private ArrayList<Conversation> activeConversation;

	// Socket d'écoute qui permet de recevoir les demandes de conversation
	public void ConversationManager() {
	
		this.activeConversation = new ArrayList<Conversation>();
		
	}
/*
	public Conversation createConversation(Socket mySock){
			
	}
*/


	/* il faudra implémenter un bouton quand on appuie dessus on récupère l'inet adresse du psudo associé, puis on crée une conversation et on ajoute cette conversation dans la lisye des conversations active*/
	public Conversation createConversation(InetAddress adressDest, int portdest) {
		Conversation conv = null;

		try {
			Socket mySock = new Socket(adressDest,portdest);
			conv = new Conversation(mySock);
			
		} catch (IOException e) {
			System.err.println("Conversation not created");
			e.printStackTrace();
		}
		return conv;
	}
	
	public Conversation receiveConversation(Socket mySock) {

		Conversation conv = new Conversation(mySock);
		(new Thread(conv)).start();
		return conv;
	}
	// Thread faisant tourner en continue la socket d'écoute en mode accept
	@Override
	public void run() {
		try {
			this.listenerSocket = new ServerSocket(port);
			} catch (IOException e) {
			System.err.println("Listen socket not created");
			e.printStackTrace();
			}
			while(true){
				System.out.print("Je suis dans le run de conversation Manager \n");  
			try {
				Socket sock = this.listenerSocket.accept();
				this.activeConversation.add(receiveConversation(sock)); 
				System.out.println("Conversation created");
			} catch (IOException e) {
				System.err.println("Error accept socklisten");
				e.printStackTrace();
			}
		}		
	}
}

