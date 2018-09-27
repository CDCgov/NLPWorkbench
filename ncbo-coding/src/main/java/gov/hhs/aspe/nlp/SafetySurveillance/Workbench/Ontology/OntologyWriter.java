package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Ontology;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Guangfan.Zhang
 *
 * Engility Corporation
 * June 15, 2017
 * Revised June 11, 2018
 * Handle NCBO's ontology information from/to a file; 
 */

public class OntologyWriter {
	String fileName = "";
	public OntologyWriter(String fileName){
		this.fileName = fileName;
	}
	public OntologyWriter(){
	}
	
	/**
	 * Write ontology information from OntologyList to a file
	 * 
	 * @param oList: OntologyList
	 */
	public void exec(OntologyList oList){
		
		try{
		    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		    ArrayList<String> names = oList.name;
		    writer.println("Ontology" + ":: " + "Acronym");
		    for(int i = 0; i < names.size(); i++){
		    	String name = names.get(i).trim();
		    	String acry = oList.nameToAcronym.get(name).trim();
			    writer.println(name + ":: " + acry);
		    }
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Read ontology information from jar file
	 * 
	 * @param oList: OntologyList
	 */
	public void readFromJar(OntologyList oList){
		
		try{
			if(oList == null )
				oList = new OntologyList();
			
			InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("ontology/OntologyList.txt");
			if (in == null) {
				return ;
			}

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			line = br.readLine();
			ArrayList<String> names = oList.name, acrys = oList.acrononym;
			HashMap<String, String> nameToAcronym = oList.nameToAcronym;
			HashMap<String, String> acronymToName = oList.acronymToName;

			while ((line = br.readLine()) != null) {
				String[] items = line.split(":: ");
				String name = items[0].trim();
				String acry = items[1].trim();
				names.add(name);
				acrys.add(acry);
				nameToAcronym.put(name, acry);
				acronymToName.put(acry,  name);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Read ontology information from a plain text file physically located on hard disk
	 * 
	 * @param oList: OntologyList
	 */
	public void read(OntologyList oList){
		
		try{
			if(oList == null )
				oList = new OntologyList();
			
			FileInputStream fis = new FileInputStream(fileName);
			 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		 
			String line = null;
			line = br.readLine();
			ArrayList<String> names = oList.name, acrys = oList.acrononym;
			HashMap<String, String> nameToAcronym = oList.nameToAcronym;
			HashMap<String, String> acronymToName = oList.acronymToName;
			
			while ((line = br.readLine()) != null) {
				String[] items = line.split(":: ");
				String name = items[0].trim();
				String acry = items[1].trim();
				names.add(name);
				acrys.add(acry);
				nameToAcronym.put(name, acry);
				acronymToName.put(acry,  name);
			}
		 
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
