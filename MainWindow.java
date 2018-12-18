package clavardage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.util.Iterator;
import java.net.InetAddress;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class MainWindow extends Frame {

   public class onlineUpdater implements Runnable {
      JPanel aP;
      OnlineUsersManager networkDiscovery;
      ConversationManager CM;

      public onlineUpdater(JPanel p, OnlineUsersManager nD,
            ConversationManager c) {
         this.aP = p;
         this.networkDiscovery = nD;
         this.CM = c;
      }

      public void run() {
         while (true) {
            Set<String> usersSet = networkDiscovery.getOnlineUsers();
            if (usersSet != null) { // Test if we have modifications to be done
               // aP.removeAll();
               Iterator<String> it = usersSet.iterator();
               JLabel jl = new JLabel("-------- Currently Online Users : --------");
               aP.add(jl);
               synchronized (usersSet) {
                  while (it.hasNext()) {
                     String aUser = it.next();
                     System.out.println("test " + aUser);
                     JLabel jll = new JLabel(aUser);
                     aP.add(jll);
                  }
               }
               CM.updatePseudos();
            }
         }
      }

   }

   private static JLabel lblInput;
   private Dialog login;
   private static String currentUserName;
   private static JPanel panelOnline;
   private static Label lblPseudoError;
   private static OnlineUsersManager networkDiscovery;
   private static TextField txtNewPseudo;
   private static TextField openConvWith;
   private static ConversationManager CM;
   private static HistoryManager HM;

   class DisplayHistory implements Runnable {

      public void setList(ArrayList<String> l) {
         HistoryDisplayWindow w = new HistoryDisplayWindow(l);
      }

      public void run() {

      }
   }

   class MyButtonHistListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         System.out.println("Creating an history display window");
         DisplayHistory d = new DisplayHistory();
         d.setList((new HistoryManager()).listAllHist());
         Thread thrD = new Thread(d);
         thrD.start();
      }
   }

   class MyButtonChatListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         String distantUser = openConvWith.getText();
         openConvWith.setText("");
         if (distantUser.equals("")) {
            lblPseudoError.setText("Error : Empty field");
         } else if (distantUser.equals(currentUserName)) {
            lblPseudoError
                  .setText("Error : " + distantUser + " is your own pseudo");
         } else if (!networkDiscovery.isOnline(distantUser)) {
            lblPseudoError.setText("Error : " + distantUser + " is not online");
         } else {
            InetAddress hostAddress = networkDiscovery.getAddress(distantUser);
            System.out.println("Beginning communication with " + distantUser
                  + " at @" + hostAddress);
            CM.createConversation(hostAddress, 8042);
         }
      }
   }

   public class MyButtonChangePseudo implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         if (!txtNewPseudo.getText().equals("")) {
            String newPse = txtNewPseudo.getText();
            int returnCode = networkDiscovery.notifyNewPseudo(newPse);
            txtNewPseudo.setText("");
            if (returnCode == -1) {
               lblPseudoError
                     .setText("Error : this pseudo is already used by a user");
            } else if (returnCode == -2) {
               lblPseudoError.setText("Error : this is already your pseudo");
            } else if (returnCode == 0) {
               lblPseudoError.setText("Pseudo change OK");
               currentUserName = newPse;
               lblInput.setText("Welcome " + currentUserName
                     + " in myChatroom. You can now start messaging online users");
            } else {
               lblPseudoError.setText("Error : unexpected return Code");
            }
         } else {
            lblPseudoError.setText("Error : empty text field");
         }
      }
   }

   public class MyButtonExitListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         // Fermeture de la découverte réseau
         synchronized (networkDiscovery) {
            networkDiscovery.notifyOffline();
            networkDiscovery.closeCommunications();
         }
         /*
          * synchronized(CM) { CM.closeCM(); }
          */
         login.dispose();
         System.exit(0);
      }
   }

   public MainWindow(String userName) {
      // Création de la fenêtre graphique
      this.setTitle("Chat Room");
      currentUserName = userName;
      login = new Dialog(this);
      lblInput = new JLabel(
            "Welcome " + currentUserName
                  + " in myChatroom. You can now start messaging online users",
            JLabel.CENTER); // Construct by invoking a constructor via the new
                           // operator
      panelOnline = new JPanel(new GridLayout(0, 1));
      JScrollPane sB = new JScrollPane(panelOnline);
      lblPseudoError = new Label("");
      login.setLayout(new GridLayout(0, 1));
      JButton chat = new JButton("Send a message to :");
      openConvWith = new TextField();
      txtNewPseudo = new TextField();
      JButton changePseudo = new JButton("Set new pseudo :");
      changePseudo.addActionListener(new MyButtonChangePseudo());
      chat.addActionListener(new MyButtonChatListener());
      JButton exit = new JButton("Quit");
      exit.addActionListener(new MyButtonExitListener());
      JButton displayHist = new JButton("Display all available histories");
      displayHist.addActionListener(new MyButtonHistListener());
      login.setSize(850, 500);
      login.add(lblInput);
      login.add(sB);
      login.add(chat);
      login.add(openConvWith);
      login.add(exit);
      login.add(changePseudo);
      login.add(txtNewPseudo);
      login.add(lblPseudoError);
      login.add(displayHist);
      login.setVisible(true);

      // Lancement de la découverte Réseau
      networkDiscovery = new OnlineUsersManager(userName);
      Thread networkDiscoveryThread = new Thread(networkDiscovery);
      networkDiscoveryThread.start();

      // Création du gestionnaire d'historique
      HistoryManager HM = new HistoryManager();
      Thread historyManagerThread = new Thread(HM);
      historyManagerThread.start();

      // Lancement du service de Messagerie
      CM = new ConversationManager();
      CM.setDiscovery(networkDiscovery);
      CM.setHistoryM(HM);
      Thread t = new Thread(CM);
      t.start();

      // Lancement du thread de màj des pseudos
      onlineUpdater onlineUpdt = new onlineUpdater(panelOnline,
            networkDiscovery, CM);
      Thread onlineUpdtThread = new Thread(onlineUpdt);
      onlineUpdtThread.start();

   }
}
