package clavardage;

import java.util.Date;
import java.util.ArrayList;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.sql.*;

public class HistoryManager implements Runnable{

	public static Boolean BDD;
	String url = "jdbc:mysql://localhost:3306/historique?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC";
	String utilisateur = "java";
	String motDePasse = "pom2pin";
	


	public History createHistory(String idDist , String fileName, Date connectDate)
	{
		return new History(idDist ,fileName, connectDate, BDD);
	}


	public void run() {
		System.out.println("History Manager Started");
	}

	public ArrayList<String> listAllHist()
	{
		ArrayList<String> listHist = new ArrayList<String>();
		if (BDD == true)
		{
		    try {
		    	System.out.println( "Chargement du driver..." );
		        Class.forName( "com.mysql.cj.jdbc.Driver" );
		        System.out.println( "Driver charge !" );
		    } catch ( ClassNotFoundException e ) {
		    	System.out.println( "Erreur lors du chargement : le driver n'a pas ete trouve dans le classpath !");

		    }
		    
			Connection connexion = null;
			Statement statement = null;
			ResultSet resultat  = null;
		
		
			try {
				System.out.println("Tentative de connection a la BDD (allHist)");
   				connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
   				System.out.println("Connecter a la BDD");
   				statement = connexion.createStatement();
				resultat = statement.executeQuery( "SELECT DISTINCT pseudo_and_date_debut_conv FROM HistConv;");

				while ( resultat.next() ) {
   					 listHist.add(resultat.getString(1));
				}
			
    		} catch ( SQLException e ) {
    			System.out.println("Erreur SQL listAllHist");
    			e.printStackTrace();
			} finally {
				if ( resultat != null ) {
       				try {
            /* On commence par fermer le ResultSet */
           				resultat.close();
       	 			} catch ( SQLException ignore ) {
        			}
    			}
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
			File repertoire = new File("histories");
			String liste[] = repertoire.list();
		
			 if (liste != null) {         
		        for (int i = 0; i < liste.length; i++) {
		            listHist.add(liste[i]);
		        }
		}
		}
		
		return listHist;
		
	}

	public ArrayList<String> readHistory(String fileName)
	{
		ArrayList<String> listHist = new ArrayList<String>();
		
		if (BDD == true)
		{
	
			Connection connexion = null;
			Statement statement = null;
			ResultSet resultat  = null;
	
		
			try {
   				connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
   				statement = connexion.createStatement();
				resultat = statement.executeQuery("SELECT * FROM HistConv WHERE pseudo_and_date_debut_conv='"+ fileName +"';");

				while ( resultat.next() ) {
   					 listHist.add(resultat.getString(2));
				}
			
    		} catch ( SQLException e ) {
    			System.out.println("Erreur SQL readHistory");
			} finally {
				if ( resultat != null ) {
       				try {
            /* On commence par fermer le ResultSet */
           				resultat.close();
       	 			} catch ( SQLException ignore ) {
        			}
    			}
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
			File fileHist = new File("histories/"+fileName);
			BufferedReader lecteurAvecBuffer = null;
			String ligne;
			try{
				lecteurAvecBuffer = new BufferedReader(new FileReader(fileHist));
			while ((ligne = lecteurAvecBuffer.readLine()) != null){
		  		listHist.add(ligne);
				}

		  	} catch(IOException e)
		  	{
			System.out.println("Erreur readHistory");
		  	}
      	}
   
		return listHist;
	}
	

}
