package gov.lanl.yadas;
import java.util.*;

// adapted from NewAddCommonPerturber.  General solution would be good.  

/**
 * Allows users to create a Metropolis proposal in which a random 
 * Gaussian is added to one set of parameters, and the same random 
 * Gaussian is subtracted from another set.  Useful in hierarchical 
 * models which are close to having identifiability issues.   Written 
 * so that only two MCMCParameters are involved, and the random Gaussian 
 * will be added to some components of the first Parameter and subtracted 
 * from some components of the second.  
 */
public class NewOneUpOneDownPerturber implements TunablePerturber {

    /**
     * The elements of expandmat are integers from zero up to 
     * one less than the number of updates this Perturber defines.  
     * Call the first parameter (whose values are stored in 
     * candarray[0]) alpha, and the second parameter (candarray[1]) 
     * theta.  For the i'th update, we let Z ~ N(0,1), add 
     * mss[i] * Z to the alphas for which expandmat[0] equals i, 
     * and subtract mss[i] * Z from the thetas for which expandmat[1] 
     * equals i.  
     */
    public NewOneUpOneDownPerturber (int[][] expandmat, double[] mss) {
	this.expandmat = expandmat;
	this.mss = mss;
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	Integer wt = new Integer(whoseTurn);
	double temp = mss[whoseTurn] * rand.nextGaussian();
	int where = 0;
	int j = 0;
	for (int k = 0; k < candarray[j].length; k++) {
	    if (expandmat[j][k] == whoseTurn) {
		candarray[j][k] += temp;
	    }
	}
	j = 1;
	for (int k = 0; k < candarray[j].length; k++) {
	    if (expandmat[j][k] == whoseTurn) {
		candarray[j][k] -= temp;
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
	double[] tem = new double[mss.length];
	System.arraycopy (mss, 0, tem, 0, mss.length);
	return tem;
    }
    public void setStepSize (double s, int i) {
	mss[i] = s;
    }
    public void setStepSizes (double[] s) {
	System.arraycopy (s, 0, mss, 0, s.length);
    }

    private int[][] expandmat;
    private double[] mss;

    static Random rand = new Random(System.currentTimeMillis());
}
