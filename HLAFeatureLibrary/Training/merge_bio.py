import sys
import glob

def merge_bio(input_files, output_file):
    result_bio = open(output_file, "w+")
    for filename in input_files:
        filename = filename.replace('\\', '/')
        file = open(filename)
        for line in file.readlines():
            result_bio.write(line)
        # result_bio.write("\n")
    result_bio.close()

if __name__ == "__main__":
    # arguments = sys.argv
    # arguments = ['../output/bio/gate_3.bio','../output/bio/gate_11.bio','../output/bio/gate_5.bio',
    #              '../output/bio/gate_6.bio','../output/bio/gate_7.bio','../output/bio/gate_8.bio']
    # arguments = ['../output/bio/gate_9.bio', '../output/bio/gate_10.bio']
    input_files = glob.glob('output/bio/stanford/tagged/*.bio')
    merge_bio(input_files,'output/bio/stanford/stanford_tagged.bio')
