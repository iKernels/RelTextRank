import pandas as pd
import os
from collections import Counter as cnt

def get_example_id_tuples(df_i):
    return [tuple(x) for x in df_i[['qid','aid']].values.tolist()]

def get_labels(df):
    return df.label.values


def filter_dataset(df, drop_all_positives=True):
    usable = set([x[0] for x in cnt(df[['qid','label']].
                                drop_duplicates()['qid'].values.tolist()).items() if x[1]>1])
    if drop_all_positives:
        return df[df.qid.isin(usable)]
    else:
        return df[df.qid.isin(usable) | df.label==True]

def read_as_tuples(filename, sep=" ", max_col=2):
    tuples_list = []
    with open(filename, 'r') as f:
        for line in f:
            tuples_list.append(tuple(line.strip().split(sep, max_col-1)))
    return tuples_list

def get_pandas_summary(qf, af, basefolder):
    df_q = pd.DataFrame(read_as_tuples(os.path.join(basefolder,qf)), columns=["qid","question"])

    df_a = pd.DataFrame(read_as_tuples(os.path.join(basefolder,af),max_col=6), columns=["qid","aid",
                                                                                      "rank","score",
                                                                                        "label_string", "answer"])
    df_a["label"] = df_a["label_string"].apply(lambda x: x=="true")
    df_a=df_a.drop(labels="label_string", axis=1)
    df_summary=df_q.merge(df_a,on=("qid"))

    return df_summary