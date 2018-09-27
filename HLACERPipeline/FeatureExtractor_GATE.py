from LifFileParser import LifFileParser
import sys
from collections import defaultdict,OrderedDict
from Authentication import Authentication
import requests
import json
import copy
from MetaMap import find_snomed

'''
This file implements a feature extractor tool for GATE which allows users to specify
the type of features they want to include.
The return file will be in BIO format which could be passed onto the CRF learn.
'''

class FeatureExtractor:
    '''
    This class implements all required function for extracting different features.
    The features are stored in a dictionary with key: start + ' ' + end
    The values of the feature dictionary is as the following sequence:
    token, length, semantic tag, pos, orth, tokenType, chunker, snomedct code, features for token-1, features for token-2, ... ,
    features for token-n, features for token+1, ..., features for token+n.
    These features will be written into the output BIO file with semantic tag at the last column.
    '''

    def __init__(self, filename="", lif_string=""):
        self.input_filename = filename
        if filename != "":
            self.lif_loader = LifFileParser(filename)
        if lif_string != "":
            self.lif_loader = LifFileParser(string=lif_string)
        self.token_features = defaultdict(list)
        self.number_of_tokens = 0
        self.chunk_code = defaultdict(list)
        self.section_code = defaultdict(list)
        self.verb_code = defaultdict(list)

    def extract_tokens(self):
        annotations = self.lif_loader.loadAnnotation("Token")
        if annotations == []:
            print("Cannot find token result!")
        else:
            for ann in annotations:
                if ann['label'] == 'Token' or ann['label'] == 'SpaceToken':
                    word = ann['features']['word']
                    if word == '\n':
                        continue
                    elif word == ' ':
                        continue
                    self.number_of_tokens = self.number_of_tokens + 1
                    start = ann['start']
                    end = ann['end']
                    length = int(end) - int(start)
                    key = start
                    self.token_features[key].append(word)
                    self.token_features[key].append(length)
                    if 'semanticTag' in ann['features'].keys():
                        semantic_tag = ann['features']['semanticTag']
                        self.token_features[key].append(semantic_tag)
                    else:
                        print("Semantic tag information cannot be found!")
        # print("Finish extracting token information")

    def extract_pos(self):
        # print("Start extracting POS information")
        annotations = self.lif_loader.loadAnnotation("Tagger")
        # print(self.token_features.keys())
        if annotations == []:
            print("Cannot find POS tag result!")
        else:
            for ann in annotations:
                if ann['label'] == 'Token' or ann['label'] == 'SpaceToken':
                    start = ann['start']
                    end = ann['end']
                    if start < self.start_position:
                        continue
                    word = ann['features']['word']
                    if word == ' ' or word == '\n':
                        continue
                    # key = str(start) + ' ' + str(end)
                    key = start
                    if 'pos' in ann['features'].keys():
                        pos = ann['features']['pos']
                        # word = ann['features']['word']
                        if key in self.token_features.keys():
                                # and self.token_features[key][0] == word:
                            self.token_features[key].append(pos)
                        else:
                            self.token_features[key].append("UN")
                            # print("Tokenization in POS Tagger is not consistent with the tokens generated before!")
        # Assign the POS as UN for those untagged tokens
        for (key, value) in self.token_features.items():
            if len(value) < 4:
                self.token_features[key].append("UN")
        # print("Finish extracting POS information")

    def extract_orth(self):
        # print("Start extracting orthography information")
        annotations = self.lif_loader.loadAnnotation("Token")
        if annotations == []:
            print("Cannot find token result!")
        else:
            for ann in annotations:
                if ann['label'] == 'Token' or ann['label'] == 'SpaceToken':
                    start = ann['start']
                    end = ann['end']
                    if start < self.start_position:
                        continue
                    word = ann['features']['word']
                    if word == ' ' or word == '\n':
                        continue
                    # key = str(start) + ' ' + str(end)
                    key = start
                    if 'orth' in ann['features'].keys():
                        orth = ann['features']['orth']
                        self.token_features[key].append(orth)
                    else:
                        self.token_features[key].append("UN")
        # Assign the orth as UN for those untagged tokens
        for (key, value) in self.token_features.items():
            if len(value) < 5:
                self.token_features[key].append("UN")
        # print("Finish extracting orthography information")

    def extract_type(self):
        # print("Start extracting token type information")
        annotations = self.lif_loader.loadAnnotation("Token")
        if annotations == []:
            print("Cannot find token result!")
        else:
            for ann in annotations:
                if ann['label'] == 'Token' or ann['label'] == 'SpaceToken':
                    start = ann['start']
                    end = ann['end']
                    if start < self.start_position:
                        continue
                    word = ann['features']['word']
                    if word == ' ' or word == '\n':
                        continue
                    # key = str(start) + ' ' + str(end)
                    key = start
                    if 'tokenType' in ann['features'].keys():
                        type = ann['features']['tokenType']
                        self.token_features[key].append(type)
                    else:
                        self.token_features[key].append("UN")
        # Assign the type as UN for those untagged tokens
        for (key, value) in self.token_features.items():
            if len(value) < 6:
                self.token_features[key].append("UN")
        # print("Finish extracting token type information")

    def extract_neighbor(self, left_number, right_number, select_number):
        # print("Start extract tokens nearby information")
        token_annotations = self.lif_loader.loadAnnotation("Token")
        self.token_features = OrderedDict(sorted(self.token_features.items()))
        self.number_of_tokens = len(list(self.token_features.keys()))
        # print(self.token_features)
        for i in range(self.number_of_tokens):
            key = list(self.token_features.keys())[i]
            # Extract features for tokens on the left
            for j in range(1, left_number+1):
                if i - j < 0:
                    for k in range(select_number+2):
                        self.token_features[key].append("start")
                else:
                    prev_key = list(self.token_features.keys())[i - j]
                    for k in range(select_number+3):
                        if k != 2:
                            prev_feature = self.token_features[prev_key][k]
                            self.token_features[key].append(prev_feature)
            # Extract features for tokens on the right
            for j in range(1, right_number + 1):
                if i + j >= self.number_of_tokens:
                    for k in range(select_number+2):
                        self.token_features[key].append("end")
                else:
                    next_key = list(self.token_features.keys())[i+j]
                    for k in range(select_number+3):
                        if k != 2:
                            next_feature = self.token_features[next_key][k]
                            self.token_features[key].append(next_feature)

        # print("Finish extract tokens nearby information")


    def extract_chunk(self):
        # print("Start extracting chunker information")
        annotations = self.lif_loader.loadAnnotation("Token")
        text = self.lif_loader.data['payload']['text']['@value']
        # print_chunk(text, annotations)
        if annotations == []:
            print("Cannot find token result!")
        else:
            for ann in annotations:
                if ann['label'] == 'Token' or ann['label'] == 'SpaceToken':
                    start = int(ann['start'])
                    end = int(ann['end'])
                    if start < self.start_position:
                        continue
                    word = ann['features']['word']
                    if word == ' ' or word ==  '\n':
                        continue
                    chunk = find_chunk(annotations, start, end)
                    # key = str(ann['start']) + ' ' + str(ann['end'])
                    key = start
                    self.token_features[key].append(chunk)
        # Assign the type as False for those untagged tokens
        for (key, value) in self.token_features.items():
            if len(value) < 7:
                self.token_features[key].append(False)
        # print("Finish extracting chunker information")

    # Extract SNOMED CODE information
    def extract_snomedct(self):
        print("Start extracting SNOMEDCT code information")
        annotations = self.lif_loader.loadAnnotation("Token")
        if annotations == []:
            print("Cannot find token result!")
        else:
            start_extract = False
            for ann in annotations:
                if ann['label'] == 'Token':
                    start = int(ann['start'])
                    end = int(ann['end'])
                    key = ann['start']
                    if key < self.start_position:
                        continue
                    word = self.token_features[key][0]
                    code, name = find_code(self.section_code, self.chunk_code, self.verb_code, start, end, word)
                    if code == "" or code == 'NONE':
                        if '.' not in word and str(word).isalpha():
                            code, name = find_snomedct(word)
                    #print(code, end=' ')
                    #print(name)
                    line = word + ' ' + code + ' ' + name  + '\n'
                    if code == "" or code == 'NONE':
                        self.token_features[key].append("NOTFOUND")
                    else:
                        self.token_features[key].append(code)
        # Assign the type as UN for those untagged tokens
        for (key, value) in self.token_features.items():
            if len(value) < 8:
                self.token_features[key].append("UNKNOWN")
        print("Finish extracting SNOMEDCT code information")

    # Extract SNOMED code by section heading
    def extract_code_section(self):
        annotations = self.lif_loader.loadAnnotation("Sentence")
        text = self.lif_loader.data['payload']['text']['@value']
        for ann in annotations:
            if ann['label'] == "Sentence":
                start = int(ann['start'])
                end = int(ann['end'])
                sent = text[start:end+1]
                camal = camalcase(sent)
                if camal == True:
                    code, name = find_snomedct(sent)
                    key = str(start) + ' ' + str(end)
                    self.section_code[key].append(sent)
                    self.section_code[key].append(code)
                    self.section_code[key].append(name)
                    line = sent + ' ' + code + ' ' + name + '\n'

    # Extract SNOMED code by chunks
    def extract_code_chunk(self):
        annotations = self.lif_loader.loadAnnotation("Chunk")
        text = self.lif_loader.data['payload']['text']['@value']
        chunks = get_chunk(text, annotations)
        for (key, value) in chunks.items():
            if '.' not in str(value[0]) and str(value[0]).isalpha():
                code, name = find_snomedct(str(value[0]))
                line = str(value[0]) + ' ' + code + ' ' + name + '\n'
                self.chunk_code[key].append(str(value[0]))
                self.chunk_code[key].append(code)
                self.chunk_code[key].append(name)


    # Extract the SNOMED code by verb chunks
    def extract_code_verb(self):
        annotations = self.lif_loader.loadAnnotation("Chunk")
        text = self.lif_loader.data['payload']['text']['@value']
        for ann in annotations:
            if ann['label'] == 'VG':
                start = int(ann['start'])
                end = int(ann['end'])
                phrase = text[start:end]
                code, name = find_snomedct(phrase)
                key = str(start) + ' ' + str(end)
                self.verb_code[key].append(phrase)
                self.verb_code[key].append(code)
                self.verb_code[key].append(name)
                line = phrase + ' ' + code + ' ' + name + '\n'


    def filter_tokens(self):
        start_valid = False
        self.print_tokens = defaultdict(list)
        self.token_features = OrderedDict(sorted(self.token_features.items()))
        self.start_position = 0
        for (key, value) in self.token_features.items():
            token = value[0]
            tag = value[2]
            if tag != 'O' and start_valid == False:
                start_valid = True
                if self.start_position == 0:
                    self.start_position = key
            if start_valid == True and key >= self.start_position:
                self.print_tokens[key] = value
        self.token_features = OrderedDict(sorted(self.token_features.items()))
        self.token_features = self.print_tokens


    def load_sentence_end(self):
        sentence_annotations = self.lif_loader.loadAnnotation("Splitter")
        self.sentence_end = []
        for ann in sentence_annotations:
            if ann['label'] == 'Sentence':
                end = ann['end']
                self.sentence_end.append(end)

    def find_sentence_end(self, token_end):
        sent_end = False
        for end in self.sentence_end:
            if int(end) + 1 == token_end:
                sent_end = True
        return sent_end

    def save_position(self, position_filename):
        position = open(position_filename, "w+")
        self.token_features = OrderedDict(sorted(self.token_features.items()))
        for (key, value) in self.token_features.items():
            token = value[0]
            line = str(token) + " " + str(key) + "\n"
            position.write(line)
        position.close()

    def write_bio(self, filename):
        output_file = open(filename, "w+")
        self.load_sentence_end()
        for (key, feature) in self.token_features.items():
            line = ""
            for i in range(len(feature)):
                if i != 2:
                    line = line + str(feature[i]) + " "
            line = line + str(feature[2])
            output_file.write(line)
            output_file.write("\n")
            end = int(key) + feature[1]
            sent_end = self.find_sentence_end(end)
            if sent_end == True:
                output_file.write("\n")
        output_file.close()


