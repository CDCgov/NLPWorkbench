import os
import string
import random
import cgi
import json
import uuid
import sys
import traceback
import bottle
from datetime import datetime
from bottle import get, post, request, run, route, Bottle
import bottle
from Pipeline.Training.cTAKE import *
from Pipeline.Training.CRFRunner import CRFRunner
from Pipeline.Training.BIOtoANN import BIOtoANN
from Pipeline.Training.ANNtoLIF import ANNtoLIF
from Pipeline.parse_hl7_file import parse_hl7_file
from collections import defaultdict
from threading import Thread

app = Bottle()

def generate_bio(transaction_id, document_text, file_num):
	#ann_data = json.loads(document_text)
	#input_text = ann_data["__text"]
	#temp_input_file = 'temp/input/' + file_num + '.ann'
	temp_bio_file = 'temp/bio/ctake/train/' + file_num + '.bio'
	temp_pos_file = 'temp/bio/ctake/tagged/' + file_num + '.pos'
	input_text = document_text
	

	# Go through LAPPS workflow

	lif_result = text_to_lif(input_text)
	ctake_lif_result = ctake_service(lif_result)

	feature_extractor(ctake_lif_result, temp_bio_file, temp_pos_file)
		

def classify(transaction_id, document_text, file_num):
	service_log_file = 'data/ctake_service_call.log'
	server_log = open(service_log_file, 'a')
	try:
		temp_input_file = 'temp/input/' + file_num + '.txt'
		file = open(temp_input_file, "w+")
		#json.dump(json.loads(str(document_text)), file)
		#file.write(document_text)
		#file.close()
		parsed_text = parse_hl7_file(document_text)
		if parsed_text == "":
			parsed_text = document_text
		file.write(parsed_text)
		file.close()
		generate_bio(transaction_id, parsed_text, file_num)

		# Setup temporary file names
		temp_bio_file = 'temp/bio/ctake/train/' + file_num + '.bio'
		temp_pos_file = 'temp/bio/ctake/tagged/' + file_num + '.pos'
		temp_output_file = 'temp/bio/ctake/tagged/' + file_num + '.bio'
		output_file_bio = 'temp/bio/ctake/' + file_num + '.bio'
		output_file = 'temp/output/ctake/' + file_num + '.txt'
		MODEL_NAME = 'temp/input/ctake_model'

		# Run through the pipeline
		crf_runner = CRFRunner(model_file=MODEL_NAME, source="ctake")
		crf_runner.crf_test(test_bio=temp_bio_file, output_filename=temp_output_file)
		append_start(temp_output_file, temp_pos_file, output_file_bio)
		tags = extract_tags(output_file_bio)
		save_output(tags, output_file)
		log = transaction_id + " || " + "cTAKE Pipeline || " + time.asctime( time.localtime(time.time())) + " || SUCCESSFULL" + "\n"
		server_log.write(log)
	except:
		print(str(traceback.format_exc()))
		message = str(traceback.format_exc()).split('\n')[-2]
		if 'NoneType' in message:
			message = 'Service Calling Error. Document text is received as None object.'
		elif 'CalledProcessError' in message and 'crf_test' in message:
			message = 'Prediction Error. CRF test raised an error and is not able to predict this file.'
		log = transaction_id + " || " + "cTAKE Pipeline || " + time.asctime( time.localtime(time.time())) + " || " + message + "\n"
		server_log.write(log)
	#data = output_json(output_file, document_text)
	#return data

def append_start(bio_file, pos_file, output_filename):
	file = open(bio_file)
	output_file = open(output_filename, "w+")
	line_number = 1
	for line in file.readlines():
		line = line[:-1]
		length = len(line.split('\t'))
		try:
			if length > 1:
				token = line.split('\t')[0]
				annot = line.split('\t')[length-1]
				start = line.split('\t')[length-2]
				new_line = '\t'.join(line.split('\t')[:-2]) + '\t' + annot + '\t' + str(start) + '\n'
				output_file.write(new_line)
				line_number += 1
			else:
				new_line = line[:-1]
				output_file.write(new_line)
		except:
			print("Exception occurs, skip this line.")
	output_file.close()

def extract_tags(output_file):
		bio_file = open(output_file)
		tags = defaultdict(list)
		current_tag = ""
		current_content = ""
		current_start = 0
		current_end = 0
		for line in bio_file.readlines():
			line = line[:-1]
			if line != "":
				token = line.split('\t')[0]
				tag = line.split('\t')[len(line.split('\t'))-2]
				start = line.split('\t')[len(line.split('\t'))-1]
				if tag[0] == "B":
					if current_tag != "" and re.search('[a-zA-Z0-9]', current_content):
						current = {}
						current['name'] = current_content
						current['start'] = current_start
						current['end'] = current_end
						tags[current_tag].append(current)
					current_tag = tag[2:]
					current_content = token
					current_start = start
					current_end = int(start) + len(token)
				elif tag[0] == "I":
					current_content = current_content + " " + token
					current_end = int(start) + len(token)
				elif tag == "O":
					if current_tag != "" and re.search('[a-zA-Z0-9]', current_content):
						current = {}
						current['name'] = current_content
						current['start'] = current_start
						current['end'] = current_end
						tags[current_tag].append(current)
					current_tag = ""
					current_content = ""
					current_end = 0
					current_start = 0
		return tags

