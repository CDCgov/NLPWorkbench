package gov.hhs.aspe.nlp.SafetySurveillance.CNER;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class MLModelParameters {
	public String propertyFile = "MLModel.Properties";
	public String modelDir = "data/model/crf";
	public String serMethod = "CRF";
	public int sequenceModel = 1;
	public int contextWindow = 0;
	public String featureExtractor = "ClinicalNamedEntityChunker";
	public int featuresetIDFrom = 5, featuresetIDTo = 5, featuresetID = 5;
	public String MLTaskClassName;
	public boolean medOnly = false;

	public MLModelParameters(){};
	
	public MLModelParameters(String fileName){
		this.propertyFile = fileName;
	}
	
	public void loadParameters(String fileName){
	      Properties props = new Properties();
	        InputStream is = null;

	        // First try loading from the current directory
	        try {
	            File f = new File(fileName);
	            is = new FileInputStream( f );
	        }
	        catch ( Exception e ) { is = null; }

	        try {
	            if ( is == null ) {
	                // Try loading from classpath
	                is = getClass().getResourceAsStream("Clew.properties");
	            }

	            // Try loading properties from the file (if found)
	            props.load( is );
	        }
	        catch ( Exception e ) { }
	        
	        modelDir = props.getProperty("ModelDirectory");
            serMethod = props.getProperty("SERMethod");
            featuresetID = new Integer(props.getProperty("FeatureSetID"));
//            featuresetIDFrom = new Integer(props.getProperty("FeatureIDScanfrom"));
//            featuresetIDTo= new Integer(props.getProperty("FeatureIDScanto"));
            contextWindow= new Integer(props.getProperty("ContextWindowSize"));
            String strChunking = props.getProperty("SequenceModeling");
            featureExtractor = props.getProperty("FeatureExtractorClassName");
            MLTaskClassName = props.getProperty("MachineLearningTaskClassName");
            if(strChunking.toLowerCase().trim().equals("false"))
            {
                sequenceModel = 0;
            }
            else
            	sequenceModel = 1;

            	props.getProperty("Testing-Method");
            	props.setProperty("Sequence-Modeling", "False");
	        
	}
	public void saveParamChanges() {
        try {
            Properties props = new Properties();
            
            props.setProperty("ModelDirectory", modelDir);
            props.setProperty("SERMethod", serMethod);
            props.setProperty("FeatureIDScanfrom", "" + featuresetIDFrom);
            props.setProperty("FeatureIDScanto", "" + featuresetIDFrom);
            props.setProperty("ContextWindowSize", "" + contextWindow);
            props.setProperty("FeatureExtractorClassName", featureExtractor );
            props.setProperty("MachineLearningTaskClassName", MLTaskClassName );
            if(sequenceModel  == 1)
            	props.setProperty("SequenceModeling", "True");
            else
            	props.setProperty("SequenceModeling", "False");
            
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
		String fileName = "MLModel.properties";
		MLModelParameters clewParam = new MLModelParameters(fileName);
		clewParam.saveParamChanges();
	}
}
