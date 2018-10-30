import numpy as np

def custom_jaccard_similarity(x, y):
    #     print x,y
    intersection_cardinality = len(set.intersection(x, y))
    union_cardinality = len(set.union(x, y))
    #     logger.debug(x)
    #     logger.debug(y)
    #     logger.debug("INTERSECTION")
    #     logger.debug(intersection_cardinality)
    #     logger.debug("UNION")
    #     logger.debug(union_cardinality)
    if union_cardinality < 1:
        return 0.0
    return np.float32(intersection_cardinality / float(union_cardinality))


def word_ngram_containment(x, y):
    intersection_cardinality = len(set.intersection(x, y))
    union_cardinality = len(x)
    if union_cardinality < 1:
        return 0.0
    return np.float32(float(intersection_cardinality) / float(union_cardinality))


# from https://github.com/scikit-learn/scikit-learn/blob/master/sklearn/metrics/pairwise.py
def lcs(a, b):
    lengths = [[0 for j in range(len(b) + 1)] for i in range(len(a) + 1)]
    # row 0 and column 0 are initialized to 0 already
    for i, x in enumerate(a):
        for j, y in enumerate(b):
            if x == y:
                lengths[i + 1][j + 1] = lengths[i][j] + 1
            else:
                lengths[i + 1][j + 1] = max(lengths[i + 1][j], lengths[i][j + 1])
    # read the substring out from the matrix
    result = ""
    x, y = len(a), len(b)
    while x != 0 and y != 0:
        if lengths[x][y] == lengths[x - 1][y]:
            x -= 1
        elif lengths[x][y] == lengths[x][y - 1]:
            y -= 1
        else:
            assert a[x - 1] == b[y - 1]
            result = a[x - 1] + result
            x -= 1
            y -= 1
    return result


def lcs_list(a, b):
    lengths = [[0 for j in range(len(b) + 1)] for i in range(len(a) + 1)]
    # row 0 and column 0 are initialized to 0 already
    for i, x in enumerate(a):
        for j, y in enumerate(b):
            if x == y:
                lengths[i + 1][j + 1] = lengths[i][j] + 1
            else:
                lengths[i + 1][j + 1] = max(lengths[i + 1][j], lengths[i][j + 1])
    # read the substring out from the matrix
    result = []
    x, y = len(a), len(b)
    while x != 0 and y != 0:
        if lengths[x][y] == lengths[x - 1][y]:
            x -= 1
        elif lengths[x][y] == lengths[x][y - 1]:
            y -= 1
        else:
            assert a[x - 1] == b[y - 1]
            result = [a[x - 1]] + result
            x -= 1
            y -= 1
    return result


def lcs_list_dist(x, y):
    lcs_str = lcs_list(x, y)
    total_len = float(len(x) + len(y))
    similarity = 1.0 - (total_len - 2 * float(len(lcs_str))) / total_len
    #     logger.debug("X=%s; Y=%s" %(x,y))
    #     logger.debug("LCS_STR = %s" %(lcs_str))
    #     logger.debug("lcs_len = %d" %(len(lcs_str)))
    #     logger.debug("total_len = %d" %(total_len))
    #     logger.debug("similarity = %.3f" %(similarity))
    #     logger.debug("")

    return similarity


def lcs_dist(x, y):
    lcs_str = lcs(x, y)
    total_len = float(len(x) + len(y))
    #     logger.debug("X=%s; Y=%s" %(x,y))
    #     logger.debug("LCS_STR = %s" %(lcs_str))
    #     logger.debug("lcs_len = %d" %(len(lcs_str)))
    #     logger.debug("total_len = %d" %(total_len))
    #     logger.debug("similarity = %.3f" %(similarity))
    #     logger.debug("")

    similarity = 1.0 - (total_len - 2 * float(len(lcs_str))) / total_len
    return 1.0 - (total_len - 2 * float(len(lcs_str))) / total_len


def custom_cdist(XA, XB, metric, **kwargs):
    mA = XA.shape[0]
    mB = XB.shape[0]
    dm = np.empty((mA, mB), dtype=np.double)
    for i in xrange(0, mA):
        for j in xrange(0, mB):
            dm[i, j] = metric(XA[i], XB[j], **kwargs)
    return dm
