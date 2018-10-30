# http://localhost:8888/notebooks/scripts/ipython/kernel/embedding_representation_extractor.ipynb
# this scripts extracts the embedding features for a corpus serialized as a dataframe

import argparse
import logging
import json
from constants import *
from gensim.models import KeyedVectors
import numpy as np
from corpus_readers import PandasBasedCorpus
import cPickle as pickle
import pandas as pd
import gzip
import os

def pickle_object_to_gz_file(filename, obj):
    with gzip.open(filename,"w") as fl:
        logging.info("Started pickling to %s" % (filename))
        obj = pickle.dump(obj, fl, protocol=pickle.HIGHEST_PROTOCOL)
        logging.info("Done pickling to %s" % (filename))
    return obj

def add_nn_representation(pd, model_name, model,suffix=""):
    for column in [QUESTION, ANSWER]:
        logging.info("Adding representation to column %s" %(column+"_"+model_name+suffix))
        pd[column+"_"+model_name+suffix] = pd[column+"_doc"].apply(vectorize_docs,args=(model,))

def vectorize_docs(sentence, model):
    count = 0
    default_empty_vector=np.zeros(model.vector_size)
    vec_list = []

    for word in sentence:
        try:
            if not word.is_stop:
                vec_list.append(model[word.lemma_.lower()])
        except:
            count+=1
            vec_list.append(default_empty_vector)
    if len(vec_list)==0:
        return default_empty_vector
    return sum(vec_list)/np.float(len(vec_list))


def get_w2v_model(twitter_model_file,binary=False):
    return KeyedVectors.load_word2vec_format(twitter_model_file, binary=binary)

def get_arg_parser():
    parser = argparse.ArgumentParser(description='Represents the questions and answers in the input corpora as weighed sums of their embeddings')

    parser.add_argument('-c', '--corpus_pickled_file', help='the name of .pkl file where you have the processed corpus', required=True)

    parser.add_argument('-e', '--embeddings_settings_file', help='file with paths to the embeddings', required=True)


    parser.add_argument('-o', '--output_file_name', required=True,
                        help='where to output the embedding-based representations')
    return parser

if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    parser = get_arg_parser()
    args = parser.parse_args()

    config = json.load(open(args.embeddings_settings_file))
    distribution_files = [(e[ID_PROP], e[FILE_PROP], e[BINARY_PROP]) for e in config[EMBEDDINGS_LOCATIONS_PROP]]
    logging.info(distribution_files)


    corpus = PandasBasedCorpus(corpus_pickled_path=args.corpus_pickled_file)
    dataset = corpus.dataset

    for model_id, w2v_file, binary in distribution_files:
        logging.info("Reading model '%s' from '%s'" % (model_id, w2v_file))
        w2v = get_w2v_model(w2v_file, binary=binary)
        logging.info("Generating distributed representations with model '%s'" %
                                 (model_id))
        for mode in dataset.keys():
            add_nn_representation(dataset[mode], model_id, w2v)
        logging.info("Done")

    #storing only relevant columns
    embedding_columns = ["qid", "aid"]
    for embedding_name, _, _ in distribution_files:
        embedding_columns = embedding_columns + [x for x in dataset[TRAIN].columns if embedding_name in x]

    # mode = TRAIN
    embedding_dataset = dict([(mode, datasubset[embedding_columns].copy()) for mode, datasubset in dataset.iteritems()])

    pickle_object_to_gz_file(args.output_file_name+".gz", embedding_dataset)




