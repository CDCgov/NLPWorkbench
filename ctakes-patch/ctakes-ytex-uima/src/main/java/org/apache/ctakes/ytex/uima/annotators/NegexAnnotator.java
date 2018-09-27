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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.typesystem.type.textsem.ContextAnnotation;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;


/**
 * Negex adapted to cTAKES. Checks negation status of named entities. Loads
 * negex triggers from classpath:
 * <tt>/org/apache/ctakes/ytex/uima/annotators/negex_triggers.txt</tt>
 * <p/>
 * The meaning of the certainty and confidence attributes is nowhere documented
 * for cTakes. There are several ways of handling 'maybes', see below. Default
 * behavior: certainty attribute for negated & possible terms set to -1
 * Configure with following uima initialization parameters:
 * <li>checkPossibilities : should we check for possibilities
 * <li>negatePossibilities : should possibilities be negated, default = true? if
 * true,
 * <ul>
 * <li>negated: polarity=-1, confidence=1
 * <li>possible: polarity=-1, confidence=-1
 * <li>affirmed: polarity=1, confidence=1
 * </ul
 * if false
 * <ul>
 * <li>negated: polarity=-1, confidence=1
 * <li>possible: polarity=1, confidence=-1
 * <li>affirmed: polarity=1, confidence=1
 * </ul>
 * <li>storeAsInterval
 * <ul>
 * <li>negated: polarity=-1, confidence = -1
 * <li>possible: polarity=1, confidence = 0.5
 * <li>affirmed: polarity=1, confidence = 1
 * </ul>
 * 
 * Added support for negating arbitrary annotations. Set the targetTypeName to
 * an annotation type. Will see if it is negated; if so will set the negated and
 * possible boolean values on the annotation.
 * 
 * @author vijay
 * 
 */
