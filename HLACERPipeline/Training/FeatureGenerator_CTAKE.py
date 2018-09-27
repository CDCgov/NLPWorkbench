import xml.etree.ElementTree as ET
from collections import defaultdict, OrderedDict
import re

"""
This file reads the result returned from the cTAKE Service and computes a set of features.
The features generated are:
token name, length, POS tag, capitalisation, canonical form, number type, number position, 
SNOMED concept code, term code, mention type, dependency, semantic relation (argument-predicate relationship), 
parser information (level, nearest parse group) features for 2 tokens before and 2 tokens after
"""
class cTakeFeatureGenerator:

	def __init__(self, input_string, ann_data, position_filename, bio_filename):
		self.input_string = input_string
		self.data = ET.fromstring(input_string)
		self.ann_data = ann_data
		self.position_filename = position_filename
		self.bio_filename = bio_filename
		self.feature_data = {}
		self.type = []
		self.sentence = []
		self.get_text()

	def extract_unique_type(self):
		for child in self.data:
			self.type.append(child.tag)
		self.type = list(set(self.type))

	def get_text(self):
		for child in self.data:
			if child.tag == "uima.cas.Sofa":
				self.text = child.attrib["sofaString"].replace(r'\\', '\\')
				return

	def extract_token(self):
		for child in self.data:
			if "tokenNumber" in child.attrib.keys():
				start = int(child.attrib["begin"])
				end = int(child.attrib["end"])
				length = end - start
				if start not in self.feature_data.keys():
					word = self.text[start:end]
					if word == '\n':
						word = 'NEWLINE'
					elif word == ' ':
						word = 'SPACE'
					elif word == '\t':
						word = 'TAB'
					info = {}
					info['token'] = word.replace('\n', 'NEWLINE').replace(' ', 'SPACE').replace('\t','TAB')
					info['length'] = length
					if "partOfSpeech" in child.attrib.keys():
						info['pos'] = child.attrib["partOfSpeech"]
					else:
						info['pos'] = 'POSUNKNOWN'
					if "capitalization" in child.attrib.keys():
						info["capitalization"] = child.attrib["capitalization"]
					else:
						info['capitalization'] = 'CAPUNKNOWN'
					if 'canonicalForm' in child.attrib.keys():
						info['canonicalForm'] = child.attrib["canonicalForm"]
					else:
						info['canonicalForm'] = 'CANONICALUNKNOWN'
					if "numPosition" in child.attrib.keys():
						info["numPosition"] = child.attrib["numPosition"]
					else:
						info["numPosition"] = 'NUMUNKNOWN'
					if "numType" in child.attrib.keys():
						info["numType"] = child.attrib["numType"]
					else:
						info["numType"] = 'TYPEUNKNOWN'
					self.feature_data[start] = info
				else:
					print("Tokens have the same starting point ", start, end)
		self.extract_class()
		# print(self.feature_data)

	def extract_info(self):
		for child in self.data:
			if "Mention" in child.tag and "_ref_ontologyConceptArr" in child.attrib.keys():
				start = int(child.attrib["begin"])
				end = int(child.attrib["end"])
				ref_num = child.attrib["_ref_ontologyConceptArr"]
				# print(ref_num)
				ref_id = self.data.findall(".//*[@_id='%s']/i" % ref_num)
				c_code = []
				t_code = []
				for rid in ref_id:
					rid = rid.text
					concept = self.data.findall(".//*[@_id='%s']" % rid)
					if len(concept) > 0:
						c_code.append(concept[0].attrib["cui"])
						t_code.append(concept[0].attrib["tui"])
				# t_code = ','.join(t_code)
				# c_code = ','.join(c_code)
				type_id = child.attrib["typeID"]
				for token_start in self.feature_data.keys():
					if token_start >= start and token_start < end:
						if "concept_code" in self.feature_data[start].keys():
							self.feature_data[start]["concept_code"].extend(c_code)
							self.feature_data[start]["type_code"].extend(t_code)
							self.feature_data[start]["mention_type"].append(type_id)
						else:
							self.feature_data[start]["concept_code"] = c_code
							self.feature_data[start]["type_code"] = t_code
							self.feature_data[start]["mention_type"] = [type_id]
			elif 'Sentence' in child.tag:
				end = int(child.attrib["end"])
				self.sentence.append(end)
			elif "ConllDependencyNode" in child.tag:
				start = int(child.attrib["begin"])
				end = int(child.attrib["end"])
				if "deprel" in child.attrib.keys():
					deprel = child.attrib["deprel"]
					for token_start in self.feature_data.keys():
						if token_start >= start and token_start < end:
							self.feature_data[start]["dependency"] = deprel
			elif "TopTreebankNode" in child.tag:
				self.extract_parse(child)
			elif "SemanticRoleRelation" in child.tag:
				self.extract_semantic(child)
		# print(self.feature_data)

	def extract_semantic(self, child):
		arg_id = child.attrib["_ref_argument"]
		pred_id = child.attrib["_ref_predicate"]
		argument = self.data.findall(".//*[@_id='%s']" % arg_id)
		predicate = self.data.findall(".//*[@_id='%s']" % pred_id)
		# print(argument, predicate)
		if len(argument) == 0 or len(predicate) == 0:
			return
		argument_begin = int(argument[0].attrib["begin"])
		argument_end = int(argument[0].attrib["end"])
		label = argument[0].attrib["label"]
		predicate_begin = int(predicate[0].attrib["begin"])
		predicate_end = int(predicate[0].attrib["end"])
		frame = predicate[0].attrib["frameSet"]
		for token_start in self.feature_data.keys():

			if token_start >= argument_begin and token_start < argument_end:
				self.feature_data[token_start]["semantic_role"] = "Argument"
				self.feature_data[token_start]["frame_set"] = frame
				self.feature_data[token_start]["semantic_label"] = label
			if token_start >= predicate_begin and token_start < predicate_end:
				self.feature_data[token_start]["semantic_role"] = "Predicate"
				self.feature_data[token_start]["frame_set"] = frame
				self.feature_data[token_start]["semantic_label"] = label 


	def extract_parse(self, child):
		start = int(child.attrib["begin"])
		end = int(child.attrib["end"])
		if "treebankParse" in child.attrib.keys():
			parse_string = child.attrib["treebankParse"]
			level = 0
			combine = []
			last_parse = ''
			parse_splitted = list(filter(lambda a: a != '', re.split('(\)| |\()', parse_string)))
			for parse in parse_splitted:
				if parse == '(':
					level += 1
				elif parse == ')':
					level -=1
				elif parse != ' ':
					if last_parse == '(':
						combine.append(parse)
					elif last_parse == ' ':
						parent = combine[-2]
						word = parse
						if parse == '-LRB-':
							word = '('
						elif parse == '-RRB-':
							word = ')'
						for token_start in self.feature_data.keys():
							if token_start >= start and token_start < end:
								if self.feature_data[token_start]["token"] == word:
									self.feature_data[token_start]["parse_level"] = level
									self.feature_data[token_start]["parse_parent"] = parent
				last_parse = parse

	def extract_class(self):
		for tag_type in self.ann_data.keys():
			if tag_type != "__text":
				for ann in self.ann_data[tag_type]:
					tag_type = tag_type.split(' (')[0].replace(" ", "_")
					start = int(ann["__extent"][0])
					end = int(ann["__extent"][1])
					for token_start in self.feature_data.keys():
						if token_start >= start and token_start < end:
							self.feature_data[token_start]["TAG"] = tag_type

	def save_position(self):
		start_tag = False
		self.feature_data = OrderedDict(sorted(self.feature_data.items()))
		position_file = open(self.position_filename, "w+")
		bio_file = open(self.bio_filename, "w+")
		last_tag = 'O'
		last_word = 'none'
		for start, item in self.feature_data.items():
			if 'TAG' in item.keys() and not start_tag:
				start_tag = True
				self.start_position = start
			if start_tag:
				word = item['token']
				length = str(item['length'])
				tag = 'O'
				if 'TAG' in item.keys():
					tag = item['TAG']
				line = word + ' ' + str(start) + '\n'
				position_file.write(line)
				if start in self.sentence and tag != last_tag:
					bio_file.write("\n")
					if self.text[start-1] == '\n':
						last_word = 'newline'
					else:
						last_word = 'none'
				# last_tag = tag

				# Set tag
				original_tag = tag
				if last_tag == tag and tag != 'O':
					tag = 'I-'+ tag
				elif last_tag != tag and tag != 'O':
					tag = 'B-' + tag
				last_tag = original_tag
				# Extract Features
				feature_line = [word, length, item['pos'], item['capitalization'], item['canonicalForm'], item['numPosition'], item['numType']]
				if "concept_code" in item.keys():
					concept_code = ",".join(item["concept_code"])
					type_code = ",".join(item["type_code"])
					mention_type = ",".join(item["mention_type"])
					feature_line.extend([concept_code, type_code, mention_type])
				else:
					feature_line.extend(["SCTNOTFOUND", "SCTNOTFOUND", "SCTNOTFOUND"])
				if "dependency" in item.keys():
					feature_line.append(item["dependency"])
				else:
					feature_line.append("DEPUNKNOWN")
				if "parse_level" in item.keys():
					feature_line.append(str(item["parse_level"]))
				else:
					feature_line.append("PARSEUNKNOWN")
				if "parse_parent" in item.keys():
					feature_line.append(str(item["parse_parent"]))
				else:
					feature_line.append("PARSEUNKNOWN")
				if "semantic_role" in item.keys():
					semantic_role = item["semantic_role"]
					semantic_label = item["semantic_label"]
					frame_set = item["frame_set"]
					feature_line.extend([semantic_role, frame_set, semantic_label])
				else:
					feature_line.extend(["SEMANTICUNKNOWN", "SEMANTICUNKNOWN", "SEMANTICUNKNOWN"])
				feature_line.append(last_word)
				feature_line.append(tag)
				last_word = word
				feature_line_str = " ".join(feature_line)
				feature_line_str = feature_line_str + "\n"
				bio_file.write(feature_line_str)
		bio_file.close()
		position_file.close()
