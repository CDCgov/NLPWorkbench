package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.Map.Entry;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bioc.BioCDocument;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;
import gov.hhs.fda.srs.annotation.vaers.ClinicalFeature;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;

public class MetaMap {
	String separator = File.separator;
//	public String metaMapRootDir = "c:" + separator + "Software" + separator + "public_mm_lite" + separator;
	public String metaMapRootDir = "";
	MetaMapLite metaMapLiteInst =null;
	public MetaMap() throws ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, IOException{
		this("");
	}
	public MetaMap(String metaMapRootDir) throws ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, IOException
	{
		Properties props = new Properties();
		if (metaMapRootDir=="") {
			File f = new File("MetaMapLiteAPI.Properties");
			InputStream fis = null;
//            try {
				fis = new FileInputStream( f );
				if (fis==null) {
					fis = getClass().getResourceAsStream("MetaMapLiteAPI.Properties");
				}
				props.load(fis);
				this.metaMapRootDir = props.getProperty("MetaMapLiteDirectory");
				fis.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} else {
			this.metaMapRootDir = metaMapRootDir;
		}
		Properties myProperties = new Properties();
		MetaMapLite.expandModelsDir(myProperties,
		               this.metaMapRootDir + "data/models");
		MetaMapLite.expandIndexDir(myProperties,
				this.metaMapRootDir + "data/ivf/2017AA/USAbase/strict");
		myProperties.setProperty("metamaplite.excluded.termsfile",
				this.metaMapRootDir + "data/specialterms.txt");
		myProperties.setProperty("metamaplite.enable.postagging", "true");
		myProperties.setProperty("metamaplite.segmentation.method", "SENTENCES");
		myProperties.setProperty("metamaplite.sourceset", "all");
		myProperties.setProperty("metamaplite.semanticgroup", "all");
		   
//		Loading properties file in :
//			try {
				metaMapLiteInst = new MetaMapLite(myProperties);
//			} catch (IOException | ClassNotFoundException | InstantiationException | NoSuchMethodException | IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Creating a metamap lite instance:
	}
	
