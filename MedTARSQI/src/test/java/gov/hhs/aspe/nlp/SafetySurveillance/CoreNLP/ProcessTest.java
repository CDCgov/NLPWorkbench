package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 *
 */
@Ignore
public class ProcessTest
{
	String root = "/Users/suderman/Projects/cdc/MedTARSQI/src/main/resources/ttk";

	public ProcessTest()
	{

	}

	@Test
	public void tarsqi() throws IOException, InterruptedException
	{
		String ttk = root + "/tarsqi.py";
		String input = "/tmp/medtarsqi/Input_1.txt";
		String output ="/tmp/Output.txt";

		File out = new File(output);
		if (out.exists()) {
			out.delete();
		}
		String[] args = {
				"python",
				ttk,
				"--treetagger", root + "/build/treetagger",
				"--mallet", root + "/build/mallet-2.0.8",
				input,
				output
		};

//		ProcessBuilder builder = new ProcessBuilder("python", ttk, input, output);
		ProcessBuilder builder = new ProcessBuilder(args);
		Process process = builder.redirectErrorStream(true).start();
		process.waitFor(5, TimeUnit.SECONDS);

		File file = new File(output);
		assertTrue(file.exists());
		System.out.println(new String(Files.readAllBytes(file.toPath())));
	}

	@Test
	public void ttkWrapper() throws IOException, InterruptedException
	{
		String text = "Information has been received on 06 Dec 2006 from a physician concerning a 5 year old female who on 29 Nov 2006 was vaccinated with MMR II 0.5ml, IME lot 651429/03/86R. There was no concomitant medications. On 04 Dec 2006, the girl complained of whole body discomfort with intolerable itch. It appeared red swelling in her neck, face, oxter and fold inguen. Also there were some little white pustules which had merged into one piece and spread along. The girl was taken to clinic and prescribed with Cetirizine, 5mg, OD, Cefuroxin sodium, 0.25 BID, antiscorbic acid, 0.1 QD and Mupirocin for external use. On 13 Dec 2006, new information was received from the physician. It was confirmed that the girl experienced rash on 03 Dec 2006 instead of 04 Dec 2006. Meanwhile, her face was flushed and pustules appeared in her neck and oxter. On 05 Dec 2006, the girl developed fever (details unknown). On 06 Dec 2006, the girl was hospitalized with the diagnosis of drug eruption. Physical examination revealed her body temperature was 38.5. Her face was flushed with pustules in her neck,, fem intern and greater lip of pudendum. Her throat was in congestion and there was not enlargement in her tonsils. Blood examination showed white blood cell count 21.72 and neutrophil count 81.4. During hospitalization, she was placed on therapy with Rocephin, Clarityne and Calcium gluconate (Detailed regimen unknown). At the time of reporting, rash was disappearing and desquamation. White blood cell count decreased to 16 and neutrophil count decreased to 65. On 13 Dec 2006, the girl was discharged from hospitalization while she was recovering from drug eruption. The reporter considered drug eruption was definitely related to MMR II. Additional information has been requested.";

		TTKWrapper wrapper = new TTKWrapper();
		String result = wrapper.extract(text);
		System.out.println(result);
	}
}
