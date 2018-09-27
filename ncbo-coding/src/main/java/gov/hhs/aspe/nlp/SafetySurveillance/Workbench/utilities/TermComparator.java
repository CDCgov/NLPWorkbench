package gov.hhs.aspe.nlp.SafetySurveillance.Workbench.utilities;

import java.util.Comparator;

import gov.hhs.aspe.nlp.SafetySurveillance.Workbench.Coding.Term;

public class TermComparator implements Comparator<Term>{

	@Override
	public int compare(Term arg0, Term arg1) {
		// TODO Auto-generated method 
		int begin1 = arg0.from;
		int begin2 = arg1.from;
		int end1 = arg0.from;
		int end2 = arg0.to;
		if (begin1 < begin2 )
			return -1;
		else
		{
			if(begin1 == begin2)
			{
				if(end1 < end2)
					return -1;
				else if(end1== end2)
					return 0;
				else 
				{
					return 1;
				}
			}else
			{
				return 1;
			}
		}
	}
}
