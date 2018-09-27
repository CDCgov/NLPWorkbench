/*-
 * Copyright 2018 The Centers for Disease Control and Prevention
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package gov.cdc.lappsgrid.uima;

import gov.cdc.lappsgrid.utils.error.UtilsException;
import gov.cdc.lappsgrid.utils.messages.Messages;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The TypeSystemAggregator is used to combine multiple UIMA TypeSystems into a
 * single TypeSyStem.
 * <p>
 * Use the static <code>defaultTypeSystem()</code> method to obtain a instance of
 * the combined TypeSystem or the static <code>loadTypeSystemDescription(File)</code>
 * to load a TypeSystem from a File.
 * </p>
 * <p>
 *     It is also possible to create an instance of the TypeSystemAggregator to
 *     combine an arbitrary set of TypeSystems.
 * </p>
 */
public class TypeSystemAggregator
{
	// The TypeSystemDescription created by combining the TypeSystems from cTAKES,
	// OpenNLP, and VAERS.
	private static TypeSystemDescription description;

	// The list of TypeSystemDescriptions that will be combined into a single
	// TypeSystem.
	private List<TypeSystemDescription> descriptions;

	public TypeSystemAggregator()
	{
		descriptions = new ArrayList<>();
	}

	public void add(String xml) throws IOException, InvalidXMLException
	{
		XMLParser parser = UIMAFramework.getXMLParser();
		add(parser.parseTypeSystemDescription(new XMLInputSource(xml)));
	}

	public void add(TypeSystemDescription description) {
		descriptions.add(description);
	}

	public TypeSystemDescription get() throws UtilsException
	{
		try
		{
			return CasCreationUtils.mergeTypeSystems(descriptions);
		}
		catch (ResourceInitializationException e)
		{
			throw new UtilsException(Messages.Error.MERGING_TYPE_SYSTEM, e);
		}
	}

	public static TypeSystemDescription defaultTypeSystem() throws UtilsException
	{
//		File directory = getTypeSystemDirectory();
		List<TypeSystemDescription> descriptions = new ArrayList<>();
		try
		{
			descriptions.add(getTypeSystemDescription("/typesystems/ctakes.xml"));
			descriptions.add(getTypeSystemDescription("/typesystems/vaers.xml"));
			descriptions.add(getTypeSystemDescription("/typesystems/opennlp.xml"));
			return CasCreationUtils.mergeTypeSystems(descriptions);
		}
		catch (IOException | InvalidXMLException | ResourceInitializationException e)
		{
			throw new UtilsException("Unable to load typesystem.", e);
		}
//		try {
//			FileFilter filter = (f) -> (f.isFile() && f.getName().endsWith(".xml"));
//			for (File file : directory.listFiles(filter)) {
//				descriptions.add(getTypeSystemDescription(file));
//			}
//			return CasCreationUtils.mergeTypeSystems(descriptions);
//		}
//		catch (IOException  | InvalidXMLException | ResourceInitializationException e)
//		{
//			throw new UtilsException(e);
//		}
	}

	public static TypeSystemDescription loadTypeSystemDescription(File file) throws UtilsException
	{
		try
		{
			XMLParser parser = UIMAFramework.getXMLParser();
			return parser.parseTypeSystemDescription(new XMLInputSource(file));
		}
		catch (InvalidXMLException | IOException e)
		{
			throw new UtilsException(Messages.Error.LOADING_TYPE_SYSTEM, e);
		}
	}

	private static TypeSystemDescription getTypeSystemDescription(String path) throws UtilsException, IOException, InvalidXMLException
	{
		URL url = TypeSystemAggregator.class.getResource(path);
		if (url == null) {
			throw new UtilsException(Messages.Error.GETTING_RESOURCE_URL + path);
		}
		return getTypeSystemDescription(new XMLInputSource(url));
	}

	private static TypeSystemDescription getTypeSystemDescription(File file) throws IOException, InvalidXMLException
	{
		return getTypeSystemDescription(new XMLInputSource(file));
	}

	private static TypeSystemDescription getTypeSystemDescription(XMLInputSource source) throws InvalidXMLException
	{
		XMLParser parser = UIMAFramework.getXMLParser();
		return parser.parseTypeSystemDescription(source);
	}

	private static File getTypeSystemDirectory() throws UtilsException
	{
		String env = System.getenv("TYPESYSTEM_DIRECTORY");
		File directory;
		if (env != null)
		{
			directory = new File(env);
		}
		else {
			// If the environment variable has not been set we look for a directory
			// named /typesystems/ on the classpath.
			URL url = TypeSystemAggregator.class.getResource("/typesystems/");
			directory = new File(url.getFile());
		}
		if (!directory.exists())
		{
			throw new UtilsException(Messages.Error.DIRECTORY_NOT_FOUND);
		}
		return directory;
	}

}
