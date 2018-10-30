import gzip
import cPickle as pickle
from global_constants import *
import numpy as np
import logging

def unpickle_object_from_gz_file(filename):
    with gzip.open(filename,"r") as fl:
        logging.info("Started unpickling from %s"%(filename))
        obj =pickle.load(fl)
        logging.info("Unpickled from %s"%(filename))
    return obj

def pickle_object_to_gz_file(filename, obj):
    with gzip.open(filename,"w") as fl:
        logging.info("Started pickling to %s"%(filename))
        obj =pickle.dump(obj,fl,pickle.HIGHEST_PROTOCOL)
        logging.info("Pickled to %s"%(filename))
    return obj

def load_numpy_matrices(filepattern):
    m = dict()
    for mode in ALL_MODES:
        with open(filepattern % (mode),"r") as fl:
            m[mode] = np.load(fl)
    return m

def save_numpy_matrices(filepattern):
    m = dict()
    for mode in ALL_MODES:
        with open(filepattern % (mode),"w") as fl:
            m[mode] = np.load(fl)
    return m

def unpickle_object_from(filename):
    obj=None
    with open(filename,"r") as fl:
        logging.info("Started unpickling from %s"%(filename))
        obj = pickle.load(fl)
        logging.info("Unpickled from %s"%(filename))
    return obj