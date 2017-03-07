from __future__ import division

import os
import sys
import logging
from optparse import OptionParser
import metrics
from ev import find_correct_answer_position
from ev import analyze_reranking_improvement
import logging
from collections import defaultdict
from operator import itemgetter
from res_file_reader import ResFileReader
import math

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


def read_truth_file(infile,format,cut_truth_map_at_N=None):
  truth = dict()
  r = ResFileReader(format)
  prevqid=""
  idcou=0;
  for line in open(infile, 'r'):
    qid, aid, relevant, ir_score = r.read_line_trec(line)
    if cut_truth_map_at_N==None:
        truth[(qid,aid)] = relevant
    else:
        if qid==prevqid:
            idcou +=1
        else:
            prevqid=qid
            idcou=1
        if idcou<=cut_truth_map_at_N:
            truth[(qid,aid)] = relevant


  return truth


def read_res_pred_files(res_fname, pred_fname, format, verbose=True,
                        reranking_th=-100.0,
                        ignore_noanswer=False, truth_map=None):
    lineReader = ResFileReader(format)

    ir, svm = defaultdict(list), defaultdict(list)
    for line_res, line_pred in zip(open(res_fname), open(pred_fname)):
        # Process the line from the res file.
        qid, aid, relevant, ir_score = lineReader.read_line(line_res)
        if (qid, aid) in truth_map:
            if (relevant != truth_map[(qid, aid)]):
         #       print "%s, %s changed label from %s to %s" % (qid, aid, relevant, truth_map[(qid, aid)])
                relevant = truth_map[(qid, aid)]
        #else:
        #    print qid, aid, "not found"
            pred_score = float(line_pred.strip())
            ir[qid].append((relevant, ir_score, aid))
            svm[qid].append((relevant, pred_score, aid))
        #else:
        #    print qid, aid, "not found in the gold standard annotations"
    if verbose:
        analyze_file = open(pred_fname + ".analyzis", "w")
        info_file = open(pred_fname + ".correctpos", "w")
    print "Annotations for %d question read" % (len(ir))
    # Remove questions that contain no correct answer
    if ignore_noanswer:
        for qid in ir.keys():
            candidates = ir[qid]
            if all(relevant == "false" for relevant, _, _ in candidates) or all(relevant == "true" for relevant, _, _ in candidates):
                del ir[qid]
                del svm[qid]

    for qid in ir:
        # Sort by IR score.
        ir_sorted = sorted(ir[qid], key=itemgetter(1), reverse=True)

        # Sort by SVM prediction score.
        svm_sorted = svm[qid]
        max_score = max([score for rel, score, aid in svm_sorted])
        if max_score >= reranking_th:
            svm_sorted = sorted(svm_sorted, key=itemgetter(1), reverse=True)

        if verbose:
            before = find_correct_answer_position(ir_sorted)
            after = find_correct_answer_position(svm_sorted)
            impr = analyze_reranking_improvement(before, after)
            analyze_file.write("%s %s\n" % (qid, str(impr)))
            info_file.write("%s %s %s\n" % (qid, str(before), str(after)))

        ir[qid] = [rel for rel, score, aid in ir_sorted]
        svm[qid] = [rel for rel, score, aid in svm_sorted]

    if verbose:
        analyze_file.close()
        info_file.close()

    return ir, svm

