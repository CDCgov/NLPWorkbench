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
package org.apache.ctakes.ytex.umls.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

/**
 * this test only works if MRCONSO is in the database (not the case for default
 * test settings). In case MRCONSO is not there, catch exception and ignore.
 * 
 * @author vgarla
 * 
 */
public class UMLSDaoTest {
	private static final Logger log = Logger.getLogger(UMLSDaoTest.class);
	UMLSDao umlsDao = null;

	@Before
	public void setUp() throws Exception {
		ApplicationContext appCtx = (ApplicationContext) ContextSingletonBeanFactoryLocator
				.getInstance(
						"classpath*:org/apache/ctakes/ytex/kernelBeanRefContext.xml")
				.useBeanFactory("kernelApplicationContext").getFactory();
		umlsDao = appCtx.getBean(UMLSDao.class);
	}

	@Test
	public void testGetAllAuiStr() {
		try {
			List<Object[]> auis = umlsDao.getAllAuiStr("");
			Assert.assertNotNull(auis);
			log.debug("testGetAllAuiStr()" + auis.size());
		} catch (Exception e) {
			log.warn(
					"sql exception - mrconso probably doesn't exist, check error",
					e);
		}
	}

}
