# import pexpect
import subprocess
import sys
sys.path.append('..')
from LifFileParser import LifFileParser
import json

"""
This file contains the wrapper of opennlp functions which saves the result in the LIF format.
"""

class OpenNLP:

    def __init__(self, path, tool, model):
        self.opennlp = path + '/bin/opennlp'
        self.model_path = path + '/models/' + model
        self.tool = tool

    def parse(self, text):
        argument = [self.opennlp, self.tool, self.model_path]
        process = subprocess.Popen(argument, stdin=subprocess.PIPE, stdout=subprocess.PIPE, universal_newlines=True)
        output,error = process.communicate(text)
        return output

class OpenNLP_Tokenizer:

    def __init__(self, lif_string):
        self.lif_parser = LifFileParser(string=lif_string)
        self.text = self.lif_parser.data['payload']['text']['@value']

    def run_tokenizer(self):
        sent_annotations = self.lif_parser.loadAnnotation("Splitter")
        id = 0
        token_annotations = []
        for ann in sent_annotations:
            token_id = 0
            if ann['label'] == 'Sentence':
                start = int(ann['start'])
                end = int(ann['end'])
                current_start = start
                sentence = self.text[start:end]
                if sentence != "":
                    tokenizer = OpenNLP('apache-opennlp-1.8.4', 'TokenizerME', 'en-token.bin')
                    token_results = tokenizer.parse(sentence)
                    token_results = token_results[:-1].split(' ')
                    print(token_results)
                    for token in token_results:
                        new_ann = {
                            "id": "tok_" + str(id),
                            "start": current_start,
                            "end": current_start+len(token),
                            "@type": "http://vocab.lappsgrid.org/Token",
                            "label": "Token",
                            "features":{
                                "word": token
                            }
                        }
                        id += 1
                        token_annotations.append(new_ann)
                        current_start = current_start + len(token) 
                        if current_start < end and sentence[current_start-start] == " ":
                            current_start += 1
        metadata = {
            "contains": {
                    "http://vocab.lappsgrid.org/Token": {
                        "producer": "org.anc.lapps.opennlp.Tokenizer:2.0.0",
                        "type": "opennlp"
                    }
            }
        }
        view = {
            "metadata" : metadata,
            "annotations": token_annotations
        }
        self.lif_parser.data["payload"]["views"].append(view)

class OpenNLP_Sentence_Splitter:

    def __init__(self, lif_string):
        self.lif_parser = LifFileParser(string=lif_string)
        self.text = self.lif_parser.data['payload']['text']['@value']
        self.first_three = self.text[:3]

    def run_opennlp_sentence_splitter(self):
        sentence_splitter = OpenNLP('apache-opennlp-1.8.4', 'SentenceDetector', 'en-sent.bin')
        sentence_results = sentence_splitter.parse(self.text)
        sentence_results = sentence_results.split('\n')
        # print(sentence_results)
        sent_annotations = []
        current_start = 0
        current_end = 0
        id = 0
        for sentence in sentence_results:
            if sentence != "":
                start = current_end + str(self.text[current_end:]).find(sentence)
                end = start + len(sentence)
                new_ann = {
                    "id": "sent_"+str(id),
                    "start": start,
                    "end": end,
                    "@type": "http://vocab.lappsgrid.org/Sentence",
                    "label": "Sentence",
                    "features":{
                        "sentence": sentence
                    }
                }
                current_start = start
                current_end = end
                sent_annotations.append(new_ann)
                id += 1
        metadata = {
            "contains": {
                "http://vocab.lappsgrid.org/Sentence": {
                    "producer": "org.anc.lapps.opennlp.SentenceSplitter:2.0.0",
                    "type": "sentence:opennlp"
                }
            }
        }
        view = {
            "metadata": metadata,
            "annotations": sent_annotations
        }
        self.lif_parser.data["payload"]["views"].append(view)

class OpenNLP_POS_Tagger:

    def __init__(self, lif_string):
        self.lif_parser = LifFileParser(string=lif_string)
        self.text = self.lif_parser.data['payload']['text']['@value']

    def run_opennlp_pos_tagger(self):
        sent_annotations = self.lif_parser.loadAnnotation("Sentence")
        annotations = self.lif_parser.loadAnnotation("Token")
        for k in range(len(sent_annotations)):
            ann = sent_annotations[k]
            if ann['label'] == 'Sentence':
                start = int(ann['start'])
                end = int(ann['end'])
                token_list = []
                id_list = []
                for i in range(len(annotations)):
                    token_ann = annotations[i]
                    if int(token_ann['start']) >= start \
                            and int(token_ann['end']) <= end:
                        token_list.append(token_ann['features']['word'])
                        id_list.append(token_ann['id'])
                # print(token_list)
                if len(token_list) > 1 and (token_list[1] == '|' or token_list[1] == '@value'):
                    continue
                sentence = ' '.join(token_list)
                tag_result = self.call_pos_tagger(sentence)
                for j in range(len(id_list)):
                    id = id_list[j]
                    i = int(id[4:])
                    key = 'tok' + str(j)
                    pos = tag_result[key]['tag']
                    word = tag_result[key]['word']
                    if word == annotations[i]['features']['word']:
                        annotations[i]['features']['pos'] = pos
                    else:
                        print("POS Result Mached Incorrectly")
        # print(annotations)
        self.lif_parser.updateAnnotation(annotations, "Token")
        self.lif_parser.addProducer("Token", "org.anc.lapps.opennlp.POSTagger:2.0.0",
                                    "http://vocab.lappsgrid.org/Token#pos", "opennlp")

    def call_pos_tagger(self, sentence):
        pos_tagger = OpenNLP('apache-opennlp-1.8.4', 'POSTagger', 'en-pos-maxent.bin')
        pos_results = pos_tagger.parse(sentence)
        pos_results = pos_results[:-1].split(' ')
        id = 0
        tag_results = {}
        for entry in pos_results:
            index = entry.rfind('_')
            word = entry[:index]
            pos = entry[index+1:]
            key = 'tok' + str(id)
            id += 1
            output = {
                'word': word,
                "tag": pos
            }
            tag_results[key] = output
        return tag_results


if __name__ == "__main__":
    lif_string = open('input.lif').read()
    opennlp_sentence_splitter = OpenNLP_Sentence_Splitter(lif_string)
    opennlp_sentence_splitter.run_opennlp_sentence_splitter()
    opennlp_sentence_lif = json.dumps(opennlp_sentence_splitter.lif_parser.data, indent=4)
    temp_output = open("temp/opennlp_sent.lif", "w+")
    json.dump(opennlp_sentence_splitter.lif_parser.data, temp_output, indent=4)
    opennlp_tokenizer = OpenNLP_Tokenizer(opennlp_sentence_lif)
    opennlp_tokenizer.run_tokenizer()
    temp_output = open("temp/opennlp_token.lif", "w+")
    json.dump(opennlp_tokenizer.lif_parser.data, temp_output, indent=4)
    opennlp_tokenizer_lif = json.dumps(opennlp_tokenizer.lif_parser.data, indent=4)
    pos_tagger = OpenNLP_POS_Tagger(opennlp_tokenizer_lif)
    pos_tagger.run_opennlp_pos_tagger()
    # print(opennlp_lif_string)
