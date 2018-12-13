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
	public void closeConversation(){
		 try {
			 distantSocket.close();
			 // Fermeture de la découverte réseau
			 convList.close();
			 System.out.println("closed 1");
			 convWrit.close();
			 System.out.println("closed 2");
			 distantSocket.close();
			 System.out.println("closed 3");
			 treceiv.stop();
			 Tsend.stop();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			 // Fermeture de la fenêtre
		    login.dispose();
			System.out.println("closed 4");		
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
			 System.out.println("closed 0");
			 convList.close();
			 System.out.println("closed 1");
			 convWrit.close();
			 System.out.println("closed 2");
			 distantSocket.close();
			 System.out.println("closed 3");
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			 // Fermeture de la fenêtre
		    login.dispose();
			System.out.println("closed 4");
      }
   }

	public Conversation(Socket dSocket) {
		// partie affichage
		login = new Dialog(this);
        lblRCV = new Label("");
		txtSEND = new TextField();  
		Button sendButton = new Button("Send Message");      
		sendButton.addActionListener(new MyButtonSend());
		Button exit = new Button("Quit");
		exit.addActionListener(new MyButtonExitListener());
		login.setLayout(new GridLayout(0, 1));
		login.setSize(850, 200);
		login.add(sendButton);
		login.add(txtSEND);
		login.add(exit);
		login.add(lblRCV);
		login.setVisible(true);
		// Create Sender and Receiver threads
		this.distantSocket = dSocket;
		convList = new ConversationListener(distantSocket, login);

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
			if(!distantSocket.isConnected()) {
				System.out.println("socket closed");
				this.closeConversation();
			}
		}
	}
	




}
