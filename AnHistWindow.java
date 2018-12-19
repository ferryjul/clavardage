package clavardage;


import java.awt.*;
import java.util.ArrayList ;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AnHistWindow extends Frame
{
   private ArrayList<String> msgList;
   private Dialog login;

   public class MyButtonExitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      { 
         login.dispose();
      }
   }

	WindowListener exitListener = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
		     login.dispose();
		}
	};

	public AnHistWindow(ArrayList<String> l)
	{
		this.msgList = l;
        login = new Dialog(this);
        JLabel lblInput = new JLabel(msgList.get(0),
              JLabel.CENTER);
		  JLabel lbl2 = new JLabel(msgList.get(1),
              JLabel.CENTER);
        login.setLayout(new GridLayout(4, 1));   
		TextArea msgDisplay = new TextArea();
		msgDisplay.setRows(10);
        JButton exit = new JButton("Quit");
		exit.addActionListener(new MyButtonExitListener());
        login.setSize(850, 450);
        login.add(lblInput);
		login.add(lbl2);
		login.add(msgDisplay);
		for(String aMsg: msgList) {
			msgDisplay.append(aMsg + "\n");
		}
		login.add(exit);    
		login.setVisible(true);
		login.addWindowListener(exitListener);
	}

}

