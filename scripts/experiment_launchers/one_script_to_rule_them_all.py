__author__ = 'kateryna'
import os
import logging
import subprocess
import multiprocessing
import sys
import argparse
import time

'''
The script generates a shell script to run
data generation, training and testing on the different corpora

Run the script without parameters to see what it does
'''


QUESTION_FILE = 0
ANSWER_FILE = 1
CAS = 2
CAND_TO_USE = 3
FEATURES_VALUES=4
FEATURES_IDS=5
PATTERNS_FILE=6

TRAIN_STR ="train"
TEST_STR = "test"
DEV_STR = "dev"
CV_STR="cv"

MAXMEM="5"
SVM_LEARN_PATH="tools/SVM-Light-1.5-rer/svm_learn"
SVM_CLASSIFY_PATH="tools/SVM-Light-1.5-rer/svm_classify"
SVM_PARALLEL_CLASSIFY_PATH="tools/uSVMp/svm_classify"


class CommandsBatch:
    """"""
    def __init__(self):
        self.train = []
        self.java = []
        self.test = []
        self.eval_command = []

    def get_train(self):
        return  self.train

    def get_test(self):
        return self.test

    def get_java(self):
        return self.java

    def add_train(self, train_script):
        self.train.append(train_script)

    def add_java(self, java_script):
        self.java.append(java_script)

    def add_test(self, test_script):
        self.test.append(test_script)

    def add_eval_command(self, eval_script):
        self.eval_command.append(eval_script)

    def get_eval_command(self):
        return self.eval_command



class CorpusInfo:
    """Wrapper for the information about the corpus"""

    def __init__(self):
        self.info = dict()

    def initialize_from_file(self, settingsfile, numericsuf=None):
        """
        Initializes the CorpusInfo from file.
        :param settingsfile: files with the settings, contains information about where to find train/test/dev data \
            * MODE_q: questions file location, if MODE_q is not set for for train/test/dev, then this modality is\
                completely ignored when generating the files
            * MODE_a: answers file location
            * MODEL_fc_feature_file: (non-mandatory) when you are reading features from the pre-extracted feature file\
                here is where you specify the path to this file for a given mode (each line of such file should contain\
                three tab-delimited columns: <question-id>, <answer-id>, <feature vector in svm light format.
            * MODE_patternsPath: (non-mandatory) if you are doing answer extraction, here you specify the path to the \
                file with the answer regular expression patterns. The file should contain two space-delimited columns, \
                namely: <question id>, <regular expression pattern>\
            * MODE_n: number of candidate answer passages to be used per question in a specific mode
            * MODE_c: folder where the CASes for questions and candidate answer passages in a specific mode will be \
                stored
            * cv: set to true if exp to be run in cross-validation
            MODE can be set to "train", "test" or "dev", for example questions file for the train mode, should be \
                specified as train_q \
            Settings file format is <parameter>=<value> in each line,e.g.:
            ``
            train_q=data/datasets/WikiQA-train.questions.txt
            train_a=data/datasets/WikiQA-train.tsv.resultset
            train_c=CASes/wikiqa/train
            ``
        :param numericsuf: (optional )if this parameter is set, then its value is appended to each value of the given \
            CorpusInfo parameters
        :return: a dictionary where keys are parameters from the input settings file, and values are the \
            respective values from this file
        """
        settings = self.read_settings_from_file( settingsfile,numericsuf)
        if CV_STR in settings:
            self.info[CV_STR]=True
        else:
            self.info[CV_STR]=False
        for s in [TRAIN_STR, TEST_STR, DEV_STR]:
            if s+"_q" in settings:
                featfile=None
                relfile=None
                patterns_file=None
                if (s+"_fc_featurefile") in settings:
                    featfile=settings[s+"_fc_featurefile"]
                if (s+"_patternsPath") in settings:
                    patterns_file=settings[s+"_patternsPath"]
                tup = (settings[s+"_q"], settings[s+"_a"], settings[s+"_c"], settings[s+"_n"],
                       featfile, relfile,patterns_file)

                self.info[s] = tup

    def get_info(self, subset):
        if not subset in self.info:
            return None
        return self.info[subset]


    def read_settings_from_file(self, settingsfile,numericsuf=None):
        """
        Reads settings from the settings file into the dictionary.
        :param settingsfile: path to the settingsfile which should have the ``key=val`` format in each line
        :param numericsuf: if not None, then this value is appended to each val in the resulting value in the \
         settings dictionary
        :return: dictionary read from the input settings file
        """
        settings = dict()
        for line in open(settingsfile,"r"):
            p = line.strip().split("=")
            if len(p)==2:
                if numericsuf==None:
                    settings[p[0]] = p[1]
                else:
                    settings[p[0]] = "%s%s" % (p[1],numericsuf)
        return settings

