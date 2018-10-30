#
# This module converts the similarities files extracted by the
# scripts/emnlp2018/run_kelp_gram_matrix_extraction.sh script


import argparse
import json
from config_file_constants import *
from global_constants import *
from io_utils import unpickle_object_from

import pandas as pd
import os
from scipy.sparse import coo_matrix
import logging
import numpy as np


def read_gram_matrix(feature_file, id_to_line_train, id_to_line_test, decompress_from_triangular=False):
    logging.info("Preparing to read from %s" % (feature_file))
    df = pd.read_csv(feature_file, index_col=False, sep="\t", header=None,
                     names=["qid1", "aid1", "qid2", "aid2", "sst_sim"])


    for col_name in ["qid1", "qid2", "aid1", "aid2"]:
        if df[col_name].dtype != "object":
            logging.debug("Converting %s column to string, it was mistakenly cast as %s" % (col_name, df[col_name].dtype))
            df[col_name] = df[col_name].apply(str)


    #converting qids and aids to str

    logging.info("Finished reading, started merging stage 1")

    df_merged = df.merge(id_to_line_test, left_on=["qid2", "aid2"], right_on=["qid", "aid"],
                         how="inner").drop(["qid", "aid", "qid2", "aid2"], axis=1).rename(index=str,
                                                                                          columns={"row": "col"})

    logging.info("Started merging stage 2")
    df_merged = df_merged.merge(id_to_line_train, left_on=["qid1", "aid1"], right_on=["qid", "aid"],
                                how="inner").drop(["qid", "aid", "qid1", "aid1"], axis=1)
    print df_merged[:20]
    logging.info("Started converting the results into the coo_matrix")

    coo = coo_matrix((df_merged.sst_sim.values, (df_merged.row.values, df_merged.col.values))).tocsr()
    if decompress_from_triangular:
        coo = coo.maximum(coo.T)
    logging.info("DONE")
    return coo.todense()


def get_arg_parser():
    parser = argparse.ArgumentParser(description='Rewrites the matrices extracted by Kelp as pickled dictionary of'
                                                 'numpy gram matrices')

    parser.add_argument('-c', '--config_file', help='config_file', required=True)

    parser.add_argument('-f', '--features_source', help='folder where the similarities extracted by the '
                                                        'scripts/emnlp2018/run_kelp_gram_matrix_extraction.sh'
                                                        'are kept', required=True)

    parser.add_argument('-s', '--suffix', help='suffix to be added to all the representation files,'
                                              , required=True)

    parser.add_argument('-o', '--outputfolder', help='folder to which to output all the data [full path, basedir'
                                                     'not will be added to this'
                        , required=True)

    parser.add_argument('-qq', '--qq_file_name_template', default='qq-%s_%s.gz',
                        help='Cross-pair question-to-question similarities;'
                             'first %s stands for the mode (train|test|dev), second - for suffix specified by the'
                             '-s parameter', required=False)

    parser.add_argument('-aa', '--aa_file_name_template', default='aa-%s_%s.gz',
                        help='Cross-pair answer-to-answer similarities;'
                             'first %s stands for the mode (train|test|dev), second - for suffix specified by the'
                             '-s parameter', required=False)

    parser.add_argument('-qa', '--qa_file_name_template', default='qa-%s_%s.gz',
                        help='Intra-pair question-to-answer similarities;'
                             'first %s stands for the mode (train|test|dev), second - for suffix specified by the'
                             '-s parameter', required=False)

    return parser

if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    parser = get_arg_parser()
    args = parser.parse_args()

    kernel = args.suffix

    config = json.load(open(args.config_file))

    dataset = unpickle_object_from(config[DATASET_PICKLED_PATH])

    features_source = os.path.join(config[BASEDIR], args.features_source)

    mode = TRAIN
    qq_file = os.path.join(features_source, args.qq_file_name_template % (mode, args.suffix))
    aa_file = os.path.join(features_source, args.aa_file_name_template % (mode, args.suffix))
    qa_file = os.path.join(features_source, args.qa_file_name_template % (mode, args.suffix))

    sst_qq_mat = dict()
    sst_aa_mat = dict()

    id_to_line_train = pd.DataFrame([(k, v[0], v[1]) for (k, v)
                                     in enumerate((tuple(x) for x in dataset[TRAIN][["qid", "aid"]].values.tolist()))],
                                    columns=["row", "qid", "aid"])
    for mode in ALL_MODES:
        id_to_line_test = id_to_line_train if mode == TRAIN else pd.DataFrame([(k, v[0], v[1]) for (k, v)
                                                                               in enumerate(
                (tuple(x) for x in dataset[mode][["qid", "aid"]].values.tolist()))],
                                                                              columns=["row", "qid", "aid"])
        qq_feature_file = os.path.join(features_source, args.qq_file_name_template % (mode, args.suffix))
        aa_feature_file = os.path.join(features_source, args.aa_file_name_template % (mode, args.suffix))

        # the train gram matrix is symmetric, so in the original file we store similarities for (t1,t2), but not for
        # (t2, t1), as these are the same
        decompress = mode == TRAIN

        sst_qq_mat[mode] = read_gram_matrix(qq_feature_file, id_to_line_train, id_to_line_test,
                                            decompress_from_triangular=decompress)

        sst_aa_mat[mode] = read_gram_matrix(aa_feature_file, id_to_line_train, id_to_line_test,
                                            decompress_from_triangular=decompress)
    for mode in [DEV, TEST]:
        sst_qq_mat[mode] = sst_qq_mat[mode].T
        sst_aa_mat[mode] = sst_aa_mat[mode].T

    sst_mult = dict()
    for mode in ALL_MODES:
        sst_mult[mode] = np.multiply(sst_qq_mat[mode], sst_aa_mat[mode])

    sst_sum = dict()
    for mode in ALL_MODES:
        sst_sum[mode] = np.add(sst_qq_mat[mode], sst_aa_mat[mode])

    matrices_to_serialize = [("%s_sum" % (kernel), sst_sum),
                             ("%s_sst" %(kernel), sst_mult)]

    for matrix_name, matrix in matrices_to_serialize:
        for mode in ALL_MODES:
            output_file_name = os.path.join(args.outputfolder, "%s_%s.npy" % (matrix_name, mode))
            np.save(output_file_name, matrix[mode])
            logging.info("Saved to %s" % (output_file_name))