def stats_cv(path=".",  format="trec", prefix="svm", th=50, suf="", verbose=False, truth_file=None, ignore_noanswer=False,cut_truth_map_at_N=None):
  mrrs_se = []
  mrrs_svm = []
  abs_mrrs = []
  rel_mrrs = []

  maps_se = []
  maps_svm = []
  abs_maps = []
  rel_maps = []

  recalls1_se = []
  recalls1_svm = []
  abs_recalls = []
  rel_recalls = []


  num_folds = 0
  truth = read_truth_file(truth_file, format,cut_truth_map_at_N)
  print "%13s %5s %7s %7s" %("IR", "SVM", "(abs)", "(rel)")
  for fold in sorted(os.listdir(path)):
    currentFold = os.path.join(path, fold)
    if not os.path.isdir(currentFold):
      continue
    if not fold.startswith("fold"):
      logging.warn("Directories containing CV folds should start with 'fold'")
      continue
    print fold

    # Relevancy file
    res_fname = os.path.join(currentFold, "%s.relevancy" % prefix)
    if not os.path.exists(res_fname):
      logging.error("Relevancy file not found: %s", res_fname)
      sys.exit(1)

    # Predictions file
    pred_fname = os.path.join(currentFold, "%s.pred" % (prefix + suf))
    if not os.path.exists(pred_fname):
      logging.error("SVM prediction file not found: %s", pred_fname)
      sys.exit(1)

    try:
      ir, svm = read_res_pred_files(res_fname, pred_fname, format, verbose, ignore_noanswer=ignore_noanswer, truth_map=truth)
    except:
      logging.error("Failed to process input files: %s %s", res_fname, pred_fname)
      logging.error("Check that the input file format is correct")
      sys.exit(1)

    # MRR
    mrr_se = metrics.mrr(ir, th)
    mrr_svm = metrics.mrr(svm, th)
    mrrs_se.append(mrr_se)
    mrrs_svm.append(mrr_svm)

    # improvement
    abs_mrr_diff = mrr_svm - mrr_se
    rel_mrr_diff = (mrr_svm - mrr_se)*100/mrr_se
    abs_mrrs.append(abs_mrr_diff)
    rel_mrrs.append(rel_mrr_diff)

    print "MRR: %5.2f %5.2f %+6.2f%% %+6.2f%%" % (mrr_se, mrr_svm, abs_mrr_diff, rel_mrr_diff) 

    # MAP
    map_se = metrics.map(ir)
    map_svm = metrics.map(svm)
    maps_se.append(map_se)
    maps_svm.append(map_svm)    
    # improvement
    abs_map_diff = map_svm - map_se
    rel_map_diff = (map_svm - map_se)*100/map_se
    abs_maps.append(abs_map_diff)
    rel_maps.append(rel_map_diff)
    print "MAP: %5.2f %5.2f %+6.2f%% %+6.2f%%" % (map_se*100, map_svm*100, abs_map_diff, rel_map_diff)

    # Recall-of-1@1
    rec_se = metrics.recall_of_1(ir, th)[0]
    rec_svm = metrics.recall_of_1(svm, th)[0]
    recalls1_se.append(rec_se)
    recalls1_svm.append(rec_svm)

    # improvement
    abs_rec_diff = rec_svm - rec_se
    rel_rec_diff = (rec_svm - rec_se)*100/rec_se
    abs_recalls.append(abs_rec_diff)
    rel_recalls.append(rel_rec_diff)

    print "P@1: %5.2f %5.2f %+6.2f%% %+6.2f%%" % (rec_se, rec_svm, abs_rec_diff, rel_rec_diff)   

    num_folds += 1

  # mrrs
  avg_mrr_se, std_mrr_se = mean_and_std(mrrs_se)
  avg_mrr_svm, std_mrr_svm = mean_and_std(mrrs_svm)
  avg_abs_impr_mrr, std_abs_impr_mrr = mean_and_std(abs_mrrs)
  avg_rel_impr_mrr, std_rel_impr_mrr = mean_and_std(rel_mrrs)

  # maps
  avg_map_se, std_map_se = mean_and_std(maps_se)
  avg_map_svm, std_map_svm = mean_and_std(maps_svm)
  avg_abs_impr_map, std_abs_impr_map = mean_and_std(abs_maps)
  avg_rel_impr_map, std_rel_impr_map = mean_and_std(rel_maps)

  # recall
  avg_rec1_se, std_rec1_se = mean_and_std(recalls1_se)  # se 
  avg_rec1_svm, std_rec1_svm = mean_and_std(recalls1_svm)  # svm
  avg_abs_impr_rec1, std_abs_impr_rec1 = mean_and_std(abs_recalls)  # absolute
  avg_rel_impr_rec1, std_rel_impr_rec1 = mean_and_std(rel_recalls)  # relative

  FMT = u"%3s: %5.2f \u00B1 %4.2f %5.2f \u00B1 %4.2f %+6.2f%% \u00B1 %4.2f %+6.2f%% \u00B1 %4.2f"
  print
  print "Averaged over %s folds" % num_folds
  print "%17s %12s %14s %14s" %("IR", "SVM", "(abs)", "(rel)")
  print FMT % ("MRR", avg_mrr_se, std_mrr_se, avg_mrr_svm, std_mrr_svm, avg_abs_impr_mrr, std_abs_impr_mrr, avg_rel_impr_mrr, std_rel_impr_mrr)
  print FMT % ("MAP", avg_map_se*100, std_map_se, avg_map_svm*100, std_map_svm, avg_abs_impr_map, std_abs_impr_map, avg_rel_impr_map, std_rel_impr_map)
  print FMT % ("P@1", avg_rec1_se, std_rec1_se, avg_rec1_svm, std_rec1_svm, avg_abs_impr_rec1, std_abs_impr_rec1, avg_rel_impr_rec1, std_rel_impr_rec1)
  print "Table view"
  print "	MRR	MAP	P@1"
  print u"IR	%5.2f\u00B1%4.2f	%5.2f\u00B1%4.2f	 %5.2f\u00B1%4.2f" % (avg_mrr_se, std_mrr_se, avg_map_se*100, std_map_se*100, avg_rec1_se, std_rec1_se)
  print u"SVM	%5.2f\u00B1%4.2f	%5.2f\u00B1%4.2f	 %5.2f\u00B1%4.2f" % (avg_mrr_svm, std_mrr_svm, avg_map_svm*100, std_map_svm*100, avg_rec1_svm, std_rec1_svm)

  # print "Averaged absolute improvement"
  # print "MRRof1: %6.2f%%" % abs_mrr_impr
  # print "RECof1: %6.2f%%" % abs_recall_impr
  
  # print "Averaged relative improvement"
  # print "MRRof1: %6.2f%%" % rel_mrr_impr
  # print "RECof1: %6.2f%%" % rel_recall_impr  
  

