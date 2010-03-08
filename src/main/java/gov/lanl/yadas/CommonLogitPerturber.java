package gov.lanl.yadas;

import java.util.*;

/** 
 * CommonLogitPerturber: used in the reversible jump example 
 * BinomialHypothesisTest.  Two unknown probabilities p[0] and p[1] 
 * may or may not have the same value.  If p[0] == p[1] and the 
 * proposed move is decided to preserve the equality and modify their 
 * common value, the proposed new value is a obtained by a Gaussian 
 * move on the logit scale.  
 * @see gov.lanl.yadas.BinomialHypothesisTest
 */ 
public class CommonLogitPerturber implements JumpPerturber, TunablePerturber {

    /** Constructor: 
     * @param which identifies which parameter is the p parameter.
     * @param mss determines the standard deviation of the Gaussian 
     * move on the logit scale.  
     */ 
    public CommonLogitPerturber (int which, double mss) {
	this.which = which;
	this.mss = mss;
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	double temp;
	scale = Math.exp(mss * rand.nextGaussian());
	adj = 1.0;
	for (int i = 0; i < candarray[which].length; i++) {
	    temp = 1 / (1 + scale * (1 - candarray[which][i]) / 
			candarray[which][i]);
	    adj *= temp * (1 - temp) / candarray[which][i] / 
		(1 - candarray[which][i]);
	    candarray[which][i] = temp;
	}
    }
    public int numTurns () { 
	return 1;
    }
    public double jacobian () { 
	return adj;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	double out = 1.0;
	for (int i = 0; i < oldarr[which].length; i++) {
	    double temp = oldarr[which][i];
	    temp = Math.log(temp/(1-temp));
	    double temp1 = newarr[which][i];
	    temp1 = Math.log(temp1/(1-temp1));
	    out *=  1/Math.sqrt(2*Math.PI) / mss * 
		Math.exp(-(temp-temp1)*(temp-temp1)/2/mss/mss) / 
		newarr[which][i] / (1 - newarr[which][i]);
	}
	return out;
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

    int which;
    double mss;
    double scale = 1.0;
    double adj = 1.0;
    static Random rand = new Random(System.currentTimeMillis());
}

