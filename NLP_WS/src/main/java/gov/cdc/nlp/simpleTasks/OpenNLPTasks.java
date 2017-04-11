package gov.cdc.nlp.simpleTasks;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by marcelo on 4/4/17.
 */
@Component
public class OpenNLPTasks implements NLPTasks {
    @Value("${nlp.opennlp.model.sentencesplitter}") String SENTENCE_MODEL;
    @Value("${nlp.opennlp.model.tokenizer}") String TOKENIZER_MODEL;

    @Autowired
    ApplicationContext context;

    private SentenceDetectorME sentenceDetector = null;
    private TokenizerME tokenizer = null;


    @Override
    public String[] splitSentences(String text) throws IOException {
        String[] sentences = getSentenceDetector().sentDetect(text);
        return sentences;
    }

    @Override
    public String[] getTokens(String sentence) throws IOException {
        return getTokenizer().tokenize(sentence);
    }

    //Lazily Initializes SentenceDetector Model.
    protected SentenceDetectorME getSentenceDetector() throws IOException {
        if (sentenceDetector == null) {
            InputStream is = context.getClassLoader().getResourceAsStream(SENTENCE_MODEL);
            //FileInputStream  modelIn = new FileInputStream(is);
            SentenceModel model = new SentenceModel(is);
            sentenceDetector = new SentenceDetectorME(model);
        }
        return sentenceDetector;
    }
    //Lazily initializes Tokenizer Model.
    protected TokenizerME getTokenizer() throws IOException {
        if (tokenizer == null) {
            InputStream is = context.getClassLoader().getResourceAsStream(TOKENIZER_MODEL);
            TokenizerModel model = new TokenizerModel(is);
            tokenizer = new TokenizerME(model);
        }
        return tokenizer;
    }
}
