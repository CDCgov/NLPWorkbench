package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.ClewParameters;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.ReadClewParameters;
//import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.App;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities.GeneralUtility;

/**
 * This class modularizes ETHER into three functions, including Clinical Feature Extraction, Temporal Feature Extraction,
 * and Association Relationship Extraction. These functions can be wrapped as Web services.
 * 
 * @author Wei.Chen
 *
 */
public class ETHERModule {

	public String[] args = null;
	
	public static ClewParameters properties = null;

	String ETHERCmd = new String("ETHERNLP.exe");
	
	String inputName = new String("input");
	String inputSuffix = new String(".txt");
	String inputFilename;
	
	String outputClinicalName = new String("outputClinical");
	public String getETHERDirectory() {
		return ETHERDirectory;
	}
	public void setETHERDirectory(String eTHERDirectory) {
		ETHERDirectory = eTHERDirectory;
	}

	String outputTemporalName = new String("outputTemporal");
	String outputRelationName = new String("outputRelation");
	
	String clinicalFile = ".SER";
	String temporalFile = ".TER";
	String relationFile = ".REL";
	
	String outputSuffix = new String(".xml");
	String absPath;
	
	public String outputClinicalFilename, outputTemporalFilename, outputRelationFilename;
	
	String workingDirectory;
	String ETHERDirectory;
	File etherNLPFile;
		
	String fileID; 
	
	ProcessBuilder pbClinical = null;
	ProcessBuilder pbTemporal = null;
	ProcessBuilder pbRelation = null;

	Boolean debugWithouCleanup = true; 

	public ETHERModule(String ID) {

		ETHERModule.properties = ReadClewParameters.Read(this.args);
		
		fileID = ID;
		workingDirectory = System.getProperty("user.dir");
		
		URL url = this.getClass().getClassLoader().getResource("lib");
		etherNLPFile = new File(url.getPath() + "/ETHERNLP.exe");
		absPath = etherNLPFile.getAbsolutePath();
		
		ETHERDirectory = absPath.substring(0,absPath.lastIndexOf(File.separator));
		
		inputFilename = new String(ETHERDirectory + "\\"+ inputName + fileID + inputSuffix);
		outputClinicalFilename = new String(ETHERDirectory + "\\"+ outputClinicalName + fileID + outputSuffix);
		outputTemporalFilename = new String(ETHERDirectory + "\\"+ outputTemporalName +fileID + outputSuffix);
		outputRelationFilename = new String(ETHERDirectory + "\\" +outputRelationName + fileID + outputSuffix);
	}
	

	public ETHERModule() {
		this.args = null; 
		
		ETHERModule.properties = ReadClewParameters.Read(this.args);
		
		Random rand = new Random();
		Integer numID = new Integer(rand.nextInt(1000)+1);
		fileID = numID.toString(); 
		workingDirectory = System.getProperty("user.dir");
		URL res = this.getClass().getClassLoader().getResource("ETHERNLP/ETHERNLP.exe");
//		System.out.println(res);
		if(res == null)
			etherNLPFile = new File(ETHERModule.properties.ETHERNLPDir + "\\ETHERNLP.exe");
		else
			etherNLPFile = new File(res.getFile());
		
		if(!etherNLPFile.exists())
		{
			System.out.println("Cannot find " + res);
			etherNLPFile = new File("target\\classes\\ETHERNLP\\ETHERNLP.exe");
			System.out.println("Try " + "target\\classes\\ETHERNLP\\ETHERNLP.exe");
		}
		if(!etherNLPFile.exists())
		{
			System.out.println("Cannot find " + etherNLPFile);
			etherNLPFile = new File("classes\\ETHERNLP\\ETHERNLP.exe");
			System.out.println("Try " + "classes\\ETHERNLP\\ETHERNLP.exe");
		}
		if(!etherNLPFile.exists())
		{
			System.out.println("ETHERNLP.exe is not found!");
			return;
		}
		absPath = etherNLPFile.getAbsolutePath();
		ETHERDirectory = absPath.substring(0,absPath.lastIndexOf(File.separator));

		inputFilename = new String(ETHERDirectory + "\\"+ inputName + fileID + inputSuffix);
		outputClinicalFilename = new String(ETHERDirectory + "\\"+ outputClinicalName + fileID + outputSuffix);
		outputTemporalFilename = new String(ETHERDirectory + "\\"+ outputTemporalName +fileID + outputSuffix);
		outputRelationFilename = new String(ETHERDirectory + "\\" +outputRelationName + fileID + outputSuffix);

	}
	
