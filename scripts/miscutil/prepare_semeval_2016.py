__author__ = 'kateryna'
import sys

def rewrite_file(input_file_path, output_file_path):
    inputf = open(input_file_path, "r")
    outputf = open(output_file_path, "w")

    for line in inputf:
        cols = line.split(" ")

        if cols[4]=="Good":
            cols[4] = "true"
        else:
            cols[4] = "false"
        outputf.write(" ".join(cols))
    inputf.close()
    outputf.close()

if __name__ == '__main__':

    if len(sys.argv)!=3:
        print "Usage: <input-semeval-file> <output-semeval-file>"
        sys.exit(0)

    input_file_path = sys.argv[1]
    output_file_path_question = sys.argv[2]

    rewrite_file(input_file_path,output_file_path_question)