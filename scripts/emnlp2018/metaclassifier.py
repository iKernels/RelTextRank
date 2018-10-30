import pandas as pd
from itertools import chain
from global_constants import *
import numpy as np

def get_metaclassifier_training_data_and_labels(cv_predictions, feature_names):
    '''
    getting meta-classifier training data from the cv fold predictions
    '''
    rez_frame = None
    column_feats = []

    for pred_name in feature_names:
        pred_val = cv_predictions[pred_name]
        rez = [tuple(x[1]) + (x[0],) for x in chain(*[zip(x[0], x[1]) for x in pred_val])]
        tmp_frame = pd.DataFrame(rez, columns=["qid", "aid", "label", "score_" + pred_name])
        column_feats.append("score_" + pred_name)
        if rez_frame is None:
            rez_frame = tmp_frame
        else:
            rez_frame = pd.merge(rez_frame, tmp_frame, how="inner")

    Y = rez_frame.label.values.astype('int')
    X = rez_frame[column_feats].values
    return X, Y


def get_metaclassifier_prediction_data(standalone_predictions, feature_names, modes=[DEV, TEST]):
    X = dict()
    Y = dict()

    for mode in modes:
        cols = []
        for pred_name in feature_names:
            cols.append(np.array(standalone_predictions[pred_name][mode]).reshape(-1, 1))

        X[mode] = np.hstack(cols)
    return X