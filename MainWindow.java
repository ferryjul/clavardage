package clavardage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.Thread;
import java.util.Set;
import java.util.Iterator;
import java.net.InetAddress;

@SuppressWarnings("serial")
public class MainWindow extends Frame
{

   public class onlineUpdater implements Runnable {
		Label aLbl;
		OnlineUsersManager networkDiscovery;

		public onlineUpdater(Label lbl, OnlineUsersManager nD) {
			this.aLbl = lbl;
			this.networkDiscovery = nD;
		}

		public void run() {
			while(true) {
				Set<String> usersSet = networkDiscovery.getOnlineUsers();
				Iterator<String> it = usersSet.iterator();
				String onlineStr = "Currently Online Users :";
				synchronized(usersSet) {
				while(it.hasNext()) {
					String aUser = it.next();
					if(it.hasNext()) {
						onlineStr = onlineStr + aUser +"," ;
					}
					else {
						onlineStr = onlineStr + aUser + ".";
					}
				}
				}
				lblOnline.setText(onlineStr);
			}
		}

	}

   private static Label lblInput;
   private Dialog login;
   private static String currentUserName;
   private static Label lblOnline;
   private static Label lblPseudoError;
   private static OnlineUsersManager networkDiscovery;
   private static TextField txtNewPseudo;
   private static TextField openConvWith;

   class MyButtonChatListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
		String distantUser = openConvWith.getText();
		openConvWith.setText("");
		if(distantUser.equals("")) {
			lblPseudoError.setText("Error : Empty field");
		}
		else if(distantUser.equals(currentUserName)) {
			lblPseudoError.setText("Error : " + distantUser + " is your own pseudo");
		}
		else if(!networkDiscovery.isOnline(distantUser)) {
			lblPseudoError.setText("Error : " + distantUser + " is not online");
		}
		else {
			InetAddress hostAddress = networkDiscovery.getAddress(distantUser);
			System.out.println("Beginning communication with " + distantUser + " at @" + hostAddress);
		}
      }
   }

   public class MyButtonChangePseudo implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
		if(!txtNewPseudo.getText().equals("")) {
			String newPse = txtNewPseudo.getText();
			int returnCode = networkDiscovery.notifyNewPseudo(newPse);		 
			txtNewPseudo.setText("");
			if(returnCode == -1) {
				lblPseudoError.setText("Error : this pseudo is already used by a user");
			} else if(returnCode == -2) {
				lblPseudoError.setText("Error : this is already your pseudo");
			} else if(returnCode == 0) {
				lblPseudoError.setText("Pseudo change OK");
				currentUserName = newPse;
				lblInput.setText("Welcome " + currentUserName
                  + " in myChatroom. You can now start messaging online users");
			} else {
				lblPseudoError.setText("Error : unexpected return Code");
			}
		}
		else {
			lblPseudoError.setText("Error : empty text field");
		}
      }
   }

   public class MyButtonExitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
		 // Fermeture de la découverte réseau
		 synchronized(networkDiscovery) {
		  networkDiscovery.notifyOffline();
		  networkDiscovery.closeCommunications();	 
		 }
         login.dispose();
         System.exit(0);
      }
   }

   public MainWindow(String userName)
   {
			// Création de la fenêtre graphique
      		currentUserName = userName;    
            login = new Dialog(this);
            lblInput = new Label("Welcome " + currentUserName
                  + " in myChatroom. You can now start messaging online users",
                  Label.CENTER); // Construct by invoking a constructor via the new
                                 // operator
            lblOnline = new Label("");
			lblPseudoError = new Label("");
            login.setLayout(new GridLayout(0, 1));
            Button chat = new Button("Send a message to :");      
			openConvWith = new TextField();  
			txtNewPseudo = new TextField();
			Button changePseudo = new Button("Set new pseudo :");
			changePseudo.addActionListener(new MyButtonChangePseudo());
            chat.addActionListener(new MyButtonChatListener());
            Button exit = new Button("Quit");
            exit.addActionListener(new MyButtonExitListener());
            login.setSize(850, 200);
            login.add(lblInput);
            login.add(lblOnline);    
            login.add(chat);   
			login.add(openConvWith);
            login.add(exit);    
			login.add(changePseudo);
			login.add(txtNewPseudo);
			login.add(lblPseudoError);
            login.setVisible(true);
		
			// Lancement de la découverte Réseau
			networkDiscovery = new OnlineUsersManager(userName);
			Thread networkDiscoveryThread = new Thread(networkDiscovery);
			networkDiscoveryThread.start();

			onlineUpdater onlineUpdt = new onlineUpdater(lblOnline, networkDiscovery);
			Thread onlineUpdtThread = new Thread(onlineUpdt);
			onlineUpdtThread.start();

			
   }
}
