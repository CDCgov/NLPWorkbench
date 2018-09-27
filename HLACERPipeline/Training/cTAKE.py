import sys
import json
import glob
import os
import traceback
from Client import ServiceClient
import time
import sys
sys.path.append('..')
from PostTokenizer_Stanford import PostTokenizer
from PostSentenceSplitter_Stanford import PostSentenceSplitter
from FeatureExtractor import FeatureExtractor
from merge_bio import merge_bio
from CRFRunner import CRFRunner
from BIOtoANN import BIOtoANN
from ANNtoLIF import ANNtoLIF
import xml.etree.ElementTree as ET
from FeatureGenerator_CTAKE import *

"""
This file contains the main function for running cTAKE pipeline. It will start by generating BIO files and finish when 
it generates the resulted LIF file.
"""

def text_to_lif(text):
	lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.text_1.0.0')
	lif_result = lif_wrapper.execute(text)
	return lif_result

def ctake_service(lif):
	ctake = ServiceClient('http://149.165.156.221:8080/ctakes-clinical-1.0.0/services/CTakesClinicalService?wsdl')
	result = ctake.execute(lif)
	payload = result.split('"payload":"')[1][:-4]
	return payload

def workflow_run(ann_filename):
	file_num = str(ann_filename).split('/')[2].split('.')[0]
	input_file = open(ann_filename)
	ann_data = json.load(input_file)
	input_text = ann_data['__text']

	lif_result = text_to_lif(input_text)
	result = ctake_service(lif_result)
	result = result.replace('\\\"', '"')
	position_filename = 'output/bio/ctake/tagged/' + file_num + '.pos'
	bio_filename = 'output/bio/ctake/train/' + file_num + '.bio'
	# Generate Features
	feature_extractor = cTakeFeatureGenerator(result, ann_data, position_filename, bio_filename)
	feature_extractor.extract_token()
	feature_extractor.extract_info()
	feature_extractor.save_position()


def run_batch(ann_files):
	ann_list = glob.glob(ann_files)
	for ann_filename in ann_list:
		try:
			ann_filename = ann_filename.replace('\\','/')
			file_num = str(ann_filename).split('/')[2].split('.')[0]
			print(ann_filename)
			workflow_run(ann_filename)
		except:
			traceback.print_exc()
			print("Exceptions occurs when processing the file ", ann_filename)

def create_output_dir():
    bio_output_1 = 'output/bio/ctake/train'
    bio_output_2 = 'output/bio/ctake/tagged'
    ann_output_1 = 'output/bio/ctake/ann'
    lif_output = 'output/ctake_lif'
    ann_output_2 = 'output/ctake_ann'
    if not os.path.exists(bio_output_1):
        os.mkdir(bio_output_1)
    if not os.path.exists(bio_output_2):
        os.mkdir(bio_output_2)
    if not os.path.exists(ann_output_1):
        os.mkdir(ann_output_1)
    if not os.path.exists(lif_output):
        os.mkdir(lif_output)
    if not os.path.exists(ann_output_2):
        os.mkdir(ann_output_2)

if __name__ == "__main__":
	start_time = time.time()
	ann_folder = 'input/CDC_ann/*.ann'
	create_output_dir()
	run_batch(ann_folder)
	bio_folder = 'output/bio/ctake/train/*.bio'
	train_bio = 'output/bio/ctake/ctake.bio'
	train_files = glob.glob(bio_folder)
	merge_bio(train_files,train_bio)
	finish_time = time.time()
	print("Finish Processing all files! --- %s seconds ---" % (finish_time - start_time))
	start_train = time.time()
	model_file = 'output/bio/ctake/ctake_model'
	template_file = 'output/bio/ctake/template'
	crf_runner = CRFRunner(train_bio, bio_folder, model_file=model_file, template_file=template_file, source='ctake')
	crf_runner.crf_train()
	crf_runner.crf_test()
	print("Finish Train CRF! --- %s seconds ---" % (time.time() - start_train))
	start_eval = time.time()
	tagged_bio_folder = 'output/bio/ctake/tagged/*.bio'
	tagged_bio_files = glob.glob(tagged_bio_folder)
	merge_bio(tagged_bio_files, 'output/bio/ctake/ctake_tagged.bio')
	for bio_filename in tagged_bio_files:
		bio_filename = bio_filename.replace('\\', '/')
		print(bio_filename)
		ann_converter = BIOtoANN(bio_filename, source='ctake')
		ann_converter.extract_bio_tags()
		ann_converter.update_ann()
		ann_converter.append_header()
	tagged_ann_folder = "output/bio/ctake/ann/*.ann"
	tagged_ann_files = glob.glob(tagged_ann_folder)
	for ann_filename in tagged_ann_files:
		ann_filename = ann_filename.replace('\\', '/')
		print(ann_filename)
		lif_converter = ANNtoLIF(ann_filename, source='ctake')
		lif_converter.initialize_lif()
		lif_converter.extract_text()
		lif_converter.extract_tags()
	print("Finish Evaluate all files! --- %s seconds ---" % (time.time() - start_eval))
	print("Finish the whole Stanford CER Pipeline! --- %s seconds ---" % (time.time() - start_time))
