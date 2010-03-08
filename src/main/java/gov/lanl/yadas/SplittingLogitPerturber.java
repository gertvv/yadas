package gov.lanl.yadas;

import java.util.*;

/**
 * Used in reversible jump algorithms when what is required is to 
 * take a vector of probabilities (for example, that are currently equal), and 
 * generate a proposal after which they are unequal.  The proposal
 * modifies each probability independently with a Gaussian move on the 
 * logit scale.  
 */
public class SplittingLogitPerturber implements JumpPerturber,TunablePerturber {

    public SplittingLogitPerturber (int which, double[] mss) {
	this.which = which;
	this.mss = mss;
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	double temp;
	for (int i = 0; i < mss.length; i++) {
	    adj = 1.0;
	    scale = Math.exp(mss[i] * rand.nextGaussian());
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
	for (int i = 0; i < mss.length; i++) {
	    double temp = oldarr[which][i];
	    temp = Math.log(temp/(1-temp));
	    double temp1 = newarr[which][i];
	    temp1 = Math.log(temp1/(1-temp1));
	    out *=  1/Math.sqrt(2*Math.PI) / mss[i] * 
		Math.exp(-(temp-temp1)*(temp-temp1)/2/mss[i]/
			 mss[i]) / newarr[which][i] / (1 - newarr[which][i]);
	}
	return out;
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

    int which;
    double[] mss;
    double scale = 1.0;
    double adj = 1.0;
    static Random rand = new Random(System.currentTimeMillis());
}

