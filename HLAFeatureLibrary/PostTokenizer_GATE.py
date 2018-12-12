import json
import sys
from collections import defaultdict
from LifFileParser import LifFileParser
import copy
import re
'''
This file implements the PostTokenizer tool for GATE Tokenizer which 
further separates the tokens with other symbols not being taken into account.
This tool also requires an ann file as input and creates a semantic tag feature 
onto the output LIF file.
Usage: python PostTokenizer_GATE.py input_lif_file input_ann_file output_lif_file
'''

# This function look up the digits occur in the annotations and merge them as a single token.
# The parameter for this function is the annotations dictionary from LifFileParser class.
# This function will return an updated annotation list.
def parse_digit(annotations):
    number = len(annotations)
    current_id = 0
    update_annotations = []
    i = 0
    # Look through all annotations
    while i < number-2:
        ann = annotations[i]
        next_ann = annotations[i+1]
        second_ann = annotations[i+2]
        new_annotations = ann
        # Update the annotation if we encounter digit with pattern number.number
        if ann['features']['tokenType'] == 'number' \
            and next_ann['features']['word'] == '.' \
            and second_ann['features']['tokenType'] == 'number':
            word = ann['features']['word'] + next_ann['features']['word'] + second_ann['features']['word']
            new_annotations['end'] = second_ann['end']
            length = int(ann['features']['length']) + int(next_ann['features']['length']) \
                     + int(second_ann['features']['length'])
            new_annotations['features']['length'] = str(length)
            new_annotations['features']['tokenType'] = 'number'
            new_annotations['features']['word'] = word
            new_annotations['id'] = str(current_id)
            new_annotations['start'] = ann['start']
            current_id = current_id + 1
            i = i + 3
            update_annotations.append(new_annotations)
        else:
            new_annotations['id'] = str(current_id)
            current_id = current_id + 1
            update_annotations.append(new_annotations)
            i = i + 1
    return update_annotations

# This function detects if there exist serial number with numbers together with letters
# Update the annotations to replace them as a single token
# Return the new annotations
def parse_serial_number(annotations):
    number = len(annotations)
    current_id = 0
    update_annotations = []
    i = 0
    while i < number-2:
        ann = annotations[i]
        next_ann = annotations[i + 1]
        second_ann = annotations[i + 2]
        new_annotations = ann
        if ann['features']['tokenType'] == 'number' \
            and 'orth' in next_ann['features'] \
            and next_ann['features']['orth'] == 'upperInitial' \
            and second_ann['features']['tokenType'] == 'number':
            word = ann['features']['word'] + next_ann['features']['word'] + second_ann['features']['word']
            new_annotations['features']['word'] = word
            new_annotations['end'] = second_ann['end']
            length = int(ann['features']['length']) + int(next_ann['features']['length']) \
                     + int(second_ann['features']['length'])
            new_annotations['features']['length'] = length
            new_annotations['features']['tokenType'] = 'number'
            new_annotations['id'] = str(current_id)
            current_id = current_id + 1
            new_annotations['start'] = ann['start']
            update_annotations.append(new_annotations)
            i = i + 3
        else:
            new_annotations['id'] = str(current_id)
            current_id = current_id + 1
            update_annotations.append(new_annotations)
            i = i + 1
    return update_annotations

# Split the token by hyphen if there exist
# Return a new annotation scheme
def parse_hyphen(annotations):
    number = len(annotations)
    current_id = 0
    update_annotations = []
    i=0
    while i < number:
        ann = annotations[i]
        # print(ann)
        word = ann['features']['word']
        length = len(word)
        if word[length-1] == '-' and length > 1:
            new_ann = copy.deepcopy(ann)
            second_ann = copy.deepcopy(ann)
            new_word = word[:-1]
            second_ann['id'] = str(current_id)
            current_id = current_id + 1
            second_ann['end'] = int(ann['end']) - 1
            second_ann['features']['word'] = new_word
            update_annotations.append(second_ann)
            new_ann['id'] = str(current_id)
            current_id = current_id + 1
            new_ann['start'] = second_ann['end']
            new_ann['end'] = new_ann['start'] + 1
            new_ann['features']['word'] = '-'
            new_ann['features']['tokenType'] = 'symbol'
            update_annotations.append(new_ann)
        elif '-' in word:
            new_first = str(word).split('-')[0]
            new_second = str(word).split('-')[1]
            first_ann = copy.deepcopy(ann)
            hyphen_ann = copy.deepcopy(ann)
            second_ann = copy.deepcopy(ann)
            first_ann['id'] = str(current_id)
            current_id = current_id + 1
            first_ann['end'] = int(first_ann['start']) + len(new_first)
            first_ann['features']['word'] = new_first
            first_ann['features']['length'] = len(new_first)
            first_ann['features']['tokenType'] = 'word'
            hyphen_ann['id'] = str(current_id)
            current_id = current_id + 1
            hyphen_ann['features']['word'] = '-'
            hyphen_ann['start'] = first_ann['end']
            hyphen_ann['end'] = int(hyphen_ann['start']) + 1
            hyphen_ann['features']['tokenType'] = 'symbol'
            hyphen_ann['features']['length'] = 1
            second_ann['id'] = str(current_id)
            current_id = current_id + 1
            second_ann['start'] = hyphen_ann['end']
            second_ann['end'] = int(second_ann['start']) + len(new_second)
            second_ann['features']['word'] = new_second
            second_ann['features']['length'] = len(new_second)
            second_ann['features']['tokenType'] = 'word'
        else:
            ann['id'] = str(current_id)
            current_id = current_id + 1
            update_annotations.append(ann)
        i = i + 1
    return update_annotations

