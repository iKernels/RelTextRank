/***********************************************************************/
/*   FAST TREE KERNEL                                                  */
/*                                                                     */
/*   tree_kernel.c                                                     */
/*                                                                     */
/*   Fast Tree kernels for Support Vector Machines		               */
/*                                                                     */
/*   Author: Alessandro Moschitti 				                       */
/*   moschitti@info.uniroma2.it					                       */	
/*   Date: 10.11.06                                                    */
/*                                                                     */
/*   Copyright (c) 2004  Alessandro Moschitti - All rights reserved    */
/*                                                                     */
/*   This software is available for non-commercial use only. It must   */
/*   not be modified and distributed without prior permission of the   */
/*   author. The author is not responsible for implications from the   */
/*   use of this software.                                             */
/*                                                                     */
/***********************************************************************/

# include "svm_common.h"

//#define TEST(a,b) printf("The value of " #a " = " #b "\n", a)

#define DPS(i,j) (*(DPS+(i)*(m+1)+j))
#define DP(i,j) (*(DP+(i)*(m+1)+j))


double LAMBDA2;
double LAMBDA;
double SIGMA;
double MU;
double REMOVE_LEAVES = 0; // if equal to MU*LAMBDA2, it removes the leaves contribution;

short  TKGENERALITY;  //store the generality of the kernel PT is 2 whereas SST and ST is 1
                      // used to load the opportune data structures (with SST and ST uses faster approach)
short  PARAM_VECT; // if 1 the vector of parameters is defined

double delta_matrix[MAX_NUMBER_OF_NODES][MAX_NUMBER_OF_NODES];

// local functions


void determine_sub_lists(FOREST *a, FOREST *b, nodePair *intersect, int *n); // pre-selection of pairs for SST and ST kernels
double Delta_SST_ST( TreeNode * Nx, TreeNode * Nz); // delta function SST and ST kernels
double Delta_SK(TreeNode **Sx, TreeNode ** Sz, int n, int m); // delta over children
double Delta_PT( TreeNode * Nx, TreeNode * Nz); // Delta for PT

void evaluateNorma(KERNEL_PARM * kernel_parm, DOC * d); // evaluate norm of trees and vectors


double basic_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b, int i, int j);  // svm-light kernels
double tree_kernel(KERNEL_PARM *kernel_parm, DOC * a, DOC * b, int i, int j); // ST and SST kernels
double SK(TreeNode **Sx, TreeNode ** Sz, int n, int m); // string kernel
double string_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int i, int j);

double choose_second_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b); //choose a standard kernel
double choose_tree_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b); //choose a tree kernel


// kernel combinations