def get_arg_parser():
  parser = argparse.ArgumentParser(description='Run an experiment on the existing data')

  parser.add_argument('-l', '--list_of_configurations', nargs="+", help='list of experiments to run, where the possible \
                    values are the names of settings files with CorpusInfo',
                    dest='exps_to_run', default=None, required=True)

  #which system to run
  parser.add_argument('-s', '--system_class_for_noncv', help='name of the system class to use for non-cross-validation \
                    experiments',
                    dest='system_class', default="it.unitn.nlpir.system.core.ClassTextPairConversion")
  parser.add_argument('-sc', '--system_class_for_cv', help='name of the system class to use for cross-validation \
                    experiments',
                    dest='cv_system_class', default="it.unitn.nlpir.system.core.ClassCVTextPairConversion")

  #do I need to run test?
  parser.add_argument('-od', '--only_development', help='run experiment on the development set only',
                    dest='only_development', default=False, action='store_true')

  #experiment to run
  parser.add_argument('-e', '--experimental_class', help='experimental class to use',
                    dest='exp_no_ir', default=None)

  parser.add_argument('-ec', '--experiment_config', help='experimental configuration file (-expConfigPath from the '
                                                         'java pipeline)',
                    dest='experiment_config', default=None)

  parser.add_argument('-a', '--additional_java_parameters', help='additional parameters to launch the java annotation \
                                                                 pipeline without a leading dash '
                                                                 '(it will be added automatically)',
                    dest='add_java', default="")
  parser.add_argument('-atr', '--additional_train_java_parameters', help='additional parameters to launch the java '
                                                                       'annotation pipeline when generating the'
                                                                       'train (TRAIN) files'
                                                                       '(should be in double quotes '\
                                                                       'and with a space after the opening quote)',
                    dest='add_java_train', default="")
  parser.add_argument('-ate', '--additional_test_java_parameters', help='additional parameters to launch the java '
                                                                       'annotation pipeline when generating the'
                                                                       'test (TEST) files'
                                                                       '(should be in double quotes '\
                                                                       'and with a space after the opening quote)',
                    dest='add_java_test', default="")
  parser.add_argument('-ade', '--additional_dev_java_parameters', help='additional parameters to launch the java '
                                                                       'annotation pipeline when generating the'
                                                                       'development (DEV) files'
                                                                       '(should be in double quotes '\
                                                                       'and with a space after the opening quote)',
                      dest='add_java_dev', default="")

  parser.add_argument('-fff', '--feats_from_file', help='Read feature vectors from feature files specified by the '
                                                        'MODE_fc_featurefile parameter in the CorpusInfo file',
                    dest='feats_from_file', default=False)

  parser.add_argument('-ae', '--answer_extraction', help='Do answer extraction',
                    dest='answer_extraction', default=False, action='store_true')

  parser.add_argument('-cv', '--cross_validation', help='Do cross-validation on the train set for all the input '
                                                        'CorpusInfo [note: in this setting you need to manually'
                                                        'set path to your distribution of svmlight-tk in',
                    dest='cross_validation', default=False, action='store_true')


  #parameters
  parser.add_argument('-p', '--params', help='SVM-TK params [%default]. If something does not work, put the space after'
                                             'the opening quote',
                    dest='params', default="-t 5 -C V")
  parser.add_argument('-shl', '--svm_learn_path', help='location of the svm_learn script of SVMLight',
                    dest='svm_learn_path', default=SVM_LEARN_PATH)
  parser.add_argument('-shc', '--svm_classify_path', help='location of the svm_classify script of SVMLight',
                    dest='svm_classify_path', default=SVM_CLASSIFY_PATH)
  parser.add_argument('-suf', '--suffix', help='suffix to add to the model/prediction files names for this specific run',
                    dest='suf', default=None)
  parser.add_argument('-c', '--exp_code', help='experimental code name (to be added to the log file name in order not to'
                                               'confuse them)',
                    dest='exp_code', default=None, required=True)

  #output parameters,
  parser.add_argument('-o', '--output_command_folder', help='folder into which to output the resulting shell scripts',
                    dest='output_command_folder', default=None,required=True)
  parser.add_argument('-d', '--date_of_the_exp', help='date (or any id) to use when generating the name of the output experimental folder;'
                                                      'if skipped the parameter will be set to the today date',
                    dest='date_of_the_exp', default=None,required=False)
  parser.add_argument('-bo', '--base_output_command_folder', help='base folder in which the folders for the train/test'
                                                                  'dev files will be created',
                    dest='base_output_command_folder', default="data/examples",required=False)


  #other options
  parser.add_argument('-n', '--ncpus', help='number of cpus to use when running cross-validation', type=int,
                    dest='ncpus', default=None)
  parser.add_argument('--only_data_generation', help='run only data generation with the pipeline, do not do training/testing/evalaution',
                      dest='only_data_generation', default=False, action='store_true')
  parser.add_argument('-v', '--verbose', help='produce verbose output [%default]',
                    dest='verbose', default=False, action='store_true')
  parser.add_argument('-hm', '--home_folder', help='home folder of this script',
                    dest='home_folder', default="/mnt/sde/data/kateryna/software/CoreQAPipeline")
  parser.add_argument('-mem', '--max_java_mem', help='maximum amount of memory (in GB) to be allocated per java process',
                    dest='max_java_mem', default="4")

  #parallelization
  parser.add_argument('-nf', '--numeric_suffix', help='numeric suffix to be appended to all train/test/dev files'
                                                      'when running parallelization',
                    dest='numeric_suffix', default=None)


  #evaluation
  parser.add_argument('-ev', '--run_eval', help='Launch evaluation script',
                    dest='run_eval', default=True)
  parser.add_argument('-ign', '--ignore_all_positive_and_all_negative', help='When evaluating,'
                                                                             'ignore the questions which have only'
                                                                             'positive or only negative answer passage'
                                                                             'candidates',
                    dest='ignore_all_positive_and_all_negative', default=True)

  return parser



