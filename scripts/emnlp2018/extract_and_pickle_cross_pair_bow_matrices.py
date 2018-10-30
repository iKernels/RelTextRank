import argparse
import logging
from global_constants import TRAIN,TEST,DEV
from collections import defaultdict
from sklearn.metrics.pairwise import cosine_similarity
from gram_matrix_extractors import CrossPairSparseFeatureVectorBasedKernelGramMatrixBuilder
from io_utils import unpickle_object_from
import numpy as np

def get_qq_aa_matrix(dataset):
    #======GETTING THE GRAM MATRICES, QA AND QQ-AA========
    bow_col_suffixes = [x.replace("question", "") for x in dataset[TRAIN].columns if 'bow' in x and 'question' in x]
    #get_qq_aa_matrices
    n_jobs=10
    metric=cosine_similarity
    logging.info("GET QQ/AA matrices (1 per feature)")

    bow_qq_aa_matrices = get_qq_aa_matrices(bow_col_suffixes, dataset, metric, n_jobs)
    qq_aa_combo_matrix = dict()
    for mode in [TRAIN, DEV, TEST]:
        qq_aa_combo_matrix[mode] = sum(tuple([v[mode] for k, v in bow_qq_aa_matrices.items()]))
    logging.info("Summed all the matrices")
    return qq_aa_combo_matrix

def get_qq_aa_matrices(bow_col_suffixes,  dataset, metric, n_jobs):
    '''
    returns multiple q-q/a-a matrices
    :param bow_col_suffixes:
    :param dataset:
    :param metric:
    :param n_jobs:
    :return:
    '''
    bow_qq_aa_matrices = defaultdict(dict)
    for source_column in bow_col_suffixes:
        logging.info("Processing %s" % source_column)
        builder = CrossPairSparseFeatureVectorBasedKernelGramMatrixBuilder(source_column,
                                                            metric, n_jobs=n_jobs)
        bow_qq_aa_matrices[source_column] = builder.get_matrix(dataset)
    return bow_qq_aa_matrices


def get_arg_parser():
    parser = argparse.ArgumentParser(description='Rewrites the data in a processable way')
    parser.add_argument('-i', '--input_pickled_dataset_file', help='input_pickled_dataset_file ', required=True)
    parser.add_argument('-o', '--output_qa_qa_matrix_file_pattern', help='output_qa_qa_matrix_file '
                                                                        'pattern with %s as a placeholder '
                                                                        'for the mode', required=True)

    return parser

if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    parser = get_arg_parser()
    args = parser.parse_args()

    dataset = unpickle_object_from(args.input_pickled_dataset_file)
    matrix = get_qq_aa_matrix(dataset)
    logging.info("Finished the qq-aa matrices extraction")

    for mode in [TRAIN, TEST, DEV]:
        output_file_name = args.output_qa_qa_matrix_file_pattern % (mode)
        with open(output_file_name, "w") as fl:
            np.save(fl, matrix[mode])
            logging.info("Saved %s matrix to %s" % (mode, output_file_name))




