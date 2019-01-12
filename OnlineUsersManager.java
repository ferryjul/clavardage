package clavardage;
import java.net.DatagramSocket ;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.Map;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.lang.String;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
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
	private boolean udpBased;
	private String sAddress;
        private String sPort;
	String message;

	public int notifyOnline() {	
		try {
			if(udpBased) {
				System.out.println("sending : " + message);
				socket.send(myPacket);
			}
			else {
				//TODO
				String address = "http://" + sAddress + ":" + sPort + "/presenceserver/connect?display=false&type=connect&pseudo=" + userPseudo;			
				System.out.println("Trying to connect to \"" + address + "\"");
				URL url = new URL(address);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int responseCode = con.getResponseCode();
				if(responseCode == 200) {
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					System.out.println("Response code from server = " + responseCode);
					System.out.println("Response message from server = " + in.readLine());
					in.close();
					con.disconnect();
				}
				else {
					System.out.println("Fatal error while connecting to server");
					System.out.println("RESTART APPLICATION or RESTART PERFORMING TASK");
				}
			}
			this.hasBeenModified = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int notifyOffline() {
		if(udpBased) {
			try {
				message = new String("[002]_" + userPseudo); //Message de déconnection
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
		else {
			try {
				String address = "http://" + sAddress + ":" + sPort + "/presenceserver/connect?display=false&type=deconnect&pseudo=" + userPseudo;			
				System.out.println("Trying to connect to \"" + address + "\"");
				URL url = new URL(address);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int responseCode = con.getResponseCode();
				if(responseCode == 200) {
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					System.out.println("Response code from server = " + responseCode);
					System.out.println("Response message from server = " + in.readLine());
					in.close();
					con.disconnect();
					return 0;
				}
				else {
					System.out.println("Fatal error while connecting to server");
					System.out.println("RESTART APPLICATION or RESTART PERFORMING TASK");
					return 1;
				}
			} catch(Exception e) {
				System.out.println("Communication error with HTTP presence server :");
				e.printStackTrace();
			}
			return 0;
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
		if(udpBased) {
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
		}
		else {
			try {
				String address = "http://" + sAddress + ":" + sPort + "/presenceserver/connect?display=false&type=change&pseudo=" + userPseudo;			
				System.out.println("Trying to connect to \"" + address + "\"");
				URL url = new URL(address);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int responseCode = con.getResponseCode();
				if(responseCode == 200) {
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					System.out.println("Response code from server = " + responseCode);
					System.out.println("Response message from server = " + in.readLine());
					in.close();
					con.disconnect();
					return 0;
				}
				else {
					System.out.println("Fatal error while connecting to server");
					System.out.println("RESTART APPLICATION or RESTART PERFORMING TASK");
					return 1;
				}
			} catch(Exception e) {
				System.out.println("Communication error with HTTP presence server :");
				e.printStackTrace();
			}
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

	public Set<String> getOnlineUsers() { /* in HTTP mode, call to this function causes update of the hashmap of the users */
	   /* if(hasBeenModified == true) {
	      synchronized(this.hasBeenModified) {
			this.hasBeenModified = false;
		   }*/
		if(!udpBased) {
			try {
				String address = "http://" + sAddress + ":" + sPort + "/presenceserver/connect?display=false&type=info&pseudo=" + userPseudo;		
				System.out.println("Trying to connect to \"" + address + "\"");
				URL url = new URL(address);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				int responseCode = con.getResponseCode();
				if(responseCode == 200) {
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					System.out.println("Response code from server = " + responseCode);
					String respo = in.readLine();
					System.out.println("Response message from server = " + respo);
					in.close();
					con.disconnect();
					HashMap<String,InetAddress> updatedOnlineUsers = new HashMap<String,InetAddress>();
					String resp = respo.substring(13); // To remove the header "SERVER_REPLY:" at the beginning of the message.
					String[] usersArray = resp.split(";;");
					for(int i = 0 ; i < usersArray.length ; i++) {
						String[] aUser = usersArray[i].split("@");
						updatedOnlineUsers.put(aUser[0], InetAddress.getByName(aUser[1]));		
					}
					onlineUsers = updatedOnlineUsers;
				}
				else {
					System.out.println("Fatal error while connecting to server");
					System.out.println("RESTART APPLICATION or RESTART PERFORMING TASK");
				}
			} catch(Exception e) {
				System.out.println("Communication error with HTTP presence server :");
				e.printStackTrace();
			}
		}
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
		if(udpBased) {
			socket.close();
			System.out.println("Datagram socket successfully closed");
		}
		else {
			//TODO
			System.out.println("Closing Online Users Manager");
		}
	}	
	
	/* !!!!!!!!!!!!!!!! SHOULD NOT BE USED !!!!!!!!!!!!!!!!!!!!!!!! */
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

	/* GENERIC PARAMETERS BUILDER ADAPTED FROM THE INTERNET */
	public static String getParamsString(Map<String, String> params) {
		String resultString = null;
		try {
		StringBuilder result = new StringBuilder();
	 
		for (Map.Entry<String, String> entry : params.entrySet()) {
		  result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
		  result.append("=\"");
		  result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		  result.append("\"");
		  result.append("&");
		}
	 
		resultString = result.toString();
		
		}
		catch(Exception e) {
			System.out.println("Error while building HTTP request parameters :");
			e.printStackTrace();
		}
		return resultString.length() > 0
		  ? resultString.substring(0, resultString.length() - 1)
		  : resultString;
	    }

	public OnlineUsersManager(String ps, boolean isUDPBased, String serverAddress, String serverPort) {
		try {
			this.udpBased = isUDPBased;
	 		sAddress = serverAddress;
      			sPort = serverPort;
			this.isActiveManager = true;
			userPseudo = ps;
			onlineUsers.put(userPseudo, null);
			/* INIT COMPONENTS */
			if(udpBased) {
				message = new String("[001]_" + userPseudo);
				sendBuf = message.getBytes();
				broadcastAddress = InetAddress.getByName("255.255.255.255");
				myPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, 4444);
				rcvPacket = new DatagramPacket(rcvBuf, 0, rcvBuf.length);
				socket = new DatagramSocket(4444);
				System.out.println("Datagram socket successfully created");
			}
			else {
				//TODO
				System.out.println("Network Discovery Launching for Server @" + sAddress + ":" + sPort);
			}
			this.notifyOnline();
			
		}
		catch(Exception e) {
			System.out.println("Error while initializing components :");
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("Network Discovery active");
		if(udpBased) {
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
		}
		else {
			//TODO
		}
	}

}
