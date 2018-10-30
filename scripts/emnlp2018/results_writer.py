import pandas as pd
from global_constants import *
import json
import os
import logging
import numpy as np
import csv
class ResultsWriter:
    def __init__(self, corpus_settings_path, dataset_frame, output_folder):
        '''
        :param corpus_settings_path:
        :param dataset_frame:
        :param output_folder:
        '''
        config = json.load(open(corpus_settings_path))

        data = dict()
        self.official_ids = dict()
        for af in config["answers_files"]:
            self.official_ids[af["mode"]]=pd.read_csv(os.path.join(config['basedir'], af['file']),
                               sep=' ', encoding="utf-8", quoting=csv.QUOTE_NONE,
                               usecols=[0, 1, 4], names=["qid", "aid", "label"], header=None)

        fields_to_be_string = ["qid", "aid"]
        for f in fields_to_be_string:
            for mode in self.official_ids:
                if self.official_ids[mode][f].dtype != "object":
                    self.official_ids[mode][f] = self.official_ids[mode][f].apply(str)

        self.output_folder = output_folder
        if not os.path.exists(self.output_folder):
            os.makedirs(self.output_folder)


        self.internal_ids = dict()
        for mode in [DEV, TEST]:
            self.internal_ids[mode] = dataset_frame[mode][['qid', 'aid']].copy()

        self.codes = [("(","*"),(")","**"), (";","_AND_")]

    def encode_name(self, string):
        code = string
        for symbol, replacement in self.codes:
            code = code.replace(symbol, replacement)
        return code

    def write_results(self, system_name, results_map):

        output_file = os.path.join(self.output_folder, "%s-%s.txt" % (self.encode_name(system_name), '%s'))
        for mode in results_map.keys():
            self.internal_ids[mode]["pred"] = results_map[mode]
            # print "Prediction results quantity", mode, len(results_map[mode])
            # print "Keys quantity", mode, len(self.official_ids[mode])
            rez = self.official_ids[mode].merge(self.internal_ids[mode], how="left", on=["qid", "aid"])
            rez.to_csv(output_file % (mode), na_rep="0", columns=["pred"], header=False, index=False)
            logging.info("Writing to %s" % (output_file % (mode)))

    def write_cv_results(self, system_name, results_map):
        cvfold_directory = os.path.join(self.output_folder,"%s-cv" % (self.encode_name(system_name)))
        if not os.path.exists(cvfold_directory):
            os.makedirs(cvfold_directory)
        logging.info("Writing to %s" % (cvfold_directory))
        i = 0
        for predictions, qid_aid_label_list in results_map:
            cvfold_directory_i = os.path.join(cvfold_directory,"fold%d" %(i))

            if not os.path.exists(cvfold_directory_i):
                os.makedirs(cvfold_directory_i)

            output_file = os.path.join(cvfold_directory_i, "svm.pred")
            np.savetxt(output_file, predictions)

            output_relevancy_file = os.path.join(cvfold_directory_i, "svm.relevancy")
            with open(output_relevancy_file, 'w') as f:
                for qid, aid, label in qid_aid_label_list:
                    f.write("%s %s 0 %.4f %s _\n" % (qid, aid, float(label), str(label).lower()))
            i+=1