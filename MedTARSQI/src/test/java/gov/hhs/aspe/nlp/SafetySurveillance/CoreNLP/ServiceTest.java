package gov.hhs.aspe.nlp.SafetySurveillance.CoreNLP;

import gov.cdc.lappsgrid.utils.error.UtilsException;
import gov.hhs.aspe.nlp.SafetySurveillance.MedTarsqi.ClinicalService;
import gov.hhs.aspe.nlp.SafetySurveillance.MedTarsqi.RelationService;
import gov.hhs.aspe.nlp.SafetySurveillance.MedTarsqi.TemporalService;
import gov.hhs.aspe.nlp.SafetySurveillance.MedTarsqi.Version;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import org.lappsgrid.api.WebService;
import org.lappsgrid.metadata.IOSpecification;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;

import java.util.List;
import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.*;

/**
 *
 */
public class ServiceTest
{
	static final String input = new Data(Uri.TEXT,"Information has been received on 06 Dec 2006 from a physician concerning a 5 year old female who on 29 Nov 2006 was vaccinated with MMR II 0.5ml, IME lot 651429/03/86R. There was no concomitant medications. On 04 Dec 2006, the girl complained of whole body discomfort with intolerable itch. It appeared red swelling in her neck, face, oxter and fold inguen. Also there were some little white pustules which had merged into one piece and spread along. The girl was taken to clinic and prescribed with Cetirizine, 5mg, OD, Cefuroxin sodium, 0.25 BID, antiscorbic acid, 0.1 QD and Mupirocin for external use. On 13 Dec 2006, new information was received from the physician. It was confirmed that the girl experienced rash on 03 Dec 2006 instead of 04 Dec 2006. Meanwhile, her face was flushed and pustules appeared in her neck and oxter. On 05 Dec 2006, the girl developed fever (details unknown). On 06 Dec 2006, the girl was hospitalized with the diagnosis of drug eruption. Physical examination revealed her body temperature was 38.5. Her face was flushed with pustules in her neck,, fem intern and greater lip of pudendum. Her throat was in congestion and there was not enlargement in her tonsils. Blood examination showed white blood cell count 21.72 and neutrophil count 81.4. During hospitalization, she was placed on therapy with Rocephin, Clarityne and Calcium gluconate (Detailed regimen unknown). At the time of reporting, rash was disappearing and desquamation. White blood cell count decreased to 16 and neutrophil count decreased to 65. On 13 Dec 2006, the girl was discharged from hospitalization while she was recovering from drug eruption. The reporter considered drug eruption was definitely related to MMR II. Additional information has been requested.").asJson();

	private WebService service;

	@After
	public void cleanup() {
		service = null;
	}

	@Test
	public void clinicalMetadata() {
		createClinicalService();
		assertNotNull(service);

		ServiceMetadata metadata = getMetadata();
		assertEquals("MedTarsqi Clinical Service", metadata.getName());
		checkMetadata(metadata);
	}

	@Test
	public void relationMetadata() {
		createRelationService();
		assertNotNull(service);
		ServiceMetadata metadata = getMetadata();
		assertEquals("MedTarsqi Relation Service", metadata.getName());
		checkMetadata(metadata);
	}

	@Test
	public void temporalMetadata() {
		createTemporalService();
		assertNotNull(service);
		ServiceMetadata metadata = getMetadata();
		assertEquals("MedTarsqi Temporal Service", metadata.getName());
		checkMetadata(metadata);
	}

	@Test
	public void clinical() throws UtilsException
	{
		createClinicalService();
		String json = service.execute(input);
		Data data = Serializer.parse(json);
		assertEquals(Uri.UIMA, data.getDiscriminator());

//		CAS cas = Utils.loadCas(data.getPayload().toString());
//		assertTrue(0 <cas.size());
	}

	@Test
	public void relation() throws UtilsException
	{
		createRelationService();
		String json = service.execute(input);
		Data data = Serializer.parse(json);
		assertEquals(Uri.UIMA, data.getDiscriminator());

//		CAS cas = Utils.loadCas(data.getPayload().toString());
//		assertTrue(0 <cas.size());
	}

	@Test
	public void temporal() throws UtilsException
	{
		createTemporalService();
		String json = service.execute(input);
		Data data = Serializer.parse(json);
		assertEquals(Uri.UIMA, data.getDiscriminator());

//		CAS cas = Utils.loadCas(data.getPayload().toString());
//		assertTrue(0 <cas.size());
	}

	private ServiceMetadata getMetadata() {
		String json = service.getMetadata();
		System.out.println(json);
		Data data = Serializer.parse(json, Data.class);
		assertEquals(Uri.META, data.getDiscriminator());
		return new ServiceMetadata((Map)data.getPayload());
	}

	private void checkMetadata(ServiceMetadata metadata) {
		assertEquals(Version.getVersion(), metadata.getVersion());
		assertEquals(Uri.APACHE2, metadata.getLicense());
		assertEquals("http://aspe.hhs.gov", metadata.getVendor());
		assertEquals(Uri.ALL, metadata.getAllow());
		assertEquals(1, metadata.getProduces().getFormat().size());

		IOSpecification requires = metadata.getRequires();
		List<String> langs = requires.getLanguage();
		assertEquals(1, langs.size());
		assertEquals("en", langs.get(0));
		List<String> formats = requires.getFormat();
		assertEquals(3, formats.size());
		assertTrue(formats.contains(Uri.TEXT));
		assertTrue(formats.contains(Uri.LIF));
		assertTrue(formats.contains(Uri.UIMA));
	}

	private void createClinicalService() {
		service = new ClinicalService();
	}
	private void createRelationService() {
		service = new RelationService();
	}
	private void createTemporalService() {
		service = new TemporalService();
	}
}
