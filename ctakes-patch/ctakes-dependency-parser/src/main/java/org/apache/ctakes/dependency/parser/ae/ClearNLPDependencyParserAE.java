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
package org.apache.ctakes.dependency.parser.ae;

import com.googlecode.clearnlp.component.AbstractComponent;
import com.googlecode.clearnlp.dependency.DEPFeat;
import com.googlecode.clearnlp.dependency.DEPNode;
import com.googlecode.clearnlp.dependency.DEPTree;
import com.googlecode.clearnlp.engine.EngineGetter;
import com.googlecode.clearnlp.morphology.AbstractMPAnalyzer;
import com.googlecode.clearnlp.nlp.NLPLib;
import com.googlecode.clearnlp.reader.AbstractReader;
import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.dependency.parser.util.ClearDependencyUtility;
import org.apache.ctakes.dependency.parser.util.DependencyUtility;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * <br>
* This class provides a UIMA wrapper for the CLEAR dependency parser. This parser is available here:
 * <p>
 * http://code.google.com/p/clearnlp
 * <p>
 * Please see
 * /ClearNLP-wrapper/resources/dependency/README
 * for important information pertaining to the models provided for this parser. In particular, note
 * that the output of the CLEAR parser is different than that of the Malt parser and so these two
 * parsers may not be interchangeable (without some effort) for most use cases.
 * <p>
 * 
 * 
 */
@TypeCapability(
		inputs = { 
				"org.apache.ctakes.typesystem.type.syntax.BaseToken:partOfSpeech",
				"org.apache.ctakes.typesystem.type.syntax.BaseToken:normalizedForm",
				"org.apache.ctakes.typesystem.type.syntax.BaseToken:tokenNumber",
				"org.apache.ctakes.typesystem.type.syntax.BaseToken:end",
				"org.apache.ctakes.typesystem.type.syntax.BaseToken:begin"
		})
@PipeBitInfo(
      name = "ClearNLP Dependency Parser",
      description = "Analyses Sentence Structure, storing information in nodes.",
      role = PipeBitInfo.Role.SPECIAL,
      dependencies = { PipeBitInfo.TypeProduct.SENTENCE, PipeBitInfo.TypeProduct.BASE_TOKEN },
      products = { PipeBitInfo.TypeProduct.DEPENDENCY_NODE }
)
public class ClearNLPDependencyParserAE extends JCasAnnotator_ImplBase {

  final String language = AbstractReader.LANG_EN;
  public Logger logger = Logger.getLogger(getClass().getName());

  // Default model values
  public static final String DEFAULT_MODEL_FILE_NAME = "org/apache/ctakes/dependency/parser/models/dependency/mayo-en-dep-1.3.0.jar";
  public static final String ENG_LEMMATIZER_DATA_FILE = "org/apache/ctakes/dependency/parser/models/lemmatizer/dictionary-1.3.1.jar";


  // Configuration Parameters 
  public static final String PARAM_PARSER_MODEL_FILE_NAME = "ParserModelFileName";
  @ConfigurationParameter(
		  name = PARAM_PARSER_MODEL_FILE_NAME,
		  description = "This parameter provides the file name of the dependency parser model required " +
					      "by the factory method provided by ClearNLPUtil.  If not specified, this " +
					      "analysis engine will use a default model from the resources directory",
		  defaultValue = DEFAULT_MODEL_FILE_NAME)
  protected URI parserModelUri;

  public static final String PARAM_LEMMATIZER_DATA_FILE = "LemmatizerDataFile";

  @ConfigurationParameter(
      name = PARAM_LEMMATIZER_DATA_FILE,
      description = "This parameter provides the data file required for the MorphEnAnalyzer. If not "
          + "specified, this analysis engine will use a default model from the resources directory",
      defaultValue = ENG_LEMMATIZER_DATA_FILE)
  protected URI lemmatizerDataFile;

	public static final String PARAM_USE_LEMMATIZER = "UseLemmatizer";
	@ConfigurationParameter(
			name = PARAM_USE_LEMMATIZER,
			defaultValue = "true",
			description = "If true, use the default ClearNLP lemmatizer, otherwise use lemmas from the BaseToken normalizedToken field")
	protected boolean useLemmatizer;


	protected AbstractComponent parser;
	protected AbstractMPAnalyzer lemmatizer;
	//protected boolean useLemmatizer = false;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		logger.info("using Morphy analysis? " + useLemmatizer);

		try {
			if (useLemmatizer) {
				// Note: If lemmatizer data file is not specified, then use lemmas from the BaseToken normalizedToken field.
				// Initialize lemmatizer
				
                InputStream lemmatizerModel = (this.lemmatizerDataFile == null)
                        ? FileLocator.getAsStream(ENG_LEMMATIZER_DATA_FILE)
                        : FileLocator.getAsStream(this.lemmatizerDataFile.getPath());
                        
                    this.lemmatizer = EngineGetter.getMPAnalyzer(language, lemmatizerModel);
			}
				InputStream parserModel = (this.parserModelUri == null)
                    ? FileLocator.getAsStream(DEFAULT_MODEL_FILE_NAME)
                    : FileLocator.getAsStream(this.parserModelUri.getPath());
                 
                    this.parser = EngineGetter.getComponent(parserModel, this.language, NLPLib.MODE_DEP);

        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			List<BaseToken> printableTokens = new ArrayList<>();
			for(BaseToken token : JCasUtil.selectCovered(jCas, BaseToken.class, sentence)){
			  if(token instanceof NewlineToken) continue;
			  printableTokens.add(token);
			}

			if ( printableTokens.isEmpty() ) {
				// If there are no printable tokens then #convert fails
				continue;
			}
			DEPTree tree = new DEPTree();

			// Convert CAS data into structures usable by ClearNLP
			for (int i = 0; i < printableTokens.size(); i++) {
				BaseToken token = printableTokens.get(i);
				String lemma = useLemmatizer ? lemmatizer.getLemma(token.getCoveredText(), token.getPartOfSpeech()) : token.getNormalizedForm();
				DEPNode node = new DEPNode(i+1, token.getCoveredText(), lemma, token.getPartOfSpeech(), new DEPFeat());
				tree.add(node);
			}

			// Run parser and convert output back to CAS friendly data types
			parser.process(tree);
			ArrayList<ConllDependencyNode> nodes = ClearDependencyUtility.convert( jCas, tree, sentence, printableTokens );
			DependencyUtility.addToIndexes( jCas, nodes );
		}
		
		
	}
	
	public static AnalysisEngineDescription createAnnotatorDescription() throws ResourceInitializationException{
	  return AnalysisEngineFactory.createEngineDescription(ClearNLPDependencyParserAE.class);
	}
}
