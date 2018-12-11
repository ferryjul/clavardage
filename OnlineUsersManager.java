package clavardage;
import java.net.DatagramSocket ;
import java.util.ArrayList; 
import java.util.HashMap; 
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.lang.String;

public class OnlineUsersManager implements Runnable {
	private DatagramSocket socket;
	private HashMap<String,InetAddress> onlineUsers = new HashMap<String,InetAddress>();
	private String userPseudo;
	private InetAddress broadcastAddress;
	private DatagramPacket myPacket;
	private DatagramPacket rcvPacket;
	byte[] sendBuf;
	byte[] rcvBuf = new byte[256];

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
	
	public void closeCommunications() { // To properly close the socket
		socket.close();
		System.out.println("Datagram socket successfully closed");
	}	
	
	public OnlineUsersManager(int portNb, int portDest) {
		try {
			String message = new String("hello my friend");
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

	public OnlineUsersManager() {
		try {
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
			String sent = new String(myPacket.getData(), 0, myPacket.getLength());
			System.out.println("sending : " + sent);
			socket.send(myPacket);
			socket.receive(rcvPacket);
			String received = new String(rcvPacket.getData(), 0, rcvPacket.getLength());
			System.out.println("Quote of the Moment: (my port is " + myPacket.getPort() + ")" + received + "(from port " + rcvPacket.getPort() + ")");
		}	
		catch(Exception e) {
			System.out.println("Error while sending UDP packet");
		}
		while(true) {
			//System.out.println("Test");
		}
	}

}
