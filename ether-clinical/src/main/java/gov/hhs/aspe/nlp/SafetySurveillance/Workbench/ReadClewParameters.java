package gov.hhs.aspe.nlp.SafetySurveillance.Workbench;

import java.util.ArrayList;
import java.util.List;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;

public class ReadClewParameters {
	public static ClewParameters Read(String[] args)
	{
		List<Option> optsList = new ArrayList<Option>();
		ClewParameters properties = new ClewParameters();
		List<String> argsList = new ArrayList<String>();
		String propertyFile = "Clew.Properties";
		properties.propertyFile = propertyFile;
		boolean loadFromFile = false;
		
		if (args==null) {
			// do nothing
		} else
		if (!(args.length ==0)) {
		
		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
			case '-':
				if (args[i].length() < 2)
					throw new IllegalArgumentException("Not a valid argument: "+args[i]);
				if (args[i].charAt(1) == '-') {
					if (args[i].length() < 3)
						throw new IllegalArgumentException("Not a valid argument: "+args[i]);
					// --opt
					argsList.add(args[i].substring(2, args[i].length()));
				} else {
					if (args.length-1 == i)
						throw new IllegalArgumentException("Expected arg after: "+args[i]);
					// -opt
					if(args[i].substring(1).toLowerCase().equals("property"))
					{
						loadFromFile = true;
						propertyFile = args[i+1];
					}
					optsList.add(new Option(args[i].substring(1), args[i+1]));
					i++;
				}
				break;
			default:
				// arg
				argsList.add(args[i]);
				break;
			}
		}
		}
//		System.out.println("Read from clew property file..." + properties.propertyFile);
		
		properties.loadParameters(properties.propertyFile);
		if(argsList.contains("x"))
		{
			//cross validation
			properties.cvTestingMode = true;
		}
		else
			properties.cvTestingMode = false;

		if(argsList.contains("NoGUI") || argsList.contains("nogui")||argsList.contains("NOGUI") || argsList.contains("NoGui"))
		{
			properties.noGUI = true;
		}
//		else
//			properties.noGUI = false;

		if(optsList.size() > 0 )
		{
			for(Option option: optsList){
				switch(option.flag.toLowerCase())
				{
				case "model": //model directory
				{
					properties.modelDir = option.opt;
					properties.modelDir = 
							GeneralUtility.checkDirectoryName(properties.modelDir);
					break;
				}
				case "property": //property file
				{
					//					properties.propertyFile = option.opt;
					//					loadFromFile = true;
					break;
				}
				case "metamap": //model directory
				{
					properties.metamapRootDir= option.opt;
					properties.metamapRootDir = 
							GeneralUtility.checkDirectoryName(properties.metamapRootDir);
					break;
				}
				case "data":
				{
					properties.dataDir = option.opt;
					properties.dataDir = GeneralUtility.checkDirectoryName(properties.dataDir);
					break;
				}
				case "testdata":
				{
					properties.testDataDir = option.opt;
					properties.testDataDir = 
							GeneralUtility.checkDirectoryName(properties.testDataDir);
					break;
				}
				case "chunking":
				{
					properties.chunkingMethod= Integer.parseInt(option.opt);
					break;
				}
				case "ETHER":
				{
					properties.ETHERNLPDir= option.opt;
					break;
				}
				case "ser":
				{
					properties.serMethodShort = option.opt.trim();
					if(option.opt.toLowerCase().equals("svm"))
					{
						properties.serMethod = "org.cleartk.ml.libsvm.LibSvmStringOutcomeDataWriter";
					}
					else if(option.opt.toLowerCase().equals("svmboolean")){
						properties.serMethod = "org.cleartk.ml.libsvm.LibSvmBooleanOutcomeDataWriter";
					}
					else if(option.opt.toLowerCase().equals("crf")){
						properties.serMethod = "org.cleartk.ml.mallet.MalletCrfStringOutcomeDataWriter";
					}
					break;	
				}
				case "computelexiconfeatures":
				{
					if(option.opt.toLowerCase().equals("true"))
						properties.computeLexiconFeatures = true;
					else
						properties.computeLexiconFeatures = false;
					break;
				}
				case "kfold":
				{

					properties.kFold = Integer.parseInt(option.opt);
					break;	
				}
				case "featuresetfrom":
				{

					properties.featuresetIDFrom = Integer.parseInt(option.opt);
					break;	
				}
				case "featuresetto":
				{

					properties.featuresetIDTo = Integer.parseInt(option.opt);
					break;	
				}
				case "medonly":
				{

					if(option.opt.toLowerCase().equals("true"))
						properties.medOnly= true;
					else
						properties.medOnly = false;
					break;	
				}
				case "gui":
				{

					if(option.opt.toLowerCase().equals("true"))
						properties.noGUI= false;
					else
						properties.noGUI= true;
					break;	
				}
				case "medmodeldir":
				{

					properties.medModelDir= option.opt;
					break;	
				}
				case "lexiconfeaturedir":
				{

					properties.lexiconFeatureDir= option.opt;
					break;	
				}
				case "contextwindow":
				{

					properties.contextWindow= Integer.parseInt(option.opt);
					break;	
				}
				case "ethernlpdirectory":
				{

					properties.ETHERNLPDir = args[1];
					break;	
				}				}
			}
		}
		properties.saveParamChanges();
		return properties;
	}
}