# Split the token containing single quotes into separate tokens
# Return a new annotation list for LIF file
def parse_quotes(annotations):
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
                    new_ann['id'] = str(current_id)
                    current_id = current_id + 1
                    new_ann['features']['word'] = word
                    new_ann['start'] = str(start)
                    start = start + len(word)
                    new_ann['end'] = str(start)
                    new_ann['features']['tokenType'] = 'word'
                    new_ann['features']['length'] = len(word)
                    update_annotations.append(new_ann)
                    if start != end:
                        hyphen_ann = copy.deepcopy(ann)
                        hyphen_ann['id'] = str(current_id)
                        current_id = current_id + 1
                        hyphen_ann['features']['word'] = "'"
                        hyphen_ann['start'] = str(start)
                        start = start + 1
                        hyphen_ann['end'] = str(start)
                        new_ann['features']['tokenType'] = 'symbol'
                        new_ann['features']['length'] = 1
                        new_ann['features']['orth'] = 'symbol'
                        update_annotations.append(hyphen_ann)
        else:
            ann['id'] = str(current_id)
            current_id = current_id + 1
            update_annotations.append(ann)
    return update_annotations

# Find the token position stored which stands for the corresponding tags
def find_tokens(annotations, tag_start, tag_end):
    index = []
    number = len(annotations)
    for i in range(number):
        ann = annotations[i]
        start = int(ann['start'])
        end = int(ann['end'])
        if start >= int(tag_start) \
            and end <= int(tag_end) \
            and ann['@type'] != 'SpaceToken':
            index.append(i)
    return index

def split_special_symbols(annotations):
    update_annotations = []
    current_id = 0
    for ann in annotations:
        splitted = re.split('(\^|<| |\?|-|#|\+|\'|~|\\\\"|\&|\|)', ann['features']['word'])
        second_split = list(filter(lambda a: a != '', splitted))
        start = int(ann['start'])
        for word in second_split:
            new_ann = copy.deepcopy(ann)
            new_ann['id'] = str(current_id)
            current_id = current_id + 1
            new_ann['features']['word'] = word
            new_ann['features']['length'] = len(word)
            new_ann['start'] = str(start)
            start = start + len(word)
            new_ann['end'] = str(start)
            update_annotations.append(new_ann)
    return update_annotations

# This class defines all functionality for PostTokenizer including matching the semantic Tag
class PostTokenizer:

    def __init__(self, lif_filename="", ann_filename="", lif_string = ""):
        self.input_filename = lif_filename
        self.ann_filename = ann_filename
        if lif_filename != "":
            self.lif_loader = LifFileParser(filename=lif_filename)
        if lif_string != "":
            self.lif_loader = LifFileParser(string=str(lif_string))

    def load_ann(self):
        if self.ann_filename != "":
            ann_file = open(self.ann_filename)
            self.ann_data = json.load(ann_file)
        else:
            self.ann_data = {}
            
    # Extract the semantic tag for all tokens
    def extract_tag(self):
        annotations = self.lif_loader.loadAnnotation("Token")
        annotations = split_special_symbols(annotations)
        annotations = parse_digit(annotations)
        annotations = parse_serial_number(annotations)
        annotations = parse_hyphen(annotations)
        annotations = parse_quotes(annotations)
        print("Finish fixing tokenizer")
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
                        tag = ''.join(str(tag).split(' '))
                        if i == index[0]:
                            ann['features']['semanticTag'] = 'B-'+tag.upper()
                        else:
                            ann['features']['semanticTag'] = 'I-'+tag.upper()
                        annotations[i] = ann
        # Tag all other tokens without tags assigned to them
        if len(self.ann_data.keys()) > 0:
            for i in range(len(annotations)):
                ann = annotations[i]
                if 'semanticTag' not in ann['features'].keys():
                    ann['features']['semanticTag'] = 'O'
                    annotations[i] = ann
        self.lif_loader.updateAnnotation(annotations, "Token")

    def write_output(self, filename):
        self.lif_loader.writeLifFile(filename)

def find_tag_abbreviation(tag):
    tag_abbreviation = open('Training/tag_abbreviation.txt')
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