double sequence(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
double AVA(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
double AVA_tree_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
double sequence_tree_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
double evaluateParseTreeKernel(nodePair *pairs, int n);
double advanced_kernels(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
double sequence_ranking(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int memberA, int memberB);//all_vs_all vectorial kernel
double SRL2008(KERNEL_PARM *kernel_parm, DOC *a, DOC *b);
double ACL2008_Entailment_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
double ACL2007_Entailment_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
double ACL2008(KERNEL_PARM *kernel_parm, DOC *a, DOC *b);

double QA_true_reranking(KERNEL_PARM * kernel_parm, DOC * a, DOC * b);
//-------------------------------------------------------------------------------------------------------
// SST and ST KERNELS see Moschitti - ECML 2006


double Delta_SST_ST( TreeNode * Nx, TreeNode * Nz){
	int i;
	double prod=1;
	
	//printf("Delta Matrix: %1.30lf node1:%s node2:%s, LAMBDA %lf, SIGMA %lf\n",delta_matrix[Nx->nodeID][Nz->nodeID],Nx->sName,Nz->sName,LAMBDA,SIGMA);
	
	if(delta_matrix[Nx->nodeID][Nz->nodeID]>=0)
		return delta_matrix[Nx->nodeID][Nz->nodeID]; // Case 0 (Duffy and Collins 2002);
	else 
		if(Nx->pre_terminal || Nz->pre_terminal)
			return (delta_matrix[Nx->nodeID][Nz->nodeID]=LAMBDA); // case 1 
		else{
			for(i=0;i<Nx->iNoOfChildren;i++)
				if(Nx->pChild[i]->production!=NULL && Nz->pChild[i]->production!= NULL)
					if(strcmp(Nx->pChild[i]->production, Nz->pChild[i]->production)==0)
						prod*= (SIGMA+Delta_SST_ST( Nx->pChild[i], Nz->pChild[i])); // case 2
					else prod*=SIGMA; 	          
			return (delta_matrix[Nx->nodeID][Nz->nodeID]=LAMBDA*prod);
		}
}

double evaluate_SST_ST(nodePair *pairs, int n){
	
	int i;
	double sum=0,contr;
	
	for(i=0;i<n;i++){
			//printf("Score for the pairs (%s , %s)",pairs[i].Nx->sName,pairs[i].Nz->sName);fflush(stdout);
			//printf("\ntree 1: "); writeTreeString(pairs[i].Nx); printf("\ntree 2: "); writeTreeString(pairs[i].Nz); printf("\n");
			//fflush(stdout);
		// 
		contr=Delta_SST_ST(pairs[i].Nx,pairs[i].Nz);
			//printf("%f\n",contr);fflush(stdout);
		sum+=contr;
	}
	//printf("FINAL KERNEL = %f \n\n\n",sum); 
	
	return sum;
}


//-------------------------------------------------------------------------------------------------------
// Kernel with slot used in ACL07 and ECIR07 to simulate a fast PT (within the first tree level)
//-------------------------------------------------------------------------------------------------------

double Delta_ACL07( TreeNode * Nx, TreeNode * Nz){
  int i;
  double prod=1;
	
  //printf("Delta Matrix: %1.30lf node1:%s node2:%s, LAMBDA %lf, SIGMA %lf\n",delta_matrix[Nx->nodeID][Nz->nodeID],Nx->sName,Nz->sName,LAMBDA,SIGMA);
	
  if(delta_matrix[Nx->nodeID][Nz->nodeID]>=0) {
		//printf("Delta Matrix: %1.30lf diverso da -1 boh \n",delta_matrix[Nx->nodeID][Nz->nodeID],Nx->sName,Nz->sName,LAMBDA,SIGMA);
		
		return delta_matrix[Nx->nodeID][Nz->nodeID]; // Case 0 (Duffy and Collins 2002);
  }
  else 
		if(Nx->pre_terminal || Nz->pre_terminal)
			//Alessandro
			if (strcmp(Nx->pChild[0]->sName,"null")==0 ||  strcmp(Nz->pChild[0]->sName,"null")==0) return 0; //don't consider null slots
			else
				return (delta_matrix[Nx->nodeID][Nz->nodeID]=LAMBDA); // case 1 
			else{
				for(i=0;i<Nx->iNoOfChildren;i++)
					if(strcmp(Nx->pChild[i]->production,Nz->pChild[i]->production)==0)
						prod*= (SIGMA+Delta_ACL07( Nx->pChild[i], Nz->pChild[i])); // case 2
				
				return (delta_matrix[Nx->nodeID][Nz->nodeID]=LAMBDA*prod);
			}
}

double evaluate_SST_ACL07(nodePair *pairs, int n){
	
	int i;
	double sum=0,contr;
	
	for(i=0;i<n;i++){
		//printf("Score for the pairs (%s , %s)",pairs[i].Nx->sName,pairs[i].Nz->sName);fflush(stdout);
		//printf("\ntree 1: "); writeTreeString(pairs[i].Nx); printf("\ntree 2: "); writeTreeString(pairs[i].Nz); printf("\n");
		//fflush(stdout);
		
		// Don't consider BOX label in the Bag of X trees
		if(strcmp(pairs[i].Nx->sName,"BOX")!= 0 && strcmp(pairs[i].Nz->sName,"BOX")!= 0) // to be removed as it is verified that is useless
		{
			contr=Delta_ACL07(pairs[i].Nx,pairs[i].Nz);
			//printf("%f\n",contr);fflush(stdout);
			sum+=contr;
		}
	}
	// printf("FINAL KERNEL = %f \n\n",sum); 
	
	return sum;
}



//-------------------------------------------------------------------------------------------------------
// A more general Collins' based kernel (leaves are considered as features)
//-------------------------------------------------------------------------------------------------------



double Delta_GSST( TreeNode * Nx, TreeNode * Nz){
	int i;
	double prod=1;
	if(delta_matrix[Nx->nodeID][Nz->nodeID]>=0) return delta_matrix[Nx->nodeID][Nz->nodeID]; // cashed
	else{
		for(i=0;i<Nx->iNoOfChildren && i<Nz->iNoOfChildren ;i++)
			if(Nx->pChild[i]->production != NULL && Nz->pChild[i]->production != NULL &&
				 strcmp(Nx->pChild[i]->production,Nz->pChild[i]->production)==0 
				 && Nx->pChild[i]->pre_terminal != -1 && Nz->pChild[i]->pre_terminal != -1)
				prod*= (1+Delta_GSST( Nx->pChild[i], Nz->pChild[i])); // case 2
		return (delta_matrix[Nx->nodeID][Nz->nodeID]=LAMBDA*prod);
	}
}

double evaluate_GSST(nodePair *pairs, int n){
	
	int i;
	double sum=0,contr;
	
	for(i=0;i<n;i++){
		//printf("Score for the pairs (%s , %s)",pairs[i].Nx->sName,pairs[i].Nz->sName);fflush(stdout);
		//printf("\ntree 1: "); writeTreeString(pairs[i].Nx); printf("\ntree 2: "); writeTreeString(pairs[i].Nz); printf("\n");
		//fflush(stdout);
		//        if(pairs[i].Nx->iNoOfChildren && pairs[i].Nz->iNoOfChildren) 
		contr=Delta_GSST(pairs[i].Nx,pairs[i].Nz);
		//        else contr=0;
		//printf("%f\n",contr);fflush(stdout);
		
		sum+=contr;
	}
	//printf("FINAL KERNEL = %f \n\n\n",sum); 
	
	return sum;
}

//-------------------------------------------------------------------------------------------------------
// Partial Tree Kernel - see Moschitti - ECML 2006
//-------------------------------------------------------------------------------------------------------


#ifdef FAST

double Delta_SK(TreeNode **Sx, TreeNode ** Sz, int n, int m){
	
	double DPS[MAX_NUMBER_OF_CHILDREN_PT][MAX_NUMBER_OF_CHILDREN_PT];
	double DP[MAX_NUMBER_OF_CHILDREN_PT][MAX_NUMBER_OF_CHILDREN_PT];
	double kernel_mat[MAX_NUMBER_OF_CHILDREN_PT];
  
  int i,j,l,p;
  double K;
  
  
  p = n; if (m<n) p=m;if (p>MAX_CHILDREN) p=MAX_CHILDREN;
  
  
	//  if(n==0 || m==0 || m!=n) return 0;
  
  for (j=0; j<=m; j++)
		for (i=0; i<=n; i++)DPS[i][j]=DP[i][j]=0;
	
  kernel_mat[0]=0;
  for (i=1; i<=n; i++)
		for (j=1; j<=m; j++)
			if(strcmp((*(Sx+i-1))->sName,(*(Sz+j-1))->sName)==0) 
			{
				DPS[i][j]=Delta_PT(*(Sx+i-1),*(Sz+j-1));
				kernel_mat[0]+=DPS[i][j];
			}
			else DPS[i][j]=0;
	
	
	//   printf("\nDPS\n"); stampa_math(DPS,n,m); printf("DP\n");  stampa_math(DP,n,m); 
	
	//   printf("kernel: n=%d m=%d, %s %s \n\n",n,m,(*(Sx))->sName,(*(Sz))->sName);
	
	for(l=1;l<p;l++){
		kernel_mat[l]=0;
		for (j=0; j<=m; j++)DP[l-1][j]=0;
		for (i=0; i<=n; i++)DP[i][l-1]=0;
		
		for (i=l; i<=n; i++)
			for (j=l; j<=m; j++){
				DP[i][j] = DPS[i][j]+LAMBDA*DP[i-1][j]
				+ LAMBDA*DP[i][j-1]
				- LAMBDA2*DP[i-1][j-1];
				
				if(strcmp((*(Sx+i-1))->sName,(*(Sz+j-1))->sName)==0){
					DPS[i][j] = Delta_PT(*(Sx+i-1),*(Sz+j-1))* DP[i-1][j-1];
					kernel_mat[l] += DPS[i][j];
				}
				// else DPS[i][j] = 0;             
			}
		//      printf("\n----------------------------------\n"); printf("DPS i:%d, j:%d, l:%d\n",n,m,l+1);stampa_math(DPS,n,m);printf("DP\n");stampa_math(DP,n,m); 
	}
	//  K=kernel_mat[p-1];
	K=0;
	for(l=0;l<p;l++){K+=kernel_mat[l];
		//printf("String kernel of legnth %d: %1.7f \n\n",l+1,kernel_mat[l]);
	}
  return K;
}

#endif

#ifndef FAST


void stampa_math(double *DPS,int n,int m){
	int i,j;
	
	printf("\n");  
	for (i=0; i<=n; i++){
		for (j=0; j<=m; j++)
			printf("%1.8f\t",DPS(i,j));
		printf("\n");  
	}
  printf("\n");  
}

// SLOW SOLUTION BUT ABLE TO DEAL WITH MORE DATA 

double Delta_SK(TreeNode **Sx, TreeNode ** Sz, int n, int m){
	
	
  double *DPS =(double*) malloc((m+1)*(n+1)*sizeof(double));
  double *DP = (double*) malloc((m+1)*(n+1)*sizeof(double));
  double *kernel_mat = (double*) malloc((n+1)*sizeof(double));
  
  int i,j,l,p;
  double K;
	
  p = n; if (m<n) p=m;if (p>MAX_CHILDREN) p=MAX_CHILDREN;
	
	//  if(n==0 || m==0 || m!=n) return 0;
  
  for (j=0; j<=m; j++)
		for (i=0; i<=n; i++) DPS(i,j) = DP(i,j) =0;
	
  
	//printf("\nDPS(%d,%d)\n",n,m); fflush(stdout);
	//stampa_math(DPS,n,m); fflush(stdout);
	
  kernel_mat[0]=0;
  for (i=1; i<=n; i++)
		for (j=1; j<=m; j++)
			if(strcmp((*(Sx+i-1))->sName,(*(Sz+j-1))->sName)==0) 
			{
				DPS(i,j)=Delta_PT(*(Sx+i-1),*(Sz+j-1));
				kernel_mat[0]+=DPS(i,j);
			}
			else DPS(i,j)=0;
	
	
	//  printf("\n\nDPS(%d,%d)\n",n,m); fflush(stdout);
	//  stampa_math(DPS,n,m); fflush(stdout);
	//  printf("\n\nDP(%d,%d)\n",n,m);  fflush(stdout);
	//  stampa_math(DPS,n,m); fflush(stdout);
	//  printf("\n\nKernel: n=%d m=%d, %s %s \n\n",n,m,(*(Sx))->sName,(*(Sz))->sName);fflush(stdout);
	
	for(l=1;l<p;l++){
		kernel_mat[l]=0;
		for (j=0; j<=m; j++)DP(l-1,j)=0;
		for (i=0; i<=n; i++)DP(i,l-1)=0;
		
		for (i=l; i<=n; i++)
			for (j=l; j<=m; j++){
				DP(i,j) = DPS(i,j)+LAMBDA*DP(i-1,j)
				+ LAMBDA*DP(i,j-1)
				- LAMBDA2*DP(i-1,j-1);
				
				if(strcmp((*(Sx+i-1))->sName,(*(Sz+j-1))->sName)==0){
					DPS(i,j) = Delta_PT(*(Sx+i-1),*(Sz+j-1))* DP(i-1,j-1);
					kernel_mat[l] += DPS(i,j);
				}
				// else DPS[i][j] = 0;             
			}
		//      printf("\n----------------------------------\n"); printf("DPS i:%d, j:%d, l:%d\n",n,m,l+1);stampa_math(DPS,n,m);printf("DP\n");stampa_math(DP,n,m); 
	}
	//  K=kernel_mat[p-1];
	K=0;
	for(l=0;l<p;l++){K+=kernel_mat[l];
		//printf("String kernel of legnth %d: %1.7f \n\n",l+1,kernel_mat[l]);
	}
	
  
  free(kernel_mat);
  free(DPS);
  free(DP);
  
  return K;
}

#endif

// DELTA FUNCTION Moschitti's Partial Tree

double Delta_PT( TreeNode * Nx, TreeNode * Nz){
	double sum=0;
	
	if(delta_matrix[Nx->nodeID][Nz->nodeID]!=-1) 
	  return delta_matrix[Nx->nodeID][Nz->nodeID]; // already there
	
	if(strcmp(Nx->sName,Nz->sName)!=0)
		return (delta_matrix[Nx->nodeID][Nz->nodeID]=0);
	else if(Nx->iNoOfChildren==0 || Nz->iNoOfChildren==0)
		return (delta_matrix[Nx->nodeID][Nz->nodeID]=MU*LAMBDA2);
	else{
		
		//              printf("\n************************************************************\n");printf("\n\t\t\tSTRING KERNEL\n");printf("\n************************************************************\n");
		
		//              printf("\ntree 1: "); writeTreeString(Nx); printf("\ntree 2: "); writeTreeString(Nz); printf("\n\n");
		
		sum = MU*(LAMBDA2+Delta_SK(Nx->pChild, Nz->pChild,Nx->iNoOfChildren, Nz->iNoOfChildren)); 
		
		//             printf("\n (node1:%s node2:%s) -----------------------> %1.30f \n\n\n",Nx->sName,Nz->sName, sum);
		return (delta_matrix[Nx->nodeID][Nz->nodeID]=sum);
	}
	return 0;
}


double evaluate_PT(nodePair *pairs, int n){
	
	int i;
	double sum=0,contr;
	
	for(i=0;i<n;i++){
		//printf("\n------------------------------------------------\n");fflush(stdout);
		//printf("Considering pair (%s_%d , %s_%d):",pairs[i].Nx->sName,pairs[i].Nx->nodeID,pairs[i].Nz->sName,pairs[i].Nz->nodeID);fflush(stdout);
		//printf("\ntree 1: "); writeTreeString(pairs[i].Nx); printf("\ntree 2: "); writeTreeString(pairs[i].Nz); printf("\n");fflush(stdout);
		contr=Delta_PT(pairs[i].Nx,pairs[i].Nz)-REMOVE_LEAVES; // remove the contribution of leaves
																													 //printf("Score: %1.15f\n",contr); fflush(stdout);
		if(pairs[i].Nx->iNoOfChildren!=0 || pairs[i].Nz->iNoOfChildren!=0)contr-=REMOVE_LEAVES;
		
		// exit(-1);
		sum+=contr;
	}
	//printf("TOTAL %f\n\n\n", sum);
	return sum;	  
}

void determine_sub_lists(FOREST *a, FOREST *b, nodePair *intersect, int *n){
	
	int i=0,j=0,j_old,j_final;
	int n_a,n_b;
	short cfr;
	OrderedTreeNode *list_a, *list_b;
	
	n_a = a->listSize;
	n_b = b->listSize;
	list_a=a->orderedNodeSet; 
	list_b=b->orderedNodeSet;
	*n=0;
	
	/*  TEST
	 printf("\n\n\nLenghts %d %d\n",a->listSize , b->listSize);
	 printf("LIST1:\n");
	 for(i=0;i<a->listSize;i++) printf("%s\n",a->orderedNodeSet[i].sName);
	 printf("\n\n\nLIST2:\n");
	 for(i=0;i<b->listSize;i++) printf("%s\n",b->orderedNodeSet[i].sName);
	 i=0;
	 printf("Determining LISTS:\n");fflush(stdout);
	 */
	while(i<n_a && j<n_b){
		if((cfr=strcmp(list_a[i].sName,list_b[j].sName))>0)j++;
		else if(cfr<0)i++;
		else{
			j_old=j;
			do{
				do{
					intersect[*n].Nx=list_a[i].node;
					intersect[*n].Nz=list_b[j].node;
					(*n)++;
					if (*n>MAX_NUMBER_OF_PAIRS) { 
						printf ("ERROR: The number of identical parse nodes exceed the current capacityn\n"); 
						exit(-1);
					}
					delta_matrix[list_a[i].node->nodeID][list_b[j].node->nodeID]=-1.0;		    
					//  TEST           printf("Evaluating-Pair: (%s  ,  %s) i %d,j %d j_old%d \n",list_a[i].sName,list_b[j].sName,i,j,j_old);fflush(stdout);
					j++;
				}
				while(j<n_b && strcmp(list_a[i].sName,list_b[j].sName)==0);
				i++;j_final=j;j=j_old;
			} 		
			while(i<n_a && strcmp(list_a[i].sName,list_b[j].sName)==0);
			j=j_final;
		}
	}
	
	
	//printf ("number of pairs  %d \n",*n); 
	
}


//-------------------------------------------------------------------------------------------------------
// STRING/SEQUENCE KERNEL
//-------------------------------------------------------------------------------------------------------

double SK(TreeNode **Sx, TreeNode ** Sz, int n, int m){
  
  double *DPS =(double*) malloc((m+1)*(n+1)*sizeof(double));
  double *DP = (double*) malloc((m+1)*(n+1)*sizeof(double));
  double *kernel_mat = (double*) malloc((n+1)*sizeof(double));
	
	
  int i,j,l,p;
  double K;
  
  p = n; if (m<n) p=m;
  if(p>MAX_SUBSEQUENCE)p=MAX_SUBSEQUENCE;
	
  for (j=0; j<=m; j++)
		for (i=0; i<=n; i++) DPS(i,j) = DP(i,j) =0;
	
  
	//printf("\nDPS(%d,%d)\n",n,m); fflush(stdout);
	//stampa_math(DPS,n,m); fflush(stdout);
	
  kernel_mat[0]=0;
  for (i=1; i<=n; i++)
		for (j=1; j<=m; j++)
			if(strcmp((*(Sx+i-1))->sName,(*(Sz+j-1))->sName)==0) 
			{
				DPS(i,j)=1;
				kernel_mat[0]+=DPS(i,j);
			}
			else DPS(i,j)=0;
	
	
	//  printf("\n\nDPS(%d,%d)\n",n,m); fflush(stdout);
	//  stampa_math(DPS,n,m); fflush(stdout);
	//  printf("\n\nDP(%d,%d)\n",n,m);  fflush(stdout);
	//  stampa_math(DPS,n,m); fflush(stdout);
	//  printf("\n\nKernel: n=%d m=%d, %s %s \n\n",n,m,(*(Sx))->sName,(*(Sz))->sName);fflush(stdout);
	
	for(l=1;l<p;l++){
		kernel_mat[l]=0;
		for (j=0; j<=m; j++)DP(l-1,j)=0;
		for (i=0; i<=n; i++)DP(i,l-1)=0;
		
		for (i=l; i<=n; i++)
			for (j=l; j<=m; j++){
				DP(i,j) = DPS(i,j)+LAMBDA*DP(i-1,j)
				+ LAMBDA*DP(i,j-1)
				- LAMBDA2*DP(i-1,j-1);
				
				if(strcmp((*(Sx+i-1))->sName,(*(Sz+j-1))->sName)==0){
					DPS(i,j) = DP(i-1,j-1);
					kernel_mat[l] += DPS(i,j);
				}
				// else DPS[i][j] = 0;             
			}
		//      printf("\n----------------------------------\n"); printf("DPS i:%d, j:%d, l:%d\n",n,m,l+1);stampa_math(DPS,n,m);printf("DP\n");stampa_math(DP,n,m); 
	}
	//  K=kernel_mat[p-1];
	K=0;
	for(l=0;l<p;l++){K+=kernel_mat[l];
		//printf("String kernel of legnth %d: %1.7f \n\n",l+1,kernel_mat[l]);
	}
	
  
  free(kernel_mat);
  free(DPS);
  free(DP);
  
  return K;
}


double string_kernel_not_norm(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int i, int j){
	if(a->num_of_trees == 0 || b->num_of_trees == 0) return 0;
	else   
		if(a->num_of_trees <=i || b->num_of_trees<=j){
			printf("\nERROR: attempt to access to a not-defined item of the tree forest");
			if(a->num_of_trees <=i)
				printf("\n     : position %d of the tree forest of the first instance (line %d)\n\n",i,a->docnum+1);
			if(b->num_of_trees<=j)
				printf("\n     : position %d of the tree forest of the second instance (line %d)\n\n",j,b->docnum+1);
			fflush(stdout);
			exit(-1);
		}
		else if(a->forest_vec[i]->root==NULL || b->forest_vec[j]->root==NULL) return 0;
		else return SK(a->forest_vec[i]->root->pChild, b->forest_vec[j]->root->pChild, 
									 a->forest_vec[i]->root->iNoOfChildren, b->forest_vec[j]->root->iNoOfChildren);
}


double string_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int i, int j){
	double k;
	//printf("\ntree 1: "); writeTreeString(a->forest_vec[i]->root); printf("\ntree 2: "); 
	//writeTreeString(b->forest_vec[j]->root); printf("\n");
	//fflush(stdout);
	k=SK(a->forest_vec[i]->root->pChild, b->forest_vec[j]->root->pChild, 
			 a->forest_vec[i]->root->iNoOfChildren, b->forest_vec[j]->root->iNoOfChildren);
	//printf("STK:%f\n",k);
	
	return k;
}


//-------------------------------------------------------------------------------------------------------
// General Tree Kernel
//-------------------------------------------------------------------------------------------------------


double tree_kernel_not_norm(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int i, int j){
	
  int n_pairs=0;
  nodePair intersect[MAX_NUMBER_OF_PAIRS];
	
	if(a->forest_vec[i]->orderedNodeSet != NULL && b->forest_vec[j]->orderedNodeSet != NULL) 
		determine_sub_lists(a->forest_vec[i],b->forest_vec[j],intersect,&n_pairs);
	else if(kernel_parm->first_kernel!=6 && a->forest_vec[i]->root != NULL && 
					b->forest_vec[i]->root != NULL){ 
		// if trees are not empty, from empty orderedNodeList => they are sequences and should be run wtih kernel 6
		printf("\nERROR: Tree Kernels cannot be used over sequences (positions %d or %d) \n\n",i,j);fflush(stdout);
		exit(-1);
	} 
	
	/*
	printf("FIRST KERNEL: %d\n", kernel_parm->first_kernel);
	printf("CTYPE: %d\n", kernel_parm->combination_type);
	printf("PARAM VECT %d\n", PARAM_VECT);
	*/

	/*
	writeTreeString(a->forest_vec[i]->root);printf(">");fflush(stdout);
	writeTreeString(b->forest_vec[j]->root);printf(">");fflush(stdout);
	*/
	
	// kernel_parm->first_kernel = 3;

	switch(kernel_parm->first_kernel) {
			
		case  -1: if(TKGENERALITY > SUBSET_TREE_KERNEL){
			printf("\nERROR: SHALLOW SEMANTIC TK kernel (-F -1) cannot be used on trees of Generality higher than 1, i.e. the subset tree kernel \n\n",i,j);
			fflush(stdout); exit(-1);
		} 
			SIGMA = 1;return evaluate_SST_ACL07(intersect,n_pairs); // SSTK kernel ACL07
			
		case  0: if(TKGENERALITY > SUBSET_TREE_KERNEL){
			printf("\nERROR: ST kernel (-F 0) cannot be used on trees of Generality higher than 1, i.e. the subset tree kernel \n\n",i,j);
			fflush(stdout); exit(-1);
		} 
			SIGMA = 0;return evaluate_SST_ST(intersect,n_pairs); // ST kernel NISP2001 (Wisnathan and Smola)
			
		case  1: if(TKGENERALITY > SUBSET_TREE_KERNEL){
			printf("\nERROR: SST kernel (-F 1) cannot be used on trees of Generality higher than 1, i.e. the subset tree kernel \n\n",i,j);
			fflush(stdout); exit(-1);
		} 
			SIGMA = 1;return evaluate_SST_ST(intersect,n_pairs); // SST kernel (Collins and Duffy, 2002)
			
		case  2: return evaluate_GSST(intersect,n_pairs); // SST kenel + bow kernel on leaves, 
																											// i.e. SST until the leaves
		case  3: REMOVE_LEAVES=0;return evaluate_PT(intersect,n_pairs); // PT kernel
		case  4: REMOVE_LEAVES=MU*LAMBDA2;return evaluate_PT(intersect,n_pairs); // PT kernel no leaves
		case  6: return string_kernel(kernel_parm, a, b, i, j);
			
		default:  printf("\nERROR: Tree Kernel number %ld not available \n\n",kernel_parm->first_kernel);
			fflush(stdout);
			exit(-1);
	}
	
	return 0;
}

double tree_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b, int i, int j){
	if(a->num_of_trees == 0 || b->num_of_trees == 0) return 0;
	else   
			//check if we access an existing tree
		if(a->num_of_trees <=i || b->num_of_trees<=j){
			printf("\nERROR: attempt to access to a not-defined item of the tree forest");
			if(a->num_of_trees <=i)
				printf("\n     : position %d of the tree forest of the first instance (line %d)\n\n",i,a->docnum+1);
			if(b->num_of_trees<=j)
				printf("\n     : position %d of the tree forest of the second instance (line %d)\n\n",j,b->docnum+1);
			fflush(stdout);
			exit(-1);
		}       
		else if(a->forest_vec[i]->root==NULL || b->forest_vec[j]->root==NULL) return 0;
		else {
			double   k = tree_kernel_not_norm(kernel_parm, a, b, i, j)/
			sqrt(a->forest_vec[i]->twonorm_PT * b->forest_vec[j]->twonorm_PT);
			//					  printf("kernel %f\n",k);
			return k;
		}
	
	
}


/*-----------------------------------------------------------------------------------------------------*/

double basic_kernel_not_norm(KERNEL_PARM *kernel_parm, DOC *a, DOC *b, int i, int j) 
/* calculate the kernel function */
{
  switch(kernel_parm->second_kernel) {
    case 0: /* linear */ 
			return sprod_ss(a->vectors[i]->words,b->vectors[j]->words); 
    case 1: /* polynomial */
			return (double) pow(((double)kernel_parm->coef_lin)*(double)sprod_ss(a->vectors[i]->words,b->vectors[j]->words)
													+(double)kernel_parm->coef_const,(double) kernel_parm->poly_degree);
    case 2: /* radial basis function */
			return(exp(-kernel_parm->rbf_gamma*(a->vectors[i]->twonorm_sq-2*sprod_ss(a->vectors[i]->words,b->vectors[j]->words)+b->vectors[i]->twonorm_sq)));
    case 3: /* sigmoid neural net */
			return(tanh(kernel_parm->coef_lin*sprod_ss(a->vectors[i]->words,b->vectors[j]->words)+kernel_parm->coef_const)); 
    case 4: /* custom-kernel supplied in file kernel.h*/
			return(custom_kernel(kernel_parm,a,b));
			/* string kernel*/
    case 6: return string_kernel(kernel_parm,a,b,i,j);
    default: printf("Error: The kernel function to be combined with the Tree Kernel is unknown\n"); 
			fflush(stdout);
			exit(1);
  }
}


double basic_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b, int i, int j){
	if(a->num_of_vectors == 0 || b->num_of_vectors == 0) return 0;
	else   
		if(a->num_of_vectors <=i || b->num_of_vectors <= j){
			printf("\nERROR: attempt to access to a not-defined item of the vector set");
			if(a->num_of_vectors <=i)
				printf("\n     :position %d of the vector set of the first istance (line %d)\n\n",i,a->docnum+1);
			if(a->num_of_vectors <=j)
				printf("\n     :position %d of the vector set of the second instance (line %d)\n\n",j,b->docnum+1);
			fflush(stdout);
			exit(-1);
		}
		else if(a->vectors[i]==NULL || b->vectors[j]==NULL) return 0;
		else return basic_kernel_not_norm(kernel_parm, a, b,  i, j)/
			sqrt(a->vectors[i]->twonorm_STD*b->vectors[j]->twonorm_STD);
}

