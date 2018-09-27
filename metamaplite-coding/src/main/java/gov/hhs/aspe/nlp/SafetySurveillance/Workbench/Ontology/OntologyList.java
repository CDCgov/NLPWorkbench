/**
Guangfan.Zhang
*/

/**
 * 
 */
package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Ontology;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Guangfan.Zhang
 *
 * Engility Corporation
 * Jun 14, 2017
 */

/**
 * 
 * @author Guangfan.Zhang
 *
 * Engility Corporation
 * Jun 27, 2017
 */


public class OntologyList {
	
	/**
	 * Acronym to ontology mapping
	 */
	public HashMap<String, Ontology> ontologyMapping = new HashMap<String, Ontology>();
	/**
	 * Ontology name to acronym mapping
	 */
	public HashMap<String, String> nameToAcronym = new HashMap<String, String> ();
	/**
	 * Acronym to Ontology mapping
	 */
	
	public HashMap<String, String> acronymToName = new HashMap<String, String> ();
	
	/**
	 * List of ontology names: such as Medical
	 */
	public ArrayList<String> name = new ArrayList<String>();
	/**
	 * List of ontology acronyms: such as MEDDRA 
	 */
	public ArrayList<String> acrononym = new ArrayList<String>();

}
