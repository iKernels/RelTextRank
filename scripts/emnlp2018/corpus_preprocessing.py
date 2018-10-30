import argparse
import logging
from dataset_utils import get_pandas_summary, filter_dataset, get_labels
from global_constants import TRAIN,TEST,DEV,QUESTION,ANSWER
from gram_matrix_extractors import get_qa_matrix
from linguistic_annotation_utils import linguistic_annotations, dummy_preprocess, dummy_tokenize
from collections import defaultdict
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from gram_matrix_extractors import CrossPairSparseFeatureVectorBasedKernelGramMatrixBuilder
from global_utils import train_and_predict
from eval_pd import get_evaluation_results





def add_bow_qa_representation(dataset, feature_config=("_all", "_bow"), ngram_range=(1, 4), min_df=2):
    '''
    adds a new column with bow representation of selected data
    '''

    mode = TRAIN
    ds = dataset[mode]

    column_suffix, feature_name = feature_config

    vectorizer = TfidfVectorizer(use_idf=False, ngram_range=ngram_range, min_df=min_df,
                                 tokenizer=dummy_tokenize, preprocessor=dummy_preprocess)
    questions = ds[QUESTION + column_suffix].values.tolist()
    answers = ds[ANSWER + column_suffix].values.tolist()
    vectorizer.fit(questions + answers)

    for mode in [TRAIN, TEST, DEV]:
        ds = dataset[mode]
        questions = ds[QUESTION + column_suffix].values.tolist()
        answers = ds[ANSWER + column_suffix].values.tolist()
        ds[QUESTION + feature_name] = [x for x in vectorizer.transform(questions)]
        ds[ANSWER + feature_name] = [x for x in vectorizer.transform(answers)]

def read_corpus_to_dataframe(questions_file, answers_file, basedir, examples_train, remove_irrelevant):
    logging.info("Reading the corpus data")
    df = get_pandas_summary(questions_file, answers_file, basedir)
    logging.info("Finished reading the corpus data")
    df_train = df[df.qid.str.startswith("R")]
    df_train = df_train.groupby('qid').head(examples_train)
    df_dev = df[df.qid.str.startswith("D")]
    df_test = df[df.qid.str.startswith("T")]
    if remove_irrelevant:
        df_train = filter_dataset(df_train, drop_all_positives=False)
        df_dev = filter_dataset(df_dev)
        df_test = filter_dataset(df_test)
    dataset = dict()
    dataset[TRAIN] = df_train
    dataset[TEST] = df_test
    dataset[DEV] = df_dev
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
    for mode in [TRAIN, TEST, DEV]:
        for colname in [QUESTION, ANSWER]:
            linguistic_annotations(dataset[mode], colname, configurations)
    return configurations



def get_annotations_list_from_triples(doc, lemma=True, pos=True, include_sw=True):
    '''
    converts triples to tokens
    :param doc:
    :param lemma:
    :param pos:
    :param include_sw:
    :return:
    '''
    ann=[]
    for lemma_, tag_, is_stop in doc:
        if not include_sw and is_stop:
            continue
        comps=[]
        if lemma:
            comps.append(lemma_)
        if pos:
            comps.append(tag_)
        ann.append("_".join(comps))
    return ann

def get_bow_for_dataset(ds, configurations, source_col_suffix="_rel_triples",
                        target_col_marker="_rel"):
    '''
    generates columns with the tokens for bows
    :param ds:
    :param configurations:
    :param source_col_suffix:
    :param target_col_marker:
    :return:
    '''
    for colname in [QUESTION, ANSWER]:
        for confname in configurations:
            target_name=colname+confname+target_col_marker
            ds[target_name]=ds[colname+source_col_suffix].apply(get_annotations_list_from_triples,
                                                                args=configurations[confname] )

def add_bow_representations_universal(dataset, feature_configs_lemmas, feature_configs_pos,
                           lemma_ngram_range=[(1, 4), (2, 4), (1, 2), (1, 3), (2, 3)],
                            pos_ngram_range=[(1, 4), (2, 4), (1, 3)]):
    '''
    adds bow represntations to a dataset with the lemma and  pos n-gram ranges set with lemma_ngram_range and
    pos_ngram_range and the source columns for lemma and pos n-grams specified by feature_configs_lemmas
    and feature_configs_pos

    :param dataset:
    :param feature_configs_lemmas:
    :param feature_configs_pos:
    :param lemma_ngram_range:
    :param pos_ngram_range:
    :return:
    '''
    for ngram_range in lemma_ngram_range:
        logging.info("Processing %d,%d" % ngram_range)
        for source_column_suffix, feature_column_suffix in feature_configs_lemmas:
            ngram_suf = "_%d_%d" % (ngram_range[0], ngram_range[1])
            add_bow_qa_representation(dataset, feature_config=(source_column_suffix,
                                                               feature_column_suffix + ngram_suf),
                                      ngram_range=ngram_range, min_df=4)
    for ngram_range in pos_ngram_range:
        logging.info("Processing %d,%d" % ngram_range)
        for source_column_suffix, feature_column_suffix in feature_configs_pos:
            ngram_suf = "_%d_%d" % (ngram_range[0], ngram_range[1])
            add_bow_qa_representation(dataset, feature_config=(source_column_suffix,
                                                               feature_column_suffix + ngram_suf),

                                      ngram_range=ngram_range, min_df=4)

