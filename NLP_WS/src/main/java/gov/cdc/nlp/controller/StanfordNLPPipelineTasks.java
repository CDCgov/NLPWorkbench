package gov.cdc.nlp.controller;

import gov.cdc.nlp.simpleTasks.NLPTasks;
import gov.cdc.nlp.simpleTasks.StanfordNLPTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by marcelo on 4/4/17.
 */
@RestController
@RequestMapping("/stanford")
public class StanfordNLPPipelineTasks extends NLPPipelineTasks {
    @Autowired
    StanfordNLPTasks nlpTasks;

    @Override
    public NLPTasks getNlpTasks() {
        return nlpTasks;
    }
}
