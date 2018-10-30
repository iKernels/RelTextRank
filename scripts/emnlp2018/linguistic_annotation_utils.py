
import spacy
from spacy.attrs import LEMMA
from global_constants import *
import numpy as np

def add_rel_to_indices(lst, indices, rel_label="REL", rel_pattern="%s-%s"):
    '''
    adds REL labels to the elements with the predefined indices in a list
    :param lst:
    :param indices:
    :param rel_label:
    :param rel_pattern:
    :return:
    '''
    for i in indices:
        lemma, pos, sw =  lst[i]
        if lemma.startswith(rel_label):
            continue
        lst[i] = (rel_pattern % (rel_label, lemma),
                               rel_pattern % (rel_label, pos), sw)



def add_token_triples_representation(df, annotation_col_suffix="_doc", rel_col_suffix="_rel_triples",
                                      add_rel=True):
    '''
    converts spacy annotations (lemma, pos, stopword) into the tuples
    :param df:
    :param annotation_col_suffix:
    :param rel_col_suffix:
    :param add_rel:
    :return:
    '''
    rel_q=[]
    rel_a=[]
    for qdoc, adoc in zip(df[QUESTION+annotation_col_suffix].values.tolist(),
                          df[ANSWER+annotation_col_suffix].values.tolist()):

        lemmas_and_pos_q = [(t.lemma_, t.tag_,  t.is_stop) for t in qdoc]
        lemmas_and_pos_a = [(t.lemma_, t.tag_,  t.is_stop) for t in adoc]

        if add_rel:
            qarr= qdoc.to_array([LEMMA])
            aarr = adoc.to_array([LEMMA])

            mask_q = np.in1d(qarr, aarr)
            mask_a = np.in1d(aarr, qarr)


            aids = np.arange(aarr.shape[0])[mask_a]
            qids = np.arange(qarr.shape[0])[mask_q]

            add_rel_to_indices(lemmas_and_pos_q, qids)
            add_rel_to_indices(lemmas_and_pos_a, aids)
        rel_q.append(lemmas_and_pos_q)
        rel_a.append(lemmas_and_pos_a)
    print len(rel_q)
    df[QUESTION+rel_col_suffix] = rel_q
    df[ANSWER+rel_col_suffix] = rel_a

def get_annotations_list(doc, lemma=True, pos=True, include_sw=True):
    ann=[]
    for t in doc:
        if not include_sw and t.is_stop:
            continue
        comps=[]
        if lemma:
            comps.append(t.lemma_)
        if pos:
            comps.append(t.tag_)
        ann.append("_".join(comps))
    return ann

def dummy_preprocess(text):
    return text


def dummy_tokenize(text):
    return text

def spacy_linguistic_annotations(ds, colname, n_threads=1, suffix="_doc"):
    '''
    does only spacy linguistic annotations
    :param ds:
    :param colname:
    :param n_threads:
    :param suffix:
    :return:
    '''
    utexts = [t.decode("utf-8") if t is not None else "".decode() for t in ds[colname].values.tolist()]
    annotations = []
    nlp = spacy.load('en', disable=['parser', 'textcat'])
    for doc in nlp.pipe(utexts, batch_size=10000, n_threads=n_threads):
        annotations.append(doc)

    ds[colname+suffix]=annotations

def linguistic_annotations(ds, colname, configurations,n_threads=1):
    '''
    does linguistic annotations and adds columns with the lists of various tokens representations (specified by configs)
    :param ds:
    :param colname:
    :param configurations:
    :param n_threads:
    :return:
    '''
    # utexts=[t.decode("utf-8") for t in ds[colname].values.tolist()]
    utexts = [t.decode("utf-8") if t is not None else "".decode() for t in ds[colname].values.tolist()]
    annotations = []
    nlp = spacy.load('en', disable=['parser', 'textcat'])
    for doc in nlp.pipe(utexts, batch_size=10000, n_threads=n_threads):
        annotations.append(doc)

    ds[colname+"_doc"]=annotations


    for confname in configurations:
        ds[colname+confname]=ds[colname+"_doc"].apply(get_annotations_list,
                                                                        args=configurations[confname] )



