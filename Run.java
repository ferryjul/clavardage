package clavardage;

import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Run extends Frame
{
   private static Run r;
   private static JLabel lblInfo;
   private static JLabel lblError;
   private Dialog login;
   private TextField box;
   private static PreConnectDiscovery discovery;
   private static JRadioButton modeSelectionUDP;
   private static JRadioButton modeSelectionHTTP;


   class MyButtonValidateListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
	    if(modeSelectionUDP.isSelected()) {
		String wantedPseudo = box.getText();
		if(wantedPseudo.equals("")) {
			lblError.setText("Impossible to login in with an empty pseudo !");
		} else {
			if(discovery.getOnlineUsers().contains(wantedPseudo)) {
			lblError.setText("Impossible to login ; " + wantedPseudo + " is already Online.");
			}
			else {
				try {
					discovery.closeCommunications();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				finally {
					new MainWindow(wantedPseudo);
					r.dispose();
				}
			}
		}
		} else {
			lblError.setText("Not implemented yet.");
	    }
      }
   }

   public class MyButtonExitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
	 		try {
				discovery.closeCommunications();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
         login.dispose();
         System.exit(0);
      }
   }

	WindowListener exitListener = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
		     try {
				discovery.closeCommunications();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
         login.dispose();
         System.exit(0);
		}
	};

   public Run()
   {
			this.setTitle("Chat Room");
			
			discovery = new PreConnectDiscovery();
			modeSelectionUDP = new JRadioButton("Local Mode (UDP-Based)");
			modeSelectionHTTP = new JRadioButton("Distant Mode (HTTP-Based)");
			ButtonGroup group = new ButtonGroup();
            group.add(modeSelectionUDP);
			group.add(modeSelectionHTTP);
			//modeSelectionHTTP.setEnabled(false);
			modeSelectionUDP.setEnabled(true);
			modeSelectionUDP.setSelected(true);
    		//modeSelectionUDP.setMnemonic(KeyEvent.VK_R);
   			//modeSelectionUDP.setActionCommand(rabbitString);
			(new Thread(discovery)).start();
			box = new TextField();
            login = new Dialog(this);
			lblError = new JLabel("");
            lblInfo = new JLabel("Welcome. Enter your pseudo please.",
                  JLabel.CENTER); // Construct by invoking a constructor via the new
                                 // operator
            login.setLayout(new GridLayout(0, 1));
            JButton validate = new JButton("Validate Pseudo");        
            validate.addActionListener(new MyButtonValidateListener());
            JButton exit = new JButton("Quit");
            exit.addActionListener(new MyButtonExitListener());		   
            login.setSize(850, 300);
            login.add(lblInfo);   
			login.add(lblError);
			login.add(box);
            login.add(validate);
			login.add(modeSelectionUDP);   
			login.add(modeSelectionHTTP);   
            login.add(exit);    
            login.setVisible(true);
			login.addWindowListener(exitListener);
   }

	public static void main(String argv[]) {
		r = new Run();
	}
	
}
