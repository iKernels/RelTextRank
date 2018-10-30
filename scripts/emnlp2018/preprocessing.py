import argparse
from constants import *
import os
from util import read_properties
import logging
from dataset_utils import get_pandas_summary, get_labels, filter_dataset
import cPickle as pickle
from linguistic_annotation_utils import linguistic_annotations
from corpus_preprocessing import add_bow_representations

def get_arg_parser():
    parser = argparse.ArgumentParser(description='Converts train/test files the RelTextRank internal data format '
                                                 'into pickled DataFrames, which also contain annotation performed by Spacy '
                                                 'and bag-of-words representations of the input data')

    parser.add_argument('-o', '--output_file', help='the name of .pkl file where to write the processed corpus', required=True)

    parser.add_argument('-c', '--configuration_file', help='configuration file with paths to train/dev/test data ', required=True)

    parser.add_argument('-r', '--remove_irrelevant', help='remove all positives/all negatives in training/test (all-positives '
                                                          'will be kept in the training, however)',
                        dest='remove_irrelevant', default=False, action='store_true')

    parser.add_argument('-t', '--max_threads_number', default=10,
                        help='maximum number of processes to be run by spacy', required=False)

    parser.add_argument('-et', '--examples_train', default=10, type=int,
                        help='number of training candidate answers to be used per training question')
    return parser

def read_corpus_to_dataframe(questions_file, answers_file, examples_limit=None, remove_irrelevant=False,
                             keep_all_positives=False):
    logging.info("Reading the data from (%s,%s)" % (questions_file, answers_file))
    df = get_pandas_summary(questions_file, answers_file, "")
    logging.info("Finished reading the corpus data")
    if examples_limit is not None:
        df = df.groupby('qid').head(examples_limit)
    if remove_irrelevant:
        df = filter_dataset(df, drop_all_positives=not keep_all_positives)
    return df

def get_data_locations(data_properties):
    data_locations = dict()
    for mode in [TRAIN, DEV, TEST]:
        key_q = "%s_%s" % (mode, Q_SUF)
        key_a = "%s_%s" % (mode, A_SUF)
        if key_q in data_properties:
            data_locations[mode] = (data_properties[key_q], data_properties[key_a])
    return data_locations


def read_dataset_from_locations(data_locations, remove_irrelevant, train_examples_limit):
    dataset = dict()
    for mode in data_locations:

        questions_file, answers_file = data_locations[mode]
        examples_limit = train_examples_limit if mode == TRAIN else None
        keep_all_positives = True if mode == TRAIN else False
        # print "examples_limit=", examples_limit
        # print "mode=", mode

        dataset[mode] = read_corpus_to_dataframe(questions_file, answers_file, examples_limit=examples_limit,
                                                 remove_irrelevant=remove_irrelevant,
                                                 keep_all_positives=keep_all_positives)
        # print "shape=", dataset[mode].shape
    return dataset

def annotate_dataset(dataset):
    configurations = dict()

    # booleans below refer to lemma, pos and include_sw when generating the additional ds data representation columns
    configurations["_all"] = (True, True, True,)
    configurations["_all_nosw"] = (True, True, False,)
    configurations["_lemma"] = (True, False, True,)
    configurations["_lemma_nosw"] = (True, False, False,)
    configurations["_pos"] = (False, True, True,)

    # doing the linguistic annotations in correspondance with above configurations
    for mode in dataset:
        for colname in [QUESTION, ANSWER]:
            linguistic_annotations(dataset[mode], colname, configurations)
    return configurations

if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    parser = get_arg_parser()
    args = parser.parse_args()
    os.environ['MKL_NUM_THREADS'] = str(args.max_threads_number)

    colnames=[QUESTION, ANSWER]

    data_properties = read_properties(args.configuration_file)

    data_locations = get_data_locations(data_properties)

    dataset = read_dataset_from_locations(data_locations, args.remove_irrelevant, args.examples_train)

    labels = dict()
    for mode in dataset:
        labels[mode] = get_labels(dataset[mode])

    logging.info("Annotating the dataset with spacy")
    configurations = annotate_dataset(dataset)
    logging.info("Finished annotating, started pickling")

    outfile = args.output_file
    add_bow_representations(configurations, dataset)
    logging.info("Extracted bow, started pickling")

    with open(outfile, "wb") as fl:
        pickle.dump(dataset, fl, protocol=pickle.HIGHEST_PROTOCOL)
        logging.info("Writing to %s" % (os.path.join(outfile)))

    logging.info("DONE!")