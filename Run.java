package clavardage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@SuppressWarnings("serial")
public class Run extends Frame
{
   private static Run r;
   private static Label lblInfo;
   private static Label lblError;
   private Dialog login;
   private TextField box;

   class MyButtonValidateListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
		String wantedPseudo = box.getText();
		if(wantedPseudo.equals("")) {
			lblError.setText("Impossible to login in with an empty pseudo !");
		} else {
			new MainWindow(wantedPseudo);
			r.dispose();
		}
      }
   }

   public class MyButtonExitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         login.dispose();
         System.exit(0);
      }
   }

   public Run()
   {
			this.setTitle("Chat Room");
			box = new TextField();
            login = new Dialog(this);
			lblError = new Label("");
            lblInfo = new Label("Welcome. Enter your pseudo please.",
                  Label.CENTER); // Construct by invoking a constructor via the new
                                 // operator
            login.setLayout(new GridLayout(0, 1));
            Button validate = new Button("Validate Pseudo");        
            validate.addActionListener(new MyButtonValidateListener());
            Button exit = new Button("Quit");
            exit.addActionListener(new MyButtonExitListener());
            login.setSize(850, 200);
            login.add(lblInfo);   
			login.add(lblError);
			login.add(box);
            login.add(validate);   
            login.add(exit);    
            login.setVisible(true);
   }

	public static void main(String argv[]) {
		r = new Run();
	}
	
}