	public ETHERModule(String[] args) {
		this.args = args; 
		
		ETHERModule.properties = ReadClewParameters.Read(this.args);
		
		Random rand = new Random();
		Integer numID = new Integer(rand.nextInt(1000)+1);
		fileID = numID.toString(); 
		workingDirectory = System.getProperty("user.dir");
		URL res = this.getClass().getClassLoader().getResource("ETHERNLP/ETHERNLP.exe");
//		System.out.println(res);
		if(res == null)
			etherNLPFile = new File(ETHERModule.properties.ETHERNLPDir + "\\ETHERNLP.exe");
		else
			etherNLPFile = new File(res.getFile());
		
		if(!etherNLPFile.exists())
		{
			System.out.println("Cannot find " + res);
			etherNLPFile = new File("target\\classes\\ETHERNLP\\ETHERNLP.exe");
			System.out.println("Try " + "target\\classes\\ETHERNLP\\ETHERNLP.exe");
		}
		if(!etherNLPFile.exists())
		{
			System.out.println("Cannot find " + etherNLPFile);
			etherNLPFile = new File("classes\\ETHERNLP\\ETHERNLP.exe");
			System.out.println("Try " + "classes\\ETHERNLP\\ETHERNLP.exe");
		}
		if(!etherNLPFile.exists())
		{
			System.out.println("ETHERNLP.exe is not found!");
			return;
		}
		absPath = etherNLPFile.getAbsolutePath();
		ETHERDirectory = absPath.substring(0,absPath.lastIndexOf(File.separator));

		inputFilename = new String(ETHERDirectory + "\\"+ inputName + fileID + inputSuffix);
		outputClinicalFilename = new String(ETHERDirectory + "\\"+ outputClinicalName + fileID + outputSuffix);
		outputTemporalFilename = new String(ETHERDirectory + "\\"+ outputTemporalName +fileID + outputSuffix);
		outputRelationFilename = new String(ETHERDirectory + "\\" +outputRelationName + fileID + outputSuffix);
//		System.out.println("Clinical ETHER output: " + outputClinicalFilename);
//		System.out.println("Temporal ETHER output: " + outputTemporalFilename);
//		System.out.println("Relation ETHER output: " + outputRelationFilename);

	}
	
	public void setETHERInputFile(String inputFileName, String outputDir){

		String inputFileNameNoPath = inputFileName.substring(inputFileName.lastIndexOf(File.separator)+1) ;
		
		inputFilename = outputDir + "\\"+ inputFileNameNoPath+ inputSuffix;
		GeneralUtility.CreateADir(outputDir);
		GeneralUtility.CreateADir(outputDir + "\\SER");
		outputClinicalFilename = new String(outputDir + "\\SER\\"+ inputFileNameNoPath+ clinicalFile +outputSuffix);
		outputTemporalFilename = new String(outputDir + "\\SER\\"+ inputFileNameNoPath +temporalFile +outputSuffix); //Treat temporal entity recognition as one type of SER's
		outputRelationFilename = new String(outputDir + "\\REL\\" +inputFileNameNoPath + relationFile +outputSuffix);
	}
	
	public void setETHERClinicalFile(String inputFileName, String outputDir){

		GeneralUtility.CreateADir(outputDir);
		GeneralUtility.CreateADir(outputDir + "\\SER");
		String inputFileNameNoPath = inputFileName.substring(inputFileName.lastIndexOf(File.separator)+1) ;
		outputClinicalFilename = new String(outputDir + "\\SER\\"+ inputFileNameNoPath+ clinicalFile +outputSuffix);
	}
	
	public void setETHERTemporalFile(String inputFileName, String outputDir){

		GeneralUtility.CreateADir(outputDir);
		GeneralUtility.CreateADir(outputDir + "\\SER");
		String inputFileNameNoPath = inputFileName.substring(inputFileName.lastIndexOf(File.separator)+1) ;
		outputTemporalFilename = new String(outputDir + "\\SER\\"+ inputFileNameNoPath+ temporalFile +outputSuffix);
	}
	public void setETHERAssociationFile(String inputFileName, String outputDir, String methodSER){

		GeneralUtility.CreateADir(outputDir);
		GeneralUtility.CreateADir(outputDir + "\\SER");
		GeneralUtility.CreateADir(outputDir + "\\REL");
		String inputFileNameNoPath = inputFileName.substring(inputFileName.lastIndexOf(File.separator)+1) ;
		outputRelationFilename = new String(outputDir + "\\REL\\"+ inputFileNameNoPath+ relationFile + "." + methodSER +outputSuffix);
	}
	
	
	public String getOutputClinicalFilename () {
		return this.outputClinicalFilename;
	}
		
