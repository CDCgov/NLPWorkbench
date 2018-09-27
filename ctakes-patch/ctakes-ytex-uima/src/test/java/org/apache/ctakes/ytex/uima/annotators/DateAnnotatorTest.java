/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ctakes.ytex.uima.annotators;

import org.apache.ctakes.typesystem.type.textsem.DateAnnotation;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Assert;
import org.junit.Test;
import org.apache.uima.fit.factory.JCasFactory;

/**
 * TODO get rid of hard-coded path to Types.xml - load from classpath
 * @author vgarla
 *
 */
public class DateAnnotatorTest {

	/**
	 * Verify that date parsing with a manually created date works
	 * @throws UIMAException
	 */
	@Test
	public void testParseDate() throws UIMAException {
	    JCas jCas = JCasFactory.createJCasFromPath("src/main/resources/org/apache/ctakes/ytex/types/TypeSystem.xml");
	    String date = (new java.util.Date()).toString();
	    jCas.setDocumentText(date);
	    DateAnnotation ctakesDate = new DateAnnotation(jCas);
	    ctakesDate.setBegin(0);
	    ctakesDate.setEnd(date.length());
	    ctakesDate.addToIndexes();
	    DateAnnotator dateAnnotator = new DateAnnotator();
	    dateAnnotator.dateType = org.apache.ctakes.ytex.uima.types.Date.class.getName();
	    dateAnnotator.process(jCas);
	    AnnotationIndex<Annotation> ytexDates = jCas.getAnnotationIndex(org.apache.ctakes.ytex.uima.types.Date.type);
	    Assert.assertTrue(ytexDates.iterator().hasNext());
	    String dateParsed = ((org.apache.ctakes.ytex.uima.types.Date)ytexDates.iterator().next()).getDate();
	    Assert.assertNotNull(dateParsed);
	    System.out.println(dateParsed);
	}

}
