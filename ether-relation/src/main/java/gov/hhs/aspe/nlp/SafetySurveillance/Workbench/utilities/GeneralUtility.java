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
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;

//import gov.hhs.aspe.nlp.SafetySurveillance.Corpus.TLink;
//import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;

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
	

	public static boolean CreateADir(String targetLocation){
	    boolean result = false;
		File targetDir = new File(targetLocation);
		if (!targetDir.exists()) {
		    try{
		    	targetDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		}
		return result;
	}
	
	public static String checkDirectoryName(String dirName){
		if(!dirName.endsWith("\\") || dirName.endsWith("/"))
		{
			dirName += "/";
		}
		return dirName;
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
}