package gov.hhs.aspe.nlp.SafetySurveillance.metamap;

import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.lappsgrid.api.WebService;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import static org.lappsgrid.discriminator.Discriminators.*;

import static org.junit.Assert.*;

/**
 *
 */
public class MetaMapServiceTest
{
	private static final String text = "My son received Smallpox vaccination on 4/21/2006 in left deltoid. 12 days after he developed increased left arm pain and pleuritic substernal chest pain. 5/11/06 transferred to hospital with chest pain, right arm pain. Final dx of acute myopericarditis, serum reaction, allergic reaction, anemia, abnormal reaction to vaccine. Medical records from previous hospitalization obtained on 5/14/06 showed PMHx of Stevens-Johnson syndrome; family hx reveals patient's father had myocardial infarction.";
	private WebService service;

	@Before
	public void setup() {
		service = new MetaMapService();
	}

	@After
	public void cleanup() {
		service = null;
	}

	@Test
	public void execute() throws UtilsException
	{
		Data data = new Data(Uri.TEXT, text);
		String json = service.execute(data.asJson());
		data = Serializer.parse(json);
		assertEquals(Uri.UIMA, data.getDiscriminator());
		System.out.println(data.getPayload().toString());
	}

	@Test
	public void roundtrip() throws UtilsException
	{
		Data data = new Data(Uri.TEXT, text);
		String json = service.execute(data.asJson());
		data = Serializer.parse(json);
		assertEquals(Uri.UIMA, data.getDiscriminator());

		// See if we can load the XML into a UIMA CAS object.
		String xml = data.getPayload().toString();
		Utils.loadCas(xml);
	}

	@Test
	public void getMetadata()
	{
		String json = service.getMetadata();
		assertNotNull(json);

		Data data = Serializer.parse(json);
		assertEquals(data.getPayload().toString(), Uri.META, data.getDiscriminator());

	}


}