def find_chunk(annotations, start, end):
    for chunk in annotations:
        if chunk['label'] == 'NounChunk':
            chunk_start = int(chunk['start'])
            chunk_end = int(chunk['end'])
            if start >= chunk_start \
                and end <= chunk_end:
                return True
    return False

# Load the chunker result 
def get_chunk(text, annotations):
    chunks = defaultdict(list)
    for chunk in annotations:
        if chunk['label'] == 'NounChunk':
            chunk_start = int(chunk['start'])
            chunk_end = int(chunk['end'])
            chunk = text[chunk_start:chunk_end+1]
            length = len(chunk)
            if str(chunk)[-1].isalpha() == False \
                and str(chunk)[-1].isnumeric() == False:
                chunk = chunk[:-1]
            elif chunk[length-2:] == "'s" \
                    or chunk[length-2:] == "s'":
                next_word = str(text[chunk_end+1:]).split(' ')[1]
                chunk_end = chunk_end + len(next_word) + 1
                chunk = chunk + ' ' + next_word
            if '/' in chunk:
                index = str(chunk).index('/')
                first = chunk[0:index]
                second = chunk[index+1:]
                first_end = chunk_start + len(first)
                second_start = first_end
                if find_chunk_exist(chunks, first, chunk_start, first_end) == False:
                    key = str(chunk_start) + ' ' + str(first_end)
                    chunks[key].append(first)
                if find_chunk_exist(chunks, second, second_start, chunk_end) == False:
                    key = str(second_start) + ' ' + str(chunk_end)
                    chunks[key].append(second)
            else:
                if find_chunk_exist(chunks, chunk, chunk_start, chunk_end) == False:
                    key = str(chunk_start) + ' ' + str(chunk_end)
                    chunks[key].append(chunk)
                else:
                    print(chunk)
                    print('already added')
    return chunks

