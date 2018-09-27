import subprocess
import glob
import sys
from merge_bio import merge_bio

"""
This is the class to run CRF with the specified training file, model filename, template file.
There is also function to test each file in the specified corpus.
"""
class CRFRunner:

    def __init__(self, train_file, bio_folder, template_file="", model_file = "", source = ""):
        self.train_file = train_file
        self.bio_files = glob.glob(bio_folder)
        self.template_file = template_file
        self.model_file = model_file
        self.source = source

    def crf_test(self):
        if self.model_file == "":
            print("The model has not been trained yet!")
            return
        crf_testpath = 'crf_test'
        for test_bio in self.bio_files:
            test_bio = test_bio.replace('\\', '/')
            file_num = test_bio.split('/')[4].split('.')[0]

            # output_filename = 'output/bio/stanford/tagged/' + file_num + '.bio'
            run_argument = [crf_testpath, '-m', self.model_file, test_bio]
            output = subprocess.run(run_argument, stdout=subprocess.PIPE).stdout.decode('utf-8')
            self.write_bio(file_num, output)

    def write_bio(self, file_num, output):
        output_filename = 'output/bio/' + self.source + '/tagged/' + file_num + '.bio'
        output_file = open(output_filename, "w+")
        for line in output.split('\n'):
            line = line + "\n"
            output_file.write(line)
        output_file.close()

    def crf_train(self):
        crf_trainpath = 'crf_learn'
        run_argument = [crf_trainpath, self.template_file, self.train_file, self.model_file, '-p', str(9)]
        subprocess.call(run_argument)

    def write_position(self, file_num):
        position_filename = 'output/bio/stanford/tagged/' + file_num + '.pos'
        input_filename = 'input/'

if __name__ == "__main__":
    bio_folder = 'output/bio/stanford/train/*.bio'
    model_file = 'output/model/stanford_model'
    train_file = 'output/bio/stanford/stanford_fix.bio'
    template_file = 'output/bio/stanford/template'
    position_folder = 'output/bio/stanford'
    merge_bio(glob.glob(bio_folder), train_file)
    crf_runner = CRFRunner(train_file, bio_folder, model_file=model_file, template_file=template_file, source="stanford")
    crf_runner.crf_train()
    crf_runner.crf_test()