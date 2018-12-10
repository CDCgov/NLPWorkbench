package gov.hhs.aspe.nlp.SafetySurveillance.ClearTK;

/**
 * Utility class to hold the machine learning models names.  Currently the
 * only models available are SVM (Support Vector Machine) and CRF (Conditional
 * Random Field).
 */
public final class Models
{
	private Models() { }

	public static final String SVM = "svm";
	public static final String CRF = "crf";
}
