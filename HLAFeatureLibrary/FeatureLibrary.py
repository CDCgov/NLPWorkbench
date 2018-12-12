import sys
import glob
import os
import json
from Training.Client import ServiceClient
import Training.Stanford
import Training.OpenNLP
import Training.GATE
import TokenType

"""
This is the Feature Library which will take the input files and 
user selected features. It will determine the sequence of pipeline to be called
in LAPPS order to implement the selected features.
The input is a LIF file or a TXT file.
The output is a BIO file.
"""

class FeatureLibrary:

	def __init__(self, lif_filename="", txt_filename="", ann_filename = "", output_filename="", pipeline="", pipeline_type="gate"):
		self.pipeline_type = pipeline_type
		if txt_filename != "":
			self.txt_filename = txt_filename
			self.input_type = "TXT"
			self.load_text()
		if lif_filename != "":
			self.lif_filename = lif_filename
			self.input_type = "LIF"
			self.load_lif()
		if ann_filename != "":
			self.ann_filename = ann_filename
			self.load_ann()
		else:
			self.ann_filename = ""
			# self.find_ann()
		if txt_filename == "" and lif_filename == "" and ann_filename == "":
			print("No input file is specified!")
			exit(1)
		if output_filename != "":
			self.output_filename = output_filename
		else:
			self.output_filename = self.file_num + '.bio'
		if pipeline != "":
			self.pipeline = update_pipeline(pipeline)
		

	def find_ann(self):
		self.file_num = ""
		if self.input_type == "LIF":
			self.file_num = self.lif_filename.split('/')[1].split('.')[0]
		if self.input_type == "TXT":
			self.file_num = self.txt_filename.split('/')[1].split('.')[0]
		ann_filename = 'input/' + self.file_num + '.ann'
		if os.path.isfile(ann_filename):
			self.ann_filename = ann_filename
		else:
			print("No annotation file is found!")
			exit(1)

	# Load the LIF file
	def load_lif(self):
		lif_file = open(self.lif_filename)
		self.lif_data = json.load(lif_file)
		lif_file.close()

	# Load TXT file into LIF
	def load_text(self):
		text_file = open(self.txt_filename)
		text = text_file.read()
		text_file.close()
		lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.lif_1.0.0')
		self.lif_data = lif_wrapper.execute(text)

	def load_ann(self):
		ann_file = open(self.ann_filename)
		ann_data = json.load(ann_file)
		text = ann_data["__text"]
		ann_file.close()
		if self.pipeline_type == 'stanford':
			lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.text_1.0.0')
			self.lif_data = lif_wrapper.execute(text)
		else:
			lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.lif_1.0.0')
			self.lif_data = lif_wrapper.execute(text)

	# Load feature selection and determine and sequence of execution
	# Argument sequence: pipeline, token type, time feature, POS Tag, Noun Chunk, SNOMED code, left size, right size
	def load_feature_selection(self, args):
		self.pipeline_type = args[0]
		self.pipeline = []
		self.features = ['word']
		if self.pipeline_type == "opennlp":
			self.pipeline.append('sentence_splitter')
			self.pipeline.append('post_sentence_splitter')
		self.pipeline.append('tokenizer')
		self.pipeline.append('post_tokenizer')
		self.token_type = False
		self.pos_tag = False
		self.noun_chunk = False
		self.meta_map = False
		self.token_length = False
		self.orth_feature = False
		self.time_feature = False
		self.select_number = 0
		if args[1] == "yes":
			self.pipeline.append('token_type')
			self.token_type = True
			self.select_number += 1
			self.features.append('type')
		if args[2] == "yes":
			self.pipeline.append('time_feature')
			self.time_feature = True
			self.features.append('time')
		if args[3] == "yes":
			if self.pipeline_type != "opennlp":
				self.pipeline.append('sentence_splitter')
				self.pipeline.append('post_sentence_splitter')
			self.pipeline.append('pos_tagger')
			self.pos_tag = True
			self.features.append('pos')
			self.select_number += 1
		if args[4] == "yes":
			self.pipeline.append('noun_chunker')
			self.noun_chunk = True
			self.features.append('chunk')
			self.select_number += 1
		if args[5] == "yes":
			self.pipeline.append('meta_map')
			self.meta_map = True
			self.select_number += 1
			self.features.append('code')
		if args[6] == "yes":
			self.token_length = True
			self.features.append('length')
		if args[7] == "yes":
			self.orth_feature = True
			self.pipeline.append('orthography')
			self.features.append('orth')
			self.select_number += 1
		self.left_size = int(args[8])
		self.right_size = int(args[9])
		self.prev_features = []
		for feature in args[10].split(','):
			self.prev_features.append(feature)
		self.next_features = []
		for feature in args[11].split(','):
			self.next_features.append(feature)
		self.pipeline.append('feature_extractor')

	def load_feature_type(self, args):
		self.pipeline_type = args[0]
		self.pipeline = []
		self.select_number = 0
		self.token_type = False
		self.pos_tag = False
		self.noun_chunk = False
		self.meta_map = False
		self.token_length = False
		for type in args[1:]:
			if str(type) == "token_type":
				self.pipeline.append(type)
				self.token_type = True
				self.select_number += 1
			elif str(type) == "pos_tagger":
				self.pipeline.append('sentence_splitter')
				self.pipeline.append('post_sentence_splitter')
				self.pipeline.append('pos_tagger')
				self.pos_tag = True
				self.select_number += 1
			elif str(type) == "noun_chunker":
				self.pipeline.append('noun_chunker')
				self.noun_chunk = True
				self.select_number += 1
			elif str(type) == "meta_map":
				self.pipeline.append('meta_map')
				self.meta_map = True
				self.select_number += 1
			elif str(type) == "token_length":
				self.token_length = True
		self.pipeline.append('feature_extractor')
		self.left_size = args[len(args)-2]
		self.right_size = args[len(args)-1]

	# Assign the process to the correct pipeline
	def process_pipeline(self):
		if self.pipeline_type == "stanford":
			self.execute_pipeline(Training.Stanford)
		elif self.pipeline_type == "opennlp":
			self.execute_pipeline(Training.OpenNLP)
		elif self.pipeline_type == "gate":
			self.execute_pipeline(Training.GATE)

	def update_pipeline(self, pipeline):
		pipeline_result = pipeline.split(",")
		return pipeline_result

	# Execute the pipeline to call the corresponding function
	def execute_pipeline(self, Pipeline):
		self.lif_output = {}
		# if self.pipeline_type != "gate":
		#     lif_tokenize = Pipeline.tokenizer(self.lif_data)
		#     self.lif_output['tokenizer'] = lif_tokenize
		#     lif_post_tokenizer = Pipeline.post_tokenizer(lif_tokenize, self.ann_filename)
		#     self.lif_output['post_tokenizer'] = lif_post_tokenizer
		# else:
		#     gate_tokenize = Pipeline.tokenizer(self.lif_data)
		#     lif_tokenize = Pipeline.gate_to_lif(gate_tokenize)
		#     self.lif_output['tokenizer'] = lif_tokenize
		#     print("Running Post Processor for GATE")
		#     print(self.ann_filename)
		#     lif_post_tokenizer = Pipeline.post_tokenizer(lif_tokenize, self.ann_filename)
		#     self.lif_output['post_tokenizer'] = lif_post_tokenizer
		#     temp_file = open('output/post_token.lif', "w+")
		#     temp_file.write(lif_post_tokenizer)
		#     temp_file.close()
		print(self.pipeline)
		self.lif_process = []
		for i in range(len(self.pipeline)):
			name = self.pipeline[i]
			# The input of this process is the output of the last process
			# lif_list = list(self.lif_output.values())
			# lif_input = lif_list[len(lif_list) - 1]
			lif_input = ""
			if len(self.lif_process) == 0:
				lif_input = self.lif_data
			else:
				# print(name)
				# print(self.lif_process)
				lif_input = self.lif_output[self.lif_process[len(self.lif_process)-1]]
				# print(lif_input)
			if name == "tokenizer":
				print("Running Tokenizer")
				if self.pipeline_type != "gate":
					lif_tokenize = Pipeline.tokenizer(lif_input)
					self.lif_output['tokenizer'] = lif_tokenize
					self.lif_process.append('tokenizer')
				else:
					gate_tokenize = Pipeline.tokenizer(lif_input)
					lif_tokenize = Pipeline.gate_to_lif(gate_tokenize)
					self.lif_output['tokenizer'] = lif_tokenize
					self.lif_process.append('tokenizer')
			if name == "post_tokenizer":
				print("Running Post Tokenizer")
				lif_post_tokenizer = Pipeline.post_tokenizer(lif_input, self.ann_filename)
				self.lif_output['post_tokenizer'] = lif_post_tokenizer
				self.lif_process.append('post_tokenizer')
			if name == "token_type":
				if self.pipeline_type != "gate":
					print("Extracting token type")
					# print(lif_input)
					lif_result = TokenType.token_type_caller(lif_string=lif_input)
					# print("Finish Processing token type")
					# print(lif_result)
					self.lif_output['token_type'] = lif_result
					# print("Finish Adding result")
					self.lif_process.append('token_type')
					# print("Finish Adding processed module")
			if name == "time_feature":
				lif_result = TokenType.time_caller(lif_string=lif_input)
				self.lif_output['time_feature'] = lif_result
				self.lif_process.append('time_feature')
			if name == "sentence_splitter":
				print("Running SBD")
				if self.pipeline_type != "gate":
					lif_result = Pipeline.sentence_splitter(lif_input)
					self.lif_output['sentence_splitter'] = lif_result
					self.lif_process.append('sentence_splitter')
				else:
					gate_input = Pipeline.lif_to_gate(lif_input)
					gate_result = Pipeline.sentence_splitter(gate_input)
					lif_result = Pipeline.gate_to_lif(gate_result)
					self.lif_output['sentence_splitter'] = lif_result
					self.lif_process.append('sentence_splitter')
					temp_file = open('output/sbd.lif', "w+")
					temp_file.write(lif_result)
					temp_file.close()
			if name == "post_sentence_splitter":
				print("Running Post Processor of SBD")
				lif_result = Pipeline.post_sentence_splitter(lif_input)
				self.lif_output['post_sentence_splitter'] = lif_result
				self.lif_process.append('post_sentence_splitter')
			if name == "pos_tagger":
				print("Running POS")
				if self.pipeline_type != "gate":
					lif_result = Pipeline.pos_tagger(lif_input)
					self.lif_output['pos_tagger'] = lif_result
					self.lif_process.append('pos_tagger')
				else:
					gate_input = Pipeline.lif_to_gate(lif_input)
					gate_result = Pipeline.pos_tagger(gate_input)
					lif_result = Pipeline.gate_to_lif(gate_result)
					self.lif_output['pos_tagger'] = lif_result
					self.lif_process.append('pos_tagger')
					temp_file = open('output/pos.lif', "w+")
					temp_file.write(lif_result)
					temp_file.close()
			if name == "noun_chunker":
				if self.pipeline_type != "gate":
					print("Running Noun Chunker")
					gate_input = Pipeline.lif_to_gate(lif_input)
					gate_pos = Pipeline.gate_pos_tagger(gate_input)
					gate_result = Pipeline.noun_chunker(gate_pos)
					lif_result = Pipeline.gate_to_lif(gate_result)
					self.lif_output['noun_chunker'] = lif_result
					self.lif_process.append('noun_chunker')
				else:
					print("Running Noun Chunker")
					gate_input = Pipeline.lif_to_gate(lif_input)
					gate_result = Pipeline.noun_chunker(gate_input)
					lif_result = Pipeline.gate_to_lif(gate_result)
					self.lif_output['noun_chunker'] = lif_result
					self.lif_process.append('noun_chunker')
					temp_file = open('output/last.lif', "w+")
					temp_file.write(lif_result)
					temp_file.close()
			if name == "meta_map":
				continue
			if name == "orthography":
				if self.pipeline_type != "gate":
					print("Extracting Orthography")
					lif_result = TokenType.orthography_caller(lif_string=lif_input)
					self.lif_output['orthography'] = lif_result
					self.lif_process.append('orthography')
					temp_file = open('output/last.lif', "w+")
					temp_file.write(lif_result)
					temp_file.close()
			if name == "feature_extractor":
				print("Extracting features")
				# print(lif_input)
				Pipeline.feature_extractor(lif = lif_input, output_filename = self.output_filename,
										   left_info = self.left_size
										   , right_info = self.right_size, token_type = self.token_type,
										   pos_tag = self.pos_tag
										   , token_length = self.token_length, orthography = self.orth_feature,
										   select_number = self.select_number, noun_chunker= self.noun_chunk,
										   features = self.features, prev_features = self.prev_features,
										   next_features = self.next_features, meta_map = self.meta_map, 
										   time_feature = self.time_feature)

def invoke_feature_library(arguments, pipeline=""):
	input_filename = arguments[0]
	output_filename = arguments[1]
	feature_arguments = arguments[2:]
	feature_library = None
	print(arguments)
	if input_filename.endswith('.txt'):
		feature_library = FeatureLibrary(txt_filename=input_filename, output_filename=output_filename, pipeline=pipeline,
		 pipeline_type=feature_arguments[0])
	elif input_filename.endswith('.lif'):
		feature_library = FeatureLibrary(lif_filename=input_filename, output_filename=output_filename, pipeline=pipeline,
			pipeline_type=feature_arguments[0])
	elif input_filename.endswith('.ann'):
		feature_library = FeatureLibrary(ann_filename=input_filename, output_filename=output_filename, pipeline=pipeline,
			pipeline_type=feature_arguments[0])
	if pipeline == "":
		feature_library.load_feature_selection(feature_arguments)
	feature_library.process_pipeline()

if __name__ == "__main__":
	arguments = sys.argv[1:]
	invoke_feature_library(arguments)