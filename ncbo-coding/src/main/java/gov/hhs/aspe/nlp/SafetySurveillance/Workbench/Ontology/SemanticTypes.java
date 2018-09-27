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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Guangfan.Zhang
 *
 * Engility Corporation
 * Jun 20, 2017
 */
public class SemanticTypes {
	public HashMap<String, String> semanticTypeMappings = new HashMap<String, String>();
	public HashMap<String, String> semanticTypeMappingsRev = new HashMap<String, String>();
	public ArrayList<String> semanticTypes = new ArrayList<String>();
	
	String fileName = null; //"src\\main\\resources\\ontology\\UMLSSemanticTypes.txt";
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public SemanticTypes(){
//		URL url = this.getClass().getClassLoader().getResource("ontology");
		fileName = "target\\classes\\ontology\\UMLSSemanticTypes.txt";
		if(!new File(fileName).exists())
			fileName = "classes\\ontology\\UMLSSemanticTypes.txt";
//		if (App.workbenchDeployed) {
//			fileName = "target\\classes\\ontology\\UMLSSemanticTypes.txt";
//		} else {
//			fileName = "src\\main\\resources\\ontology\\UMLSSemanticTypes.txt";
//		}
//
	};
	public SemanticTypes(String fileName){
		this.fileName = fileName;
	}
	public void read(){
		try{
			FileInputStream fis = new FileInputStream(fileName);
			 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		 
			String line = null;
			line = br.readLine();
			
			while ((line = br.readLine()) != null) {
				String[] items = line.split(",");
				String acry = items[0].trim();
				String name= items[1].trim();
				name = name.replaceAll("\"",  "");
				semanticTypes.add(name);
				semanticTypeMappings.put(name, acry);
				semanticTypeMappingsRev.put(acry,  name);
//				System.out.println(line);
			}
			Collections.sort(semanticTypes);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		   // do something
		}

	}
}
