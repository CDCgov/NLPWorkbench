from nltk.tag.stanford import StanfordPOSTagger
import os
import sys
import json
sys.path.append('..')
from LifFileParser import LifFileParser

"""
This is the wrapper of stanford POS tagger with the result as LIF format.
"""

# Call Stanford POS Tagger
def call_pos_tagger(token_list):
    tagger = StanfordPOSTagger(model_filename='stanford-postagger-2018-02-27/models/english-bidirectional-distsim.tagger',
                               path_to_jar='stanford-postagger-2018-02-27/stanford-postagger.jar', encoding='UTF-8')
    tagged = tagger.tag(token_list)
    id = 0
    tag_result = {}
    for elem in tagged:
        word = elem[0]
        tag = elem[1]
        key = 'tok' + str(id)
        id += 1
        output = {
            'word': word,
            'tag': tag
        }
        tag_result[key] = output
    return tag_result

class LAPPS_StanfordPOSTagger:

    def __init__(self, lif_string):
        self.lif_string = lif_string
        self.lif_parser = LifFileParser(string=lif_string)

    def run_pos_tagger(self):
        annotations = self.lif_parser.loadAnnotation("Token")
        tokens = []
        for i in range(len(annotations)):
            ann = annotations[i]
            if 'word' in ann['features'].keys():
                tokens.append(ann['features']['word'])
        result = call_pos_tagger(tokens)
        for i in range(len(annotations)):
            ann = annotations[i]
            id = ann['id']
            if 'word' in ann['features'].keys():
                word = ann['features']['word']
                if word == result[id]['word']:
                    annotations[i]['features']['pos'] = result[id]['pos']
                else:
                    print("POS Result Mached Incorrectly")
        self.lif_parser.updateAnnotation(annotations, "Token")
        self.lif_parser.addProducer("Token", "org.anc.lapps.stanford.POSTagger:2.0.0" ,
                                    "http://vocab.lappsgrid.org/Token#pos", "stanford")

    def pos_tagger(self):
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
                if len(token_list) > 1 and (token_list[1] == '|' or token_list[1] == '@value'):
                    continue

                tag_result = call_pos_tagger(token_list)
                for j in range(len(id_list)):
                    id = id_list[j]
                    i = int(id[3:])
                    key = 'tok' + str(j)
                    pos = tag_result[key]['tag']
                    word = tag_result[key]['word']
                    if word == annotations[i]['features']['word']:
                        annotations[i]['features']['pos'] = pos
                    else:
                        print("POS Result Mached Incorrectly")
        self.lif_parser.updateAnnotation(annotations, "Token")
        self.lif_parser.addProducer("Token", "org.anc.lapps.stanford.POSTagger:2.0.0",
                                    "http://vocab.lappsgrid.org/Token#pos", "stanford")


if __name__ == "__main__":
    lif_string = open('stanford_pos_sent.lif').read()
    tagger = LAPPS_StanfordPOSTagger(lif_string=lif_string)
    tagger.pos_tagger()
    print(json.dumps(tagger.lif_parser.data))