/*-----------------------------------------------------------------------------------------------------*/

void evaluateNorma(KERNEL_PARM * kernel_parm, DOC * d){
	
	int  i;
	double k=0;
	
	//printf("doc ID :%d \n",d->docnum);
	//printf("num of vectors:%d \n",d->num_of_vectors);
	//fflush(stdout);
	
	short kernel_type_tmp=kernel_parm->first_kernel; //save parameters from command line
	double lambda_tmp=LAMBDA; 
	double mu_tmp=MU;
	short TKG_tmp=TKGENERALITY;
	
	for(i=0;i < d->num_of_trees;i++){
		
		k=0;
		
		if (d->num_of_trees > i && d->forest_vec[i]->root!=NULL){
			
			/*  TESTS
			 
			 //printf("\n\n\ndoc ID :%ld \n",d->docnum);fflush(stdout);
			 
			 printf("node ID: %d \n", d->forest_vec[i]->root->nodeID); fflush(stdout);
			 
			 printf("node list length: %d\n", d->forest_vec[i]->listSize);fflush(stdout);
			 
			 printf("tree: <"); writeTreeString(d->forest_vec[i]->root);printf(">");fflush(stdout);
			 
			 printf("\n\n"); fflush(stdout);
			 */
			
			if(tree_kernel_params[i].kernel_type==END_OF_TREE_KERNELS)PARAM_VECT=0;
			
			if(PARAM_VECT==1){
				if (tree_kernel_params[i].kernel_type!=NOKERNEL){
					if(tree_kernel_params[i].normalization==1){
						kernel_parm->first_kernel=tree_kernel_params[i].kernel_type;
						LAMBDA = tree_kernel_params[i].lambda; 
						LAMBDA2 = LAMBDA*LAMBDA;
						MU=tree_kernel_params[i].mu;
						TKGENERALITY=tree_kernel_params[i].TKGENERALITY;
						d->forest_vec[i]->twonorm_PT = tree_kernel_params[i].weight*tree_kernel_not_norm(kernel_parm, d, d, i, i);
						if(d->forest_vec[i]->twonorm_PT==0) d->forest_vec[i]->twonorm_PT=1;
					}
					else d->forest_vec[i]->twonorm_PT=1;
				}
			}
			else{
				k = tree_kernel_not_norm(kernel_parm, d, d, i, i);
				if(k!=0 && (kernel_parm->normalization == 1 || kernel_parm->normalization == 3)) 
					d->forest_vec[i]->twonorm_PT=k; 
				else d->forest_vec[i]->twonorm_PT=1; 
			}
			
			// this avoids to check for norm == 0
			// printf ("Norm %f\n",k);fflush(stdout);
			
		}
		kernel_parm->first_kernel=kernel_type_tmp; //re-set command line parameters 
		LAMBDA=lambda_tmp; 
		MU=mu_tmp;
		TKGENERALITY=TKG_tmp;
	}
  
  
  /* SECOND KERNEL NORM EVALUATION */
	
	for(i=0;i < d->num_of_vectors;i++){
		
		if(d->num_of_vectors>0 && d->vectors[i]!=NULL){
			d->vectors[i]->twonorm_STD=1; // basic-kernel normalizes the standard kernels
																		// this also avoids to check for norm == 0
			k = basic_kernel_not_norm(kernel_parm, d, d, i, i);               
			d->vectors[i]->twonorm_sq=sprod_ss(d->vectors[i]->words,d->vectors[i]->words);
			if(k!=0 && (kernel_parm->normalization == 2 || kernel_parm->normalization == 3))
				d->vectors[i]->twonorm_STD=k; // if selected normalization is applied
																			//            printf ("Norm %f\n",k);
			
		}
		
	} 
	// maintain the compatibility with svm-light single linear vector 
	if(d->num_of_vectors>0 && d->vectors[0]!=NULL) d->twonorm_sq=sprod_ss(d->vectors[0]->words,d->vectors[0]->words);
	else d->twonorm_sq=0;
	
}


