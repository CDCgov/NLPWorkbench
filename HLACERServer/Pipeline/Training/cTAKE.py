import sys
import json
import glob
from Client import ServiceClient
import time
import sys
sys.path.append('..')
from merge_bio import merge_bio
from CRFRunner import CRFRunner
from BIOtoANN import BIOtoANN
from ANNtoLIF import ANNtoLIF
import xml.etree.ElementTree as ET
from FeatureGenerator_CTAKE import *
import traceback

"""
This file contains the main function for running cTAKE pipeline. It will start by generating BIO files and finish when 
it generates the resulted LIF file.
"""

def text_to_lif(text):
	lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.text_1.0.0')
	lif_result = lif_wrapper.execute(text)
	return lif_result

def ctake_service(lif):
	ctake = ServiceClient('http://services.lappsgrid.org/clinical?wsdl')
	result = ctake.execute(lif)
	payload = result.split('"payload":"')[1][:-4]
	return payload

def feature_extractor(result, bio_filename, position_filename):
	result = result.replace('\\\"', '"')
	feature_extractor = cTakeFeatureGenerator(result, position_filename, bio_filename)
	feature_extractor.extract_token()
	feature_extractor.extract_info()
	feature_extractor.save_position()

def workflow_run(ann_filename):
	file_num = str(ann_filename).split('/')[2].split('.')[0]
	input_file = open(ann_filename)
	ann_data = json.load(input_file)
	input_text = ann_data['__text']

	lif_result = text_to_lif(input_text)
	result = ctake_service(lif_result)
	result = result.replace('\\\"', '"')
	ctake_output = 'ctake_output/' + file_num + '.xml'
	position_filename = 'output/bio/ctake/tagged/' + file_num + '.pos'
	bio_filename = 'output/bio/ctake/train/' + file_num + '.bio'
	output_file = open(ctake_output, "w+")
	output_file.write(result)
	output_file.close()
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

if __name__ == "__main__":
	start_time = time.time()
	ann_folder = 'input/CDC_ann/Batch_5_*.ann'
	run_batch(ann_folder)
	bio_folder = 'output/bio/ctake/train/Batch_5_*.bio'
	train_bio = 'output/bio/ctake/ctake_5.bio'
	train_files = glob.glob(bio_folder)
	merge_bio(train_files,train_bio)
	finish_time = time.time()
	print("Finish Processing all files! --- %s seconds ---" % (finish_time - start_time))
	start_train = time.time()
	model_file = 'output/bio/ctake/ctake_model_5'
	template_file = 'output/bio/ctake/template'
	crf_runner = CRFRunner(train_bio, bio_folder, model_file=model_file, template_file=template_file, source='ctake')
	crf_runner.crf_train()
	crf_runner.crf_test()
	print("Finish Train CRF! --- %s seconds ---" % (time.time() - start_train))
	start_eval = time.time()
	tagged_bio_folder = 'output/bio/ctake/tagged/Batch_5_*.bio'
	tagged_bio_files = glob.glob(tagged_bio_folder)
	merge_bio(tagged_bio_files, 'output/bio/ctake/ctake_tagged_5.bio')
	for bio_filename in tagged_bio_files:
		bio_filename = bio_filename.replace('\\', '/')
		print(bio_filename)
		ann_converter = BIOtoANN(bio_filename, source='ctake')
		ann_converter.extract_bio_tags()
		ann_converter.update_ann()
		ann_converter.append_header()
	tagged_ann_folder = "output/bio/ctake/ann/Batch_5_*.ann"
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