def trecv_commands(corpus_info, system_class, experiment_class, experiment_name, base_output_folder,model_suf,svm_params,corpus_name,add_java, read_feats_from_file,
                   str_time, do_answer_extraction=False, run_eval=True, skip_training_and_eval=False):
    cb = CommandsBatch()

    #str_time = time.strftime("%d-%m-%Y");
    expid=corpus_name+"_"+experiment_name+"_"+str_time;
    #launch java processing
    command_pattern = "nohup java -XX:ParallelGCThreads=4 -Xmx%sG -Xss512m %s -questionsPath %s -answersPath %s " \
                      "-outputDir %s -filePersistence %s -candidatesToKeepTrain %s  " \
                      "-candidatesToKeepTest %s -expClassName %s %s> logs/java_train_%s.log 2>&1 &"

    if read_feats_from_file:
        add_java = "%s  -featureCacheFileName %s"  % (add_java, corpus_info.get_info(TRAIN_STR)[FEATURES_VALUES])

    if do_answer_extraction:
                add_java = add_java+ " -patternsPath %s" %(corpus_info.get_info(TRAIN_STR)[PATTERNS_FILE])

    expfolder=os.path.join(base_output_folder, expid)


    if experiment_class!=None:
        java_command_train = command_pattern % (MAXMEM,system_class, corpus_info.get_info(TRAIN_STR)[QUESTION_FILE], corpus_info.get_info(TRAIN_STR)[ANSWER_FILE],
                                            expfolder,
                                            corpus_info.get_info(TRAIN_STR)[CAS], corpus_info.get_info(TRAIN_STR)[CAND_TO_USE],
                                            corpus_info.get_info(TEST_STR)[CAND_TO_USE], experiment_class, add_java, expid)
        cb.add_java(java_command_train)

    if skip_training_and_eval:
        return cb
    svm_command_train_pattern = "nohup python scripts/cv_py/svm_run_cv.py %s -n 5 -p \"%s\" -s _%s > logs/svm_%s_%s.log 2>&1 &"
    if do_answer_extraction:
        svm_command_train = svm_command_train_pattern % (expfolder +" --skip_eval", svm_params, model_suf, expid, model_suf)
    else:
        if run_eval:
            svm_command_train = svm_command_train_pattern % (expfolder, svm_params, model_suf, expid, model_suf)
        else:
            svm_command_train = svm_command_train_pattern % (expfolder +" --skip_eval",
                                                             svm_params, model_suf, expid, model_suf)


    cb.add_train(svm_command_train)

    if do_answer_extraction:
        eval_script_pattern = "python scripts/answer-extraction/eval_answer_extraction_cv.py %s %s %s"
        eval_script_command = eval_script_pattern % (expfolder, model_suf, corpus_info.get_info(TRAIN_STR)[PATTERNS_FILE])
        cb.add_eval_command(eval_script_command)
    return cb