/***************************************************************************************/
/*                           KERNELS OVER SET OF KERNELS                               */
/***************************************************************************************/


// sequence summation of trees

double two_set_trees(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int memberA, int memberB){//all_vs_all vectorial kernel

  int i;
  int startA, startB;
  
  double k=0;
  
   startA= a->num_of_trees*memberA/2;
   startB= b->num_of_trees*memberB/2;
   
   if(a->num_of_trees==0 || b->num_of_trees==0) return 0;
   
   for(i=0; i< a->num_of_trees/2 && i< b->num_of_trees/2; i++)
//  for(i=0; i<1 && i< a->num_of_vectors/2 && i< b->num_of_vectors/2 ; i++)     
      if(a->forest_vec[i+startA]!=NULL && b->forest_vec[startB+i]!=NULL){
         k+= tree_kernel(kernel_parm, a, b, startA+i, startB+i);
      }
   return k;
}

double sequence_tree_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  int i;
  double k=0;
	for(i=0; i< a->num_of_trees && i< b->num_of_trees; i++){
		
    //printf("\n\n\n nodes: %d  %d\n", a->forest_vec[i]->root->nodeID,b->forest_vec[i]->root->nodeID);
		//    // printf("node list lenghts: %d  %d\n", a->forest_vec[i]->listSize,b->forest_vec[i]->listSize);
		//    printf("doc IDs :%ld %ld",a->docnum,b->docnum);
		//    printf("\ntree 1: "); writeTreeString(a->forest_vec[i]->root);
		//    printf("\ntree 2: "); writeTreeString(b->forest_vec[i]->root);
		//    
    k+= tree_kernel(kernel_parm, a, b, i, i);
		
		//   if(k>0) printf("Kernel :%0.20f norm %f\n",k,sqrt(a->forest_vec[i]->twonorm_PT * b->forest_vec[i]->twonorm_PT));
    
	}
	//   printf("Kernel :%f \n",k);
	
	return k;
}


