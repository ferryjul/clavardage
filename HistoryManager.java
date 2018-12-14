package clavardage;

import java.util.Date;
import java.util.ArrayList;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class HistoryManager implements Runnable{


	public History createHistory(String idDist , String fileName, Date connectDate)
	{
		return new History(idDist ,fileName, connectDate);
	}


	public void run() {
		System.out.println("History Manager Started");
	}

	public ArrayList<String> listAllHist()
	{
		File repertoire = new File("histories");
		String liste[] = repertoire.list();
		ArrayList<String> listHist = new ArrayList<String>();
		
		 if (liste != null) {         
            for (int i = 0; i < liste.length; i++) {
                listHist.add(liste[i]);
            }
		}
		
		return listHist;
		
	}

	public ArrayList<String> readHistory(String fileName)
	{
		File fileHist = new File("histories/"+fileName);
		BufferedReader lecteurAvecBuffer = null;
		ArrayList<String> listHist = new ArrayList<String>();
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
   
		return listHist;
	}
	

}
