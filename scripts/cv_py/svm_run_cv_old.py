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
from ev_cv import stats_cv

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.DEBUG)

SVM_HOME = "tools/SVM-Light-1.5-rer"
SVM_LEARN = os.path.join(SVM_HOME, "svm_learn")
SVM_TEST = os.path.join(SVM_HOME, "svm_classify")
SVM_LEARN_PARAMS = '-t 5 -F 3 -C + -W R -V R -m 400'
#SVM_LEARN_PARAMS = '-t 5 -F 3 -m 2000'
  

global counter
counter = 0

def run_svm(currentFold):
  train = os.path.join(currentFold, "svm.train")
  test = os.path.join(currentFold, "svm.test")
  model = os.path.join(currentFold, "svm.model")
  pred = os.path.join(currentFold, "svm.pred")
  train_log = os.path.join(currentFold, "train.log")
  test_log = os.path.join(currentFold, "test.log")

  cmd = [SVM_LEARN] + SVM_LEARN_PARAMS.split() + [train] + [model]
  print " ".join(cmd)

  # Train
  proc_train = subprocess.Popen(cmd, stdout=subprocess.PIPE, close_fds=True)
  (stdout, stderr) = proc_train.communicate()
  with open(train_log, 'w') as log:
    log.write(stdout)

  # Classify
  cmd = [SVM_TEST, test, model, pred]
  print " ".join(cmd)
  proc_test = subprocess.Popen(cmd, stdout=subprocess.PIPE, close_fds=True) 
  (stdout, stderr) = proc_test.communicate()
  
  with open(test_log, 'w') as log:
    log.write(stdout)



def cb(r):
  global counter
  print counter
  counter +=1


def eval_cv(path="."):
  path = sys.argv[1]  
  # pool = multiprocessing.Pool()
  procs = []

  folds = []
  for fold in sorted(os.listdir(path)):
    currentFold = os.path.join(path, fold)
    if not os.path.isdir(currentFold):
      continue
    if not fold.startswith("fold"):
      logging.warn("Directories containing CV folds should start with 'fold'")
      continue
    folds.append(currentFold)

    # pool.apply_async(run_svm, args=(currentFold,), callback=cb)
    p = multiprocessing.Process(target=run_svm, args=(currentFold,))
    procs.append(p)

  for p in procs:
    p.start()

  for p in procs:
    p.join()

  # pool.close()
  # pool.join()

  stats_cv(path=path, format="trec", th=15)


if __name__ == '__main__':
  path = sys.argv[1]
  eval_cv(path)

    
    