// all vs all summation of trees


double AVA_tree_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){//all_vs_all_tree_kernel
	
  int i,j;
  double k=0;
	
	if (b->num_of_trees == 0 || a->num_of_trees==0) return 0;
	
	for(i=0; i< a->num_of_trees; i++)
		for(j=0; j< b->num_of_trees;j++)
			k+= tree_kernel(kernel_parm, a, b, i, j);
	
	return k;
}



// sequence summation of vectors


double sequence(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  int i;
  double k=0;
	
	for(i=0; i< a->num_of_vectors && i< b->num_of_vectors; i++)
		k+=basic_kernel(kernel_parm, a, b, i, i);
	
	return k;
}


// all vs all summation of vectors

double AVA(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  int i,j;
	
  double k=0;
	
	for(i=0; i< a->num_of_vectors; i++)
		for(j=0; j< b->num_of_vectors;j++)
			k+= basic_kernel(kernel_parm, a, b, i, j);
  return k;
}



// ranking algorithm based on only trees. It can be used for parse-tree re-ranking

double tree_kernel_ranking(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){//all_vs_all_tree_kernel

  double k=0;


        k =  two_set_trees(kernel_parm, a, b, 0, 0);
        k += two_set_trees(kernel_parm, a, b, 1, 1);
        k -= two_set_trees(kernel_parm, a, b, 1, 0);
        k -= two_set_trees(kernel_parm, a, b, 0, 1);

//        k =  tree_kernel(kernel_parm, a, b, 0, 0);
//        k += tree_kernel(kernel_parm, a, b, 1, 1);
//        k -= tree_kernel(kernel_parm, a, b, 1, 0);
//        k -= tree_kernel(kernel_parm, a, b, 0, 1);

   return k;
}


// ranking algorithm based on only vectors. For example, it can be used for ranking documents wrt a query


double vector_ranking(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  double k=0;
	
	k+=basic_kernel(kernel_parm, a, b, 0, 0);
	k+=basic_kernel(kernel_parm, a, b, 1, 1);
	k-=basic_kernel(kernel_parm, a, b, 0, 1);
	k-=basic_kernel(kernel_parm, a, b, 1, 0);
	return k;
}


