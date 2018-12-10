package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import gov.cdc.lappsgrid.utils.Utils;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import gov.hhs.aspe.nlp.SafetySurveillance.ncbo.NcboService;
import org.apache.uima.cas.CAS;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.io.IOException;

import static org.lappsgrid.discriminator.Discriminators.*;

/**
 *
 */
public class NcboServiceTest
{
	private static final String TEXT = "My son received Smallpox vaccination on 4/21/2006 in left deltoid. 12 days after he developed increased left arm pain and pleuritic substernal chest pain. 5/11/06 transferred to hospital with chest pain, right arm pain. Final dx of acute myopericarditis, serum reaction, allergic reaction, anemia, abnormal reaction to vaccine. Medical records from previous hospitalization obtained on 5/14/06 showed PMHx of Stevens-Johnson syndrome; family hx reveals patient's father had myocardial infarction.";

	public NcboServiceTest()
	{

	}

	@Test
	public void noParameters() {
		NcboService service = new NcboService();
		Data data = new Data(Uri.TEXT, TEXT);

		String json = service.execute(data.asJson());
		data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();
		assertEquals(Uri.UIMA, discriminator);

		String xml = data.getPayload().toString();
		System.out.println(xml);
	}

	@Test
	public void withParameters()
	{
		NcboService service = new NcboService();
		Data data = new Data(Uri.TEXT, TEXT);
		data.setParameter("ontologies", new String[] { "MEDDRA" });
		data.setParameter("range", 5);

		String json = service.execute(data.asJson());
		data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();
		assertEquals(Uri.UIMA, discriminator);

		String xml = data.getPayload().toString();
		System.out.println(xml);
	}

	@Test
	public void roundTrip() throws UtilsException, IOException
	{
		NcboService service = new NcboService();
		Data data = new Data(Uri.TEXT, TEXT);
		data.setParameter("ontologies", new String[] { "MEDDRA" });
		data.setParameter("range", 5);
		String json = service.execute(data.asJson());
		data = Serializer.parse(json, Data.class);
		String discriminator = data.getDiscriminator();
		assertEquals(Uri.UIMA, discriminator);

		// See if the XML can be round-tripped through a CAS object.
		String xml = data.getPayload().toString();
		System.out.println(xml);
		CAS cas = Utils.loadCas(xml);
		xml = Utils.toXcas(cas);
		System.out.println(xml);
	}


}
