package clavardage;
import java.io.IOException;
import java.net.Socket;
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
	// private History currentHistory

	// Composants Graphiques
	private static TextField txtSEND;	
	private Dialog login;
	private static Label lblRCV;
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
			 // Fermeture de la fenêtre
		    login.dispose();
			System.out.println("closed GUI");		
	}

	class MyButtonSend implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
		String toBeSent = txtSEND.getText();
		txtSEND.setText("");
		convWrit.send(toBeSent);
      }
   }

	 public class MyButtonExitListener implements ActionListener
   {
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
			 // Fermeture de la fenêtre
		    login.dispose();
			System.out.println("closed GUI");
      }
   }

	public Conversation(Socket dSocket) {
		// partie affichage
		login = new Dialog(this);
        lblRCV = new Label("Conversation with " + distantID);
		txtSEND = new TextField();  
		Button sendButton = new Button("Send Message");      
		sendButton.addActionListener(new MyButtonSend());
		Button exit = new Button("Quit");
		exit.addActionListener(new MyButtonExitListener());
		Scrollbar redSlider=new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 255);
 		login.add(redSlider);
		login.setLayout(new GridLayout(0, 1));
		login.setSize(850, 400);
		login.add(lblRCV);
		login.add(sendButton);
		login.add(txtSEND);
		login.add(exit);	
		login.setVisible(true);
		// Create Sender and Receiver threads
		this.distantSocket = dSocket;
		convList = new ConversationListener(distantSocket, login, lblRCV);

		treceiv = new Thread(convList);
		treceiv.start();

		convWrit = new ConversationWriter(this.distantSocket);
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
			if(convList.isActive()) {
				System.out.println("socket closed");
				this.closeConversation();
			}
		}
	}
	




}
