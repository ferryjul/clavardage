package clavardage;

import java.util.Date;


public class HistoryManager implements Runnable{


	public History createHistory(String idDist , String fileName, Date connectDate)
	{
		return new History(idDist ,fileName, connectDate);
	}


	public void run() {
		System.out.println("History Manager Started");
	}
}
