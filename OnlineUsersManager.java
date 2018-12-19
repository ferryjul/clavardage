package clavardage;
import java.net.DatagramSocket ;
import java.util.ArrayList; 
import java.util.HashMap; 
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.lang.String;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
/*
Conventions (types de messages)
-> Demande des pseudos en ligne : [000]
-> Se déclarer en ligne : [001]_Pseudo
-> Réponse à une déclaration en ligne : [011]_Pseudo
-> Déclaration d'un nouveau pseudo : [021]_NouveauPseudo //Ancien pseudo déterminé par l'addresse !
-> Se déconnecter : [002]_Pseudo

*/

public class OnlineUsersManager implements Runnable {
	private DatagramSocket socket;
	private HashMap<String,InetAddress> onlineUsers = new HashMap<String,InetAddress>();
	private String userPseudo;
	private InetAddress broadcastAddress;
	private DatagramPacket myPacket;
	private DatagramPacket rcvPacket;
	byte[] sendBuf;
	byte[] rcvBuf = new byte[256];
	private boolean isActiveManager = false;
	private Boolean hasBeenModified = true;
	String message;

	public int notifyOnline() {	
		try {
			System.out.println("sending : " + message);
			socket.send(myPacket);
			this.hasBeenModified = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int notifyOffline() {
		try {
		message = new String("[002]_" + userPseudo); //Réponse à un utilisateur en ligne
		sendBuf = message.getBytes();
		myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 4444);
		System.out.println("sending : " + message);
		socket.send(myPacket);  
		return 0;
		}
		catch(Exception e) {
			System.out.println("error while logging out");
			return 1;
		}
		
	}

	public String getUserFromAddress(InetAddress hostAddr) {
				Set<String> usersOnTable = onlineUsers.keySet();
				Iterator<String> it = usersOnTable.iterator();
				String theUser = "shouldchange";
				while(it.hasNext()) {	
					String aUser = it.next();
					if((onlineUsers.get(aUser)) != null) {	
						if((onlineUsers.get(aUser)).equals(hostAddr)) {	
							theUser = aUser;
						}
					}
			 	}
				return theUser;
	}


	public int notifyNewPseudo(String newPseudo) {
		if((!userPseudo.equals(newPseudo)) && onlineUsers.containsKey(newPseudo)) {
			return(-1);
		}
		if(userPseudo.equals(newPseudo)) {
			return(-2);
		}
		this.hasBeenModified = true;
		synchronized(onlineUsers) {
			onlineUsers.remove(userPseudo);
			this.userPseudo = newPseudo;
			onlineUsers.put(userPseudo, null);			
		}
		try {
			message = new String("[021]_" + userPseudo); //Réponse à un utilisateur en ligne
			sendBuf = message.getBytes();
			myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 4444);
			System.out.println("sending : " + message);
			if(this.isActiveManager) {
				socket.send(myPacket);
			}
		} catch(Exception e) {
			System.out.println("Error while sending new pseudo notification");
		}
		return 0;
	}
	
	public boolean isOnline(String nameUser) {
		return (onlineUsers.containsKey(nameUser));
	}

	public InetAddress getAddress(String nameUser) {
		if((onlineUsers.containsKey(nameUser))) {
			return (onlineUsers.get(nameUser));
		} else {
			return null;
		}
	}

	public Set<String> getOnlineUsers() {
	   /* if(hasBeenModified == true) {
	      synchronized(this.hasBeenModified) {
			this.hasBeenModified = false;
		   }*/
   		synchronized(onlineUsers) {
			Set<String> s = new HashSet<String>(onlineUsers.keySet());
   			return s;
   		}
	 /*  }
	   else {
	   	return null;
		}*/
	}
	
	public void closeCommunications() { // To properly close the socket
		this.isActiveManager = false;
		socket.close();
		System.out.println("Datagram socket successfully closed");
	}	
	
