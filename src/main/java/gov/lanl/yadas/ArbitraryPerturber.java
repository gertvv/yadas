package gov.lanl.yadas;

import java.util.*;

/** 
 * ArbitraryPerturber: allows user to specify one or more arbitrary 
 * directions in which to move several parameters.  Originally conceived 
 * to be used in conjunction with principal component analysis of 
 * MCMC iterations, hence the name 'loadings' for one of the arguments.
 * @see gov.lanl.yadas.MultipleParameterUpdate
 * @author TLG
 */
public class ArbitraryPerturber implements TunablePerturber {

    /**
     * @param mss is an array of positive numbers to multiply by the 
     * random numbers and the loadings.  The length of this array 
     * determines how many Metropolis steps this update defines.  
     * @param loadings is a two-dimensional array; its second 
     * dimension is the same size as mss.  Its first dimension is 
     * the sum of the lengths of the parameters that are being updated 
     * as part of this update.  For example, suppose that the parameters 
     * being updated are alpha (of length 2) and theta (of length 1).  
     * Suppose loadings = {{1, 1, -2}, {1, -1, 0}} and mss = {0.5, 1.5}.  
     * Then the first Metropolis move samples Z1 ~ N(0,1) and proposes 
     * alpha[0] <- alpha[0] + 0.5 * 1 * Z1, 
     * alpha[1] <- alpha[1] + 0.5 * 1 * Z1, 
     * theta <- theta + 0.5 * (-2) * Z1, while the second move 
     * samples Z2 ~ N(0,1) and proposes
     * alpha[0] <- alpha[0] + 1.5 * 1 * Z2, 
     * alpha[1] <- alpha[1] + 1.5 * -1 * Z2, 
     * theta <- theta + 1.5 * 0 * Z2 (theta is left fixed).  
     */
    public ArbitraryPerturber (double[][] loadings, double[] mss) {
	num = Math.min(loadings.length, mss.length);
	this.loadings = loadings;
	this.mss = mss;
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	int counter = 0;
	double step = mss[whoseTurn] * rand.nextGaussian();
	for (int i = 0; i < candarray.length; i++) {
	    for (int j = 0; j < candarray[i].length; j++) {
		candarray[i][j] += step * loadings[whoseTurn][counter++];
	    }
	}
    }
    
    public int numTurns () {
	return num;
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

    private int num;
    private double[][] loadings;
    private double[] mss;
    private static Random rand = new Random();
}

/* 
   This class is intended to work with principal component analysis 
   of the estimated covariance matrix of the parameters.  
*/
