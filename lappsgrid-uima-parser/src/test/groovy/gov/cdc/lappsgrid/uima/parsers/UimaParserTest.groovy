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
package gov.cdc.lappsgrid.uima.parsers

import org.junit.After
import org.junit.Before;
import org.junit.Test
import org.lappsgrid.serialization.lif.Container;

import static org.junit.Assert.*;

/**
 *
 */
class UimaParserTest
{
	UimaParser parser

	@Before
	void setup() {
		parser = new UimaParser()
	}

	@After
	void teardown() {
		parser = null
	}

	@Test
	void parseXcasFile()
	{
		File file = new File("src/test/resources/ctakes.xml")
		Container container = parser.parse(file)
		assert container != null
		assert 1 == container.views.size()
		assert 1103 == container.views[0].annotations.size()
		println "UimaParserTest.parseXcasFile"
	}

	@Test
	void parseXmiFile() {
		File file = new File("src/test/resources/ctakes.xmi")
		Container container = parser.parse(file)
		assert container != null
		assert 9 == container.metadata.namespaces.size()
		assert 1 == container.views.size()
		assert 842 == container.views[0].annotations.size()
		println "UimaParserTest.parseXmiFile"
	}

	@Test
	void parseXcasUrl() {
		URL url = this.class.getResource('/ctakes.xml')
		Container container = parser.parse(url)
		assert container != null
		assert 1 == container.views.size()
		assert 1103 == container.views[0].annotations.size()
		println "UimaParserTest.parseXcasUrl"
	}

	@Test
	void parseXmiUrl() {
		URL url = this.class.getResource('/ctakes.xmi')
		Container container = parser.parse(url)
		assert container != null
		assert 9 == container.metadata.namespaces.size()
		assert 1 == container.views.size()
		assert 842 == container.views[0].annotations.size()
		println "UimaParserTest.parseXmiUrl"
	}

	@Test
	void parseXcasInputStream() {
		InputStream stream = this.class.getResourceAsStream('/ctakes.xml')
		Container container = parser.parse(stream)
		assert container != null
		assert 1 == container.views.size()
		assert 1103 == container.views[0].annotations.size()
		println "UimaParserTest.parseXcasInputStream"
	}

	@Test
	void parseXmiInputStream() {
		InputStream stream = this.class.getResourceAsStream('/ctakes.xmi')
		Container container = parser.parse(stream)
		assert container != null
		assert 9 == container.metadata.namespaces.size()
		assert 1 == container.views.size()
		assert 842 == container.views[0].annotations.size()
		println "UimaParserTest.parseXmiInputStream"
	}
}