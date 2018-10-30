from abc import ABCMeta
import os

from io_utils import unpickle_object_from, unpickle_object_from_gz_file
from dataset_utils import get_labels
from global_constants import  *
import logging
from feature_utils import read_strong_feats
import numpy as np
import base64
import pandas as pd
import json
import logging
class Corpus:
    __metaclass__ = ABCMeta

    def __init__(self):
        pass


class PandasBasedCorpus(Corpus):
    def __init__(self, corpus_settings_path=None, corpus_name=None, corpus_pickled_path=None):
        super(PandasBasedCorpus, self).__init__()

        if corpus_pickled_path is not None:
            config = dict()
            self.dataset = unpickle_object_from(corpus_pickled_path)
        else:
            config = json.load(open(corpus_settings_path))


            #reading all the parameters available in the settings file
            self.basedir = config['basedir']
            remove_irrelevant = config['remove_irrelevant']
            DATASET_PICKLED_PATH = 'dataset_pickled_path'
            if DATASET_PICKLED_PATH in config:
                dataset_pickled_path = os.path.join(self.basedir, config['dataset_pickled_path'])
                self.dataset = unpickle_object_from(dataset_pickled_path)
            else:
                questions_file = os.path.join(self.basedir, config['questions_file'])
                answers_file = os.path.join(self.basedir, config['answers_file'])
                examples_train = config['examples_train_max']
                self.dataset = self.read_dataset_from_files(questions_file, answers_file, self.basedir,
                                                             examples_train=examples_train,
                                                             remove_irrelevant=remove_irrelevant)

            self.qa_task = config['qa_task']

            self.corpus_id = corpus_name

        #reading labels
        self.labels = dict([(k, get_labels(v)) for k, v in self.dataset.iteritems()])

        fields_to_be_string = ["qid", "aid"]
        for f in fields_to_be_string:
            for mode in self.dataset:
                if self.dataset[mode][f].dtype != "object":
                    self.dataset[mode][f] = self.dataset[mode][f].apply(str)



        #=====reading features======
        #read and add  features from the svmlight files
        SVM_LIGHT_FEATURE_FILES="svm_light_feature_files"
        if SVM_LIGHT_FEATURE_FILES in config:
            for feature_set in config[SVM_LIGHT_FEATURE_FILES]:
                self.add_features_from_svm_light_file(self.basedir, feature_set)

        CSV_BASE64_FEATURE_FILES="csv_base64_feature_files"
        # read and add csv base64-encoded features
        if CSV_BASE64_FEATURE_FILES in config:
            for feature_set in config[CSV_BASE64_FEATURE_FILES]:
                precomputed_features_file = os.path.join(self.basedir, feature_set["feature_file"])
                precomputed_features_id = feature_set["id"]
                logging.info("Adding precomputed features '%s' from %s" % (precomputed_features_id,
                                                                               precomputed_features_file))
                self.add_features_from_base64_encoded_file(precomputed_features_file,
                                                           features_column_name=precomputed_features_id)
        PANDAS_BASED_FEATURE_FILES="pandas_based_feature_files"

        if PANDAS_BASED_FEATURE_FILES in config:
            for feature_set in config[PANDAS_BASED_FEATURE_FILES]:
                pandas_features_file = os.path.join(self.basedir, feature_set["feature_file"])

                df_pandas_features = unpickle_object_from_gz_file(pandas_features_file)
                for f in fields_to_be_string:
                    for mode in df_pandas_features:
                        if df_pandas_features[mode][f].dtype != "object":
                            df_pandas_features[mode][f] = df_pandas_features[mode][f].apply(str)

                logging.info("Adding precomputed features from %s containing '%s'" % (pandas_features_file,
                                                                                          feature_set["description"]))
                for mode in ALL_MODES:

                    self.dataset[mode] = pd.merge(self.dataset[mode], df_pandas_features[mode],
                                                  on=["qid", "aid"], how='left')


    # def add_features_from_svm_light_file(self,  feature_desription):
    #     strong_features_file = os.path.join(self.basedir, feature_desription["feature_file"])
    #     strong_feature_array_size = feature_desription["array_size"]
    #     strong_features_ids_file = os.path.join(self.basedir, feature_desription["id_file"])
    #     features_id = feature_desription["id"]
    #     log_info_message("Adding pre-computed features '%s' from %s svmlight-file with %d features" % (
    #         features_id, strong_features_file, strong_feature_array_size))
    #     df_strong_feats = read_strong_feats(strong_features_ids_file, strong_features_file,
    #                                         strong_feature_array_size, feature_column_name=features_id)
    #     for mode in ALL_MODES:
    #         self.dataset[mode] = pd.merge(self.dataset[mode], df_strong_feats, on=["qid", "aid"], how='left')

    def add_features_from_svm_light_file(self, basedir, feature_desription):
        strong_feature_array_size = feature_desription["array_size"]
        features_id = feature_desription["id"]

        for feature_file in feature_desription["files"]:
            strong_features_file = os.path.join(basedir, feature_file["features"])
            strong_features_ids_file = os.path.join(basedir, feature_file["labels"])
            mode = feature_file["mode"]
            logging.info("Adding pre-computed features '%s' from %s svmlight-file with %d features" % (
                features_id, strong_features_file, strong_feature_array_size))
            df_strong_feats = read_strong_feats(strong_features_ids_file, strong_features_file,
                                                strong_feature_array_size, feature_column_name=features_id)
            self.dataset[mode] = pd.merge(self.dataset[mode], df_strong_feats, on=["qid", "aid"], how='left')



    def add_features_from_base64_encoded_file(self, precomputed_features_file, sep="\t", features_column_name="v"):
        '''
        reads features from the tab-delimited column file, with four columns. First row is column headers.
        Column 1: Unnamed; contains index;
        Column 2: qid; question id;
        Column 3: aid; answer id;
        Column 4: v; b64-encoded numpy feature vector;
        :param dataset:
        :param precomputed_features_file:
        :param sep:
        :param features_column_name:
        :return:
        '''

        df_features_full = pd.read_csv(precomputed_features_file, index_col=0, sep=sep,
                                       converters={"v": lambda x: np.frombuffer(base64.b64decode(x))})

        if features_column_name!='v':
            df_features_full = df_features_full.rename(columns={'v': features_column_name})
        for mode in [TRAIN, TEST, DEV]:
            self.dataset[mode] = pd.merge(self.dataset[mode], df_features_full, on=["qid", "aid"], how='left')

def unpickle_precomputed_gram_matrices(precomputed_matrices_file, gram_matrix_dict, basedir):
    matrix_config = None
    with open(precomputed_matrices_file, "r") as f:
        matrix_config = json.load(f)


    for matrix in matrix_config['precomputed_serialized_matrices']:
        matrix_i = dict()
        for mode in ALL_MODES:
            logging.info("Unpickling %s matrix '%s' from '%s' (%s)" %(mode, matrix['name'],
                                                                          matrix['path'][mode],
                                                                          matrix['description']))
            path = os.path.join(basedir,  matrix['path'][mode])
            matrix_i[mode]=np.load(path)
            logging.info("Unpickled '%s' %s matrix with dimensionality [%d, %d]" %(mode, matrix['name'],
                                                                                   matrix_i[mode].shape[0],
                                                                                   matrix_i[mode].shape[1]) )
        gram_matrix_dict[matrix['name']] = matrix_i