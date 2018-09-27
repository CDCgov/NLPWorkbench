
/****************************************************************************/
/* How to use the TreeTagger                                                */
/*                                                                          */
/* Author: Helmut Schmid, CIS, Ludwig-Maximilians-Universität, Germany      */
/****************************************************************************/


The TreeTagger consists of two programs: the training program creates
a parameter file from a fullform lexicon and a handtagged corpus. The
tagger program reads the parameter file and annotates the text with
part of speech and lemma information. Both programs print information
about their usage when they are called without arguments.


Tagging
-------

Tagging is done with the *tree-tagger* program. 

The first argument is the name of a parameter file which was generated
with the train-tree-tagger program. Parameter files generated on
different platforms or with older versions of train-tree-tagger will
not work.

The second argument is the input file. It must be in one-word-per-line
format, i.e. each line contains one token (word, punctuation character
or parenthesis) and should not exceed 1000 characters. Tokens may contain
blanks. It is possible to override the lexical information contained
in the parameter file of the tagger by specifying a list of possible
tags after the token. This list has to be preceded by a tab character
and the elements are separated by tab characters. Pretagging could be
used e.g. to ensure that certain text-specific expressions are tagged
correctly. Clitics (like "'s", "'re", and "'d" in English or "-la" and
"-t-elle" in French) have to be separated if they were separated in
the training data. (The French and English parameter files available
by ftp expect separation of clitics).

Sample input file:
He
moved
to
New York City	NP
.


The third argument is the name of the output file. The output is also
in one-word-per-line format. Depending on the specified options, it
will contain columns with tokens, tags and lemmas. If the third
argument is missing, the output will be printed to standard output. If
the second argument is missing, too, input is read from standard
input.

Options:

-token: Prints the token as well.
-lemma: Prints the lemma as well.
-sgml:  Don't tag SGML annotations, i.e. lines starting with '<' and ending
        with '>'.
-threshold <p>: Print all tags with a probability higher than <p> times the
        probability of the best tag.
-prob:  Print tag probabilities (requires option -threshold)
-no-unknown: Print the token rather than <unknown> for unknown lemmas
-quiet: Don't print status messages
-pt-with-lemma: If this option is specified, then each pretagging tag
        (see above) has to be followed by a whitespace and a lemma.
-pt-with-prob: If this option is specified, then each pretagging tag
        (see above) has to be followed by whitespace and a tag probability
        value. If -pt-with-prob and -pt-with-lemma have been specified,
        then each pretagging tag is followed by a probability and a lemma
        in that order.
-files f: Read the names of input and output files pairwise from the
        file f. The format of f is the lexicon file format described below.
-lex f: Read auxiliary lexicon entries from the file f.
-eos-tag <tag>: The SGML tag <tag> signals the end of a sentence.
        This option implies the option -sgml

Some more exotic options:
-proto: Print lexical information for each word
  The lexicon type is signalled by one of the characters
  f: The word was found in the full form lexicon.
  c: The word in lowercase was found in the lexicon
  h: The word contains an hyphen and the word following the hyphen was found
     in the full form lexicon; e.g. instead of "table-wine" only "wine" has
     been found.
  s: The word has been looked up in the suffix lexicon
  p: Tags have been assigned by pretagging.
-gramotron: Same as -proto but with a different format
-proto-with-prob: Same as -proto but with lexical tag probabilities
-print-prob-tree: Print the transition probability tree and exit
-eps <epsilon>: Value which is used to replace zero lexical frequencies.
  Zero frequencies occur when a word/tag pair is contained in the lexicon
  but not in the training corpus. The default is 0.1.
-base:  Use only lexical probabilities for tagging. This option is only
  useful to obtain a baseline result to which the actual tagger output is
  compared.



Training
--------

Training is done with the *train-tree-tagger* program. If the program is 
called without arguments, the following output is printed:

USAGE: train-tree-tagger <lexicon> <open class file> <infile> <outfile> 
       {-cl <context length>} {-dtg <min. decision tree gain>}
       {-ecw <eq. class weight>} {-atg <affix tree gain>} {-st <sent. tag>}

Description of the command line arguments:
* <lexicon>: name of a file which contains the fullform lexicon. Each line 
  of the lexicon corresponds to one word form and contains the word form 
  itself followed by a Tab character and a sequence of tag-lemma pairs.
  The tags and lemmata are separated by whitespace.

Example:
aback	RB aback
abacuses	NNS abacus
abandon	VB abandon	VBP abandon
abandoned	JJ abandoned	VBD abandon	VBN abandon
abandoning	VBG abandon

  Important: Ordinal and cardinal numbers which consist of digits
  should not be included in the lexicon. Otherwise, the tagger will
  not be able to learn how to tag numbers which are not listed in the
  lexicon. Numbers with unusual tags should be added to the lexicon,
  however.

  Remark: The tagger doesn't need the lemmata for tagging. If
  you do not have the lemma information or if you do not plan to
  annotate corpora with lemmas, you can replace the lemma with a dummy
  value, e.g. "-".

* <open class file>: name of a file which contains a list of open class tags
  i.e. possible tags of unknown word forms. This information is needed to
  estimate likely tags of unknown words. This file would typically contain
  adverb, adjective, noun, proper name and perhaps verb tags, but not
  prepositions, determiners, pronouns or numbers.
* <input file>: name of a file which contains tagged training data. The data
  must be in one-word-per-line format. This means that each line contains 
  one token and one tag in that order separated by a tabulator. 
  Punctuation marks are considered as tokens and must have been tagged as well.

Example:
Pierre	NP
Vinken	NP
,	,
61	CD
years	NNS

* <output file>: name of the file in which the resulting tagger parameters 
  are stored.


The following parameters are optional:

* -cl <context length>: number of preceding words forming the tagging
  context. The default is 2 which corresponds to a trigram context. For
  small training corpora and/or large tagsets, it could be useful to reduce
  this parameter to 1.
* -dtg <min. decision tree gain>: Threshold - If the information gain at a 
  leaf node of the decision tree is below this threshold, the node is deleted.
  The default value is 0.7.
* -ecw <eq. class weight>: weight of the equivalence class based probability
  estimates. The default is 0.15.
* -atg <affix tree gain> Threshold - If the information gain at a leaf of an
  affix tree is below this threshold, it is deleted. The default is 1.2.
* -st <sent. tag>: the end-of-sentence part-of-speech tag, i.e. the tag which
  is assigned to sentence punctuation like ".", "!", "?". 
  Default is "SENT". It is important to set this option properly, if your
  tag for sentence punctuation is not "SENT".

The accuracy of the TreeTagger usually improves a bit, if different
settings of the above parameters are tested and the best combination
is chosen.
