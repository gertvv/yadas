package gov.lanl.yadas;

import java.util.*;

/**
 * Used for defining Metropolis-Hastings steps in which a single vector 
 * of parameters "theta" is rescaled by multiplying each element's 
 * distance from their mean by a lognormal with standard deviation mss.  
 * Simultaneously, a second scalar parameter is multiplied by the 
 * same lognormal.  
 * @see gov.lanl.yadas.MultipleParameterUpdate
 */
public class ScalePerturber implements TunablePerturber {

    /**
     * @param mss is the standard deviation of the lognormal random 
     * variables that rescale the parameters.  
     */
    public ScalePerturber (double mss) {
	this.mss = mss;
    }

    public double mean(double[] vec) {
	double out = 0.0;
	for (int i = 0; i < vec.length; i++) {
	    out += vec[i];
	}
	return out / vec.length;
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	double temp = Math.exp(mss * rand.nextGaussian());
	scale = Math.pow(temp, (candarray[0].length + 1));
	//System.out.println("scale multiplier = " + scale);
	double mien = mean(candarray[0]);
	candarray[1][0] *= temp;
	for (int i = 0; i < candarray[0].length; i++) {
	    candarray[0][i] = mien + (candarray[0][i] - mien) * temp;
	}
    }

    public int numTurns () {
	return 1;
    }

    public double jacobian () {
	return scale;
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
    private double scale;

    static Random rand = new Random();
}