double two_vector_set(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int memberA, int memberB){//all_vs_all vectorial kernel

  int i;
  int startA, startB;
  
  double k=0;
  
   startA= a->num_of_vectors*memberA/2;
   startB= b->num_of_vectors*memberB/2;
   
   if(a->num_of_vectors==0 || b->num_of_vectors==0) return 0;
   
   for(i=0; i< a->num_of_vectors/2 && i< b->num_of_vectors/2; i++)
//  for(i=0; i<1 && i< a->num_of_vectors/2 && i< b->num_of_vectors/2 ; i++)     
      if(a->vectors[i+startA]!=NULL && b->vectors[startB+i]!=NULL){
         k+= basic_kernel(kernel_parm, a, b, startA+i, startB+i);
      }
   return k;
}

// ranking algorithm based on tree forests. In this case the ranked objetcs are described by a forest

double vector_sequence_ranking(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){

  double k=0;

  k+=two_vector_set(kernel_parm, a, b, 0, 0); // ranking with sequences of vectors
  k+=two_vector_set(kernel_parm, a, b, 1, 1);
  k-=two_vector_set(kernel_parm, a, b, 0, 1);
  k-=two_vector_set(kernel_parm, a, b, 1, 0);

/*  
  k+=sequence_ranking(kernel_parm, a, b, 0, 0); // ranking with sequences of vectors
  k+=sequence_ranking(kernel_parm, a, b, 1, 1);
  k-=sequence_ranking(kernel_parm, a, b, 0, 1);
  k-=sequence_ranking(kernel_parm, a, b, 1, 0);
  */
  
  return k;
}


/* uses all the vectors in the vector set for ranking */
/* this means that there are n/2 vectors for the first pair and n/2 for the second pair */

double sequence_ranking(KERNEL_PARM * kernel_parm, DOC * a, DOC * b, int memberA, int memberB){//all_vs_all vectorial kernel
	
  int i;
  int startA, startB;
  
  double k=0;
  
	startA= a->num_of_vectors*memberA/2;
	startB= b->num_of_vectors*memberB/2;
	
	if(a->num_of_vectors==0 || b->num_of_vectors==0) return 0;
	
	//   for(i=0; i< a->num_of_vectors/2 && i< b->num_of_vectors/2; i++)
  for(i=0; i<1 && i< a->num_of_vectors/2 && i< b->num_of_vectors/2 ; i++)     
		if(a->vectors[i+startA]!=NULL && b->vectors[startB+i]!=NULL){
			k+= basic_kernel(kernel_parm, a, b, startA+i, startB+i);
		}
	return k;
}



/***************************************************************************************/
/*                                  KERNELS COMBINATIONS                               */
/***************************************************************************************/

// select the method to combine a forest of trees
// when will be available more kernel types, remeber to define a first_kernel option (e.g. -F)

double choose_tree_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b){
	/* calculate the kernel function */
	
  switch(kernel_parm->vectorial_approach_tree_kernel) {
			
    case 'S': /* TREE KERNEL Sequence k11+k22+k33+..+knn*/
			return sequence_tree_kernel(kernel_parm,a,b);; 
    case 'A': /* TREE KERNEL ALL-vs-ALL k11+k12+k13+..+k23+k33+..knn*/
			return(AVA_tree_kernel(kernel_parm,a,b));         	     
    case 'R': /* re-ranking kernel classic SST*/
			return((CFLOAT)tree_kernel_ranking(kernel_parm,a,b));
			//    case 7: /* TREE KERNEL MAX of ALL-vs-ALL */
			//            return(AVA_MAX_tree_kernel(kernel_parm,a,b));         	     
			//    case 8: /* TREE KERNEL MAX of sequence of pairs Zanzotto et all */
			//            return(AVA_MAX_tree_kernel_over_pairs(kernel_parm,a,b));         	     
    default: printf("Error: Unknown tree kernel function\n");  fflush(stdout);
			exit(1);
	}
}


// select the method to combine the set of vectors

double choose_second_kernel(KERNEL_PARM *kernel_parm, DOC *a, DOC *b) 
/* calculate the kernel function */
{
  switch(kernel_parm->vectorial_approach_standard_kernel) {
			
		case 'S':/* non structured KERNEL Sequence k11+k22+k33+..+knn*/
			return(sequence(kernel_parm,a,b)); 
		case 'A': /* Linear KERNEL ALL-vs-ALL k11+k12+k13+..+k23+k33+..knn*/
			return(AVA(kernel_parm,a,b));
		case 'R': /* re-ranking kernel*/
			return((CFLOAT)vector_sequence_ranking(kernel_parm,a,b));
			
			//    case 13: /* Linear KERNEL MAX of ALL-vs-ALL */
			//            return((CFLOAT)AVA_MAX(kernel_parm,a,b));         	     
			//    case 14: /* TREE KERNEL MAX of sequence of pairs Zanzotto et all */
			//            return((CFLOAT)AVA_MAX_over_pairs(kernel_parm,a,b));         	     
    default: printf("Error: Unknown kernel combination function\n"); 
			fflush(stdout); 
			exit(1);
	}
}


// select the data to be used in kenrels:
//            vectors, trees, their sum or their product

double advanced_kernels(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  double k1,
	k2;
	/* TEST
	 tmp = (k1*k2);
	 printf("K1 %f and K2= %f NORMA= %f norma.a= %f  norma.b= %f\n",k1,k2,norma,a->twonorm_sq,b->twonorm_sq);
	 printf("\nKernel Evaluation: %1.20f\n", tmp);
	 */
	
	switch(kernel_parm->combination_type) {
			
    case '+': /* sum first and second kernels*/
			k1 = choose_tree_kernel(kernel_parm, a, b);
			k2 = choose_second_kernel(kernel_parm, a, b);
			return k2 + kernel_parm->tree_constant*k1;
    case '*': /* multiply first and second kernels*/
			k1 = choose_tree_kernel(kernel_parm, a, b);
			k2 = choose_second_kernel(kernel_parm, a, b);
			return k1*k2;
    case 'T': /* only trees */
			return choose_tree_kernel(kernel_parm, a, b);
    case 'V': /* only vectors*/
			return choose_second_kernel(kernel_parm, a, b); 
			// otherwise evaluate the vectorial kernel on the basic kernels
    default: printf("Error: Unknown kernel combination\n"); 
			fflush(stdout);
			exit(1);
	}
}


// kernel for for question and answer classification [Moschitti et al., ACL 2007]
// BOW, POS, PAS, PT, PASN

double Question_Answer_Classification(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){//all_vs_all_tree_kernel
	
  int i,j;
  double k,k0,k1,k2,k3,k4,k5,k15,k16,k17,k20,k21,k22,Kall_all;
	
  k0=k1=k2=k3=k4=k5=k15=k16=k17=k20=k21=k22=Kall_all=0;
	
	//   printf("\n\nDocum %ld and %ld, size=(%d,%d)\n",a->docnum,b->docnum,a->num_of_trees,b->num_of_trees);
	
	//Q PT	
  k0 = tree_kernel(kernel_parm, a,b,0,0); 
	
	// Q BOW
  k1 = tree_kernel(kernel_parm, a,b,1,1); 
	
	//  Q POS
	//  k2 = tree_kernel(kernel_parm, a,b,2,2); 
	
	// Q PAS
	//  k5 = tree_kernel(kernel_parm, a,b,5,5); 
	
	//A PT
  k15 = tree_kernel(kernel_parm, a,b,15,15); 
	
	// A BOW
  k16 = tree_kernel(kernel_parm, a,b,16,16); 
	
	// A POS
	// k17 = tree_kernel(kernel_parm, a,b,17,17); 
	
	// A PAS 1, 2 and 3
	//	k20 = tree_kernel(kernel_parm, a,b,20,20); k21 = tree_kernel(kernel_parm, a,b,21,21); k22 = tree_kernel(kernel_parm, a,b,22,22);
	
	//KALL  
	for(i=20; i<23 && i < a->num_of_trees; i++)
		for(j=20; j<23 && j< b->num_of_trees; j++)
			Kall_all += tree_kernel(kernel_parm, a,b,i,j); 
	
	k=Kall_all*kernel_parm->tree_constant+k0+k1+k16+k15;	  // I removed the tree constant default was .3 ...how much was before did you use -T 1 ?... I hope 1
	
	//k = k0+k1+k16+k15;	
	
	//SUPER KERNEL
	// k = (k0+1)*(k15+1)+ (k1+1)*(k16+1) + k17;// + k20 ;
  return k;
}


