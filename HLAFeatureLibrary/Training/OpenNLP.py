import sys
import json
import glob
from Training.Client import ServiceClient
import sys
sys.path.append('..')
from PostTokenizer_OpenNLP import PostTokenizer
from PostSentenceSplitter_OpenNLP import PostSentenceSplitter
from FeatureExtractor import FeatureExtractor
from Training.merge_bio import merge_bio
import time
from Training.CRFRunner import CRFRunner
from Training.BIOtoANN import BIOtoANN
from Training.ANNtoLIF import ANNtoLIF
from Training.opennlp_wrapper import *

def text_to_lif(text):
	lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.lif_1.0.0')
	lif_result = lif_wrapper.execute(text)
	return lif_result

def opennlp_tokenizer(lif):
	OpenNLP_tokenizer = ServiceClient('http://eldrad.cs-i.brandeis.edu:8080/service_manager/wsdl/brandeis_eldrad_grid_1:opennlp.tokenizer_2.0.3')
	tokenier_lif = OpenNLP_tokenizer.execute(lif)
	tokenier_lif = str(tokenier_lif).split('\n')[1].split("'_value_1': '")[1]
	tokenier_lif = tokenier_lif[:-2]
	tokenizer_lif = tokenier_lif.replace('\\"', '\"')
	tokenizer_lif = tokenizer_lif.replace('\\\\', "\\")
	tokenizer_lif = tokenizer_lif.replace("\\'", "'")
	return tokenizer_lif

def tokenizer(lif):
	tokenizer = OpenNLP_Tokenizer(lif)
	tokenizer.run_tokenizer()
	opennlp_tokenizer_lif = json.dumps(tokenizer.lif_parser.data)
	return opennlp_tokenizer_lif

def sentence_splitter(lif):
	sentence_splitter = OpenNLP_Sentence_Splitter(lif)
	sentence_splitter.run_opennlp_sentence_splitter()
	opennlp_sentence_lif = json.dumps(sentence_splitter.lif_parser.data)
	return opennlp_sentence_lif

def pos_tagger(lif):
	pos_tagger = OpenNLP_POS_Tagger(lif)
	pos_tagger.run_opennlp_pos_tagger()
	pos_tagger_lif = json.dumps(pos_tagger.lif_parser.data)
	return pos_tagger_lif

def post_tokenizer(lif, ann):
	# print(lif[1226:1229])
	post_tokenizer = PostTokenizer(ann_filename=ann, lif_string=str(lif))
	post_tokenizer.load_ann()
	post_tokenizer.extract_tag()
	post_tokenizer_lif = json.dumps(post_tokenizer.lif_loader.data)
	return post_tokenizer_lif
	
def gate_to_lif(gate):
	lif_converter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:convert.gate2json_2.1.0')
	lif_string = lif_converter.execute(gate)
	return lif_string

def lif_to_gate(lif):
	gate_converter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:convert.json2gate_2.1.0')
	gate_string = gate_converter.execute(lif)
	return gate_string

def gate_pos_tagger(lif):
	gate_pos = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:gate.tagger_2.3.0')
	pos_lif = gate_pos.execute(lif)
	return pos_lif

def noun_chunker(gate):
	gate_noun = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:gate.npchunker_2.3.0')
	noun_gate = gate_noun.execute(gate)
	return noun_gate

def opennlp_sentence_splitter(lif):
	OpenNLP_sentence_splitter = ServiceClient('http://eldrad.cs-i.brandeis.edu:8080/service_manager/wsdl/brandeis_eldrad_grid_1:opennlp.splitter_2.0.3')
	sentence_lif = OpenNLP_sentence_splitter.execute(lif)
	sentence_lif = str(sentence_lif).split('\n')[1].split("'_value_1': '")[1]
	sentence_lif = sentence_lif[:-2]
	sentence_lif = sentence_lif.replace('\\"', '\"')
	sentence_lif = sentence_lif.replace('\\\\', "\\")
	sentence_lif = sentence_lif.replace("\\'", "'")
	return sentence_lif

def post_sentence_splitter(lif):
	post_sentence_splitter = PostSentenceSplitter(lif_string=lif)
	post_sentence_splitter.parse_sentence()
	post_sentence_splitter_lif = json.dumps(post_sentence_splitter.parser.data)
	return post_sentence_splitter_lif

