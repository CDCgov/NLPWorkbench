from bottle import Bottle, run, template, get, post, request
import bottle
from Feature_Library_Demo_Helper import *
import json
app = Bottle()
import time
import shutil
import zipfile
import glob
from FeatureLibrary_Caller import Feature_Library_Service_Caller
import os

STATIC_ROOT_PATH = "static/"

@app.route('/feature_library/main')
def load_main():
    return template('template/main.tpl', output='download/download.zip')

@app.route('/feature_library/main_plot', method="POST")
def load_main_plot():
    features = initialize_caller(request)
    process = get_process(features)
    plot_process(process)
    return template('template/main_plot.tpl', plot='process.png', features=features, output='output/output.bio')

@app.route('/feature_library/static/<filename:path>')
def static_file_load(filename):
    return bottle.static_file(filename, root='static/images')

@app.route('/feature_library/output/<filename:path>', method="POST")
def return_result(filename):
    uploads = request.files.getall('myFile')
    upload_anns = request.files.getall('myANNFile')
    input_filename = ""
    document_text = ""
    for file in glob.glob('download/*'):
        os.remove(file)
    if upload_anns is not None:
        for upload_ann in upload_anns:
            input_filename = upload_ann.filename
            document_text = upload_ann.file.read().decode("utf-8")
            output_filename = 'download/' + input_filename[:-4] + '.bio'
            call_feature_library(request, input_filename, output_filename, document_text)
    elif uploads is not None:
        for upload in uploads:
            input_filename = upload.filename
            document_text = upload.file.read().decode("utf-8")
            output_filename = 'download/' + input_filename[:-4] + '.bio'
            call_feature_library(request, input_filename, output_filename, document_text)
    with zipfile.ZipFile("download/download.zip", "w") as zip:
        for bio_filename in glob.glob('download/*.bio'):
            zip.write(bio_filename)
    return bottle.static_file('download.zip', root='download', download='download.zip')

def call_feature_library(request, input_filename, filename, document_text):
    document_text = document_text.replace('\r\n', '\n')
    features = initialize_caller(request)
    process = get_process(features)
    plot_process(process)
    caller = Feature_Library_Service_Caller(input_filename, filename, features)
    result = caller.parse_file(str(document_text))
    output = open(filename, "w+")
    output.write(result)
    output.close()

@app.route('/feature_library/result', method="POST")
def display_result():
    uploads = request.files.getall('myFile')
    upload_anns = request.files.getall('myANNFile')
    input_filename = ""
    document_text = ""
    result = ""
    if upload_anns is not None:
        for upload_ann in upload_anns:
            input_filename = upload_ann.filename
            document_text = upload_ann.file.read().decode("utf-8").replace('\r\n', '\n')
            output_filename = 'output/output.bio'
            features = initialize_caller(request)
            caller = Feature_Library_Service_Caller(input_filename, output_filename, features)
            result += caller.parse_file(str(document_text)) + "\n" + "\n"
    elif uploads is not None:
        for upload in uploads:
            input_filename = upload.filename
            document_text = upload.file.read().decode("utf-8").replace('\r\n', '\n')
            output_filename = 'output/output.bio'
            features = initialize_caller(request)
            caller = Feature_Library_Service_Caller(input_filename, output_filename, features)
            result += caller.parse_file(str(document_text))
    features = initialize_caller(request)
    process = get_process(features)
    plot_process(process)
    return template('template/result.tpl', output='output/output.bio', plot='process.png', bio=result, features = features, last='result')

@app.route('/feature_library/download/<filename:path>', method="POST")
def download_file(filename):
    result = request.forms.get('bio')
    output = open(filename, "w+")
    output.write(result)
    output.close()
    return bottle.static_file('output.bio', root='output', download='output.bio')

@app.route('/feature_library/change_plot')
def change_plot():
    input_filename = '41.txt'
    output_filename = 'output/output.bio'
    features = initialize_caller(request)
    process = get_process(features)
    plot_process(process)
    caller = Feature_Library_Service_Caller(input_filename, output_filename, features)
    result = caller.parse_file()
    return template('template/change.tpl', output='output/output.bio', plot='process.png', bio=result,
                    features=features, pipeline=process)

application = app
#app.run(host='127.0.0.1', port=9001)