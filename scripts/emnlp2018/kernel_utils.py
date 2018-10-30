import logging
import numpy as np

from eval_pd import get_evaluation_results
from itertools import combinations
from global_constants import  *
from global_utils import  *

def run_config(c, dataset, resulting_matrices, labels,show_test=True, probability=False,
               skip_all_positives_and_all_negatives=True):
    cs = set(c)
    label = " + ".join(cs)

    matrix = dict()
    for mode in ALL_MODES:
        matrix[mode] = sum([v[mode] for k, v in resulting_matrices.items() if k in cs])

    predictions = train_and_predict(matrix, labels, kernel='precomputed', probability=probability)
    dev_results = get_evaluation_results(dataset[DEV], predictions[DEV],
                                         skip_all_positives_and_all_negatives=skip_all_positives_and_all_negatives)
    test_results = get_evaluation_results(dataset[TEST], predictions[TEST],
                                          skip_all_positives_and_all_negatives=skip_all_positives_and_all_negatives)

    message = "%s\t%s\t%s" % (label, "\t".join(["%.2f" % (x) for x in list(dev_results)]),
                              "\t".join(["%.2f" % (x) for x in list(test_results)]))

    logging.info("DEV:\t%s\t%s" % (label, "\t".join(["%.2f" % (x) for x in list(dev_results)])))
    if show_test:
        logging.info("TEST:\t%s\t%s" % (label, "\t".join(["%.2f" % (x) for x in list(test_results)])))
    return message


def run_experiments(dataset, configurations, resulting_matrices, labels, predefined=False, show_test=True, min_length=1,
                    max_length=-1, probability=False, skip_all_positives_and_all_negatives=True):
    messages = []
    if predefined:
        for c in configurations:
            message = run_config(c, dataset, resulting_matrices, labels, show_test=show_test, probability=probability,
                                 skip_all_positives_and_all_negatives= skip_all_positives_and_all_negatives)
            messages.append(message)
    else:
        start = min_length - 1
        end = max_length if max_length > 0 else len(configurations)
        for l in range(start, end):
            for c in combinations(configurations, l + 1):
                message = run_config(c, dataset, resulting_matrices, labels, show_test=show_test,
                                     probability=probability,
                                     skip_all_positives_and_all_negatives= skip_all_positives_and_all_negatives)
                messages.append(message)
    return messages

def convert_svm_light_vector_to_np_array(x,array_size,zero_vector):
    aa = zero_vector
#     print type(x),x
    if not isinstance(x,str):
        logging.debug("Substituting %s with zero vector" % (str(x)))
        return aa
#     if isinstance(x,np.ndarray):
#         return x
    aa = np.zeros(shape=(1,array_size))
    if len(x.strip())==0:
        return aa
    for tuplesa in x.strip().split(" "):
        if not ":" in tuplesa:
            logging.error("Wrong format: %s" % (str(x)))

        indexa,valuea=tuplesa.strip().split(":")
        if int(indexa)-1>=array_size:
            print x

        aa[0,int(indexa)-1]=np.float64(valuea)
#         print aa[0]
    return aa