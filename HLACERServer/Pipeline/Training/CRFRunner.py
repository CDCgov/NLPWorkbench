import subprocess
import glob
import sys
from Pipeline.Training.merge_bio import merge_bio

class CRFRunner:

    def __init__(self, train_file="", bio_folder="", template_file="", model_file = "", source = ""):
        self.train_file = train_file
        self.template_file = template_file
        self.model_file = model_file
        self.source = source

    def crf_test(self, test_bio, output_filename):
        if self.model_file == "":
            print("The model has not been trained yet!")
            return
        crf_testpath = 'crf_test'
        run_argument = [crf_testpath, '-m', self.model_file, test_bio]
        output = subprocess.check_output(run_argument).decode('utf-8')
        self.write_bio(output, output_filename)

    def write_bio(self, output, output_filename):
        output_file = open(output_filename, "w+")
        for line in output.split('\n'):
            line = line + "\n"
            output_file.write(line)
        output_file.close()

    def crf_train(self):
        crf_trainpath = 'crf_learn'
        run_argument = [crf_trainpath, self.template_file, self.train_file, self.model_file, '-p', str(2)]
        subprocess.call(run_argument)

    def write_position(self, file_num):
        position_filename = 'output/bio/stanford/tagged/' + file_num + '.pos'
        input_filename = 'input/'

if __name__ == "__main__":
    print("Must be called by Pipeline Core")