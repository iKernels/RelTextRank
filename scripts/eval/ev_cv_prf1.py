from __future__ import division

import os
import sys
import logging
from optparse import OptionParser
import metrics
from res_file_reader import ResFileReader
from collections import defaultdict
import math

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


def get_tp_fp_tn_fn(res_fname, pred_fname,
               ignore_noanswer=False,
               ignore_allanswer=False):
  lineReader = ResFileReader()
  ir, svm = defaultdict(list), defaultdict(list)
  tp = 0
  fp = 0
  tn = 0
  fn = 0
  for line_res, line_pred in zip(open(res_fname), open(pred_fname)):
    # Process the line from the res file.
    qid, aid, relevant_string, ir_score = lineReader.read_line(line_res)
    relevant = (relevant_string=="true")
    pred_score = float(line_pred.strip())
    if (pred_score > 0.0) and relevant:
      tp += 1
    if (pred_score > 0.0) and not relevant:
      fp += 1
    if (pred_score <= 0.0) and not relevant:
      tn += 1
    if (pred_score <= 0.0) and relevant:
      fn += 1
  return tp, fp, tn, fn


def stats_cv(path=".", format="trec", prefix="svm", th=50, suf="", verbose=False, ignore_noanswer=False,
             ignore_allanswer=False):

  num_folds = 0

  print "FOLD\tP\tR\tF1\tAccuracy"
  macro_p = []
  macro_r = []
  macro_f1 = []
  macro_acc = []
  total_tp = 0
  total_fn = 0
  total_fp = 0
  total_tn = 0
  for fold in sorted(os.listdir(path)):
    currentFold = os.path.join(path, fold)
    if not os.path.isdir(currentFold):
      continue
    if not fold.startswith("fold"):
      logging.warn("Directories containing CV folds should start with 'fold'")
      continue


    # Relevancy file
    res_fname = os.path.join(currentFold, "%s.test.relevancy" % prefix)
    if not os.path.exists(res_fname):
      logging.error("Relevancy file not found: %s", res_fname)
      sys.exit(1)

    # Predictions file
    pred_fname = os.path.join(currentFold, "%s.pred" % (prefix + suf))
    if not os.path.exists(pred_fname):
      logging.error("SVM prediction file not found: %s", pred_fname)
      sys.exit(1)

    # try:
    tp, fp, tn, fn = get_tp_fp_tn_fn(res_fname, pred_fname, ignore_noanswer=ignore_noanswer,
                                  ignore_allanswer=ignore_allanswer)
    total_tp = total_tp + tp
    total_tn = total_tn + tn
    total_fp = total_fp + fp
    total_fn = total_fn + fn

    precision = tp/(tp+fp)
    recall = tp/(tp+fn)
    f1 = 2*precision*recall/(precision+recall)
    accuracy = (tp+tn)/(tp+tn+fp+fn)
    macro_p.append(precision)
    macro_r.append(recall)
    macro_f1.append(f1)
    macro_acc.append(accuracy)
    print "fold%d\t%5.2f\t%5.2f\t%5.2f\t%5.2f" % (num_folds, precision, recall, f1, accuracy)

    num_folds += 1


  precision = total_tp/(total_tp+total_fp)
  recall = total_tp/(total_tp+total_fn)
  f1 = 2*precision*recall/(precision+recall)
  accuracy = (total_tp+total_tn)/(total_tp+total_tn+total_fp+total_fn)
  print "Micro-average\t%5.2f\t%5.2f\t%5.2f\t%5.2f" % (precision*100, recall*100, f1*100, accuracy*100)

  avg_p, std_p = mean_and_std(macro_p)
  avg_r, std_r = mean_and_std(macro_r)
  avg_f1, std_f1 = mean_and_std(macro_f1)
  avg_acc, std_acc = mean_and_std(macro_acc)

  FMT = u"Macro-average:\t %5.2f \u00B1 %4.2f\t%5.2f \u00B1 %4.2f\t %5.2f \u00B1 %4.2f\t%5.2f \u00B1 %4.2f"
  print FMT % (avg_p*100, std_p*100, avg_r*100, std_r*100, avg_f1*100, std_f1*100, avg_acc*100, std_acc*100)





def mean_and_std(values):
  """Compute mean standard deviation"""
  size = len(values)
  mean = sum(values) / size
  s = 0.0
  for v in values:
    s += (v - mean) ** 2
  std = math.sqrt((1.0 / (size - 1)) * s)
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

    stats_cv(path=path, format=options.format, th=options.th, suf=options.suf,
             ignore_noanswer=options.ignore_noanswer,
             ignore_allanswer=options.ignore_allanswer)
  else:
    parser.print_help()
    sys.exit(1)


if __name__ == '__main__':
  main()

