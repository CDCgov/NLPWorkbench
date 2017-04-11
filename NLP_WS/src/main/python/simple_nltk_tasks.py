from flask import Flask, request, make_response
from json import dumps
from nltk.tokenize import sent_tokenize
from nltk.tokenize import word_tokenize

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'

@app.route('/nltk/sentences', methods=['POST'])
def splitSentences():
    data = request.get_data()
    sent_tokenize_list = sent_tokenize(data)
    response = make_response(dumps(sent_tokenize_list))
    response.headers['Content-Type'] = 'application/json; charset=utf-8'
    return response

@app.route('/nltk/tokenize')
def tokenize():
    sent = request.args.get('sentence')
    word_list = word_tokenize(sent)
    response = make_response(dumps(word_list))
    response.headers['Content-Type'] = 'application/json; charset=utf-8'
    return response


if __name__ == '__main__':
    app.run()