// kernel for entailments [Zanzotto and Moschitti, ACL 2006]

double ACL2006_Entailment_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  int i;
  double k=0,
	max=0;
	
	if(b->num_of_trees > a->num_of_trees){
		if (b->num_of_trees > 5 && a->num_of_trees > 5){
			for(i=2;i+1<b->num_of_trees ;i+=2){
				k= tree_kernel(kernel_parm, a, b, 2, i);
				k+= tree_kernel(kernel_parm, a, b, 3, i+1);
				if(max<k)max=k;
			}
		}}
	else{
		if (a->num_of_trees > 5 && b->num_of_trees > 5){
			for(i=2;i+1<a->num_of_trees ;i+=2){
			  k= tree_kernel(kernel_parm, a, b, i, 2);
			  k+= tree_kernel(kernel_parm, a, b, i+1, 3); 
			  if(max<k)max=k;
			}
		}}
  
	//printf("\n---------------------------------------------------------------\n");fflush(stdout);
	//printf("\n\nKernel :%f \n",max);
	
  if(kernel_parm->combination_type=='+' && (a->vectors!=NULL && b->vectors!=NULL))
		return basic_kernel(kernel_parm, a, b, 0, 0)+
		kernel_parm->tree_constant*max;
  else return max;
}

/*
 double ACL2006_Entailment_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
 
 int i;
 double k=0,
 max=0;
 
 //	if (b->num_of_trees > 5 && a->num_of_trees>5){ // 2 normal treee, 2 placeholdered tree, and all the other assigment
 //		max = 0;
 if (b->num_of_trees > 5  && a->num_of_trees > 5){
 if(b->num_of_trees >  a->num_of_trees) {
 for(i=2;i+1<b->num_of_trees ;i+=2){
 k= tree_kernel(kernel_parm, a, b, 2, i);
 k+= tree_kernel(kernel_parm, a, b, 3, i+1);
 if(max<k)max=k;
 }
 }  else{
 for(i=2;i+1<a->num_of_trees ;i+=2){
 k= tree_kernel(kernel_parm, a, b, i, 2);
 k+= tree_kernel(kernel_parm, a, b, i+1, 3); 
 if(max<k)max=k;
 }
 }
 }
 //	}
 //printf("\n---------------------------------------------------------------\n");fflush(stdout);
 //printf("\n\nKernel :%f \n",max);
 
 if(kernel_parm->combination_type=='+' && (a->vectors!=NULL && b->vectors!=NULL))
 return basic_kernel(kernel_parm, a, b, 0, 0)+
 kernel_parm->tree_constant*max;
 else return max;
 }
 */

// kernel for entailments [Zanzotto and Moschitti, ACL 2006]

double ACL2008_Entailment_kernel(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  int i;
  double k=0,
	max=0;
	
	if(PARAM_VECT==0)
	{printf("\n\nERROR!!: This kernel requires external parameter file (option -U 1)\n\n"); exit(-1);}
	
	if (b->num_of_trees > 1 && a->num_of_trees>1){
		max = 0;
		if (b->num_of_trees > 6  && a->num_of_trees > 6){ 
			if(b->num_of_trees >  a->num_of_trees) {  
				for(i=6;i<b->num_of_trees ;i+=2){
					k= tree_kernel(kernel_parm, a, b, 6, i);
					k+= tree_kernel(kernel_parm, a, b, 7, i+1);
					if(max<k)max=k;
				}
			}  else{
				for(i=6;i<a->num_of_trees ;i+=2){
					k= tree_kernel(kernel_parm, a, b, i, 6);
					k+= tree_kernel(kernel_parm, a, b, i+1, 7);
					if(max<k)max=k;
				}
			}
		}
	}
	//printf("\n---------------------------------------------------------------\n");fflush(stdout);
	//printf("\n\nKernel :%f \n",max);
	
  if(kernel_parm->combination_type=='+' && (a->vectors!=NULL && b->vectors!=NULL)){
		double KK;
		int tmp1,tmp2;
		KK= basic_kernel(kernel_parm, a, b, 0, 0)+ kernel_parm->tree_constant*max;
		
		tmp1=kernel_parm->first_kernel;
		tmp2=TKGENERALITY;
		
		for(i=0;i<4;i++){
			kernel_parm->first_kernel=tree_kernel_params[i].kernel_type;
			LAMBDA = tree_kernel_params[i].lambda; 
			LAMBDA2 = LAMBDA*LAMBDA;
			MU=tree_kernel_params[i].mu;
			TKGENERALITY=tree_kernel_params[i].TKGENERALITY;
			KK+= 0.4*tree_kernel(kernel_parm, a, b, i, i);
		}			
		
		kernel_parm->first_kernel=tmp1;
		TKGENERALITY=tmp2;
		return KK;
  }
	
  else return max;
}


// Kernel for re-ranking predicate argument structures, [Moschitti, CoNLL 2006]

double SRL_re_ranking_CoNLL2006(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){//all_vs_all_tree_kernel
	
  double k1=0,k2=0;   
  
  if(kernel_parm->kernel_type==11 || kernel_parm->kernel_type==12){
		k1 = tree_kernel(kernel_parm, a, b, 0, 0);
		k1+= tree_kernel(kernel_parm, a, b, 1, 1);
		k1-= tree_kernel(kernel_parm, a, b, 1, 0);
		k1-= tree_kernel(kernel_parm, a, b, 0, 1);
	}
	k1*=kernel_parm->tree_constant;
	
  if(kernel_parm->kernel_type==13 || kernel_parm->kernel_type==12){
		k2+=sequence_ranking(kernel_parm, a, b, 0, 0);
		k2+=sequence_ranking(kernel_parm, a, b, 1, 1);
		k2-=sequence_ranking(kernel_parm, a, b, 0, 1);
		k2-=sequence_ranking(kernel_parm, a, b, 1, 0);
	}
	
	return  k1+k2;
}

double QA_true_reranking(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){//all_vs_all_tree_kernel
	
  double k1=0,k2=0;   
  
  if(kernel_parm->kernel_type==70 || kernel_parm->kernel_type==71){
		k1 = tree_kernel(kernel_parm, a, b, 1, 1);
		k1+= tree_kernel(kernel_parm, a, b, 2, 2);
		k1-= tree_kernel(kernel_parm, a, b, 1, 2);
		k1-= tree_kernel(kernel_parm, a, b, 2, 1);
		k1*= tree_kernel(kernel_parm, a, b, 0, 0);
	}
	k1*=kernel_parm->tree_constant;
	
  if(kernel_parm->kernel_type==71 || kernel_parm->kernel_type==72){
		k2+=sequence_ranking(kernel_parm, a, b, 0, 0);
		k2+=sequence_ranking(kernel_parm, a, b, 1, 1);
		k2-=sequence_ranking(kernel_parm, a, b, 0, 1);
		k2-=sequence_ranking(kernel_parm, a, b, 1, 0);
	}
	
	return  k1+k2;
}

//         k1 = tree_kernel(kernel_parm, a, b, T	, T	 );
//         k1+= tree_kernel(kernel_parm, a, b, T+1, T+1);
//         k1-= tree_kernel(kernel_parm, a, b, T+1, T	 );
//         k1-= tree_kernel(kernel_parm, a, b, T	, T+1);


// Kernel for re-ranking predicate argument structures, [Moschitti, ACL 2009 - attempt]

