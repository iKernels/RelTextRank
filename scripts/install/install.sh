#remember to launch ${JAVA_HOME} before running this script


#install local libraries
echo "Importing local libraries..."
mvn install:install-file -Dfile=lib/PTK.jar -DgroupId=it.unitn.kernels.ptk -DartifactId=ptk -Dversion=1.0 -Dpackaging=jar

#installing maven dependencies
echo "Downloading maven dependencies..."
mvn clean install
mvn clean dependency:copy-dependencies package


#compiling SVMLight-TK
cd tools/SVM-Light-1.5-rer
make clean; make
cd ../..