from collections import defaultdict
import itertools
import numpy as np
import random
from global_constants import *
from global_utils import get_predictions
import metrics as m
import pandas as pd
import csv
import logging

class CVManager:
    def __init__(self, df, labels, num_splits=10, seed=512, qid_file=None):
        random.seed(seed)


        if qid_file is not None:
            tmp_df = pd.read_csv(qid_file,
                                sep=' ', encoding="utf-8", quoting=csv.QUOTE_NONE,
                                usecols=[0], names=["qid"], header=0)
            if tmp_df.qid.dtype != object:
                tmp_df.qid = tmp_df.qid.apply(str)
            qids_list = tmp_df.qid.unique().tolist()

        else:
            qids_list = list(set(df.qid.values.tolist()))



        random.shuffle(qids_list)


        self.num_splits = num_splits
        self.folds = [set(x.tolist()) for x in np.array_split(np.array(qids_list), num_splits)]

        self.train_splits = []
        self.test_splits = []
        self.train_labels = []
        self.test_labels = []

        self.test_qid_aids = []
        for f in self.folds:
            qids = list(enumerate(df.qid.values.tolist()))
            split_train = [x[0] for x in qids if x[1] not in f]
            split_test = [x[0] for x in qids if x[1] in f]
            print "split_train", len(split_train)
            print "split_test", len(split_test)
            self.train_splits.append(split_train)
            self.test_splits.append(split_test)

            self.train_labels.append(labels[split_train])
            self.test_labels.append(labels[split_test])

            self.test_qid_aids.append(df[["qid", "aid", "label"]].values[split_test].tolist())

    def save_splits(self, output_file_name):
        with open(output_file_name, 'w') as w:
            for fold_id, fold_contents in list(enumerate(self.folds)):
                line = "%d\t%s\n" % (fold_id, " ".join(fold_contents))
                w.write(line)


    def get_num_splits(self):
        return self.num_splits

    def get_labels(self, fold):
        pass

    def get_train_lines(self, fold):
        return self.train_splits[fold]

    def get_test_lines(self, fold):
        return self.test_splits[fold]

    def get_train_labels(self, fold):
        return self.train_labels[fold]

    def get_test_labels(self, fold):
        return self.test_labels[fold]

    def get_test_qid_aids(self, fold):
        return self.test_qid_aids[fold]


def run_cv_evaluation(config, gram_matrix_dict, cv_manager):
    '''
    :param config: list of matrices to sum
    :param gram_matrix_dict:
    :param cv_manager:
    :return:
    '''
    logging.info("Running %s" % ("+".join(config)))
    per_fold_predictions = []
    for i in range(0, cv_manager.get_num_splits()):
        split_train = cv_manager.get_train_lines(i)
        split_test = cv_manager.get_test_lines(i)

        mat_cv_labels = dict()
        mat_cv_labels[TRAIN] = cv_manager.get_train_labels(i)
        mat_cv_labels[TEST] = cv_manager.get_test_labels(i)

        mat_cv_dict = defaultdict(dict)
        for matrix_id in config:
            mat_cv_dict[matrix_id][TRAIN] = gram_matrix_dict[matrix_id][TRAIN][split_train][:, split_train]
            mat_cv_dict[matrix_id][TEST] = gram_matrix_dict[matrix_id][TRAIN][split_test][:, split_train]

        fold_predictions = get_predictions(config, mat_cv_dict, mat_cv_labels,  probability=False,
                                           modes=[TRAIN, TEST])
        qid_aid_label_list = cv_manager.get_test_qid_aids(i)
        per_fold_predictions.append((fold_predictions[TEST], qid_aid_label_list))
    return per_fold_predictions



def evaluate_macro_performance(predictions, verbose=False):
    total = float(len(predictions))
    macro_map = 0.0
    macro_mrr = 0.0
    macro_p1 = 0.0
    i = 0
    for fold_predictions, qid_aid_label_list in predictions:
        mmap, mrr, p1 = get_cv_evaluation_results(qid_aid_label_list, fold_predictions)
        macro_map = macro_map + mmap
        macro_mrr = macro_mrr + mrr
        macro_p1 = macro_p1 + p1
        if verbose:
            logging.info("Fold %d: %5.4f\t%5.4f\t%5.4f" % (i, mmap, mrr, p1))
    return macro_mrr / total, macro_map/total, macro_p1/total

def evaluate_micro_performance(predictions):
    global_qid_aid_label_list = list(itertools.chain(*[x[1] for x in predictions]))
    global_fold_predictions = list(itertools.chain(*[x[0] for x in predictions]))
    return get_cv_evaluation_results(global_qid_aid_label_list, global_fold_predictions)

def get_cv_ranked_predictions_dict(qid_aid_label_list, pred):
    info = [tuple(z) + (v,) for z, v in zip(qid_aid_label_list, pred)]
    info = sorted(info, key=lambda x: x[3], reverse=True)
    result = defaultdict(list)
    for qid, aid, label, _ in info:
        result[qid].append("true" if label else "false")
    return result


def get_cv_evaluation_results(qid_aid_label_list, y_pred):
    predictions_dict = get_cv_ranked_predictions_dict(qid_aid_label_list, y_pred)
    logging.debug("Num of questions: %d" % (len(predictions_dict)))

    mrr_score = m.mrr(predictions_dict, 1000)
    map_score = m.map(predictions_dict) * 100
    p1_score = m.recall_of_1(predictions_dict, 1000)[0]

    return mrr_score, map_score, p1_score


def evaluate_macro_performance_and_std(predictions, verbose=False):
    total = float(len(predictions))
    maps = []
    mrrs = []
    p1s = []
    i = 0
    for fold_predictions, qid_aid_label_list in predictions:
        mrr, mmap, p1 = get_cv_evaluation_results(qid_aid_label_list, fold_predictions)
        maps.append(mmap)
        mrrs.append(mrr)
        p1s.append(p1)
        i = i + 1
        if verbose:
            logging.info("Fold %d: %5.4f\t%5.4f\t%5.4f" % (i, mmap, mrr, p1))


    return np.mean(mrrs), np.std(mrrs), np.mean(maps), np.std(maps), np.mean(p1s), np.std(p1s)