double SRL_re_ranking_ACL2009(KERNEL_PARM * kernel_parm, DOC * a, DOC * b){
	
  double k1=0,k2=0;   
  int T;
  
  T=atoi(kernel_parm->custom);
	
	kernel_parm->first_kernel=tree_kernel_params[T].kernel_type;
	LAMBDA = tree_kernel_params[T].lambda; 
	LAMBDA2 = LAMBDA*LAMBDA;
	MU=tree_kernel_params[T].mu;
	TKGENERALITY=tree_kernel_params[T].TKGENERALITY;
	
	
  if(kernel_parm->kernel_type==51 || kernel_parm->kernel_type==52){
		k1 = tree_kernel(kernel_parm, a, b, T	, T	 );
		k1+= tree_kernel(kernel_parm, a, b, T+3, T+3);
		k1-= tree_kernel(kernel_parm, a, b, T+3, T	 );
		k1-= tree_kernel(kernel_parm, a, b, T	, T+3);
	}
	
	k1*=kernel_parm->tree_constant;
	
  if(kernel_parm->kernel_type==53 || kernel_parm->kernel_type==52){
		k2+=basic_kernel(kernel_parm, a, b, 0, 0);
		k2+=basic_kernel(kernel_parm, a, b, 1, 1);
		k2-=basic_kernel(kernel_parm, a, b, 0, 1);
		k2-=basic_kernel(kernel_parm, a, b, 1, 0);
	}
	
	return  k1+k2+basic_kernel(kernel_parm, a, b, 2, 2);;
}


double ACL2008(KERNEL_PARM *kernel_parm, DOC *a, DOC *b) 
{
	
  int i,j;
  double k =0;
  
  for(i=0; i< 20; i++){
		if (tree_kernel_params[i].kernel_type!=NOKERNEL){
			if(tree_kernel_params[i].kernel_type==END_OF_TREE_KERNELS){
				printf("\nERROR: the parameter found in the parameter file/VECTOR are lower than");
				printf("\n       those required by the trees in the forest (only %d trees are fulfilled)\n\n",i);
				exit(-1);
			}
			
			kernel_parm->first_kernel=tree_kernel_params[i].kernel_type;
			LAMBDA = tree_kernel_params[i].lambda; 
			LAMBDA2 = LAMBDA*LAMBDA;
			MU=tree_kernel_params[i].mu;
			TKGENERALITY=tree_kernel_params[i].TKGENERALITY;
			
			k += tree_kernel_params[i].weight*tree_kernel(kernel_parm, a, b, i, i); 
			//printf("kernel %f\n",k);
    }
  }
	
  i=20;kernel_parm->first_kernel=tree_kernel_params[i].kernel_type;
  LAMBDA = tree_kernel_params[i].lambda; 
  LAMBDA2 = LAMBDA*LAMBDA;
  MU=tree_kernel_params[i].mu;
  TKGENERALITY=tree_kernel_params[i].TKGENERALITY;
  
  for(i=20; i<23 && i < a->num_of_trees; i++)
		for(j=20; j<23 && j< b->num_of_trees; j++)
			if (tree_kernel_params[i].kernel_type!=NOKERNEL||tree_kernel_params[j].kernel_type!=NOKERNEL)
				k += tree_kernel(kernel_parm, a,b,i,j)*kernel_parm->tree_constant;
	
  
  return k;
}


/***************************************************************************************/
//                            JHON HOPKINS 2007 KERNELS
/***************************************************************************************/

double JHU_KERNELS(KERNEL_PARM *kernel_parm, DOC *a, DOC *b) 
/* calculate the kernel function */
{
	double Poly,TK0,TK1,TK2,TK3,TK4,SK3,SK5,SK7,SK9;
	
  Poly=TK0=TK1=TK2=TK3=TK4=SK3=SK5=SK7=SK9=0;
  
  switch(atoi(kernel_parm->custom)) {
			
    case -1:Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return Poly;
    case 0: TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK0*Poly;
    case 1: TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK0+Poly;
    case 2: TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK0*Poly+Poly;
    case 3: TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK0+Poly+TK1;
    case 4: TK2=tree_kernel(kernel_parm, a, b, 2, 2);
			TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK0+Poly+TK1+TK2;
    case 5: TK2=tree_kernel(kernel_parm, a, b, 2, 2);
			TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return Poly*(TK0+TK1+TK2+1);            
    case 6: TK2=tree_kernel(kernel_parm, a, b, 2, 2);
			TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return Poly*(TK0+1)+TK1+TK2;
    case 7: TK2=tree_kernel(kernel_parm, a, b, 2, 2);
			TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return Poly*(TK0+1)+TK1*TK2;
    case 8: TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK1*Poly+Poly;
    case 9: TK2=tree_kernel(kernel_parm, a, b, 2, 2);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK2*Poly+Poly;
    case 10:TK3=tree_kernel(kernel_parm, a, b, 3, 3);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK3*Poly+Poly;
    case 11:TK4=tree_kernel(kernel_parm, a, b, 4, 4);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return TK4*Poly+Poly;
    case 12:TK3=tree_kernel(kernel_parm, a, b, 3, 3);
			TK4=tree_kernel(kernel_parm, a, b, 4, 4);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return (TK3+TK4)*Poly+Poly;
    case 13:TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			TK1=tree_kernel(kernel_parm, a, b, 1, 1);    
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return (TK0+TK1)*kernel_parm->tree_constant+Poly;
    case 14: kernel_parm->first_kernel=1;
			TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			TK2=tree_kernel(kernel_parm, a, b, 2, 2);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return (TK0+TK1+TK2)*kernel_parm->tree_constant+Poly;
    case 15: kernel_parm->first_kernel=1;
			TK0=tree_kernel(kernel_parm, a, b, 0, 0);
			TK1=tree_kernel(kernel_parm, a, b, 1, 1);
			TK2=tree_kernel(kernel_parm, a, b, 2, 2);
			TK3=tree_kernel(kernel_parm, a, b, 3, 3);
			Poly=basic_kernel(kernel_parm, a, b, 0, 0);
			return (TK0+TK1+TK2+TK3)*kernel_parm->tree_constant+Poly;
    case 16: kernel_parm->first_kernel=6;
			SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			//  SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			//  SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
																									//  TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return SK3+(TK0+1)*Poly;
    case 17: kernel_parm->first_kernel=6;
			SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			//SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			//SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
			TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return SK3+(TK0+TK1+1)*Poly;
    case 18: kernel_parm->first_kernel=6;
			SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			//SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
			TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return (SK3+SK7+TK0+TK1+1)*Poly;
    case 19: kernel_parm->first_kernel=6;
			SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
			TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return (SK3+SK5+SK7+TK0+TK1+1)*Poly;
    case 21: kernel_parm->first_kernel=6;
			//  SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			//  SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
																									//  TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return SK5+(TK0+1)*Poly;
    case 22: kernel_parm->first_kernel=6;
			//  SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			//  SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
																									//  TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return (SK5+TK0+1)*Poly;
    case 23: kernel_parm->first_kernel=6;
			SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			//SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			//SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
			TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return (TK0+SK3+1)*Poly;
    case 24: kernel_parm->first_kernel=6;
			SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			//  SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			//  SK7= tree_kernel(kernel_parm, a, b, 7, 7)+tree_kernel(kernel_parm, a, b, 8, 8);
			kernel_parm->first_kernel=1;
			TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
																									//  TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return SK3+(TK0+1)*Poly;
    case 25: 
			//kernel_parm->first_kernel=6;
			//  SK3= tree_kernel(kernel_parm, a, b, 3, 3)+tree_kernel(kernel_parm, a, b, 4, 4);
			// SK5= tree_kernel(kernel_parm, a, b, 5, 5)+tree_kernel(kernel_parm, a, b, 6, 6);
			//             kernel_parm->first_kernel=4;
			SK9= tree_kernel(kernel_parm, a, a, 11, 12)*tree_kernel(kernel_parm, b, b, 11, 12);
			//kernel_parm->first_kernel=1;
			// TK0 = tree_kernel(kernel_parm, a, b, 0, 0); // Evaluate tree kernel between the two i-th trees.
			//  TK1 = tree_kernel(kernel_parm, a, b, 1, 1)+tree_kernel(kernel_parm, a, b, 2, 2);   
			Poly = basic_kernel(kernel_parm, a, b, 0, 0); // Compute standard kernel (selected according to the "second_kernel" parameter).
			return (SK9+1)*Poly;//(SK9+TK0+SK5+1)*Poly;
		default: printf("Error: Unknown tree kernel function\n");  fflush(stdout);
			exit(1);
			
	}
}