def run_train_dev_test(corpus_info, system_class, experiment_class, experiment_name, base_output_folder,
                       model_suf,svm_params, corpus_name, add_java, read_feats_from_file,
                       str_time, add_java_train="", add_java_dev="", add_java_test="", do_answer_extraction=False,
                       only_development=False, svmlight_learn_location=SVM_LEARN_PATH,
                       svm_light_classify_location=SVM_CLASSIFY_PATH,
                       run_eval=True,
                       ignore_all_positive_and_all_negative=True,
                       skip_training_and_eval=False):

    #str_time = time.strftime("%d-%m-%Y");
    expid=corpus_name+"_"+experiment_name+"_"+str_time;
    expfolder=os.path.join(base_output_folder, expid)
    #print "expfolder:", expfolder
    expid_test=corpus_name+"_TEST_"+experiment_name+"_"+str_time
    expfolder_test=os.path.join(base_output_folder, expid_test)
    java_command_train_pattern = "nohup java -XX:ParallelGCThreads=4 -Xmx%sG -Xss512m %s -questionsPath %s -answersPath %s " \
                                 "-outputDir %s -filePersistence %s -candidatesToKeep %s -expClassName %s -mode train %s > logs/java_train_%s.log 2>&1 & "
    java_command_test_pattern = "nohup java -XX:ParallelGCThreads=4 -Xmx%sG -Xss512m %s -questionsPath %s -answersPath %s " \
                                "-outputDir %s -filePersistence %s -candidatesToKeep %s -expClassName %s -mode test %s > logs/java_%s_%s.log 2>&1 &"

    cb = CommandsBatch()

    if experiment_class!=None:
        #add_java_train = ""
        if corpus_info.get_info(TRAIN_STR) != None:
            add_java_train = add_java + " "+add_java_train
            if read_feats_from_file:
                add_java_train = add_java_train+" -featureCacheFileName %s"  % (corpus_info.get_info(TRAIN_STR)[FEATURES_VALUES])
            if do_answer_extraction:
                add_java_train = add_java_train + " -patternsPath %s" %(corpus_info.get_info(TRAIN_STR)[PATTERNS_FILE])

            java_command_train = java_command_train_pattern % (MAXMEM, system_class, corpus_info.get_info(TRAIN_STR)[QUESTION_FILE],
                                                               corpus_info.get_info(TRAIN_STR)[ANSWER_FILE],
                                                               expfolder,
                                                               corpus_info.get_info(TRAIN_STR)[CAS], corpus_info.get_info(TRAIN_STR)[CAND_TO_USE], experiment_class,add_java_train,expid)

            cb.add_java(java_command_train)
        if corpus_info.get_info(DEV_STR)!=None:
            #add_java_dev = ""
            add_java_dev = add_java + " "+add_java_dev
            if read_feats_from_file:
                add_java_dev = add_java_dev+" -featureCacheFileName %s"  % (corpus_info.get_info(DEV_STR)[FEATURES_VALUES])
            if do_answer_extraction:
                add_java_dev = add_java_dev+ " -patternsPath %s" %(corpus_info.get_info(DEV_STR)[PATTERNS_FILE])

            java_command_dev = java_command_test_pattern % (MAXMEM, system_class, corpus_info.get_info(DEV_STR)[QUESTION_FILE], corpus_info.get_info(DEV_STR)[ANSWER_FILE],
                                                            expfolder,
                                                            corpus_info.get_info(DEV_STR)[CAS], corpus_info.get_info(DEV_STR)[CAND_TO_USE], experiment_class, add_java_dev, "dev", expid)
            cb.add_java(java_command_dev)

        #str_time = time.strftime("%d-%m-%Y");

        #add_java_test = ""
        if not only_development:
            add_java_test = add_java + " "+add_java_test
            if read_feats_from_file:
                add_java_test = add_java_test+" -featureCacheFileName %s"  % (corpus_info.get_info(TEST_STR)[FEATURES_VALUES])
            if do_answer_extraction:
                add_java_test = add_java_test+ " -patternsPath %s" %(corpus_info.get_info(TEST_STR)[PATTERNS_FILE])

            java_command_test = java_command_test_pattern % (MAXMEM, system_class, corpus_info.get_info(TEST_STR)[QUESTION_FILE], corpus_info.get_info(TEST_STR)[ANSWER_FILE],
                                                             expfolder_test,
                                                             corpus_info.get_info(TEST_STR)[CAS], corpus_info.get_info(TEST_STR)[CAND_TO_USE], experiment_class, add_java_test, "test", expid)
            cb.add_java(java_command_test)

    if skip_training_and_eval:
        return cb
    '''
    =====GENERATE SVM TRAIN COMMANDS=====
    '''

    svm_command_train_pattern  = "nohup %s %s %s %s > logs/svm_train_%s_%s.log 2>&1 &"
    svm_command_test_pattern = "nohup %s %s %s %s > logs/svm_%s_%s_%s.log 2>&1 &"

    train_file = "svm.train"
    model_file_name = "svm_"+model_suf+".model"
    pred_file_name = "svm_"+model_suf+".pred"
    test_file = "svm.test"

    if corpus_info.get_info(TRAIN_STR) != None:
        #print "expfolder:", expfolder
        svm_command_train = svm_command_train_pattern % (svmlight_learn_location, svm_params,
                                                         os.path.join(expfolder,train_file),
                                                         os.path.join(expfolder, model_file_name), expid,model_suf)
        cb.add_train(svm_command_train)


    ev_pattern_py = "python scripts/eval/ev.py %s %s"
    ev_pattern_py_ignore_noans = "python scripts/eval/ev.py --ignore_noanswer --ignore_allanswer %s %s"

    '''
    =====DEV SET PREDICTION=====
    If there is a development set, generate the commands for prediction and evaluating on it
    '''

    if corpus_info.get_info(DEV_STR)!=None:
        svm_command_dev = svm_command_test_pattern % (svm_light_classify_location, os.path.join(expfolder, test_file),
                                                      os.path.join(expfolder, model_file_name),
                                                      os.path.join(expfolder, pred_file_name), "dev", expid, model_suf)
        cb.add_test(svm_command_dev)

        if run_eval:
            relfile = os.path.join(expfolder,"svm.relevancy")
            eval_command = ev_pattern_py_ignore_noans % (relfile,os.path.join(expfolder, pred_file_name)) \
                if ignore_all_positive_and_all_negative else ev_pattern_py\
                                                             % (relfile,os.path.join(expfolder, pred_file_name))
            cb.add_eval_command(eval_command)

    '''
    =====TEST SET PREDICTION=====
    If there is a test set, generate the commands for prediction and evaluating on it
    '''
    if not only_development:
        svm_command_test = svm_command_test_pattern % (svm_light_classify_location, os.path.join(expfolder_test, test_file),
                                                       os.path.join(expfolder, model_file_name),
                                                       os.path.join(expfolder_test, pred_file_name), "test", expid, model_suf)


        cb.add_test(svm_command_test)
        if run_eval:
            relfile = os.path.join(expfolder_test,"svm.relevancy")
            eval_command = ev_pattern_py_ignore_noans % (relfile,os.path.join(expfolder_test, pred_file_name)) \
                if ignore_all_positive_and_all_negative else ev_pattern_py\
                                                             % (relfile,os.path.join(expfolder_test, pred_file_name))
            cb.add_eval_command(eval_command)

    return cb




