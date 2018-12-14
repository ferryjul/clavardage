package clavardage;

import java.io.IOException;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;


public class History {

	private String id;
	private File fileHist;
	private Date sessionDate;
	private FileWriter fWrit;


	public History(String idDist , String fileName, Date connectDate)
	{
		this.sessionDate = connectDate;
		this.id = idDist;
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

	public void updateHist(String msg) {
		try{
			fWrit.write("-- "+msg+"\n");
			fWrit.flush();
		} catch (IOException e){
			System.err.println("update history fail");
			e.printStackTrace();
		}
	}

		public void closeHistory() {
		try{
			fWrit.close();
			
		} catch (IOException e){
			System.err.println("close file write failed");
			e.printStackTrace();
		}
	}


	

}
