from abc import ABCMeta, abstractmethod
from global_constants import  *
from collections import defaultdict
from sklearn.utils import gen_even_slices
from sklearn.externals.joblib import Parallel
from sklearn.externals.joblib import delayed
from functools import partial
from nltk import everygrams
from custom_similarity_metrics import *
import scipy
from sklearn.metrics.pairwise import pairwise_kernels
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.preprocessing import normalize
import logging



def sum_feature_qq_aa_matrices(matrices_dict, keys_to_skip=None):
    sum_dict = dict()
    for mode in ALL_MODES:
        sum_dict[mode] = sum([v[mode] for k,v in matrices_dict.items() if keys_to_skip is None
                              or k not in keys_to_skip])
    return sum_dict

def get_ngrams(x, min_size, max_size):
    n_grams=set()
    for n_gram in everygrams(x,min_size,max_size):
        n_grams.add("_".join(n_gram))
    return n_grams

class GramMatrixBuilder:
    __metaclass__ = ABCMeta

    def __init__(self, source_rep, metric=None, n_jobs=1, do_normalize=False):
        self.metric = metric
        self.source_rep = source_rep
        self.n_jobs = n_jobs
        self.do_normalize = do_normalize

    @abstractmethod
    def get_data_representation(self, dataset):
        pass

    def get_matrix(self, dataset):
        input_data = self.get_data_representation(dataset)
        matrices = dict()
        for mode in [TRAIN, TEST, DEV]:
            X_q = np.array(input_data[mode][QUESTION])
            Y_q = np.array(input_data[TRAIN][QUESTION])

            X_a = np.array(input_data[mode][ANSWER])
            Y_a = np.array(input_data[TRAIN][ANSWER])

            logging.info("Getting q matrix")
            gram_q = get_custom_gram_matrix(self.metric, X_q, Y=Y_q, n_jobs=self.n_jobs)
            logging.info("Getting a matrix")
            gram_a = get_custom_gram_matrix(self.metric, X_a, Y=Y_a, n_jobs=self.n_jobs)
            matrices[mode] = np.multiply(gram_q, gram_a)
        return matrices

    # def save_matrix(self, output_file):
    #     np.sa


class SetBasedKernelGramMatrixBuilder(GramMatrixBuilder):
    def __init__(self, source_rep, metric, ngram_range=(1, 1), n_jobs=1):
        super(SetBasedKernelGramMatrixBuilder, self).__init__(source_rep, metric, n_jobs)
        self.ngram_range = ngram_range

    def get_data_representation(self, dataset):
        input_data = defaultdict(dict)
        logging.info("Convering data")
        for mode in [TRAIN, TEST, DEV]:
            for column in [QUESTION, ANSWER]:
                ds_column_name = column + self.source_rep
                input_data[mode][column] = dataset[mode][ds_column_name].apply(get_ngrams, args=self.ngram_range)
        return input_data


class SequenceBasedKernelGramMatrixBuilder(GramMatrixBuilder):
    def __init__(self, source_rep, metric, n_jobs=1):
        super(SequenceBasedKernelGramMatrixBuilder, self).__init__(source_rep, metric, n_jobs)

    def get_data_representation(self, dataset):
        input_data = defaultdict(dict)
        logging.info("Convering data")
        for mode in [TRAIN, TEST, DEV]:
            for column in [QUESTION, ANSWER]:
                ds_column_name = column + self.source_rep
                input_data[mode][column] = dataset[mode][ds_column_name]
        return input_data


def get_custom_gram_matrix(metric, X, Y=None, n_jobs=1):
    if Y is None:
        Y = X
    func = partial(custom_cdist, metric=metric)
    fd = delayed(func)
    ret = Parallel(n_jobs=n_jobs, verbose=1)(fd(X, Y[s]) for s in gen_even_slices(Y.shape[0], n_jobs))
    rez = np.hstack(ret)
    return rez



