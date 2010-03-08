package gov.lanl.yadas;

import java.util.*;

/**
 * A simple Perturber that creates a Metropolis step which adds a 
 * common random number to all the elements of all the parameters 
 * being perturbed.  
 */
public class FasterAddCommonPerturber implements TunablePerturber {

    // double mss

    public FasterAddCommonPerturber (double mss) {
	this.mss = mss;
    }
				     
    public void perturb (double[][] candarray, int whoseTurn) {
	double temp = mss * rand.nextGaussian();
	for (int j = 0; j < candarray.length; j++) {
	    for (int k = 0; k < candarray[j].length; k++) {
		candarray[j][k] += temp;
	    }
	}
    }


    public int numTurns () {
	return 1;
    }

    public double jacobian () {
	return 1.0;
    }

    public double[] getStepSizes () {
	double[] tem = new double[1];
	tem[0] = mss;
	return tem;
    }
    public void setStepSize (double s, int i) {
	mss = s;
    }
    public void setStepSizes (double[] s) {
	mss = s[0];
    }

    private double mss;

    static Random rand = new Random(System.currentTimeMillis());
}
