import sys
import os
import json
import glob
from Client import ServiceClient
import time
import sys
import traceback

sys.path.append('..')
from PostTokenizer_Stanford import PostTokenizer
from PostSentenceSplitter_Stanford import PostSentenceSplitter
from FeatureExtractor import FeatureExtractor
from merge_bio import merge_bio
from CRFRunner import CRFRunner
from BIOtoANN import BIOtoANN
from ANNtoLIF import ANNtoLIF
from StanfordPOSTagger import LAPPS_StanfordPOSTagger
from stanford_wrapper import *

"""
This file contains the main function for running Stanford pipeline. It will start by generating BIO files and finish when 
it generates the resulted LIF file.
"""


def text_to_lif(text):
    lif_wrapper = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:wrap.text_1.0.0')
    lif_result = lif_wrapper.execute(text)
    return lif_result


def tokenizer(lif):
    stanford_tokenizer = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:stanford.tokenizer_2.0.0')
    tokenier_lif = stanford_tokenizer.execute(lif)
    return tokenier_lif


def stanford_tokenizer(lif):
    st = StanfordTokenizer(lif)
    st.call_tokenizer()
    stanford_tokenizer_lif = json.dumps(st.lif_parser.data)
    return stanford_tokenizer_lif


def post_tokenizer(lif, ann):
    post_tokenizer = PostTokenizer(ann_filename=ann, lif_string=lif)
    post_tokenizer.load_ann()
    post_tokenizer.extract_tag()
    post_tokenizer_lif = json.dumps(post_tokenizer.lif_loader.data)
    return post_tokenizer_lif


def sentence_splitter(lif):
    stanford_sentence_splitter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:stanford.splitter_2.0.0')
    sentence_lif = stanford_sentence_splitter.execute(lif)
    return sentence_lif


def stanford_sentence_splitter(lif):
    sentence = StanfordSentenceSplitter(lif)
    sentence.call_splitter()
    stantord_sentence_splitter_lif = json.dumps(sentence.lif_parser.data)
    return stantord_sentence_splitter_lif


def post_sentence_splitter(lif):
    post_sentence_splitter = PostSentenceSplitter(lif_string=lif)
    post_sentence_splitter.parse_sentence()
    post_sentence_splitter_lif = json.dumps(post_sentence_splitter.parser.data)
    return post_sentence_splitter_lif


def pos_tagger(lif):
    stanford_pos = ServiceClient(
        'http://eldrad.cs-i.brandeis.edu:8080/service_manager/wsdl/brandeis_eldrad_grid_1:stanfordnlp.postagger_2.0.3')
    pos_lif = stanford_pos.execute(lif)
    return pos_lif


def stanford_pos_tagger(lif):
    tagger = LAPPS_StanfordPOSTagger(lif_string=lif)
    tagger.pos_tagger()
    pos_lif = json.dumps(tagger.lif_parser.data)
    return pos_lif


def feature_extractor(lif, file_num, left_info, right_info):
    extractor = FeatureExtractor(lif_string=lif)
    output_filename = "output/bio/stanford/train/" + str(file_num) + ".bio"
    position_filename = 'output/bio/stanford/tagged/' + file_num + '.pos'
    extractor.extract_tokens()
    extractor.filter_tokens()
    extractor.save_position(position_filename)
    extractor.extract_pos()
    # extractor.extract_snomedct()
    if int(left_info) != 0 or int(right_info) != 0:
        extractor.extract_neighbor(int(left_info), int(right_info), 1)
    extractor.write_bio(output_filename)


def workflow_run(ann_filename):
    file_num = str(ann_filename).split('/')[2].split('.')[0]
    input_file = open(ann_filename)
    ann_data = json.load(input_file)
    input_text = ann_data['__text']

    lif_result = text_to_lif(input_text)
    tokenizer_lif = ""
    try:
        tokenizer_lif = tokenizer(lif_result)
    except:
        tokenizer_lif = stanford_tokenizer(lif_result)
    post_tokenizer_lif = post_tokenizer(tokenizer_lif, ann_filename)
    sentence_lif = ""
    try:
        sentence_lif = sentence_splitter(post_tokenizer_lif)
    except:
        sentence_lif = sentence_splitter(post_tokenizer_lif)
    post_sentence_lif = post_sentence_splitter(sentence_lif)
    pos_lif = ""
    try:
        pos_lif = pos_tagger(post_sentence_lif)
    except:
        pos_lif = stanford_pos_tagger(post_sentence_lif)
    feature_extractor(pos_lif, file_num, 2, 2)


def run_batch(ann_files):
    ann_list = glob.glob(ann_files)
    for ann_filename in ann_list:
        time.sleep(10)
        try:
            ann_filename = ann_filename.replace('\\', '/')
            file_num = str(ann_filename).split('/')[2].split('.')[0]
            print(ann_filename)
            workflow_run(ann_filename)
        except:
            traceback.print_exc()
            print("Exceptions occurs when processing the file ", ann_filename)


def create_output_dir():
    bio_output_1 = 'output/bio/stanford/train'
    bio_output_2 = 'output/bio/stanford/tagged'
    ann_output_1 = 'output/bio/stanford/ann'
    lif_output = 'output/stanford_lif'
    ann_output_2 = 'output/stanford_ann'
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
    bio_folder = 'output/bio/stanford/train/*.bio'
    train_bio = 'output/bio/stanford/stanford_2.bio'
    train_files = glob.glob(bio_folder)
    merge_bio(train_files, train_bio)
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
    merge_bio(tagged_bio_files, 'output/bio/stanford/stanford_tagged.bio')
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
    print("Finish the whole Stanford CER Pipeline! --- %s seconds ---" % (time.time() - start_time))