class CrossPairSparseFeatureVectorBasedKernelGramMatrixBuilder(GramMatrixBuilder):
    """
    computes cross-pair features on fly as linear/cosine kernel on sparse feature vector representations
    of Q and A

    indices of representation columns for Q and A are passed in as a list

    former FeatureVectorBasedKernelGramMatrixBuilder
    """
    def __init__(self, source_rep, metric=None, n_jobs=1, do_normalize=False):
        super(CrossPairSparseFeatureVectorBasedKernelGramMatrixBuilder, self).__init__(source_rep, metric=metric,
                                                                                       n_jobs=n_jobs,
                                                                        do_normalize=do_normalize)

    def get_matrix(self, dataset):
        input_data = self.get_data_representation(dataset)
        matrices = dict()
        for mode in [TRAIN, TEST, DEV]:
            X_q = input_data[mode][QUESTION]
            Y_q = input_data[TRAIN][QUESTION]

            X_a = input_data[mode][ANSWER]
            Y_a = input_data[TRAIN][ANSWER]

            gram_q = X_q.dot(Y_q.T)
            gram_a = X_a.dot(Y_a.T)

            matrices[mode] = gram_q.multiply(gram_a).todense()
        return matrices

    def get_data_representation(self, dataset):
        input_data = defaultdict(dict)
        logging.info("Extracting data")
        for mode in [TRAIN, TEST, DEV]:
            for column in [QUESTION, ANSWER]:
                ds_column_name = column + self.source_rep
                input_data[mode][column] = scipy.sparse.vstack(dataset[mode][ds_column_name].values.tolist())
        return input_data


class IntraPairSparseFeatureVectorBasedKernelGramMatrixBuilder(GramMatrixBuilder):
    """
    computes intra-pair features on fly as linear/cosine kernel on sparse feature vector representations
    of Q and A

    indices of representation columns for Q and A are passed in as a list

    former BaselineQAFeatureVectorBasedKernelGramMatrixBuilder
    """
    def __init__(self, source_rep_list, n_jobs=1, do_normalize=False, metric=None):
        super(IntraPairSparseFeatureVectorBasedKernelGramMatrixBuilder, self).__init__(source_rep_list, n_jobs=n_jobs,
                                                                                  do_normalize=do_normalize,
                                                                                       metric=metric)

    def get_matrix(self, dataset):
        input_data = self.get_data_representation(dataset)
        matrices = dict()
        for mode in [TRAIN, TEST, DEV]:
            matrices[mode] = np.dot(input_data[mode], input_data[TRAIN].T)
        return matrices

    def get_data_representation(self, dataset):
        input_data = defaultdict(dict)
        logging.info("Extracting data")

        for mode in [TRAIN, TEST, DEV]:
            features = []
            for source_rep in self.source_rep:
                logging.info(source_rep)
                ds_column_name_q = QUESTION + source_rep
                ds_column_name_a = ANSWER + source_rep
                mat_q = scipy.sparse.vstack(dataset[mode][ds_column_name_q].values.tolist())
                mat_a = scipy.sparse.vstack(dataset[mode][ds_column_name_a].values.tolist())
                features.append(mat_q.multiply(mat_a).sum(axis=1))

            input_data[mode] = np.hstack(features)
            logging.debug("Feature matrix for %s mode dimensions: %d,%d" % (mode, input_data[mode].shape[0],
                                                                               input_data[mode].shape[1]))
        return input_data


