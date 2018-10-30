#! /usr/bin/env python

import sys
import logging
from collections import defaultdict
from operator import itemgetter
from optparse import OptionParser
from res_file_reader import ResFileReader
import metrics

logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


def read_res_file(res_fname, format):
	logging.info("Processing file: %s with search engine ranks" % res_fname)
	lineReader = ResFileReader(format)

	ir = defaultdict(list)
	for line_res in open(res_fname):
		qid, aid, relevant, ir_score = lineReader.read_line(line_res)  # process the line from the res file
		ir[qid].append( (relevant, ir_score) )
	
	# Sort based on the search engine score (largest to smallest).
	for qid, resList in ir.iteritems():
		ir[qid] = [rel for rel, score in sorted(resList, key = itemgetter(1), reverse = True)]  
	return ir


def read_res_pred_files(res_fname, pred_fname, format, verbose=True, 
                        reranking_th=-1000.0,
                        ignore_noanswer=False, ignore_allanswer=False):

	lineReader = ResFileReader(format)

	ir, svm = defaultdict(list), defaultdict(list)
	for line_res, line_pred in zip(open(res_fname), open(pred_fname)):
		# Process the line from the res file.
		qid, aid, relevant, ir_score = lineReader.read_line(line_res)  
		pred_score = float(line_pred.strip())
		ir[qid].append( (relevant, ir_score, aid) )
		svm[qid].append( (relevant, pred_score, aid) )

	if verbose:
		analyze_file = open(pred_fname + ".analyzis", "w")
		info_file = open(pred_fname + ".correctpos", "w")

	# Remove questions that contain no correct answer
	if ignore_noanswer:
		for qid in ir.keys():
			candidates = ir[qid]
			if all(relevant == "false" for relevant,_,_ in candidates):
				del ir[qid]
				del svm[qid]

	if ignore_allanswer:
		for qid in ir.keys():
			candidates = ir[qid]
			if all(relevant == "true" for relevant,_,_ in candidates):
				del ir[qid]
				del svm[qid]

	for qid in ir:
		# Sort by IR score.
		ir_sorted = sorted(ir[qid], key = itemgetter(1), reverse = True)
		
		# Sort by SVM prediction score.
		svm_sorted = svm[qid]
		max_score = max([score for rel, score, aid in svm_sorted])
		if max_score >= reranking_th:
			svm_sorted = sorted(svm_sorted, key = itemgetter(1), reverse = True)

		if verbose:
			before = find_correct_answer_position(ir_sorted)
			after = find_correct_answer_position(svm_sorted)
			impr = analyze_reranking_improvement(before, after)
			analyze_file.write("%s %s\n" % (qid, str(impr)))
			info_file.write("%s %s %s\n" % (qid, str(before), str(after)))

		ir[qid] = [rel for rel, score, aid in ir_sorted]
		svm[qid] = [rel for rel, score, aid in svm_sorted]
	
	if verbose:
		analyze_file.close()
		info_file.close()

	return ir, svm

