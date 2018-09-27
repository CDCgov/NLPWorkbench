from requests import Session
from requests.auth import HTTPBasicAuth
from zeep import Client
from zeep.transports import Transport
import json

class ServiceClient:
    def __init__(self, uri):
        session = Session()
        session.auth = HTTPBasicAuth('tester', 'tester')
        transport = Transport(session=session)
        self.client = Client(uri, transport=transport)

    def get_metadata(self):
        return self.client.service.getMetadata()

    def execute(self, json):
        return self.client.service.execute(json)
