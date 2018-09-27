from collections import defaultdict
from LifFileParser import LifFileParser
import copy
import sys
# The input and output of this PostSentenceSplitter are both LIF format from LAPPS
class PostSentenceSplitter:

    def __init__(self, input_filename):
        self.input_filename = input_filename
        self.sentence_info = defaultdict(list)
        self.parser = LifFileParser(input_filename)

    def parse_sentence(self):
        annotations = self.parser.loadAnnotation("Splitter")
        current_id = 0
        update_annotations = []
        for ann in annotations:
            sentence = ann['features']['sentence']
            splitted_sentence = sentence.split('\n')
            start = int(ann['start'])
            for sent in splitted_sentence:
                if sent != '':
                    new_annotations = copy.deepcopy(ann)
                    id = "sent_" + str(current_id)
                    new_annotations['id'] = id
                    current_id = current_id + 1
                    new_annotations['start'] = start
                    length = len(sent)
                    start = start + length - 1
                    new_annotations['end'] = start
                    start = start + 1
                    new_annotations['features']['sentence'] = sent
                    update_annotations.append(new_annotations)
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