	public HashMap<String, ArrayList<Term>> processText(String rawText, 
			ArrayList<String> selectedOntologies,
			ArrayList<String> selectedUMLS, int range) throws IllegalAccessException, InvocationTargetException, IOException, Exception{
		HashMap<String, ArrayList<Term>> termMapping = new HashMap<String, ArrayList<Term>> ();
		
		List<Entity> entityList = exec(rawText);

		for (Entity entity: entityList) {
//			System.out.println(">>>" + entity.getMatchedText() + "<<<" );
			
//			List<Ev> listEV= entity.getEvList();
			for (Ev ev: entity.getEvSet()) {
				
//				System.out.println("Concept string: " + ev.getConceptInfo().getConceptString() +  
//				"; Concept preferred name: " + ev.getConceptInfo().getPreferredName()); 
//				+ "; Concept class: " + ev.getConceptInfo().getClass()); 
//				if(ev.getConceptInfo().getConceptString().toLowerCase().contains("guillain"))
//				{
//					System.out.println(entity.getLocationPosition());
//				}
				Set<String> semanticSet = ev.getConceptInfo().getSemanticTypeSet();
//				System.out.println("Concept semantic type set: " + semanticSet
//				 + ";Concept source set: " + ev.getConceptInfo().getSourceSet()); 
//				for(String st:semanticSet)
//				{
//					System.out.println("Concept semantic type set: " + st);
//				}

//				System.out.print("Concept: " + ev.getConceptInfo().getCUI() +  
//						";Field ID: " + entity.getFieldId() + 
//						";ID: " + entity.getId() + 
//						";Lexical Category: " + entity.getLexicalCategory() + 
//						";Location Position: " + entity.getLocationPosition() + ";Score: " + entity.getScore() + ";Start: " + entity.getStart()); 
//						";Type: " + entity.getType());
//				+ ";Class:" + entity.getClass());
//				System.out.println();
				
				for(String semanticType:semanticSet)
				{
					Term term = new Term();
					term.fill(rawText, ev.getConceptString(),  entity.getStart()+1, ev.getConceptInfo().getPreferredName(), semanticType, "MetaMap" );
					String key = ev.getConceptString() + "-" + term.ontology + "-" + term.from;
					ArrayList<Term> terms = null;
					if(termMapping.containsKey(key))
					{
						terms = termMapping.get(key);
					}
					else
					{
						terms = new ArrayList<Term>();
					}
					terms.add(term);
					termMapping.put(key,  terms);
				}
			}
		}
		return termMapping;
	}
	public List<Entity> exec(String txt) throws IllegalAccessException, InvocationTargetException, IOException, Exception{
		List<Entity> entityList = null;
		
		BioCDocument document = FreeText.instantiateBioCDocument(txt);
//		BioCDocument document = FreeText.instantiateBioCDocument("Pt developed generalized pruritus for 3 weeks and papular rash in intercostal areas "
//				+ "followed by intermittent achy pin prick dysesthesias and tender lymph nodes that exacerbated in 2/00.");
		document.setID("1");
		List<BioCDocument> documentList = new ArrayList<BioCDocument>();
		documentList.add(document);
//		List<BioCDocument> documentList = FreeText.loadFreeTextFile("test.txt");
		entityList = metaMapLiteInst.processDocumentList(documentList);
		for(Entity entity: entityList) {
//			System.out.println(entity.getMatchedText());
//			List<Ev> listEV= entity.getEvList();
//			for (Ev ev: entity.getEvSet()) {
//				System.out.println("Concept string: " + ev.getConceptInfo().getConceptString()); 
//				System.out.println("Concept preferred name: " + ev.getConceptInfo().getPreferredName()); 
//				System.out.println("Concept class: " + ev.getConceptInfo().getClass()); 
//				Set<String> semanticSet = ev.getConceptInfo().getSemanticTypeSet();
//				for(String st:semanticSet)
//				{
//					System.out.println("Concept semantic type set: " + st);
//				}
//				System.out.println("Concept source set: " + ev.getConceptInfo().getSourceSet()); 
//
//				System.out.print("Concept: " + ev.getConceptInfo().getCUI() + "\nMatched Text: " + entity.getMatchedText() + 
//						"\nField ID: " + entity.getFieldId() + "\nID: " + entity.getId() + "\nLexical Category: " + entity.getLexicalCategory() + 
//						"\nLocation Position: " + entity.getLocationPosition() + "\nScore: " + entity.getScore() + "\nStart: " + entity.getStart() + 
//						"\nType: " + entity.getType() + "\nClass:" + entity.getClass());
//				System.out.println();
//			}
		}
		return entityList; 
	}
	
    public void printResult(HashMap<String, ArrayList<Term>> codedResults) {
		for (Entry<String, ArrayList<Term>> entry : codedResults.entrySet()) {
			String word = entry.getKey();
			ArrayList<Term> terms = entry.getValue();

//			System.out.println("\n");

			for (Term term : terms) {
				int from = term.from;
				int to = term.to;
				String cls = term.strCls;
				String type = term.strType;
				String context = term.context;
				String ontology = term.ontology;
//				System.out.println(word + ": " + word + "; from " + from + " to " + to);
//				System.out.println(context);
//				System.out.println("Ontology: " + ontology + "; " + "Type = " + type + "; Class = " + cls + "\n");
			}
		}
	}


	public CAS writeVaersResultStrIntoCas(CAS cas, String resultXMLStr) throws IOException, InvalidXMLException, ResourceInitializationException, ParserConfigurationException, SAXException, CASException {
		CAS modifiedCas = cas;
		if (modifiedCas == null) {
			URL url = this.getClass().getClassLoader().getResource("vaers.xml");
			if (url == null) {
				throw new IOException("Could not find type system");
			} 
				XMLInputSource inputSource = new XMLInputSource(url);
				XMLParser parser = UIMAFramework.getXMLParser();
				TypeSystemDescription tsd = parser.parseTypeSystemDescription(inputSource);
				modifiedCas = CasCreationUtils.createCas(tsd, null, null, null);
		} 

		JCas jcas = modifiedCas.getJCas();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(resultXMLStr));
		Document doc = builder.parse(is);
		
