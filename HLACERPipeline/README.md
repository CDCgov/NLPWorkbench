This project includes the CER Pipelines for 4 pipelines: Stanford, OpenNLP, GATE, cTAKE

To run the CER pipelines and generate the model as well as the tagged files, the following command could be used in Training folder:

nohup python3 –u Stanford.py > stanford.out &
nohup python3 –u OpenNLP.py > opennlp.out &
nohup python3 –u GATE.py > gate.out &
nohup python3 –u cTAKE.py > ctake.out &

The input is the corpus of CDC_ann files stored in Training/input/CDC_ann folder. There are 4 various kinds of output:
1. Training BIO Files 
	e.g. Training/output/bio/stanford/train/*.bio
	e.g. Training/output/bio/stanford/stanford.bio
2. Model File
	e.g. Training/output/bio/stanford/stanford_model
3. Tagged BIO Files
	e.g. Training/output/bio/stanford/tagged/*.bio
	e.g. Training/output/bio/stanford/stanford_tagged.bio
4. Output LIF Files
	e.g. Training/output/stanford_lif/*.lif

After the pipeline finishes running, the evaluation statistics including precision, recall, f-score can be generated using the following example command:

python Eval.py p output/bio/stanford/stanford_tagged.bio