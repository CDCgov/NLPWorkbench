from collections import defaultdict
import json
import glob

class BIOtoANN:

    def __init__(self, bio_filename = "", source="", ann_filename="", pos_filename=""):
        self.bio_filename = bio_filename
        self.source = source
        self.file_num = ""
        self.ann_filename = ann_filename
        self.position_filename = pos_filename
        self.bio_tags = defaultdict(list)

    def extract_bio_tags(self):
        result = open(self.bio_filename, "r")
        line_number = 1
        for line in result.readlines():
            line = line[:-1]
            length = len(line.split('\t'))
            if length > 1:
                token = line.split('\t')[0]
                gold = line.split('\t')[length - 2]
                annot = line.split('\t')[length - 1]
                index = gold.find('-')
                if index != -1:
                    gold = gold[index + 1:]
                index = annot.find('-')
                if index != -1:
                    annot = annot[index + 1:]
                start = self.find_start_position(line_number, token)
                annot_dict = {}
                annot_dict['tagged'] = annot
                annot_dict['token'] = token
                annot_dict['found'] = False
                annot_dict['start'] = start
                self.bio_tags[gold].append(annot_dict)
                line_number += 1

    def find_start_position(self, line_number, token):
        position_file = open(self.position_filename, "r")
        current_line = 1
        for line in position_file.readlines():
            line = line[:-1]
            current_token = line.split(' ')[0]
            start = line.split(' ')[1]
            if current_line == line_number and current_token == token:
                return start
            current_line += 1
        position_file.close()
        return -1

    def update_ann(self, output_filename=""):
        ann_file = open(self.ann_filename)
        ann_data = json.load(ann_file)
        context = ann_data['__text']
        model = self.source.upper() + "_MODEL"
        # Check the annotations against tags
        for type in ann_data.keys():
            if type != "__text":
                short_type = type.split(' (')[0]
                bio_type = ''.join(short_type.split(' ')).upper()
                tagged_info = self.bio_tags[bio_type]
                for i in range(len(ann_data[type])):
                    ann_info = ann_data[type][i]
                    start = ann_info['__extent'][0]
                    end = ann_info['__extent'][1]
                    annotation = context[start:end]
                    annotation_tokens = annotation.split(' ')
                    agreement = True
                    for token in annotation_tokens:
                        find_outcome = self.find_tagging(bio_type, token, start, end)
                        if not find_outcome:
                            agreement = False
                    if agreement:
                        ann_data[type][i]['__agreement__'] = "TRUE POSITIVE"
                        ann_data[type][i]['__annotator__'] = "Gemma, " + model
                    else:
                        ann_data[type][i]['__agreement__'] = "FALSE NEGATIVE"
                        ann_data[type][i]['__annotator__'] = "Gemma"
        # Check the tags against annotations
        for type in self.bio_tags.keys():
            for tagged_info in self.bio_tags[type]:
                if not tagged_info['found'] and tagged_info['tagged'] != 'O':
                    tag_name = get_original_tagname(tagged_info['tagged'])
                    start = int(tagged_info['start'])
                    end = int(tagged_info['start']) + len(tagged_info['token'])
                    tag_type = find_tag_key(tag_name, ann_data)
                    ann_add = {}
                    ann_add['__agreement__'] = "FALSE POSITIVE"
                    ann_add['__annotator__'] = model
                    ann_add['__extent'] = [start, end]
                    if tag_type != "":
                        ann_data[tag_type].append(ann_add)
                    else:
                        ann_data[tag_name] = []
                        ann_data[tag_name].append(ann_add)
        output_file = open(output_filename, "w+")
        json.dump(ann_data, output_file, indent=4)

    def find_tagging(self, bio_type, token, start, end):
        tagged_info = self.bio_tags[bio_type]
        for i in range(len(tagged_info)):
            tag = tagged_info[i]
            if tag['token'] == token and int(tag['start']) >= start \
                    and int(tag['start'])+len(token) <= end \
                    and tag['tagged'] == bio_type:
                self.bio_tags[bio_type][i]['found'] = True
                return True
        return False

    def append_header(self):
        temp_ann = 'output/bio/' + self.source + '/ann/' + self.file_num + '.ann'
        output_filename = 'output/' + self.source + '_ann/' + self.file_num + '.ann'
        temp_ann_file = open(temp_ann)
        context = temp_ann_file.read()
        temp_ann_file.close()
        context = "# HAF: 1" + "\n" + context
        output_file = open(output_filename, "w+")
        output_file.write(context)
        output_file.close()

def get_original_tagname(tag):
    original = open('tag_abbreviation.txt')
    for line in original.readlines():
        name = line.split('->')[0]
        abbre = ''.join(name.split('(')[0].split(' ')).upper()
        if abbre == tag:
            return name
    return ""

def find_tag_key(tag_name, ann_data):
    for type in ann_data.keys():
        if tag_name in type:
            return type
    return ""

if __name__ == "__main__":
    bio_folder = 'output/bio/stanford/tagged/*.bio'
    ann_folder = 'input/CDC_batch_1_ann/*.ann'
    bio_files = glob.glob(bio_folder)
    for bio_filename in bio_files:
        bio_filename = bio_filename.replace('\\','/')
        print(bio_filename)
        converter = BIOtoANN(bio_filename, 'stanford')
        converter.extract_bio_tags()
        converter.update_ann()
        converter.append_header()
