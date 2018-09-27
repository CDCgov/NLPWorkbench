import sys
import glob

def merge_bio(input_files, output_file):
    result_bio = open(output_file, "w+")
    for filename in input_files:
        filename = filename.replace('\\', '/')
        file = open(filename)
        for line in file.readlines():
            result_bio.write(line)
    result_bio.close()

if __name__ == "__main__":
    input_files = glob.glob('output/bio/stanford/tagged/Batch_1_*.bio')
    merge_bio(input_files,'output/bio/stanford/stanford_tagged_1.bio')