@PipeBitInfo(
		name = "Negation Annotator (Negex)",
		description = "Use negex to assign polarity to Named Entities.",
		dependencies = { PipeBitInfo.TypeProduct.SENTENCE, PipeBitInfo.TypeProduct.IDENTIFIED_ANNOTATION }
)
public class NegexAnnotator extends JCasAnnotator_ImplBase {
	private static final Log log = LogFactory.getLog(NegexAnnotator.class);
	private List<NegexRule> listNegexRules = null;
	private boolean negatePossibilities = true;
	private boolean checkPossibilities = true;
	private boolean storeAsInterval = false;
	private String targetTypeName = null;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		this.listNegexRules = this.initializeRules();
		negatePossibilities = getBooleanConfigParam(aContext,
				"negatePossibilities", negatePossibilities);
		if (negatePossibilities) {
			checkPossibilities = true;
		} else {
			checkPossibilities = getBooleanConfigParam(aContext,
					"checkPossibilities", checkPossibilities);
		}
		storeAsInterval = getBooleanConfigParam(aContext, "storeAsInterval",
				storeAsInterval);
		targetTypeName = (String) aContext
				.getConfigParameterValue("targetTypeName");
	}

	private boolean getBooleanConfigParam(UimaContext aContext, String param,
			boolean defaultVal) {
		Boolean paramValue = (Boolean) aContext.getConfigParameterValue(param);
		return paramValue == null ? defaultVal : paramValue;

	}

	private List<String> initalizeRuleList() {
		List<String> rules = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(this.getClass()
					.getResourceAsStream(
							"/org/apache/ctakes/ytex/uima/annotators/negex_triggers.txt")));
			String line = null;
			try {
				while ((line = reader.readLine()) != null)
					rules.add(line);
			} catch (IOException e) {
				log.error("oops", e);
			}
			Collections.sort(rules, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					int l1 = o1.trim().length();
					int l2 = o2.trim().length();
					if (l1 < l2)
						return 1;
					else if (l1 > l2)
						return -1;
					else
						return 0;
				}

			});
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				log.error("oops", e);
			}
		}
		return rules;
	}

	private List<NegexRule> initializeRules() {
		List<String> listRules = this.initalizeRuleList();
		List<NegexRule> listNegexRules = new ArrayList<NegexRule>(
				listRules.size());
		Iterator<String> iRule = listRules.iterator();
		while (iRule.hasNext()) {
			String rule = iRule.next();
			Pattern p = Pattern.compile("[\\t]+"); // Working.
			String[] ruleTokens = p.split(rule.trim());
			if (ruleTokens.length == 2) {
				// Add the regular expression characters to tokens and asemble
				// the
				// rule again.
				String[] ruleMembers = ruleTokens[0].trim().split(" ");
				String rule2 = "";
				for (int i = 0; i <= ruleMembers.length - 1; i++) {
					if (!ruleMembers[i].equals("")) {
						if (ruleMembers.length == 1) {
							rule2 = ruleMembers[i];
						} else {
							rule2 = rule2 + ruleMembers[i].trim() + "\\s+";
						}
					}
				}
				// Remove the last s+
				if (rule2.endsWith("\\s+")) {
					rule2 = rule2.substring(0, rule2.lastIndexOf("\\s+"));
				}

				String rule3 = "(?m)(?i)[[\\p{Punct}&&[^\\]\\[]]|\\s+]("
						+ rule2 + ")[[\\p{Punct}&&[^_]]|\\s+]";

				Pattern p2 = Pattern.compile(rule3.trim());
				listNegexRules.add(new NegexRule(p2, rule2, ruleTokens[1]
						.trim()));
			} else {
				log.warn("could not parse rule:" + rule);
			}
			// Matcher m = p2.matcher(sentence);
			//
			// while (m.find() == true) {
			// sentence = m.replaceAll(" " + ruleTokens[1].trim()
			// + m.group().trim().replaceAll(" ", filler)
			// + ruleTokens[1].trim() + " ");
			// }
		}
		return listNegexRules;

	}

	public static interface TargetAnnoFilter {
		public boolean filter(Annotation anno);
	}

	/**
	 * only bother with IdentifiedAnnotations that have concepts
	 * 
	 * @author vijay
	 * 
	 */
	public static class NamedEntityTargetAnnoFilter implements TargetAnnoFilter {

		@Override
		public boolean filter(Annotation anno) {
			if (!(anno instanceof IdentifiedAnnotation))
				return false;
			IdentifiedAnnotation ia = (IdentifiedAnnotation) anno;
			return ia.getOntologyConceptArr() != null
					&& ia.getOntologyConceptArr().size() > 0;
		}

	}

	@Override
	public void process(JCas aJCas) {
		AnnotationIndex sentenceIdx = aJCas
				.getAnnotationIndex(Sentence.typeIndexID);
		AnnotationIndex neIdx = aJCas
				.getAnnotationIndex(IdentifiedAnnotation.typeIndexID);
		negateAnnotations(aJCas, sentenceIdx, neIdx,
				new NamedEntityTargetAnnoFilter());
		if (targetTypeName != null) {
			try {
				negateAnnotations(
						aJCas,
						sentenceIdx,
						aJCas.getAnnotationIndex(aJCas.getTypeSystem().getType(
								targetTypeName)), null);
			} catch (Exception e) {
				log.error("error getting typeSystemId for " + targetTypeName, e);
			}
		}
	}

	private void negateAnnotations(JCas aJCas, AnnotationIndex sentenceIdx,
			AnnotationIndex targetIdx, TargetAnnoFilter filter) {
		FSIterator sentenceIter = sentenceIdx.iterator();
		while (sentenceIter.hasNext()) {
			Sentence s = (Sentence) sentenceIter.next();
			FSIterator neIter = targetIdx.subiterator(s);
			while (neIter.hasNext()) {
				Annotation ne = (Annotation) neIter.next();
				if (filter == null || filter.filter(ne))
					checkNegation(aJCas, s, ne);
				// checkNegation2(aJCas, s, ne);
			}
		}
	}

	public static class NegexRule {

		@Override
		public String toString() {
			return "NegexRule [rule=" + rule + ", tag=" + tag + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((rule == null) ? 0 : rule.hashCode());
			result = prime * result + ((tag == null) ? 0 : tag.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NegexRule other = (NegexRule) obj;
			if (rule == null) {
				if (other.rule != null)
					return false;
			} else if (!rule.equals(other.rule))
				return false;
			if (tag == null) {
				if (other.tag != null)
					return false;
			} else if (!tag.equals(other.tag))
				return false;
			return true;
		}

		private Pattern pattern;
		private String tag;
		private String rule;

		public Pattern getPattern() {
			return pattern;
		}

		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getRule() {
			return rule;
		}

		public void setRule(String rule) {
			this.rule = rule;
		}

		public NegexRule() {
			super();
		}

		public NegexRule(Pattern pattern, String rule, String tag) {
			super();
			this.pattern = pattern;
			this.tag = tag;
			this.rule = rule;
		}
	}

	public static class NegexToken implements Comparable<NegexToken> {
		private int start;
		private int end;
		private NegexRule rule;

		@Override
		public String toString() {
			return "NegexToken [start=" + start + ", end=" + end + ", rule="
					+ rule + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + ((rule == null) ? 0 : rule.hashCode());
			result = prime * result + start;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NegexToken other = (NegexToken) obj;
			if (end != other.end)
				return false;
			if (rule == null) {
				if (other.rule != null)
					return false;
			} else if (!rule.equals(other.rule))
				return false;
			if (start != other.start)
				return false;
			return true;
		}

		public NegexToken(int start, int end, NegexRule rule) {
			super();
			this.start = start;
			this.end = end;
			this.rule = rule;
		}

		@Override
		public int compareTo(NegexToken o) {
			return new Integer(this.start).compareTo(o.start);
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public String getTag() {
			return rule.getTag();
		}

	}

	private NegexToken findTokenByTag(String tag, String stopTags[],
			boolean before, int neRelStart, int neRelEnd, NegexToken tokens[]) {
		Set<String> stopTagSet = new HashSet<String>(stopTags.length);
		stopTagSet.addAll(Arrays.asList(stopTags));
		if (before) {
			for (int i = neRelStart - 1; i > 0; i--) {
				if (tokens[i] != null) {
					if (tokens[i].getTag().equals(tag)) {
						return tokens[i];
					} else if (stopTagSet.contains(tokens[i].getTag()))
						break;
				}
			}
		} else {
			for (int i = neRelEnd; i < tokens.length; i++) {
				if (tokens[i] != null) {
					if (tokens[i].getTag().equals(tag)) {
						return tokens[i];
					} else if (stopTagSet.contains(tokens[i].getTag()))
						break;
				}
			}
		}
		return null;
	}

	/**
	 * check the negation status of the specfied term in the specified sentence
	 * 
	 * @param aJCas
	 *            for adding annotations
	 * @param s
	 *            the sentence in which we will look
	 * @param ne
	 *            the named entity whose negation status will be checked.
	 * @param checkPoss
	 *            should possibility be checked?
	 * @param negPoss
	 *            should possiblities be negated?
	 */
	private void checkNegation(JCas aJCas, Sentence s, Annotation ne) {
		if (storeAsInterval && ne instanceof IdentifiedAnnotation) {
			// default is affirmed, which is coded as confidence = 1
			((IdentifiedAnnotation) ne).setConfidence(1);
		}
		// need to add . on either side due to the way the regexs are built
		String sentence = "." + s.getCoveredText() + ".";
		// allocate array of tokens
		// this maps each character of the sentence to a token
		NegexToken[] tokens = new NegexToken[sentence.length()];
		// char buffer for modify the sentence
		// we want to 'black out' trigger words already found and the phrase we
		// were looking for
		CharBuffer buf = CharBuffer.wrap(sentence.toCharArray());
		// calculate location of the ne relative to the sentence
		int neRelStart = ne.getBegin() - s.getBegin() + 1;
		int neRelEnd = ne.getEnd() - s.getBegin() + 1;
		// black out the ne in the sentence buffer
		for (int i = neRelStart; i < neRelEnd; i++) {
			// black out the named entity from the char buffer
			buf.put(i, '_');
		}
		// look for negex rules in the sentence
		for (NegexRule rule : this.listNegexRules) {
			Matcher m = rule.getPattern().matcher(buf);
			while (m.find() == true) {
				// see if the range has not already been marked
				boolean bUnoccupied = true;
				for (int i = m.start(); i < m.end() && bUnoccupied; i++)
					bUnoccupied = tokens[i] == null;
				if (bUnoccupied) {
					// mark the range in the sentence with this token
					// black it out so other rules do not match
					NegexToken t = new NegexToken(m.start(), m.end(), rule);
					for (int i = m.start(); i < m.end() && bUnoccupied; i++) {
						// black out this range from the char buffer
						buf.put(i, '_');
						// add the token to the array
						tokens[i] = t;
					}
				}
			}
		}
		// prenegation
		// look for a PREN rule before the ne, without any intervening stop tags
		NegexToken t = this.findTokenByTag("[PREN]", new String[] { "[CONJ]",
				"[PSEU]", "[POST]", "[PREP]", "[POSP]" }, true, neRelStart,
				neRelEnd, tokens);
		if (t != null) {
			// hit - negate the ne
			annotateNegation(aJCas, s, ne, t, true, false);
		} else {
			// look for POST rule after the ne, without any intervening stop
			// tags
			t = this.findTokenByTag("[POST]", new String[] { "[CONJ]",
					"[PSEU]", "[PREN]", "[PREP]", "[POSP]" }, false,
					neRelStart, neRelEnd, tokens);
			if (t != null) {
				annotateNegation(aJCas, s, ne, t, true, false);
			} else if (this.checkPossibilities || this.negatePossibilities) {
				// check possibles
				t = this.findTokenByTag("[PREP]", new String[] { "[CONJ]",
						"[PSEU]", "[PREN]", "[POST]", "[POSP]" }, true,
						neRelStart, neRelEnd, tokens);
				if (t != null) {
					annotateNegation(aJCas, s, ne, t, false, true);
				} else {
					t = this.findTokenByTag("[POSP]", new String[] { "[CONJ]",
							"[PSEU]", "[PREN]", "[POST]", "[PREP]" }, false,
							neRelStart, neRelEnd, tokens);
					if (t != null)
						annotateNegation(aJCas, s, ne, t, true, true);
				}
			}
		}
	}

	private void checkNegation2(JCas aJCas, Sentence s,
			IdentifiedAnnotation ne, boolean negPoss) {
		// Sorter s = new Sorter();
		String sToReturn = "";
		String sScope = "";
		// String sentencePortion = "";
		// ArrayList sortedRules = null;

		String filler = "_";
		// boolean negationScope = true;

		// Sort the rules by length in descending order.
		// Rules need to be sorted so the longest rule is always tried to match
		// first.
		// Some of the rules overlap so without sorting first shorter rules
		// (some of them POSSIBLE or PSEUDO)
		// would match before longer legitimate negation rules.
		//

		// There is efficiency issue here. It is better if rules are sorted by
		// the
		// calling program once and used without sorting in GennegEx.
		// sortedRules = this.rules;

		// Process the sentence and tag each matched negation
		// rule with correct negation rule tag.
		//
		// At the same time check for the phrase that we want to decide
		// the negation status for and
		// tag the phrase with [PHRASE] ... [PHRASE]
		// In both the negation rules and in the phrase replace white space
		// with "filler" string. (This could cause problems if the sentences
		// we study has "filler" on their own.)

		// Sentence needs one character in the beginning and end to match.
		// We remove the extra characters after processing.
		// vng String sentence = "." + sentenceString + ".";
		String sentence = "." + s.getCoveredText() + ".";

		// Tag the phrases we want to detect for negation.
		// Should happen before rule detection.
		// vng String phrase = phraseString;
		String phrase = ne.getCoveredText();
		Pattern pph = Pattern.compile(phrase.trim(), Pattern.CASE_INSENSITIVE);
		Matcher mph = pph.matcher(sentence);
		CharBuffer buf = CharBuffer.wrap(sentence.toCharArray());

		while (mph.find() == true) {
			sentence = mph.replaceAll(" [PHRASE]"
					+ mph.group().trim().replaceAll(" ", filler) + "[PHRASE]");
		}

		for (NegexRule rule : this.listNegexRules) {
			Matcher m = rule.getPattern().matcher(sentence);
			while (m.find() == true) {
				sentence = m.replaceAll(" " + rule.getTag()
						+ m.group().trim().replaceAll(" ", filler)
						+ rule.getTag() + " ");
			}
		}

		// Exchange the [PHRASE] ... [PHRASE] tags for [NEGATED] ... [NEGATED]
		// based of PREN, POST rules and if flag is set to true
		// then based on PREP and POSP, as well.

		// Because PRENEGATION [PREN} is checked first it takes precedent over
		// POSTNEGATION [POST].
		// Similarly POSTNEGATION [POST] takes precedent over POSSIBLE
		// PRENEGATION [PREP]
		// and [PREP] takes precedent over POSSIBLE POSTNEGATION [POSP].

		Pattern pSpace = Pattern.compile("[\\s+]");
		String[] sentenceTokens = pSpace.split(sentence);
		StringBuilder sb = new StringBuilder();

		// Check for [PREN]
		for (int i = 0; i < sentenceTokens.length; i++) {
			sb.append(" " + sentenceTokens[i].trim());
			if (sentenceTokens[i].trim().startsWith("[PREN]")) {

				for (int j = i + 1; j < sentenceTokens.length; j++) {
					if (sentenceTokens[j].trim().startsWith("[CONJ]")
							|| sentenceTokens[j].trim().startsWith("[PSEU]")
							|| sentenceTokens[j].trim().startsWith("[POST]")
							|| sentenceTokens[j].trim().startsWith("[PREP]")
							|| sentenceTokens[j].trim().startsWith("[POSP]")) {
						break;
					}

					if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
						sentenceTokens[j] = sentenceTokens[j].trim()
								.replaceAll("\\[PHRASE\\]", "[NEGATED]");
					}
				}
			}
		}

		sentence = sb.toString();
		pSpace = Pattern.compile("[\\s+]");
		sentenceTokens = pSpace.split(sentence);
		StringBuilder sb2 = new StringBuilder();

		// Check for [POST]
		for (int i = sentenceTokens.length - 1; i > 0; i--) {
			sb2.insert(0, sentenceTokens[i] + " ");
			if (sentenceTokens[i].trim().startsWith("[POST]")) {
				for (int j = i - 1; j > 0; j--) {
					if (sentenceTokens[j].trim().startsWith("[CONJ]")
							|| sentenceTokens[j].trim().startsWith("[PSEU]")
							|| sentenceTokens[j].trim().startsWith("[PREN]")
							|| sentenceTokens[j].trim().startsWith("[PREP]")
							|| sentenceTokens[j].trim().startsWith("[POSP]")) {
						break;
					}

					if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
						sentenceTokens[j] = sentenceTokens[j].trim()
								.replaceAll("\\[PHRASE\\]", "[NEGATED]");
					}
				}
			}
		}
		sentence = sb2.toString();

		// If POSSIBLE negation is detected as negation.
		// negatePossible being set to "true" then check for [PREP] and [POSP].
		if (negPoss == true) {
			pSpace = Pattern.compile("[\\s+]");
			sentenceTokens = pSpace.split(sentence);

			StringBuilder sb3 = new StringBuilder();

			// Check for [PREP]
			for (int i = 0; i < sentenceTokens.length; i++) {
				sb3.append(" " + sentenceTokens[i].trim());
				if (sentenceTokens[i].trim().startsWith("[PREP]")) {

					for (int j = i + 1; j < sentenceTokens.length; j++) {
						if (sentenceTokens[j].trim().startsWith("[CONJ]")
								|| sentenceTokens[j].trim()
										.startsWith("[PSEU]")
								|| sentenceTokens[j].trim()
										.startsWith("[POST]")
								|| sentenceTokens[j].trim()
										.startsWith("[PREN]")
								|| sentenceTokens[j].trim()
										.startsWith("[POSP]")) {
							break;
						}

						if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
							sentenceTokens[j] = sentenceTokens[j].trim()
									.replaceAll("\\[PHRASE\\]", "[POSSIBLE]");
						}
					}
				}
			}
			sentence = sb3.toString();
			pSpace = Pattern.compile("[\\s+]");
			sentenceTokens = pSpace.split(sentence);
			StringBuilder sb4 = new StringBuilder();

			// Check for [POSP]
			for (int i = sentenceTokens.length - 1; i > 0; i--) {
				sb4.insert(0, sentenceTokens[i] + " ");
				if (sentenceTokens[i].trim().startsWith("[POSP]")) {
					for (int j = i - 1; j > 0; j--) {
						if (sentenceTokens[j].trim().startsWith("[CONJ]")
								|| sentenceTokens[j].trim()
										.startsWith("[PSEU]")
								|| sentenceTokens[j].trim()
										.startsWith("[PREN]")
								|| sentenceTokens[j].trim()
										.startsWith("[PREP]")
								|| sentenceTokens[j].trim()
										.startsWith("[POST]")) {
							break;
						}

						if (sentenceTokens[j].trim().startsWith("[PHRASE]")) {
							sentenceTokens[j] = sentenceTokens[j].trim()
									.replaceAll("\\[PHRASE\\]", "[POSSIBLE]");
						}
					}
				}
			}
			sentence = sb4.toString();
		}

		// Remove the filler character we used.
		sentence = sentence.replaceAll(filler, " ");

		// Remove the extra periods at the beginning
		// and end of the sentence.
		sentence = sentence.substring(0, sentence.trim().lastIndexOf('.'));
		sentence = sentence.replaceFirst(".", "");

		// Get the scope of the negation for PREN and PREP
		if (sentence.contains("[PREN]") || sentence.contains("[PREP]")) {
			int startOffset = sentence.indexOf("[PREN]");
			if (startOffset == -1) {
				startOffset = sentence.indexOf("[PREP]");
			}

			int endOffset = sentence.indexOf("[CONJ]");
			if (endOffset == -1) {
				endOffset = sentence.indexOf("[PSEU]");
			}
			if (endOffset == -1) {
				endOffset = sentence.indexOf("[POST]");
			}
			if (endOffset == -1) {
				endOffset = sentence.indexOf("[POSP]");
			}
			if (endOffset == -1 || endOffset < startOffset) {
				endOffset = sentence.length() - 1;
			}
			sScope = sentence.substring(startOffset, endOffset + 1);
		}

		// Get the scope of the negation for POST and POSP
		if (sentence.contains("[POST]") || sentence.contains("[POSP]")) {
			int endOffset = sentence.lastIndexOf("[POST]");
			if (endOffset == -1) {
				endOffset = sentence.lastIndexOf("[POSP]");
			}

			int startOffset = sentence.lastIndexOf("[CONJ]");
			if (startOffset == -1) {
				startOffset = sentence.lastIndexOf("[PSEU]");
			}
			if (startOffset == -1) {
				startOffset = sentence.lastIndexOf("[PREN]");
			}
			if (startOffset == -1) {
				startOffset = sentence.lastIndexOf("[PREP]");
			}
			if (startOffset == -1) {
				startOffset = 0;
			}
			sScope = sentence.substring(startOffset, endOffset);
		}

		// Classify to: negated/possible/affirmed
		if (sentence.contains("[NEGATED]")) {
			sentence = sentence + "\t" + "negated" + "\t" + sScope;
		} else if (sentence.contains("[POSSIBLE]")) {
			sentence = sentence + "\t" + "possible" + "\t" + sScope;
		} else {
			sentence = sentence + "\t" + "affirmed" + "\t" + sScope;
		}

		sToReturn = sentence;
		System.out.println(sToReturn);
	}

	/**
	 * set the certainty/confidence flag on a named entity, and add a negation
	 * context annotation.
	 * 
	 * @param aJCas
	 * @param s
	 *            used to figure out text span
	 * @param ne
	 *            the certainty/confidence will be set to -1
	 * @param t
	 *            the token
	 * @param fSetCertainty
	 *            should we set the certainty (true) or confidence (false)
	 */
	private void annotateNegation(JCas aJCas, Sentence s, Annotation anno,
			NegexToken t, boolean negated, boolean possible) {
		if (anno instanceof IdentifiedAnnotation) {
			IdentifiedAnnotation ne = (IdentifiedAnnotation) anno;
			if (!storeAsInterval) {
				if (possible)
					ne.setConfidence(-1);
				if (negated || (this.negatePossibilities && possible))
					ne.setPolarity(-1);
			} else {
				ne.setPolarity(negated || possible ? -1 : 0);
				float confidence = negated ? -1 : 1;
				if (possible)
					confidence *= 0.5;
				ne.setConfidence(confidence);
			}
		} else {
			try {
				BeanUtils.setProperty(anno, "negated", negated);
				BeanUtils.setProperty(anno, "possible", possible);
			} catch (IllegalAccessException iae) {
				log.error("error negating annotation", iae);
			} catch (InvocationTargetException e) {
				log.error("error negating annotation", e);
			}
		}
		ContextAnnotation nec = new ContextAnnotation(aJCas);
		nec.setBegin(s.getBegin() + t.getStart() - 1);
		nec.setEnd(s.getBegin() + t.getEnd() - 1);
		nec.setScope(t.getTag());
		nec.setFocusText(anno.getCoveredText());
		nec.addToIndexes();
	}

}
