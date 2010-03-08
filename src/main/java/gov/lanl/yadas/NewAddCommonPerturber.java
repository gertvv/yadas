package gov.lanl.yadas;

import java.util.*;

/**
 * Perturber that allows users to construct a Metropolis proposal that adds 
 * the same random number to many parameters simultaneously.  
 * @see gov.lanl.yadas.MultipleParameterUpdate
 */
public class NewAddCommonPerturber implements TunablePerturber {

    /**
     * @param expandmat contains integers beginning at zero and working 
     * up to one less than the number of different updates.  expandmat 
     * has the same shape as the two-dimensional array of parameters.  
     * The first update adds the random Gaussian to every parameter 
     * component that corresponds to a zero in expandmat.  
     */
    public NewAddCommonPerturber (int[][] expandmat, double[] mss) {
	this.expandmat = expandmat;
	this.mss = mss;
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	Integer wt = new Integer(whoseTurn);
	double temp = mss[whoseTurn] * rand.nextGaussian();
	int where = 0;
	for (int j = 0; j < candarray.length; j++) {
	    for (int k = 0; k < candarray[j].length; k++) {
		if (expandmat[j][k] == whoseTurn) {
		    candarray[j][k] += temp;
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

    static Random rand = new Random(System.currentTimeMillis());
}
