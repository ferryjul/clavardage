package clavardage;
import java.net.DatagramSocket ;
import java.util.ArrayList; 
import java.util.HashMap; 
import java.net.InetAddress;

public class OnlineUsersManager {
	private DatagramSocket socket;
	private HashMap<String,InetAddress> onlineUsers = new HashMap<String,InetAddress>();
	private String userPseudo;
	private InetAddress broadcastAddress;
	public int notifyOnline() {
		return 0;
	}

	public int notifyOffline() {
		return 0;
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

	public ArrayList<String> getOnlineUsers() {
		return null;
	}
	
	public OnlineUsersManager() {
		try {
			broadcastAddress = InetAddress.getByName("255.255.255.255");
			socket = new DatagramSocket(4444,broadcastAddress);
		}
		catch(Exception e) {
			System.out.println("Error while creating DatagramSocket");
		}
	}

}
