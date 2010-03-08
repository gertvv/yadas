package gov.lanl.yadas;

import java.util.*;

/**
 * Perturber used when a variability parameter can get stuck at low values due to 
 * many deviations also attaining small absolute values.  The prototypical example 
 * is if we have parameters y, mu, and sigma, where the y's are normally distributed 
 * with means given by the mu and SD's given by the sigma's.  
 * There is one proposed move for each sigma, and the proposed move corresponding to 
 * a given sigma multiplies that sigma by a lognormal random variable, and changes the 
 * y's so that their distance from the appropriate mu is multiplied by the same 
 * lognormal random variable.  
 */
public class GroupedSDPerturber implements TunablePerturber {

    /**
     */
    public GroupedSDPerturber (int[][] expandmat, double[] mss) {
	this.expandmat = expandmat;
	this.mss = mss;
	// preprocess
	int n2 = 1+(int)(max(expandmat[2]));
	int n0 = 1+(int)(max(expandmat[0]));
	int[][] mat = new int[n2][n0];
	int[][] mumat = new int[n2][n0];
	for (int i = 0; i < n2; i++) {
	    for (int j = 0; j < n0; j++) {
		mat[i][j] = 0;
	    }
	}
	for (int i = 0; i < expandmat[2].length; i++) {
	    mat[expandmat[2][i]][expandmat[0][i]] = 1;
	    mumat[expandmat[2][i]][expandmat[0][i]] = expandmat[1][i];
	}
	translator = new int[n2][];
	mutrans = new int[n2][];
	for (int i = 0; i < n2; i++) {
	    translator[i] = new int[sum(mat[i])];
	    mutrans[i] = new int[translator[i].length];
	    int k = 0;
	    for (int j = 0; j < n0; j++) {
		if (mat[i][j] > 0) {
		    translator[i][k] = j;
		    mutrans[i][k++] = mumat[i][j];
		}
	    }
	}
    }

    // stolen from Fred Swartz: obvious algorithm; 
    // need to write smarter one anyway
    public static int max(int[] t) {
	int maximum = t[0];   // start with the first value
	for (int i=1; i<t.length; i++) {
	    if (t[i] > maximum) {
		maximum = t[i];   // new maximum
	    }
	}
	return maximum;
    }

    int sum (int[] vec) {
	int out = 0;
	for (int i = 0; i < vec.length; i++) {
	    out += vec[i];
	}
	return out;
    }


    /* assumptions:
       parameter 0 ~ N ( parameter 1, (parameter 2)^2) or similar.
       call them theta ~ mu, sigma  
     */

    public void perturb (double[][] candarray, int whoseTurn) {
	double temp = Math.exp(mss[whoseTurn] * rand.nextGaussian());
	scale = Math.pow(temp, 1.0 + translator[whoseTurn].length);
	candarray[2][whoseTurn] *= temp;
	for (int i = 0; i < translator[whoseTurn].length; i++) {
	    double mu = candarray[1][mutrans[whoseTurn][i]];
	    candarray[0][translator[whoseTurn][i]] = 
		mu + temp * (candarray[0][translator[whoseTurn][i]] - mu);
	}
    }

    public int numTurns () {
	return mss.length;
    }

    public double jacobian () {
	return scale;
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
    double[] mss;
    double scale;
    int[][] translator, mutrans;

    static Random rand = new Random(System.currentTimeMillis());
}
