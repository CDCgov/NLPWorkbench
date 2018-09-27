import sys
from Pipeline.LifFileParser import LifFileParser
import corenlp
import json

class StanfordTokenizer:

	def __init__(self, lif_string=""):
		self.lif_parser = LifFileParser(string=lif_string)
		self.text = self.lif_parser.data['payload']['text']['@value']

	def call_tokenizer(self):
		token_annotations = []
		port = 9000
		address = "http://localhost:" + str(port)
		success = False
		while not success:
			try:
				address = "http://localhost:" + str(port)
				with corenlp.client.CoreNLPClient(annotators="tokenize".split(),timeout=300000, endpoint=address) as client:
					ann = client.annotate(self.text)
					id = 0
					for token in ann.sentencelessToken:
						word = token.word
						start = int(token.beginChar)
						end = int(token.endChar)
						new_ann = {
							"id": "tok" + str(id),
							"start": start,
							"end": end,
							"@type": "http://vocab.lappsgrid.org/Token",
							"label": "http://vocab.lappsgrid.org/Token",
							"features": {
								"word": word
							}
						}
						id += 1
						token_annotations.append(new_ann)
					success = True
					break
			except:
				port += 1
				success = False
		metadata = {
			"contains": {
					"http://vocab.lappsgrid.org/Token": {
						"producer": "org.anc.lapps.stanford.Tokenizer:2.0.0",
						"type": "stanford"
					}
			}
		}
		view = {
			"metadata": metadata,
			"annotations": token_annotations
		}
		self.lif_parser.data["payload"]["views"].append(view)

class StanfordSentenceSplitter:

	def __init__(self, lif_string=""):
		self.lif_parser = LifFileParser(string=lif_string)
		self.text = self.lif_parser.data['payload']['text']['@value']

	def call_splitter(self):
		sent_annotations = []
		port = 9000
		address = "http://localhost:" + str(port)
		success = False
		while not success:
			try:
				address = "http://localhost:" + str(port)
				with corenlp.client.CoreNLPClient(annotators="ssplit".split(), timeout=300000, endpoint=address) as client:
					ann = client.annotate(self.text)
					id = 0
					for sentence in ann.sentence:
						length = len(sentence.token)
						start = int(sentence.token[0].beginChar)
						end = int(sentence.token[length-1].endChar)
						ss = self.text[start:end]
						new_ann = {
							"id": "sent"+str(id),
							"start": start,
							"end": end,
							"@type": "http://vocab.lappsgrid.org/Sentence",
							"label": "Sentence",
							"features":{
								"sentence": ss
							}
						}
						id += 1
						sent_annotations.append(new_ann)
					success = True
					break
			except:
				port += 1
				success = False
		metadata = {
			"contains": {
				"http://vocab.lappsgrid.org/Sentence": {
					"producer": "org.anc.lapps.stanford.SentenceSplitter:2.0.0",
					"type": "sentence:stanford"
				}
			}
		}
		view = {
			"metadata": metadata,
			"annotations": sent_annotations
		}
		self.lif_parser.data["payload"]["views"].append(view)

class StanfordTagger:

	def __init__(self, lif_string=""):
		self.lif_parser = LifFileParser(string=lif_string)
		self.text = self.lif_parser.data['payload']['text']['@value']

	def call_pos(self):
		sent_annotations = []
		with corenlp.client.CoreNLPClient(annotators="pos".split()) as client:
			ann = client.annotate(self.text)
			print(ann)

if __name__ == "__main__":
	converter = TextToLif("Gross Pathology\nThis content contains adenocarcinoma !? I expect it to be tagged.")
	converter.convert_lif()
	
	st = StanfordTokenizer(converter.lif_string)
	st.call_tokenizer()
	stanford_tokenizer_lif = json.dumps(st.lif_parser.data)

	sentence = StanfordSentenceSplitter(stanford_tokenizer_lif)
	sentence.call_splitter()
	stantord_sentence_splitter_lif = json.dumps(sentence.lif_parser.data)
	print(stantord_sentence_splitter_lif)
