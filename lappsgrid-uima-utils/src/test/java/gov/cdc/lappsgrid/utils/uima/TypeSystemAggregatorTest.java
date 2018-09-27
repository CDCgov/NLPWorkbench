package gov.cdc.lappsgrid.utils.uima;

import gov.cdc.lappsgrid.uima.TypeSystemAggregator;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class TypeSystemAggregatorTest
{

	@Test
	public void get() throws UtilsException
	{
		TypeSystemAggregator aggregator = new TypeSystemAggregator();
		aggregator.add(read("src/main/resources/typesystems/ctakes.xml"));
		aggregator.add(read("src/main/resources/typesystems/opennlp.xml"));
		aggregator.add(read("src/main/resources/typesystems/vaers.xml"));
		TypeSystemDescription tsd = aggregator.get();
		TypeDescription[] types = tsd.getTypes();
		assert 280 == types.length;
	}

	@Test
	public void defaultTypeSystem() throws UtilsException
	{
		// Just ensure no NPE are thrown
		TypeSystemDescription tsd = TypeSystemAggregator.defaultTypeSystem();
		assertNotNull(tsd);
		TypeDescription[] types = tsd.getTypes();
		assertEquals(280, types.length);
	}

	@Test
	public void loadCtakesTypeSystem() throws UtilsException
	{
		TypeSystemDescription tsd = load("ctakes.xml");
		assertNotNull(tsd);
		TypeDescription[] types = tsd.getTypes();
		System.out.println("cTAKES: " + types.length);
		List<TypeDescription> typeList = Arrays.asList(types);
		Comparator<TypeDescription> compare = (a,b) -> { return a.getName().compareTo(b.getName()); };
		typeList.sort(compare);
		typeList.stream().map(TypeDescription::getName).forEach(System.out::println);
	}

	@Test
	public void loadOpenNLPTypeSystem() throws UtilsException
	{
		TypeSystemDescription tsd = load("opennlp.xml");
		assertNotNull(tsd);
		TypeDescription[] types = tsd.getTypes();
		System.out.println("OpenNLP: " + types.length);
	}

	@Test
	public void loadVaersTypeSystem() throws UtilsException
	{
		TypeSystemDescription tsd = load("vaers.xml");
		assertNotNull(tsd);
		TypeDescription[] types = tsd.getTypes();
		System.out.println("VAERS: " + types.length);
	}

	@Test
	public void getTypeSystemDescription() throws UtilsException
	{
		TypeSystemAggregator aggregator = new TypeSystemAggregator();
		aggregator.add(load("ctakes.xml"));
		aggregator.add(load("opennlp.xml"));
		aggregator.add(load("vaers.xml"));
		TypeSystemDescription tsd = aggregator.get();
		assertNotNull(tsd);
	}

	private TypeSystemDescription read(String path) throws UtilsException
	{
		File file = new File(path);
		return TypeSystemAggregator.loadTypeSystemDescription(file);
	}

	private TypeSystemDescription load(String path) throws UtilsException
	{
		File file = getFileForResource("/typesystems/" + path);
		assertNotNull(file);
		return TypeSystemAggregator.loadTypeSystemDescription(file);
	}

	private File getFileForResource(String path) {
		URL url = this.getClass().getResource(path);
		if (url == null) {
			return null;
		}
		File file = new File(url.getFile());
		if (!file.exists()) {
			return null;
		}
		return file;
	}
}