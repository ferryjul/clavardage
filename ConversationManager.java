package clavardage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.net.InetAddress;
import java.util.Date;


public class ConversationManager implements Runnable {

	private ServerSocket listenerSocket;
		
	private OnlineUsersManager networkD;
	int port = 8042;

	private HistoryManager historyM;

	private ArrayList<Conversation> activeConversation;

	// Socket d'écoute qui permet de recevoir les demandes de conversation
	public void ConversationManager() {				
	}

	public void setDiscovery(OnlineUsersManager d) {
		networkD = d;
	}
/*
	public Conversation createConversation(Socket mySock){
			
	}
*/
	public void updatePseudos() {
		synchronized(activeConversation) {
		for(Conversation c : activeConversation) {
			if(c != null) {
				String newPseudo = (networkD.getUserFromAddress(c.getAddress()));
				if(!newPseudo.equals(c.getPseudo())) {
					System.out.println("Updating a pseudo in a conversation");
					c.setPseudo(newPseudo);					
				}
			}
		}
		}
	}

	public void setHistoryM(HistoryManager HM) {
		this.historyM = HM;
	}

	/* il faudra implémenter un bouton quand on appuie dessus on récupère l'inet adresse du psudo associé, puis on crée une conversation et on ajoute cette conversation dans la liste des conversations active */
	public void createConversation(InetAddress adressDest, int portdest) {
		Conversation conv = null;

		try {
			String idHost = networkD.getUserFromAddress(mySock.getInetAddress());
			Socket mySock = new Socket(adressDest,portdest);
			conv = new Conversation(mySock, 
			historyM.createConversation(idHost, 
									   (idHost + (new java.util.Date()).toString() + ".history"), new java.util.Date()), 
			idHost);
			
		} catch (IOException e) {
			System.err.println("Conversation not created");
			e.printStackTrace();
		}
		synchronized(activeConversation) {
			this.activeConversation.add(conv); 
		}
	}
	
	public Conversation receiveConversation(Socket mySock) {
		Conversation conv = new Conversation(mySock, networkD.getUserFromAddress(mySock.getInetAddress()));
		return conv;
	}

	// Thread faisant tourner en continue la socket d'écoute en mode accept
	@Override
	public void run() {
		try {
			this.activeConversation = new ArrayList<Conversation>();
			this.listenerSocket = new ServerSocket(port);
			} catch (IOException e) {
			System.err.println("Listen socket not created");
			e.printStackTrace();
			}
			System.out.print("Conversation Manager launched \n");  
			while(true){				
			try {
				Socket sock = this.listenerSocket.accept();
				synchronized(activeConversation) {
					this.activeConversation.add(receiveConversation(sock)); 
				}
				System.out.println("Conversation created");
			} catch (IOException e) {
				System.err.println("Error accept socklisten");
				e.printStackTrace();
			}
		}		
	}
}

