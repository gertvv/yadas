package gov.lanl.yadas;
import java.util.*;

/**
 * Important general update step, often used in analysis-of-variance type situations.  
 * Each move proposed by this update proposes Gaussian steps to a set of parameters, 
 * with the sum of these Gaussian random variables equal to zero (used when, for 
 * example, the data are quite informative about the sum, but information about 
 * the pieces that make this sum is much more sparse.)  
 */
public class SumPreservingPerturber implements TunablePerturber {

    /**
     * Default constructor: operates on an array of parameters.  Defines a single 
     * update step in which each element of a given parameter gets the same 
     * perturbation.  
     */
    public SumPreservingPerturber
	(MCMCParameter[] params, double[] mss) {
	int d = params.length;
	numrvs = d;
	stepindex = new int[d][];
	rvindex = new int[d][];
	for (int i = 0; i < d; i++) {
	    int ni = params[i].length();
	    stepindex[i] = new int[ni];
	    rvindex[i] = new int[ni];
	    for (int j = 0; j < ni; j++) {
		stepindex[i][j] = 0;
		rvindex[i][j] = i;
	    }
	}
	this.mss = mss;	
    }

    /**
     * The number of update steps is one more than the largest integer inside the 
     * stepindex array.  When we are attempting the i'th update step, all parameters 
     * with stepindex == i with be moved, and the index of the Gaussian random variable 
     * is given by the rvindex array.  
     */
    public SumPreservingPerturber 
	(int numrvs, int[][] stepindex, int[][] rvindex, double[] mss) {
	this.numrvs = numrvs;	
	this.stepindex = stepindex;
	this.rvindex = rvindex;
	this.mss = mss;
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	double[] normals = new double[numrvs];
	for (int i = 0; i < numrvs; i++) {
	    normals[i] = mss[whoseTurn] * rand.nextGaussian();
	}
	double temp = Tools.mean(normals);
	for (int i = 0; i < numrvs; i++) {
	    normals[i] -= temp; 
	} // now they sum to zero
	for (int j = 0; j < candarray.length; j++) {
	    for (int k = 0; k < candarray[j].length; k++) {
		if (stepindex[j][k] == whoseTurn) {
		    candarray[j][k] += normals[rvindex[j][k]];
		}
	    }
	}
    }

    public int numTurns () {
	return mss.length;
    }

    public double jacobian () {
	return 1.0;
    }
    public double[] getStepSizes () {
	return mss;
    }
    public void setStepSize (double s, int i) {
	mss[i] = s;
    }
    public void setStepSizes (double[] s){
	mss = s;
    }

    private int numrvs;
    private int[][] stepindex, rvindex;
    private double[] mss;

    static Random rand = new Random(System.currentTimeMillis());
}
