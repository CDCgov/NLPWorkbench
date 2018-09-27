import json
import sys
from collections import defaultdict
from Pipeline.LifFileParser import LifFileParser
import copy
from Pipeline.PostTokenizer_GATE import parse_hyphen, find_tokens
import re

'''
This file implements the PostTokenizer tool for Stanford Tokenizer which 
further separates the tokens with other symbols not being taken into account.
This tool also requires an ann file as input and creates a semantic tag feature 
onto the output LIF file.
Usage: python PostTokenizer_Stanford.py input_lif_file input_ann_file output_lif_file
'''

# Split the tokens containing hyphen inside into separate tokens
# Return a new annotation list for LIF file
def split_hyphen(annotations):
    update_annotations = []
    current_id = 0
    for ann in annotations:
        if '-' in ann['features']['word'] \
                and len(ann['features']['word']) > 1:
            new_words = str(ann['features']['word']).split('-')
            start = int(ann['start'])
            end = int(ann['end'])
            for word in new_words:
                if word != '':
                    new_ann = copy.deepcopy(ann)
                    new_ann['id'] = 'tok' + str(current_id)
                    current_id = current_id + 1
                    new_ann['features']['word'] = word
                    new_ann['start'] = str(start)
                    start = start + len(word)
                    new_ann['end'] = str(start)
                    update_annotations.append(new_ann)
                    if start != end:
                        hyphen_ann = copy.deepcopy(ann)
                        hyphen_ann['id'] = 'tok' + str(current_id)
                        current_id = current_id + 1
                        hyphen_ann['features']['word'] = '-'
                        hyphen_ann['start'] = str(start)
                        start = start + 1
                        hyphen_ann['end'] = str(start)
                        update_annotations.append(hyphen_ann)
        else:
            ann['id'] = 'tok' + str(current_id)
            current_id = current_id + 1
            update_annotations.append(ann)
    return update_annotations

# Split the token containing forward slash into separate tokens
# Return a new annotation list for LIF file
def split_forwardslash(annotations):
    update_annotations = []
    current_id = 0
    for ann in annotations:
        if '/' in ann['features']['word'] \
            and len(ann['features']['word']) > 1:
            new_words = str(ann['features']['word']).split('/')
            start = int(ann['start'])
            end = int(ann['end'])
            for word in new_words:
                if word != '':
                    new_ann = copy.deepcopy(ann)
                    new_ann['id'] = 'tok' + str(current_id)
                    current_id = current_id + 1
                    new_ann['features']['word'] = word
                    new_ann['start'] = str(start)
                    start = start + len(word)
                    new_ann['end'] = str(start)
                    update_annotations.append(new_ann)
                    if start != end:
                        hyphen_ann = copy.deepcopy(ann)
                        hyphen_ann['id'] = 'tok' + str(current_id)
                        current_id = current_id + 1
                        hyphen_ann['features']['word'] = '/'
                        hyphen_ann['start'] = str(start)
                        start = start + 1
                        hyphen_ann['end'] = str(start)
                        update_annotations.append(hyphen_ann)
        else:
            ann['id'] = 'tok' + str(current_id)
            current_id = current_id + 1
            update_annotations.append(ann)
    return update_annotations

# Split the token containing single quotes into separate tokens
# Return a new annotation list for LIF file
def split_quotes(annotations):
    update_annotations = []
    current_id = 0
    for ann in annotations:
        if "'" in ann['features']['word'] \
            and len(ann['features']['word']) > 1:
            new_words = str(ann['features']['word']).split("'")
            start = int(ann['start'])
            end = int(ann['end'])
            for word in new_words:
                if word != '':
                    new_ann = copy.deepcopy(ann)
                    new_ann['id'] = 'tok' + str(current_id)
                    current_id = current_id + 1
                    new_ann['features']['word'] = word
                    new_ann['start'] = str(start)
                    start = start + len(word)
                    new_ann['end'] = str(start)
                    update_annotations.append(new_ann)
                    if start != end:
                        hyphen_ann = copy.deepcopy(ann)
                        hyphen_ann['id'] = 'tok' + str(current_id)
                        current_id = current_id + 1
                        hyphen_ann['features']['word'] = "'"
                        hyphen_ann['start'] = str(start)
                        start = start + 1
                        hyphen_ann['end'] = str(start)
                        update_annotations.append(hyphen_ann)
        else:
            ann['id'] = 'tok' + str(current_id)
            current_id = current_id + 1
            update_annotations.append(ann)
    return update_annotations