def add_bow_representations(configurations, dataset):
    # what kind of tokens will I use to construct my ngrams?
    feature_configs_lemmas = [(x, "_bow" + x) for x in list(configurations.keys()) if "_pos" not in x]
    print "LEMMAS for BOW features: ", feature_configs_lemmas
    feature_configs_pos = [(x, "bow" + x) for x in list(configurations.keys()) if "_pos" in x and not "_nosw" in x]
    print "POS for BOW features: ", feature_configs_pos
    # adding columns with bow represeentations of input data
    for ngram_range in [(1, 4), (2, 4), (1, 2), (1, 3), (2, 3)]:
        # for ngram_range in [(1,4)]:
        logging.info("Processing %d,%d" % ngram_range)
        for source_column_suffix, feature_column_suffix in feature_configs_lemmas:
            ngram_suf = "_%d_%d" % (ngram_range[0], ngram_range[1])
            add_bow_qa_representation(dataset, feature_config=(source_column_suffix, feature_column_suffix + ngram_suf),
                                      ngram_range=ngram_range, min_df=4)
    for ngram_range in [(1, 4), (2, 4), (1, 3)]:
        logging.info("Processing %d,%d" % ngram_range)
        for source_column_suffix, feature_column_suffix in feature_configs_pos:
            ngram_suf = "_%d_%d" % (ngram_range[0], ngram_range[1])
            add_bow_qa_representation(dataset, feature_config=(source_column_suffix, feature_column_suffix + ngram_suf),

                                      ngram_range=ngram_range, min_df=4)


def process_dataset(dataset):
    train_y = get_labels(dataset[TRAIN])
    dev_y = get_labels(dataset[DEV])
    test_y = get_labels(dataset[TEST])

    labels = dict()
    labels[TRAIN] = train_y
    labels[DEV] = dev_y
    labels[TEST] = test_y

    configurations = annotate_dataset(dataset)
    add_bow_representations(configurations, dataset)


    #======GETTING THE GRAM MATRICES, QA AND QQ-AA========
    bow_col_suffixes = [x.replace("question", "") for x in dataset[TRAIN].columns if 'bow' in x and 'question' in x]
    #get_qq_aa_matrices
    n_jobs=10
    metric=cosine_similarity
    logging.info("GET QQ/AA matrices (1 per feature)")

    bow_qq_aa_matrices = get_qq_aa_matrices(bow_col_suffixes, dataset, metric, n_jobs)


    logging.info("GET baseline QA matrix (1 matrix overall)")
    bow_qa_matrix = get_qa_matrix(bow_col_suffixes, dataset, metric, n_jobs)

    # ======TRAINING AND PREDICTING========
    resulting_matrices = defaultdict(dict)

    for mode in [TRAIN, DEV, TEST]:
        #     print mode, [k for k,v in matrices.items() if k!='combo']
        #     for k,v in bow_qq_aa_matrices.items():
        #         print k,v.keys()
        resulting_matrices['qaqa'][mode] = sum(tuple([v[mode] for k, v in bow_qq_aa_matrices.items()]))

    resulting_matrices['qa'] = bow_qa_matrix

    for mode in [TRAIN, DEV, TEST]:
        resulting_matrices['qa_qaqa'][mode] = sum((resulting_matrices['qa'][mode], resulting_matrices['qaqa'][mode]))

    for key in ['qa', 'qaqa', 'qa_qaqa']:
        logging.info(key)
        predictions = train_and_predict(resulting_matrices[key], labels, kernel='precomputed')

        dev_results = get_evaluation_results(dataset[DEV], predictions[DEV])
        test_results = get_evaluation_results(dataset[TEST], predictions[TEST])

        logging.info("DEV:\t%s\t%s" % (key, "\t".join(["%.2f" % (x) for x in list(dev_results)])))
        logging.info("TEST:\t%s\t%s" % (key, "\t".join(["%.2f" % (x) for x in list(test_results)])))

        print key, "DEV", "\t".join(["%.2f" % (x) for x in list(dev_results)])


def get_qq_aa_matrices(bow_col_suffixes,  dataset, metric=None, n_jobs=1, do_normalize=False):
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
        logging.info("Processing %s" % (source_column))
        builder = CrossPairSparseFeatureVectorBasedKernelGramMatrixBuilder(source_column, metric=metric,
                                                                       n_jobs=n_jobs, do_normalize=do_normalize)
        bow_qq_aa_matrices[source_column] = builder.get_matrix(dataset)
    return bow_qq_aa_matrices

def get_arg_parser():
    parser = argparse.ArgumentParser(description='Rewrites the data in a processable way')

    parser.add_argument('-b', '--basedir', help='basedir file', required=True)

    parser.add_argument('-q', '--questions_file', help='questions_file ', required=True)
    parser.add_argument('-a', '--answers_file', help='answers file', required=True)



    parser.add_argument('-r', '--remove_irrelevant', help='remove all positives/all negatives',
                        dest='remove_irrelevant', default=False, action='store_true')


    parser.add_argument('-et', '--examples_train', default=10, type=int,
                        help='examples_train')
    return parser

if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    parser = get_arg_parser()
    args = parser.parse_args()

    basedir = args.basedir
    questions_file = args.questions_file
    answers_file = args.answers_file


    remove_irrelevant = args.remove_irrelevant
    examples_train = args.examples_train

    dataset = read_corpus_to_dataframe(questions_file, answers_file, basedir, examples_train, remove_irrelevant)
    process_dataset(dataset)


