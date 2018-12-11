package clavardage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.Thread;

@SuppressWarnings("serial")
public class MainWindow extends Frame
{
   private static Label lblInput;
   private Dialog login;
   private static String currentUserName;
   private static Label lblOnline; //unused now, to be used later.
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
		 networkDiscovery.closeCommunications();
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
			networkDiscovery = new OnlineUsersManager();
			Thread networkDiscoveryThread = new Thread(networkDiscovery);
			networkDiscoveryThread.start();
   }
}
