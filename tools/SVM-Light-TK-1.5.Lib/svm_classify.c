/***********************************************************************/
/*                                                                     */
/*   svm_classify.c                                                    */
/*                                                                     */
/*   Classification module of Support Vector Machine.                  */
/*                                                                     */
/*   Author: Thorsten Joachims                                         */
/*   Date: 02.07.02                                                    */
/*                                                                     */
/*   Copyright (c) 2002  Thorsten Joachims - All rights reserved       */
/*                                                                     */
/*   This software is available for non-commercial use only. It must   */
/*   not be modified and distributed without prior permission of the   */
/*   author. The author is not responsible for implications from the   */
/*   use of this software.                                             */
/*                                                                     */
/************************************************************************/

#include "svm_common.h"
#include "SVMLightTK.h"
#define MAX_MODEL_NUM 1000

char docfile[200];
char modelfile[200];
char predictionsfile[200];

static int initialize(const char *, double *thresh);
static void shutdown_classifier(int);
static double classify (const char *, int);
void read_input_parameters(int, char **, char *, char *, char *, long *, 
			   long *);
void print_help(void);

static double last_threshold;

JNIEXPORT jint JNICALL Java_svmlighttk_SVMLightTK_load_1model
  (JNIEnv *env, jclass cls, jstring s)
{
  const char *str = (*env)->GetStringUTFChars(env, s, 0);
  int model_idx=initialize(str,&last_threshold);
  (*env)->ReleaseStringUTFChars(env, s, str);
  return model_idx;
}

JNIEXPORT jdouble JNICALL Java_svmlighttk_SVMLightTK_classify_1instance
  (JNIEnv *env, jclass cls, jint model_no, jstring s)
{
  double result;
  const char *str = (*env)->GetStringUTFChars(env, s, 0);
  result=classify(str,model_no);
  (*env)->ReleaseStringUTFChars(env, s, str);
  return result;
}

JNIEXPORT jdouble JNICALL Java_svmlighttk_SVMLightTK_get_1threshold
  (JNIEnv *env, jclass cls)
{
  return last_threshold;
}

static MODEL model_vect[MAX_MODEL_NUM]; 
static int number_of_model=0;

  
int initialize (const char *modelfile, double *thresh)
{
  long llsv;
  long max_sv,max_words_sv;

//  read_input_parameters(argc,argv,docfile,modelfile,predictionsfile,
//			&verbosity,&pred_format);
			
  /* STANDARD SVM KERNELS */
      			
  nol_ll(modelfile,&max_sv,&max_words_sv,&llsv); /* scan size of model file */
  max_words_sv+=2;
  llsv+=2;

  model_vect[number_of_model].supvec = (DOC **)my_malloc(sizeof(DOC *)*max_sv);
  model_vect[number_of_model].alpha = (double *)my_malloc(sizeof(double)*max_sv);


  read_model(modelfile,&(model_vect[number_of_model]),max_words_sv,llsv);


  if(model_vect[number_of_model].kernel_parm.kernel_type == 0) { /* linear kernel */
    /* compute weight vector */
    add_weight_vector_to_linear_model(&model_vect[number_of_model]);
  }
  
  *thresh=model_vect[number_of_model].b;
  number_of_model++;
  return number_of_model-1;
}  
  
double classify (const char *line_input, int i){
        
  DOC doc;   /* test example */
  long max_words_doc;
  long wnum=0;
  long j;
  double doc_label;
  double t1,runtime=0;
  double dist;
  char *line;

  LAMBDA = model_vect[i].kernel_parm.lambda; // to make faster the kernel evaluation 
  LAMBDA2 = LAMBDA*LAMBDA;
  MU=model_vect[i].kernel_parm.mu;
  TKGENERALITY=model_vect[i].kernel_parm.first_kernel;
  PARAM_VECT=model_vect[i].kernel_parm.tree_kernel_params;
  //TO MODIFY if PARAMETERS FROM FILE if(PARAM_VECT == 1) read_input_tree_kernel_param(); // if there is the file tree_kernel.param load paramters
  
  line = strdup(line_input);

  max_words_doc=strlen(line);
  parse_document(line,&doc,&doc_label,&wnum,max_words_doc,&model_vect[i].kernel_parm);
 //   totdoc++;

    if(model_vect[i].kernel_parm.kernel_type == 0) {   /* linear kernel */
      for(j=0;(doc.vectors[0]->words[j]).wnum != 0;j++) {  /* Check if feature numbers   */
	if((doc.vectors[0]->words[j]).wnum>model_vect[i].totwords) /* are not larger than in     */
	  (doc.vectors[0]->words[j]).wnum=0;               /* model. Remove feature if   */
      }                                        /* necessary.                 */
      t1=get_runtime();
      dist=classify_example_linear(&model_vect[i],&doc);
      runtime+=(get_runtime()-t1);
    }
    else {                             /* non-linear kernel */
      t1=get_runtime();
      dist=classify_example(&model_vect[i],&doc);
      runtime+=(get_runtime()-t1);
    }
  
   freeExample(&doc);// free the trees in the data item
   free(line);

  return(dist);
}

void shutdown_classifier(int i){
int j;

  for(j=1;j<model_vect[i].sv_num;j++) {       
    freeExample(model_vect[i].supvec[j]);
  }
  free(model_vect[i].supvec);
  free(model_vect[i].alpha);
  
  if(model_vect[i].kernel_parm.kernel_type == 0) { /* linear kernel */
    free(model_vect[i].lin_weights);
  }
  number_of_model--;
}

 

void read_input_parameters(int argc, char **argv, char *docfile, 
			   char *modelfile, char *predictionsfile, 
			   long int *verbosity, long int *pred_format)
{
  long i;
  
  /* set default */
  strcpy (modelfile, "svm_model");
  strcpy (predictionsfile, "svm_predictions"); 
  (*verbosity)=2;
  (*pred_format)=1;

  for(i=1;(i<argc) && ((argv[i])[0] == '-');i++) {
    switch ((argv[i])[1]) 
      { 
      case 'h': print_help(); exit(0);
      case 'v': i++; (*verbosity)=atol(argv[i]); break;
      case 'f': i++; (*pred_format)=atol(argv[i]); break;
      default: printf("\nUnrecognized option %s!\n\n",argv[i]);
	       print_help();
	       exit(0);
      }
  }
  if((i+1)>=argc) {
    printf("\nNot enough input parameters!\n\n");
    print_help();
    exit(0);
  }
  strcpy (docfile, argv[i]);
  strcpy (modelfile, argv[i+1]);
  if((i+2)<argc) {
    strcpy (predictionsfile, argv[i+2]);
  }
  if(((*pred_format) != 0) && ((*pred_format) != 1)) {
    printf("\nOutput format can only take the values 0 or 1!\n\n");
    print_help();
    exit(0);
  }
}

void print_help(void)
{
  printf("\nTree Kernel in SVM-light %s : SVM Classification module %s\n",VERSION,VERSION_DATE);
  printf("by Alessandro Moschitti, moschitti@info.uniroma2.it\n");
  printf("University of Rome \"Tor Vergata\"\n\n");

  copyright_notice();
  printf("   usage: svm_classify [options] example_file model_file output_file\n\n");
  printf("options: -h         -> this help\n");
  printf("         -v [0..3]  -> verbosity level (default 2)\n");
  printf("         -f [0,1]   -> 0: old output format of V1.0\n");
  printf("                    -> 1: output the value of decision function (default)\n\n");
}



