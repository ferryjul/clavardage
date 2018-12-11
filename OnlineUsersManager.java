package clavardage;
import java.net.DatagramSocket ;
import java.util.ArrayList; 
import java.util.HashMap; 
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.lang.String;
import java.util.Set;

/*
Conventions (types de messages)
-> Se déclarer en ligne : [001]_Pseudo
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
	String message;

	public int notifyOnline() {
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

	public int notifyNewPseudo(String newPseudo) {
		return 0;
	}
	
	public boolean isOnline(String nameUser) {
		return true;
	}

	public InetAddress getAdress(String nameUser) {
		return null;
	}

	public Set<String> getOnlineUsers() {
		synchronized(onlineUsers) {
		return onlineUsers.keySet();
		}
	}
	
	public void closeCommunications() { // To properly close the socket
		socket.close();
		System.out.println("Datagram socket successfully closed");
	}	
	
	public OnlineUsersManager(int portNb, int portDest) {
		try {
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
			userPseudo = ps;
			onlineUsers.put(userPseudo, null);
			message = new String("[001]_" + userPseudo);
			sendBuf = message.getBytes();
			broadcastAddress = InetAddress.getByName("255.255.255.255");
			myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 4444);
			rcvPacket = new DatagramPacket(rcvBuf, 0, rcvBuf.length);
			socket = new DatagramSocket(4444);
			System.out.println("Datagram socket successfully created");
		}
		catch(Exception e) {
			System.out.println("Error while creating DatagramSocket");
		}
	}
	
	public void run() {
		System.out.println("Network Discovery active");
		try {
			System.out.println("sending : " + message);
			socket.send(myPacket);
			while(true) {
			socket.receive(rcvPacket);
			String received = new String(rcvPacket.getData(), 0, rcvPacket.getLength());
			System.out.println("received :" + received);
			if(!userPseudo.equals(received.substring(6))) { //!(rcvPacket.getAddress()).equals("localhost")
				if((received.substring(0,6)).equals("[001]_")) { //Déclaration utilisateur en ligne
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
					System.out.println(received.substring(6) + " is already online");
					synchronized(onlineUsers) {
					if(!onlineUsers.containsKey(received.substring(6))) {
						onlineUsers.put(received.substring(6),rcvPacket.getAddress());
					}
					}
				}
				if((received.substring(0,6)).equals("[002]_")) { //Déclaration déconnexion utilisateur
					System.out.println(received.substring(6) + " is deconnected");
					onlineUsers.remove(received.substring(6));
				}
			}
			}
		}	
		catch(Exception e) {
			System.out.println("Error while sending UDP packet");
		}
		while(true) {
			//System.out.println("Test");
		}
	}

}