def mean_and_std(values):
  """Compute mean standard deviation"""
  size = len(values)
  mean = sum(values)/size
  s = 0.0  
  for v in values:  
    s += (v - mean)**2
  std = math.sqrt((1.0/(size-1)) * s) 
  return mean, std


def main():
  usage = "usage: %prog [options] arg1 arg2 "
  desc = """arg1: work folder
  arg2: suffix of the model/prediction
  arg3: file witht he corrections to the truth labels in the output of the baseline saearch engine"""

  parser = OptionParser(usage=usage, description=desc)
  parser.add_option("-t", "--threshold", dest="th", default=15, type=int, 
                    help="supply a value for computing Precision up to a given threshold "
                    "[default: %default]", metavar="VALUE")
  parser.add_option("-c", "--cut_at_number", dest="cutn", default=None, type=int,
                    help="Evaluate only on to 20 results returned by the search engine"
                    "[default: %default]", metavar="VALUE")
  parser.add_option("-f", "--format", dest="format", default="trec", 
                    help="format of the result file (trec, answerbag): [default: %default]", 
                    metavar="VALUE")      
  parser.add_option("-v", "--verbose", dest="verbose", default=False, action="store_true",
                    help="produce verbose output [default: %default]")      
  parser.add_option("--ignore_noanswer", dest="ignore_noanswer", default=False, action="store_true",
	                  help="ignore questions with no correct answer [default: %default]")
  (options, args) = parser.parse_args()

  #args = ["/Users/kateryna/Documents/qapipeline_current/qapipeline/data/examples/stanfordbaseline-nodetach-noprune-2014-11-19","",
  #        "/Users/kateryna/Documents/qapipeline_current/qapipeline/data/aquaint/crowdflower/job_794640.csv"]
  if len(args) == 3:
    path = args[0]
    suf = args[1]
    truthfile = args[2]
    stats_cv(path=path, format=options.format, th=options.th, suf=suf, truth_file=truthfile, ignore_noanswer=options.ignore_noanswer)
  else:
    parser.print_help()
    sys.exit(1)
  

if __name__ == '__main__':  
  main()

