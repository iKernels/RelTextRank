#!/usr/bin/env bash
if [ $# -lt 2 ]
  then
    echo "Usage: corpusname corpusfldername nofocus(optinoal)"
    echo "name of the corpus"
    echo "name of the corpus folder"
    exit
fi

focus_cmd=""
remove_irrelevant=" --remove_irrelevant"

corpusname=${1}
corpusname_folder=${2}

if [ "${corpusname}" = "semeval" ]; then
    focus_cmd=" -a \" -additionalConfigProperties doFocusMatch=false \"  -atr \" -keepAllNegatives\""
    echo "Additional parameters: ${focus_cmd}"
    remove_irrelevant=""
fi





#**********INITIAL PREPROCESSING (PROVIDES DATA FOR B  MATRIX COMPUTATION)**********
#[MANDATORY FOR EVERY CONFIG]
#Converting the Wikiqa corpus to the pandas dataframe, preprocessing all the text with Spacy, removing all-positives/all-negatives,
#extracting the BOW representations, and pickling everything into a file
#the corpus will be contained in a dictionary with three keys "train", "test" and "dev" and their respective values will be the respective data split
#You conveniently view the contents of the object by unpickling it and viewing each dataframe in Jupyter notebooks

echo "preprocessing the corpus and saving it as a dict of pandas dataframes in data/${corpusname_folder}/pkl"
mkdir -p data/${corpusname_folder}/pkl
echo python scripts/emnlp2018/preprocessing.py -o data/${corpusname_folder}/pkl/${corpusname}.pkl -c scripts/experiment_launchers/corpus_settings/${corpusname}.settings -et 10 ${remove_irrelevant}
python scripts/emnlp2018/preprocessing.py -o data/${corpusname_folder}/pkl/${corpusname}.pkl -c scripts/experiment_launchers/corpus_settings/${corpusname}.settings -et 10 ${remove_irrelevant}


#**********GETTING CROSS-PAIR BOW MATRICES(PROVIDES DATA FOR B_{CR}  MATRIX COMPUTATION)**********
echo "generating cross-pair bow matrices and saving them in data/gram-matrices/${corpusname}"
mkdir -p data/gram-matrices/${corpusname}
python scripts/emnlp2018/extract_and_pickle_cross_pair_bow_matrices.py -i data/${corpusname_folder}/pkl/${corpusname}.pkl -o data/gram-matrices/${corpusname}/bow_qq_aa_matrix_%s.npy

#**********EMBEDDINGS EXTRACTION (PROVIDES DATA FOR E AND E_{CR} MATRIX COMPUTATION)**********
#DO THIS ONLY IF YOU WANT TO RUN EXPS WITH E AND E_{CR}
#Extract the w2v- and GloVe-based representations of the input corpora (needed for E and $E_cr$ features from the paper)
#Each text (a question or an answer) is represented as an averaged sum of embeddings of its individual words
#You can consulte the list of embeddings in the scripts/configs/embeddings.json file.
#Theoretically, you can add your own pre-learned vocabularies there, but this option has not been tested.
#Modify paths in embeddings.json to point to your local distributions of embeddings collections.
#The scripts/configs/embeddings.json contains instructions from were to download the embeddings required for replicating the experiment
#We opted not provide a script which wgets them to a specific locations, as they are very big and you are very likely to already have them on your local machine/server.
#The output will be stored to data/${corpusname_folder}/pkl/embeddings.pkl.gz
#Again, this will be a dict with an entry for each split
echo "extracting the embeddings-based representations of the input data and saving them in data/gram-matrices/${corpusname}"
mkdir -p data/gram-matrices/${corpusname}
python scripts/emnlp2018/embedding_extractor.py -c data/${corpusname_folder}/pkl/${corpusname}.pkl -o data/${corpusname_folder}/pkl/embeddings.pkl -e scripts/configs/embeddings.json


#**********SST AND PTK EXTRACTION (PROVIDES DATA FOR PTK AND SST MATRIX COMPUTATION)**********
#DO THIS ONLY IF YOU WANT TO RUN EXPS WITH PTK AND SST

#----extracting SST matrix----
# Extracting tree representations for the input data and writing them down in svmlight-tk format
# Note: at first run it will do annotation of the corpus with Stanford CoreNLP tools (and write the annotations to
#the CASes folder and will take time); when performing the first run, do not run it simultaneously the the analogous step
# for extracting the PTK matrix as they both will try write tot he same folders and conflicts may happen
# NOTE: if this script is too computationally heavy for your machine, edit it by adding "wait" in-between different java calls
#the below line represents the Q and AP as CONST trees
#nohup sh scripts/generated_scripts/cross_pair/${corpusname}_NORELConstExperiment_emnlp2018.sh > logs/${corpusname}_NORELConstExperiment_emnlp2018.log 2>&1  &
mkdir -p scripts/generated_scripts/cross_pair
for expclass in CHExperiment ConstExperiment; do
    echo "extracting the ${expclass} representation of the input data for cross-pair tree kernel computation and saving it to data/examples/${corpusname}_${expclass}_emnlp2018"
    pipeline_cmd=`eval python scripts/experiment_launchers/experiment_launcher_generic.py ${focus_cmd} -l ${corpusname} -o scripts/generated_scripts/cross_pair -c ${expclass} -e it.unitn.nlpir.experiment.fqa.${expclass} -s it.unitn.nlpir.system.core.ClassTextPairConversion --only_data_generation -d emnlp2018 --pararellize_annotation`
    echo "Running ${pipeline_cmd}"
    eval  ${pipeline_cmd}
    wait
done

#here we compute the sst tree kernel on the CONST tree representations used in the previous step
# NOTE: this launches three java processes in parallel; If this is too heavy for you local system
#manually insert the "wait" command between the nohups in the scripts/emnlp2018/run_kelp_gram_matrix_extraction.sh shell
#script
echo "Using Kelp implementation of the SST kernel to extract gram matrix from the CONST input representation and save it as a txt file"
sh scripts/emnlp2018/run_kelp_gram_matrix_extraction.sh data/examples/${corpusname}_ConstExperiment_emnlp2018 sst data/examples/${corpusname}_ConstExperiment_emnlp2018_gram
wait

echo "Converting the text file  with the CONST-SST Gram matrix to a numpy matrix and pickling it"
folder=data/examples/${corpusname}_ConstExperiment_emnlp2018_gram
kernel_name=sst
mkdir -p data/gram-matrices/${corpusname}
python scripts/emnlp2018/kelp_to_pickled_df_matrix_converter.py -c scripts/emnlp2018/corpus_configs/${corpusname}.json -f ${folder} -o data/gram-matrices/${corpusname} -s ${kernel_name}


echo "Using Kelp implementation of the PTK kernel to extract gram matrix from the CH input representation and save it as txt file"
sh scripts/emnlp2018/run_kelp_gram_matrix_extraction.sh data/examples/${corpusname}_CHExperiment_emnlp2018 ptk data/examples/${corpusname}_CHExperiment_emnlp2018_gram
wait


folder=data/examples/${corpusname}_CHExperiment_emnlp2018_gram
kernel_name=ptk
echo "Converting the text file  with the CH-PTK Gram matrix to a numpy matrix and pickling it"
python scripts/emnlp2018/kelp_to_pickled_df_matrix_converter.py -c scripts/emnlp2018/corpus_configs/${corpusname}.json -f ${folder} -o data/gram-matrices/${corpusname} -s ${kernel_name}
echo "Gram matrices written to data/gram-matrices/${corpusname}"

#**********BASELINE STRONG FEATURE VECTOR EXTRACTION (PROVIDES DATA FOR V MATRIX COMPUTATION)**********
#DO THIS ONLY IF YOU WANT TO RUN EXPS WITH V
if [ ${corpusname} != "semeval" ]; then
    echo "Extracting the feature vector representations and saving the to data/examples/${corpusname}_noesa_strong_features_emnlp2018"
    expclass=CHPureFeaturesExperiment
    cmd_feats=`python scripts/experiment_launchers/experiment_launcher_generic.py -l ${corpusname} -o scripts/generated_scripts/cross_pair -c noesa_strong_features -e it.unitn.nlpir.experiment.fqa.features.${expclass} -s it.unitn.nlpir.system.nonstruct.NonStructClassTextPairConversion --only_data_generation -d emnlp2018 --pararellize_annotation -a " -featureExtractorClass  it.unitn.nlpir.features.presets.NoESANoQCAllFeatures"`
#    nohup sh scripts/generated_scripts/cross_pair/${corpusname}_noesa_strong_features_emnlp2018.sh > logs/${corpusname}_noesa_strong_features_emnlp2018.log 2>&1  &
    echo ${cmd_feats}
    eval ${cmd_feats}
    wait
fi
echo "DONE"