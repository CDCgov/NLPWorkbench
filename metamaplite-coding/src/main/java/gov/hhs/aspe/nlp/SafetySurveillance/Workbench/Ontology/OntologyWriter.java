/**
Guangfan.Zhang
*/

/**
 * 
 */
package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author Guangfan.Zhang
 *
 * Engility Corporation
 * Jun 15, 2017
 */
public class OntologyWriter {
	String fileName = "";
	public OntologyWriter(String fileName){
		this.fileName = fileName;
	}
	public void exec(OntologyList oList) throws FileNotFoundException, UnsupportedEncodingException{
		
//		try{
		    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		    ArrayList<String> names = oList.name;
		    writer.println("Ontology" + ":: " + "Acronym");
		    for(int i = 0; i < names.size(); i++){
		    	String name = names.get(i).trim();
		    	String acry = oList.nameToAcronym.get(name).trim();
			    writer.println(name + ":: " + acry);
		    }
		    writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		   // do something
//		}
	}
	public void read(OntologyList oList) throws IOException{
		
//		try{
			if(oList == null )
				oList = new OntologyList();
			
			FileInputStream fis = new FileInputStream(fileName);
			 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		 
			String line = null;
			line = br.readLine();
			ArrayList<String> names = oList.name, acrys = oList.acrononym;
//			HashMap<String, Ontology> ontologyMapping = null;
			HashMap<String, String> nameToAcronym = oList.nameToAcronym;
			HashMap<String, String> acronymToName = oList.acronymToName;
			ArrayList<String> lines = new ArrayList<String>();
			
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
//		} catch (IOException e) {
//			e.printStackTrace();
//		   // do something
//		}
	}
}
