#!usr/bin/python -w

import sys
from collections import defaultdict
from lxml import html
import requests
from LifFileParser import LifFileParser

# Look for the word from all the annotations
def find_word(annotations, start, end):
    word = ""
    for ann in annotations:
        if 'Token' in ann['@type']:
            if start == ann['start'] and end == ann['end']:
                word = ann['features']['word']
                break
    return word

class POSPostProcess:

    def __init__(self, filename):
        self.input_filename = filename
        self.lif_loader = LifFileParser(filename)
        self.proper_nouns = []

    def extract_list(self, url):
        page = requests.get(url)
        tree = html.fromstring(page.content)
        diseases = tree.xpath('//a[@class="mw-redirect"]/text()')
        diseases = diseases[1:-8]
        names = []
        for dis in diseases:
            people = dis.encode('utf-8').split(' ')
            name = people[0]
            if "'" in name:
                index = name.index("'")
                name = name[:index]
            names.append(name)
        self.proper_nouns = names
        # return names

    def parse_pos(self):
        pos_annotations = self.lif_loader.loadAnnotation("Tagger")
        token_annotations = self.lif_loader.loadAnnotation("Token")
        print(pos_annotations)
        print(token_annotations)
        update_annotations = []
        for ann in pos_annotations:
            if 'pos' in ann['@type'] \
                and ann['features']['pos'] == 'NNP':
                start = ann['start']
                end = ann['end']
                word = find_word(token_annotations, start, end)
                print("Found tagged NNP")
                print(word)
                if word not in self.proper_nouns:
                    print("Change NNP to NN!")
                    ann['features']['pos'] = 'NN'
                update_annotations.append(ann)
            else:
                update_annotations.append(ann)
        self.lif_loader.updateAnnotation(update_annotations, "Tagger")

    def write_output(self, filename):
        self.lif_loader.writeLifFile(filename)


input_filename = sys.argv[1]
output_filename = sys.argv[2]
post_process = POSPostProcess(input_filename)
post_process.extract_list("https://en.wikipedia.org/wiki/List_of_eponymously_named_diseases")
post_process.parse_pos()
post_process.write_output(output_filename)
