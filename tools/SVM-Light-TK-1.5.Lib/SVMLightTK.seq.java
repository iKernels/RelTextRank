package svmlighttk;

import java.io.*;
import java.util.*;

class SVMLightTK
{
    static {
	System.load("/home/noname/workspace/NLPIR/tools/SVM-Light-TK-1.5.Lib/svmlight_tk.so");
    }
    final private int modelHandle;
 
    public SVMLightTK(String modelFile)
    {
	modelHandle=load_model(modelFile);
    }
    
    public double classify(String instance)
    {
	return classify_instance(modelHandle, instance);
    }

    private static native int load_model(String modelFile);
    private static native double classify_instance(int modelNumber, String instance);
    public static void main(String[] args)
    {
        System.out.println("loading models\n");
	String test_input="-1      |BT| (ROOT (SBARQ (WHNP (WP What))(SQ (VBZ is)(NP (DT the)(NN abbreviation))(PP (IN for)(NP (NNP Texas))))(. ?))) |ET|";
	SVMLightTK[] models=new SVMLightTK[3];
	
	models[0]=new SVMLightTK("/home/noname/workspace/NLPIR/data/questionclassifier/models/ABBR.model");
        //models[1]=new SVMLightTK("/home/noname/workspace/NLPIR/data/Answerbag/modified/60kmodels/answerbag.poschunklemmafocus.10k.model");
        System.out.println("loaded\n");	
	
	String line;

        try
        {
	    BufferedReader in1 = new BufferedReader(new FileReader("/home/noname/workspace/NLPIR/data/questionclassifier/ABBR_test.dat"));

            if (!in1.ready())
                throw new IOException();

            while ((line = in1.readLine()) != null) {
			System.out.println("SCORE model question: "+
			      models[0].classify("q" + line));
                	//System.out.println("SCORE model pairs: "+
			  //    models[1].classify(line));
	    }
            in1.close();
        }
	catch (IOException e)
        {
            System.out.println(e);
        }
	

        models[1]=new SVMLightTK("/home/noname/workspace/NLPIR/data/Answerbag/modified/60kmodels/answerbag.poschunklemmafocus.10k.model");
        System.out.println("loaded\n");	

        try
        {
            BufferedReader in2 = new BufferedReader(new FileReader("/home/noname/workspace/NLPIR/data/Answerbag/modified/60kmodels/test"));

            if ( !in2.ready())
                throw new IOException();

            while ((line = in2.readLine()) != null) {
			System.out.println("SCORE model reranker: "+
			      models[1].classify(line));
                	//System.out.println("SCORE model pairs: "+
			  //    models[1].classify(line));
	    }
	    in2.close();
        }		
        catch (IOException e)
        {
            System.out.println(e);
        }
	
/*
	for(int i=0;i<3;i++){
	    System.out.println("SCORE model "+i+": "+
			      models[i].classify(test_input));
	}
*/

    }
}
