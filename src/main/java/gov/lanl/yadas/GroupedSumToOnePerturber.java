package gov.lanl.yadas;

import java.util.*;

/**
 * Used to update parameters that consist of several subvectors, each of whose 
 * elements sum to one.  There is one update step for each element of the parameter.  
 * Each update step proposes to add a Gaussian amount to that element on the log 
 * scale, and proposes to rescale the remaining elements in that subvector so that 
 * they will still sum to one.  This update algorithm is typically used with the 
 * SeveralDirichlets distribution.  
 */
public class GroupedSumToOnePerturber implements TunablePerturber {
    
    /**
     */
    public GroupedSumToOnePerturber (double[] mss, int[] labels) {
	this.mss = mss;
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
    }
    
    public void perturb (double[][] candarray, int whoseTurn) {
	double newone, oldone;
	whichlabel = labels[whoseTurn];
	oldone = candarray[0][whoseTurn];
	newone = 2.0;
	//while ((newone <= 0.) || (newone >= 1.)) { 
	scale = Math.exp(mss[whoseTurn] * rand.nextGaussian());
	newone = 1. / (1. + (1.0/scale) * (1. - candarray[0][whoseTurn]) / 
		       candarray[0][whoseTurn]);
	//}
	candarray[0][whoseTurn] = newone;
	for (int k = 0; k < labelmat[whichlabel].length; k++) {
	    int kk = labelmat[whichlabel][k];
	    if (kk != whoseTurn) {
		candarray[0][kk] = candarray[0][kk] * (1. - newone) / (1. - oldone);
	    }
	}
	double summ = somesum(candarray[0], labelmat[whichlabel]);
	for (int j = 0; j < labelmat[whichlabel].length; j++) {
	    candarray[0][labelmat[whichlabel][j]] /= summ;
	}
	int jj = whoseTurn;
	adj = candarray[0][jj] / oldone * 
	    Math.pow ((1. - candarray[0][jj]) / (1. - oldone), 
		      labelmat[whichlabel].length - 1.);
    }
    
    public static double sum (double[] arr) {
	double out = 0.0;
	for (int i = 0; i < arr.length; i++) {
	    out += arr[i];
	}
	return out;
    }
    
    public static double somesum (double[] arr, int[] subset) {
	double out = 0.0;
	for (int i = 0; i < subset.length; i++) {
	    out += arr[subset[i]];
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
    
    public int numTurns () {
	return mss.length;
    }
    
    public double jacobian () {
	return adj;
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
    
    double[] mss;
    double adj = 1.0;
    double scale;
    private int[] labels;
    private int N, whichlabel;
    private int[][] labelmat;
    
    static Random rand = new Random(System.currentTimeMillis());
}