class CrossPairDenseFeatureVectorBasedKernelGramMatrixBuilder(CrossPairSparseFeatureVectorBasedKernelGramMatrixBuilder):
    """
    former DenseFeatureVectorBasedKernelGramMatrixBuilder

    computes cross-pair gram matrix with I, J element being np.dot(V_qIqJ,V_aIaJ)

    """

    def __init__(self, source_rep, metric=None, n_jobs=1,do_normalize=False):
        """

        :param source_rep: input question and answer columns should be named as question_<source_rep> and answer_<source_rep>, respectively
        :param do_normalize: normalize input feature vectors
        """
        super(CrossPairDenseFeatureVectorBasedKernelGramMatrixBuilder, self).__init__(source_rep, metric=metric,
                                                                                      n_jobs=n_jobs,
                                                                                      do_normalize=do_normalize)


    def get_matrix(self, dataset):
        input_data = self.get_data_representation(dataset)
        matrices = dict()
        for mode in [TRAIN, TEST, DEV]:
            X_q = input_data[mode][QUESTION]
            Y_q = input_data[TRAIN][QUESTION]

            X_a = input_data[mode][ANSWER]
            Y_a = input_data[TRAIN][ANSWER]

            if self.do_normalize:
                X_q = normalize(X_q)
                Y_q = normalize(Y_q)
                X_a = normalize(X_a)
                Y_a = normalize(Y_a)

            logging.debug("Getting q matrix")
            gram_q = np.dot(X_q, Y_q.T)
            logging.debug("Getting a matrix")
            gram_a = np.dot(X_a, Y_a.T)
            matrices[mode] = np.multiply(gram_q, gram_a)
        return matrices

    def get_data_representation(self, dataset):
        input_data = defaultdict(dict)
        logging.info("Extracting data")
        for mode in [TRAIN, TEST, DEV]:
            for column in [QUESTION, ANSWER]:
                ds_column_name = column + self.source_rep
                input_data[mode][column] = np.vstack(dataset[mode][ds_column_name].values.tolist())
        return input_data


class IntraPairLinearDenseQAFeatureVectorBasedKernelGramMatrixBuilder(IntraPairSparseFeatureVectorBasedKernelGramMatrixBuilder):
    """
    computes features on fly as linear (if d_normalize is false) or cosine (otherwise) kernel
    between two dense vector representations stored in the columns

    its cross-pair counterpart is CrossPairDenseFeatureVectorBasedKernelGramMatrixBuilder

    former BaselineDenseQAFeatureVectorBasedKernelGramMatrixBuilder
    """

    def __init__(self, source_rep_list, n_jobs=1, do_normalize=False, metric=None):
        """

        :param source_rep: input question and answer columns should be named as question_<source_rep> and answer_<source_rep>, respectively
        :param do_normalize: normalize input feature vectors

        """
        super(IntraPairLinearDenseQAFeatureVectorBasedKernelGramMatrixBuilder, self).__init__(source_rep_list,
                                                                                              metric=metric,
                                                                                              n_jobs=n_jobs,
                                                                                              do_normalize=do_normalize)


    def get_matrix(self, dataset):
        input_data = self.get_data_representation(dataset)
        matrices = dict()
        for mode in [TRAIN, TEST, DEV]:
            matrices[mode] = np.dot(input_data[mode], input_data[TRAIN].T)

        return matrices

    def get_data_representation(self, dataset):
        input_data = defaultdict(dict)
        logging.info("Extracting data")

        for mode in [TRAIN, TEST, DEV]:
            features = []
            for source_rep in self.source_rep:
                logging.info(source_rep)
                ds_column_name_q = QUESTION + source_rep
                ds_column_name_a = ANSWER + source_rep

                mat_q = np.vstack(dataset[mode][ds_column_name_q].values.tolist())
                mat_a = np.vstack(dataset[mode][ds_column_name_a].values.tolist())

                if self.do_normalize:
                    mat_q = normalize(mat_q)
                    mat_a = normalize(mat_a)

                features.append(np.sum(np.multiply(mat_q, mat_a), axis=1).reshape(mat_a.shape[0], 1))

            input_data[mode] = np.hstack(features)
            logging.debug("Feature matrix for %s mode dimensions: %s" % (mode, input_data[mode].shape))
        return input_data