def opennlp_pos_tagger(lif):
	OpenNLP_pos = ServiceClient('http://eldrad.cs-i.brandeis.edu:8080/service_manager/wsdl/brandeis_eldrad_grid_1:opennlp.postagger_2.0.3')
	pos_lif = OpenNLP_pos.execute(lif)
	pos_lif = str(pos_lif).split('\n')[1].split("'_value_1': '")[1]
	pos_lif = pos_lif[:-2]
	pos_lif = pos_lif.replace('\\"', '\"')
	pos_lif = pos_lif.replace('\\\\', "\\")
	pos_lif = pos_lif.replace("\\'", "'")
	return pos_lif

def feature_extractor(lif, output_filename, left_info, right_info, token_type=False,
					  pos_tag = False, token_length = False, orthography = False, noun_chunker = False,
					  select_number = 0, features = [], prev_features = [], next_features = [], meta_map = False,
					  time_feature = False):
	extractor = FeatureExtractor(lif_string=lif)
	extractor.extract_tokens(token_type, token_length, orthography, time_feature)
	extractor.filter_tokens()
	if pos_tag:
		extractor.extract_pos()
	if noun_chunker:
		extractor.extract_chunk()
	if meta_map:
		extractor.extract_snomedct()
	if int(left_info) != 0 or int(right_info) != 0:
		extractor.extract_neighbor(int(left_info), int(right_info), select_number, prev_features, next_features)
	extractor.write_bio(output_filename, features, int(left_info), int(right_info), prev_features, next_features)

def workflow_run(ann_filename):
	file_num = str(ann_filename).split('/')[2].split('.')[0]
	print("Processing the file number ")
	print(file_num)
	ann_file = open(ann_filename)
	ann_data = json.load(ann_file)
	input_text = ann_data["__text"]
	lif_result = text_to_lif(input_text)

	tokenizer_lif = tokenizer(lif_result)
	#print(tokenizer_lif)
	post_tokenizer_lif = post_tokenizer(tokenizer_lif, ann_filename)
	sentence_lif = sentence_splitter(post_tokenizer_lif)
	post_sentence_lif = post_sentence_splitter(sentence_lif)
	pos_lif = pos_tagger(post_sentence_lif)
	#print(pos_lif)
	feature_extractor(pos_lif, file_num, 2, 2)

def run_batch(ann_files):
	ann_list = glob.glob(ann_files)
	for ann_filename in ann_list:
		time.sleep(20)
		try:
			ann_filename = ann_filename.replace('\\','/')
			file_num = str(ann_filename).split('/')[2].split('.')[0]
			workflow_run(ann_filename)
		except:
			print("Something wrong with processing the file ", ann_filename)

if __name__ == "__main__":
	start_time = time.time()
	ann_folder = 'input/CDC_ann/Batch_2_*.ann'
	#txt_folder = 'input/CDC_batch_1_txt/*.txt'

	run_batch(ann_folder)
	bio_folder = 'output/bio/opennlp/train/Batch_2_*.bio'
	train_bio = 'output/bio/opennlp/opennlp_train_2.bio'
	train_files = glob.glob(bio_folder)
	merge_bio(train_files, train_bio)
	finish_time = time.time()
	print("Finish Processing all files! --- %s seconds ---" % (finish_time - start_time))
	start_train = time.time()
	model_file = 'output/bio/opennlp/opennlp_model_2'
	template_file = 'output/bio/opennlp/template'
	crf_runner = CRFRunner(train_bio, bio_folder, model_file=model_file, template_file=template_file, source='opennlp')
	crf_runner.crf_train()
	crf_runner.crf_test()
	print("Finish Train CRF! --- %s seconds ---" % (time.time() - start_train))
	start_eval = time.time()
	tagged_bio_folder = 'output/bio/opennlp/tagged/Batch_2_*.bio'
	tagged_bio = 'output/bio/opennlp/opennlp_tagged_2.bio'
	tagged_bio_files = glob.glob(tagged_bio_folder)
	merge_bio(tagged_bio_files, tagged_bio)
	for bio_filename in tagged_bio_files:
		bio_filename = bio_filename.replace('\\', '/')
		print(bio_filename)
		ann_converter = BIOtoANN(bio_filename, source='opennlp')
		ann_converter.extract_bio_tags()
		ann_converter.update_ann()
		ann_converter.append_header()
	tagged_ann_folder = "output/bio/opennlp/ann/Batch_2_*.ann"
	tagged_ann_files = glob.glob(tagged_ann_folder)
	for ann_filename in tagged_ann_files:
		ann_filename = ann_filename.replace('\\', '/')
		print(ann_filename)
		lif_converter = ANNtoLIF(ann_filename, source='opennlp')
		lif_converter.initialize_lif()
		lif_converter.extract_text()
		lif_converter.extract_tags()
	print("Finish Evaluate all files! --- %s seconds ---" % (time.time() - start_eval))