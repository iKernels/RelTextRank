__author__ = 'kateryna'
#converts the svm pipeline prediction and relevancy files into an input file to be used by trec_eval

import argparse
from res_file_reader import ResFileReader

def read_orig_file(origfile):
    qid = ""
    i  = 0;
    mapping = dict()
    with open(origfile, "r") as f:
        for line in f:
            cur_qid = line.split(" ")[0].strip()
            if cur_qid=="QuestionID":
                continue
            if cur_qid!=qid:
                qid = cur_qid
                i += 1
            if not qid in mapping:
                mapping[qid] = i

    #print "read the qid mapping"
    #print mapping
    return mapping



def convert(svmpred, svmrel, origfile, outfilepath):
    mapping = read_orig_file(origfile)
    outfile = open(outfilepath, "w")
    lineReader = ResFileReader("trec")
    for line_res, line_pred in zip(open(svmrel), open(svmpred)):
        # Process the line from the res file.
        qid, aid, relevant, ir_score = lineReader.read_line(line_res)
        pred_score = line_pred.strip()
        outfile.write("%d 0 %s 0 %s UNITN\n" % (mapping[qid], aid.split("-")[-1], pred_score) )
    outfile.close()

if __name__ == '__main__':
  parser = argparse.ArgumentParser(description='Convert svm output to svmlight')
  parser.add_argument('-p', '--svmpred', metavar='svmpred',  help='svm predictions file')
  parser.add_argument('-r', '--svmrel', metavar='svmrel',  help='svm relevancy file for the prediction file')
  parser.add_argument('-w', '--origfile', metavar='origfile',  help='original wikiQA file')
  parser.add_argument('-o', '--output', metavar='output',  help='output file')

  args = parser.parse_args()
  #print("Started conversion")
  convert(args.svmpred, args.svmrel, args.origfile, args.output)
  #print("output written to %s" % args.output)