def generate_sh_file(cb, outfile):

    f = open(outfile,'w')

    for i in cb.get_java():
        f.write("echo '"+i+"'\n")
        f.write(i+"\n")
    f.write("\nwait\n\n")

    for i in cb.get_train():
        f.write("echo '"+i+"'\n")
        f.write(i+"\n")
    f.write("\nwait\n\n")

    f.write("export OMP_NUM_THREADS=4\n")
    f.write("export OMP_STACKSIZE=1048576\n")

    for i in cb.get_test():
        f.write("echo '"+i+"'\n")
        f.write(i+"\n")
    f.write("\nwait\n\n")

    for i in cb.get_eval_command():
        f.write("echo '"+i+"'\n")
        f.write(i+"\n")

    f.close()


def generate_additional_wiki_java_commands_treccv(config_ann_persistence_file, wiki_annotation_suffix, experiment_config,
                                      lod_persistence_path,
                                      lod_persistence_suffix, corpus_name):
    command_pattern = "-annotationsPersistence %s -corpusName %s_%s " \
                          "-expConfigPath %s -jlodfeatPersistencePath %s " \
                          "-jlodfeatPersistenceFeatureItemName featurecache_%s_%s";

    train_addition  = command_pattern % (config_ann_persistence_file, corpus_name, wiki_annotation_suffix,
                                         experiment_config, lod_persistence_path, corpus_name, lod_persistence_suffix)
    return train_addition


