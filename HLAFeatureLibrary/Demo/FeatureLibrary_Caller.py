# from urllib.parse import urlencode
# from urllib.request import Request, urlopen
import urllib
import urllib2
import sys
import traceback

class Feature_Library_Service_Caller:

    def __init__(self, input_filename, output_filename, features, input_type='txt', process=""):
        self.input_filename = input_filename
        self.output_filename = output_filename
        self.feature_list = features
        self.input_type = 'txt'
        self.process = process

    def process_request(self):
        # Change the line below to where the server is stored
        url = 'http://hostname.com/clew_feature_library/request_feature_library'
        data = urllib.urlencode({
            'document_text': self.document_text,
            'input_type': self.input_type,
            'output_filename': self.output_filename,
            'pipeline': self.feature_list[0],
            'token_type': self.feature_list[1],
            'time_feature': self.feature_list[2],
            'pos_tagger': self.feature_list[3],
            'noun_chunk': self.feature_list[4],
            'snomed_code': self.feature_list[5],
            'token_length': self.feature_list[6],
            'orthography': self.feature_list[7],
            'left_size': self.feature_list[8],
            'right_size': self.feature_list[9],
            'prev_features': self.feature_list[10],
            'next_features': self.feature_list[11],
            'process': self.process
        }).encode("utf-8")
        request = urllib2.Request(url, data)
        response = urllib2.urlopen(request)
        transaction_id = response.read().decode("utf-8")
        return transaction_id

    def fetch_transaction_result(self, transaction_id):
        # Change the line below to where the server is stored
        url = 'http://hostname.com/clew_feature_library/fetch_feature_library_result/' + transaction_id
        response = urllib.urlopen(url)
        transaction_result = response.read()
        return transaction_result

    def parse_file(self, document_text=""):
        if document_text == "":
            self.document_text = open(self.input_filename).read()
        else:
            self.document_text = document_text
        if self.input_filename.endswith('.txt'):
            self.input_type = 'txt'
        elif self.input_filename.endswith('.lif'):
            self.input_type = 'lif'
        elif self.input_filename.endswith('.ann'):
            self.input_type = 'ann'
        transaction_id = self.process_request()
        transaction_result = self.fetch_transaction_result(transaction_id)
        return transaction_result

    def save_output(self, transaction_result):
        output_file = open(self.output_filename, "w+")
        output_file.write(transaction_result)
        output_file.close()

if __name__ == "__main__":
    input_filename = '41.txt'
    output_filename = 'output/output.bio'
    prev_features = "word,type,pos,length,orth,chunk"
    next_features = "word,type,pos,length,orth,chunk"
    features = ['opennlp', 'yes', 'no', 'yes', 'yes', 'no', 'yes', 'yes', '2', '2', prev_features, next_features]
    caller = Feature_Library_Service_Caller(input_filename, output_filename, features)
    result = caller.parse_file()
    caller.save_output(result.decode("utf-8"))
