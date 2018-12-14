package clavardage;

import java.awt.*;
import java.util.ArrayList ;
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


	public AnHistWindow(ArrayList<String> l)
	{
		this.msgList = l;
        login = new Dialog(this);
        Label lblInput = new Label(msgList.get(0),
              Label.CENTER);
		Label lbl2 = new Label(msgList.get(1),
              Label.CENTER);
        login.setLayout(new GridLayout(4, 1));   
		TextArea msgDisplay = new TextArea();
		msgDisplay.setRows(10);
        Button exit = new Button("Quit");
		exit.addActionListener(new MyButtonExitListener());
        login.setSize(850, 200);
        login.add(lblInput);
		login.add(lbl2);
		login.add(msgDisplay);
		for(String aMsg: msgList) {
			msgDisplay.append(aMsg + "\n");
		}
		login.add(exit);    
		login.setVisible(true);
	}

}

