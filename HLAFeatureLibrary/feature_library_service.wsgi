from bottle import Bottle, run, template, get, post, request, static_file
import bottle
import json
import glob
import uuid
app = Bottle()
import time
from FeatureLibrary import invoke_feature_library
import traceback

@app.route('/request_feature_library', method="POST")
def call_feature_library():
    request_id = str(uuid.uuid1())
    request_time = time.asctime(time.localtime(time.time()))
    arguments = []
    process = []
    input_txt = 'input/input.txt'
    input_lif = 'input/input.lif'
    input_ann = 'input/input.ann'
    document_text = ""
    file_num = 'output'
    try:
        document_text = request.forms.get('document_text')
        # arguments.append(document_text)
    except:
        print('Invalid document text')
    try:
        input_type = request.forms.get('input_type')
        if input_type == 'txt':
            input_file = open(input_txt, "w+")
            input_file.write(document_text)
            input_file.close()
            arguments.append(input_txt)
        elif input_type == 'lif':
            input_file = open(input_lif, "w+")
            lif_data = json.loads(document_text)
            json.dump(lif_data, input_file, indent=4)
            input_file.close()
            arguments.append(input_lif)
        elif input_type == 'ann':
            input_file = open(input_ann, "w+")
            document_text = '\n'.join(document_text.split('\n')[1:])
            ann_data = json.loads(document_text)
            json.dump(ann_data, input_file, indent=4)
            input_file.close()
            arguments.append(input_ann)
    except:
        traceback.print_exc()
        print('Invalid input type')
    try:
        output_filename = request.forms.get('output_filename')
        output_filename = 'output/output.bio'
        arguments.append(output_filename)
    except:
        print('Invalid output filename')
    try:
        pipeline = request.forms.get('pipeline')
        arguments.append(pipeline)
    except:
        print('Invalid pipeline name')
    try:
        feature = request.forms.get('token_type')
        arguments.append(feature)
        feature = request.forms.get('time_feature')
        arguments.append(feature)
        feature = request.forms.get('pos_tagger')
        arguments.append(feature)
        feature = request.forms.get('noun_chunk')
        arguments.append(feature)
        feature = request.forms.get('snomed_code')
        arguments.append(feature)
        feature = request.forms.get('token_length')
        arguments.append(feature)
        feature = request.forms.get('orthography')
        arguments.append(feature)
        feature = request.forms.get('left_size')
        arguments.append(feature)
        feature = request.forms.get('right_size')
        arguments.append(feature)
        feature = request.forms.get('prev_features')
        arguments.append(feature)
        feature = request.forms.get('next_features')
        arguments.append(feature)
        process = request.forms.get('process')
    except:
        print('Invalid feature name')
    try:
        invoke_feature_library(arguments, process)
        process = "SUCCESSFULL"
    except:
        traceback.print_exc()
        process = "FAILED"
    with open('data/feature_library_his.log', "a") as log_file:
        log = "Feature Library || " + file_num + " || " + request_id + " || " \
              + request_time + " || " + process + "\n"
        log_file.write(log)
    return request_id

@app.route('/fetch_feature_library_result/:transaction_id', method="GET")
def get_feature_library_result(transaction_id):
    log_file = open('data/feature_library_his.log')
    for line in log_file.readlines():
        line = line[:-1]
        transaction = line.split(" || ")
        if transaction[2] == transaction_id:
            file_num = transaction[1]
            output_file = 'output/' + file_num + '.bio'
            file = open(output_file)
            result = file.read()
            file.close()
            return result
    return 'Could not find output file'

@app.route('/feature_library_service/static/<filename:path>')
def static_file_load(filename):
    print(filename)
    return bottle.static_file(filename, root='static')

application = app
#app.run(host='127.0.0.1', port='9001', reloader=True)