import json
from FeatureLibrary_Caller import Feature_Library_Service_Caller
import pydot
import os
os.environ["PATH"] += os.pathsep + '/home/marcelocaldas/HLAFeatureLibrary/graphviz-2.38/release/bin'

def initialize_caller(request):
    pipeline = request.forms.get('type')
    token_type = request.forms.get('token_type')
    time_feature = request.forms.get('time_feature')
    pos_tagger = request.forms.get('pos_tagger')
    noun_chunker = request.forms.get('noun_chunker')
    snomed_code = request.forms.get('snomed_code')
    token_length = request.forms.get('token_length')
    orthography = request.forms.get('orthography')
    left_size = request.forms.get('left_size')
    right_size = request.forms.get('right_size')
    features = [pipeline]
    if token_type is not None:
        features.append('yes')
    else:
        features.append('no')
    if time_feature is not None:
        features.append('yes')
    else:
        features.append('no')
    if pos_tagger is not None:
        features.append('yes')
    else:
        features.append('no')
    if noun_chunker is not None:
        features.append('yes')
    else:
        features.append('no')
    if snomed_code is not None:
        features.append('yes')
    else:
        features.append('no')
    if token_length is not None:
        features.append('yes')
    else:
        features.append('no')
    if orthography is not None:
        features.append('yes')
    else:
        features.append('no')
    features.append(left_size)
    features.append(right_size)
    word = request.forms.get('prev_word')
    token_type = request.forms.get('prev_token_type')
    time_feature = request.forms.get('prev_time_feature')
    pos_tagger = request.forms.get('prev_pos_tagger')
    noun_chunker = request.forms.get('prev_noun_chunker')
    snomed_code = request.forms.get('prev_snomed_code')
    token_length = request.forms.get('prev_token_length')
    orthography = request.forms.get('prev_orthography')
    prev_features = []
    if word is not None:
        prev_features.append('word')
    if token_type is not None:
        prev_features.append('type')
    if time_feature is not None:
        prev_features.append('time')
    if pos_tagger is not None:
        prev_features.append('pos')
    if noun_chunker is not None:
        prev_features.append('chunk')
    if snomed_code is not None:
        prev_features.append('code')
    if token_length is not None:
        prev_features.append('length')
    if orthography is not None:
        prev_features.append('orth')
    prev_feature_string = ','.join(prev_features)
    features.append(prev_feature_string)
    word = request.forms.get('next_word')
    token_type = request.forms.get('next_token_type')
    time_feature = request.forms.get('next_time_feature')
    pos_tagger = request.forms.get('next_pos_tagger')
    noun_chunker = request.forms.get('next_noun_chunker')
    snomed_code = request.forms.get('next_snomed_code')
    token_length = request.forms.get('next_token_length')
    orthography = request.forms.get('next_orthography')
    next_features = []
    if word is not None:
        next_features.append('word')
    if token_type is not None:
        next_features.append('type')
    if time_feature is not None:
        next_features.append('time')
    if pos_tagger is not None:
        next_features.append('pos')
    if noun_chunker is not None:
        next_features.append('chunk')
    if snomed_code is not None:
        next_features.append('code')
    if token_length is not None:
        next_features.append('length')
    if orthography is not None:
        next_features.append('orth')
    next_feature_string = ','.join(next_features)
    features.append(next_feature_string)
    return features

def get_process(features):
    input_filename = 'input/input.txt'
    output_filename = 'output/output.bio'
    process = load_feature_selection(features)
    return process

def load_feature_selection(args):
    pipeline_type = args[0]
    pipeline = []
    pipeline.append('tokenizer')
    pipeline.append('post_tokenizer')
    token_type = False
    pos_tag = False
    noun_chunk = False
    meta_map = False
    token_length = False
    orth_feature = False
    select_number = 0
    if args[1] == "yes":
        if pipeline_type != "gate":
            pipeline.append('token_type')
        token_type = True
        select_number += 1
    if args[2] == "yes":
        pipeline.append('time_feature')
    if args[3] == "yes":
        pipeline.append('sentence_splitter')
        pipeline.append('post_sentence_splitter')
        pipeline.append('pos_tagger')
        pipeline.append('post_pos_tagger')
        pos_tag = True
        select_number += 1
    if args[4] == "yes":
        pipeline.append('noun_chunker')
        noun_chunk = True
        select_number += 1
    if args[5] == "yes":
        pipeline.append('meta_map')
        meta_map = True
        select_number += 1
    if args[6] == "yes":
        token_length = True
    if args[7] == "yes":
        orth_feature = True
        if pipeline_type != "gate":
            pipeline.append('orthography')
        select_number += 1
    left_size = int(args[8])
    right_size = int(args[9])
    pipeline.append('feature_extractor')
    return pipeline

def plot_process_backup(process):
    graph = pydot.Dot(graph_type='digraph', rankdir="LR")
    for i in range(len(process)-1):
        edge = pydot.Edge(process[i], process[i+1])
        graph.add_edge(edge)
    graph.write_png('static/images/process.png')

def plot_process(process):
    graph = pydot.Dot(graph_type='digraph', rankdir="LR")
    nodes = {}
    input_node = pydot.Node('Input File', shape='box')
    graph.add_node(input_node)
    nodes['input'] = input_node
    for i in range(len(process)):
        node = pydot.Node(process[i])
        graph.add_node(node)
        nodes[process[i]] = node
    output_node = pydot.Node('Output BIO File', shape='box')
    graph.add_node(output_node)
    nodes['output'] = output_node

    # Define Edges
    edge = pydot.Edge(input_node, nodes[process[0]])
    graph.add_edge(edge)
    for i in range(len(process)-1):
        edge = pydot.Edge(nodes[process[i]], nodes[process[i+1]])
        graph.add_edge(edge)
    edge = pydot.Edge(nodes[process[len(process)-1]], output_node)
    graph.add_edge(edge)
    graph.write_png('static/images/process.png')

        