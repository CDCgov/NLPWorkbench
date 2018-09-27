import json

'''
This file converts the text into LIF format for LAPPS
Lif type can be LIF or TEXT. Default type is LIF
'''

class TextToLif:

    def __init__(self, txt_string="", lif_type = "TEXT"):
        self.text = txt_string
        self.lif_type = lif_type
        self.lif_data = {}

    def convert_lif(self):
        if self.lif_type == "LIF":
            self.lif_data = {
                "discriminator": "http://vocab.lappsgrid.org/ns/media/jsonld#lif",
                "payload": {
                    "@context": "http://vocab.lappsgrid.org/context-1.0.0.jsonld",
                    "metadata": {},
                    "text": {
                        "@value": self.text
                    },
                    "views": []
                }
            }
        elif self.lif_type == "TEXT":
            self.lif_data = {
                "discriminator": "http://vocab.lappsgrid.org/ns/media/text",
                "payload": self.text
            }
        self.lif_string = json.dumps(self.lif_data)

if __name__ == "__main__":
    converter = TextToLif("dkdjldlfdlfkdfdlsld\ndkdjkdkd")
    converter.convert_lif()
    print(converter.lif_string)