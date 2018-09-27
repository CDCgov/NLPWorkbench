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

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ctakes.typesystem.type.refsem.OntologyConcept;
import org.apache.ctakes.typesystem.type.textsem.EntityMention;
import org.apache.ctakes.ytex.kernel.dao.ConceptDao;
import org.apache.ctakes.ytex.kernel.model.ConceptGraph;
import org.apache.ctakes.ytex.kernel.wsd.WordSenseDisambiguator;
import org.apache.ctakes.ytex.uima.ApplicationContextHolder;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.uima.fit.factory.JCasFactory;

import com.google.common.collect.Lists;

public class SenseDisambiguatorAnnotatorTest {
	ConceptDao conceptDao;
	@Before
	public void setUp() throws Exception {
		BeanFactory appCtx = ContextSingletonBeanFactoryLocator
				.getInstance("classpath*:org/apache/ctakes/ytex/kernelBeanRefContext.xml")
				.useBeanFactory("kernelApplicationContext").getFactory();
		conceptDao = appCtx.getBean(ConceptDao.class);
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(appCtx.getBean(DataSource.class));
		Properties ytexProperties = (Properties)appCtx.getBean("ytexProperties");
		String dbtype = ytexProperties.getProperty("db.type");
		if("hsql".equals(dbtype) || "mysql".equals(dbtype))
			jdbcTemplate.execute("drop table if exists test_concepts");
		if("mssql".equals(dbtype))
			jdbcTemplate.execute("if exists(select * from sys.objects where object_id = object_id('test_concepts')) drop table test_concepts");
		if ("orcl".equals(dbtype)) {
			// just try dropping the table, catch exception and hope all is well
			try {
				jdbcTemplate.execute("drop table test_concepts");
			} catch (Exception ignore) {

			}
		}
		jdbcTemplate
				.execute("create table test_concepts(parent varchar(20), child varchar(20))");
		jdbcTemplate
				.execute("insert into test_concepts values ('root', 'animal')");
		jdbcTemplate
				.execute("insert into test_concepts values ('animal', 'vertebrate')");
		jdbcTemplate
				.execute("insert into test_concepts values ('vertebrate', 'cat')");
		jdbcTemplate
				.execute("insert into test_concepts values ('vertebrate', 'dog')");
		jdbcTemplate
				.execute("insert into test_concepts values ('root', 'bacteria')");
		jdbcTemplate
				.execute("insert into test_concepts values ('bacteria', 'e coli')");
		conceptDao.createConceptGraph(null, "test",
				"select child, parent from test_concepts", true,
				Collections.EMPTY_SET);
		ConceptGraph cg = conceptDao.getConceptGraph("test");
		Assert.assertNotNull(cg);
		((ConfigurableApplicationContext) appCtx).close();
	}

	/**
	 * 3 concepts, middle one with 2 cuis.  Middle concept should be disambiguated
	 * @throws UIMAException
	 */
	@Test
	public void testDisambiguate() throws UIMAException {
		System.setProperty("ytex.conceptGraphName", "test");
		System.setProperty("ytex.conceptPreload", "false");
		System.setProperty("ytex.conceptSetName", "");
		JCas jCas = JCasFactory
				.createJCasFromPath("src/main/resources/org/apache/ctakes/ytex/types/TypeSystem.xml");
		String text = "concept1 concept2 concept3";
		jCas.setDocumentText(text);
		EntityMention em1 = new EntityMention(jCas);
		em1.setBegin(0);
		em1.setEnd(8);
		setConcepts(jCas, em1, new String[] { "dog" });
		em1.addToIndexes();

		EntityMention em2 = new EntityMention(jCas);
		em2.setBegin(9);
		em2.setEnd(17);
		setConcepts(jCas, em2, new String[] { "e coli", "animal" });
		em2.addToIndexes();
		
		EntityMention em3 = new EntityMention(jCas);
		em3.setBegin(18);
		em3.setEnd(26);
		setConcepts(jCas, em3, new String[] { "cat" });
		em3.addToIndexes();
		
		SenseDisambiguatorAnnotator sda = new SenseDisambiguatorAnnotator();
		sda.wsd = ApplicationContextHolder.getApplicationContext().getBean(
				WordSenseDisambiguator.class);
		sda.process(jCas);
		AnnotationIndex<Annotation> annoIdx = jCas.getAnnotationIndex(EntityMention.type);
		List<Annotation> annoList = Lists.newArrayList(annoIdx);
		EntityMention emD = (EntityMention)annoList.get(1);
		FSArray fsa = emD.getOntologyConceptArr();
		for(int i = 0; i < fsa.size(); i++) {
			OntologyConcept oc = (OntologyConcept)fsa.get(i);
			if("animal".equals(oc.getCode())) {
				Assert.assertTrue(oc.getDisambiguated());
			} else {
				Assert.assertFalse(oc.getDisambiguated());
			}
		}
	}

	private void setConcepts(JCas jCas, EntityMention em1, String[] concepts) {
		FSArray em1oc = new FSArray(jCas, concepts.length);
		for (int i = 0; i < concepts.length; i++) {
			OntologyConcept oc1 = new OntologyConcept(jCas);
			oc1.setCode(concepts[i]);
			em1oc.set(i, oc1);
		}
		em1.setOntologyConceptArr(em1oc);
	}

}
