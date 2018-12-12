from LifFileParser import LifFileParser
import sys
import json
import re

class TokenType:

    def __init__(self, lif_string = "", lif_filename = ""):
        if lif_string != "":
            self.lif_string = lif_string
            # print(lif_string)
            self.lif_parser = LifFileParser(string=lif_string)
        elif lif_filename != "":
            self.lif_filename = lif_filename
            self.lif_parser = LifFileParser(filename=lif_filename)

    def add_orthography(self):
        annotations = self.lif_parser.loadAnnotation("Token")
        for i in range(len(annotations)):
            ann = annotations[i]
            if "Token" in ann['@type'] and 'word' in ann['features'].keys():
                type = ""
                word = str(ann["features"]["word"])
                if word.isalpha():
                    if word.isupper():
                        type = "Uppercase"
                    elif word[0].isupper():
                        type = "UpperInitial"
                    else:
                        type = "Lowercase"
                elif word.isdigit():
                    type = "Number"
                elif word.isalnum():
                    type = "AlphaNumeric"
                elif word.isspace():
                    type = "Space"
                elif re.match('([0-9]|0[0-9]|1[0-9]|2[0-3])(:[0-5][0-9])', word) is not None:
                    # regex = ^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$
                    type = "Time"
                else:
                    type = "Punctuation"
                annotations[i]["features"]["orth"] = type
        self.lif_parser.updateAnnotation(annotations, "Token")

    def add_token_type(self):
        annotations = self.lif_parser.loadAnnotation("Token")
        # print(annotations)
        for i in range(len(annotations)):
            ann = annotations[i]
            if "Token" in ann['@type']:
                type = ""
                word = str(ann["features"]["word"])
                if word.isspace():
                    type = "Space"
                elif word.isalpha():
                    type = "Word"
                elif word.isdigit():
                    type = "Digit"
                elif word.isalnum():
                    type = "AlphaNumeric"
                else:
                    type = "Punctuation"
                annotations[i]["features"]["TokenType"] = type
        self.lif_parser.updateAnnotation(annotations, "Token")

    def add_time(self):
        annotations = self.lif_parser.loadAnnotation("Token")
        # print(annotations)
        for i in range(len(annotations)):
            ann = annotations[i]
            if "Token" in ann['@type']:
                time = False
                word = str(ann["features"]["word"])
                if ":" in word:
                    parts = word.split(":")
                    if len(parts) == 2 and parts[0].isdigit() and parts[1].isdigit():
                        time = True
                annotations[i]["features"]["Time"] = time
        self.lif_parser.updateAnnotation(annotations, "Token")

def token_type_caller(lif_string = "", lif_filename = ""):
    token_type = None
    if lif_string != "":
        token_type = TokenType(lif_string=lif_string)
    elif lif_filename != "":
        token_type = TokenType(lif_filename=lif_filename)
    # print("Finish Parsing file")
    token_type.add_token_type()
    # print("Finish Updating file")
    lif_data = json.dumps(token_type.lif_parser.data)
    # print("Finish Loading file")
    return lif_data

def orthography_caller(lif_string = "", lif_filename = ""):
    token_type = None
    if lif_string != "":
        token_type = TokenType(lif_string=lif_string)
    elif lif_filename != "":
        token_type = TokenType(lif_filename=lif_filename)
    token_type.add_orthography()
    lif_data = json.dumps(token_type.lif_parser.data)
    return lif_data

def time_caller(lif_string = "", lif_filename = ""):
    token_type = None
    if lif_string != "":
        token_type = TokenType(lif_string=lif_string)
    elif lif_filename != "":
        token_type = TokenType(lif_filename=lif_filename)
    token_type.add_time()
    lif_data = json.dumps(token_type.lif_parser.data)
    return lif_data