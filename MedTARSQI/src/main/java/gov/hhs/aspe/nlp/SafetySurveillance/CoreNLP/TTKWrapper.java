package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.parsers.ParserConfigurationException;

import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Temporal.MedTARSQIResultFileReader;

/**
 * This class wraps the initialization of MedTARSQI, and the file manipulations.
 * 
 * @author Wei.Chen & Geoffrey Zhang
 *
 */
public class TTKWrapper {

	private static final Logger logger = LoggerFactory.getLogger(TTKWrapper.class);

	// a static ID to be associated with the temporary files
	static AtomicLong ID = new AtomicLong();

	private String id;
	public TTKWrapper() {
		this.id = Long.toString(ID.incrementAndGet());
	}

	/**
	 * This class accepts a raw text (e.g., a clinical note) as input, and
	 * generates a String output that contains the results from MedTARSQI
	 * without file operation.
	 * 
	 * @param rawText
	 * @return
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public String extract(String rawText) throws IOException, InterruptedException {

		String root = System.getenv("TTK_ROOT");
		if (root == null) {
			throw new IOException("TTK_ROOT not set.");
		}
		File home = new File(root);
		if (!home.exists()) {
			throw new FileNotFoundException("TTK_ROOT directory does not exist.");
		}

		File directory = new File("/tmp/medtarsqi");
		if (!directory.exists()) {
			logger.debug("Creating temporary directory.");
			directory.mkdirs();
		}

		String xmlInput = "<?xml version=\"1.0\"?><xml><TEXT>" + rawText + "</TEXT></xml>";
		File xmlInputFile = new File(directory, String.format("input_%s.txt", id));
		generateMedTARSQIInputFile(xmlInput, xmlInputFile);

		File xmlOutputFile = new File(directory, String.format("output_%s.txt", id));
		xmlOutputFile.delete();
//		String xmlOutputFile = new String("MedTARSQI_output" + new Long(this.id).toString() + ".xml");
//		Path outputXMLPath = Paths.get(xmlOutputFile);
//		Files.deleteIfExists(outputXMLPath);

//		URL url = this.getClass().getResource("/ttk/tarsqi.py");
		String ttk = root + "/tarsqi.py";
		if (! new File(ttk).exists()) {
			throw new IOException(" not found.");
		}

		File mallet = new File(home, "build/mallet-2.0.8");
		File treetagger = new File(home, "build/TreeTagger");
		if (!mallet.exists()) {
			throw new IOException("Mallet not found.");
		}
		if (!treetagger.exists()) {
			throw new IOException("Treetagger not found");
		}
		logger.trace("Command {}", ttk);
		logger.trace("Input: {}", xmlInputFile.getPath());
		logger.trace("Output: {}", xmlOutputFile.getPath());
		logger.trace("Root: {}", System.getenv("TTK_ROOT"));
		String[] args = {
			"python",
			ttk,
			"--treetagger", treetagger.getPath(),
			"--mallet", mallet.getPath(),
			xmlInputFile.getPath(),
			xmlOutputFile.getPath()
		};
//		ProcessBuilder pb = new ProcessBuilder("python", ttk, xmlInputFile.getPath(), xmlOutputFile.getPath());
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Process p = pb.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null)
//		    System.out.println("tasklist: " + line);
		
		p.waitFor();

//		try
//		{
			if (!p.waitFor(30, TimeUnit.SECONDS))
			{
				p.destroyForcibly();
				logger.error("Process hung");
				throw new IOException("Processing did not terminate.");
			}
			return new String(Files.readAllBytes(xmlOutputFile.toPath()));
//		}
		/*
		finally
		{
			if (xmlOutputFile.exists()) {
				if (!xmlOutputFile.delete()) {
					xmlOutputFile.deleteOnExit();
				}
			}
			if (xmlInputFile.exists()) {
				if (!xmlInputFile.delete()) {
					xmlInputFile.deleteOnExit();
				}
			}
		}
		*/
	}

	/**
	 * This function takes the raw text from xmlInput and generate a temporary
	 * file specified by another String input of xmlInputFile.
	 * 
	 * @param xmlInput
	 * @param xmlInputFile
	 * @throws FileNotFoundException
	 */
	public void generateMedTARSQIInputFile(String xmlInput, File xmlInputFile) throws FileNotFoundException {
		xmlInputFile.delete();
		PrintWriter fileOut = null;
		fileOut = new PrintWriter(xmlInputFile);
		fileOut.println(xmlInput);
		fileOut.flush();
		fileOut.close();
	}

	/**
	 * This function return a String result. 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

	/**
	 * This function takes an input file - the first parameter - containing
	 * clinical text and embed the textual content in an XML file as specified
	 * in the 2nd parameter.
	 * 
	 * @param inputFile
	 * @param inputXMLFile
	 */
	public void generateXMLContentForInputFile(String inputFile, String inputXMLFile) throws IOException {
//		try {
			Path pathTmp = Paths.get(inputFile);
			String result = new String(Files.readAllBytes(pathTmp));
			String xmlResult = "<?xml version=\"1.0\"?><xml><TEXT>" + result + "</TEXT></xml>";
			// generate/replace the xml file
			PrintWriter fileOut = new PrintWriter(inputXMLFile);
			fileOut.println(xmlResult);
			fileOut.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
