package clavardage;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Conversation extends Frame {
	// Composants Réseau
	private String distantID;
	private Socket distantSocket;
	private ConversationListener convList;
	private ConversationWriter convWrit;
	private History currentHistory;

	// Composants Graphiques
	private TextField txtSEND;	
	private Dialog login;
	private Label lblRCV;
	private Thread Tsend;
	private Thread treceiv;

	@SuppressWarnings( "deprecation" )
	public void closeConversation(){
		 try {
			 // Fermeture de la découverte réseau
			 convList.close();
			 System.out.println("closed Listener");
			 convWrit.close();
			 System.out.println("closed Writer");
			 treceiv.stop();
			 Tsend.stop();
			 System.out.println("closed Stopped Listener and Writer threads");
			 distantSocket.close();
			 System.out.println("closed socket");
			 
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			// Fermeture de l'historique
			currentHistory.closeHistory();
			System.out.println("Closed History");
			 // Fermeture de la fenêtre
		    login.dispose();
			System.out.println("closed GUI");		
	}

	class MyButtonSend implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
		if(convList.isActive()) {
			String toBeSent = txtSEND.getText();
			txtSEND.setText("");
			convWrit.send(toBeSent);
		}
      }
   }

	public InetAddress getAddress() {
		return distantSocket.getInetAddress();
	}

	public void setPseudo(String p) {
		this.distantID = p;
		if(convList.isActive()) {
			lblRCV.setText("Conversation with " + distantID);
		}
	}

	public String getPseudo() {
		return this.distantID;
	 }

   public class MyButtonExitListener implements ActionListener
   {
	  @SuppressWarnings( "deprecation" )
      public void actionPerformed(ActionEvent e)
      {	    try {
			 // Fermeture de la découverte réseau
			 convList.close();
			 System.out.println("closed Listener");
			 convWrit.close();
			 System.out.println("closed Writer");
			 treceiv.stop();
			 Tsend.stop();
			 System.out.println("closed Stopped Listener and Writer threads");
			 distantSocket.close();
			 System.out.println("closed socket");
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			// Fermeture de l'historique
			currentHistory.closeHistory();
			System.out.println("Closed History");
			 // Fermeture de la fenêtre
		    login.dispose();
			System.out.println("closed GUI");
      }
   }

	public Conversation(Socket dSocket, History hist, String s) {
		// partie affichage
		distantID = s;
		this.currentHistory = hist;
		login = new Dialog(this);
        lblRCV = new Label("Conversation with " + distantID);
		txtSEND = new TextField();  
		Button sendButton = new Button("Send Message");      
		sendButton.addActionListener(new MyButtonSend());
		Button exit = new Button("Quit");
		exit.addActionListener(new MyButtonExitListener());
		TextArea tA = new TextArea();
		login.setLayout(new GridLayout(0, 1));
		login.setSize(850, 400);
		login.add(lblRCV);
		login.add(sendButton);
		login.add(txtSEND);
		login.add(exit);	
		login.add(tA);
		login.setVisible(true);

		this.distantSocket = dSocket;

		// Create Sender and Receiver threads		
		convList = new ConversationListener(distantSocket, tA, lblRCV, currentHistory);
		treceiv = new Thread(convList);
		treceiv.start();

		convWrit = new ConversationWriter(this.distantSocket, tA, currentHistory);
		Tsend = new Thread(convList);
		Tsend.start();
		System.out.println("conv created");
	}

	public void sendMsg(String m) {
		convWrit.send(m);
	}

	public void run() {
		System.out.println("conv started");
		while(true) {
			/*if(convList.isActive()) {
				lblRCV.setText("Conversation with " + distantID);
			}*/
			/*if(convList.isActive()) {
				System.out.println("socket closed");
				this.closeConversation();
			}*/
			
		}
	}
	




}