def read_res_pred_files_part(res_fname, pred_fname, format, verbose=True,
                        reranking_th=-1000.0,
                        ignore_noanswer=False, ignore_allanswer=False, fold_num=1, total_fold=5):

	lineReader = ResFileReader(format)

	ir, svm = defaultdict(list), defaultdict(list)
	qids_list = []
	for line_res, line_pred in zip(open(res_fname), open(pred_fname)):
		# Process the line from the res file.
		qid, aid, relevant, ir_score = lineReader.read_line(line_res)
		pred_score = float(line_pred.strip())
		ir[qid].append( (relevant, ir_score, aid) )
		svm[qid].append( (relevant, pred_score, aid) )
		if not qid in qids_list:
			qids_list.append(qid)

	if verbose:
		analyze_file = open(pred_fname + ".analyzis", "w")
		info_file = open(pred_fname + ".correctpos", "w")


	fold_list = qids_list[fold_num::total_fold]
	print fold_list
	for qid in ir.keys():
		if qid not in fold_list:
			del ir[qid]
			del svm[qid]

	# Remove questions that contain no correct answer
	if ignore_noanswer:
		for qid in ir.keys():
			candidates = ir[qid]
			if all(relevant == "false" for relevant,_,_ in candidates):
				del ir[qid]
				del svm[qid]

	if ignore_allanswer:
		for qid in ir.keys():
			candidates = ir[qid]
			if all(relevant == "true" for relevant,_,_ in candidates):
				del ir[qid]
				del svm[qid]

	for qid in ir:
		# Sort by IR score.
		ir_sorted = sorted(ir[qid], key = itemgetter(1), reverse = True)

		# Sort by SVM prediction score.
		svm_sorted = svm[qid]
		max_score = max([score for rel, score, aid in svm_sorted])
		if max_score >= reranking_th:
			svm_sorted = sorted(svm_sorted, key = itemgetter(1), reverse = True)

		if verbose:
			before = find_correct_answer_position(ir_sorted)
			after = find_correct_answer_position(svm_sorted)
			impr = analyze_reranking_improvement(before, after)
			analyze_file.write("%s %s\n" % (qid, str(impr)))
			info_file.write("%s %s %s\n" % (qid, str(before), str(after)))

		ir[qid] = [rel for rel, score, aid in ir_sorted]
		svm[qid] = [rel for rel, score, aid in svm_sorted]

	if verbose:
		analyze_file.close()
		info_file.close()

	return ir, svm

def find_correct_answer_position(candidates):
	out = {}
	for i, (rel, score, aid) in enumerate(candidates, 1):
		if rel == "true":
			out[aid] = i
	return out

def analyze_reranking_improvement(before, after):
	out = {}
	for key, rank_before in before.iteritems():
		rank_after = after[key]
		improvement = rank_before - rank_after
		out[key] = improvement
	return out


def eval_reranker(res_fname="svm.test.res", pred_fname="svm.train.pred", 
                  format="trec",
                  th=50, 
                  verbose=False,
                  reranking_th=0.0,
                  ignore_noanswer=False,
				  ignore_allanswer=False):
	ir, svm = read_res_pred_files(res_fname, pred_fname, format, verbose, 
		                              reranking_th=reranking_th, 
		                              ignore_noanswer=ignore_noanswer,
								  	  ignore_allanswer=ignore_allanswer)
	# evaluate IR
	prec_se = metrics.recall_of_1(ir, th)
	acc_se = metrics.accuracy(ir, th)
	acc_se1 = metrics.accuracy1(ir, th)
	acc_se2 = metrics.accuracy2(ir, th)

	# evaluate SVM
	prec_svm = metrics.recall_of_1(svm, th)
	acc_svm = metrics.accuracy(svm, th)
	acc_svm1 = metrics.accuracy1(svm, th)
	acc_svm2 = metrics.accuracy2(svm, th)

	mrr_se = metrics.mrr(ir, th)
	mrr_svm = metrics.mrr(svm, th)
	map_se = metrics.map(ir)
	map_svm = metrics.map(svm)

	avg_acc1_svm = metrics.avg_acc1(svm, th)
	avg_acc1_ir = metrics.avg_acc1(ir, th)

	print "%13s %5s" %("IR", "SVM")
	print "MRR: %5.2f %5.2f" %(mrr_se, mrr_svm)
	print "MAP: %5.4f %5.4f" %(map_se, map_svm)
	print "AvgRec: %5.2f %5.2f" %(avg_acc1_ir, avg_acc1_svm)
	print "%16s %6s  %14s %6s  %14s %6s  %12s %4s" % ("IR", "SVM", "IR", "SVM", "IR", "SVM", "IR", "SVM")
	for i, (p_se, p_svm, a_se, a_svm, a_se1, a_svm1, a_se2, a_svm2) in enumerate(zip(prec_se, prec_svm, acc_se, acc_svm, acc_se1, acc_svm1, acc_se2, acc_svm2), 1):
		print "REC-1@%02d: %6.2f %6.2f  ACC@%02d: %6.2f %6.2f  AC1@%02d: %6.2f %6.2f  AC2@%02d: %4.0f %4.0f" %(i, p_se, p_svm, i, a_se, a_svm, i, a_se1, a_svm1, i, a_se2, a_svm2)
	print
	print "REC-1 - percentage of questions with at least 1 correct answer in the top @X positions (useful for tasks were questions have at most one correct answer)"
	print "ACC   - accuracy, i.e. number of correct answers retrieved at rank @X normalized by the rank and the total number of questions"
	print "AC1   - the number of correct answers at @X normalized by the number of maximum possible answers (perfect re-ranker)"
	print "AC2   - the absolute number of correct answers at @X"
	

