DIRS="tools/common/
	tools/converters/
	tools/dbpedia/
	tools/gate/
	tools/gigaword/
	tools/kyoto/
	tools/lingpipe/
	tools/masc/
	tools/oaqa/
	tools/opennlp/
	tools/ranking/
	tools/stanford/
	tools/test/"
	
for dir in $DIRS ; do
	rm -rf $dir
done