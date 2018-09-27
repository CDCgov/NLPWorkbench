@Grab("org.lappsgrid:serialization:2.5.0")
@Grab("org.lappsgrid:api:2.0.1")
@Grab("org.lappsgrid:discriminator:2.3.2")

import ac.uk.lancs.ucrel.semtaggers.web.clients.SemanticTaggerClient;
import groovy.json.*

File input = new File(args[0])
File output = new File(args[1])

def parser = new JsonSlurper()
def data = parser.parseText(input.text)

//def text = "A 33 year-old man with past medical history significant for dizziness/fainting spells received the following vaccines on 10 March 2010: VAX1 (lot number not reported); and VAX2 (lot number not reported either). Ten days after vaccination, he developed shortness of breath and chest pain and was subsequently diagnosed with myocarditis. On Day 20 (30 March 2010) post vaccination, the following tests were performed: an electrocardiogram which was reported to be normal and troponin I levels were measured and found to be 12.3 ng/ml (abnormal). Patient died on 02 April 2010. COD: heart failure. List of documents held by sender: None."
def tagger = new SemanticTaggerClient()

def text = data.payload.text['@value']
output.text = tagger.tagEngText(text)
