from collections import defaultdict, OrderedDict
import json
import glob
import re

"""
This file reads the tagged BIO file and position file. It then matchs with the Gold Standard to get the evaluation value 
like True Positive, False Negative, False Positive for each instance.
"""
class BIOtoANN:

    def __init__(self, bio_filename = "", source=""):
        self.bio_filename = bio_filename
        self.source = source
        self.file_num = bio_filename.split('/')[4].split('.')[0]
        self.ann_filename = 'input/CDC_ann/' + self.file_num + '.ann'
        self.position_filename = 'output/bio/' + source + '/tagged/' + self.file_num + '.pos'
        self.bio_tags = defaultdict(list)
        ann_file = open(self.ann_filename)
        self.ann_data = json.load(ann_file)

    def extract_bio_tags(self):
        result = open(self.bio_filename)
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
        # print(self.bio_tags)

    def find_start_position(self, line_number, token):
        position_file = open(self.position_filename)
        current_line = 1
        for line in position_file.readlines():
            line = line[:-1]
            length = len(line.split(' '))
            current_token = line.split(' ')[0]
            start = line.split(' ')[length-1]
            if current_line == line_number and current_token == token:
                return start
            current_line += 1
        return -1

    def update_ann(self):
        ann_file = open(self.ann_filename)
        ann_data = json.load(ann_file)
        context = ann_data['__text']
        # Check the annotations against tags
        model = self.source.upper() + "_MODEL"
        for type in ann_data.keys():
            if type != "__text":
                # short_type = type.split(' (')[0]
                # bio_type = ''.join(short_type.split(' ')).upper()
                bio_type = type.split(' (')[0].replace(" ", "_")
                tagged_info = self.bio_tags[bio_type]
                for i in range(len(ann_data[type])):
                    ann_info = ann_data[type][i]
                    start = ann_info['__extent'][0]
                    end = ann_info['__extent'][1]
                    annotation = context[start:end]
                    annotation_tokens = annotation.split(' ')
                    # splitted = re.split('(\^|\?|-|\+|\'|~|\\\\"|\&|\|)', annotation)
                    # annotation_tokens = list(filter(lambda a: a != '', splitted))
                    agreement = True
                    for token in annotation_tokens:
                        splitted = re.split('(\^|\?|-|;|\.|\(|\)|:|\+|\'|~|\\\\"|\&|\|)', token)
                        second_token = list(filter(lambda a: a != '', splitted))
                        outcome = self.find_tagging(bio_type, token, start, end)
                        if not outcome:
                            for tok in second_token:
                                find_outcome = self.find_tagging(bio_type, tok, start, end)
                                if not find_outcome:
                                    #print(tok)
                                    agreement = False
                        # if "'" in token:
                        #     second_token = token.split("'")
                        #     second_token.append("'")
                        #     for tok in second_token:
                        #         find_outcome = self.find_tagging(bio_type, tok, start, end)
                        #         if not find_outcome:
                        #             agreement = False
                        # else:
                        #     find_outcome = self.find_tagging(bio_type, token, start, end)
                        #     if not find_outcome:
                        #         agreement = False
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
                    # print(tag_name)
                    # print(tag_type)
                    ann_add = {}
                    ann_add['__agreement__'] = "FALSE POSITIVE"
                    ann_add['__annotator__'] = model
                    ann_add['__extent'] = [start, end]
                    if tag_type != "":
                        ann_data[tag_type].append(ann_add)
                    else:
                        ann_data[tag_name] = []
                        ann_data[tag_name].append(ann_add)
        output_filename = 'output/bio/' + self.source + '/ann/' + self.file_num + '.ann'
        output_file = open(output_filename, "w+")
        json.dump(ann_data, output_file, indent=4)

    def update_ann_evaluation(self):
        result = open(self.bio_filename)
        line_number = 1
        current_gold_tag, current_tag = "O", "O"
        current_gold_content, current_content = "", ""
        current_gold_extent = [0,0]
        current_extent = [0,0]
        self.match_dict = defaultdict(list)
        for line in result.readlines():
            line = line[:-1]
            length = len(line.split('\t'))
            if length > 1:
                line_number += 1
                token = line.split('\t')[0]
                gold = line.split('\t')[length - 2]
                annot = line.split('\t')[length - 1]
                index = gold.find('-')
                start = self.find_start_position(line_number, token)
                end = start + len(token)
                #Update previous tags
                if gold[0] == "B" and annot == "O":
                    if current_gold_tag != "O" and current_tag != "O":
                        key = str(current_gold_extent[0]) + " " + str(current_gold_extent[1])
                        self.match_dict[key].append(current_gold_tag)
                        self.match_dict[key].append("GOLD")
                        key = str(current_extent[0]) + " " + str(current_extent[1])
                        self.match_dict[key].append(current_tag)
                        self.match_dict[key].append("ANNOT")
                        current_tag = "O"
                        current_extent = [0,0]
                        current_content = ""

                # Extract the current golden tag
                if gold[0] == "B" and gold[1] == "-":
                    current_gold_tag = gold[2:]
                    current_gold_content = token
                    current_gold_extent[0] = start
                    current_gold_extent[1] = end
                elif gold[0] == "I" and gold[1] == "-" and gold[2:] == current_gold_tag:
                    current_gold_content = current_gold_content + " " + token
                    current_gold_extent[1] = end
                # Extract the annotated tag
                if annot[0] == "B" and annot[1] == "-":
                    current_tag = annot[2:]
                    current_content = token
                    current_extent[0] = start
                    current_extent[1] = end
                elif annot[0] == "I" and annot[1] == "-" and annot[2:] == current_tag:
                    current_content = current_content + " " + token
                    current_extent[1] = end
                elif annot[0] == "I" and annot[1] == "-" and annot[2:] != current_tag:
                    current_tag = annot[2:]
                    current_content = token
                    current_extent[0] = start
                    current_extent[1] = end



    def find_tagging(self, bio_type, token, start, end):
        tagged_info = self.bio_tags[bio_type]
        for i in range(len(tagged_info)):
            tag = tagged_info[i]
            # if bio_type == "RELATIVELOCATION":
            #     if tag['token'] == 'at' or tag['token'] == '1':
            #         print(tag['token'])
            #         print(tag['start'])
            #         print(int(tag['start']) + len(tag['token']))
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
        abbre = name.split(' (')[0].replace(" ", "_")
        if abbre == tag:
            return name
    return ""

