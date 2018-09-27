from Pipeline.Authentication import Authentication
import requests
import json
import copy
from pprint import pprint
import time

def find_snomedct_server(term):
    apikey = "ca310f05-53e6-4984-82fd-8691dc30174e"
    AuthClient = Authentication(apikey)
    version = "2017AB"
    tgt = AuthClient.gettgt()
    query = {'ticket': AuthClient.getst(tgt), 'targetSource': 'SNOMEDCT_US'}
    base_uri = "https://uts-ws.nlm.nih.gov/rest"
    search_uri = "/search/current?string="
    content_uri = "/content/current/CUI/"
    source = "&sabs=SNOMEDCT_US"
    search_type = '&searchType=words'
    path = base_uri + search_uri + term + search_type + source
    r = requests.get(path, params=query)
    code, name, semantic = "", "", ""
    try:
        items = json.loads(r.text)
        pprint(items['result']['results'])
        code, name = select_code(items['result']['results'], term)
        if code != "":
            path2 = base_uri + content_uri + code
            tgt2 = AuthClient.gettgt()
            query2 = {'ticket': AuthClient.getst(tgt2), 'targetSource': 'SNOMEDCT_US'}
            r2 = requests.get(path2, params=query2)
            try:
                items2 = json.loads(r2.text)
                semantic = items2['result']['semanticTypes'][0]['name']
            except json.decoder.JSONDecodeError:
                semantic = "UNKNOWN"
    except json.decoder.JSONDecodeError:
        code, name = "", ""
    return code, name, semantic

def find_snomed(snomed_term, snomed_code, term):
    match_code = []
    local = time.time()
    for description, code in zip(snomed_term, snomed_code):
        if term in description:
            sub = {
                'name': description,
                'ui': code
            }
            match_code.append(sub)
    # print(match_code)
    code, name = select_code(match_code, term)
    return code, name

# Select the maximum code and name that matches the searched term
def select_code(results, term):
    # Initialize the minimum number of matches threshold we accept
    score = 0.6
    def_score = 0.4
    code, name = "",""
    for result in results:
        title = result['name']
        temp_score, temp_def_score = calculate_score(title, term)
        if temp_score > score and temp_def_score > def_score:
            score = temp_score
            def_score = temp_def_score
            code = result['ui']
            name = title
    return code, name

# Calculate the similarity score between SNOMED CT name and the term to be searched
def calculate_score(name, term):
    score, score_name = 0, 0
    separate = str(term).lower().split(' ')
    separate_copy = copy.deepcopy(separate)
    number = len(separate)
    definitions = str(name).lower().split(' (')[0].split(' ')
    definitions_copy = copy.deepcopy(definitions)
    number_of_definitions = len(definitions)
    for word in definitions:
        if separate_copy != None:
            if word.lower() in separate_copy:
                score_name = score_name + 1
                separate_copy.remove(word.lower())
            elif len(word) > 1 and word[-1] == 's' and word[:-1].lower() in separate_copy:
                score_name = score_name + 1
                separate_copy.remove(word[:-1].lower())
            elif word.lower() == 'centimeter' and separate_copy[0] == 'cm':
                score_name = score_name + 1
                separate_copy.remove(separate_copy[0])
            else:
                for sep in separate_copy:
                    if word.lower() in sep:
                        score_name = score_name + 1
                        separate_copy.remove(sep)
                        break
            # term = str(term).replace(word.lower(), "")
    for word in separate:
        if definitions_copy != None:
            if word.lower() != 'x' and word.replace('.', '', 1).isdigit() == False \
                    and word.lower() in definitions_copy:
                score = score + 1
                definitions_copy.remove(word.lower())
            elif len(word) >= 1 and word[-1] == 's' and word[:-1].lower() in definitions_copy:
                score = score + 1
                definitions_copy.remove(word[:-1].lower())
            elif word.replace('.', '', 1).isdigit() and len(definitions_copy) == 1 \
                    and definitions_copy[0].replace('.','',1).isdigit():
                score = score + 1
                definitions_copy.remove(word.lower())
            elif word.lower() == 'cm' and definitions_copy[0] == 'centimeter':
                score = score + 1
                definitions_copy.remove(definitions_copy[0])
            elif word.lower() != 'x' and word.replace('.', '', 1).isdigit() == False:
                for defi in definitions_copy:
                    if word.lower() in defi:
                        score = score + 1
                        definitions_copy.remove(defi)
                        break
            # name = str(name).replace(word.lower(), '')
    return score/number, score_name/number_of_definitions
