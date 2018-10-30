import codecs
import re
import os
import sys
import os
import logging


qa_pair_regexp = ".*\<QApairs +id=\'([^\']+)\'\>.*"

question_open = "<question>"
question_close = "</question>"

positive_open = "<positive>"
positive_close = "</positive>"

negative_open = "<negative>"
negative_close = "</negative>"


def remove_spaces(s):
    for mark in [",", "."]:
        s = s.replace(" %s" % (mark), mark)
    s = s.replace("<num> ", "")
    return s


def convert_input_file(data_file, remove_space_before_punkt=False):
    qopen = False
    questions = []
    answers = []

    with codecs.open(data_file, 'r', encoding='utf8') as inputfile:
        lines = [line.strip() for line in inputfile]

    i = 0
    running_qid = -1
    running_aid = 0
    while i < len(lines):
        line = lines[i]
        txt = line.strip()
        m = re.match(qa_pair_regexp, txt)
        if m:
            running_qid = m.group(1)
            running_aid = 0
            i = i + 1
            continue

        if line.startswith(question_open):
            i = i + 1
            question_text = lines[i].replace("\t", " ")
            questions.append((running_qid, question_text))
            i = i + 1
            continue

        if line.startswith(positive_open) or line.startswith(negative_open):
            i = i + 1
            answer_text = lines[i].replace("\t", " ")
            label = "true" if line.startswith(positive_open) else "false"
            answers.append((running_qid, running_aid, label, remove_spaces(answer_text) if remove_space_before_punkt
            else answer_text))
            running_aid = running_aid + 1
            i = i + 1
            continue
        i = i + 1
    return questions, answers

if __name__ == '__main__':
    if len(sys.argv)!=2:
        print "Usage: python scripts/converters/trec13_convert.py <input_data_folder> <output_folder>"
        sys.exit(0)

    basedir = sys.argv[1]
    output_folder = sys.argv[2]
    # basedir = "/home/kateryna/corpora/trec13/jacana/tree-edit-data/answerSelectionExperiments/answer"

    modes = [("train", "train2393.cleanup.xml"),
             ("dev", "dev-less-than-40.manual-edit.xml"),
             ("test", "test-less-than-40.manual-edit.xml")]


    full_data = dict()
    for mode, file_name in modes:
        data_file = os.path.join(basedir, file_name)
        full_data[mode] = convert_input_file(data_file, remove_space_before_punkt=(mode == "train"))



    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    # output_folder = "/home/kateryna/software/distribution/emnlp2018/RelationalTextRanking/data/trec13"
    for mode in full_data:
        questions, answers = full_data[mode]
        print "Writing to: %s" % os.path.join(output_folder, "%s.questions" % mode)
        logging.info("Writing to: %s" % os.path.join(output_folder, "%s.questions" % mode))
        with codecs.open(os.path.join(output_folder, "%s.questions" % mode), "w", encoding='utf8') as of:
            for qid, qtext in questions:
                of.write("%s %s\n" % (qid, qtext))
        print "Writing to: %s" % os.path.join(output_folder, "%s.resultset" % mode)
        logging.info("Writing to: %s" % os.path.join(output_folder, "%s.resultset" % mode))
        with codecs.open(os.path.join(output_folder, "%s.resultset" % mode), "w", encoding='utf8') as of:
            for qid, aid, label, answer_text in answers:
                of.write("%s %s-%d %d 1.0 %s %s\n" % (qid, qid, aid, aid + 1, label, answer_text))