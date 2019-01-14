package clavardage;

import java.io.IOException;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.sql.*;

public class History {

	private String id;
	private File fileHist;
	private String name_and_date;
	private Date sessionDate;
	private FileWriter fWrit; // si on utilise persistence des données avec fichiers (sinon inutile)
	private Boolean BDD;
	private String url = "jdbc:mysql://localhost:3306/historique";
	private String utilisateur = "java";
	private String motDePasse = "pom2pin";

	public History(String idDist , String fileName, Date connectDate, Boolean b)
	{
		this.BDD = b;
		this.sessionDate = connectDate;
		this.id = idDist;
		this.name_and_date = fileName;
		
		if (BDD == true)
		{
		Connection connexion = null;
		Statement statement = null;
		int statut = 0;
		
		try {
    connexion = DriverManager.getConnection( url, utilisateur, motDePasse ); 
    statement = connexion.createStatement();
    statut = statement.executeUpdate("INSERT INTO HistConv VALUES ('"+ fileName +"','----- DEBUT CONVERSATION -----')");
    if (statut == 0){
    System.out.println("Erreur mise à jour de la base de données (nouvelle conv)");
    }
    } catch ( SQLException e ) {
    System.out.println("Erreur SQL début de conversation");
} finally {		
    if ( statement != null ) {
        try {
            /* Puis on ferme le Statement */
            statement.close();
        } catch ( SQLException ignore ) {
        }
    }
    if ( connexion != null ) {
        try {
            /* Et enfin on ferme la connexion */
            connexion.close();
        } catch ( SQLException ignore ) {
        }
    }
 
}
		}
		else // persistance des données avec fichiers si jamais BDD fixé à false
		{
		this.fileHist = new File("histories/"+fileName);

		try{
			fWrit = new FileWriter(fileHist, true);
			fWrit.write("----- Pseudo :" + id + "-----\n");
			fWrit.write("----- Date de la session de conversation :" + sessionDate.toString() + "-----\n");
			fWrit.flush();
		} catch (IOException e){
			System.err.println("Create FileWRITER FAIL");
			e.printStackTrace();
		}
		}
			
	}

	public synchronized void updateHist(String msg) {
		if (BDD == true)
			{
				Connection connexion = null;
				Statement statement = null;
				int statut = 0;
		
				try {
	   				connexion = DriverManager.getConnection( url, utilisateur, motDePasse );    
					statement = connexion.createStatement();
					statut = statement.executeUpdate("INSERT INTO HistConv VALUES ('" + name_and_date + "', '"+ msg +"')");
					if (statut == 0){
	   					System.out.println("Erreur mise à jour de la base de données (message)");
					}
				} catch ( SQLException e ) {
					System.out.println("Erreur SQL message écriture");
				} finally {
	   				if ( statement != null ) {
		   				try {
		        			/* Puis on ferme le Statement */
		        			statement.close();
		    			} catch ( SQLException ignore ) {
		    			}
	   				}
	  		  		if ( connexion != null ) {
		    			try {
		       				 /* Et enfin on ferme la connexion */
		        			connexion.close();
		    				} catch ( SQLException ignore ) {
		    				}
					}
				}
			}
	 
	 	else{
			try{
				fWrit.write(msg+"\n");
				fWrit.flush();
			} catch (IOException e){
				System.err.println("update history fail");
				e.printStackTrace();
			}
		}	
	}

	public void closeHistory() {
		
		
		
		if (BDD == true)
		{
			Connection connexion = null;
			Statement statement = null;
			ResultSet resultat  = null;
			int statut = 0;
		
			try {
   				connexion = DriverManager.getConnection( url, utilisateur, motDePasse );    
    			statement = connexion.createStatement();
    			statut = statement.executeUpdate("INSERT INTO HistConv VALUES ('" + name_and_date +"','----- FIN CONVERSATION -----')");
    			if (statut == 0){
   					System.out.println("Erreur mise à jour de la base de données (fin de conv)");
    			}
    		} catch ( SQLException e ) {
    			System.out.println("Erreur SQL fin de conversation");
			} finally {
   				if ( statement != null ) {
       				try {
            			/* Puis on ferme le Statement */
            			statement.close();
        			} catch ( SQLException ignore ) {
        			}
   				}
  		  		if ( connexion != null ) {
        			try {
           				 /* Et enfin on ferme la connexion */
            			connexion.close();
        				} catch ( SQLException ignore ) {
        				}
    			}
    		}
    	}
 
 		else{
			try{
				fWrit.write("----- FIN session de conversation :" + (new java.util.Date()).toString() + "-----\n");
				fWrit.close();
			
			} catch (IOException e){
				System.err.println("close file write failed");
				e.printStackTrace();
			}
		}	
	}

}
