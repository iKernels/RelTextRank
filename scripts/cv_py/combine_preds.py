__author__ = 'ktym'
import sys
#from ev import eval_reranker
HOME = "d:/Work/QA/qapipeline_current/qapipeline"
if __name__ == '__main__':
    argsstr="{}/data/examples/rte3_dep-2014-09-29/svm.pred {}/data/examples/rte3-nnf/svm_v.pred {}/data/examples/rte3-nnf/svm_t_nnf_combo.pred {}/data/examples/rte3/svm.relevancy".format(HOME, HOME, HOME, HOME)
    argv_or = sys.argv

    #sys.argv = argv_or + argsstr.split(" ")
    file1=sys.argv[1]
    file2=sys.argv[2]
    outfile=sys.argv[3]
    relevancy=sys.argv[4]

    pred1 = [float(line.strip()) for line in open(file1, 'r')]
    pred2 = [float(line.strip()) for line in open(file2, 'r')]
    #rel = [line.strip().split(" ")[4] for line in open(relevancy, 'r',encoding='utf8')]
    i=0.3
    acc1 =0.0;
    acc2 = 0.0;
    acc3 = 0.0
    combo = []
    while i<=1:
        print("alpha="+str(i)+"\n")
        thefile = open(outfile, 'w')
        combo = [i*x1+(1-i)*x2 for (x1, x2) in zip(pred1, pred2)]
        for sumx in [i*x1+(1-i)*x2 for (x1, x2) in zip(pred1, pred2)]:
            thefile.write("%.7f\n" % sumx)
        thefile.close()
        #eval_reranker(pred_fname=outfile,res_fname=relevancy)
        i += 1
    '''
    j = 0
    i = 0
    tp = [0.0,0.0,0.0]
    fp = [0.0,0.0,0.0]
    fn = [0.0,0.0,0.0]
    for r in rel:
        #print("{} {} {} {}".format(pred1[i], pred2[i], combo[i], r))
        ans = [pred1[i], pred2[i],combo[i]]


        j = 0
        for a in ans:
            if (r=="true"):
                if a>0.0:
                    tp[j] += 1.0
                else:
                    fn[j] += 1.0
            else:
                if a>0.0:
                    fp[j] += 1.0
            j = j + 1

        if ((r=="true") and (pred1[i]>0)) or ((r=="false") and (pred1[i]<=0)):
            acc1 += 1.0
        if ((r=="true") and (pred2[i]>0)) or ((r=="false") and (pred2[i]<=0)):
            acc2 += 1.0
        if ((r=="true") and (combo[i]>0)) or ((r=="false") and (combo[i]<=0)):
            acc3 += 1.0
        i += 1
    p1 = tp[0]/(tp[0]+fp[0])
    p2 = tp[1]/(tp[1]+fp[1])
    p3 = tp[2]/(tp[2]+fp[2])
    r1 = tp[0]/(tp[0]+fn[0])
    r2 = tp[1]/(tp[1]+fn[1])
    r3 = tp[2]/(tp[2]+fn[2])
    f11 = 2*p1*r1/(p1+r1)
    f12 = 2*p2*r2/(p2+r2)
    f13 = 2*p3*r3/(p3+r3)
    print("Accuracy on {}: {:.8f}, F1: {:.8f}\n".format(file1, acc1/float(len(rel)), f11))
    print("Accuracy on {}: {:.8f}, F1: {:.8f}\n".format(file2, acc2/float(len(rel)), f12))
    print("Accuracy on {}: {:.8f}, F1: {:.8f}\n".format(outfile, acc3/float(len(rel)), f13))
    '''