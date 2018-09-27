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
package gov.cdc.lappsgrid.utils;

import gov.cdc.lappsgrid.utils.messages.Messages;
import gov.cdc.lappsgrid.utils.error.UtilsException;
import gov.cdc.lappsgrid.uima.TypeSystemAggregator;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIndexRepository;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.CasIOUtils;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods for reading characters from Files and URLs, loading/saving CAS
 * objects, and getting the XML representation of CAS objects.
 *
 */
public class Utils
{

	private static final String UTF8 = StandardCharsets.UTF_8.name();

	/**
	 * Reads the contents of the URL as a single string object using UTF-8
	 *
	 * @param url The url to read.
	 * @return A string object containing the contents read from the URL.
	 * @throws IOException
	 */
	public static String readString(URL url) throws UtilsException
	{
		InputStream stream = null;
		try
		{
			stream = url.openStream();
		}
		catch (IOException e)
		{
			throw new UtilsException();
		}

		return readString(stream, UTF8);
	}

	/**
	 * Reads the contents of a URL into a String object using the specified
	 * character encoding.
	 *
	 * @param url the URL to be read.
	 * @param encoding the character encoding.
	 * @return a String containing the contents of the stream.
	 * @throws UtilsException if there is a problem with the character encoding of
	 * the file or if the file can not be found.
	 */

	public static String readString(URL url, String encoding) throws UtilsException
	{
		InputStream stream = null;
		try
		{
			stream = url.openStream();
		}
		catch (IOException e)
		{
			throw new UtilsException(Messages.Error.OPENING_STREAM);
		}
		return readString(stream, encoding);
	}

	/**
	 * Reads the contents of a File into a String object. It is assumed
	 * the characters in the file are UTF-8.
	 *
	 * @param filename the name of the File to be read.
	 * @return a String containing the contents of the stream
	 * @throws UtilsException if there is a problem with the character encoding of
	 * the file or if the file can not be found.
	 */
	public static String readString(String filename) throws UtilsException
	{
		return readString(new File(filename), UTF8);
	}

	public static String readString(String filename, String encoding) throws UtilsException
	{
		return readString(new File(filename), encoding);
	}

	/**
	 * Reads the contents of the File into a String object using. It is assumed
	 * the characters in the file are UTF-8.
	 *
	 * @param file the File to be read.
	 * @return a String containing the contents of the stream
	 * @throws UtilsException if there is a problem with the character encoding of
	 * the file or if the file can not be found.
	 */
	public static String readString(File file) throws UtilsException
	{
		return readString(file, UTF8);
	}

	/**
	 * Reads the contents of the File into a String object using the specified
	 * character encoding.
	 *
	 * @param file the File to be read.
	 * @param encoding the character encoding of the file
	 * @return a String containing the contents of the stream
	 * @throws UtilsException if there is a problem with the character encoding of
	 * the file or if the file can not be found.
	 */
	public static String readString(File file, String encoding) throws UtilsException
	{
		InputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			throw new UtilsException(Messages.Error.READING_STRING, e);
		}

