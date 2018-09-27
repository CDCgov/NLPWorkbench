package gov.hhs.aspe.nlp.SafetySurveillance.Workbench;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class ClewParameters {
	public String propertyFile = "Clew.Properties";
	public String modelDir = "";
	public String metamapRootDir = "C:/Software/public_mm_lite2/";;
	public String ETHERNLPDir = "C:/ETHERNLP/";;
	public String NLTKDir = "C:/NLTK/";;
	public String NLTKDataDir = "C:/nltk_data/";;
	public String dataDir = "";
	public String testDataDir = "";
	public String serMethod = "";
	public String serMethodShort = "";
	public int chunkingMethod = 1;
	public int kFold = 2;
	public int contextWindow = 0;
	public int featuresetIDFrom = 0, featuresetIDTo = 0, featuresetID = 0;
	public boolean noGUI = false;
	public boolean cvTestingMode = false;
	public boolean computeLexiconFeatures = false;
	public boolean medOnly = false;
	public String medModelDir;
	public String lexiconFeatureDir = "";

	public ClewParameters(){};
	
	public ClewParameters(String fileName){
		this.propertyFile = fileName;
	}
	
	public void loadParameters(String fileName){
//		System.out.println(fileName);
	      Properties props = new Properties();
	        InputStream is = null;

	        try {
	            File f = new File(fileName);
	            is = new FileInputStream( f );
	        }
	        catch ( Exception e ) { is = null; }

	        try {
	            if ( is == null ) {
	                is = getClass().getResourceAsStream("Clew.properties");
	            }

	            props.load( is );
	        }
	        catch ( Exception e ) { }
	        Enumeration<?> propNames = props.propertyNames();
	        while(propNames.hasMoreElements())
	        {
	        	String name = (String)propNames.nextElement();
//	        	System.out.println(name);
//	        	System.out.println(props.getProperty(name));
	        }
//	        System.out.println(propNames);
	        if( props.size() > 0) 
	        {
	        	modelDir = props.getProperty("ModelDir");
	        	metamapRootDir = props.getProperty("MetaMapLiteDirectory");
	        	dataDir = props.getProperty("TrainingDataDirectory" );
	        	testDataDir = props.getProperty("TestingDataDirectory");
	        	serMethod = props.getProperty("SERMethod");
	        	kFold = new Integer(props.getProperty("kFold"));
	        	featuresetIDFrom = new Integer(props.getProperty("FeatureIDScanfrom"));
	        	featuresetIDTo = new Integer(props.getProperty("FeatureIDScanto"));
	        	System.out.println(props.getProperty("ContextWindowSize"));
	        	contextWindow= new Integer(props.getProperty("ContextWindowSize"));
	        	ETHERNLPDir = props.getProperty("ETHERNLPDirectory");

	        	String strNoGUI = props.getProperty("GUI");

	        	if(strNoGUI.toLowerCase().trim().equals("false"))
	        	{
	        		noGUI = true;
	        	}
	        	else
	        		noGUI = false;

	        	String strChunking = props.getProperty("SequenceModeling");

	        	if(strChunking.toLowerCase().trim().equals("false"))
	        	{
	        		chunkingMethod = 0;
	        	}
	        	else
	        		chunkingMethod = 1;

	        	props.getProperty("Testing-Method");
	        	props.setProperty("Sequence-Modeling", "False");


	        	String strCVTestingMethod = props.getProperty("CrossValidation");
	        	if(strCVTestingMethod.toLowerCase().equals("true"))
	        		cvTestingMode = true;
	        	else
	        		cvTestingMode = false;            	

	        	lexiconFeatureDir = props.getProperty("LexiconFeatureDirectory");
	        }
	        
	}
	public void saveParamChanges() {
        try {
            Properties props = new Properties();
            
            if(modelDir == null )
            	modelDir = "";
            props.setProperty("ModelDirectory", modelDir);
            props.setProperty("MetaMapLiteDirectory", metamapRootDir);
            props.setProperty("TrainingDataDirectory", dataDir);
            props.setProperty("TestingDataDirectory", testDataDir);
            props.setProperty("SERMethod", serMethod);
            props.setProperty("ETHERNLPDirectory", ETHERNLPDir);
            props.setProperty("kFold", "" + kFold);
            props.setProperty("FeatureIDScanfrom", "" + featuresetIDFrom);
            props.setProperty("FeatureIDScanto", "" + featuresetIDFrom);
            props.setProperty("ContextWindowSize", "" + contextWindow);
            
            if(noGUI)
            {
                props.setProperty("GUI", "False");
            }
            else
                props.setProperty("GUI", "True");
            
                		
            if(chunkingMethod  == 1)
            	props.setProperty("SequenceModeling", "True");
            else
            	props.setProperty("SequenceModeling", "False");
            
            if (cvTestingMode)
            	props.setProperty("CrossValidation", "True");
            else
            	props.setProperty("CrossValidation", "False");

        	props.setProperty("LexiconFeatureDirectory", lexiconFeatureDir);
            
            
            File f = new File(propertyFile);
            OutputStream out = new FileOutputStream( f );
            props.store(out, "Property values for CLEW");
            out.close();
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args){
		String fileName = "TestProperties.properties";
		ClewParameters clewParam = new ClewParameters(fileName);
		clewParam.saveParamChanges();
	}
}
