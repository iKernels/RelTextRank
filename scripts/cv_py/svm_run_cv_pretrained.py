"""
Runs svm_learn & svm_classify for each fold in the CV experiment.

Takes in the path to the folder with CV folds. Each folder should start with
'fold' and contain the following files: svm.train, svm.test, svm.relevancy
"""

import os
import logging
import subprocess
import multiprocessing
import sys
import optparse

from ev_cv_suf import stats_cv

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.DEBUG)

SVM_HOME = "tools/SVM-Light-1.5-rer"
SVM_LEARN = os.path.join(SVM_HOME, "svm_learn")
SVM_TEST = os.path.join(SVM_HOME, "svm_classify")


global counter
counter = 0

def launch(cmd, logFile, verbose=False):
  if verbose:
    proc_train = subprocess.Popen(cmd, stdout=sys.stdout, close_fds=True)
    proc_train.communicate()
  else:
    proc_train = subprocess.Popen(cmd, stdout=subprocess.PIPE, close_fds=True)
    (stdout, stderr) = proc_train.communicate()
    with open(logFile, 'w') as log:
      log.write(stdout)


def run_svm(currentFold, modelname, testfolder,suf,  verbose=False):
  train = os.path.join(currentFold, "svm.train")
  test = os.path.join(testfolder, "svm.test")
  model = os.path.join(currentFold, modelname)
  #testfoldername=testfolder.split("/")[-1]
  #suf=modelname+"_on_test_"+testfoldername
  pred = os.path.join(testfolder, "svm"+suf+".pred")
  train_log = os.path.join(currentFold, "train.log")
  test_log = os.path.join(currentFold, "test.log")

  # Train
  #cmd = [SVM_LEARN] + params.split() + [train] + [model]
  #print " ".join(cmd)
  #launch(cmd, train_log, verbose)

  # Classify
  cmd = [SVM_TEST, test, model, pred]
  print " ".join(cmd)
  launch(cmd, test_log, verbose)


def cb(r):
  global counter
  print counter
  counter +=1


def eval_cv(path,  modelname, testfolder, ncpus=None, verbose=False):
  procs = []
  folds = []
  testfoldername=testfolder.split("/")[-1]


  for fold in sorted(os.listdir(path)):
    currentFold = os.path.join(path, fold)
    suf="_"+modelname+"_ontrain_"+path.split("/")[-1]+"_"+fold
    print("EV: sh scripts/quickrun/eval_judge40.sh {} {}".format(testfoldername,suf))
    if not os.path.isdir(currentFold):
      continue
    if not fold.startswith("fold"):
      logging.warn("Directories containing CV folds should start with 'fold'")
      continue
    folds.append(currentFold)

    p = multiprocessing.Process(target=run_svm, args=(currentFold, modelname, testfolder,suf, verbose,))
    procs.append(p)

  if ncpus:
    for i in xrange(0, len(procs), ncpus):
      jobs = procs[i:i+ncpus]
      for p in jobs:
        p.start()
      for p in jobs:
        p.join()
  else:
    for p in procs:
      p.start()
      p.join()

  #stats_cv(path=path, format="trec", th=15, suf=suf)


if __name__ == '__main__':
  parser = optparse.OptionParser()
  parser.add_option('-n', '--ncpus', help='number of cpus', type=int,
                    dest='ncpus', default=None)
  parser.add_option('-v', '--verbose', help='produce verbose output [%default]', 
                    dest='verbose', default=False, action='store_true')
  parser.add_option('-m', '--modelname', help='modelname',
                    dest='modelname', default="")
  parser.add_option('-t', '--testfolder', help='testfolder',
                    dest='testfolder', default="")
  (opts, args) = parser.parse_args()
  print args
  path = args[0]
  print path
  eval_cv(path, opts.modelname, opts.testfolder, opts.ncpus, opts.verbose)

    
    
