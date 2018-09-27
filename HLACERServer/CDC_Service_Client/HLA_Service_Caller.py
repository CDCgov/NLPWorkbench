import os
import sys
import json
import urllib
import urllib2
import time
import glob
from datetime import datetime
from collections import OrderedDict

'''
This script provides example of how to call HLA CER Servers.
It can be run using:
python HLA_Service_Caller.py INPUT OUTPUT
The INPUT folder should contain some text files to be processed.
The results for each file are saved in the OUTPUT folder.
'''

class HLA_Service_Caller:

    def __init__(self, pipeline, input_folder, output_folder):
        self.INPUT_FOLDER = input_folder
        self.OUTPUT_FOLDER = output_folder
        self.pipeline = pipeline

        if pipeline != "stanford" and pipeline != "opennlp" and pipeline != "gate":
            print("Invalid Pipeline name")
            exit(1)

        for file_name in os.listdir(input_folder):
            if file_name.endswith(".txt"):
                file_path = os.path.join(input_folder, file_name)
                document_text = (file(file_path, 'rU').read()).decode("utf-8",'replace')
                file_id = file_name.split('.')[0]
                transaction_id = self.process_request(document_text, file_id, pipeline)
                print(transaction_id)

                transaction_result = self.fetch_transaction_result(transaction_id, pipeline)
                output_filename = str(file_id) + '.txt'
                output_path = os.path.join(output_folder, output_filename)
                with open(output_path, "w+") as output_file:
                    output_file.write(transaction_result)

    def process_request(self, document_text, file_id, pipeline):
        url = ""
        if pipeline.lower() == "stanford":
            url = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_stanford/request_cdc_stanford_service'
        elif pipeline.lower() == "opennlp":
            url = 'http://lvsu1604009.lab.local/hla_service/opennlp/request_cdc_opennlp_service'
        elif pipeline.lower() == "gate":
            url = 'http://lvsu1604009.lab.local/hla_service/gate/request_cdc_gate_service'

        data = urllib.urlencode({"document_text": document_text})
        request = urllib2.Request(url, data)
        response = urllib2.urlopen(request)
        transaction_id = response.read()
        return transaction_id

    def __get_platform(self):
        import os
        if os.name == 'nt':
            return 'WINDOWS'
        else:
            return 'LINUX'

    def fetch_transaction_result(self, transaction_id, pipeline):
        url = ""
        if pipeline.lower() == "stanford":
            url = 'http://ec2-18-213-219-240.compute-1.amazonaws.com/cdc_service_stanford/fetch_cdc_stanford_service_result/' + transaction_id
        elif pipeline.lower() == "opennlp":
            url = 'http://lvsu1604009.lab.local/hla_service/opennlp/fetch_cdc_opennlp_service_result/' + transaction_id
        elif pipeline.lower() == "gate":
            url = 'http://lvsu1604009.lab.local/hla_service/gate/fetch_cdc_gate_service_result/' + transaction_id

        response = urllib.urlopen(url)
        transaction_result = response.read()
        # print(transaction_result)
        return transaction_result

if __name__ == '__main__':
    start_time = time.time()
    pipeline = sys.argv[1]
    input_folder = sys.argv[2]
    output_folder = sys.argv[3]
    caller = HLA_Service_Caller(pipeline, input_folder, output_folder)
    print("Finish calling the service! --- %s seconds ---" % (time.time() - start_time))
