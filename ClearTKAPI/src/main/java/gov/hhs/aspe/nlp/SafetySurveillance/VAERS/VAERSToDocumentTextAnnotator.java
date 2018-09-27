package gov.hhs.aspe.nlp.SafetySurveillance.VAERS;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.component.ViewCreatorAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.util.ViewUriUtil;
import org.jdom2.JDOMException;

public class VAERSToDocumentTextAnnotator extends JCasAnnotator_ImplBase  {
	public static final String PARAM_FILE_DIRECTORY = "fileDir";
	private String fileDir;
	public static final String PARAM_DATA = "strData";
	private String strData = "";
	private static Logger log = LogManager.getLogger(VAERSToDocumentTextAnnotator.class);
//	Logger log = Logger.getLogger(VAERSToDocumentTextAnnotator.class);
	
	  public void initialize(UimaContext context) throws ResourceInitializationException {
		    super.initialize(context);

		    fileDir= (String) context.getConfigParameterValue(
		    		PARAM_FILE_DIRECTORY);
		    strData = (String) context.getConfigParameterValue(
		    		PARAM_DATA);
//			System.out.println("Start to read vaers data: " + fileDir);

	  }
	public static AnalysisEngineDescription getDescription() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(VAERSToDocumentTextAnnotator.class);
	}

	/**
	 * This description will read the contents into the specify view. If the view does not exist, it
	 * will make it as needed.
	 */
	public static AnalysisEngineDescription getDescriptionForView(String targetViewName)
			throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(
				ViewCreatorAnnotator.class,
				ViewCreatorAnnotator.PARAM_VIEW_NAME,
				targetViewName));
		builder.add(VAERSToDocumentTextAnnotator.getDescription(), CAS.NAME_DEFAULT_SOFA, targetViewName);
		log.debug("Start to read vaers data");
		
		return builder.createAggregateDescription();
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		
		VAERSParser parser = null;
		if(strData!=null )
			if(!strData.equals(""))
			{
				// Handle report data input directly

				aJCas.setSofaDataString(strData, "text/plain");

				return;
			}

		if(aJCas.getDocumentText()==null)
		{
			URI uri = ViewUriUtil.getURI(aJCas);
			String fileName = uri.getPath();
			if(fileName.startsWith("/"))
				fileName = fileName.substring(1);

			try {
				if(new File(fileName).isDirectory())
					return;
				parser = new VAERSParser(fileName);
				VAERSDataStructure vaersData = parser.getVaersData();
				String text = vaersData.getRawText();
				aJCas.setSofaDataString(text, "text/plain");
				
				log.debug(fileName + " has been read" );
			} catch (IOException | JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else{
			try {
				parser = new VAERSParser(fileDir);
				VAERSDataStructure vaersData = parser.getVaersData();
				String text = vaersData.getRawText();
				aJCas.setSofaDataString(text, "text/plain");
				log.debug("VAERS data has been read." );
				
			} catch (IOException | JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
