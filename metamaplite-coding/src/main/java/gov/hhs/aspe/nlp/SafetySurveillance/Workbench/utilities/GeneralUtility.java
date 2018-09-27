package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.apache.commons.lang.StringUtils;

import gov.hhs.aspe.nlp.SafetySurveillance.Corpus.TLink;
import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;


/**
 * 
 * @author Guangfan.Zhang
 *
 * Engility Corporation
 * May 20, 2017
 * 
 * Class for utility functions (static)
 */
public class GeneralUtility {
/**
 * Get NLP component names in a category, for example, tokenizer.  
 * @param category: an NLP component category, tokenizer, POS tagger, NER, etc.
 * @param classesSet: the class name array list containing NLP component names
 * @return
 */
	public static ArrayList<String> getNLPComponentsByCategory(String category, ArrayList<String> classesSet){
		ArrayList<String> listComponents = new ArrayList<String>();
		category = category.toLowerCase();
		
		for(String s:classesSet){
			if(s.toLowerCase().contains(category)){
				listComponents.add(s);
			}
		}
		return listComponents;
	}
	/**
	 * Read a CSV file
	 * @param csvFile: CSV file name
	 * @param cvsSplitBy: split character
	 * @return
	 */
	public static String csvReader(String csvFile, String cvsSplitBy){
		String line = "";
		String res = "";
		BufferedReader br = null;
        try {

        	 br = new BufferedReader(new FileReader(csvFile));
            while (( line = br.readLine()) != null) {
                res = res + line.replaceAll(cvsSplitBy, "\t");
                res += "\r\n" ;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
	}
	
	public static String encodeURIComponent(String s) {
	    String result;

//	    try {
//	    	result = s.replaceAll("\\+", "%2B");
//	    	result = URLEncoder.encode(result, "UTF-8");
	    	result = s.replaceAll(" ", "%20");
	    	result = result.replaceAll("%2C", ",");
//	    	result = result.replaceAll("%2C", ",");
	        result = result
//	                .replaceAll("\\+", "%20")
//	                .replaceAll("!", "\\%21")
	                .replaceAll("'", "\\%27")
	                .replaceAll("\"", "\\%22");
//	                .replaceAll(";", "\\%3B" )
//	                .replaceAll("\\(", "\\%28")
//	                .replaceAll("-", "\\%2D")
//	                .replaceAll( "\\)", "\\%29")
//	                .replaceAll("~", "\\%7E");
//	        result = URLEncoder.encode(s, "UTF-8")
//	                .replaceAll("\\+", "%2B")
//	                .replaceAll("\\%21", "!")
//	                .replaceAll("\\%27", "'")
//	                .replaceAll("\\%2B", ";")
//	                .replaceAll("\\%28", "(")
//	                .replaceAll("\\%29", ")")
//	                .replaceAll("\\%7E", "~");
//	    } catch (UnsupportedEncodingException e) {
//	        result = s;
//	    }

	    return result;
	}
	public static void sortByFieldInHashMap(HashMap<String, Object> termMapping, String field){
		ArrayList<String> sortedKeys = new ArrayList<String>();
		ArrayList<Integer> sortedV = new ArrayList<Integer>();
		HashMap<String, Integer> vMapping = new HashMap<String, Integer>();
		for(Entry<String, Object>entry:termMapping.entrySet()){
			sortedKeys.add(entry.getKey());
			
		}
		
	}
	public static void getPositionMapping(HashMap<String, Object> termMapping, String field){
		ArrayList<String> sortedKeys = new ArrayList<String>();
		ArrayList<Integer> sortedV = new ArrayList<Integer>();
		HashMap<String, Integer> vMapping = new HashMap<String, Integer>();
		for(Entry<String, Object>entry:termMapping.entrySet()){
			sortedKeys.add(entry.getKey());
			
		}
		
	}
	public static String readFileToJTextArea(String fileName){
		FileReader f;
		 StringBuilder sb = new StringBuilder();
		try {
			f = new FileReader(fileName);
			BufferedReader b = new BufferedReader(f);
			boolean eof = false;
			  
			while(! eof)
			  {
			  String lineIn = b.readLine();
			  if(lineIn == null)
			    {
			    eof = true;
			    }
			  else
			    {
			    sb.append(lineIn + System.getProperty("line.separator"));
			    }
			  }
						
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	/**
	 * Sort term mapping based on begin location
	 * @return
	 */
	public static LinkedHashMap<String, ArrayList<Term>> sortTerms(HashMap<String, ArrayList<Term>> terms){
		LinkedHashMap<String, ArrayList<Term>> outputMapping = new LinkedHashMap<String, ArrayList<Term>>();
		ArrayList<Integer> fromArray = new ArrayList<Integer>();
		HashMap<Integer, ArrayList<String>> mapping = new HashMap<Integer, ArrayList<String>>();
		HashSet<Integer> fromSet = new HashSet<Integer>();
		for(Entry<String, ArrayList<Term>>entry: terms.entrySet()){
			//same from value
			ArrayList<String> indexList = null;
			String index = entry.getKey();
			String[] items = index.split("-");
			int from = Integer.parseInt(items[items.length-1]);
			fromSet.add(from);
			if(mapping.containsKey(from))
			{
				indexList = mapping.get(from);
			}
			else
			{
				indexList = new ArrayList<String>();
			}
			
			indexList.add(index);
			mapping.put(from, indexList);
		}
		fromArray = new ArrayList<Integer>(fromSet);
		
		Collections.sort(fromArray);
		for(int i = 0; i < fromArray.size(); i++)
		{
			ArrayList<String> mappings = mapping.get(fromArray.get(i));
			ArrayList<Term> termMappings = null;
			
			termMappings = new ArrayList<Term>();
			
			for(int j = 0; j < mappings.size(); j++)
			{
				termMappings.addAll(terms.get(mappings.get(j)));
			}
			outputMapping.put(fromArray.get(i) - 1+"", termMappings);
		}
		return outputMapping;
		
	}
	
	public static HashMap<String, ArrayList<Term>> sortTermsByWord(HashMap<String, ArrayList<Term>> terms){
		HashMap<String, ArrayList<Term>> outputMapping = new LinkedHashMap<String, ArrayList<Term>>();
		HashMap<Integer, ArrayList<String>> mapping = new HashMap<Integer, ArrayList<String>>();
		HashSet<Integer> fromSet = new HashSet<Integer>();
		for(Entry<String, ArrayList<Term>>entry: terms.entrySet()){
			//same from value
			String keyAll = entry.getKey();
			String[] items = keyAll.split("-");
			String key = items[0];
			ArrayList<Term> curTerms = entry.getValue();
			
			for(Term term:curTerms){
				String words = term.word.toUpperCase();
				ArrayList<Term> termList = null;
				String[] wordArray = words.split("\\s+");
//				if(wordArray.length > 1)
//					System.out.println("Complicated words: " + words);
				for(String word:wordArray){
					if(outputMapping.containsKey(word))
					{
						termList = outputMapping.get(word);
					}
					else
					{
						termList = new ArrayList<Term>();
					}
					termList.add(term);
//					if(word.contains("NCREASE"))
//						System.out.println(word);
					outputMapping.put(word, termList);
				}
			}
		}
		return outputMapping;
		
	}
public static HashMap<String, ArrayList<String[]>> getTermClass(ArrayList<Term> terms){
		
		HashMap<String, ArrayList<String[]>> outputs = new HashMap<String, ArrayList<String[]>>();
		for(int i = 0; i < terms.size(); i++){
			Term curTerm = terms.get(i);
			String word = curTerm.word;
			String[] items = word.split("\\s+");
			for(int j = 0; j < items.length; j++){
				ArrayList<String[]> lstCls = null;
				if(outputs.containsKey(items[j])){
					lstCls = outputs.get(items[j]);
				}
				else
					lstCls = new ArrayList<String[]>();
				lstCls.add(new String[]{curTerm.strCls, curTerm.strType, curTerm.ontology});
				outputs.put(items[j].toUpperCase(), lstCls);
			}
		}
		return outputs;
	}
	/**
	 * Get orders of string2 in str1
	 * @param str1
	 * @param string2
	 * @return
	 */
	public static ArrayList<Integer> getOrdersInString(String str1, String str2)
	{
		ArrayList<Integer> output = new ArrayList<Integer> ();
		String[] string2 = str2.toLowerCase().split("\\s+");
		String[] items = str1.toLowerCase().split("\\s+");
		ArrayList<String> itemList = new ArrayList<String>(Arrays.asList(items));
		
		for(String str:string2)
		{
			int i = itemList.indexOf(str.toLowerCase());
			output.add(i);
		}
		return output;
	}
	
	public static ArrayList<String> getAdjacentTerms(String str1, String str2){
		ArrayList<String> terms = new ArrayList<String>();
		ArrayList<Integer> orders = getOrdersInString(str1, str2);
		String[] string2 = str2.toLowerCase().split("\\s+");
		
		String curStr = string2[0];
		for(int i = 1; i < string2.length; i++){
			if (orders.get(i) - orders.get(i-1) > 1){
				terms.add(curStr);
				curStr = string2[i];
			}
			else
			{
				curStr = curStr + " " + string2[i];
			}
		}
		
		if(!curStr.equals(""))
			terms.add(curStr);
		
		return terms;
	}
	
	public static Set<String> getUniqueWords(String[] word){
		Set<String> set = new HashSet(Arrays.asList(word));
		int unique = set.size();
		
		return set;
	}
//	public static ArrayList<String> getClassesInPackage(String packageName){
//	
////	Reflections reflections = new Reflections(packageName, 
////		    new SubTypesScanner(false));
////	Reflections reflections = new Reflections(packageName); 
//		    
//	
////	Set<Class<? extends Object>> allClasses = 
////			reflections.getSubTypesOf(Object.class);
//	
//	List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
//	classLoadersList.add(ClasspathHelper.contextClassLoader());
//	classLoadersList.add(ClasspathHelper.staticClassLoader());
//
//	Reflections reflections = new Reflections(new ConfigurationBuilder()
//	    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
//	    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
//	    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));
//	
//	ArrayList<String> classesList = new ArrayList<String>();
//	Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
//	for(Class c:allClasses){
//		String name = c.getName();
//		if(name.toLowerCase().contains("token"))
//			System.out.println(name);
//		if(!name.toLowerCase().contains("_type"))
//			if(name.contains("$"))
//				classesList.add(c.getName().substring(0, name.indexOf('$')));
//			else
//				classesList.add(c.getName());
//				
//	}
//	return classesList;
//}

	public static void CheckFileProcessed(ArrayList<String> selectedFiles, String strDir){
		File dir = new File(strDir);

		if(!dir.exists())
			return;

		ArrayList<String> fileNames = new ArrayList<String>();
		String fileName = selectedFiles.get(0);
		int indexFileName = selectedFiles.get(0).lastIndexOf(File.separator);
		String path = selectedFiles.get(0).substring(0, indexFileName + 1);
		for(int i = 0; i < selectedFiles.size(); i++){
			fileNames.add(selectedFiles.get(i).substring(indexFileName+1));
		}
		File[] files = dir.listFiles();
		for(File f:files){
			String name = f.getName();
			int pos = name.lastIndexOf(".");
			name = name.substring(0, pos);
			if(fileNames.contains(name))
			{
				System.out.println(path + name + " has been processed. Skipped...");
				selectedFiles.remove(path + name);
			}
		}
	}
	public static HashMap<String, ArrayList<Term>> convertToTermList(ArrayList<Term> allTerms){
		HashMap<String, ArrayList<Term>> classifiedOutputs = 
				new HashMap<String, ArrayList<Term>>();
		
		for(int i = 0; i < allTerms.size(); i++){
			Term term = allTerms.get(i);
			if(term.mentionType!=null ){
				//		if(term.mentionType == "True"){
				//			System.out.println(allTerms.get(i).word);
//				term.ontology = "LibSVM";
				String key = term.word + "-" + term.ontology + "-" + term.from;
				ArrayList<Term> terms = null;
				if(classifiedOutputs.containsKey(key)){
					terms = classifiedOutputs.get(key);
				}
				else
					terms = new ArrayList<Term>();

				terms.add(term);
				classifiedOutputs.put(key, terms);
			}
		}
		return classifiedOutputs;
	}
	
	public static ArrayList<Term> MergeTerm(ArrayList<Term> allTerms, String report, 
			String ontology, int range){
		boolean byGap = false;
		int gap = 3;
		
		ArrayList<Term> mergedTerms = 
				new ArrayList<Term>();
		Collections.sort(allTerms, new TermComparator());
		boolean termExists = true;
		if(allTerms == null){
			termExists = false;
		}
		if(allTerms.size() == 0 )
		{
			termExists = false;
		}
		if(!termExists)
		{
			return mergedTerms;
		}
		Term prevTerm = allTerms.get(0).copy();
		int prevFrom = prevTerm.from;
		int prevTo = prevTerm.to;
		String prevMention = prevTerm.mentionType;
		boolean merged = false;
		if(allTerms.get(allTerms.size()-1).sentenceID == 0)
		{
			byGap = true;
		}
//		System.out.println(prevTerm.word + " " + prevFrom + " ---- " + prevTo);
		for(int i = 1; i < allTerms.size(); i++){
			Term term = allTerms.get(i);
//			System.out.println(term.word + " " + term.from + " ---- " + term.to);
//			if(term.word.contains("chronic"))
//				System.out.println(term.word);
			if(term.mentionType!=null ){
				//assume terms are sorted already based on the from location
				int from = term.from;
//				if (from == 3172)
//					System.out.println(term.word);
				int to = term.to;
				int sentenceID = term.sentenceID;
				if(term.mentionType.equals(prevTerm.mentionType)){
//					if(prevTo > from)
//						System.out.println(prevTo + " --- " + from);
					
					boolean mergedTerm = false; 
					if(byGap){
						if(from - prevTo <= gap) //treated as one term, in conjunction with the previous term
						{
							mergedTerm = true;
						}
					}
					else{
						if(sentenceID == prevTerm.sentenceID)
						{
							mergedTerm = true;
						}
					}
					if(mergedTerm)
					{
						if(to > prevTerm.to)
							prevTerm.to = to;
						prevTerm.word = prevTerm.word  + " " + term.word;
						
//						if(prevTerm.word.equals("diabetes medication diabetes medication"))
//							System.out.println(prevTerm.word);
//						prevTerm.word = report.substring(prevTerm.from, prevTerm.to);
						prevTerm.context = GeneralUtility.getContext(report, 
								prevTerm.word, prevTerm.from, to, range);
						merged = false;
						prevTo = to;
					}
					else
					{
						mergedTerms.add(prevTerm);
						merged = true;
						prevTerm = term.copy();
						prevTo = term.to;
					}
				}
				else{
					mergedTerms.add(prevTerm);
					merged = true;
					prevTerm = term.copy();
					prevTo = term.to;
				}
			}
		}
		if(merged)
			mergedTerms.add(allTerms.get(allTerms.size()-1).copy());
		else{
			mergedTerms.add(prevTerm);
		}
//		for(Term term:mergedTerms)
//		{
//			System.out.println(term.from + " - " + term.to + "; " + term.mentionType + "; " + term.word  );
//		}
		return mergedTerms;
	}
	
	public static Term findTerm(ArrayList<Term> terms, int from, int to){
		boolean found = false;
		
		Term termFound = null;
		for(Term t: terms){
			
			int fromT = t.from;
			int toT = t.to;
			if(fromT > to)
				continue;
			else if (fromT == to)
			{
				found = true;
				termFound = t;
				break;
			}
			else{
				if(toT < from)
					continue;
				else 
				{
					found = true; 
					termFound = t;
					break;
				}
			}
		}
		return termFound;
	}
	
	public static Term findTermInTLink(ArrayList<Term> terms, int from, int to){
		boolean found = false;
		
		Term termFound = null;
		for(Term t: terms){
			TLink tlink= t.tlink;
			int fromT = tlink.fromText;
			int toT = tlink.toText;
			if(fromT > to)
				continue;
			else if (fromT == to)
			{
				found = true;
				termFound = t;
				break;
			}
			else{
				if(toT < from)
					continue;
				else 
				{
					found = true; 
					termFound = t;
					break;
				}
			}
		}
		return termFound;
	}
	public static Term findWordInTerm(ArrayList<Term> terms, int from, int to, String word){
		boolean found = false;
		
		Term termFound = null;
		try {
			for(Term t: terms){
				
				int fromT = t.from;
				int toT = t.to;
				if(fromT > to)
					continue;
				else if (fromT == to)
				{
					if(!containsAWord(t.word, word))
						continue;
					
					found = true;
					termFound = t;
					break;
				}
				else{
					if(toT < from)
						continue;
					else 
					{
						t.word = t.word.replaceAll("-", " ");
						if(containsAWord(t.word, word))
						{
							found = true; 
							termFound = t;
							try {
//								t.word  = t.word.substring(0,  from) + t.word.substring(from).replaceFirst(word, StringUtils.repeat("*", word.length()));
								t.word = GeneralUtility.replaceWordWithHyphen(t.word, word);
//								t.word  = t.word.replaceFirst(word, StringUtils.repeat("*", word.length()));

							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
	//						t.word = t.word.replace(word,  "");
							break;
						}
						else if(word.contains("-"))
						{
							String[] items = word.split("-");
							for(String item:items) {
								if(containsAWord(t.word, item))
								{
									found = true;
									try {
										t.word  = t.word.replaceFirst(item, StringUtils.repeat("_", item.length()));
									}
									catch(Exception e)
									{
										e.printStackTrace();
									}
									termFound = t;
									break;
								}
							}
							if(found == false)
							{
								continue;
							}
						}
							
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return termFound;
	}
	public static boolean findExactTerm(ArrayList<Term> terms, int from, int to){
		boolean found = false;
		int tolerance = 2;
		for(Term t: terms){
			int fromT = t.from;
			int toT = t.to;
			if(Math.abs(from - fromT ) <= tolerance && Math.abs(toT - to)< tolerance)
			{
				found = true;
				break;
			}
		}
		return found;
	}
	public static boolean findExactTermInTLink(ArrayList<Term> terms, int from, int to){
		boolean found = false;
		
		int tolerance = 2;
		for(Term t: terms){
			TLink tlink = t.tlink;
			int fromT = tlink.fromText;
			int toT = tlink.toText;
			if(Math.abs(from  -fromT) < tolerance && Math.abs(toT - to)< tolerance)
			{
				found = true;
				break;
			}
		}
		return found;
	}
	public static Term findTerm(HashMap<String, ArrayList<Term>> termMapping, int from, int to){
		boolean found = false;
		Term tFound = null;
		for(Entry<String, ArrayList<Term>>entry:termMapping.entrySet())
		{
			ArrayList<Term> terms = entry.getValue();
			for(Term t: terms){
				int fromT = t.from;
				int toT = t.to;
				
				if(fromT > to)
					continue;
				else if (fromT == to)
				{
					found = true;
					tFound =t; 
					break;
				}
				else{
					if(toT < from)
						continue;
					else 
					{
						found = true; 
						tFound =t; 
						break;
					}
				}
			}
			if(found)
				break;
		}
		
		return tFound;
	}
	
	public static ArrayList<Term> findRelationTermInGold(ArrayList<Term> terms, int from, int to, int fromTemporal, int toTemporal){
		boolean found = false;
		ArrayList<Term> foundE = new ArrayList<Term>();
		for(Term t: terms){
			if(t.eventTemporalAssociation!=2)
				continue;
			TLink tlink = t.tlink;
			t.mentionType = tlink.linkType;
			int fromE = tlink.fromText;
			int toE = tlink.toText;
			
			int fromT = tlink.fromTemporal;
			int toT = tlink.toTemporal;
			if(isSpanOverlapped(fromE, toE, from, to)){
				if(isSpanOverlapped(fromT, toT, fromTemporal, toTemporal))
				{
					found = true;
					foundE.add(t);
				}
			}
//			if(!t.text.contains(word))
//				continue;
			
			//??????????????????????????????????????????????????????????????????????????
			
		}
		if(found)
			return foundE;
		else
			return null;
	}
	public static Term findBestRelationTermInGold(ArrayList<Term> terms, Term gold){
		boolean found = false;
		
		String mentionType = gold.tlink.linkType;
		int commonWordSize = 0;
		String strDate = gold.tlink.refText;
		String strEvent = gold.tlink.coreText;
		Term termFound = null;
		for(Term t: terms){
			if(t.eventTemporalAssociation!=2)
				continue;
			
			if(!t.tlink.linkType.equals(mentionType))
			{
				if(termFound == null )
					termFound = t;
				
				continue;
			}
			
			Set<String> commonEventWords = commonWords(t.tlink.coreText, strEvent);
			Set<String> commonDateWord = commonWords(t.tlink.refText, strDate);
			int curSize = commonEventWords.size() + commonDateWord.size(); 
			if(curSize >= commonWordSize)
			{
				termFound = t;
				commonWordSize =curSize; 
			}
		}
		return termFound;
	}
	public static Term findBestTermInGold(ArrayList<Term> terms, Term gold){
		boolean found = false;
		
		String mentionType = gold.mentionType;
		int commonWordSize = 0;
		String strEvent = gold.word;
		Term termFound = null;
		for(Term t: terms){
			if(t.eventTemporalAssociation==2)
				continue;
			
			if(!t.mentionType.equals(mentionType))
			{
				if(termFound == null )
					termFound = t;
				continue;
			}
			
			Set<String> commonEventWords = commonWords(t.word, strEvent);
			int curSize = commonEventWords.size();
			if(curSize >= commonWordSize)
			{
				termFound = t;
				commonWordSize =curSize; 
			}
		}
		return termFound;
	}
	public static Term findExactRelationTermInGold(ArrayList<Term> terms, int from, int to, int fromTemporal, int toTemporal, String word){
		boolean found = false;
		Term foundE = null;
		int tolerance = 15;
		for(Term t: terms){
			if(t.eventTemporalAssociation!=2)
				continue;
			TLink tlink = t.tlink;
			
			int fromE = tlink.fromText;
			int toE = tlink.toText;
			
			int fromT = tlink.fromTemporal;
			int toT = tlink.toTemporal;
			
			if(Math.abs(fromE - from) <= tolerance && Math.abs(fromT - fromTemporal ) <= tolerance 
					&& Math.abs(toE - to) <= tolerance && Math.abs(toT - toTemporal) <= tolerance){
					found = true;
					foundE = t;
					break;
			}
//			if(!t.text.contains(word))
//				continue;
			
			//??????????????????????????????????????????????????????????????????????????
			
		}
		return foundE;
	}
	
	public static boolean isSpanOverlapped(int from, int to, int from2, int to2){
		boolean found = false; 
		
		if (from2 == to)
		{
			found = true;
		} 
		else if(from2 < to){
			if(to2 >= from)
			{
				found = true; 
			}
		}
		return found;
	}

	public static Term findTermInGold(ArrayList<Term> terms, int from, int to, String word){
		boolean found = false;
		Term tFound = null;
		for(Term t: terms){
			int fromT = t.from;
			int toT = t.to;
			
//			if(!t.text.contains(word))
//				continue;
			
			if(fromT > to)
				continue;
			else if (fromT == to)
			{
				found = true;
				tFound = t;
				break;
			}
			else{
				if(toT < from)
					continue;
				else 
				{
					found = true; 
					tFound = t;
					break;
				}
			}
		}
		return tFound;
	}
	public static ArrayList<Term> findTermInGoldMultiple(ArrayList<Term> terms, 
			int from, int to, String word){
		boolean found = false;
		ArrayList<Term> foundE = new ArrayList<Term>();
		for(Term t: terms){
			int fromT = t.from;
			int toT = t.to;
			found = false;
			
//			if(!t.text.contains(word))
//				continue;
			
			if(fromT > to)
				continue;
			else if (fromT == to)
			{
				found = true;
			}
			else{
				if(toT < from)
					continue;
				else 
				{
					found = true; 
				}
			}
			
			if(found)
			{
				foundE.add(t);

//				String[] words = t.word.split("[,;.\\s]+");
//				boolean foundTrue = false;
//				if(t.word.contains("autism"))
//					System.out.println(t.word);
//				for(String w:words)
//				{
//					if(GeneralUtility.containsAWord(word, w))
//					{
//						foundE.add(t);
//						foundTrue = true;
//						break;
//					}
//				}
//				if(foundTrue == false)
//				{
//					System.out.println(t.word);
//					System.out.println(word);
//					System.out.println("No match");
//					
//				}
//				
			}
		}
		if(foundE.size()>0)
			return foundE;
		else
			return null;
	}
	public static Term findExactTermInGold(ArrayList<Term> terms, int from, int to, String word){
		boolean found = false;
		Term foundE = null;
		int tolerance = 2;
		for(Term t: terms){
			int fromT = t.from;
			int toT = t.to;
			
			if (Math.abs(fromT - from ) <= tolerance && Math.abs(toT - to) <= tolerance)
			{
				foundE = t;
				found = true;
				break;
			}
		}
		return foundE;
	}

	public static ArrayList<Term> findExactTermInGoldMultiple(ArrayList<Term> terms, int from, int to, String word){
		boolean found = false;
		ArrayList<Term> foundE = new ArrayList<Term>(); 
		int tolerance = 0;
		for(Term t: terms){
			int fromT = t.from;
			int toT = t.to;
			
			if (Math.abs(fromT - from ) <= tolerance && Math.abs(toT - to) <= tolerance)
			{
				foundE.add(t);
				found = true;
			}
		}
		if(found)
			return foundE;
		else
			return null;
	}

	public static String setPrecision(String number, int decimal) {
		double nbr = Double.valueOf(number);
		int integer_Part = (int) nbr;
		double float_Part = nbr - integer_Part;
		long floating_point = Math.round(Math.pow(10, decimal) * float_Part);
		String str_Point = String.valueOf(floating_point);
		String final_nbr = "";
		if(decimal - str_Point.length() < 0 )
		{
			final_nbr = String.valueOf(integer_Part+1) + ".00";
		}
		else {
			String str1 = new String(new char[decimal - str_Point.length()]).replace("\0", "0"); 
			final_nbr = String.valueOf(integer_Part) + "." + str1 + str_Point;
		}
		return final_nbr;
	}
	
	public static int findTrueLocation(String txt, String word, int from, int to)
	{
		int trueFrom = 0;
		try{
			int initGuess = 999;
			txt = txt.substring(from, to);
			if(txt.contains(word))
			{
				initGuess = txt.indexOf(word);
			}
			word = word.replaceAll("[^A-Za-z0-9]", "");

//			System.out.println(word);
			String[] tokens = txt.toLowerCase().split("[-,:;.\\s]+");
			String regex = "\\b"+word+"\\b";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(txt);

			int index  = 0;
			while(matcher.find() == true){
				int end = matcher.end();
				index = matcher.start();
				break;
			}

			if(initGuess < index)
				index = initGuess;

			//		int index = txt.toLowerCase().indexOf(word.toLowerCase());
			trueFrom = from + index;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return trueFrom;
	}
	
	public static void wordCount(HashMap<String, Integer> map, ArrayList<Term> terms){
		if(map == null)
			map = new HashMap<String, Integer>();
		
		for(Term t:terms)
		{
			String word = t.word.toLowerCase();
			if(word.equals(""))
			{
				System.out.println(word);
			}
			if(map.containsKey(word))
			{
				map.put(word, map.get(word)+1);
			}
			else
				map.put(word,  1);
		}
		
	}
	
	public static boolean CreateADir(String targetLocation){
	    boolean result = false;
		File targetDir = new File(targetLocation);
		if (!targetDir.exists()) {
		    try{
		    	boolean created = targetDir.mkdirs();
		    	
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		}
		return result;
	}
	
	public static boolean containsAWord(String str, String word){
		boolean contain = false;
		str = str.toLowerCase();
		word = word.toLowerCase();
		String[] tokens = str.toLowerCase().split("[-,:;.\\s]+");
		word = word.toLowerCase();
		for(String token:tokens){
			if(token.toLowerCase().equals(word))
			{
				contain = true;
				break;
			}
		}
		
		return contain;
	}

	public static Set<String> commonWords(String str, String word){
		String[] tokens1 = str.toLowerCase().split("[,;.\\s]+");
		String[] tokens2 = word.toLowerCase().split("[,;.\\s]+");
		Set<String> common = new HashSet<>(Arrays.asList(tokens1));
	    common.retainAll(new HashSet<>(Arrays.asList(tokens2)));

		return common;
	}
	
	public static Term getElementByID(ArrayList<Term> elements, String id){
		Term eFound = null;
		for(Term element:elements){
			String eid =element.id;
			if(eid.equals(id))
			{
				eFound = element;
				break;
			}
		}
		return eFound;
	}
	
	public static int compareDates(String strDate1, String strDate2)
	{
		int rel = 0; 
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
//			if(strDate1.contains("XX"))
//			{
//				System.out.println(strDate1);
//			}
				
			strDate1 = strDate1.replaceAll("XX",  "01");
			strDate2 = strDate2.replaceAll("XX",  "01");
			Date date1 = format.parse(strDate1);
			Date date2 = format.parse(strDate2);
			rel = date1.compareTo(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rel;
	}
	
	public static String joinTwoStrings(String str1, String str2)
	{
		String res = "";
		if(str1 == null && str2 == null)
			return  null;
		
		if(str1 ==null )
		{
			if(str2 != null)
			{
				res = str2;
			}
		}else
		{
			if(str2 == null )
				res = str1;
			else{
				str1 = str1.trim();
				str2 = str2.trim();
				
				if(str1.equals(""))
				{
					res = str2;
				}
				else
				{
					if(str2.equals(""))
						res = str1;
					else
						if(!str1.contains(str2))
							res = str1 + "; " + str2;
				}
			}
		}
		
		return res;
	}
	
	public static String combineHTTPContext(String context1, String context2){
		String res = context1;
		if(context1.contains("</html>"))
		{
			res = res.substring(0, res.indexOf("</html>"));
			if(context2.contains("<html>"))
			{
				res = res + context2.substring(context2.indexOf("<html>") + 6); 
			}
		}
		
		return res;
	}
	public static ArrayList<Term> getTermByType(ArrayList<Term> classifiedTerms, int type)
	{
		ArrayList<Term> resTerms = new ArrayList<Term>();
		for(Term t:classifiedTerms)
		{
			if(t.eventTemporalAssociation == type)
				resTerms.add(t);
		}
		return resTerms;
	}
	public static String checkDirectoryName(String dirName){
		if(!dirName.endsWith("\\") || dirName.endsWith("/"))
		{
			dirName += "/";
		}
		return dirName;
	}
	
	public static int findLocationInArray(ArrayList<Integer> fromList, int from){
	
		int loc = -1;
		int size = fromList.size();
		for(int i = size-1; i >=0; i--){
			if (from > fromList.get(i))
			{
				loc = i;
				break;
			}
		}
		return loc;
	}
	public static void setSplitPaneProperties(JSplitPane splitPane){
		
		splitPane.setContinuousLayout( true );
		splitPane.setOneTouchExpandable( true );
//		splitPane.setDividerLocation(0.1);
        
//        splitPane.setResizeWeight(0);
		try {
			   Component divider =
			     ((BasicSplitPaneUI)splitPane.getUI()).getDivider();
			       
			   divider.addMouseListener (new MouseAdapter() {
			     public void mouseClicked (MouseEvent event)
			     {
			       if (event.getClickCount() == 2) {
			    	   splitPane.resetToPreferredSizes();
//			         instance.repaint(); // This shouldn't be necessary!
			       } // if
			     } // mouseClicked
			   });
			 } 
		catch (ClassCastException e) {
			
		}
	}
	
	public static String getLastFolder(String fileName){
		String lastFolder = "";
		int loc = 0, loc1=-999, loc2=-999;
		if(fileName.contains("\\"))
		{
			loc1 = fileName.lastIndexOf("\\");
		}
		if(fileName.contains("/"))
		{
			loc2 = fileName.lastIndexOf("/");
		}
		
		if(loc1 < loc2)
			loc = loc2;
		else
			loc = loc1;
		
		fileName= fileName.substring(0, loc);
		if(fileName.contains("\\"))
		{
			loc1 = fileName.lastIndexOf("\\");
		}
		if(fileName.contains("/"))
		{
			loc2 = fileName.lastIndexOf("/");
		}
		
		if(loc1 < loc2)
			loc = loc2;
		else
			loc = loc1;
		
		lastFolder = fileName.substring(loc+1);
		return lastFolder;
		
	}
	
	public static String[] getSubFolders(String folderName){
		
		File file = new File(folderName);
		String[] subFolders = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});

		
		return subFolders;
	}
	public static String getContext(String rawText, String strCls, 
			int intFrom, int intTo, int range){
		String context;
		String strSep = ". ";
//		if(strCls.toLowerCase().contains("epatitis b vaccine"))
//		{	
//			System.out.println(strCls + ": " + intFrom);		
//			System.out.println(rawText.substring(intFrom));
//		}
		rawText = rawText.toLowerCase();
		int len = rawText.length();
		int start = 0, end = 0; 
		start = intFrom - range ;
		end = intTo + range;
		int startToFrom = 0;
		boolean isStart= false, isEnd= false;
		if(start < 0){
			start = 0; 
			isStart = true;
		}
		if(end > rawText.length() -2){
			end = rawText.length() ;
			isEnd = true;
		}
		if(start > rawText.length() -2){
			start = rawText.length() ;
			isEnd = true;
		}
		if(start == 0 )
			context = rawText.substring(0, end);
		else{
			int len1 = rawText.length();
			if(start-1 < 0 || end >= len1)
			{
//				System.out.println("not found in raw text: from " + (start -1) + " to " + end + "; length = " + len1);
			}
			context = rawText.substring(start-1, end);
		}
		startToFrom = intFrom - start; 

		if(context.length() > startToFrom && startToFrom > 0)
			if(context.substring(0, startToFrom).contains(strSep))
			{
				int indSep =context.indexOf(strSep);
				context = context.substring(context.indexOf(strSep) + 2);
			}
		if(!isStart)
			context = "..." + context;
		if(!isEnd)
			context = context + "...";
		int indContext = 0;
		//deal with multiple occurrences in the string
		boolean found = false;

		if(context.length() > range)
		{
			range = 1;
			
			indContext = context.toLowerCase().substring(range-1).indexOf(" " + strCls.toLowerCase() + " ");
			if(indContext == -1 )
			{
				indContext = context.toLowerCase().substring(range-1).indexOf(" " + strCls.toLowerCase());
				if(indContext >=0 )
				{
					indContext++;
				}
				else
				{
					indContext = context.toLowerCase().substring(range-1).indexOf(strCls.toLowerCase());
				}
			}
			else 
				indContext++;
			if(indContext>=0)
			{
				found = true;
				indContext = indContext + range-1;
			}
		
		}
		if(indContext == -1){
			indContext = context.toLowerCase().indexOf(" " + strCls.toLowerCase() + " ") +1 ;

			if(indContext == 0 )
			{
				indContext = context.toLowerCase().indexOf(" " + strCls.toLowerCase()) +1 ;
				if(indContext == 0 )
				{
					indContext = context.toLowerCase().indexOf( strCls.toLowerCase() + " ") +1 ;
					if(indContext == 0)
						indContext = -1;
				}
			}
		}
		
//		
//		while(context.toLowerCase().indexOf(strCls.toLowerCase()) != -1) {
//		     int index = context.toLowerCase().indexOf(strCls.toLowerCase());
//		     System.out.println(index);
//		     temp = temp.substring(index + 1);
//		}
//		
		if(indContext >= 0 )
		{
			if(indContext > 0)
				context = context.substring(0, indContext) + "<font color=red>" + context.substring(indContext, strCls.length()+indContext) + "</font>" 
						+ context.substring( strCls.length()+indContext);
			else
			{
				context = "<font color=red>" + context.substring(0, strCls.length()) + "</font>" + context.substring( strCls.length()+indContext);
			}
		}
//		else
//			System.out.println("Context is not found for " + strCls);
		context = "<html>" + context + "</html>";
		
		return context;
	}
	
	public static ArrayList<String> excudeFilesFromADirectory(String sourceDir, String excludeDir, boolean includeFileExtension){
		
		
		File dirSrc = new File(sourceDir);
		File dirExclude= new File(excludeDir);
		
		if(!dirSrc.exists()|| !dirExclude.exists())
			return null;
		
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<String> fileNamesExclude = new ArrayList<String>();
		
		if (!includeFileExtension)
		{
			String firstFile = dirSrc.list()[0];
			String fileExtension = firstFile.substring(firstFile.indexOf("." ));
			for(String f:dirSrc.list())
			{
				fileNames.add(f.substring(0, f.indexOf(".")));
			}
			for(String f:dirExclude.list())
			{
				fileNamesExclude.add(f.substring(0, f.indexOf(".")));
			}
			fileNames.removeAll(fileNamesExclude);
			for(int i = 0; i < fileNames.size(); i++)
			{
				String f = fileNames.get(i);
				f = f + fileExtension;
				fileNames.set(i,  f);
			}
		}
		else {
			fileNames = new ArrayList<String>(Arrays.asList(dirSrc.list()));
			fileNamesExclude = new ArrayList<String>(Arrays.asList(dirExclude.list()));
			fileNames.removeAll(fileNamesExclude);
		}
		
		return fileNames;
	}
	
	public static HashSet<String> getPossibleMethodInADirectory(String folderName, ArrayList<String> extNames, String divider){
		File folder = new File(folderName);
		
		if(!folder.exists())
			return null;
		
		String[] files = folder.list();
		
		HashSet<String> methods = new HashSet<String>();
	
		File f[] = getFilesInADirectory(folderName, extNames);
		for(File f1:f) {
			String fileName = f1.getName().toLowerCase();
			int indexDivider = 0;
			
			String method = "";
			indexDivider = fileName.lastIndexOf(divider);
			if(indexDivider < 0) 
			{
				continue;
			}
			method = fileName.substring(0, indexDivider);
			indexDivider = method.lastIndexOf(divider);
			if(indexDivider < 0) 
			{
				continue;
			}
			method = method.substring(indexDivider+1);
			methods.add(method);
		}
		return methods;
	}
	
	public static File[] getFilesInADirectory(String folderName, ArrayList<String> extNames){
		File folder = new File(folderName);
		
		if(!folder.exists())
			return null;
		
		String[] files = folder.list();
		
		ArrayList<String> methods = new ArrayList<String>();
	
		File f[] = (new File(folderName)).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				
				boolean accept = false;
				
				for(String ext: extNames)
				{
					if(name.toLowerCase().endsWith(ext.toLowerCase()))
					{
						accept = true;
						break;
					}
				}
				return accept;
			}
		});
		
		return f;
	}	
	public static ArrayList<String> getSERTERREL(String directory){
		ArrayList<String> types = null;
		File dir = new File(directory);
		
		if(!dir.exists())
			return null;
		types = new ArrayList<String>();
		if(new File(directory + "/SER").exists())
			types.add("SER");
		
		if(new File(directory + "/TER").exists())
			types.add("TER");
		
		if(new File(directory + "/REL").exists())
			types.add("REL");
		
		return types;
	}
	
	public static String getFirstWord(String name) {
		String res = name;
		if (name.contains("("))
		{
			int i = name.indexOf('(');
			res = name.substring(0, i);
		}
		if (name.contains(" "))
		{
			int i = name.indexOf(' ');
			res = name.substring(0, i);
		}
		
		return res;
		
	}
	public static String replaceWordWithHyphen(String newWord, String wordToFiind)
	{

		int locWord = newWord.indexOf(wordToFiind);

		if(newWord.length() > locWord + wordToFiind.length()) {
			if(newWord.charAt(locWord + wordToFiind.length() ) == '-' )
			{
				int locSpace = newWord.substring(locWord).indexOf(" ");
				if(locSpace > 0) {
					try {
						String curSingleWord = newWord.substring(locWord, locWord+ locSpace);
						newWord = newWord.replaceFirst(curSingleWord, StringUtils.repeat("_", curSingleWord.length()));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
					newWord = newWord.replaceFirst(wordToFiind, StringUtils.repeat("_", wordToFiind.length()));
			}
			if(locWord >= 1) {
				if(newWord.charAt(locWord -1) == '-')
				{
					int locSpace = newWord.substring(0, locWord).lastIndexOf(" ");
					if(locSpace > 0) {
						try {
							String curSingleWord = newWord.substring(locSpace+1, locWord + wordToFiind.length());
							newWord = newWord.replaceFirst(curSingleWord, StringUtils.repeat("_", curSingleWord.length()));
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				else {
					newWord = newWord.replaceFirst(wordToFiind, StringUtils.repeat("_", wordToFiind.length()));
				}
			}
			else {
				newWord = newWord.replaceFirst(wordToFiind, StringUtils.repeat("_", wordToFiind.length()));
			}
		}else {
			newWord = newWord.replaceFirst(wordToFiind, StringUtils.repeat("_", wordToFiind.length()));
		}
		return newWord;
	}
}
//http://data.bioontology.org/annotator?apikey=8b5b7825-538d-40e0-9e9e-5ab9274a9aeb&text=information%20has%20been%20received%20from%20a%20health%20professional%20concerning%20her%2060%20year%20old%20hospitalized%20mother%20with%20an%20allergy%20to%20duramorph%20and%20with%20a%20history%20of%20pneumonia%20nos,%20cardiac%20failure%20congestive%20and%20septic%20shock%20who%20on%204/30/02%20was%20vaccinated%20in%20her%20hip%20with%20pneumococcal%20vaccine%2023%20polyvalent.%20concomitant%20therapy%20included%20cefepime%20(maxipime)%20and%20gentamicin.%20the%20pt%27s%20daughter%20reported%20that%20her%20mother%20had%20been%20hospitalized%20since%204/6/02%20with%20pneumonia%20and%20complication%20of%20chf%20and%20septic%20shock.%20she%20reported%20that%20her%20mother%20seemed%20to%20have%20recovered%20from%20all%20complications%20and%20was%20fever%20free.%20on%204/30/02,%20she%20was%20administered%20pneumococcal%20vaccine%2023%20polyvalent%20in%20her%20hip.%20by%20that%20same%20evening,%20the%20pt%20was%20again%20febrile.%20the%20pt%20also%20had%20a%20severe%20local%20reaction%20to%20pneumococcal%20vaccine%2023%20polyvalent%20injection.%20it%20was%20reported%20that%20the%20injection%20site%20was%20so%20painful%20that%20the%20pt%20could%20not%20walk%20after%20receiving%20the%20injection.%20the%20pt%27s%20daughter%20reported%20that%20on%205/2/02,%20her%20mother%20was%20started%20on%202%20antibiotics%20that%20she%20%22thinks%22%20were%20maxipime%20and%20gentamicin.%20on%205/3/02,%20the%20pt%20still%20had%20a%20fever%20and%20now%20had%20a%20rash%20over%20most%20of%20her%20body.%20as%20of%205/6/02,%20the%20pt%20still%20had%20a%20fever%20and%20rash.%20the%20pt%27s%20highest%20temperature%20measurement%20was%20reported%20as%20102.5f.%20additional%20information%20had%20been%20received%20from%20a%20physician.%20the%20physician%20reported%20that%20during%20a%20%22complicated%20hospitalization%22,%20the%20pt%20with%20pneumonia,%20primary%20pulmonary%20hypertension%20and%20an%20uti%20received%20pneumococcal%20vaccine%2023%20polyvalent%20and%20then%20developed%20a%20fever%2024%20hours%20later.%20the%20pt%20recovered%20from%20the%20fever%20%22days%20later%22.%20the%20physician%20reported%20that%20he%20doubted%20that%20the%20vaccine%20caused%20the%20fever.%20the%20physician%20reported%20that%20a%20uti%20(concurrent%20condition)%20was%20suspected%20as%20the%20cause%20of%20the%20fever.%20additional%20information%20is%20not%20expected.&ontologies=SNOMEDCT&longest_only=false&exclude_numbers=false&whole_word_only=true&exclude_synonyms=false>>>>>>> .r2015
