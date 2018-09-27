/*
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
package org.apache.ctakes.constituency.parser.ae;

import org.apache.ctakes.constituency.parser.MaxentParserWrapper;
import org.apache.ctakes.constituency.parser.ParserWrapper;
import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.FileNotFoundException;


@PipeBitInfo(
      name = "Constituency Parser",
      description = ".",
      dependencies = { PipeBitInfo.TypeProduct.DOCUMENT_ID, PipeBitInfo.TypeProduct.SENTENCE }
)
public class ConstituencyParser extends JCasAnnotator_ImplBase {
	public static final String PARAM_MODEL_FILENAME = "MODEL_FILENAME";
	
	@ConfigurationParameter(
			name = PARAM_MODEL_FILENAME,
			description = "File containing the opennlp-trained parser model",
			mandatory = false,
			defaultValue = "org/apache/ctakes/constituency/parser/models/sharpacq-3.1.bin"
	) private String modelFilename;
	
	
	private ParserWrapper parser = null;
	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			logger.info("Initializing parser...");		
			parser = new MaxentParserWrapper(FileLocator.getAsStream(modelFilename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Error reading parser model file/directory: " + e.getMessage());
			throw new ResourceInitializationException(e);
		}
	}


	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		parser.createAnnotations(jcas);
	}
	
	  public static AnalysisEngineDescription createAnnotatorDescription(
		      String modelPath) throws ResourceInitializationException {
		    return AnalysisEngineFactory.createEngineDescription(
		    		ConstituencyParser.class,
		    		ConstituencyParser.PARAM_MODEL_FILENAME,
		        modelPath);
		  }
	  public static AnalysisEngineDescription createAnnotatorDescription() 
			  throws ResourceInitializationException {
		    return AnalysisEngineFactory.createEngineDescription(
		    		ConstituencyParser.class);
		  }	  
}
