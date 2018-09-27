import sys
import json
import traceback
import os
import glob
from Client import ServiceClient
import sys
sys.path.append('..')
from PostTokenizer_GATE import PostTokenizer
from PostSentenceSplitter_GATE import PostSentenceSplitter
from FeatureExtractor_GATE import FeatureExtractor
import time
from merge_bio import merge_bio
from CRFRunner import CRFRunner
from BIOtoANN import BIOtoANN
from ANNtoLIF import ANNtoLIF

"""
This file contains the main function for running GATE pipeline. It will start by generating BIO files and finish when 
it generates the resulted LIF file.
"""


def text_to_lif(text):
    lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.lif_1.0.0')
    lif_result = lif_wrapper.execute(text)
    return lif_result

def tokenizer(lif):
    gate_tokenizer = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:gate.tokenizer_2.3.0')
    tokenier_lif = gate_tokenizer.execute(lif)
    return tokenier_lif

def gate_to_lif(gate):
    lif_converter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:convert.gate2json_2.1.0')
    lif_string = lif_converter.execute(gate)
    return lif_string

def lif_to_gate(lif):
    gate_converter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:convert.json2gate_2.1.0')
    gate_string = gate_converter.execute(lif)
    return gate_string

def post_tokenizer(lif, ann):
    post_tokenizer = PostTokenizer(ann_filename=ann, lif_string=lif)
    post_tokenizer.load_ann()
    post_tokenizer.extract_tag()
    post_tokenizer_lif = json.dumps(post_tokenizer.lif_loader.data)
    return post_tokenizer_lif

def sentence_splitter(lif):
    gate_sentence_splitter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:gate.splitter_2.3.0')
    sentence_lif = gate_sentence_splitter.execute(lif)
    return sentence_lif

def post_sentence_splitter(lif):
    post_sentence_splitter = PostSentenceSplitter(lif_string=lif)
    post_sentence_splitter.parse_sentence()
    post_sentence_splitter_lif = json.dumps(post_sentence_splitter.parser.data)
    return post_sentence_splitter_lif

def pos_tagger(lif):
    gate_pos = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:gate.tagger_2.3.0')
    pos_lif = gate_pos.execute(lif)
    return pos_lif

def noun_chunker(gate):
    gate_noun = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:gate.npchunker_2.3.0')
    noun_gate = gate_noun.execute(gate)
    return noun_gate

def verb_chunker(gate):
    gate_verb = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:gate.vpchunker_2.3.0')
    verb_gate = gate_verb.execute(gate)
    return verb_gate

def feature_extractor(lif, file_num, left_info, right_info):
    extractor = FeatureExtractor(lif_string=lif)
    output_filename = "output/bio/gate/train/" + str(file_num) + ".bio"
    position_filename = 'output/bio/gate/tagged/' + file_num + '.pos'
    extractor.extract_tokens()
    extractor.filter_tokens()
    extractor.save_position(position_filename)
    extractor.extract_pos()
    extractor.extract_orth()
    extractor.extract_type()
    extractor.extract_chunk()
    # extractor.extract_code_chunk()
    # extractor.extract_code_section()
    # extractor.extract_code_verb()
    # extractor.extract_snomedct()
    if int(left_info) != 0 or int(right_info) != 0:
        extractor.extract_neighbor(int(left_info), int(right_info), 4)
    extractor.write_bio(output_filename)

def workflow_run(ann_filename):
    file_num = str(ann_filename).split('/')[2].split('.ann')[0]
    print("Processing the file number ", end=' ')
    print(file_num)
    ann_file = open(ann_filename)
    ann_data = json.load(ann_file)
    input_text = ann_data["__text"]
    lif_result = text_to_lif(input_text)

    tokenizer_gate = tokenizer(lif_result)
    tokenizer_lif = gate_to_lif(tokenizer_gate)
    post_tokenizer_lif = post_tokenizer(tokenizer_lif, ann_filename)
    post_tokenizer_gate = lif_to_gate(post_tokenizer_lif)
    sentence_gate = sentence_splitter(post_tokenizer_gate)
    sentence_lif = gate_to_lif(sentence_gate)
    post_sentence_lif = post_sentence_splitter(sentence_lif)
    post_sentence_gate = lif_to_gate(post_sentence_lif)
    pos_gate = pos_tagger(post_sentence_gate)
    noun_gate = noun_chunker(pos_gate)
    verb_gate = verb_chunker(noun_gate)
    result_lif = gate_to_lif(verb_gate)
    feature_extractor(result_lif, file_num, 2, 2)

def run_batch(ann_files):
    ann_list = glob.glob(ann_files)
    for ann_filename in ann_list:
        time.sleep(10)
        try:
            ann_filename = ann_filename.replace('\\','/')
            file_num = str(ann_filename).split('/')[2]
            workflow_run(ann_filename)
        except:
            traceback.print_exc()
            print("Exception occurs for the file ", ann_filename)

def create_output_dir():
    bio_output_1 = 'output/bio/gate/train'
    bio_output_2 = 'output/bio/gate/tagged'
    ann_output_1 = 'output/bio/gate/ann'
    lif_output = 'output/gate_lif'
    ann_output_2 = 'output/gate_ann'
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
    bio_folder = 'output/bio/gate/train/*.bio'
    train_bio = 'output/bio/gate/gate_train.bio'
    train_files = glob.glob(bio_folder)
    merge_bio(train_files,train_bio)
    finish_time = time.time()
    print("Finish Processing all files! --- %s seconds ---" % (finish_time - start_time))
    start_train = time.time()
    model_file = 'output/bio/gate/gate_model'
    template_file = 'output/bio/gate/template'
    crf_runner = CRFRunner(train_bio, bio_folder, model_file=model_file, template_file=template_file, source='gate')
    crf_runner.crf_train()
    crf_runner.crf_test()
    print("Finish Train CRF! --- %s seconds ---" % (time.time() - start_train))
    start_eval = time.time()
    tagged_bio_folder = 'output/bio/gate/tagged/*.bio'
    tagged_bio_files = glob.glob(tagged_bio_folder)
    tagged_bio = 'output/bio/gate/gate_tagged.bio'
    merge_bio(tagged_bio_files,tagged_bio)
    for bio_filename in tagged_bio_files:
        bio_filename = bio_filename.replace('\\', '/')
        print(bio_filename)
        ann_converter = BIOtoANN(bio_filename, source='gate')
        ann_converter.extract_bio_tags()
        ann_converter.update_ann()
        ann_converter.append_header()
    tagged_ann_folder = "output/bio/gate/ann/*.ann"
    tagged_ann_files = glob.glob(tagged_ann_folder)
    for ann_filename in tagged_ann_files:
        ann_filename = ann_filename.replace('\\', '/')
        print(ann_filename)
        lif_converter = ANNtoLIF(ann_filename, source='gate')
        lif_converter.initialize_lif()
        lif_converter.extract_text()
        lif_converter.extract_tags()
    print("Finish Evaluate all files! --- %s seconds ---" % (time.time() - start_eval))
    print("Finish the whole Pipeline! --- %s seconds ---" % (time.time() - start_time))