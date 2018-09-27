from collections import defaultdict
import json

class LifFileParser:

    '''
    This class implements the functions for loading LIF file.
    The functions include loading and updating the annotation sets.
    '''

    def __init__(self, filename):
        file = open(filename)
        self.input_file = open(filename)
        self.input_txt = file.read()
        file.close()
        self.data = json.load(self.input_file)

    def loadAnnotation(self, type):
        annotations = []
        for view in self.data['payload']['views']:
            for key in view['metadata']['contains'].keys():
                if type in view['metadata']['contains'][key]['producer']:
                    annotations = view['annotations']
                    print("Loaded Annotation type")
                    print(type)
                    break
        # annotations = self.data['payload']['views'][0]['annotations']
        return annotations

    def updateAnnotation(self, annotations, type):
        number = len(self.data['payload']['views'])
        if number == 1:
            self.data['payload']['views'][0]['annotations'] = annotations
            print("Succesfully Update Annotation type")
            print(type)
        else:
            for i in range(number):
                view = self.data['payload']['views'][i]
                for key in view['metadata']['contains'].keys():
                    if type in view['metadata']['contains'][key]['producer']:
                        self.data['payload']['views'][i]['annotations'] = annotations
                        print("Succesfully Update Annotation type")
                        print(type)

    def writeLifFile(self, filename):
        output_file = open(filename, "w+")
        json.dump(self.data, output_file, ensure_ascii=False)
