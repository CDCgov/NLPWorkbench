from collections import defaultdict
from LifFileParser import LifFileParser
import copy
import sys

'''
This file defines the PostSentenceSplitter tool intalled onto LAPPS
This tool returns a LIF file at GATE Sentence Splitter format.
Usage: python PostSentenceSplitter_GATE.py input_lif_file output_lif_file
'''

# This is the function for detecting camalcase
# We detect if the string has both lower and upper case
def camel(s):
    return (s != s.lower() and s != s.upper())

# The input and output of this PostSentenceSplitter are both LIF format from LAPPS
class PostSentenceSplitter:

    def __init__(self, input_filename="", lif_string=""):
        self.input_filename = input_filename
        self.sentence_info = defaultdict(list)
        if input_filename != "":
            self.parser = LifFileParser(input_filename)
        if lif_string != "":
            self.parser = LifFileParser(string=lif_string)

    def parse_sentence(self):
        annotations = self.parser.loadAnnotation("Splitter")
        original_text = str(self.parser.data['payload']['text']['@value'])
        current_id = 0
        update_annotations = []
        for ann in annotations:
            if 'Sentence' == ann['label']:
                start = int(ann['start'])
                end = int(ann['end'])
                sentence = original_text[start:end+1]
                splitted_sentence = sentence.split('\n')
                for sent in splitted_sentence:
                    if sent != '':
                        # The sentence is valid if it stops with dot or is camalcase or all upper case
                        if sent[len(sent) - 1] == '.' or camel(sent) or sent == sent.upper():
                            new_annotations = copy.deepcopy(ann)
                            id = str(current_id)
                            new_annotations['id'] = id
                            current_id = current_id + 1
                            new_annotations['start'] = start
                            length = len(sent)
                            start = start + length - 1
                            new_annotations['end'] = start
                            start = start + 1
                            update_annotations.append(new_annotations)
            else:
                ann['id'] = str(current_id)
                current_id = current_id + 1
                update_annotations.append(ann)
        self.parser.updateAnnotation(update_annotations, "Splitter")

    def parse_sentence_old(self, input_filename):
        input_file = open(input_filename)
        input_text = input_file.read()
        input_file.close()
        chunks = input_text.split('{"id"')
        for chunk in chunks:
            if 'sent_' in chunk:
                information = chunk.split(':')
                id = information[1].split('"')[1]
                number_id = int(id.split('_')[1])
                start = information[2].split(',')[0]
                end = information[3].split(',')[0]
                type = information[4].split('"')[1]
                sentence = information[6].split('"')[1]
                if "\\n" in sentence:
                    new_sentences = sentence.split('\\n')
                    new_end = start
                    new_start = start
                    for sent in new_sentences:
                        if number_id == 0:
                            key = "sent_0"
                            new_end = int(start) + len(sent)
                            self.sentence_info[key].append(new_start)
                            self.sentence_info[key].append(new_end)
                            self.sentence_info[key].append(type)
                            self.sentence_info[key].append(sent)

    def write_output(self, filename):
        self.parser.writeLifFile(filename)

if __name__ == "__main__":
    input_filename = sys.argv[1]
    output_filename = sys.argv[2]
    sentence_splitter = PostSentenceSplitter(input_filename)
    sentence_splitter.parse_sentence()
    sentence_splitter.write_output(output_filename)