from urllib.parse import urlencode
from urllib.request import Request, urlopen

class Feature_Library_Service_Caller:

    def __init__(self, input_filename, output_filename, features):
        self.input_filename = input_filename
        self.output_filename = output_filename
        self.feature_list = features

    def process_request(self):
        url = 'http://hostname.com/clew_feature_library/request_feature_library'
        data = urlencode({
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
            'left_size': self.feature_list[7],
            'right_size': self.feature_list[8]
        }).encode("utf-8")
        request = Request(url, data)
        response = urlopen(request)
        transaction_id = response.read().decode("utf-8")
        return transaction_id

    def fetch_transaction_result(self, transaction_id):
        url = 'http://hostname.com/clew_feature_library/fetch_feature_library_result/' + transaction_id
        response = urlopen(url)
        transaction_result = response.read()
        return transaction_result

    def parse_file(self):
        self.document_text = open(self.input_filename).read()
        if self.input_filename.endswith('.txt'):
            self.input_type = 'txt'
        elif self.input_filename.endswith('.lif'):
            self.input_type = 'lif'
        transaction_id = self.process_request()
        transaction_result = self.fetch_transaction_result(transaction_id)
        

if __name__ == "__main__":
    input_filename = 'input/41.txt'
    output_filename = 'output/output.bio'
    # features = ['stanford' 'token_type' 'pos_tagger' 'token_length' '2' '2']
    features = ['gate', 'yes', 'no', 'yes', 'no', 'no', 'yes', '2', '2']
    caller = Feature_Library_Service_Caller(input_filename, output_filename, features)
    caller.parse_file()