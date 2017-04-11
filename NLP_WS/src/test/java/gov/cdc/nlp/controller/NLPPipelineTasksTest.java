package gov.cdc.nlp.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by marcelo on 4/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NLPPipelineTasksTest {

    @Autowired
    OpenNLPPipelineTasks service;

    String content = "hello there. I am here to see you through this. \n" +
                        "o'l right, Dr. who is about to show himself!\n" +
                        "stop.\n" +
                        "i need more coke";

    @Test
    public void testGetSentences() {
        ResponseEntity<String[]> test = service.splitSentences(content);
       for (String s: test.getBody()) {
           System.out.println("s = " + s);
       }
    }

    @Test
    public void testGetTokens() {
        ResponseEntity<String[]> tokens = service.getTokens("I am here to see you through this at 11:00 am on 04/30/2017 near Alphareta, GA 30005");
        for (String t: tokens.getBody()) {
            System.out.println("t = " + t);
        }
    }

}