# Find if the chunk already exists in the dictionary
def find_chunk_exist(chunks, text, start, end):
    exist = False
    for (key, value) in chunks.items():
        cur_start = int(str(key).split(' ')[0])
        cur_end = int(str(key).split(' ')[1])
        if cur_start <= start \
            and cur_end >= end-2 \
                and text in value:
            exist = True
    return exist

def find_snomedct(term):
    apikey = "ca310f05-53e6-4984-82fd-8691dc30174e"
    AuthClient = Authentication(apikey)
    version = "2017AB"
    tgt = AuthClient.gettgt()
    query = {'ticket': AuthClient.getst(tgt), 'targetSource': 'SNOMEDCT_US'}
    base_uri = "https://uts-ws.nlm.nih.gov/rest"
    search_uri = "/search/current?string="
    content_uri = "/content/current/CUI/"
    source = "&sabs=SNOMEDCT_US"
    search_type = '&searchType=words'
    path = base_uri + search_uri + term + search_type + source
    r = requests.get(path, params=query)
    code, name, semantic = "", "", ""
    try:
        items = json.loads(r.text)
        code, name = select_code(items['result']['results'], term)
        if code != "":
            path2 = base_uri + content_uri + code
            tgt2 = AuthClient.gettgt()
            query2 = {'ticket': AuthClient.getst(tgt2), 'targetSource': 'SNOMEDCT_US'}
            r2 = requests.get(path2, params=query2)
            try:
                items2 = json.loads(r2.text)
                semantic = items2['result']['semanticTypes'][0]['name']
            except json.decoder.JSONDecodeError:
                semantic = "UNKNOWN"
    except json.decoder.JSONDecodeError:
        code, name = "", ""
    return code, name

