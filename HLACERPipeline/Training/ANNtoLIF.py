from collections import defaultdict
import json
import glob

"""
This class turns the ANN format into LIF format
"""

class ANNtoLIF:

    def __init__(self, ann_filename, source=""):
        self.ann_filename = ann_filename
        self.file_num = ann_filename.split('/')[4].split('.')[0]
        self.ann_file = open(ann_filename)
        self.ann_data = json.load(self.ann_file)
        self.source = source
        self.output_filename = 'output/' + self.source + '_lif/' + self.file_num + '.lif'
        self.lif_data = defaultdict()


    def initialize_lif(self):
        self.lif_data["discriminator"] = "http://vocab.lappsgrid.org/ns/media/jsonld#lif"
        payload = {}
        payload["@context"] = "http://vocab.lappsgrid.org/context-1.0.0.jsonld"
        view = {
            "metadata": {
                "contains": "Stanford Pipeline CER"
            },
            "annotations": []
        }
        payload["views"] = [view]
        self.lif_data["payload"] = payload

    def extract_text(self):
        context = self.ann_data["__text"]
        text = {}
        text["@value"] = context
        self.lif_data["payload"]["text"] = text

    def extract_tags(self):
        context = self.ann_data["__text"]
        id = 0
        for type in self.ann_data.keys():
            if type != "__text":
                for tag_info in self.ann_data[type]:
                    start = tag_info["__extent"][0]
                    end = tag_info["__extent"][1]
                    annotation = {}
                    annotation["id"] = id
                    annotation["start"] = start
                    annotation["end"] = end
                    annotation["@type"] = type
                    annotation["label"] = "Tag"
                    features = {}
                    features["agreement"] = tag_info["__agreement__"]
                    features["annotator"] = tag_info["__annotator__"]
                    features["content"] = context[start:end]
                    annotation["features"] = features
                    self.lif_data["payload"]["views"][0]["annotations"].append(annotation)
        output_file = open(self.output_filename, "w+")
        json.dump(self.lif_data, output_file, ensure_ascii=False, indent=4, sort_keys=False)

if __name__ == "__main__":
    ann_folder = "output/bio/gate/ann/*.ann"
    ann_files = glob.glob(ann_folder)
    for ann_filename in ann_files:
        ann_filename = ann_filename.replace('\\','/')
        print(ann_filename)
        converter = ANNtoLIF(ann_filename)
        converter.initialize_lif()
        converter.extract_text()
        converter.extract_tags()