# Split the tokens containing other special symbols into separate tokens
# Return a new annotation for LIF file
def split_special_symbols(annotations):
    update_annotations = []
    current_id = 0
    for ann in annotations:
        splitted = re.split('( |<| |:|#|\+|\'|~|\\\\"|\&|\|)', ann['features']['word'])
        second_split = list(filter(lambda a: a != '', splitted))
        start = int(ann['start'])
        for word in second_split:
            new_ann = copy.deepcopy(ann)
            new_ann['id'] = 'tok' + str(current_id)
            current_id = current_id + 1
            new_ann['features']['word'] = word
            new_ann['start'] = str(start)
            start = start + len(word)
            new_ann['end'] = str(start)
            update_annotations.append(new_ann)
    return update_annotations

class PostTokenizer:

    def __init__(self, lif_filename="", ann_filename="", lif_string=""):
        self.input_filename = lif_filename
        self.ann_filename = ann_filename
        if lif_filename != "":
            self.lif_loader = LifFileParser(filename=lif_filename)
        if lif_string != "":
            self.lif_loader = LifFileParser(string=lif_string)

    def load_ann(self):
        ann_file = open(self.ann_filename)
        self.ann_data = json.load(ann_file)

    def extract_tag(self):
        annotations = self.lif_loader.loadAnnotation("Token")
        annotations = split_hyphen(annotations)
        annotations = split_forwardslash(annotations)
        annotations = split_quotes(annotations)
        annotations = split_special_symbols(annotations)
        # Look through all the semantic tags
        for key in self.ann_data.keys():
            if key != "__text":
                tag = str(key).split("(")[0]
                for info in self.ann_data[key]:
                    tag_start = info["__extent"][0]
                    tag_end = info["__extent"][1]
                    index = find_tokens(annotations, tag_start, tag_end)
                    for i in index:
                        ann = annotations[i]
                        tag = ''.join(tag.split(' '))
                        if i == index[0]:
                            ann['features']['semanticTag'] = 'B-'+tag.upper()
                        else:
                            ann['features']['semanticTag'] = 'I-'+tag.upper()
                        annotations[i] = ann
        # Tag all other tokens without tags assigned to them
        for i in range(len(annotations)):
            ann = annotations[i]
            if 'semanticTag' not in ann['features'].keys():
                ann['features']['semanticTag'] = 'O'
                annotations[i] = ann
        self.lif_loader.updateAnnotation(annotations, "Token")

    def extract_info(self):
        annotations = self.lif_loader.loadAnnotation("Token")
        annotations = split_hyphen(annotations)
        annotations = split_forwardslash(annotations)
        annotations = split_quotes(annotations)
        annotations = split_special_symbols(annotations)
        self.lif_loader.updateAnnotation(annotations, "Token")
        
    def write_output(self, filename):
        self.lif_loader.writeLifFile(filename)

def find_tag_abbreviation(tag):
    tag_abbreviation = open('tag_abbreviation.txt')
    for line in tag_abbreviation.read().split('\n'):
        original = line.split('->')[0]
        abbreviation = line.split('->')[1]
        if original == tag:
            return abbreviation
    return ''.join(str(tag).split(' '))

if __name__ == "__main__":
    input_filename = sys.argv[1]
    ann_filename = sys.argv[2]
    output_filename = sys.argv[3]
    post_tokenizer = PostTokenizer(input_filename, ann_filename)
    post_tokenizer.load_ann()
    post_tokenizer.extract_tag()
    post_tokenizer.write_output(output_filename)