def output_json(output_filename, document_text):
	output_file = open(output_filename)
	data = {}
	data['payload'] = {}
	data['payload']['views'] = []
	view = {}
	view['metadata'] = {}
	view['metadata']['contains'] = 'cTAKE Pipeline Service'
	view['annotation'] = []
	id = 0
	for line in output_file.readlines():
		line = line[:-1]
		annotation = line.split('\t')
		ann = {}
		ann['id'] = id
		ann['start'] = annotation[2]
		ann['end'] = annotation[3]
		ann['label'] = 'Tag'
		ann['@type'] = annotation[0]
		ann['features'] = {}
		ann['features']['annotator'] = 'cTAKE'
		ann['features']['content'] = annotation[1]
		view['annotation'].append(ann)
		id += 1
	data['payload']['views'].append(view)
	data['payload']['text'] = document_text
	data['payload']['@context'] = 'http://vocab.lappsgrid.org/context-1.0.0.jsonld'
	data['discriminator'] = 'http://vocab.lappsgrid.org/ns/media/jsonld#lif'
	return data

def save_output(tags, output_filename):
	output_file = open(output_filename, "w+")
	for type, value in tags.items():
			#tag = get_original_tagname(type)
			tag = type.replace('_', ' ')
			for item in value:
				line = '\t'.join([tag, item['name'], str(item['start']), str(item['end'])])
				line = line + "\n"
				output_file.write(line)
	output_file.close()

def get_original_tagname(tag):
	original = open('tag_abbreviation.txt')
	for line in original.readlines():
		name = line.split('->')[0]
		abbre = ''.join(name.split('(')[0].split(' ')).upper()
		if abbre == tag:
			return name
	return ""
	
def find_start_position(line_number, token, pos_filename):
	position_file = open(pos_filename)
	current_line = 1
	for line in position_file.readlines():
		line = line[:-1]
		current_token = line.split(' ')[0]
		start = line.split(' ')[1]
		if current_line == line_number and current_token == token:
			return start
		current_line += 1
	return -1

def __get_platform():
	import os
	if os.name == 'nt':
		return 'WINDOWS'
	else:
		return 'LINUX'

@app.route('/request_cdc_ctake_service', method='POST')
def generate_ctake_result():
	request_id = str(uuid.uuid1())
	request_time = time.asctime( time.localtime(time.time()))
	document_text,file_num = "",""
	data = {}
	ctype = request.environ.get('CONTENT_TYPE','').lower().split(';')[0]
	if ctype in ['application/json', 'application/json-rpc']:
		input_data = request.json
		document_text = input_data['document_text']
	else:
		try:
			document_text = request.forms.get('document_text')
		except:
			print('Invalid document text')
	file_num = request_id
	platform = __get_platform()
	#try:
		#document_text = request.forms.get('document_text')
	#except:
		#print('Invalid document text')
	#try:
		#file_num = request.forms.get('file_id')
		#file_num = random.choice(string.digits)
		#file_num = request_id
	#except:
		#print('Invalid file number')
	#try:
		#platform = request.forms.get('platform')
		#platform = "WINDOWS"
	#except:
		#print('Invalid platform name')
	status = ""
	try:
		t = Thread(target=classify, args=(request_id, document_text, file_num))
		t.start()
		#data = classify(document_text, file_num)
		status = "SUCCESS"
	except:
		traceback.print_exc()
		status = "FAILED"
	with open('data/ctake_server.log', 'a') as log_file:
		log = request_id + " || " + str(file_num) + " || " + platform + " || " + request_time + " || " + status + "\n"
		log_file.write(log)
	output = {}
	output['transaction_id'] = request_id
	#output['result'] = data
	return output

@app.route('/fetch_cdc_ctake_service_result/:transaction_id', method='GET')
def fetch_ctake_result(transaction_id):
	log_file = open('data/ctake_server.log')
	for line in log_file.readlines():
		line = line[:-1]
		transaction = line.split(" || ")
		if transaction[0] == transaction_id:
			file_num = transaction[1]
			output_file = 'temp/output/ctake/' + file_num + '.txt'
			input_file = 'temp/input/' + file_num + '.txt'
			try:
				document_text = open(input_file).read()
				file = open(output_file)
				data = output_json(output_file, document_text)
				file.close()
				return data
			except:
				message = find_error_message(transaction_id)
				return message
	return "Did not find output file. Failed sending the text to the request."

def find_error_message(transaction_id):
	error_file = open('data/ctake_service_call.log')
	message = 'Did not find output file. An error occurred during the process.\n'
	for line in error_file.readlines():
		line = line[:-1]
		transaction = line.split(" || ")
		if transaction[0] == transaction_id:
			message += transaction[3]
	return message
	
application = app