# # Select the maximum code and name that matches the searched term
def select_code(results, term):
    # Initialize the minimum number of matches threshold we accept
    score = 0.6
    def_score = 0.4
    code, name = "",""
    for result in results:
        title = result['name']
        temp_score, temp_def_score = calculate_score(title, term)
        if temp_score > score and temp_def_score > def_score:
            score = temp_score
            def_score = temp_def_score
            code = result['ui']
            name = title
    return code, name

# # Calculate the similarity score between SNOMED CT name and the term to be searched
def calculate_score(name, term):
    score, score_name = 0, 0
    separate = str(term).lower().split(' ')
    separate_copy = copy.deepcopy(separate)
    number = len(separate)
    definitions = str(name).lower().split(' (')[0].split(' ')
    definitions_copy = copy.deepcopy(definitions)
    number_of_definitions = len(definitions)
    for word in definitions:
        if separate_copy != None:
            if word.lower() in separate_copy:
                score_name = score_name + 1
                separate_copy.remove(word.lower())
            elif word[-1] == 's' and word[:-1].lower() in separate_copy:
                score_name = score_name + 1
                separate_copy.remove(word[:-1].lower())
            elif word.lower() == 'centimeter' and separate_copy[0] == 'cm':
                score_name = score_name + 1
                separate_copy.remove(separate_copy[0])
            else:
                for sep in separate_copy:
                    if word.lower() in sep:
                        score_name = score_name + 1
                        separate_copy.remove(sep)
                        break
            # term = str(term).replace(word.lower(), "")
    for word in separate:
        if definitions_copy != None:
            if word.lower() != 'x' and word.replace('.', '', 1).isdigit() == False \
                    and word.lower() in definitions_copy:
                score = score + 1
                definitions_copy.remove(word.lower())
            elif len(word) >= 1 and word[-1] == 's' and word[:-1].lower() in definitions_copy:
                score = score + 1
                definitions_copy.remove(word[:-1].lower())
            elif word.replace('.', '', 1).isdigit() and len(definitions_copy) == 1 \
                    and definitions_copy[0].replace('.','',1).isdigit():
                score = score + 1
                definitions_copy.remove(word.lower())
            elif word.lower() == 'cm' and definitions_copy[0] == 'centimeter':
                score = score + 1
                definitions_copy.remove(definitions_copy[0])
            elif word.lower() != 'x' and word.replace('.', '', 1).isdigit() == False:
                for defi in definitions_copy:
                    if word.lower() in defi:
                        score = score + 1
                        definitions_copy.remove(defi)
                        break
            # name = str(name).replace(word.lower(), '')
    return score/number, score_name/number_of_definitions