def generate_additional_wiki_java_commands_train_dev(config_ann_persistence_file, wiki_annotation_suffix, experiment_config,
                                      lod_persistence_path,
                                      lod_persistence_suffix, corpus_name):

    command_pattern = "-annotationsPersistence %s -corpusName %s_%s_%s " \
                          "-expConfigPath %s -jlodfeatPersistencePath %s " \
                          "-jlodfeatPersistenceFeatureItemName featurecache_%s_%s_%s";

    train_addition  = command_pattern % (config_ann_persistence_file, "train", corpus_name, wiki_annotation_suffix,
                                         experiment_config, lod_persistence_path, corpus_name, lod_persistence_suffix,
                                         "train")

    dev_addition  = command_pattern % (config_ann_persistence_file, "dev", corpus_name, wiki_annotation_suffix,
                                         experiment_config, lod_persistence_path, corpus_name, lod_persistence_suffix,
                                         "dev")

    test_addition  = command_pattern % (config_ann_persistence_file, "test", corpus_name, wiki_annotation_suffix,
                                         experiment_config, lod_persistence_path, corpus_name, lod_persistence_suffix,
                                         "test")

    return (train_addition, dev_addition, test_addition)

if __name__ == '__main__':
  parser = get_arg_parser()
  args = parser.parse_args()
  HOME=args.home_folder
  MAXMEM=args.max_java_mem
  cbs = []
  java_param=""


  for corpus_name in args.exps_to_run:
      ci = CorpusInfo()
      str_date = args.date_of_the_exp
      if str_date == None:
          str_date=time.strftime("%d-%m-%Y");

      settingsfile=os.path.join(".", "scripts", "experiment_launchers", "corpus_settings",corpus_name+".settings")


      ci.initialize_from_file(settingsfile, args.numeric_suffix)
      if args.numeric_suffix!=None:
          args.exp_code = "%s_fold%s" % (args.exp_code, args.numeric_suffix)

      cb = None
      str_time = time.strftime("%d-%m-%Y");



      #if we have wiki experiment
      add_java_train = args.add_java_train
      add_java_dev = args.add_java_dev
      add_java_test = args.add_java_test

      corpus_cross_validation = ci.get_info(CV_STR)

      base_exp_folder=args.base_output_command_folder


      if corpus_cross_validation or args.cross_validation==True:
        add_java =  args.add_java.strip();
        cb = trecv_commands(ci,args.cv_system_class,args.exp_ir,args.exp_code, base_exp_folder, args.suf, args.params,corpus_name,add_java.strip(),
                            args.feats_from_file, str_date,
                            do_answer_extraction=args.answer_extraction, run_eval=args.run_eval, skip_training_and_eval=args.only_data_generation)
      else:
        cb = run_train_dev_test(ci, args.system_class, args.exp_no_ir, args.exp_code, base_exp_folder, args.suf, args.params, corpus_name,
                                args.add_java.strip(),
                                args.feats_from_file, str_date,
                                add_java_train=add_java_train, add_java_dev=add_java_dev, add_java_test=add_java_test,
                                do_answer_extraction=args.answer_extraction, only_development=args.only_development,
                                svmlight_learn_location=args.svm_learn_path,
                                svm_light_classify_location=args.svm_classify_path, run_eval=args.run_eval,
                                ignore_all_positive_and_all_negative=args.ignore_all_positive_and_all_negative,
                                skip_training_and_eval=args.only_data_generation)

      run_code_name= corpus_name+"_"+args.exp_code+"_"+str_date+"_"+args.suf if args.suf is not None else corpus_name+"_"+args.exp_code+"_"+str_date

      try:
          os.makedirs(args.output_command_folder)
      except OSError:
          pass
      shellname = os.path.join(args.output_command_folder,run_code_name);


      sh_file=os.path.join(".", shellname+".sh")

      generate_sh_file(cb, sh_file)
      command_pattern = "nohup sh %s.sh > logs/%s.log 2>&1  &"

      print command_pattern % (shellname, run_code_name)




