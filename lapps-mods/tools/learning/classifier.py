#!/ltg/python/bin/python2.7
# coding: utf-8

import sys
import numpy as np
import pandas as pd
from sklearn import cross_validation
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score
from sklearn.metrics import classification_report
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score

if __name__ == '__main__':
    data = pd.read_csv(sys.argv[1], header=None, delimiter="\t", error_bad_lines=False)
    data.reindex(np.random.permutation(data.index))

    train_data_features = data.iloc[:, 1:]
    classes = data.iloc[:, 0]
    
    outfile_name = sys.argv[2]
    outfile = open(outfile_name,"w")

    # Optionally print feature table size
    outfile.write('Train data:', train_data_features.shape)

#    outfile.write("Training the model (this may take a while)...")

    algo = LogisticRegression(penalty="l2", solver="lbfgs", multi_class="multinomial", max_iter=300, n_jobs=4)

    outfile.write(algo)

    # Fit the model to the training set, using the vectors as
    # features and the class labels as the response variable
    # This may take a few minutes to run
    classifier = algo.fit(train_data_features, classes)

    # Evaluating...
    predicted = classifier.predict(train_data_features)

    outfile.write("Number of mislabeled points out of a total %d points in the training set: %d" % (
        train_data_features.shape[0], (classes != predicted).sum()))

    outfile.write ("Accuracy on the training set:", accuracy_score(classes, predicted))

    outfile.write(classification_report(classes, predicted))

    outfile.write ('Confusion matrix on the training set:')
    outfile.write (confusion_matrix(classes, predicted))

    #outfile.write >> sys.stderr, 'Calculating cross-validated scores...'
    predicted = cross_validation.cross_val_predict(classifier, train_data_features, classes, cv=10)
    outfile.write(classification_report(classes, predicted))
    outfile.write('Macro-averaged F1', f1_score(classes, predicted, average="macro"))
    outfile.write('Micro-averaged F1', f1_score(classes, predicted, average="micro"))
    outfile.write('Weighted F1', f1_score(classes, predicted, average="weighted"))