		NodeList nodes = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			String nodeName = n.getNodeName();
			if (nodeName != null) {
				if (nodeName.contains("vaers")) {
//					System.out.println("the NodeName contains vaers");
					NamedNodeMap nnp1 = n.getAttributes();
					int begin = Integer.parseInt(nnp1.getNamedItem("begin").getNodeValue());
					int end = Integer.parseInt(nnp1.getNamedItem("end").getNodeValue());
					// notably, 'text' is not stored in cTAKES event elements.
					String text = nnp1.getNamedItem("text").getNodeValue();
					String preferred_term = nnp1.getNamedItem("preferred_term").getNodeValue();

					ClinicalFeature cf = new ClinicalFeature(jcas);
					cf.setBegin(begin);
					cf.setEnd(end);
					cf.setText(text);
					cf.setPreferred_term(preferred_term);
					jcas.addFsToIndexes(cf);
				}
			}
		}

		return jcas.getCas();
	}
	
    /*
    
	public static void main(String[] args) throws InvocationTargetException, Exception{

		MetaMap metaMap = new MetaMap("C:/Software/public_mm_lite/");
		
//		try {
//			String rawText= "Pt developed generalized pruritus for 3 weeks, papular rash in intercostal areas, followed by intermittent achy, pin prick dysesthesias and tender lymph nodes that exacerbated in 2/00. "
//					+ "Papular erythema, pain in both hands, chronic illness. Annual follow - up states the pt occassionally experiences papulo-pustular lesions and also a papular rash. A follow up report received 6/12/2000 "
//					+ "adds: Pt reports info given in box 18 was in error. The generalized pruritis occurred on 11/14/1999 and lasted approx 3 weeks. He was given Varivax \"as a precaution\""
//							+ "after exposure to a pt with sore throat and \"weird type\" lymph nodes who later developed chicken pox-like rash. \"There was no time to do a titer\" so Varivax given to this physician. "
//									+ "A \"few days\" after the Varivax both the pt and his wife, an ICU nurse, developed \"weird rashes\". The rash abated, but in February he developed pain in left arm and 2 weeks later itching. "
//											+ "A \"lymphatic cord\" was noted in left arm and mild \"chickenpox lesions\". In late February he developed a rash on his hands and peeling goes to the present. "
//													+ "Intermittent appearance of papular rash on his back and aches and pains in medial aspect of arms (lsp left) and thighs. Aches and pains temporarily relieved with aspirin. "
//													+ "Also has tried acyclovir x 2 which \"helped rash temporarily\". The \"cord\" in left arm was diagnosed as ulnar entrapment neuropathy\" and is \"probably coincidental\"."  
//													+ "Pt consulted with a doctor at CDC who stated there some similar reports. He also spoke with Merck who offered to biopsy lesions, but none are vesicular at present."  
//													+ "Pt will fax and mail a complete progress note and lab test results currently available.";
			
//			String rawText = "Within 2 weeks, post vax, noticed pt not responsive to his name anymore. Lost all eye contact and seemed in a \"fog\", started excessive toe walking. "
//					+ "Lost all acquired words, started holding stomach and continually crying. Looked like he was in pain. Chronic diarrhea started. Autism was dx'd after many series "
//					+ "of tests. History of present liiness: Pt is a 23 month old little boy who s being seen for his initial evaluation through the clinic because of concerns about "
//					+ "development. Parents report that they felt that pt's development was okay up until he was apprx. 15 months old. They report that at 14 to 15 months, pt was saying "
//					+ "a few words such as, mama, dada, ball, and oh oh. He was interactive and would play patty cake. At about 15 months of age, they noticed that he seemed to "
//					+ "lose words, stopped talking, and did not babble as much. He became less responsive to his name and would not come when called. He started toe walking and jumping "
//					+ "alot. His eye contact decreased and he seemed less socially interactive. He was no longer very interested in people. He also became less interested in toys. "
//					+ "Parents noticed that he was doing more blank staring. He quit imitating things. This past February, pt was seen by MD for a neurology evaluation. He had an "
//					+ "abnormal EEG, although apparently it did not suggeste a clear cut seizure disorder. MD suggested, but parents opted not to start pt on medication right away. "
//					+ "They report that pt is scheduled to have a repeat EEG next month. Pt also has a history of chronic diarrhea. MD ordered in abdominal x-ray and diagnosed "
//					+ "constipation. He explained to parents that he felt the loose stool was actually leaking around compacted stool. He suggested they try giving pt Milk of Magnesia,"
//					+ "mineral oil, or enemas. Parents report that pt has a long history of having watery stools. They report that the last few days, pt'a stools have been more formed, "
//					+ "although they are not sure why. The parents have also pursued other treatments for pt. They had lab work done, which apparently indicated a number of abn";
//
			
			String rawText = "Vaccine given to left arm per client on 11/2/15. She began having itching on 11/3 to left arm and chest. Developed rash on 11/8 to (L) arm, (L) breast and chest, 2-3 spots to center of back. Most spots on breast. Itching/burning worse today. No blisters.";
			HashMap<String, ArrayList<Term>> termList = metaMap.processText(rawText, null, null, 0);
			System.out.println(termList.size());
//			List<Entity> entityList = metaMap.exec("atrial fibrillation");
//
//			for(Entity entity: entityList) {
////				System.out.println(entity.getMatchedText());
//				List<Ev> listEV= entity.getEvList();
//				for (Ev ev: entity.getEvSet()) {
//					System.out.println("Concept string: " + ev.getConceptInfo().getConceptString()); 
//					System.out.println("Concept preferred name: " + ev.getConceptInfo().getPreferredName()); 
//					System.out.println("Concept class: " + ev.getConceptInfo().getClass()); 
//					Set<String> semanticSet = ev.getConceptInfo().getSemanticTypeSet();
//					for(String st:semanticSet)
//					{
//						System.out.println("Concept semantic type set: " + st);
//					}
//					System.out.println("Concept source set: " + ev.getConceptInfo().getSourceSet()); 
//					
//					System.out.print("Concept: " + ev.getConceptInfo().getCUI() + "\nMatched Text: " + entity.getMatchedText() + 
//							"\nField ID: " + entity.getFieldId() + "\nID: " + entity.getId() + "\nLexical Category: " + entity.getLexicalCategory() + 
//							"\nLocation Position: " + entity.getLocationPosition() + "\nScore: " + entity.getScore() + "\nStart: " + entity.getStart() + 
//							"\nType: " + entity.getType() + "\nClass:" + entity.getClass());
//					System.out.println();
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


	}
	*/
}
//mvn install:install-file -Dfile=target/metamaplite-3.5.jar -DgroupId=gov.nih.nlm.nls -DartifactId=metamaplite -Dversion=3.5 -Dpackaging=jar
//mvn install:install-file -Dfile=lib/context-2012.jar -DgroupId=context -DartifactId=context -Dversion=2012 -Dpackaging=jar
//mvn install:install-file -Dfile=lib/bioc-1.0.1.jar -DgroupId=bioc -DartifactId=bioc -Dversion=1.0.1 -Dpackaging=jar
//mvn install:install-file -Dfile=lib/nlp-2.4.C.jar -DgroupId=gov.nih.nlm.nls -DartifactId=nlp -Dversion=2.4.C -Dpackaging=jar


//java -cp target/metamaplite-3.5.jar gov.nih.nlm.nls.ner.MetaMapLite test.txt 
//--indexdir=<ivfdir> <