def eval_search_engine(res_fname, format, th=50):
	ir = read_res_file(res_fname, format)		

	# evaluate IR
	rec = metrics.recall_of_1(ir, th)
	acc = metrics.accuracy(ir, th)
	acc1 = metrics.accuracy1(ir, th)
	acc2 = metrics.accuracy2(ir, th)

	mrr = metrics.mrr(ir, th)

	print "%13s" %"IR"
	print "MRRof1: %5.2f" % mrr
	for i, (r, a, a1, a2) in enumerate(zip(rec, acc, acc1, acc2), 1):
		print "REC-1@%02d: %6.2f  ACC@%02d: %6.2f  AC1@%02d: %6.2f  AC2@%02d: %4.0f" %(i, r, i, a, i, a1, i, a2)
	print
	print "REC-1 - percentage of questions with at least 1 correct answer in the top @X positions (useful for tasks were questions have at most one correct answer)"
	print "ACC   - accuracy, i.e. number of correct answers retrieved at rank @X normalized by the rank and the total number of questions"
	print "AC1   - the number of correct answers at @X normalized by the number of maximum possible answers (perfect re-ranker)"
	print "AC2   - the absolute number of correct answers at @X"


def main():
	usage = "usage: %prog [options] arg1 [arg2]"
	desc = """arg1: file with the output of the baseline search engine (ex: svm.test.res) 
	arg2: predictions file from svm (ex: train.predictions)
	if arg2 is ommited only the search engine is evaluated"""

	parser = OptionParser(usage=usage, description=desc)
	parser.add_option("-t", "--threshold", dest="th", default=15, type=int, 
	                  help="supply a value for computing Precision up to a given threshold "
	                  "[default: %default]", metavar="VALUE")
	parser.add_option("-r", "--reranking_threshold", dest="reranking_th", default=None, type=float, 
	                  help="if maximum prediction score for a set of candidates is below this threshold, do not re-rank the candiate list."
	                  "[default: %default]", metavar="VALUE")
	parser.add_option("-f", "--format", dest="format", default="trec", 
	                  help="format of the result file (trec, answerbag): [default: %default]", 
	                  metavar="VALUE")	 	  
	parser.add_option("-v", "--verbose", dest="verbose", default=False, action="store_true",
	                  help="produce verbose output [default: %default]")	 	  
	parser.add_option("--ignore_noanswer", dest="ignore_noanswer", default=False, action="store_true",
	                  help="ignore questions with no correct answer [default: %default]")
	parser.add_option("--ignore_allanswer", dest="ignore_allanswer", default=False, action="store_true",
	                  help="ignore questions with all correct answers [default: %default]")
	
	(options, args) = parser.parse_args()

	if len(args) == 1:
		res_fname = args[0]
		eval_search_engine(res_fname, options.format, options.th)
	elif len(args) == 2:
		res_fname = args[0]
		pred_fname = args[1]	
		eval_reranker(res_fname, pred_fname, options.format, options.th, 
		              options.verbose, options.reranking_th, options.ignore_noanswer, options.ignore_allanswer)
	else:
		parser.print_help()
		sys.exit(1)
	

if __name__ == '__main__':	
	main()

