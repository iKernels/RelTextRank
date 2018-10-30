from kernel_utils import convert_svm_light_vector_to_np_array
import pandas as pd
from itertools import izip
import numpy as np
def read_strong_feats(strong_features_ids_file, strong_features_file, strong_feature_array_size,
                      feature_column_name="v"):
    zero_vector = np.zeros(strong_feature_array_size)
    tuples = []
    with open(strong_features_ids_file, "r") as f1, open(strong_features_file, "r") as f2:
        for id_line, feature_line in izip(f1, f2):
            qid, aid = id_line.split(" ")[:2]
            fv = convert_svm_light_vector_to_np_array(" ".join(feature_line.strip().split(" ")[1:]),
                                                      strong_feature_array_size, zero_vector)
            tuples.append((qid, aid, fv))

    return pd.DataFrame(tuples, columns=["qid", "aid", feature_column_name])
