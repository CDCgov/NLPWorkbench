from LifFileParser import LifFileParser
import sys
from collections import defaultdict, OrderedDict
from FeatureExtractor_GATE import find_snomedct
from MetaMap import find_snomed, load_snomed
import copy
'''
This file implements a feature extractor tool which allows users to specify
the type of features they want to include.
The return file will be in BIO format which could be passed onto the CRF learn.
'''

snomed_data = load_snomed()
stopword_file = open('data/stopwords')
stopwords = [line[:-1] for line in stopword_file.readlines()]

class FeatureExtractor:
	'''
	This class implements all required function for extracting different features.
	The features are stored in a dictionary with key: start + ' ' + end
	The values of the feature dictionary is as the following sequence:
	token, length, semantic tag, pos, features for token-1, features for token-2, ... ,
	features for token-n, features for token+1, ..., features for token+n.
	These features will be written into the output BIO file with semantic tag at the last column.
	'''

	def __init__(self, filename="", lif_string=""):
		self.input_filename = filename
		if filename != "":
			self.lif_loader = LifFileParser(filename)
		if lif_string != "":
			self.lif_loader = LifFileParser(string=lif_string)
		self.token_features = defaultdict()

	# Extract token and semantic tag information 
	def extract_tokens(self, token_type=False, token_length=True, orthography = False, time_feature = False):
		annotations = self.lif_loader.loadAnnotation("Token")
		if annotations == []:
			print("Cannot find token result!")
		else:
			for ann in annotations:
				if 'features' in ann.keys() and 'word' in ann['features'].keys():
					word = ann['features']['word']
					if word == ' ':
						word = 'space'
					elif word == '\t':
						word = 'TAB'
					start = int(ann['start'])
					end = ann['end']
					key = int(ann['start'])
					length = int(end) - int(start)
					value = {}
					value['word'] = word
					if token_length == True:
						value['length'] = length
					if 'semanticTag' in ann['features'].keys():
						semantic_tag = ann['features']['semanticTag']
						value['semanticTag'] = semantic_tag
					if token_type == True:
						if 'TokenType' in ann['features'].keys():
							tokenType = ann['features']['TokenType']
							value['type'] = tokenType
						else:
							value['type'] = "UNKNOWN"
					if orthography == True:
						if 'orth' in ann['features'].keys():
							orth = ann['features']['orth']
							value['orth'] = orth
						else:
							value['orth'] = "UNKNOWN"
					if time_feature == True:
						if 'Time' in ann['features'].keys():
							time = ann['features']['Time']
							value['time'] = time
						else:
							value['time'] = False
					self.token_features[key] = value


	# Extract POS tag information
	def extract_pos(self):
		annotations = self.lif_loader.loadAnnotation("Tagger")
		if annotations == []:
			print("Cannot find POS tag result!")
		else:
			# print(annotations)
			for ann in annotations:
				start = int(ann['start'])
				end = int(ann['end'])
				if start < self.start_position:
					continue
				key = int(ann['start'])
				if 'features' in ann.keys() and key in self.token_features.keys():
					if 'pos' in ann['features'].keys():
						pos = ann['features']['pos']
						self.token_features[key]['pos'] = pos
		# Assign the POS as UN for those untagged tokens
		
		for (key, value) in self.token_features.items():
			if 'pos' not in value.keys():
				self.token_features[key]['pos'] = "UNKNOWN"

	def extract_chunk(self):
		annotations = self.lif_loader.loadAnnotation("Token")
		text = self.lif_loader.data['payload']['text']['@value']
		if annotations == []:
			print("Cannot find token result!")
		else:
			for ann in annotations:
				if "Token" in ann['@type'] and 'word' in ann['features'].keys():
					start = int(ann['start'])
					end = int(ann['end'])
					if start < self.start_position:
						continue
					word = ann['features']['word']
					if word == ' ':
						word = 'space'
					elif word == '\t':
						word = 'TAB'
					chunk = find_chunk(annotations, start, end)
					key = start
					self.token_features[key]['chunk'] = chunk

	def extract_neighbor(self, left_number, right_number, select_number, prev_feautres, next_features):
		# print("Start extract tokens nearby information")
		token_annotations = self.lif_loader.loadAnnotation("Token")
		self.token_features = OrderedDict(sorted(self.token_features.items()))
		self.copy_features = copy.deepcopy(self.token_features)
		self.number_of_tokens = len(list(self.token_features.keys()))
		for i in range(self.number_of_tokens):
			key = list(self.token_features.keys())[i]
			# Extract features for tokens on the left
			for j in range(1, left_number+1):
				if i - j < 0:
					for feat in prev_feautres:
						if feat != 'semanticTag':
							prev_feat = 'prev_'*j + feat
							self.token_features[key][prev_feat] = 'start'
				else:
					prev_key = list(self.token_features.keys())[i - j]
					for feat in prev_feautres:
						if feat != 'semanticTag':
							value = self.copy_features[prev_key][feat]
							prev_feat = 'prev_'*j + feat
							self.token_features[key][prev_feat] = value
			# Extract features for tokens on the right
			for j in range(1, right_number + 1):
				if i + j >= self.number_of_tokens:
					for feat in next_features:
						if feat != 'semanticTag':
							next_feat = 'next_'*j + feat
							self.token_features[key][next_feat] = 'end'
				else:
					next_key = list(self.token_features.keys())[i+j]
					for feat in next_features:
						if feat != 'semanticTag':
							value = self.copy_features[next_key][feat]
							next_feat = 'next_'*j + feat
							self.token_features[key][next_feat] = value
		print("Finish extract tokens nearby information")

	def filter_tokens(self):
		start_valid = False
		self.print_tokens = defaultdict()
		self.start_position = 9223372036854775807
		self.token_features = OrderedDict(sorted(self.token_features.items()))
		for (key, value) in self.token_features.items():
			token = value['word']
			if (token == "Gross" or token == "Path") and start_valid == False:
				start_valid = True
				if self.start_position == 9223372036854775807:
					self.start_position = key
			if start_valid == True and key >= self.start_position:
				self.print_tokens[key] = value
		self.token_features = self.print_tokens


	def save_position(self, position_filename):
		position = open(position_filename, "w+")
		self.print_tokens = OrderedDict(sorted(self.token_features.items()))
		for (key, value) in self.print_tokens.items():
			token = value[0]
			line = str(token) + " " + str(key) + "\n"
			position.write(line)
		position.close()

	def load_sentence_end(self):
		sentence_annotations = self.lif_loader.loadAnnotation("Splitter")
		self.sentence_end = []
		for ann in sentence_annotations:
			if 'Sentence' in ann['@type']:
				end = ann['end']
				self.sentence_end.append(end)

	def find_sentence_end(self, token_end):
		sent_end = False
		for end in self.sentence_end:
			if int(end) == token_end:
				sent_end = True
		return sent_end

	def write_bio(self, filename, features, left_number, right_number, prev_feautres, next_features):
		output_file = open(filename, "w+")
		self.load_sentence_end()
		for (key, feature) in self.token_features.items():
			line = ""
			for feat in features:
				line = line + str(feature[feat]) + ' '
			for j in range(1, left_number + 1):
				for feat in prev_feautres:
					prev_key = 'prev_'*j + feat
					line = line + str(feature[prev_key]) + ' '
			for j in range(1, right_number + 1):
				for feat in next_features:
					next_key = 'next_'*j + feat
					line = line + str(feature[next_key]) + ' '
			if 'semanticTag' in feature.keys():
				line = line + str(feature['semanticTag'])
			else:
				line = line[:-1]
			output_file.write(line)
			output_file.write("\n")
			end = int(key) + int(feature['length'])
			sent_end = self.find_sentence_end(end)
			if sent_end == True:
				output_file.write("\n")
		output_file.close()

	def extract_snomedct(self):
		print("Start extracting SNOMEDCT code information")
		annotations = self.lif_loader.loadAnnotation("Token")
		if annotations == []:
			print("Cannot find token result!")
		else:
			start_extract = False
			for ann in annotations:
				key = int(ann['start'])
				if int(ann['start']) in self.token_features.keys() and 'code' not in self.token_features[key].keys():
					start = int(ann['start'])
					end = int(ann['end'])
					key = int(ann['start'])
					if key < self.start_position:
						continue
					word = self.token_features[key]['word']
					code, name = "", ""
					if '.' not in word and str(word).isalpha() and word not in stopwords and len(word) > 1:
						code, name = find_snomed(snomed_data, word)
					line = word + ' ' + code + ' ' + name  + '\n'
					if code == "" or code == 'NONE':
						self.token_features[key]['code'] = "SCTNOTFOUND"
					else:
						self.token_features[key]['code'] = code
		# Assign the type as UN for those untagged tokens
		for (key, value) in self.token_features.items():
			if 'code' not in value.keys():
				self.token_features[key]['code'] = "SCTNOTFOUND"
		print("Finish extracting SNOMEDCT code information")

def find_chunk(annotations, start, end):
	for chunk in annotations:
		if chunk['label'] == 'NounChunk':
			chunk_start = int(chunk['start'])
			chunk_end = int(chunk['end'])
			if start >= chunk_start \
				and end <= chunk_end:
				return True
	return False
	
def run(arguments):
	input_filename = arguments[1]
	output_filename = arguments[2]
	pos_info = arguments[3]
	code_info = arguments[4]
	left_info = arguments[5]
	right_info = arguments[6]
	# Extract Features
	extractor = FeatureExtractor(input_filename)
	extractor.extract_tokens()
	extractor.filter_tokens()
	select_number = 0
	if pos_info == "yes":
		extractor.extract_pos()
		select_number += 1
	if code_info == "yes":
		extractor.extract_snomedct()
		select_number += 1
	if int(left_info) != 0 or int(right_info) != 0:
		extractor.extract_neighbor(int(left_info), int(right_info), select_number)
	extractor.write_bio(output_filename)

if __name__ == "__main__":
	arguments = sys.argv
	run(arguments)
