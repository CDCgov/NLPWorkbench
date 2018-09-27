package gov.hhs.fda.srs.annotation.vaers;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

public class VaersUtilities {

	 public static CAS getCasFromXMI(String filenameXMI, String filenameDescriptorXML)
	 {
		 CAS cas = null;
		 
		 try {
			
			 // first, create a CAS structure based on the UIMA Component Descriptor (the feature type hierarchy definition, etc.)
			XMLInputSource in = new XMLInputSource(new File(filenameDescriptorXML));
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
			cas = ae.newCAS(); //with structure, but the content is empty at this time.
			
//			TypeSystem ts1 = cas.getTypeSystem();
//			XmiCasDeserializer xmiDes = new XmiCasDeserializer(ts1);
			File fFile = new File(filenameXMI);
			XMLInputSource xmlInputSource1 = new XMLInputSource(fFile);
			XmiCasDeserializer.deserialize(xmlInputSource1.getInputStream(), cas);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 
		 return cas;
	 }
}
