package clavardage;
import java.net.Socket;
import java.net.InetAddress;

import javax.swing.JLabel;

import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

@SuppressWarnings("serial")
public class Conversation extends Frame {
	// Composants Réseau
	private String distantID;
	private Socket distantSocket;
	private ConversationListener convList;
	private ConversationWriter convWrit;
	private History currentHistory;
	private boolean isActive;

	// Composants Graphiques
	private JTextField txtSEND;	
	private Dialog login;
	private JLabel lblRCV;
	private Thread Tsend;
	private Thread treceiv;

	@SuppressWarnings( "deprecation" )
	public void closeConversation(){
		 try {
			 isActive = false;
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
			this.dispose();
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

	public void firstPlan() {
		/*this.setAlwaysOnTop(true);
		try {
			Thread.sleep(10);
		} catch(Exception ex) {

		}
		this.toFront();
		this.setAlwaysOnTop(false);*/
		this.setVisible(true);
	}

	private Action sendAction = new AbstractAction("sendAction")
    {
        public void actionPerformed(ActionEvent e)
        {
            if(convList.isActive()) {
			String toBeSent = txtSEND.getText();
			txtSEND.setText("");
			convWrit.send(toBeSent);
		}
        }
    };

	public boolean isActive() {
		return (this.isActive && convList.isActive());
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

	WindowListener exitListener = new WindowAdapter() {

		@Override
		@SuppressWarnings( "deprecation" )
		public void windowClosing(WindowEvent e) {
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
	};

	public Conversation(Socket dSocket, History hist, String s) {
		// partie affichage
		isActive = true;
		distantID = s;
		this.currentHistory = hist;
		login = new Dialog(this);
        lblRCV = new JLabel("Conversation with " + distantID);
		txtSEND = new JTextField();  
		txtSEND.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
		        if(e.getKeyCode() == KeyEvent.VK_ENTER){
		            if(convList.isActive()) {
					String toBeSent = txtSEND.getText();
					txtSEND.setText("");
					convWrit.send(toBeSent);
			  		}
		        }
		    }
    	});
		JButton sendButton = new JButton("Send Message");      
		//sendButton.addActionListener(new MyButtonSend());
		JButton exit = new JButton("Quit");
		exit.addActionListener(new MyButtonExitListener());
		sendButton.setAction(sendAction);
		sendButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "SEND");
        sendButton.getActionMap().put("SEND", sendAction);
		TextArea tA = new TextArea();
		login.setLayout(new GridLayout(0, 1));
		login.setSize(850, 400);
		login.add(lblRCV);
		login.add(sendButton);
		login.add(txtSEND);
		login.add(exit);	
		login.add(tA);
		login.setVisible(true);
		login.addWindowListener(exitListener);
		
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