	public OnlineUsersManager(int portNb, int portDest) {
		try {
			this.isActiveManager = true;
			message = new String("[001]_" + userPseudo);
			System.out.println(message);
			sendBuf = message.getBytes();
			broadcastAddress = InetAddress.getByName("255.255.255.255");
			myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, portDest);
			rcvPacket = new DatagramPacket(rcvBuf, 0, rcvBuf.length);
			socket = new DatagramSocket(portNb);
			System.out.println("Datagram socket successfully created");
		}
		catch(Exception e) {
			System.out.println("Error while creating DatagramSocket");
		}
	}


	public OnlineUsersManager(String ps) {
		try {
			this.isActiveManager = true;
			userPseudo = ps;
			onlineUsers.put(userPseudo, null);
			message = new String("[001]_" + userPseudo);
			sendBuf = message.getBytes();
			broadcastAddress = InetAddress.getByName("255.255.255.255");
			myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 4444);
			rcvPacket = new DatagramPacket(rcvBuf, 0, rcvBuf.length);
			socket = new DatagramSocket(4444);
			this.notifyOnline();
			System.out.println("Datagram socket successfully created");
		}
		catch(Exception e) {
			System.out.println("Error while creating DatagramSocket");
		}
	}

	public void run() {
		System.out.println("Network Discovery active");
		try {
			while(this.isActiveManager) {
			socket.receive(rcvPacket);
			String received = new String(rcvPacket.getData(), 0, rcvPacket.getLength());
			System.out.println("received :" + received);
			if(received.length()>4) {
			if(received.equals("[000]")) { //Demande des pseudos en ligne
					message = new String("[011]_" + userPseudo); //Réponse à une demande de pseudos en ligne
					sendBuf = message.getBytes();
					myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 4444);
					System.out.println("sending : " + message);
					socket.send(myPacket);
			} else
				if(!userPseudo.equals(received.substring(6))) { //!(rcvPacket.getAddress()).equals("localhost")
						if((received.substring(0,6)).equals("[001]_")) { //Déclaration utilisateur en ligne
						 	synchronized(this.hasBeenModified) {
								this.hasBeenModified = true;
							}
							System.out.println(received.substring(6) + " is online");
							synchronized(onlineUsers) {
							onlineUsers.put(received.substring(6),rcvPacket.getAddress());
							}
							message = new String("[011]_" + userPseudo); //Réponse à un utilisateur en ligne
							sendBuf = message.getBytes();
							myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 4444);
							System.out.println("sending : " + message);
							socket.send(myPacket);
						}
						if((received.substring(0,6)).equals("[011]_")) { //Déclaration utilisateur en ligne
							synchronized(this.hasBeenModified) {
								this.hasBeenModified = true;
							}
							System.out.println(received.substring(6) + " is already online");
							synchronized(onlineUsers) {
							if(!onlineUsers.containsKey(received.substring(6))) {
								onlineUsers.put(received.substring(6),rcvPacket.getAddress());
							}
							}
						}
						if((received.substring(0,6)).equals("[002]_")) { //Déclaration déconnexion utilisateur
							synchronized(this.hasBeenModified) {
								this.hasBeenModified = true;
							}
							System.out.println(received.substring(6) + " is deconnected");
							synchronized(onlineUsers) {			
								onlineUsers.remove(received.substring(6));
							}
						}
						if((received.substring(0,6)).equals("[021]_")) { //Déclaration changement de pseudo
							this.hasBeenModified = true;
							// Find corresponding old pseudo
							try {
								InetAddress hostAddr = rcvPacket.getAddress();
								Set<String> usersOnTable = onlineUsers.keySet();
								Iterator<String> it = usersOnTable.iterator();
								String theUser = "shouldchange";
								this.hasBeenModified = true;
								while(it.hasNext()) {	
										String aUser = it.next();
										if((onlineUsers.get(aUser)) != null) {	
											if((onlineUsers.get(aUser)).equals(hostAddr)) {	
												theUser = aUser;
											}
										}
								}
								synchronized(onlineUsers) {									
									System.out.println(theUser + "addr = " + (onlineUsers.get(theUser)).toString() );																		
									onlineUsers.remove(theUser);
									onlineUsers.put(received.substring(6),hostAddr);
								}
								System.out.println(received.substring(6) + " new pseudo");
							}
							catch(Exception e) {
								System.out.println("Error while updating a pseudo...");
								e.printStackTrace();
							}
						}
					}
				}
			
			}
		}	
		catch(java.net.SocketException exe) {
			//NOTHING, GREAT
		}
		catch(Exception e) {
			System.out.println("Error while sending UDP packet");
			e.printStackTrace();
		}
		while(true) {
			//System.out.println("Test");
		}
	}

}
