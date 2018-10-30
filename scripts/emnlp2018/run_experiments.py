import argparse

from corpus_readers import PandasBasedCorpus
import json
from cv_utils import CVManager, run_cv_evaluation
from global_constants import *
import os
from collections import defaultdict
from gram_matrix_extractors import compute_default_predefined_coling_gram_matrices
from corpus_readers import unpickle_precomputed_gram_matrices
import numpy as np
from global_utils import get_predictions
from eval_pd import evaluate_and_get_message
from cv_utils import evaluate_macro_performance_and_std
from sklearn.linear_model import LogisticRegression
from metaclassifier import get_metaclassifier_training_data_and_labels, get_metaclassifier_prediction_data
from results_writer import ResultsWriter
import logging

def get_arg_parser():
    parser = argparse.ArgumentParser(description='Gets all the numbers for the emnlp paper')

    parser.add_argument('-c', '--corpus_name', help='corpus_name', required=True)

    parser.add_argument('-s', '--corpus_settings_filename', help='json file with the corpus settings ', required=True)
    parser.add_argument('-m', '--precomputed_matrices_settings_filename', help='json file with the paths'
                                                                               'to the precomputed gram matrices',
                        required=True)
    parser.add_argument('-o', '--output_folder', help='folder to which to output the predictions data', required=True)

    parser.add_argument('-r', '--remove_irrelevant', help='remove all positives/all negatives (NOTE: MUST BE THE SAME '
                                                          'SETTING '
                                                          'THAT YOU USED TO CREATE THE PRECOMPUTED GRAM MATRICES)',
                        dest='remove_irrelevant', default=False, action='store_true')

    parser.add_argument('-k', '--kernel_settings_file', help='file with all the kernel settings')


    return parser


if __name__ == '__main__':
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    parser = get_arg_parser()
    args = parser.parse_args()

    # running experiments
    logging.info("Reading experiment settings from %s" %(args.kernel_settings_file))
    experiments_config = json.load(open(args.kernel_settings_file))


    corpus = PandasBasedCorpus(corpus_settings_path=args.corpus_settings_filename,
                               corpus_name=args.corpus_name)
    dataset = corpus.dataset


    labels = corpus.labels

    #initializing the results writer
    rwriter = ResultsWriter(args.corpus_settings_filename, corpus.dataset, args.output_folder)

    #Reading the corpus configuration file
    config = json.load(open(args.corpus_settings_filename))

    #Computing predefined matrices which are fast to compute
    basedir = corpus.basedir
    matrix_folder = os.path.join(basedir, "data/gram-matrices/%s" % (args.corpus_name))
    gram_matrix_dict = defaultdict(dict)
    compute_default_predefined_coling_gram_matrices(gram_matrix_dict, corpus.dataset)

    #reading the precomputed matrices
    precomputed_matrices_file = os.path.join(corpus.basedir, args.precomputed_matrices_settings_filename)
    unpickle_precomputed_gram_matrices(precomputed_matrices_file, gram_matrix_dict, corpus.basedir)

    logging.info("total set of matrices available:")
    for key in gram_matrix_dict:
        logging.info(key)
    logging.info("")



    # RUNNING STANDALONE SYSTEMS
    standalone_systems = [(k["id"], k["kernels"]) for k in experiments_config["standalone_experiments"]]
    standalone_systems_dict = dict(standalone_systems)
    standalone_predictions = dict([(k, get_predictions(v, gram_matrix_dict, labels)) for k, v in standalone_systems])
    messages = []
    for key in standalone_predictions:
        message = evaluate_and_get_message(corpus.dataset, standalone_predictions[key],
                                       label=key, show_test=True,
                                       skip_all_positives_and_all_negatives=args.remove_irrelevant)
        rwriter.write_results(key,standalone_predictions[key])

    logging.info("Results obtained by the standalone systems: ")
    for m in messages:
        logging.info(m)


    # SUMMING OUTPUTS OF SELECTED STANDALONE SYSTEMS
    combo_rez = defaultdict(dict)
    messages = []
    for experiment in experiments_config["standalone_experiments_to_sum"]:
        standalone_system_names = set(experiment["kernels"])

        relevant_standalone_predictions = dict([(k, standalone_predictions[k]) for k in standalone_system_names])

        experiment_id = experiment["id"]
        for mode in [DEV, TEST]:
            combo_rez[experiment_id][mode] = sum([np.array(v[mode]) for k, v in relevant_standalone_predictions.items()]).tolist()

        message = evaluate_and_get_message(corpus.dataset, combo_rez[experiment_id],
                                           label=experiment_id, show_test=True,
                                           skip_all_positives_and_all_negatives=args.remove_irrelevant)
        rwriter.write_results(experiment_id, combo_rez[experiment_id])
        messages.append(message)

    logging.info("Results obtained by the simple ensemble systems which simply sum outputs of the basic systems")
    for m in messages:
        logging.info(m)


    #RUNNING CROSS-VALIDATION

    train_qids_file = [e['file'] for e in config['answers_files'] if e["mode"] == TRAIN][0]
    cv_manager = CVManager(dataset[TRAIN], labels[TRAIN],
                           qid_file=os.path.join(config['basedir'], train_qids_file))
    run_all_systems_in_cv = len(experiments_config["standalone_systems_to_run_cv_upon"])==1 \
                             and experiments_config["standalone_systems_to_run_cv_upon"][0]=="all"


    cv_configs = standalone_systems_dict.keys() if run_all_systems_in_cv else experiments_config["standalone_systems_to_run_cv_upon"]
    cv_predictions = dict()
    for config_name in cv_configs:
        cv_predictions[config_name] = run_cv_evaluation(standalone_systems_dict[config_name],
                                                        gram_matrix_dict,
                                                        cv_manager)
        rwriter.write_cv_results(config_name, cv_predictions[config_name])

    logging.info("Cross-validation results")
    print "SYSTEM\tMRR\tMAP\tP@1"
    for label, pred in cv_predictions.items():
        macrop = evaluate_macro_performance_and_std(pred)
        message = u"%s\t%5.2f \u00B1%5.2f\t%5.2f \u00B1%5.2f\t%5.2f \u00B1%5.2f" % (tuple([label]) + tuple(macrop))
        print message

    #RUNNING ENSEMBLE SYSTEMS WITH LOGISTIC REGRESSION
    ensemble_systems = [(k["id"], k["kernels"]) for k in experiments_config["ensembles"]]
    for system_name, feature_names in ensemble_systems:
        train_X, train_Y = get_metaclassifier_training_data_and_labels(cv_predictions, feature_names)
        X = get_metaclassifier_prediction_data(standalone_predictions, feature_names)
        classifier = LogisticRegression()
        classifier.fit(train_X, train_Y)

        ensemble_scores = dict()
        for mode in [DEV, TEST]:
            ensemble_scores[mode] = classifier.predict_proba(X[mode])[:, 1]
        rwriter.write_results(system_name, ensemble_scores)
        print evaluate_and_get_message(corpus.dataset, ensemble_scores, label=system_name, show_test=True)