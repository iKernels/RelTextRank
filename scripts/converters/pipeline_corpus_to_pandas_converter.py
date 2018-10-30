from coling2018main import read_corpus_to_dataframe
from global_logging_utils import log_info_message
import cPickle as pickle
import json
import os
from emnlp2018.global_constants import TRAIN, TEST, DEV, QUESTION, ANSWER
from emnlp2018.linguistic_annotation_utils import linguistic_annotations


class CorpusToPandasConverter():
    def __init__(self, corpus_settings_path, corpus_name):
        # reading the dataset
        config = json.load(open(corpus_settings_path))
        basedir = config['basedir']
        remove_irrelevant = config['remove_irrelevant']
        questions_file = os.path.join(basedir, config['questions_file'])
        answers_file = os.path.join(basedir, config['answers_file'])
        examples_train = config['examples_train']
        self.corpus_id = corpus_name

        self.dataset = read_corpus_to_dataframe(questions_file, answers_file,
                                                basedir, examples_train, remove_irrelevant)

        # storing the pickled path
        self.dataset_pickled_path = os.path.join(basedir, config['dataset_pickled_path'])

    def get_default_configurations(self):
        configurations = dict()
        # booleans below refer to lemma, pos and include_sw when
        # generating the additional ds data representation columns
        configurations["_all"] = (True, True, True,)
        configurations["_all_nosw"] = (True, True, False,)
        configurations["_lemma"] = (True, False, True,)
        configurations["_lemma_nosw"] = (True, False, False,)
        configurations["_pos"] = (False, True, True,)
        log_info_message("Using defaul configurations")
        log_info_message(configurations)
        return configurations

    def preprocess_dataset_with_spacy(self, configurations=None, n_threads=10):
        log_info_message("Annotating the dataset with spacy")
        if configurations is None:
            configurations = self.get_default_configurations()
        # doing the linguistic annotations in correspondance with above configurations
        for mode in [TRAIN, TEST, DEV]:
            for colname in [QUESTION, ANSWER]:
                linguistic_annotations(self.dataset[mode], colname, configurations, n_threads=n_threads)
        return configurations
        log_info_message("Finishing annotating")

    def pickle_the_dataset(self):
        log_info_message("Started pickling")

        with open(self.dataset_pickled_path, "w") as fl:
            pickle.dump(self.dataset, fl, protocol=pickle.HIGHEST_PROTOCOL)
            log_info_message("Writing to %s" % (self.dataset_pickled_path))
        log_info_message("Finished pickling, started bow rep extraction")