class PrecomputedIntraPairKernelGramMatrixBuilder(IntraPairLinearDenseQAFeatureVectorBasedKernelGramMatrixBuilder):
    def __init__(self, source_rep_list,  do_normalize=False, n_jobs=1, normalize_kernel=False, metric='linear'):
        super(PrecomputedIntraPairKernelGramMatrixBuilder, self).__init__(source_rep_list, metric=metric,
                                                                          do_normalize=do_normalize,
                                                                          n_jobs=n_jobs)

        self.normalize_kernel = normalize_kernel

    def get_matrix(self, dataset):
        input_data = self.get_data_representation(dataset)
        matrices = dict()
        train_gram = pairwise_kernels(input_data[TRAIN],
                                      input_data[TRAIN],
                                      metric=self.metric)

        train_diag = np.sqrt(train_gram.diagonal()).reshape(-1, 1)
        for mode in [TRAIN, TEST, DEV]:
            if mode == TRAIN:
                # print input_data[mode].shape
                matrices[mode] = train_gram
            else:
                matrices[mode] = pairwise_kernels(input_data[mode],
                                                  input_data[TRAIN], metric=self.metric)

            if self.normalize_kernel:
                sq1 = np.sqrt(pairwise_kernels(input_data[mode],
                                               input_data[mode], metric=self.metric)
                              .diagonal()).reshape(-1, 1)

                norm_matrix = 1.0 / np.dot(sq1, train_diag.T)
                matrices[mode] = np.multiply(matrices[mode], norm_matrix)

        return matrices

    def get_data_representation(self, dataset):
        input_data = defaultdict(dict)
        logging.info("Extracting data")

        for mode in [TRAIN, TEST, DEV]:
            # print dataset[mode][self.source_rep][:10].values.tolist()
            input_data[mode] = np.vstack(dataset[mode][self.source_rep].values.tolist())
            if self.do_normalize:
                input_data[mode] = normalize(input_data[mode])
            logging.info("Feature matrix dimension [%s]: %d,%d" % (mode, input_data[mode].shape[0],
                                                                       input_data[mode].shape[1]))
        return input_data

def get_qa_dense_matrix(bow_col_suffixes, dataset,  do_normalize=False):

    logging.info("COMPUTING COSINE_BOW_BASED QA FEATURE MATRICES")

    builder = IntraPairLinearDenseQAFeatureVectorBasedKernelGramMatrixBuilder(bow_col_suffixes,
                                                                      do_normalize=do_normalize)
    bow_qa_matrix = builder.get_matrix(dataset)
    return bow_qa_matrix

def get_crossp_dense_matrix(bow_col_suffixes, dataset, metric = None, n_jobs=1,do_normalize=False):

    logging.info("COMPUTING COSINE_BOW_BASED QA FEATURE MATRICES")


    builder = CrossPairDenseFeatureVectorBasedKernelGramMatrixBuilder(bow_col_suffixes,
                                                            do_normalize = do_normalize,
                                                                      metric=metric,
                                                                      n_jobs=n_jobs)
    bow_qa_matrix = builder.get_matrix(dataset)
    return bow_qa_matrix

def get_precomputed_feature_qa_matrix(dataset,feature_column, metric=None,
                                      do_normalize=False, n_jobs=1, normalize_kernel=False):
    builder = PrecomputedIntraPairKernelGramMatrixBuilder(feature_column, metric=metric,
                                                          do_normalize=do_normalize,
                                                         normalize_kernel = normalize_kernel,
                                                          n_jobs=n_jobs)
    v_qa_matrix = builder.get_matrix(dataset)
    return v_qa_matrix



