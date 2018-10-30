#!/bin/bash

if [ $# != 3 ]
  then
    echo "Usage: input_folder kernel_id output_folder log_file_id"
    echo 'input folder should contain files svm.mode and svm.mode.relevancy, where mode is train, dev or test'
    echo 'kelp configuration file will be read from config/kelp/${kernel_id}-kernel.json'
  exit
fi

inputfolder=${1}
kernel_id=${2}
folder=${3}


mkdir -p ${folder}

nohup java -Xmx20G it.unitn.nlpir.system.core.precomputed.KelpGramMatrixGenerator -mode train_${kernel_id} -svmLightFileTrain ${inputfolder}/svm.train -idsFileTrain ${inputfolder}/svm.train.relevancy -outputMatrixFolder ${folder} -kernelConfigurationFile config/kelp/${kernel_id}-kernel.json -logstep 100000 > ${folder}/${kernel_id}_gram_train.log  2>&1 &

nohup java -Xmx10G it.unitn.nlpir.system.core.precomputed.KelpGramMatrixGenerator -mode dev_${kernel_id} -svmLightFileTrain ${inputfolder}/svm.train -idsFileTrain ${inputfolder}/svm.train.relevancy -svmLightFileTest ${inputfolder}/svm.dev -idsFileTest ${inputfolder}/svm.dev.relevancy -outputMatrixFolder ${folder} -kernelConfigurationFile config/kelp/${kernel_id}-kernel.json > ${folder}/${kernel_id}_gram_dev.log 2>&1 &

nohup java -Xmx10G it.unitn.nlpir.system.core.precomputed.KelpGramMatrixGenerator -mode test_${kernel_id} -svmLightFileTrain ${inputfolder}/svm.train -idsFileTrain ${inputfolder}/svm.train.relevancy -svmLightFileTest ${inputfolder}/svm.test -idsFileTest ${inputfolder}/svm.test.relevancy -outputMatrixFolder ${folder} -kernelConfigurationFile config/kelp/${kernel_id}-kernel.json > ${folder}/${kernel_id}_gram_test.log  2>&1 &

echo "Three parallels matrix extraction scripts are running in the background. You may check the logs in the following folders...."
echo ${folder}/${kernel_id}_gram_train.log
echo ${folder}/${kernel_id}_gram_dev.log
echo ${folder}/${kernel_id}_gram_test.log
wait
echo "output written to ${inputfolder}_gram"