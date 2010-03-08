package gov.lanl.yadas;

import java.util.*;

/** 
 * Replicates update functionality in MultiplicativeMCMCParameter (Metropolis-Hastings 
 * with Gaussian proposals on the log scale) so that such updates can be 
 * implemented inside a MultipleParameterUpdate.  
 * @see gov.lanl.yadas.MultiplicativeMCMCParameter
 */
public class StandardMultiplicativePerturber extends StandardPerturber {

    /*
      replicates the update functionality in MultiplicativeMCMCParameter
     */
    public StandardMultiplicativePerturber (double[] mss) {
	this (mss, 0);
    }
    public StandardMultiplicativePerturber (double[] mss, int which) {
	super (mss, which);
    }
    public StandardMultiplicativePerturber (int which, double[] mss) {
	this (mss, which);
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	scale = adj = Math.exp(mss[whoseTurn] * rand.nextGaussian());
	candarray[which][whoseTurn] *= scale;
   }

    public double jacobian () {
	return adj;
    }

    double scale = 1.0, adj = 1.0;
}
