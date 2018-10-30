from __future__ import division

import os
import sys
import logging
from optparse import OptionParser
import metrics
from ev import read_res_pred_files
import math

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


def stats_cv(path=".",  format="trec", prefix="svm",  th=50, suf="",  verbose=False, ignore_noanswer=False,
             ignore_allanswer=False):
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

    #try:
    ir, svm = read_res_pred_files(res_fname, pred_fname, format, verbose, ignore_noanswer=ignore_noanswer,ignore_allanswer=ignore_allanswer)
    '''
    except:
      logging.error("Failed to process input files: %s %s", res_fname, pred_fname)
      logging.error("Check that the input file format is correct")
      sys.exit(1)
    '''
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
    print "MAP: %5.2f %5.2f %+6.2f%% %+6.2f%%" % (map_se, map_svm, abs_map_diff, rel_map_diff) 

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
  print FMT % ("MAP",  avg_map_se*100, std_map_se*100, avg_map_svm*100, std_map_svm*100, avg_abs_impr_map, std_abs_impr_map, avg_rel_impr_map, std_rel_impr_map)
  print FMT % ("P@1", avg_rec1_se, std_rec1_se, avg_rec1_svm, std_rec1_svm, avg_abs_impr_rec1, std_abs_impr_rec1, avg_rel_impr_rec1, std_rel_impr_rec1)

  print "\nMRR\tMAP\tP@1\tMRRstd\tMAPstd\tP@1std"
  print "%5.2f\t%5.2f\t%5.2f\t%4.2f\t%4.2f\t%4.2f" % (avg_mrr_svm, avg_map_svm*100, avg_rec1_svm, std_mrr_svm,
                                                   std_map_svm*100, std_rec1_svm)

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
  usage = "usage: %prog [options] arg1 [arg2]"
  desc = """arg1: file with the output of the baseline search engine (ex: svm.test.res) 
  arg2: predictions file from svm (ex: train.predictions)
  if arg2 is ommited only the search engine is evaluated"""

  parser = OptionParser(usage=usage, description=desc)
  parser.add_option("-t", "--threshold", dest="th", default=15, type=int, 
                    help="supply a value for computing Precision up to a given threshold "
                    "[default: %default]", metavar="VALUE")
  parser.add_option("-f", "--format", dest="format", default="trec", 
                    help="format of the result file (trec, answerbag): [default: %default]", 
                    metavar="VALUE")      
  parser.add_option("-v", "--verbose", dest="verbose", default=False, action="store_true",
                    help="produce verbose output [default: %default]")      
  parser.add_option("-s", "--suf", dest="suf", default=False,
                    help="")
  parser.add_option("--ignore_noanswer", dest="ignore_noanswer", default=False, action="store_true",
	                  help="ignore questions with no correct answer [default: %default]")
  parser.add_option("--ignore_allanswer", dest="ignore_allanswer", default=False, action="store_true",
	                  help="ignore questions with all correct answers [default: %default]")
  (options, args) = parser.parse_args()

  # args = ["/Users/aseveryn/PhD/projects/qapipeline-git/data/examples/dd1/cand5.ray1.cv5fold"]
  if len(args) == 1:
    path = args[0]

    stats_cv(path=path, format=options.format, th=options.th, suf=options.suf, ignore_noanswer=options.ignore_noanswer,
             ignore_allanswer=options.ignore_allanswer)
  else:
    parser.print_help()
    sys.exit(1)
  

if __name__ == '__main__':  
  main()

