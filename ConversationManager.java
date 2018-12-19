package clavardage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;

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
	public int createConversation(InetAddress adressDest, int portdest) {
		Conversation conv = null;
		boolean notcreated = true ;
		try {
			Iterator<Conversation> iterator = activeConversation.iterator();

			while (iterator.hasNext()) {
				Conversation ctest = iterator.next();
				if(ctest.isActive() && ctest.getAddress().equals(adressDest)){
					notcreated = false;
					ctest.firstPlan();
				}
			}
				
			if (notcreated)
			{
				Socket mySock = new Socket(adressDest,portdest);
				String idHost = networkD.getUserFromAddress(mySock.getInetAddress());
				synchronized(historyM) {
					conv = new Conversation(mySock, 
					historyM.createHistory(idHost, 
										   ("Conversation with " + idHost + " (date : " + (new java.util.Date()).toString() + ")"), new java.util.Date()), 
				idHost);
				}
			
				synchronized(activeConversation) {
					this.activeConversation.add(conv); 
				}
				return 0;
			}
			else{
				System.out.println("Conversation already created");
				return 1;
			}

		}catch (IOException e) {
				System.err.println("Conversation not created");
				e.printStackTrace();
			}
		return 0;
	}
	
	public Conversation receiveConversation(Socket mySock) {
		String idHost = networkD.getUserFromAddress(mySock.getInetAddress());
		Conversation conv;
		synchronized(historyM) {
			conv = new Conversation(mySock, 
				historyM.createHistory(idHost, 
				(idHost + (new java.util.Date()).toString() + ".history"), new java.util.Date()), 
				idHost);
		}
		return conv;
	}

/*	public void closeCM() {
		try {
			listenerSocket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}*/

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

