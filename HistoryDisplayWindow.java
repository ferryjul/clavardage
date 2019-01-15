package clavardage;

import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList ;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class HistoryDisplayWindow extends JFrame
{
   private JLabel lblInput;
   private JDialog login;

   public class MyButtonExitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      { 
         login.dispose();
      }
   }

	class MyButtonHistListener implements ActionListener
   {
	  private String filepath;

	  public MyButtonHistListener(String s) {
		this.filepath = s;
	  }

      public void actionPerformed(ActionEvent e)
      {
		new AnHistWindow((new HistoryManager()).readHistory(filepath));
      }
	
   }

	WindowListener exitListener = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
		      login.dispose();
		}
	};

	public HistoryDisplayWindow(ArrayList<String> l)
	{
	   this.setTitle("List of stored histories");
        login = new JDialog(this);
        lblInput = new JLabel("List of all available history files for this user of the computer :",
              JLabel.CENTER);
        login.setLayout(new GridLayout(0, 1));   

        JButton exit = new JButton("Quit");
		exit.addActionListener(new MyButtonExitListener());
		   JPanel myPannel = new JPanel(new GridLayout(0, 1));
        login.setSize(850, 200);
        login.add(lblInput);
		if(!l.isEmpty()) {
			for(String anHist : l) {
				JButton displayHist = new JButton("Display " + anHist);
				displayHist.addActionListener(new MyButtonHistListener(anHist));
				myPannel.add(displayHist);
        		//login.add(displayHist);
				//login.setSize(850, login.getHeight()+20);
			}
		}
		else {
			Label ll = new Label("No history found");
			login.add(ll);
		}
		JScrollPane sB = new JScrollPane(myPannel);
		login.add(sB);
		login.add(exit);    
		login.setVisible(true);
		login.addWindowListener(exitListener);
	}

}

