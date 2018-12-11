package clavardage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.Thread;
import java.util.Set;
import java.util.Iterator;

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
				while(it.hasNext()) {
					String aUser = it.next();
					if(it.hasNext()) {
						onlineStr = onlineStr + aUser +"," ;
					}
					else {
						onlineStr = onlineStr + aUser + ".";
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
   private static OnlineUsersManager networkDiscovery;

   class MyButtonChatListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
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
            login.setLayout(new GridLayout(0, 1));
            Button chat = new Button("Send a message");        
            chat.addActionListener(new MyButtonChatListener());
            Button exit = new Button("Quit");
            exit.addActionListener(new MyButtonExitListener());
            login.setSize(850, 200);
            login.add(lblInput);
            login.add(lblOnline);    
            login.add(chat);   
            login.add(exit);    
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