# Judge if a sentence is camalcase, used for judging section headings.
def camalcase(sent):
    words = str(sent).split(" ")
    camalcase = True
    for word in words:
        if len(word) > 0:
            if word[0] != word[0].upper()\
                    and word != 'of':
                camalcase = False
    return camalcase

#Look for extracted codes in sections and chunks
def find_code(section_code, chunk_code, verb_code, start, end, word):
    code, name = "", ""
    for (key, value) in section_code.items():
        sec_start = int(str(key).split(' ')[0])
        sec_end = int(str(key).split(' ')[1])
        if sec_start <= start \
            and sec_end >= end-1 \
                and word in value[0]:
            code = value[1]
            name = value[2]
    if code == "":
        for (key, value) in verb_code.items():
            verb_start = int(str(key).split(' ')[0])
            verb_end = int(str(key).split(' ')[1])
            if verb_start <= start \
                and verb_end >= end-1\
                    and word in value[0]:
                code = value[1]
                name = value[2]
    if code == "":
        for (key, value) in chunk_code.items():
            chunk_start = int(str(key).split(' ')[0])
            chunk_end = int(str(key).split(' ')[1])
            if chunk_start <= start \
                and chunk_end >= end-1 \
                    and word in value[0]:
                code = value[1]
                name = value[2]
    return code, name

def match_snomed_code(cui, filename):
    file = open(filename)
    snomed_code = ""
    for line in file.readlines():
        sections = line.split('|')
        cui_code = sections[3]
        if cui == cui_code:
            snomed_code = sections[0]
    return snomed_code

def run(arguments):
    input_filename = arguments[1]
    output_filename = arguments[2]
    pos_info = arguments[3]
    orth_info = arguments[4]
    type_info = arguments[5]
    chunk_info = arguments[6]
    code_info = arguments[7]
    left_info = arguments[8]
    right_info = arguments[9]
    # Extract Features
    extractor = FeatureExtractor(input_filename)
    extractor.extract_tokens()
    extractor.filter_tokens()
    select_number = 0
    if pos_info == "yes":
        extractor.extract_pos()
        select_number += 1
    if orth_info == "yes":
        extractor.extract_orth()
        select_number += 1
    if type_info == "yes":
        extractor.extract_type()
        select_number += 1
    if chunk_info == "yes":
        extractor.extract_chunk()
        select_number += 1
    if code_info == "yes":
        extractor.extract_code_chunk()
        extractor.extract_code_section()
        extractor.extract_code_verb()
        extractor.extract_snomedct()
        select_number += 1
    if int(left_info) != 0 or int(right_info) != 0:
        extractor.extract_neighbor(int(left_info), int(right_info), select_number)
    extractor.write_bio(output_filename)

if __name__ == "__main__":
    arguments = sys.argv
    run(arguments)