def compute_default_predefined_coling_gram_matrices(gram_matrix_dict, dataset):
    settings_file_pattern = "corpus_settings/%s.settings"

    # computing "strong" features, i.e. those involving syntax, semantics and domain-specific info
    STRONG_LINEAR = "strong_linear"
    STRONG_COSINE = "strong_cosine"
    STRONG_POLY = "strong_poly"
    STRONG_POLY_NORM = "strong_poly_norm"
    INTRA_EMBEDDING = "intra_embedding"
    CROSS_EMBEDDING = "cross_embedding"
    INTRA_BOW = "intra_bow"

    if 'qcri_features' in dataset[TRAIN].columns:
        gram_matrix_dict[STRONG_LINEAR] = get_precomputed_feature_qa_matrix(dataset, 'qcri_features',
                                                                            metric='linear')
        gram_matrix_dict[STRONG_COSINE] = get_precomputed_feature_qa_matrix(dataset, 'qcri_features',
                                                                            metric='cosine')
        gram_matrix_dict[STRONG_POLY] = get_precomputed_feature_qa_matrix(dataset, 'qcri_features',
                                                                            metric='poly')
        gram_matrix_dict[STRONG_POLY_NORM] = get_precomputed_feature_qa_matrix(dataset, 'qcri_features',
                                                                          metric='poly', do_normalize=True)
    else:
        gram_matrix_dict[STRONG_COSINE] = get_precomputed_feature_qa_matrix(dataset, 'strong_features',
                                                                            metric='cosine', do_normalize=False)
        gram_matrix_dict[STRONG_LINEAR] = get_precomputed_feature_qa_matrix(dataset, 'strong_features',
                                                                            metric='linear', do_normalize=False)
        gram_matrix_dict[STRONG_POLY] = get_precomputed_feature_qa_matrix(dataset, 'strong_features',
                                                                          metric='poly', do_normalize=False)
        gram_matrix_dict[STRONG_POLY_NORM] = get_precomputed_feature_qa_matrix(dataset, 'strong_features',
                                                                          metric='poly', do_normalize=True)

    embedding_matrix = dict()

    model_names = ["aqw2v", "gl", "gw2v"]
    suffix = ["_%s" % (model_name) for model_name in model_names]

    # INTRA-PAIR COSINE
    norm_embedding_matrix = dict()
    logging.info("getting %s embedding QA matrix" % (suffix))
    gram_matrix_dict[INTRA_EMBEDDING] = get_qa_dense_matrix(suffix, dataset, do_normalize=True)

    # =====CROSS-PAIR=====#
    model_names = ["aqw2v", "gl", "gw2v"]
    # CROSS-PAIR NORMALIZED
    norm_cross_embedding_matrix = defaultdict(dict)
    logging.info("getting %s embedding QA matrix, normalize the inputs" % (suffix))
    for model_name in model_names:
        logging.info("getting %s embedding QA matrix, normalize the inputs" % (model_name))
        suffix = "_%s" % (model_name)
        norm_cross_embedding_matrix[model_name] = get_crossp_dense_matrix(suffix, dataset, do_normalize=True)

    for mode in ALL_MODES:
        gram_matrix_dict[CROSS_EMBEDDING][mode] = sum([v[mode] for
                                                       k, v in norm_cross_embedding_matrix.items() if not k == 'combo'])

    # ===BOW-BASELINE-MATRICES===#
    # computing the 'bow' based gram matrix with intra-pair similarities,
    # using exactly the same features as for cross-pair similrity previously
    bow_col_suffixes = [x.replace("question", "") for x in dataset[TRAIN].columns if 'bow' in x and 'question' in x]
    n_jobs = 10
    metric = cosine_similarity
    logging.info("GET baseline QA matrix (1 matrix overall)")
    gram_matrix_dict[INTRA_BOW] = get_qa_matrix(bow_col_suffixes, dataset, metric, n_jobs)


def get_qa_matrix(bow_col_suffixes, dataset, metric=None, n_jobs=1, do_normalize=False):

    logging.info("COMPUTING COSINE_BOW_BASED QA FEATURE MATRICES")
    bow_qa_matrix = defaultdict(dict)

    builder = IntraPairSparseFeatureVectorBasedKernelGramMatrixBuilder(bow_col_suffixes, metric=metric,
                                                                       n_jobs=n_jobs, do_normalize=do_normalize)
    bow_qa_matrix = builder.get_matrix(dataset)
    return bow_qa_matrix