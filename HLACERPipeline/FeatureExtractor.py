from LifFileParser import LifFileParser
import sys
from collections import defaultdict, OrderedDict
from FeatureExtractor_GATE import find_snomedct
from MetaMap import find_snomed

'''
This file implements a feature extractor tool which allows users to specify
the type of features they want to include.
The return file will be in BIO format which could be passed onto the CRF learn.
'''

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
		self.token_features = defaultdict(list)

	# Extract token and semantic tag information 
	def extract_tokens(self):
		annotations = self.lif_loader.loadAnnotation("Token")
		if annotations == []:
			print("Cannot find token result!")
		else:
			for ann in annotations:
				if 'features' in ann.keys() and 'word' in ann['features'].keys() and 'semanticTag' in ann['features'].keys():
					word = ann['features']['word']
					start = ann['start']
					end = ann['end']
					key = int(ann['start'])
					length = int(end) - int(start)
					self.token_features[key].append(word)
					self.token_features[key].append(length)
					if 'semanticTag' in ann['features'].keys():
						semantic_tag = ann['features']['semanticTag']
						self.token_features[key].append(semantic_tag)
					else:
						print("Semantic tag information cannot be found!")

	# Extract POS tag information
	def extract_pos(self):
		annotations = self.lif_loader.loadAnnotation("Tagger")
		if annotations == []:
			print("Cannot find POS tag result!")
		else:
			for ann in annotations:
				start = int(ann['start'])
				end = ann['end']
				if start < self.start_position:
					continue
				key = int(ann['start'])
				if 'features' in ann.keys() and key in self.token_features.keys() and len(self.token_features[key]) < 4:
					if 'word' in ann['features'].keys() and 'pos' in ann['features'].keys():
						word = ann['features']['word']
						pos = ann['features']['pos']
						if key in self.token_features.keys() \
								and self.token_features[key][0] == word:
							self.token_features[key].append(pos)
		# Assign the POS as UN for those untagged tokens
		for (key, value) in self.token_features.items():
			if len(value) < 4:
				self.token_features[key].append("UN")

	def extract_neighbor(self, left_number, right_number, select_number):
		#print("Start extract tokens nearby information")
		token_annotations = self.lif_loader.loadAnnotation("Token")
		self.token_features = OrderedDict(sorted(self.token_features.items()))
		self.number_of_tokens = len(list(self.token_features.keys()))
		#print(self.token_features)
		for i in range(self.number_of_tokens):
			ann = token_annotations[i]
			start = int(ann['start'])
			end = ann['end']
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
					next_key = list(self.token_features.keys())[i + j]
					for k in range(select_number+3):
						if k != 2:
							next_feature = self.token_features[next_key][k]
							self.token_features[key].append(next_feature)

	def filter_tokens(self):
		start_valid = False
		self.print_tokens = defaultdict(list)
		self.start_position = 9223372036854775807
		self.token_features = OrderedDict(sorted(self.token_features.items()))
		for (key, value) in self.token_features.items():
			token = value[0]
			tag = value[2]
			if tag != 'O' and start_valid == False:
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

	def extract_snomedct(self):
		#print("Start extracting SNOMEDCT code information")
		annotations = self.lif_loader.loadAnnotation("Token")
		if annotations == []:
			print("Cannot find token result!")
		else:
			start_extract = False
			for ann in annotations:
				if int(ann['start']) in self.token_features.keys() and len(self.token_features[int(ann['start'])]) < 5:
					start = int(ann['start'])
					end = int(ann['end'])
					key = ann['start']
					if key < self.start_position:
						continue
					word = self.token_features[key][0]
					#print(word, end=' ')
					code, name = "", ""
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
			if len(value) < 5:
				self.token_features[key].append("UNKNOWN")
		#print("Finish extracting SNOMEDCT code information")

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
