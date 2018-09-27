This project includes different tools added to LAPPS machine.
The tools can also be runned locally with the correct input and output format.
Example running command is showed as follows:

For the three PostTokenizers,

python PostTokenizer_*.py input.lif input.ann output.lif

For the three PostSentenceSplitters,

python PostSentenceSplitter_*.py input.lif output.lif

For POSPostProcessor,

python POSPostProcessor.py input.lif output.lif

For FeatureExtractor of OpenNLP and Stanford, the first two arguments specify the input and output filename. The 3rd and 4th arguments represent the boolearn value of whether to include POS Tag and MetaMap Code as features respectively. The 5th and 6th arguments represent the window size to the left and right. It can be runned as:

python FeatureExtractor.py input.lif output.bio yes yes 2 2

For FeatureExtractor of GATE, the first two arguments specify the input and output filename. The 3rd to 7th arguments represent the boolearn value of whether to include POS Tag, Orthography, Token Type, NounChunk Information, MetaMap Code as features respectively. The 8th and 9th arguments represent the window size to the left and right. It can be runned as:

python FeatureExtractor_GATE.py input.lif output.bio yes yes yes yes yes 2 2 

The tools above has been tested on Ubuntu on python 3.5 and python 2.7.
