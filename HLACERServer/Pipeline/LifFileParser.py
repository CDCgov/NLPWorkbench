from collections import defaultdict
import json

class LifFileParser:

    def __init__(self, filename="", string=""):
        if filename != "":
            file = open(filename)
            self.input_file = open(filename)
            self.input_txt = file.read()
            file.close()
            self.data = json.load(self.input_file)
        if string != "":
            self.data = json.loads(string)
        # self.annotations = list(defaultdict(list))

    def loadAnnotation(self, type):
        annotations = []
        for view in self.data['payload']['views']:
            for key in view['metadata']['contains'].keys():
                if type in view['metadata']['contains'][key]['producer']:
                    annotations = view['annotations']
                    # print("Loaded Annotation type", end=' ')
                    # print(type)
                    break
        # annotations = self.data['payload']['views'][0]['annotations']
        return annotations

    def updateAnnotation(self, annotations, type):
        number = len(self.data['payload']['views'])
        if number == 1:
            self.data['payload']['views'][0]['annotations'] = annotations
            # print("Succesfully Update Annotation type", end=' ')
            # print(type)
        else:
            for i in range(number):
                view = self.data['payload']['views'][i]
                for key in view['metadata']['contains'].keys():
                    if type in view['metadata']['contains'][key]['producer']:
                        self.data['payload']['views'][i]['annotations'] = annotations
                        # print("Succesfully Update Annotation type", end=' ')
                        # print(type)

    def addProducer(self, type, producer, tag_type, pipeline):
        number = len(self.data['payload']['views'])
        for i in range(number):
            view = self.data['payload']['views'][i]
            is_view = False
            for key in view['metadata']['contains'].keys():
                if type in view['metadata']['contains'][key]['producer']:
                    is_view = True
            if is_view:
                new_producer = {
                    'producer': producer,
                    'type': pipeline
                }
                self.data['payload']['views'][i]['metadata']['contains'][tag_type] = new_producer
                
    def writeLifFile(self, filename):
        output_file = open(filename, "w+", encoding="utf-8")
        json.dump(self.data, output_file, ensure_ascii=False)
