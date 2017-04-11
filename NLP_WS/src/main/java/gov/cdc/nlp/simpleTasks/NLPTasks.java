package gov.cdc.nlp.simpleTasks;

import java.io.IOException;

/**
 * Created by marcelo on 4/4/17.
 */
public interface NLPTasks {
    public String[] splitSentences(String text) throws IOException;
    public String[] getTokens(String sentence) throws IOException;


}