def sort_position(pos_folder):
    position_files = glob.glob(pos_folder)
    for position_filename in position_files:
        position_filename = position_filename.replace('\\', '/')
        file_num = position_filename.split('/')[4]
        file = open(position_filename)
        position_data = {}
        for line in file.readlines():
            line = line[:-1]
            pos = int(line.split(' ')[1])
            token = line.split(' ')[0]
            position_data[pos] = token
        file.close()
        position_data = OrderedDict(sorted(position_data.items()))
        # print(position_data)
        output_filename = 'output/bio/gate/tagged/' + file_num
        write_file = open(output_filename, "w+")
        for (pos, token) in position_data.items():
            line = token + ' ' + str(pos) + '\n'
            write_file.write(line)
        write_file.close()

def find_tag_key(tag_name, ann_data):
    for type in ann_data.keys():
        if tag_name in type:
            return type
    return ""

if __name__ == "__main__":
    bio_folder = 'output/bio/gate/tagged/Batch_5_*.bio'
    ann_folder = 'input/CDC_ann/*.ann'
    bio_files = glob.glob(bio_folder)
    sort_position('output/bio/gate/tagged/*.pos')
    for bio_filename in bio_files:
        bio_filename = bio_filename.replace('\\','/')
        print(bio_filename)
        converter = BIOtoANN(bio_filename, 'gate')
        converter.extract_bio_tags()
        converter.update_ann()
        converter.append_header()
