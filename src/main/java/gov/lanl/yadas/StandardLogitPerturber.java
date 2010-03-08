package gov.lanl.yadas;

import java.util.*;

/** 
 * Replicates update functionality in LogitMCMCParameter (Metropolis-Hastings 
 * with Gaussian proposals on the logit scale) so that such updates can be 
 * implemented inside a MultipleParameterUpdate.  
 * @see gov.lanl.yadas.LogitMCMCParameter
 */
public class StandardLogitPerturber extends StandardPerturber {

    /*
      replicates the update functionality in LogitMCMCParameter
     */
    public StandardLogitPerturber (double[] mss) {
	this (mss, 0);
    }
    public StandardLogitPerturber (double[] mss, int which) {
	super (mss, which);
    }
    public StandardLogitPerturber (int which, double[] mss) {
	this (mss, which);
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	double temp;
	scale = Math.exp(mss[whoseTurn] * rand.nextGaussian());
	temp = 1 / (1 + scale * (1 - candarray[which][whoseTurn]) / 
		    candarray[which][whoseTurn]);
	adj = temp * (1 - temp) / candarray[which][whoseTurn] / 
	    (1 - candarray[which][whoseTurn]);
	candarray[which][whoseTurn] = temp;
   }

    public double jacobian () {
	return adj;
    }

    double scale = 1.0, adj = 1.0;
}