	public String processETHERClinical(String input) {
		
		try {
			Files.write(Paths.get(inputFilename), input.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pbClinical = new ProcessBuilder(absPath, "-e", inputFilename, outputClinicalFilename);
		File f = pbClinical.directory();

		if (f!=null) {
		} else {
			pbClinical.directory(new File(ETHERDirectory));
		}

		String content = null;
		try {
			Process p = pbClinical.start(); // the 'clinical' process
			p.waitFor();
			
			return outputClinicalFilename;
//			content = new String(Files.readAllBytes(Paths.get(outputClinicalFilename)));
//			return content;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		Path p1 = null;
		Path p2 = null;
		try {
			p1 = FileSystems.getDefault().getPath(inputFilename);
		    Files.delete(p1);
		    p2 = FileSystems.getDefault().getPath(outputClinicalFilename);
		    Files.delete(p2);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p1);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p1);
		} catch (IOException x) {
		    System.err.println(x);
		}
		return content;
	}

	public String processETHERTemporal(String input) {
		
		try {
			Files.write(Paths.get(inputFilename), input.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			Files.deleteIfExists(Paths.get(outputTemporalFilename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		pbTemporal = new ProcessBuilder(absPath, "-t", inputFilename, outputTemporalFilename);
		File f = pbTemporal.directory();

		if (f!=null) {
		} else {
			pbTemporal.directory(new File(ETHERDirectory));
		}

		String content = null;
		try {
			Process p = pbTemporal.start(); // the 'clinical' process
			p.waitFor();
			
			return outputTemporalFilename;
//			content = new String(Files.readAllBytes(Paths.get(outputTemporalFilename)));
//			return content;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Path p1 = null;
		Path p2 = null;
		try {
			p1 = FileSystems.getDefault().getPath(inputFilename);
		    Files.delete(p1);
		    p2 = FileSystems.getDefault().getPath(outputTemporalFilename);
		    Files.delete(p2);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p1);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p1);
		} catch (IOException x) {
		    System.err.println(x);
		}
		
		return content;
	}
	
	public String processETHERRelation() {
		
		try {
			Files.deleteIfExists(Paths.get(outputRelationFilename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		pbRelation = new ProcessBuilder(absPath, "-r", outputClinicalFilename, outputTemporalFilename, outputRelationFilename);
		File f = pbRelation.directory();

		if (f!=null) {
		} else {
			pbRelation.directory(new File(ETHERDirectory));
		}

		String content = null;
		try {
			Process p = pbRelation.start(); // the 'relation' process
			p.waitFor();
			
//			content = new String(Files.readAllBytes(Paths.get(outputRelationFilename)));

			if (debugWithouCleanup) {
				// do nothing - NOT removing the temp XML files for debugging purpose.
			} else {
				Boolean cleanedUp = cleanUpFiles();
				if (cleanedUp)
					System.out.println("\n[all the temporary files have been removed for this query]");
				else
					System.out.println("\n[Some temporary files may not have been removed - need to check...]");
			}

			return outputRelationFilename;
//			return content;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * This function cleans up all the temporary files related to ETHER Clinical and ETHER Temporal/Relation.
	 * @return
	 */
	boolean cleanUpFiles() {
		Path p = null;
		try {
			p = FileSystems.getDefault().getPath(inputFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		try { 
			p=FileSystems.getDefault().getPath(outputClinicalFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		try {
			p=FileSystems.getDefault().getPath(outputTemporalFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		try {
			p=FileSystems.getDefault().getPath(outputRelationFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		    return false;
		}
		return true;
	}
	
	/**
	 * This function cleans up the temporary files related to ETHER Clinical service query alone.
	 * @return
	 */
	boolean cleanUpClinicalFiles() {
		Path p = null;
		try {
			p = FileSystems.getDefault().getPath(inputFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		try { 
			p=FileSystems.getDefault().getPath(outputClinicalFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		return true;
	}
	
	/**
	 * This function cleans up the temporary files related to ETHER Temporal service query alone.
	 * @return
	 */
	boolean cleanUpTemporalFiles() {
		Path p = null;
		try {
			p = FileSystems.getDefault().getPath(inputFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}
		
		try {
			p=FileSystems.getDefault().getPath(outputTemporalFilename);
		    Files.delete(p);
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", p);
		    return false;
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", p);
		    return false;
		} catch (IOException x) {
		    System.err.println(x);
		    return false;
		}


		return true;
	}
	
}
