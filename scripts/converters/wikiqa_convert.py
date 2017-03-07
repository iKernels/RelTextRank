__author__ = 'kateryna'

import sys
import csv
import codecs



if __name__ == '__main__':
    if len(sys.argv)!=4:
        print "Usage: python scripts/converters/wikiqa_convert.py <input-wiki-qa-file> <output reltextrank questionfile>" \
              " <output reltextrank answerfile>"
        sys.exit(0)
    input_file_path = sys.argv[1]
    output_file_path_question = sys.argv[2]
    output_file_path_answer = sys.argv[3]
    outfile_question = codecs.open(output_file_path_question, "w", encoding='utf8')
    outfile_answer = codecs.open(output_file_path_answer, "w", encoding='utf8')

    questionsList = set()

    questionLinePattern = "%s %s\n"
    answerLinePattern = "%s %s-%s 0 %.5f %s %s\n"

    firstLine= True
    with codecs.open(input_file_path, 'r', encoding='utf8') as inputfile:
#        reader = csv.DictReader(inputfile, delimiter="\t")
        for line in inputfile:
            if firstLine:
                firstLine=False
                continue
            questionID, question, documentID, documentTitle, sentenceID, sentence, label = line.split("\t")
            if not questionID in questionsList:
                questionsList.add(questionID)
                outfile_question.write(questionLinePattern % (questionID, question))
            text_label = "true"
            if int(label)<1:
                text_label="false"
            answerTuple = (questionID, questionID, sentenceID, float(label), text_label,
                           sentence)
            outfile_answer.write(answerLinePattern % answerTuple)



    outfile_question.close()
    outfile_answer.close()