		return readString(stream, encoding);
	}

	/**
	 * Reads the contents of the InputStream into a String object. It is assumed that
	 * the stream uses UTF-8 as the character encoding.
	 *
	 * @param stream an InputStream
	 * @return a String containing the contents of the stream
	 * @throws UtilsException if there is a problem with the character encoding of the stream.
	 */
	public static String readString(InputStream stream) throws UtilsException
	{
		return readString(stream, UTF8);
	}

	/**
	 * Reads the contents of the InputStream into a String object,
	 *
	 * @param stream an InputStream
	 * @param encoding the character encoding of the bytes in the stream
	 * @return a String containing the contents read from the InputStream
	 * @throws UtilsException if there is a problem with the character encoding of the stream.
	 */
	public static String readString(InputStream stream, String encoding) throws UtilsException
	{
		try
		{
			return new BufferedReader(new InputStreamReader(stream, encoding)).lines().collect(Collectors.joining("\n"));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new UtilsException(Messages.Error.READING_STRING, e);
		}
	}

	/**
	 * Creates a CAS object using the default combined TypeSystem.
	 *
	 * @return a new empty CAS object.
	 * @throws UtilsException if there is a problem initializing the CAS object.
	 */
	public static CAS createCas() throws UtilsException
	{
		try
		{
			return CasCreationUtils.createCas(TypeSystemAggregator.defaultTypeSystem(), null, null, null);
		}
		catch (ResourceInitializationException e)
		{
			throw new UtilsException(Messages.Error.CREATING_CAS, e);
		}
	}

	/**
	 * Reads XML from the URL and creates a CAS object.
	 *
	 * @param url the URL containing the XML (XCAS or XMI) representation of a CAS object.
	 * @return the CAS object constructed from the XML.
	 * @throws UtilsException any IOException thrown when reading loading the CAS
	 * is caught, wrapped in a UtilsException, and rethrown.
	 */
	public static CAS loadCas(URL url) throws UtilsException
	{
		CAS cas = createCas();
		try
		{
			CasIOUtils.load(url, cas);
		}
		catch (IOException e)
		{
			throw new UtilsException(Messages.Error.LOADING_CAS, e);
		}
		return cas;
	}

	/**
	 * Reads XML from the file and creates a CAS object.
	 *
	 * @param file the file containing the XML (XCAS or XMI) representation of a CAS object.
	 * @return the CAS object constructed from the XML.
	 * @throws UtilsException any IOException thrown when reading loading the CAS
	 * is caught, wrapped in a UtilsException, and rethrown.
	 */
	public static CAS loadCas(File file) throws UtilsException
	{
		CAS cas = createCas();
		try
		{
			CasIOUtils.load(new FileInputStream(file), cas);
		}
		catch (IOException e)
		{
			throw new UtilsException(Messages.Error.LOADING_CAS);
		}
		return cas;
	}

	/**
	 * Parses the input xml String and creates a CAS object.
	 *
	 * @param xml the XML (XCAS or XMI) representation of a CAS object.
	 * @return the CAS object constructed from the XML.
	 * @throws UtilsException any IOException thrown when reading loading the CAS
	 * is caught, wrapped in a UtilsException, and rethrown.
	 */
	public static CAS loadCas(String xml) throws UtilsException
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
		CAS cas = createCas();

		try
		{
			CasIOUtils.load(stream, cas);
		}
		catch (IOException e)
		{
			throw new UtilsException(Messages.Error.LOADING_CAS, e);
		}
		return cas;
	}

	/**
	 * Saves a TypeSystemDescription to a file on disk.
	 *
	 * @param tsd the TypeSystemDescription to be saved.
	 * @param path the location where the TypeSystemDescription should be written.
	 * @throws UtilsException Any IOException or SAXException is caught, wrapped in a
	 * UtilsException and re-thrown.
	 */
	public static void save(TypeSystemDescription tsd, String path) throws UtilsException
	{
		save(tsd, new File(path));
	}

	/**
	 * Saves a TypeSystemDescription to a file on disk.
	 *
	 * @param tsd the TypeSystemDescription to be saved.
	 * @param file the location where the TypeSystemDescription should be written.
	 * @throws UtilsException Any IOException or SAXException is caught, wrapped in a
	 * UtilsException and re-thrown.
	 */
	public static void save(TypeSystemDescription tsd, File file) throws UtilsException
	{
		try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))
		{
			tsd.toXML(writer);
			writer.flush();
			writer.close();
		}
		catch (IOException | SAXException e) {
			throw new UtilsException(Messages.Error.SAVING_TYPESYSTEM, e);
		}
	}


	/**
	 * Saves a CAS object in the XCAS format.
	 *
	 * @param cas the CAS object to be saved.
	 * @return the XML (XCAS) representation of the CAS
	 * @throws IOException if there is an error saving the CAS object.
	 * @deprecated Use the Utils.toXcas() method insteam.
	 */
	@Deprecated
	public static String toXml(CAS cas) throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CasIOUtils.save(cas, stream, SerialFormat.XCAS);
		return new String(stream.toByteArray(), "UTF-8");
	}

	/**
	 * Saves a CAS object in the XCAS format.
	 *
	 * @param cas the CAS object to be saved.
	 * @return the XML (XCAS) representation of the CAS
	 * @throws IOException if there is an error saving the CAS object.
	 */
	public static String toXcas(CAS cas) throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CasIOUtils.save(cas, stream, SerialFormat.XCAS);
		return new String(stream.toByteArray());
	}

	/**
	 * Saves a CAS object in the XMI format.
	 *
	 * @param cas the CAS object to be saved.
	 * @return the XML (XMI) representation of the CAS
	 * @throws IOException if there is an error saving the CAS object.
	 */
	public static String toXmi(CAS cas) throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		CasIOUtils.save(cas, stream, SerialFormat.XMI);
		return new String(stream.toByteArray());
	}

	/**
	 * Returns a list of all annotations (FeatureStructure objects) in the CAS.
	 *
	 * @param cas the CAS object containing FeatureStructures
	 * @return a list of all annotations (FeatureStructures)
	 */
	public static List<FeatureStructure> getAnnotations(CAS cas) {
		List<FeatureStructure> result = new ArrayList<>();
		FSIndexRepository repository = cas.getIndexRepository();
		Iterator<String> labels = repository.getLabels();
		while (labels.hasNext())
		{
			String label = labels.next();
			FSIndex index = repository.getIndex(label);
			FSIterator fsIterator = index.iterator();
			while (fsIterator.hasNext()) {
				FeatureStructure fs = (FeatureStructure) fsIterator.next();
				result.add(fs);
			}
		}
		return result;
	}
}
