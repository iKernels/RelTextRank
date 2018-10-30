from sklearn.svm import SVC
from global_constants import TRAIN, TEST, DEV


def train_and_predict(data, labels, kernel='linear', probability=True):
    # print "Probability", probability
    svc = SVC(kernel=kernel, probability=probability, verbose=False, max_iter=100000)
    svc.fit(data[TRAIN], labels[TRAIN])
    predictions = dict()
    predictions[TEST] = svc.decision_function(data[TEST]).tolist()

    if DEV in data:
        predictions[DEV] = svc.decision_function(data[DEV]).tolist()

    return predictions


def get_predictions(c, gram_matrix_dict, labels, probability=False, modes=[TRAIN,TEST,DEV]):
    '''

    :param c: which matrices from gram_matrix_dict to use
    :param gram_matrix_dict:
    :param labels:
    :param probability: do we want to use svc in the probabilistic setting
    :param modes:
    :return:
    '''
    cs = set(c)

    matrix = dict()
    for mode in modes:
        matrix[mode] = sum([v[mode] for k, v in gram_matrix_dict.items() if k in cs])

    return train_and_predict(matrix, labels, kernel='precomputed', probability=probability)