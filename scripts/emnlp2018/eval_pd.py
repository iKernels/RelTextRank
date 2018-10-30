import logging
from collections import Counter, defaultdict

import metrics as m

from global_constants import *


def evaluate_and_get_message(dataset, predictions, label="", show_test=False, skip_all_positives_and_all_negatives=True):
    dev_results = get_evaluation_results(dataset[DEV], predictions[DEV], skip_all_positives_and_all_negatives=skip_all_positives_and_all_negatives)
    test_results = get_evaluation_results(dataset[TEST], predictions[TEST], skip_all_positives_and_all_negatives=skip_all_positives_and_all_negatives)

    message = "%s\t%s\t%s" % (label, "\t".join(["%.2f" % (x) for x in list(dev_results)]),
                              "\t".join(["%.2f" % (x) for x in list(test_results)]))

    logging.info("DEV:\t%s\t%s" % (label, "\t".join(["%.2f" % (x) for x in list(dev_results)])))
    if show_test:
        logging.info("TEST:\t%s\t%s" % (label, "\t".join(["%.2f" % (x) for x in list(test_results)])))
    return message


def get_evaluation_results(df, y_pred, skip_all_positives_and_all_negatives=True):
    predictions_dict = get_ranked_predictions_dict(df, y_pred, skip_all_positives_and_all_negatives=skip_all_positives_and_all_negatives)
    logging.debug("Num of questions: %d" % (len(predictions_dict)))

    mrr_score = m.mrr(predictions_dict, 1000)
    map_score = m.map(predictions_dict) * 100
    p1_score = m.recall_of_1(predictions_dict, 1000)[0]

    return mrr_score, map_score, p1_score


def get_ranked_predictions_dict(df, pred, skip_all_positives_and_all_negatives=True):

    good = set([k for k, v in
                Counter(df[["qid", "label"]].drop_duplicates()["qid"].values.tolist()).items()
                if v > 1])


    info = [tuple(z) + (v,) for z, v in zip(df[["qid", "aid", "label"]].values.tolist(), pred)]

    info = sorted(info, key=lambda x: x[3], reverse=True)
    result = defaultdict(list)
    for qid, aid, label, _ in info:
        if skip_all_positives_and_all_negatives and qid not in good:
            continue
        result[qid].append("true" if label else "false")
    if skip_all_positives_and_all_negatives:
        logging.info("Number of questions before and after filtering the irrelevants:  %d and %d" %
                         (len(df.qid.unique()), len(result)))
    return result


