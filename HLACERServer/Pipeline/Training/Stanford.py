import sys
import json
import glob
from Pipeline.Training.Client import ServiceClient
import time
import sys
from Pipeline.TextToLif import TextToLif
from Pipeline.PostTokenizer_Stanford import PostTokenizer
from Pipeline.PostSentenceSplitter_Stanford import PostSentenceSplitter
from Pipeline.FeatureExtractor import FeatureExtractor
from Pipeline.Training.merge_bio import merge_bio
from Pipeline.Training.CRFRunner import CRFRunner
from Pipeline.Training.BIOtoANN import BIOtoANN
from Pipeline.Training.ANNtoLIF import ANNtoLIF
from Pipeline.Training.StanfordPOSTagger import LAPPS_StanfordPOSTagger
from Pipeline.Training.stanford_wrapper import *

def text_to_lif(text):
	converter = TextToLif(text)
	converter.convert_lif()
	lif_result = converter.lif_string
	return lif_result

def tokenizer(lif):
	stanford_tokenizer = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:stanford.tokenizer_2.0.0')
	tokenier_lif = stanford_tokenizer.execute(lif)
	return tokenier_lif

def stanford_tokenizer(lif):
	st = StanfordTokenizer(lif)
	st.call_tokenizer()
	lif_result = json.dumps(st.lif_parser.data)
	return lif_result

def stanford_sentence_splitter(lif):
	sentence = StanfordSentenceSplitter(lif)
	sentence.call_splitter()
	lif_result = json.dumps(sentence.lif_parser.data)
	return lif_result

def post_tokenizer(lif):
	post_tokenizer = PostTokenizer(lif_string=lif)
	post_tokenizer.extract_info()
	post_tokenizer_lif = json.dumps(post_tokenizer.lif_loader.data)
	return post_tokenizer_lif

def sentence_splitter(lif):
	stanford_sentence_splitter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:stanford.splitter_2.0.0')
	sentence_lif = stanford_sentence_splitter.execute(lif)
	return sentence_lif

def post_sentence_splitter(lif):
	post_sentence_splitter = PostSentenceSplitter(lif_string=lif)
	post_sentence_splitter.parse_sentence()
	post_sentence_splitter_lif = json.dumps(post_sentence_splitter.parser.data)
	return post_sentence_splitter_lif

def pos_tagger(lif):
	stanford_pos = ServiceClient('http://eldrad.cs-i.brandeis.edu:8080/service_manager/wsdl/brandeis_eldrad_grid_1:stanfordnlp.postagger_2.0.3')
	pos_lif = stanford_pos.execute(lif)
	return pos_lif

def stanford_pos_tagger(lif):
	tagger = LAPPS_StanfordPOSTagger(lif_string=lif)
	tagger.pos_tagger()
	pos_lif = json.dumps(tagger.lif_parser.data)
	return pos_lif

def feature_extractor(lif, left_info, right_info, output_filename, position_filename):
	extractor = FeatureExtractor(lif_string=lif)
	extractor.extract_tokens()
	extractor.filter_tokens()
	# extractor.save_position(position_filename)
	extractor.extract_pos()
	# extractor.extract_snomedct()
	if int(left_info) != 0 or int(right_info) != 0:
		extractor.extract_neighbor(int(left_info), int(right_info), 1)
	extractor.write_bio(output_filename, position_filename)

def workflow_run(ann_filename):
	ann_file = open(ann_filename)
	ann_data = json.load(ann_file)
	input_text = ann_data["__text"]
	lif_result = text_to_lif(input_text)

	tokenizer_lif = tokenizer(lif_result)
	post_tokenizer_lif = post_tokenizer(tokenizer_lif)
	sentence_lif = sentence_splitter(post_tokenizer_lif)
	post_sentence_lif = post_sentence_splitter(sentence_lif)
	pos_lif = pos_tagger(post_sentence_lif)

def run_batch(ann_files):
	ann_list = glob.glob(ann_files)
	for ann_filename in ann_list:
		time.sleep(20)
		ann_filename = ann_filename.replace('\\','/')
		file_num = str(ann_filename).split('/')[2].split('.')[0]
		workflow_run(ann_filename)

if __name__ == "__main__":
	start_time = time.time()
	ann_folder = 'input/CDC_batch_1_ann/*.ann'
	run_batch(ann_folder)
	bio_folder = 'output/bio/stanford/train/*.bio'
	train_bio = 'output/bio/stanford/stanford_train.bio'
	train_files = glob.glob(bio_folder)
	merge_bio(train_files,train_bio)
	finish_time = time.time()
	print("Finish Processing all files! --- %s seconds ---" % (finish_time - start_time))
	start_train = time.time()
	model_file = 'output/bio/stanford/stanford_model'
	template_file = 'output/bio/stanford/template'
	crf_runner = CRFRunner(train_bio, bio_folder, model_file=model_file, template_file=template_file, source='stanford')
	crf_runner.crf_train()
	crf_runner.crf_test()
	print("Finish Train CRF! --- %s seconds ---" % (time.time() - start_train))
	start_eval = time.time()
	tagged_bio_folder = 'output/bio/stanford/tagged/*.bio'
	tagged_bio_files = glob.glob(tagged_bio_folder)
	tagged_bio = 'output/bio/stanford/stanford_tagged.bio'
	merge_bio(tagged_bio_files, tagged_bio)
	for bio_filename in tagged_bio_files:
		bio_filename = bio_filename.replace('\\', '/')
		print(bio_filename)
		ann_converter = BIOtoANN(bio_filename, source='stanford')
		ann_converter.extract_bio_tags()
		ann_converter.update_ann()
		ann_converter.append_header()
	tagged_ann_folder = "output/bio/stanford/ann/*.ann"
	tagged_ann_files = glob.glob(tagged_ann_folder)
	for ann_filename in tagged_ann_files:
		ann_filename = ann_filename.replace('\\', '/')
		print(ann_filename)
		lif_converter = ANNtoLIF(ann_filename, source='stanford')
		lif_converter.initialize_lif()
		lif_converter.extract_text()
		lif_converter.extract_tags()
	print("Finish Evaluate all files! --- %s seconds ---" % (time.time() - start_eval))
