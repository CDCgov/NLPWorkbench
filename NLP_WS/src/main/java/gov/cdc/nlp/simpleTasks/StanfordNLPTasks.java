package gov.cdc.nlp.simpleTasks;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcelo on 4/4/17.
 */
@Component
public class StanfordNLPTasks implements NLPTasks {
    @Override
    public String[] splitSentences(String text) throws IOException {

        Reader reader = new StringReader(text);
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        List<String> sentenceList = new ArrayList<String>();

        for (List<HasWord> sentence : dp) {
            // SentenceUtils not Sentence
            String sentenceString = SentenceUtils.listToString(sentence);
            sentenceList.add(sentenceString);
        }
        String[] a = new String[sentenceList.size()];
        return sentenceList.toArray(a);
    }

    @Override
    public String[] getTokens(String sentence) throws IOException {
        Reader reader = new StringReader(sentence);
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "");

        List<String> tokenList = new ArrayList<String>();
        while (ptbt.hasNext() ) {
            tokenList.add(String.valueOf(ptbt.next()));
        }
        String[] a = new String[tokenList.size()];
        return tokenList.toArray(a);
    }
}
