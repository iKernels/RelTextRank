__author__ = 'kateryna'
import os

basefolder="/Users/kateryna/Documents/workspace/RelationalTextRanking/" \
           "data/semeval2016/task3a"
label_file="original-data/test-labels.txt"

gold_labels=dict()
with open(os.path.join(basefolder,label_file),"r") as f:
    for line in f.readlines():
        if "RELC_RELEVANCE2RELQ" in line:
            continue
        aid, label = line.strip().split(" ")
        gold_labels[aid] = label
        if label=="true":
            print aid, label


source_file="test-candidates-unlabeled.txt"
output_file="test-candidates.txt"

outf = open(os.path.join(basefolder,output_file),"w")
inf = open(os.path.join(basefolder,source_file), "r")

for line in inf.readlines():
    parts = line.split(" ",5)
    aid = parts[1]
    label = gold_labels[aid]
    parts[4] = label
    # if label=="true":
    #     print line
    outf.write(" ".join(parts))

outf.close()
inf.close()