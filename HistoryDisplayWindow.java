package clavardage;

import java.awt.*;
import java.util.ArrayList ;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HistoryDisplayWindow extends Frame
{
   private ArrayList<String> filesList;
   private Label lblInput;
   private Dialog login;

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

	public HistoryDisplayWindow(ArrayList<String> l)
	{
		this.filesList = l;
        login = new Dialog(this);
        lblInput = new Label("List of all available history files for this user of the computer :",
              Label.CENTER);
        login.setLayout(new GridLayout(0, 1));   

        Button exit = new Button("Quit");
		exit.addActionListener(new MyButtonExitListener());

        login.setSize(850, 200);
        login.add(lblInput);
		if(!l.isEmpty()) {
			for(String anHist : l) {
				Button displayHist = new Button("Display " + anHist);
				displayHist.addActionListener(new MyButtonHistListener(anHist));
        		login.add(displayHist);
				login.setSize(850, login.getHeight()+20);
			}
		}
		else {
			Label ll = new Label("No history found");
			login.add(ll);
		}
		login.add(exit);    
		login.setVisible(true);
	}

}

