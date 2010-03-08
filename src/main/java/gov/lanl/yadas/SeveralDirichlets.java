package gov.lanl.yadas;

/**
 * Class for defining a collection of probability vectors, where each vector has its 
 * own Dirichlet distribution.  When using this class, you will most likely want to 
 * include a GroupedSumToOnePerturber in the algorithm.  The arguments are the vector 
 * of all the probabilities and the vector of all their exponents.  
 */
public class SeveralDirichlets implements Likelihood {
    
    // current interface: vector of labels that means the following: 
    // all of the p's that have label == i, sum to one.  
    /**
     * The constructor asks for a vector of labels: the elements should be integers from 
     * 0 to one less than the number of probability vectors, and where the intent is 
     * that all the probabilities corresponding to labels==i should sum to one, for each 
     * i.  
     */
    public SeveralDirichlets (int[] labels) {
	this.labels = labels;
	N = 1 + max(labels);
	labelmat = new int[N][];
	for (int i = 0; i < N; i++) {
	    int ct = 0;
	    for (int j = 0; j < labels.length; j++) {
		if (labels[j] == i) ct++;
	    }
	    labelmat[i] = new int[ct];
	    for (int j = 0; j < labels.length; j++) {
		if (labels[j] == i) labelmat[i][labelmat[i].length - ct--] = j;
	    }
	}
	/*
	  for (int i = 0; i < labelmat.length; i++) {
	  for (int j = 0; j < labelmat[i].length; j++) {
	  System.out.println(i + "," + j + "," + labelmat[i][j]);
	  }
	  }
	*/
    }
    
    // two arguments: the probabilities and the exponents in the "prior"
    
    public double compute (double[][] args) {
	double[] probs = args[0];
	double[] exponents = args[1];
	double out = 0;
	for (int n = 0; n < N; n++) {
	    double sumnu = 0;
	    for (int i = 0; i < labelmat[n].length; i++) {
		sumnu += exponents[labelmat[n][i]];
	    }	
	    out += Tools.loggamma(sumnu);
	    for (int i = 0; i < labelmat[n].length; i++) {
		int ii = labelmat[n][i];
		out -= Tools.loggamma(exponents[ii]);
		/*
		  if (probs[ii] < minn) {
		  minn = probs[ii];
		  System.out.println(n + "," + i + "," + ii + "," + probs[ii]);
		  }
		*/
		if (exponents[ii] > 0) {
		    out += (exponents[ii] - 1) * Math.log(probs[ii]);
		}
	    }
	}
	//System.out.println("out = " + out);
	return out;
    }
    
    public static double[] rep ( double r, int n ) {
	double[] out = new double[n];
	for (int i = 0; i < n; i++) {
	    out[i] = r;
	}
	return out;
    }
    
    public static int max (int[] vec) {
	int out = vec[0];
	for (int i = 1; i < vec.length; i++) {
	    out = Math.max(out, vec[i]);
	}
	return out;
    } 
    
    public static void main (String[] args) {
    }
    
    private int[] labels;
    private int N;
    private int[][] labelmat;
    double minn = 1.0;
}
