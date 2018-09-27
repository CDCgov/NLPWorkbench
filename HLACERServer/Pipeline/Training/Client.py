from requests import Session
from requests.auth import HTTPBasicAuth
from zeep import Client
from zeep.transports import Transport
import json

class ServiceClient:
    def __init__(self, uri):
        session = Session()
        session.auth = HTTPBasicAuth('tester', 'tester')
        transport = Transport(session=session, timeout=600)
        self.client = Client(uri, transport=transport)

    def get_metadata(self):
        return self.client.service.getMetadata()

    def execute(self, json):
        return self.client.service.execute(json)

# token_file = open('../input/feature/stanford_token_3.lif')
# token_text = token_file.read()
# sentence_splitter = ServiceClient('http://vassar.lappsgrid.org/wsdl/anc:stanford.splitter_2.0.0')
# result = sentence_splitter.execute(token_text)
# print(json.loads(result))
# print(sentence_splitter.get_metadata())