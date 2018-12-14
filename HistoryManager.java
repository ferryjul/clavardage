package clavardage;

import java.util.Date;
import java.util.ArrayList;
import java.io.*;

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

	

}
