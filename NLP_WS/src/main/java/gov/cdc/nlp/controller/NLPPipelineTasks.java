package gov.cdc.nlp.controller;

import gov.cdc.nlp.simpleTasks.NLPTasks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by marcelo on 4/4/17.
 */
public abstract class NLPPipelineTasks {
    @RequestMapping(value="/info")
    public String getInfo() {
        return "This is a test for  framework";
    }

    //@CrossOrigin(origins = "http://localhost:8081")
    @RequestMapping(value = "/sentences", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String[]> splitSentences(@RequestBody String content) {
        try {
            return ResponseEntity.ok(getNlpTasks().splitSentences(content));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @RequestMapping(value = "/tokens", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String[]> getTokens(@RequestParam String sentence) {
        try {
            return ResponseEntity.ok(getNlpTasks().getTokens(sentence));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public abstract NLPTasks getNlpTasks();

}
