package gov.lanl.yadas;

import java.util.*;

/**
 * Perturber usable in reversible jump applications (i.e. it implements the 
 * JumpPerturber interface, which mainly means that it has a density method that 
 * tells how to compute the probability density of the proposed move).  Proposes 
 * a gaussian perturbation to a parameter on the logit scale.  
 */
public class LogitPerturber implements JumpPerturber, TunablePerturber {

    public LogitPerturber (int which, double[] mss) {
	this.which = which;
	this.mss = mss;
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	double temp;
	scale = Math.exp(mss[whoseTurn] * rand.nextGaussian());
	temp = 1. / (1. + scale * (1. - candarray[which][whoseTurn]) / 
		    candarray[which][whoseTurn]);
	adj = temp * (1. - temp) / candarray[which][whoseTurn] / 
	    (1. - candarray[which][whoseTurn]);
	candarray[which][whoseTurn] = temp;
    }
    public int numTurns () { 
	return mss.length;
    }
    public double jacobian () { 
	return adj;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	double temp = oldarr[which][whoseTurn];
	temp = Math.log(temp/(1-temp));
	double temp1 = newarr[which][whoseTurn];
	temp1 = Math.log(temp1/(1-temp1));
	return 1./Math.sqrt(2.*Math.PI) / mss[whoseTurn] * 
	    Math.exp(-(temp-temp1)*(temp-temp1)/2./mss[whoseTurn]/
		     mss[whoseTurn]) / newarr[which][whoseTurn] / 
	    (1. - newarr[which][whoseTurn]);
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

